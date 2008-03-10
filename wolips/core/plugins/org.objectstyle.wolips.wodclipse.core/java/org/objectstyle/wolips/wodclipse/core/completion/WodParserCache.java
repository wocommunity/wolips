package org.objectstyle.wolips.wodclipse.core.completion;

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.TextViewerUndoManager;
import org.objectstyle.wolips.bindings.Activator;
import org.objectstyle.wolips.bindings.api.ApiCache;
import org.objectstyle.wolips.bindings.api.ApiModelException;
import org.objectstyle.wolips.bindings.api.ApiUtils;
import org.objectstyle.wolips.bindings.api.Wo;
import org.objectstyle.wolips.bindings.preferences.PreferenceConstants;
import org.objectstyle.wolips.bindings.utils.BindingReflectionUtils;
import org.objectstyle.wolips.bindings.wod.BindingValidationRule;
import org.objectstyle.wolips.bindings.wod.ITypeOwner;
import org.objectstyle.wolips.bindings.wod.TagShortcut;
import org.objectstyle.wolips.bindings.wod.TypeCache;
import org.objectstyle.wolips.core.resources.types.LimitedLRUCache;
import org.objectstyle.wolips.locate.LocateException;
import org.objectstyle.wolips.locate.LocatePlugin;
import org.objectstyle.wolips.locate.result.LocalizedComponentsLocateResult;
import org.objectstyle.wolips.wodclipse.core.util.EOModelGroupCache;

public class WodParserCache implements ITypeOwner {
  private static TypeCache _typeCache;
  private static EOModelGroupCache _modelGroupCache;

  private WodCacheEntry _wodEntry;
  private HtmlCacheEntry _htmlEntry;
  private WooCacheEntry _wooEntry;

  private TextViewerUndoManager _undoManager;
  private LocalizedComponentsLocateResult _componentsLocateResults;
  private IProject _project;
  private IJavaProject _javaProject;
  private IType _componentType;
  private IContainer _woFolder;
  private IFile _apiFile;

  private long _lastJavaParseTime;
  private boolean _validated;

  private static LimitedLRUCache<String, WodParserCache> _parsers;

  public static synchronized WodParserCache parser(IResource resource) throws CoreException, LocateException {
    return WodParserCache.parser(resource, true);
  }

  public static synchronized WodParserCache parser(IResource resource, boolean createIfMissing) throws CoreException, LocateException {
    if (_parsers == null) {
      _parsers = new LimitedLRUCache<String, WodParserCache>(10);
      ResourcesPlugin.getWorkspace().addResourceChangeListener(new WodParserCacheInvalidator());
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

  protected WodParserCache(IContainer woFolder) throws CoreException, LocateException {
    _woFolder = woFolder;
    init();
  }

  public WodParserCache() throws CoreException, LocateException {
    init();
  }

  protected void init() throws CoreException, LocateException {
    _undoManager = new TextViewerUndoManager(25);
    _wodEntry = new WodCacheEntry(this);
    _htmlEntry = new HtmlCacheEntry(this);
    _wooEntry = new WooCacheEntry(this);
    clearCache();
  }
  
  public IContainer getWoFolder() {
    return _woFolder;
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
    cache._apiFile = _apiFile;
    cache._htmlEntry.setFile(_htmlEntry.getFile());
    cache._wodEntry.setFile(_wodEntry.getFile());
    cache._wooEntry.setFile(_wooEntry.getFile());
    return cache;
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
      _htmlEntry.setFile(_componentsLocateResults.getFirstHtmlFile());
      _wodEntry.setFile(_componentsLocateResults.getFirstWodFile());
      _apiFile = _componentsLocateResults.getDotApi(true);
      _componentType = _componentsLocateResults.getDotJavaType();
      _wooEntry.setFile(_componentsLocateResults.getFirstWooFile());
    }
  }

  public void clearCache() throws CoreException, LocateException {
    //System.out.println("WodParserCache.WodParserCache: Reloading " + _woFolder);
    clearLocateResultsCache();

    _htmlEntry.clear();
    _wodEntry.clear();
    _wooEntry.clear();
    clearValidationCache();
  }

  public void clearValidationCache() {
    //System.out.println("WodParserCache.clearValidationCache: " + _woFolder);
    _validated = false;
  }

  public LocalizedComponentsLocateResult getComponentsLocateResults() {
    if (_componentsLocateResults.isValid()) {

    }
    return _componentsLocateResults;
  }

  public ApiCache getApiCache() {
    return WodParserCache.getTypeCache().getApiCache(_javaProject);
  }

  public static synchronized TypeCache getTypeCache() {
    if (WodParserCache._typeCache == null) {
      WodParserCache._typeCache = new TypeCache();
    }
    return WodParserCache._typeCache;
  }

  public static synchronized EOModelGroupCache getModelGroupCache() {
    if (_modelGroupCache == null) {
      _modelGroupCache = new EOModelGroupCache();
    }
    return _modelGroupCache;
  }
  
  public IType getType() throws CoreException, LocateException {
    return getComponentType();
  }
  
  public TypeCache getCache() {
    return WodParserCache.getTypeCache();
  }

  public Wo getWo(String elementName) throws ApiModelException, JavaModelException {
    IType elementType = getElementType(elementName);
    return getWo(elementType);
  }

  public IType getElementType(String elementName) throws JavaModelException {
    return BindingReflectionUtils.findElementType(_javaProject, elementName, false, WodParserCache.getTypeCache());
  }

  public Wo getWo(IType type) throws ApiModelException {
    return ApiUtils.findApiModelWo(type, getApiCache());
  }

  public void parse() throws Exception {
    if (_htmlEntry.shouldParse()) {
      // System.out.println("WodParserCache.parse: html");
      _htmlEntry.parse();
    }

    if (_wodEntry.shouldParse()) {
      // System.out.println("WodParserCache.parse: wod");
      _wodEntry.parse();
    }

    if (_wooEntry.shouldParse()) {
      // System.out.println("WodParserCache.parse: woo");
      _wooEntry.parse();
    }
  }

  public void validate() throws CoreException {
    if (!_validated) {
      IWorkspaceRunnable body = new IWorkspaceRunnable() {
        public void run(IProgressMonitor monitor) {
          try {
            WodParserCache.this._validate();
          }
          catch (Exception e) {
            e.printStackTrace();
            Activator activator = Activator.getDefault();
            if (activator != null) {
              activator.log(e);
            }
          }
        }
      };
      IWorkspace workspace = ResourcesPlugin.getWorkspace();
      workspace.run(body, null, IWorkspace.AVOID_UPDATE, null);
    }
  }

  public HtmlCacheEntry getHtmlEntry() {
    return _htmlEntry;
  }

  public WodCacheEntry getWodEntry() {
    return _wodEntry;
  }

  public WooCacheEntry getWooEntry() {
    return _wooEntry;
  }

  public void _setValidated(boolean validated) {
    _validated = validated;
    // System.out.println("WodParserCache._setValidated: " + _validated);
  }

  public void _validate() throws Exception {
    _validated = true;

    _htmlEntry.deleteProblems();
    _wodEntry.deleteProblems();
    _wooEntry.deleteProblems();

    if (Activator.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.VALIDATE_TEMPLATES_KEY)) {
      _htmlEntry.validate();
      _wodEntry.validate();
      _wooEntry.validate();
    }
  }

  public IFile getApiFile() throws CoreException, LocateException {
    checkLocateResults();
    return _apiFile;
  }

  public TagShortcut getTagShortcutNamed(String shortcut) {
    return ApiCache.getTagShortcutNamed(shortcut);
  }

  public List<TagShortcut> getTagShortcuts() {
    return ApiCache.getTagShortcuts();
  }

  public List<BindingValidationRule> getBindingValidationRules() {
    return ApiCache.getBindingValidationRules();
  }
}
