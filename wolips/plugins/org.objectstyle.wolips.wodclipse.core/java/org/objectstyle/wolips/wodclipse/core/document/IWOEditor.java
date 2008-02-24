package org.objectstyle.wolips.wodclipse.core.document;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.locate.LocateException;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;

public interface IWOEditor {
  public WodParserCache getParserCache() throws CoreException, LocateException;

  public IWodElement getSelectedElement() throws CoreException, LocateException, IOException;
}
