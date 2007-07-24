package org.objectstyle.wolips.wodclipse.core.util;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.objectstyle.wolips.core.resources.types.api.ApiModel;
import org.objectstyle.wolips.core.resources.types.api.ApiModelException;
import org.objectstyle.wolips.core.resources.types.api.Binding;
import org.objectstyle.wolips.core.resources.types.api.Wo;
import org.objectstyle.wolips.wodclipse.core.Activator;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.model.BindingValueKey;
import org.objectstyle.wolips.workbenchutilities.WorkbenchUtilitiesPlugin;
import org.osgi.framework.Bundle;

public class WodApiUtils {

  public static Wo findApiModelWo(IType elementType, WodParserCache cache) throws ApiModelException {
    Wo wo;
    if (elementType == null) {
      wo = null;
    }
    else {
      wo = cache.getContext().getApiForType(elementType);
      
      if (wo != null) {
        ApiModel model = wo.getModel();
        if (model.parseIfNecessary()) {
          cache.getContext().clearApiForElementType(elementType);
          wo = null;
        }
      }
  
      if (wo == null) {
        Boolean apiMissing = cache.getContext().apiMissingForElementType(elementType);
        if (apiMissing == null || !apiMissing.booleanValue()) {
          ApiModel apiModel = null;
          IOpenable typeContainer = elementType.getOpenable();
          if (typeContainer instanceof IClassFile) {
            IClassFile classFile = (IClassFile) typeContainer;
            IJavaElement parent = classFile.getParent();
            if (parent instanceof IPackageFragment) {
              IPackageFragment parentPackage = (IPackageFragment) parent;
              IPath packagePath = parentPackage.getPath();
              IPath apiPath = packagePath.removeLastSegments(2).append(elementType.getElementName()).addFileExtension("api");
              File apiFile = apiPath.toFile();
              boolean fileExists = apiFile.exists();
              if (!fileExists && parentPackage.getElementName().startsWith("com.webobjects")) {
                Bundle bundle = Activator.getDefault().getBundle();
                URL woDefinitionsURL = bundle.getEntry("/WebObjectDefinitions.xml");
                // apiFile = new
                // File("/Users/mschrag/Documents/workspace/org.objectstyle.wolips.wodclipse/java/org/objectstyle/wolips/wodclipse/api/WebObjectDefinitions.xml");
                apiModel = new ApiModel(woDefinitionsURL);
              }
              else if (fileExists) {
                apiModel = new ApiModel(apiFile);
              }
            }
          }
          else if (typeContainer instanceof ICompilationUnit) {
            //ICompilationUnit cu = (ICompilationUnit) typeContainer;
            //IResource resource = cu.getCorrespondingResource();
            //String name = resource.getName();
            List<IResource> apiResources = WorkbenchUtilitiesPlugin.findResourcesInProjectByNameAndExtensions(elementType.getJavaProject().getProject(), elementType.getElementName(), new String[] { "api" }, false);
            if (apiResources != null && apiResources.size() > 0) {
              IResource apiResource = apiResources.get(0);
              apiModel = new ApiModel(apiResource.getLocation().toFile());
            }
          }
  
          if (apiModel != null) {
            Wo[] wos = apiModel.getWODefinitions().getWos();
            if (wos.length == 0) {
              // leave it alone
            }
            else if (wos.length == 1) {
              wo = wos[0];
            }
            else {
              for (int i = 0; wo == null && i < wos.length; i++) {
                if (elementType.getElementName().equals(wos[i].getClassName())) {
                  wo = wos[i];
                }
              }
            }
          }
  
          if (wo == null) {
            cache.getContext().setApiMissingForElementType(true, elementType);
          }
          else {
            cache.getContext().setApiForType(wo, elementType);
          }
        }
      }
    }
    
    return wo;
  }

  public static String[] getValidValues(IJavaProject javaProject, IType wodJavaFileType, IType elementType, String bindingName, WodParserCache wodContext) throws JavaModelException, ApiModelException {
    String[] validValues = null;
    Wo wo = WodApiUtils.findApiModelWo(elementType, wodContext);
    if (wo != null) {
      Binding matchingBinding = null;
      Binding[] bindings = wo.getBindings();
      for (int i = 0; matchingBinding == null && i < bindings.length; i++) {
        String apiBindingName = bindings[i].getName();
        if (apiBindingName.equals(bindingName)) {
          matchingBinding = bindings[i];
        }
      }
      if (matchingBinding != null) {
        int selectedDefaults = matchingBinding.getSelectedDefaults();
        String defaultsName = Binding.ALL_DEFAULTS[selectedDefaults];
        if ("Boolean".equals(defaultsName)) {
          validValues = new String[] { "true", "false" };
        }
        else if ("YES/NO".equals(defaultsName)) {
          validValues = new String[] { "true", "false" };
        }
        else if ("Date Format Strings".equals(defaultsName)) {
          validValues = new String[] { "\"%m/%d/%y\"", "\"%B %d, %Y\"", "\"%b %d, %Y\"", "\"%A, %B %d, %Y\"", "\"%A, %b %d, %Y\"", "\"%d.%m.%y\"", "\"%d %B %y\"", "\"%d %b %y\"", "\"%A %d %B %Y\"", "\"%A %d %b %Y\"", "\"%x\"", "\"%H:%M:%S\"", "\"%I:%M:%S %p\"", "\"%H:%M\"", "\"%I:%M %p\"", "\"%X\"" };
        }
        else if ("Number Format Strings".equals(defaultsName)) {
          validValues = new String[] { "\"0\"", "\"0.00\"", "\"0.##\"", "\"#,##0\"", "\"_,__0\"", "\"#,##0.00\"", "\"$#,##0\"", "\"$#,##0.00\"", "\"$#,##0.##\"" };
        }
        else if ("MIME Types".equals(defaultsName)) {
          validValues = new String[] { "\"image/gif\"", "\"image/jpeg\"", "\"image/png\"" };
        }
        else if ("Direct Actions".equals(defaultsName)) {
          // do nothing for now
        }
        else if ("Direct Action Classes".equals(defaultsName)) {
          // do nothing for now
        }
        else if ("Page Names".equals(defaultsName)) {
          // do nothing for now
        }
        else if ("Frameworks".equals(defaultsName)) {
          // do nothing for now
        }
        else if ("Resources".equals(defaultsName)) {
          // do nothing for now
        }
        else if ("Actions".equals(defaultsName)) {
          List<BindingValueKey> bindingKeysList = WodReflectionUtils.getBindingKeys(javaProject, wodJavaFileType, "", false, WodReflectionUtils.VOID_ONLY, wodContext);
          validValues = new String[bindingKeysList.size()];
          for (int i = 0; i < validValues.length; i++) {
            validValues[i] = bindingKeysList.get(i).getBindingName();
          }
        }
      }
    }
    return validValues;
  }

}
