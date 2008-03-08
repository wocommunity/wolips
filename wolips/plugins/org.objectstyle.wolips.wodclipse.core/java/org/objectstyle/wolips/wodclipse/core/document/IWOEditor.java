package org.objectstyle.wolips.wodclipse.core.document;

import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;

public interface IWOEditor {
  public WodParserCache getParserCache() throws Exception;

  public IWodElement getSelectedElement(boolean refreshModel) throws Exception;
}
