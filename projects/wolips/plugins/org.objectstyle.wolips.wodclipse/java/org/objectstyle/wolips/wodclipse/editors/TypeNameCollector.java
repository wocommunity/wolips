/**
 * 
 */
package org.objectstyle.wolips.wodclipse.editors;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.TypeNameRequestor;

public class TypeNameCollector extends TypeNameRequestor {
  private IJavaProject myProject;
  private List myTypeNames;
  private Map myTypeNameToPath;
  private Map myTypeNameToType;
  private IType myWOElementType;

  public TypeNameCollector(IJavaProject _project) throws JavaModelException {
    this(_project, new LinkedList());
  }

  public TypeNameCollector(IJavaProject _project, List _typeNames) throws JavaModelException {
    myProject = _project;
    myTypeNames = _typeNames;
    myTypeNameToPath = new HashMap();
    myTypeNameToType = new HashMap();
    myWOElementType = myProject.findType("com.webobjects.appserver.WOElement");
  }

  public boolean isExactMatch() {
    return myTypeNames.size() == 1;
  }

  public boolean isEmpty() {
    return myTypeNames.isEmpty();
  }

  public String firstTypeName() {
    return (String) myTypeNames.get(0);
  }

  public Iterator typeNames() {
    return myTypeNames.iterator();
  }

  public String getPathForClassName(String _className) {
    return (String) myTypeNameToPath.get(_className);
  }

  public IType getTypeForClassName(String _className) {
    return (IType) myTypeNameToType.get(_className);
  }

  public void acceptType(int _modifiers, char[] _packageName, char[] _simpleTypeName, char[][] _enclosingTypeNames, String _path) {
    String className;
    String simpleClassName = new String(_simpleTypeName);
    if (_packageName == null || _packageName.length == 0) {
      className = simpleClassName;
    }
    else {
      String packageName = new String(_packageName);
      className = packageName + "." + simpleClassName;
    }
    try {
      IType type = myProject.findType(className);
      if (type != null) {
        ITypeHierarchy typeHierarchy = type.newSupertypeHierarchy(null);
        if (typeHierarchy.contains(myWOElementType)) {
          myTypeNames.add(className);
          myTypeNameToPath.put(className, _path);
          myTypeNameToType.put(className, type);
        }
      }
    }
    catch (Throwable t) {
      // ignore
      t.printStackTrace();
    }
  }
}