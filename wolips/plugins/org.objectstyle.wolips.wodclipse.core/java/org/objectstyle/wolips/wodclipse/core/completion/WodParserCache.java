package org.objectstyle.wolips.wodclipse.core.completion;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLParser;
import jp.aonir.fuzzyxml.event.FuzzyXMLErrorEvent;
import jp.aonir.fuzzyxml.event.FuzzyXMLErrorListener;

import org.apache.commons.collections.map.ReferenceMap;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.core.resources.types.api.ApiModelException;
import org.objectstyle.wolips.core.resources.types.api.Wo;
import org.objectstyle.wolips.locate.LocateException;
import org.objectstyle.wolips.locate.LocatePlugin;
import org.objectstyle.wolips.locate.result.LocalizedComponentsLocateResult;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.core.document.WodFileDocumentProvider;
import org.objectstyle.wolips.wodclipse.core.model.HtmlElementName;
import org.objectstyle.wolips.wodclipse.core.model.HtmlProblem;
import org.objectstyle.wolips.wodclipse.core.model.IWodModel;
import org.objectstyle.wolips.wodclipse.core.model.WodProblem;
import org.objectstyle.wolips.wodclipse.core.preferences.BindingValidationRule;
import org.objectstyle.wolips.wodclipse.core.preferences.PreferenceConstants;
import org.objectstyle.wolips.wodclipse.core.preferences.TagShortcut;
import org.objectstyle.wolips.wodclipse.core.util.WodApiUtils;
import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;
import org.objectstyle.wolips.wodclipse.core.util.WodModelUtils;
import org.objectstyle.wolips.wodclipse.core.util.WodReflectionUtils;
import org.objectstyle.wolips.wodclipse.core.validation.TemplateValidator;

public class WodParserCache implements FuzzyXMLErrorListener {
  private Map<String, HtmlElementName> _htmlElementCache;
  private Map _typeContextCache;
  private FuzzyXMLDocument _htmlDocument;
  private String _htmlContents;

  private IWodModel _wodModel;

  private LocalizedComponentsLocateResult _componentsLocateResults;
  private IProject _project;
  private IJavaProject _javaProject;
  private IType _componentType;
  private IContainer _woFolder;
  private IFile _htmlFile;
  private IFile _wodFile;
  private IFile _apiFile;

  private List<HtmlProblem> _htmlParserProblems;
  private long _lastJavaParseTime;
  private long _lastWodParseTime;
  private long _lastHtmlParseTime;
  private boolean _validated;

  private static WodParserCacheContext _context;
  private static Map _parsers;

  public static synchronized WodParserCache parser(IResource resource) throws CoreException, LocateException {
    return WodParserCache.parser(resource, true);
  }

  public static synchronized WodParserCache parser(IResource resource, boolean createIfMissing) throws CoreException, LocateException {
    if (_parsers == null) {
      _parsers = new ReferenceMap(ReferenceMap.HARD, ReferenceMap.SOFT);
      ResourcesPlugin.getWorkspace().addResourceChangeListener(new WodHtmlResourceChangeListener());
      _context = new WodParserCacheContext();
    }
    IContainer woFolder;
    if (resource instanceof IFolder) {
      woFolder = (IContainer) resource;
    }
    else {
      woFolder = resource.getParent();
    }
    String key = woFolder.getLocation().toPortableString();
    WodParserCache cache = (WodParserCache) _parsers.get(key);
    if (cache == null && createIfMissing) {
      cache = new WodParserCache(woFolder);
      _parsers.put(key, cache);
    }
    return cache;
  }

  protected static class WodHtmlResourceChangeListener implements IResourceChangeListener, IResourceDeltaVisitor {
    public void resourceChanged(IResourceChangeEvent event) {
      IResourceDelta delta = event.getDelta();
      if (delta != null) {
        try {
          delta.accept(this);
        }
        catch (CoreException e) {
          Activator.getDefault().log(e);
        }
      }
    }

    public boolean visit(IResourceDelta delta) {
      IResource resource = delta.getResource();
      if (resource != null) {
        String extension = resource.getFileExtension();
        //System.out.println("WodHtmlResourceChangeListener.visit: " + extension);
      }
      return true;
    }
  }

  protected WodParserCache(IContainer woFolder) throws CoreException, LocateException {
    _woFolder = woFolder;

    clearCache();
  }

  public IType getComponentType() {
    return _componentType;
  }

  public IProject getProject() {
    return _project;
  }

  public IJavaProject getJavaProject() {
    return _javaProject;
  }

  public void _clearHtmlCache() {
    System.out.println("WodParserCache._clearModelCache: Clearing " + _woFolder + " cache");
    _htmlParserProblems = new LinkedList<HtmlProblem>();
    _htmlElementCache = new HashMap<String, HtmlElementName>();
    _htmlDocument = null;
    _lastHtmlParseTime = -1;
  }

  public void _clearWodCache() {
    System.out.println("WodParserCache._clearModelCache: Clearing " + _woFolder + " cache");
    _wodModel = null;
    _lastWodParseTime = -1;
  }

  public void clearCache() throws CoreException, LocateException {
    System.out.println("WodParserCache.WodParserCache: Reloading " + _woFolder);
    _componentsLocateResults = LocatePlugin.getDefault().getLocalizedComponentsLocateResult(_woFolder);
    _project = _woFolder.getProject();
    _javaProject = JavaCore.create(_project);
    _htmlFile = _componentsLocateResults.getFirstHtmlFile();
    _wodFile = _componentsLocateResults.getFirstWodFile();
    _apiFile = _componentsLocateResults.getDotApi(true);
    _componentType = _componentsLocateResults.getDotJavaType();

    _clearHtmlCache();
    _clearWodCache();
    clearValidationCache();
    _typeContextCache = new HashMap();
  }

  public void clearValidationCache() {
    _validated = false;
  }

  public LocalizedComponentsLocateResult getComponentsLocateResults() {
    return _componentsLocateResults;
  }

  public void addHtmlElement(HtmlElementName htmlElementName) {
    _htmlElementCache.put(htmlElementName.getName(), htmlElementName);
  }

  public Map<String, HtmlElementName> getHtmlElementCache() throws CoreException, IOException {
    parseHtmlAndWodIfNecessary();
    validate();
    return _htmlElementCache;
  }

  public Map getElementNameToTypeCache() {
    return _context.getElementNameToTypeCache();
  }

  public Map getElementTypeToWoCache() {
    return _context.getElementTypeToWoCache();
  }

  public Map getTypeContextCache() {
    return _typeContextCache;
  }

  public IWodModel getWodModel() {
    return _wodModel;
  }
  
  public Wo getWo(String elementName) throws ApiModelException, JavaModelException {
    IType elementType = getElementType(elementName);
    return getWo(elementType);
  }

  public IType getElementType(String elementName) throws JavaModelException {
    return WodReflectionUtils.findElementType(_javaProject, elementName, false, this);
  }
  
  public Wo getWo(IType type) throws ApiModelException {
    return WodApiUtils.findApiModelWo(type, this);
  }

  public void parseHtmlAndWodIfNecessary() throws CoreException, IOException {
    if (_htmlFile != null && ((_htmlFile.exists() && _htmlFile.getModificationStamp() != _lastHtmlParseTime) || (!_htmlFile.exists() && _lastHtmlParseTime > 0))) {
      System.out.println("WodParserCache.parseHtmlAndWodIfNecessary: Parse " + _htmlFile);
      _clearHtmlCache();

      if (_htmlFile != null && _htmlFile.exists()) {
        InputStream in = _htmlFile.getContents();
        try {
          ByteArrayOutputStream out = new ByteArrayOutputStream();
          try {
            int len = 0;
            byte[] buf = new byte[1024 * 8];
            while ((len = in.read(buf)) != -1) {
              out.write(buf, 0, len);
            }
            _htmlContents = out.toString(_htmlFile.getCharset());
          }
          finally {
            out.close();
          }
        }
        finally {
          in.close();
        }
        _htmlContents = _htmlContents.replaceAll("\r\n", " \n");
        _htmlContents = _htmlContents.replaceAll("\r", "\n");

        FuzzyXMLParser parser = new FuzzyXMLParser();
        parser.addErrorListener(this);
        _htmlDocument = parser.parse(_htmlContents);
        _lastHtmlParseTime = _htmlFile.getModificationStamp();
      }
      else {
        _htmlDocument = null;
        _htmlContents = null;
        _lastHtmlParseTime = -1;
      }
      _validated = false;
    }

    if (_wodFile != null && ((_wodFile.exists() && _wodFile.getModificationStamp() != _lastWodParseTime) || (!_wodFile.exists() && _lastWodParseTime > 0))) {
      System.out.println("WodParserCache.parseHtmlAndWodIfNecessary: Parse " + _wodFile);
      if (_wodFile != null && _wodFile.exists()) {
        FileEditorInput input = new FileEditorInput(_wodFile);
        WodFileDocumentProvider provider = new WodFileDocumentProvider();
        provider.connect(input);
        try {
          IDocument wodDocument = provider.getDocument(input);
          _wodModel = WodModelUtils.createWodModel(_wodFile, wodDocument);
        }
        finally {
          provider.disconnect(input);
        }
        _lastWodParseTime = _wodFile.getModificationStamp();
      }
      else {
        _wodModel = null;
        _lastWodParseTime = -1;
      }
      _validated = false;
    }
  }

  public void validate() throws CoreException {
    if (!_validated) {
      IWorkspaceRunnable body = new IWorkspaceRunnable() {
        public void run(IProgressMonitor monitor) {
          try {
            System.out.println("WodParserCache.validate: Validate " + _woFolder);
            _validated = true;
            if (_wodFile != null) {
              WodModelUtils.deleteWodProblems(_wodFile);
            }
            if (_htmlFile != null) {
              WodModelUtils.deleteWodProblems(_htmlFile);
            }

            if (Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.VALIDATE_TEMPLATES_KEY)) {
              if (_htmlDocument != null) {
                boolean errorOnHtmlErrorsKey = Activator.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.ERROR_ON_HTML_ERRORS_KEY);
                if (errorOnHtmlErrorsKey) {
                  for (HtmlProblem problem : _htmlParserProblems) {
                    problem.createMarker(_htmlFile);
                  }
                }
                new TemplateValidator(WodParserCache.this).validate(_htmlDocument);
              }
            }

            if (_wodModel != null) {
              List<WodProblem> wodProblems = _wodModel.getProblems(_javaProject, _componentType, WodParserCache.this);
              for (WodProblem wodProblem : wodProblems) {
                wodProblem.createMarker(_wodFile);
              }
            }
          }
          catch (Exception e) {
            Activator.getDefault().log(e);
          }
        }
      };
      IWorkspace workspace = ResourcesPlugin.getWorkspace();
      workspace.run(body, new NullProgressMonitor());
    }
  }

  public void error(FuzzyXMLErrorEvent event) {
    int offset = event.getOffset();
    int length = event.getLength();
    String message = event.getMessage();
    HtmlProblem problem = new HtmlProblem(_htmlFile, message, new Position(offset, length), WodHtmlUtils.getLineAtOffset(_htmlContents, offset), false, null);
    _htmlParserProblems.add(problem);
  }

  public FuzzyXMLDocument getHtmlDocument() throws CoreException, IOException {
    if (_htmlDocument == null) {
      parseHtmlAndWodIfNecessary();
      validate();
    }
    return _htmlDocument;
  }

  public String getHtmlContents() {
    return _htmlContents;
  }

  public IFile getHtmlFile() {
    return _htmlFile;
  }

  public IFile getApiFile() {
    return _apiFile;
  }

  public IFile getWodFile() {
    return _wodFile;
  }

  public TagShortcut getTagShortcutNamed(String shortcut) {
    TagShortcut matchingTagShortcut = null;
    for (TagShortcut tagShortcut : _context.getTagShortcuts()) {
      if (matchingTagShortcut == null && tagShortcut.getShortcut().equalsIgnoreCase(shortcut)) {
        matchingTagShortcut = tagShortcut;
      }
    }
    return matchingTagShortcut;
  }

  public List<TagShortcut> getTagShortcuts() {
    return _context.getTagShortcuts();
  }

  public List<BindingValidationRule> getBindingValidationRules() {
    return _context.getBindingValidationRules();
  }
}
