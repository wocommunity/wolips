/**
 * 
 */
package org.objectstyle.wolips.wodclipse.core.refactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.NamingConventions;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.internal.core.CreateFieldOperation;
import org.eclipse.jdt.internal.core.CreateImportOperation;
import org.eclipse.jdt.internal.core.CreateMethodOperation;
import org.eclipse.jdt.internal.core.JavaModelOperation;
import org.eclipse.jdt.internal.corext.codemanipulation.GetterSetterUtil;
import org.eclipse.jdt.internal.corext.util.CodeFormatterUtil;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.ui.IEditorPart;
import org.objectstyle.wolips.bindings.utils.BindingReflectionUtils;

public class AddKeyOperation extends JavaModelOperation {
  public static void addKey(AddKeyInfo info) throws CoreException {
    IEditorPart editorPart = JavaUI.openInEditor(info.getComponentType().getCompilationUnit());
    if (editorPart != null) {
      IRewriteTarget target = (IRewriteTarget) editorPart.getAdapter(IRewriteTarget.class);
      if (target != null) {
        target.beginCompoundChange();
      }
      try {
        new AddKeyOperation(info).run(null);
      } finally {
        if (target != null) {
          target.endCompoundChange();
        }
      }
    }
  }

  public static void replaceField(AddKeyInfo info, String originalName) throws CoreException {
    IEditorPart editorPart = JavaUI.openInEditor(info.getComponentType().getCompilationUnit());
    if (editorPart != null) {
      IRewriteTarget target = (IRewriteTarget) editorPart.getAdapter(IRewriteTarget.class);
      if (target != null) {
        target.beginCompoundChange();
      }
      try {
        String newName = info.getName();

        info.setName(originalName);
        String originalFieldName = info.getFieldName();
        info.setName(newName);

        // This is less than ideal, but will do for now. At least this way the field order
        // should remain the same.
        IField originalField = info.getComponentType().getField(originalFieldName);
        if (info.isCreateField() && originalField.exists()) {
          AddKeyOperation op = new AddKeyOperation(info);
          op._sibling = originalField;
          op._force = true;
          op.run(null);
          originalField.delete(false, null);
        }
      } finally {
        if (target != null) {
          target.endCompoundChange();
        }
      }
    }
  }

  private boolean _force = false;

  private AddKeyInfo _info;

  private IJavaElement _sibling;

  public AddKeyOperation(AddKeyInfo info) {
    super(new IJavaElement[] { info.getComponentType() });
    _info = info;
  }

  private void createAccessor(String simpleTypeName, String fieldName) throws JavaModelException {
    boolean isBoolean = BindingReflectionUtils.isBoolean(_info.getJavaTypeName());
    IType componentType = getComponentType();

    String accessorMethodName;
    if (_info.isPrependGetToAccessorMethod()) {
      accessorMethodName = NamingConventions.suggestGetterName(componentType.getJavaProject(), fieldName, Flags.AccPublic, isBoolean, null);
    } else {
      accessorMethodName = _info.getName();
    }

    String source = null;
    if (_info.isCreateField()) {
      try {
        source = GetterSetterUtil.getGetterStub(componentType.getField(fieldName), accessorMethodName, true, Flags.AccPublic);
      } catch (CoreException e) {
        throw new JavaModelException(e);
      }
    } else {
      source = "public " + simpleTypeName + " " + accessorMethodName + "() {\n" + " // TODO\n" + "return null;\n" + "}";
    }
    if (source != null) {
      String lineDelim = "\n";// TextUtilities.getDefaultLineDelimiter(document);
      source = CodeFormatterUtil.format(CodeFormatter.K_CLASS_BODY_DECLARATIONS, source, 1, null, componentType.getJavaProject());
      new CreateMethodOperation(componentType, source, false).runOperation(progressMonitor);
    }
  }

  private void createField(String simpleTypeName, String fieldName) throws JavaModelException {
    IType componentType = getComponentType();

    String accessModifier = "private ";
    if (!(_info.isCreateAccessorMethod() || _info.isCreateMutatorMethod())) {
      accessModifier = "public ";
    }
    String keyField = accessModifier + simpleTypeName + " " + fieldName + ";";
    CreateFieldOperation op = new CreateFieldOperation(componentType, keyField, _force);
    op.createAfter(_sibling);
    op.runOperation(progressMonitor);
  }

  private void createImports(String keyType, String keyParameterType) throws JavaModelException {
    if (BindingReflectionUtils.isImportRequired(keyType)) {
      new CreateImportOperation(keyType, getCompilationUnit(), Flags.AccDefault).runOperation(progressMonitor);
    }

    if (BindingReflectionUtils.isImportRequired(keyParameterType)) {
      new CreateImportOperation(keyParameterType, getCompilationUnit(), Flags.AccDefault).runOperation(progressMonitor);
    }
  }

  private void createMutator(String simpleTypeName, String fieldName) throws JavaModelException {
    boolean isBoolean = BindingReflectionUtils.isBoolean(_info.getJavaTypeName());
    IType componentType = getComponentType();

    String mutatorMethodName = NamingConventions.suggestSetterName(getJavaProject(), fieldName, Flags.AccPublic, isBoolean, null);

    String source = null;
    if (_info.isCreateField()) {
      try {
        source = GetterSetterUtil.getSetterStub(componentType.getField(fieldName), mutatorMethodName, true, Flags.AccPublic);
      } catch (CoreException e) {
        throw new JavaModelException(e);
      }
    } else {
      source = "public void " + mutatorMethodName + "(" + simpleTypeName + " " + _info.getName() + ") {\n" + " // TODO\n" + "}";
    }
    if (source != null) {
      String lineDelim = "\n";// TextUtilities.getDefaultLineDelimiter(document);
      source = CodeFormatterUtil.format(CodeFormatter.K_CLASS_BODY_DECLARATIONS, source, 1, null, getJavaProject());
      new CreateMethodOperation(componentType, source, false).runOperation(progressMonitor);
    }
  }

  @Override
  protected void executeOperation() throws JavaModelException {
    String keyType = _info.getJavaTypeName();
    String keyParameterType = _info.getJavaParameterTypeName();

    String simpleTypeName = simpleTypeName(keyType, keyParameterType);
    String fieldName = _info.getFieldName();

    createImports(keyType, keyParameterType);

    if (_info.isCreateField()) {
      createField(simpleTypeName, fieldName);
    }

    if (_info.isCreateAccessorMethod()) {
      createAccessor(simpleTypeName, fieldName);
    }

    if (_info.isCreateMutatorMethod()) {
      createMutator(simpleTypeName, fieldName);
    }
  }

  private ICompilationUnit getCompilationUnit() {
    return _info.getComponentType().getCompilationUnit();
  }

  private IType getComponentType() {
    return _info.getComponentType();
  }

  private IJavaProject getJavaProject() {
    return _info.getComponentType().getJavaProject();
  }

  private String simpleTypeName(String keyType, String keyParameterType) {
    boolean useGenerics = JavaModelUtil.is50OrHigher(getJavaProject());
    String simpleTypeName;
    if (keyParameterType != null && useGenerics) {
      simpleTypeName = Signature.getSimpleName(keyType) + "<" + Signature.getSimpleName(keyParameterType) + ">";
    } else {
      simpleTypeName = Signature.getSimpleName(keyType);
    }
    return simpleTypeName;
  }
}