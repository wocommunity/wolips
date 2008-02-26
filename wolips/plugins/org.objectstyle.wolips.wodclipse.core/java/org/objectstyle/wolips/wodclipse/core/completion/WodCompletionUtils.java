package org.objectstyle.wolips.wodclipse.core.completion;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.SearchPattern;
import org.objectstyle.wolips.bindings.api.ApiUtils;
import org.objectstyle.wolips.bindings.api.Binding;
import org.objectstyle.wolips.bindings.api.Wo;
import org.objectstyle.wolips.bindings.utils.BindingReflectionUtils;
import org.objectstyle.wolips.bindings.wod.BindingValueKey;
import org.objectstyle.wolips.bindings.wod.BindingValueKeyPath;
import org.objectstyle.wolips.bindings.wod.HtmlElementCache;
import org.objectstyle.wolips.bindings.wod.TypeCache;
import org.objectstyle.wolips.core.resources.types.TypeNameCollector;

public class WodCompletionUtils {
  protected static boolean shouldSmartInsert() {
    return true;
  }

  public static void fillInElementNameCompletionProposals(Set<String> alreadyUsedElementNames, String token, int tokenOffset, int offset, Set<WodCompletionProposal> completionProposalsSet, boolean guessed, HtmlElementCache validElementNames) {
    String partialToken = partialToken(token, tokenOffset, offset).toLowerCase();
    for (String validElementName : validElementNames.elementNames()) {
      if (validElementName.toLowerCase().startsWith(partialToken) && !alreadyUsedElementNames.contains(validElementName)) {
        WodCompletionProposal completionProposal;
        if (WodCompletionUtils.shouldSmartInsert() && guessed) {
          completionProposal = new WodCompletionProposal(token, tokenOffset, offset, validElementName + " : ", validElementName, validElementName.length() + 3);
        }
        else {
          completionProposal = new WodCompletionProposal(token, tokenOffset, offset, validElementName);
        }
        completionProposalsSet.add(completionProposal);
      }
    }
  }

  public static void fillInElementTypeCompletionProposals(IJavaProject project, String token, int tokenOffset, int offset, Set<WodCompletionProposal> completionProposalsSet, boolean guessed, IProgressMonitor progressMonitor) throws JavaModelException {
    // Lookup type names that extend WOElement based on the current partial
    // token
    TypeNameCollector typeNameCollector = new TypeNameCollector(project, false);
    String partialToken = partialToken(token, tokenOffset, offset);
    if (partialToken.length() > 0) {
      BindingReflectionUtils.findMatchingElementClassNames(partialToken, SearchPattern.R_PREFIX_MATCH, typeNameCollector, progressMonitor);
      boolean includePackageName = token.indexOf('.') != -1;
      Iterator<String> matchingElementClassNamesIter = typeNameCollector.typeNames();
      while (matchingElementClassNamesIter.hasNext()) {
        String matchingElementTypeName = matchingElementClassNamesIter.next();
        String elementTypeName;
        if (includePackageName) {
          elementTypeName = matchingElementTypeName;
        }
        else {
          elementTypeName = BindingReflectionUtils.getShortClassName(matchingElementTypeName);
        }
        WodCompletionProposal completionProposal;
        if (WodCompletionUtils.shouldSmartInsert() && guessed) {
          completionProposal = new WodCompletionProposal(token, tokenOffset, offset, elementTypeName + " {\n\t\n}", elementTypeName, elementTypeName.length() + 4);
        }
        else {
          completionProposal = new WodCompletionProposal(token, tokenOffset, offset, elementTypeName);
        }
        completionProposalsSet.add(completionProposal);
      }
    }
  }

  public static void fillInBindingNameCompletionProposals(IJavaProject project, IType elementType, String token, int tokenOffset, int offset, Set<WodCompletionProposal> completionProposalsSet, boolean guessed, TypeCache cache) throws JavaModelException {
    String partialToken = WodCompletionUtils.partialToken(token, tokenOffset, offset);
    boolean showReflectionBindings = true;

    // API files:
    try {
      Wo wo = ApiUtils.findApiModelWo(elementType, cache.getApiCache(project));
      if (wo != null) {
        String lowercasePartialToken = partialToken.toLowerCase();
        List<Binding> bindings = wo.getBindings();
        for (Binding binding : bindings) {
          String bindingName = binding.getName();
          String lowercaseBindingName = bindingName.toLowerCase();
          if (lowercaseBindingName.startsWith(lowercasePartialToken)) {
            WodCompletionProposal completionProposal;
            if (WodCompletionUtils.shouldSmartInsert() && guessed) {
              completionProposal = new WodCompletionProposal(token, tokenOffset, offset, bindingName + " = ", bindingName, bindingName.length() + 3);
            }
            else {
              completionProposal = new WodCompletionProposal(token, tokenOffset, offset, bindingName);
            }
            completionProposalsSet.add(completionProposal);
          }
        }

        if (bindings != null && bindings.size() > 0) {
          showReflectionBindings = false;
        }
      }
    }
    catch (Throwable t) {
      // It's not that big a deal ... give up on api files
      t.printStackTrace();
    }

    if (showReflectionBindings) {
      List<BindingValueKey> bindingKeys = BindingReflectionUtils.getBindingKeys(project, elementType, partialToken, false, BindingReflectionUtils.MUTATORS_ONLY, false, cache);
      WodCompletionUtils._fillInCompletionProposals(bindingKeys, token, tokenOffset, offset, completionProposalsSet, false);
    }
  }

  public static boolean fillInBindingValueCompletionProposals(IJavaProject project, IType elementType, String token, int tokenOffset, int offset, Set<WodCompletionProposal> completionProposalsSet, TypeCache cache) throws JavaModelException {
    boolean checkBindingType = false;
    String partialToken = partialToken(token, tokenOffset, offset);
    BindingValueKeyPath bindingKeyPath = new BindingValueKeyPath(partialToken, elementType, project, cache);
    List<BindingValueKey> possibleBindingKeyMatchesList = bindingKeyPath.getPartialMatchesForLastBindingKey(false);
    if (possibleBindingKeyMatchesList != null) {
      String bindingKeyName;
      if (bindingKeyPath.getOperator() != null) {
        bindingKeyName = "@" + bindingKeyPath.getOperator();
      }
      else {
        bindingKeyName = bindingKeyPath.getLastBindingKeyName();
      }
      WodCompletionUtils._fillInCompletionProposals(possibleBindingKeyMatchesList, bindingKeyName, tokenOffset + partialToken.lastIndexOf('.') + 1, offset, completionProposalsSet, true);
    }

    // Only do binding type checks if you're on the first of a keypath ...
    if (bindingKeyPath != null && bindingKeyPath.getLength() == 1) {
      checkBindingType = true;
    }
    return checkBindingType;
  }

  public static String partialToken(String token, int tokenOffset, int offset) {
    String partialToken;
    int partialIndex = offset - tokenOffset;
    if (partialIndex > token.length()) {
      partialToken = token;
    }
    else {
      partialToken = token.substring(0, offset - tokenOffset);
    }
    return partialToken;
  }

  protected static void _fillInCompletionProposals(List<BindingValueKey> bindingKeys, String token, int tokenOffset, int offset, Set<WodCompletionProposal> completionProposalsSet, boolean showUsefulSystemBindings) {
    Iterator<BindingValueKey> bindingKeysIter = BindingReflectionUtils.filterSystemBindingValueKeys(bindingKeys, showUsefulSystemBindings).iterator();
    while (bindingKeysIter.hasNext()) {
      BindingValueKey bindingKey = bindingKeysIter.next();
      WodCompletionProposal completionProposal = new WodCompletionProposal(token, tokenOffset, offset, bindingKey.getBindingName());
      completionProposalsSet.add(completionProposal);
    }
  }

}
