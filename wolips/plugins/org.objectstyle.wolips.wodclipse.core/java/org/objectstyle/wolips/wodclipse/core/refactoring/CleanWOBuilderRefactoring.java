package org.objectstyle.wolips.wodclipse.core.refactoring;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.objectstyle.wolips.baseforplugins.util.StringUtilities;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.model.IWodElement;
import org.objectstyle.wolips.wodclipse.core.model.IWodModel;

public class CleanWOBuilderRefactoring implements IRunnableWithProgress {
  private WodParserCache _cache;
  
  public CleanWOBuilderRefactoring(WodParserCache cache) {
    _cache = cache;
  }
  
  public void run(IProgressMonitor monitor) throws InvocationTargetException {
    List<ElementRename> elementRenames = new LinkedList<ElementRename>();
    Set<String> elementNames = new HashSet<String>();
    IWodModel wodModel = _cache.getWodModel();
    for (IWodElement wodElement : wodModel.getElements()) {
      String typeName = wodElement.getElementType();
      String shortTypeName = typeName;
      if (typeName.startsWith("WO")) {
        shortTypeName = typeName.substring("WO".length());
      }
      
      String elementName = wodElement.getElementName();
      if (elementName.startsWith(shortTypeName) && StringUtilities.isDigitsOnly(elementName.substring(shortTypeName.length()))) {
        String newName = null;
        
        if ("WOConditional".equals(typeName)) {
          String conditionValue = (String) wodElement.getBindingsMap().get("condition");
          String negateValue = (String) wodElement.getBindingsMap().get("negate");
          String prefix = null;
          if (negateValue != null && (negateValue.equalsIgnoreCase("true") || negateValue.equalsIgnoreCase("yes"))) {
            prefix = "Not";
          }
          newName = newNameFromBindingValue(prefix, conditionValue, null);
        }
        else if ("WOString".equals(typeName)) {
          String value = (String) wodElement.getBindingsMap().get("value");
          newName = newNameFromBindingValue(null, value, null);
        }
        
        if (newName != null) {
          String uniqueNewName = newName;
          int counter = 1;
          while (wodModel.getElementNamed(newName) != null || elementNames.contains(uniqueNewName)) {
            uniqueNewName = newName + String.valueOf(counter ++);
          }
          elementRenames.add(new ElementRename(elementName, uniqueNewName));
          elementNames.add(uniqueNewName);
        }
      }
    }
    
    new RenameElementsRefactoring(elementRenames, _cache).run(monitor);
  }
  
  protected String newNameFromBindingValue(String prefix, String value, String suffix) {
    String newName = null;
    if (value != null && !value.startsWith("\"") && !value.startsWith("~")) {
      String[] keys = value.split("\\.");
      StringBuffer newNameBuffer = new StringBuffer();
      if (prefix != null) {
        newNameBuffer.append(prefix);
      }
      for (int i = 0; i < keys.length; i ++) {
        if (keys[i].length() > 0) {
          newNameBuffer.append(keys[i].substring(0, 1).toUpperCase());
          newNameBuffer.append(keys[i].substring(1));
        }
      }
      if (suffix != null) {
        newNameBuffer.append(suffix);
      }
      newName = newNameBuffer.toString();
    }
    return newName;
  }
  
  public static void run(WodParserCache cache, IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException, CoreException {
    TemplateRefactoring.processHtmlAndWod(new CleanWOBuilderRefactoring(cache), cache, progressMonitor);
  }
}
