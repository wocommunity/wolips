package org.objectstyle.wolips.wodclipse.core.completion;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IDocument;
import org.objectstyle.wolips.wodclipse.core.util.WodModelUtils;

public abstract class AbstractCacheEntry<T> {
  private WodParserCache _cache;
  private T _model;
  private IFile _file;
  private String _contents;
  private IDocument _document;
  private boolean _documentChanged;
  private long _lastParseTime;
  private boolean _validated;

  public AbstractCacheEntry(WodParserCache cache) {
    _cache = cache;
    clear();
  }

  public WodParserCache getCache() {
    return _cache;
  }

  public void deleteProblems() {
    if (_file != null && _file.exists()) {
      WodModelUtils.deleteProblems(_file);
    }
  }

  public abstract void validate() throws Exception;

  public void _setModel(T model) {
    // System.out.println("AbstractCacheEntry._setModel: " + this + " => " + model);
    _model = model;
  }

  public boolean isValidated() {
    return _validated;
  }

  public void setValidated(boolean validated) {
    _validated = validated;
  }

  public void setModel(T model) {
    _model = model;
  }
  
  public T getModel() throws Exception {
    T model = _getModel();
    if (model == null) {
      WodParserCache cache = getCache();
      synchronized (cache) {
    	getCache().parse();
    	getCache().validate(false, true);
      }
      model = _getModel();
    }
    return model;
  }

  public T _getModel() {
    return _model;
  }

  public synchronized void setContents(String contents) {
    _setContents(contents);
    _setModel(null);
    _file = null;
    //System.out.println("AbstractCacheEntry.setContents: b " + _file);
    _document = null;
    _documentChanged = true;
  }

  public void _setContents(String contents) {
    _contents = contents;
  }

  public String getContents() {
    return _contents;
  }

  public synchronized void setDocument(IDocument document) {
    _document = document;
    _documentChanged = true;
  }

  public void _setDocument(IDocument document) {
    _document = document;
  }

  public IDocument getDocument() {
    return _document;
  }

  public IFile getFile() {
    return _file;
  }

  public void setFile(IFile file) {
    // System.out.println("AbstractCacheEntry.setFile: c " + file);
    _file = file;
  }

  protected abstract T _parse(String contents) throws Exception;

  protected String _process(String contents) {
    return contents;
  }

  protected synchronized T _parse(String contents, boolean updateCache) throws Exception {
    //System.out.println("WodParserCacheEntry._parse: " + getFile() + ", " + updateCache);
    String processedContents = _process(contents);
    if (updateCache) {
      _contents = processedContents;
    }

    T model = _parse(processedContents);

    if (updateCache) {
      //System.out.println("WodParserCacheEntry._parse: set model (String) = " + model);
      _setModel(model);
      _documentChanged = false;
      _validated = false;
      getCache()._setValidated(false);
    }

    return model;
  }

  public synchronized T parse(String contents, boolean updateCache) throws Exception {
    T model = _parse(contents, updateCache);

    if (updateCache) {
      //System.out.println("WodParserCacheEntry.parse: set model = " + model);
      _setModel(model);
      _documentChanged = false;
      _lastParseTime = System.currentTimeMillis();
      _validated = false;
      getCache()._setValidated(false);
    }

    return model;
  }

  protected T _parse(IDocument document, boolean updateCache) throws Exception {
    T model = _parse(document.get(), updateCache);
    return model;
  }

  public synchronized T parse(IDocument document, boolean updateCache) throws Exception {
    T model = _parse(document, updateCache);

    if (updateCache) {
      //System.out.println("WodParserCacheEntry.parse: set model (doc) = " + model);
      _setModel(model);
      _documentChanged = false;
      if (_file != null && _file.exists()) {
        _lastParseTime = _file.getModificationStamp();
      }
      _validated = false;
      getCache()._setValidated(false);
    }

    return model;
  }

  protected T _parse(IFile file, boolean updateCache) throws Exception {
    String contents;
    InputStream in = file.getContents();
    try {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      try {
        int len = 0;
        byte[] buf = new byte[1024 * 8];
        while ((len = in.read(buf)) != -1) {
          out.write(buf, 0, len);
        }
        contents = out.toString(file.getCharset());
      }
      finally {
        out.close();
      }
    }
    finally {
      in.close();
    }
    try {
    	T model = _parse(contents, updateCache);
      return model;
    }
    catch (Throwable t) {
    	throw new RuntimeException("Failed to parse '" + file + "'.", t);
    }
  }

  public synchronized T parse(IFile file, boolean updateCache) throws Exception {
    //System.out.println("AbstractCacheEntry.parse: " + file);
    T model = _parse(file, updateCache);

    if (updateCache) {
      //System.out.println("AbstractCacheEntry.parse: a " + _file);
      _file = file;
      //System.out.println("WodParserCacheEntry.parse: set model (file) = " + model);
      _setModel(model);
      if (file != null && file.exists()) {
        _lastParseTime = file.getModificationStamp();
      }
      _documentChanged = false;
    }

    return model;
  }

  public synchronized void clear() {
    // System.out.println("AbstractCacheEntry.clear: " + this);
//    Exception e= new Exception();
//    e.fillInStackTrace();
//    e.printStackTrace(System.out);
    _setModel(null);
    _lastParseTime = -1;
  }

  public synchronized T parse() throws Exception {
    T model = null;

    clear();

    if (_document != null) {
      model = parse(_document, true);
      //System.out.println("WodParserCache.parseHtmlAndWodIfNecessary:   ... from document");
    }
    else if (_file != null && _file.exists()) {
      //System.out.println("WodParserCache.parseHtmlAndWodIfNecessary:   ... from file");
      model = parse(_file, true);
    }
    else if (_contents != null) {
      model = parse(_contents, true);
    }
    else {
      // System.out.println("AbstractCacheEntry.parse: " + this + " => null");
      model = null;
      _setModel(null);
      _contents = null;
      _lastParseTime = -1;
      _documentChanged = false;
      _validated = false;
      getCache()._setValidated(false);
    }

    return model;
  }

  public synchronized boolean shouldParse() {
    return _documentChanged || (_file != null && ((_file.exists() && _file.getModificationStamp() != _lastParseTime) || (!_file.exists() && _lastParseTime > 0)));
  }

}
