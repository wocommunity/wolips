package org.objectstyle.wolips.wodclipse.core.refactoring;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.model.IWodElement;
import org.objectstyle.wolips.wodclipse.core.model.IWodModel;

public class CleanWOBuilderRefactoring implements IRunnableWithProgress {
  private WodParserCache _cache;
  private boolean _forceRename;
  
  public CleanWOBuilderRefactoring(WodParserCache cache, boolean forceRename) {
    _cache = cache;
    _forceRename = forceRename;
  }
  
  public void run(IProgressMonitor monitor) throws InvocationTargetException {
    try {
      _cache.clearCache();
      
      List<ElementRename> elementRenames = new LinkedList<ElementRename>();
      Set<String> elementNames = new HashSet<String>();
      IWodModel wodModel = _cache.getWodModel();
      for (IWodElement wodElement : wodModel.getElements()) {
        ElementRename elementRename = ElementRename.newUniqueName(wodModel, wodElement, elementNames, _forceRename);
        if (elementRename != null) {
          elementRenames.add(elementRename);
        }
      }
      new RenameElementsRefactoring(elementRenames, _cache).run(monitor);
    }
    catch (Exception e) {
      throw new InvocationTargetException(e);
    }
  }
  
  public static void run(WodParserCache cache, boolean forceRename, IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException, CoreException {
    TemplateRefactoring.processHtmlAndWod(new CleanWOBuilderRefactoring(cache, forceRename), cache, progressMonitor);
  }
}
