/**
 * 
 */
package org.objectstyle.wolips.wodclipse.core.refactoring;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.core.formatter.CodeFormatter;
import org.eclipse.jdt.internal.core.CreateImportOperation;
import org.eclipse.jdt.internal.core.CreateMethodOperation;
import org.eclipse.jdt.internal.core.JavaModelOperation;
import org.eclipse.jdt.internal.corext.util.CodeFormatterUtil;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.ui.IEditorPart;
import org.objectstyle.wolips.bindings.utils.BindingReflectionUtils;

public class AddActionOperation extends JavaModelOperation {
  private AddActionInfo _info;

  public AddActionOperation(AddActionInfo info) {
    super(new IJavaElement[] { info.getComponentType() });
    _info = info;
  }

  @Override
  protected void executeOperation() throws JavaModelException {
    IType componentType = _info.getComponentType();

    String keyType = _info.getJavaTypeName();

    if (BindingReflectionUtils.isImportRequired(keyType)) {
      new CreateImportOperation(keyType, componentType.getCompilationUnit(), Flags.AccDefault).runOperation(progressMonitor);
    }

    String actionMethodName = _info.getName();
    boolean useGenerics = JavaModelUtil.is50OrHigher(componentType.getJavaProject());
    String simpleTypeName = Signature.getSimpleName(keyType);
    
    boolean loadPage = !"WOComponent".equals(simpleTypeName) && !"WOActionResults".equals(simpleTypeName);
    
    StringBuffer sourceBuffer = new StringBuffer();
    sourceBuffer.append("public " + simpleTypeName + " " + actionMethodName + "() {\n");
    if (loadPage) {
      if (useGenerics) {
          sourceBuffer.append(simpleTypeName + " nextPage = pageWithName(" + simpleTypeName + ".class);\n");
      }
      else {
    	  sourceBuffer.append(simpleTypeName + " nextPage = (" + simpleTypeName + ")pageWithName(" + simpleTypeName + ".class.gettName());\n");
      }
      sourceBuffer.append("return nextPage;\n");
    }
    else {
      sourceBuffer.append("return null;\n");
    }
    sourceBuffer.append("}");

    String source = sourceBuffer.toString();
    String lineDelim = "\n";// TextUtilities.getDefaultLineDelimiter(document);
    source = CodeFormatterUtil.format(CodeFormatter.K_CLASS_BODY_DECLARATIONS, source, 1, null, componentType.getJavaProject());
    new CreateMethodOperation(componentType, source, false).runOperation(progressMonitor);
  }

  public static void addAction(AddActionInfo info) throws CoreException {
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
        new AddActionOperation(info).run(null);
      }
      finally {
        if (target != null) {
          target.endCompoundChange();
        }
      }
    }
  }
}