package org.objectstyle.wolips.eogenerator.ui.editors;

import java.text.ParseException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class EOGeneratorModel {
  private String myEOGeneratorPath;
  private List myModels;
  private List myRefModels;
  private String myDestination;
  private String mySubclassDestination;
  private String myTemplateDir;
  private String myJavaTemplate;
  private String mySubclassJavaTemplate;
  private List myDefines;
  private Boolean myPackageDirs;
  private Boolean myJava;
  private Boolean myVerbose;
  private List myCustomSettings;
  private boolean myDirty;

  public EOGeneratorModel(String _lineInfo) throws ParseException {
    this();
    readFromString(_lineInfo);
  }

  public EOGeneratorModel() {
    myModels = new LinkedList();
    myRefModels = new LinkedList();
    myDefines = new LinkedList();
    myCustomSettings = new LinkedList();
  }

  public String writeToString(String _defaultEOGeneratorPath, String _defaultTemplateDir, String _defaultJavaTemplate, String _defaultSubclassJavaTemplate) {
    StringBuffer sb = new StringBuffer();

    sb.append(escape(getEOGeneratorPath(_defaultEOGeneratorPath), false));

    append(sb, "-destination", myDestination);
    append(sb, "-java", myJava);
    append(sb, "-javaTemplate", getJavaTemplate(_defaultJavaTemplate));

    Iterator modelsIter = myModels.iterator();
    while (modelsIter.hasNext()) {
      EOModelReference model = (EOModelReference) modelsIter.next();
      append(sb, "-model", model.getPath());
    }

    append(sb, "-packagedirs", myPackageDirs);

    Iterator refModelsIter = myRefModels.iterator();
    while (refModelsIter.hasNext()) {
      EOModelReference refModel = (EOModelReference) refModelsIter.next();
      append(sb, "-refmodel", refModel.getPath());
    }

    append(sb, "-subclassDestination", mySubclassDestination);
    append(sb, "-subclassJavaTemplate", getSubclassJavaTemplate(_defaultSubclassJavaTemplate));
    append(sb, "-templatedir", getTemplateDir(_defaultTemplateDir));
    append(sb, "-verbose", myVerbose);

    Iterator definesIter = myDefines.iterator();
    while (definesIter.hasNext()) {
      Define define = (Define) definesIter.next();
      String name = define.getName();
      String value = define.getValue();
      append(sb, "-define-" + name, value);
    }

    return sb.toString();
  }

  public void readFromString(String _str) throws ParseException {
    myModels.clear();
    myRefModels.clear();
    myDefines.clear();
    myCustomSettings.clear();
    CommandLineTokenizer tokenizer = new CommandLineTokenizer(_str);
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      if (myEOGeneratorPath == null) {
        myEOGeneratorPath = token;
      }
      else if (token.startsWith("-")) {
        if ("-destination".equalsIgnoreCase(token)) {
          myDestination = nextTokenValue(token, tokenizer);
        }
        else if ("-java".equalsIgnoreCase(token)) {
          myJava = Boolean.TRUE;
        }
        else if ("-javaTemplate".equalsIgnoreCase(token)) {
          myJavaTemplate = nextTokenValue(token, tokenizer);
        }
        else if ("-model".equalsIgnoreCase(token)) {
          String modelPath = nextTokenValue(token, tokenizer);
          myModels.add(new EOModelReference(modelPath));
        }
        else if ("-packagedirs".equalsIgnoreCase(token)) {
          myPackageDirs = Boolean.TRUE;
        }
        else if ("-refmodel".equalsIgnoreCase(token)) {
          String refModelPath = nextTokenValue(token, tokenizer);
          myRefModels.add(new EOModelReference(refModelPath));
        }
        else if ("-subclassDestination".equalsIgnoreCase(token)) {
          mySubclassDestination = nextTokenValue(token, tokenizer);
        }
        else if ("-subclassJavaTemplate".equalsIgnoreCase(token)) {
          mySubclassJavaTemplate = nextTokenValue(token, tokenizer);
        }
        else if ("-templatedir".equalsIgnoreCase(token)) {
          myTemplateDir = nextTokenValue(token, tokenizer);
        }
        else if ("-verbose".equalsIgnoreCase(token)) {
          myVerbose = Boolean.TRUE;
        }
        else if (token.startsWith("-define-")) {
          String name = token.substring("-define-".length());
          String value = nextTokenValue(token, tokenizer);
          Define define = new Define(name, value);
          myDefines.add(define);
        }
        else {
          myCustomSettings.add(token);
        }
      }
      else {
        myCustomSettings.add(token);
      }
    }
    myDirty = false;
  }

  protected void append(StringBuffer _buffer, String _name, Boolean _value) {
    if (_value != null && _value.booleanValue()) {
      _buffer.append(" ");
      _buffer.append(_name);
    }
  }

  protected void append(StringBuffer _buffer, String _name, String _value) {
    if (_value != null && _value.trim().length() > 0) {
      _buffer.append(" ");
      _buffer.append(_name);
      _buffer.append(" ");
      _buffer.append(escape(_value, true));
    }
  }

  protected String escape(String _value, boolean _quotes) {
    String value;
    if (_value == null) {
      value = null;
    }
    else if (_value.indexOf(' ') == -1 && _value.trim().length() > 0) {
      value = _value;
    }
    else if (_quotes) {
      StringBuffer valueBuffer = new StringBuffer();
      valueBuffer.append("\"");
      valueBuffer.append(_value);
      valueBuffer.append("\"");
      value = valueBuffer.toString();
    }
    else {
      value = _value.replaceAll(" ", "\\ ");
    }
    return value;
  }

  protected String nextTokenValue(String _token, CommandLineTokenizer _tokenizer) throws ParseException {
    if (!_tokenizer.hasMoreTokens()) {
      throw new ParseException(_token + " must be followed by a value.", -1);
    }
    String token = _tokenizer.nextToken();
    return token;
  }

  public List getDefines() {
    return myDefines;
  }

  public void setDefines(List _defines) {
    myDefines = _defines;
    myDirty = true;
  }

  public String getDestination() {
    return myDestination;
  }

  public void setDestination(String _destination) {
    if (isNew(myDestination, _destination)) {
      myDestination = _destination;
      myDirty = true;
    }
  }

  public String getEOGeneratorPath(String _default) {
    String eoGeneratorPath = myEOGeneratorPath;
    if (myEOGeneratorPath == null || myEOGeneratorPath.trim().length() == 0) {
      eoGeneratorPath = _default;
    }
    return eoGeneratorPath;
  }

  public void setEOGeneratorPath(String _generatorPath) {
    if (isNew(myEOGeneratorPath, _generatorPath)) {
      myEOGeneratorPath = _generatorPath;
      myDirty = true;
    }
  }

  public Boolean isJava() {
    return myJava;
  }

  public void setJava(Boolean _java) {
    myJava = _java;
    myDirty = true;
  }

  public String getJavaTemplate(String _default) {
    String javaTemplate = myJavaTemplate;
    if (myJavaTemplate == null || myJavaTemplate.trim().length() == 0) {
      javaTemplate = _default;
    }
    return javaTemplate;
  }

  public void setJavaTemplate(String _javaTemplate) {
    if (isNew(myJavaTemplate, _javaTemplate)) {
      myJavaTemplate = _javaTemplate;
      myDirty = true;
    }
  }

  protected boolean isNew(String _oldValue, String _newValue) {
    boolean isNew;
    if (_oldValue == null && _newValue != null && _newValue.trim().length() > 0) {
      isNew = true;
    }
    else if (_oldValue != null && !_oldValue.equals(_newValue)) {
      isNew = true;
    }
    else {
      isNew = false;
    }
    return isNew;
  }

  public List getModels() {
    return myModels;
  }

  public void setModels(List _models) {
    myModels = _models;
    myDirty = true;
  }

  public Boolean isPackageDirs() {
    return myPackageDirs;
  }

  public void setPackageDirs(Boolean _packageDirs) {
    myPackageDirs = _packageDirs;
    myDirty = true;
  }

  public List getRefModels() {
    return myRefModels;
  }

  public void setRefModels(List _refModels) {
    myRefModels = _refModels;
    myDirty = true;
  }

  public String getSubclassDestination() {
    return mySubclassDestination;
  }

  public void setSubclassDestination(String _subclassDestination) {
    if (isNew(mySubclassDestination, _subclassDestination)) {
      mySubclassDestination = _subclassDestination;
      myDirty = true;
    }
  }

  public String getSubclassJavaTemplate(String _default) {
    String subclassJavaTemplate = mySubclassJavaTemplate;
    if (mySubclassJavaTemplate == null || mySubclassJavaTemplate.trim().length() == 0) {
      subclassJavaTemplate = _default;
    }
    return subclassJavaTemplate;
  }

  public void setSubclassJavaTemplate(String _subclassJavaTemplate) {
    if (isNew(mySubclassJavaTemplate, _subclassJavaTemplate)) {
      mySubclassJavaTemplate = _subclassJavaTemplate;
      myDirty = true;
    }
  }

  public String getTemplateDir(String _default) {
    String templateDir = myTemplateDir;
    if (myTemplateDir == null || myTemplateDir.trim().length() == 0) {
      templateDir = _default;
    }
    return templateDir;
  }

  public void setTemplateDir(String _templateDir) {
    if (isNew(myTemplateDir, _templateDir)) {
      myTemplateDir = _templateDir;
      myDirty = true;
    }
  }

  public Boolean isVerbose() {
    return myVerbose;
  }

  public void setVerbose(Boolean _verbose) {
    myVerbose = _verbose;
    myDirty = true;
  }

  public void setDirty(boolean _dirty) {
    myDirty = _dirty;
  }

  public boolean isDirty() {
    return myDirty;
  }

  public static class Define {
    private String myName;
    private String myValue;

    public Define(String _name, String _value) {
      myName = _name;
      myValue = _value;
    }

    public int hashCode() {
      return myName.hashCode();
    }

    public boolean equals(Object _obj) {
      return (_obj instanceof Define && ((Define) _obj).myName.equals(myName));
    }

    public String getName() {
      return myName;
    }

    public String getValue() {
      return myValue;
    }
  }

  public static EOGeneratorModel createDefaultModel() {
    EOGeneratorModel model = new EOGeneratorModel();
    model.setJava(Boolean.TRUE);
    return model;
  }
}
