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
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IUndoManager;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextViewerUndoManager;
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
import org.objectstyle.wolips.wodclipse.core.util.LimitedLRUCache;
import org.objectstyle.wolips.wodclipse.core.util.WodApiUtils;
import org.objectstyle.wolips.wodclipse.core.util.WodHtmlUtils;
import org.objectstyle.wolips.wodclipse.core.util.WodModelUtils;
import org.objectstyle.wolips.wodclipse.core.util.WodReflectionUtils;
import org.objectstyle.wolips.wodclipse.core.validation.TemplateValidator;

public class WodParserCache implements FuzzyXMLErrorListener {
  private Map<String, HtmlElementName> _htmlElementCache;
  private Map<IType, Map<String, IType>> _typeContextCache;
  private FuzzyXMLDocument _htmlXmlDocument;
  private String _htmlContents;
  private IDocument _htmlDocument;
  private boolean _htmlDocumentChanged;

  private IWodModel _wodModel;
  private IDocument _wodDocument;
  private boolean _wodDocumentChanged;
  private IUndoManager _undoManager;

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
  private static LimitedLRUCache<String, WodParserCache> _parsers;

  public static synchronized WodParserCache parser(IResource resource) throws CoreException, LocateException {
    return WodParserCache.parser(resource, true);
  }

  public static synchronized WodParserCache parser(IResource resource, boolean createIfMissing) throws CoreException, LocateException {
    if (_parsers == null) {
      _parsers = new LimitedLRUCache<String, WodParserCache>(10);
      //ResourcesPlugin.getWorkspace().addResourceChangeListener(new WodHtmlResourceChangeListener());
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
    WodParserCache cache = _parsers.get(key);
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
    _undoManager = new TextViewerUndoManager(25);
    clearCache();
  }

  public WodParserCache() throws CoreException, LocateException {
    _undoManager = new TextViewerUndoManager(25);
    clearCache();
  }

  public IUndoManager getUndoManager() {
    return _undoManager;
  }

  public IType getComponentType() throws CoreException, LocateException {
    checkLocateResults();
    return _componentType;
  }

  public IProject getProject() {
    return _project;
  }

  public IJavaProject getJavaProject() {
    return _javaProject;
  }

  // MS: This is not a complete clone at the moment ... I just needed a 
  // partial clone for preview.
  public WodParserCache cloneCache() throws CoreException, LocateException {
    WodParserCache cache = new WodParserCache();
    cache._componentsLocateResults = _componentsLocateResults;
    cache._javaProject = _javaProject;
    cache._project = _project;
    cache._componentType = _componentType;
    cache._woFolder = _woFolder;
    cache._htmlFile = _htmlFile;
    cache._wodFile = _wodFile;
    cache._apiFile = _apiFile;
    return cache;
  }

  public void _clearHtmlCache() {
    //System.out.println("WodParserCache._clearModelCache: Clearing " + _woFolder + " cache");
    _htmlParserProblems = new LinkedList<HtmlProblem>();
    _htmlElementCache = new HashMap<String, HtmlElementName>();
    _htmlXmlDocument = null;
    _lastHtmlParseTime = -1;
  }

  public void _clearWodCache() {
    //System.out.println("WodParserCache._clearModelCache: Clearing " + _woFolder + " cache");
    _wodModel = null;
    _lastWodParseTime = -1;
  }

  protected void checkLocateResults() throws CoreException, LocateException {
    if (_componentsLocateResults != null) {
      if (!_componentsLocateResults.isValid()) {
        clearLocateResultsCache();
      }
    }
  }

  public void clearLocateResultsCache() throws CoreException, LocateException {
    if (_woFolder != null) {
      _componentsLocateResults = LocatePlugin.getDefault().getLocalizedComponentsLocateResult(_woFolder);
      _project = _woFolder.getProject();
      _javaProject = JavaCore.create(_project);
      _htmlFile = _componentsLocateResults.getFirstHtmlFile();
      _wodFile = _componentsLocateResults.getFirstWodFile();
      _apiFile = _componentsLocateResults.getDotApi(true);
      _componentType = _componentsLocateResults.getDotJavaType();
    }
  }

  public void clearCache() throws CoreException, LocateException {
    //System.out.println("WodParserCache.WodParserCache: Reloading " + _woFolder);
    clearLocateResultsCache();

    _clearHtmlCache();
    _clearWodCache();
    clearValidationCache();
    _typeContextCache = new HashMap<IType, Map<String, IType>>();
  }

  public void clearValidationCache() {
    _validated = false;
  }

  public LocalizedComponentsLocateResult getComponentsLocateResults() {
    if (_componentsLocateResults.isValid()) {

    }
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

  public WodParserCacheContext getContext() {
    return _context;
  }

  public Map<IType, Map<String, IType>> getTypeContextCache() {
    return _typeContextCache;
  }

  public IWodModel getWodModel() throws CoreException, IOException {
    if (_wodModel == null) {
      parseHtmlAndWodIfNecessary();
      validate();
    }
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

  public void setWodDocument(IDocument wodDocument) {
    _wodDocument = wodDocument;
    _wodDocumentChanged = true;
  }

  public IDocument getWodDocument() {
    return _wodDocument;
  }

  public void setHtmlDocument(IDocument htmlDocument) {
    _htmlDocumentChanged = true;
    _htmlDocument = htmlDocument;
    //System.out.println("WodParserCache.setHtmlDocument: " + htmlDocument);
  }

  public IDocument getHtmlDocument() {
    return _htmlDocument;
  }

  public void parseHtmlAndWodIfNecessary() throws CoreException, IOException {
    boolean parseHtml = _htmlDocumentChanged || (_htmlFile != null && ((_htmlFile.exists() && _htmlFile.getModificationStamp() != _lastHtmlParseTime) || (!_htmlFile.exists() && _lastHtmlParseTime > 0)));
    if (parseHtml) {
      //System.out.println("WodParserCache.parseHtmlAndWodIfNecessary: Parse HTML " + parseHtml);
      _clearHtmlCache();

      if (_htmlDocument != null) {
        //System.out.println("WodParserCache.parseHtmlAndWodIfNecessary:   ... from document");
        _htmlContents = _htmlDocument.get();
        _htmlContents = _htmlContents.replaceAll("\r\n", " \n");
        _htmlContents = _htmlContents.replaceAll("\r", "\n");

        FuzzyXMLParser parser = new FuzzyXMLParser(Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.WO54_KEY));
        parser.addErrorListener(this);
        _htmlXmlDocument = parser.parse(_htmlContents);
        if (_htmlFile != null) {
          _lastHtmlParseTime = _htmlFile.getModificationStamp();
        }
        _htmlDocumentChanged = false;
      }
      else if (_htmlFile != null && _htmlFile.exists()) {
        //System.out.println("WodParserCache.parseHtmlAndWodIfNecessary:   ... from file");
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

        FuzzyXMLParser parser = new FuzzyXMLParser(Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.WO54_KEY));
        parser.addErrorListener(this);
        _htmlXmlDocument = parser.parse(_htmlContents);
        _lastHtmlParseTime = _htmlFile.getModificationStamp();
        _htmlDocumentChanged = false;
      }
      else if (_htmlContents != null) {
        _htmlContents = _htmlContents.replaceAll("\r\n", " \n");
        _htmlContents = _htmlContents.replaceAll("\r", "\n");

        FuzzyXMLParser parser = new FuzzyXMLParser(Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.WO54_KEY));
        parser.addErrorListener(this);
        _htmlXmlDocument = parser.parse(_htmlContents);
        _lastHtmlParseTime = System.currentTimeMillis();
        _htmlDocumentChanged = false;
      }
      else {
        _htmlXmlDocument = null;
        _htmlContents = null;
        _lastHtmlParseTime = -1;
        _htmlDocumentChanged = false;
      }
      _validated = false;
    }

    boolean parseWod = _wodDocumentChanged || (_wodFile != null && ((_wodFile.exists() && _wodFile.getModificationStamp() != _lastWodParseTime) || (!_wodFile.exists() && _lastWodParseTime > 0)));
    if (parseWod) {
      //System.out.println("WodParserCache.parseHtmlAndWodIfNecessary: Parsing WOD " + _wodFile);
      if (_wodDocument != null) {
        //System.out.println("WodParserCache.parseHtmlAndWodIfNecessary:   ... From document");
        _wodModel = WodModelUtils.createWodModel(_wodFile, _wodDocument);
        if (_wodFile != null) {
          _lastWodParseTime = _wodFile.getModificationStamp();
        }
        _wodDocumentChanged = false;
      }
      else if (_wodFile != null && _wodFile.exists()) {
        //System.out.println("WodParserCache.parseHtmlAndWodIfNecessary:   ... From file");
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
        _wodDocumentChanged = false;
      }
      else {
        _wodModel = null;
        _lastWodParseTime = -1;
        _wodDocumentChanged = false;
      }
      _validated = false;
    }
  }

  public void validate() throws CoreException {
    if (!_validated) {
      IWorkspaceRunnable body = new IWorkspaceRunnable() {
        public void run(IProgressMonitor monitor) {
          try {
            //System.out.println("WodParserCache.validate: Validate " + _woFolder);
            _validated = true;
            if (_wodFile != null && _wodFile.exists()) {
              WodModelUtils.deleteWodProblems(_wodFile);
            }
            if (_htmlFile != null && _htmlFile.exists()) {
              WodModelUtils.deleteWodProblems(_htmlFile);
            }

            if (Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.VALIDATE_TEMPLATES_KEY)) {
              if (_htmlXmlDocument != null && (_htmlFile == null || _htmlFile.exists())) {
                boolean errorOnHtmlErrorsKey = Activator.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.ERROR_ON_HTML_ERRORS_KEY);
                if (errorOnHtmlErrorsKey) {
                  if (_htmlFile != null && _htmlFile.exists()) {
                    for (HtmlProblem problem : _htmlParserProblems) {
                      problem.createMarker(_htmlFile);
                    }
                  }
                }
                new TemplateValidator(WodParserCache.this).validate(_htmlXmlDocument);
              }
            }

            if (_wodModel != null) {
              List<WodProblem> wodProblems = _wodModel.getProblems(_javaProject, _componentType, WodParserCache.this);
              if (_wodFile.exists()) {
                for (WodProblem wodProblem : wodProblems) {
                  wodProblem.createMarker(_wodFile);
                }
              }
            }
          }
          catch (Exception e) {
            e.printStackTrace();
            Activator.getDefault().log(e);
          }
        }
      };
      IWorkspace workspace = ResourcesPlugin.getWorkspace();
      workspace.run(body, null, IWorkspace.AVOID_UPDATE, null);
    }
  }

  public void error(FuzzyXMLErrorEvent event) {
    int offset = event.getOffset();
    int length = event.getLength();
    String message = event.getMessage();
    HtmlProblem problem = new HtmlProblem(_htmlFile, message, new Position(offset, length), WodHtmlUtils.getLineAtOffset(_htmlContents, offset), false, null);
    _htmlParserProblems.add(problem);
  }

  public FuzzyXMLDocument getHtmlXmlDocument() throws CoreException, IOException {
    if (_htmlXmlDocument == null) {
      parseHtmlAndWodIfNecessary();
      validate();
    }
    return _htmlXmlDocument;
  }

  public void setHtmlContents(String htmlContents) {
    _htmlContents = htmlContents;
    _htmlXmlDocument = null;
    _htmlFile = null;
    _htmlDocument = null;
    _htmlDocumentChanged = true;
  }

  public String getHtmlContents() {
    return _htmlContents;
  }

  public IFile getHtmlFile() {
    return _htmlFile;
  }

  public IFile getApiFile() throws CoreException, LocateException {
    checkLocateResults();
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
