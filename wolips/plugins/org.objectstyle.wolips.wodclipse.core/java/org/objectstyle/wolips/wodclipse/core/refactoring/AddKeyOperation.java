/**
 * 
 */
package org.objectstyle.wolips.wodclipse.core.refactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaElement;
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
import org.eclipse.jdt.internal.corext.codemanipulation.StubUtility;
import org.eclipse.jdt.internal.corext.util.CodeFormatterUtil;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.ui.IEditorPart;
import org.objectstyle.wolips.bindings.utils.BindingReflectionUtils;

public class AddKeyOperation extends JavaModelOperation {
  private AddKeyInfo _info;

  public AddKeyOperation(AddKeyInfo info) {
    super(new IJavaElement[] { info.getComponentType() });
    _info = info;
  }

  @Override
  protected void executeOperation() throws JavaModelException {
    IType componentType = _info.getComponentType();

    String keyType = _info.getJavaTypeName();
    String keyParameterType = _info.getJavaParameterTypeName();

    boolean useGenerics = JavaModelUtil.is50OrHigher(componentType.getJavaProject());
    String simpleTypeName;
    if (keyParameterType != null && useGenerics) {
      simpleTypeName = Signature.getSimpleName(keyType) + "<" + Signature.getSimpleName(keyParameterType) + ">";
    }
    else {
      simpleTypeName = Signature.getSimpleName(keyType);
    }

    if (BindingReflectionUtils.isImportRequired(keyType)) {
      new CreateImportOperation(keyType, componentType.getCompilationUnit(), Flags.AccDefault).runOperation(progressMonitor);
    }

    if (BindingReflectionUtils.isImportRequired(keyParameterType)) {
      new CreateImportOperation(keyParameterType, componentType.getCompilationUnit(), Flags.AccDefault).runOperation(progressMonitor);
    }

    String[] suggestedFieldNames = StubUtility.getVariableNameSuggestions(StubUtility.INSTANCE_FIELD, componentType.getJavaProject(), _info.getName(), 0, null, true);
    String fieldName = suggestedFieldNames[0];

    if (_info.isCreateField()) {
      String accessModifier = "private ";
      if (!(_info.isCreateAccessorMethod() || _info.isCreateMutatorMethod())) {
        accessModifier = "public ";
      }
      String keyField = accessModifier + simpleTypeName + " " + fieldName + ";";
      new CreateFieldOperation(componentType, keyField, false).runOperation(progressMonitor);
    }

    boolean isBoolean = BindingReflectionUtils.isBoolean(keyType);
    if (_info.isCreateAccessorMethod()) {
      String accessorMethodName;
      if (_info.isPrependGetToAccessorMethod()) {
        accessorMethodName = NamingConventions.suggestGetterName(componentType.getJavaProject(), fieldName, Flags.AccPublic, isBoolean, null);
      }
      else {
        accessorMethodName = _info.getName();
      }

      String source = null;
      if (_info.isCreateField()) {
        try {
          source = GetterSetterUtil.getGetterStub(componentType.getField(fieldName), accessorMethodName, true, Flags.AccPublic);
        }
        catch (CoreException e) {
          throw new JavaModelException(e);
        }
      }
      else {
        source = "public " + simpleTypeName + " " + accessorMethodName + "() {\n" + " // TODO\n" + "return null;\n" + "}";
      }
      if (source != null) {
        String lineDelim = "\n";// TextUtilities.getDefaultLineDelimiter(document);
        source = CodeFormatterUtil.format(CodeFormatter.K_CLASS_BODY_DECLARATIONS, source, 1, null, lineDelim, componentType.getJavaProject());
        new CreateMethodOperation(componentType, source, false).runOperation(progressMonitor);
      }
    }

    if (_info.isCreateMutatorMethod()) {
      String mutatorMethodName = NamingConventions.suggestSetterName(componentType.getJavaProject(), fieldName, Flags.AccPublic, isBoolean, null);

      String source = null;
      if (_info.isCreateField()) {
        try {
          source = GetterSetterUtil.getSetterStub(componentType.getField(fieldName), mutatorMethodName, true, Flags.AccPublic);
        }
        catch (CoreException e) {
          throw new JavaModelException(e);
        }
      }
      else {
        source = "public void " + mutatorMethodName + "(" + simpleTypeName + " " + _info.getName() + ") {\n" + " // TODO\n" + "}";
      }
      if (source != null) {
        String lineDelim = "\n";// TextUtilities.getDefaultLineDelimiter(document);
        source = CodeFormatterUtil.format(CodeFormatter.K_CLASS_BODY_DECLARATIONS, source, 1, null, lineDelim, componentType.getJavaProject());
        new CreateMethodOperation(componentType, source, false).runOperation(progressMonitor);
      }
    }
  }

  public static void addKey(AddKeyInfo info) throws CoreException {
    IEditorPart editorPart = JavaUI.openInEditor(info.getComponentType().getCompilationUnit());
    if (editorPart != null) {
      // CompilationUnitEditor cuEditor =
      // (CompilationUnitEditor) editorPart;
      // cuEditor.getDocumentProvider().getDocument(componentType.getCompilationUnit()).get
      IRewriteTarget target = (IRewriteTarget) editorPart.getAdapter(IRewriteTarget.class);
      if (target != null) {
        target.beginCompoundChange();
      }
      try {
        new AddKeyOperation(info).run(null);
      }
      finally {
        if (target != null) {
          target.endCompoundChange();
        }
      }
    }
  }
}