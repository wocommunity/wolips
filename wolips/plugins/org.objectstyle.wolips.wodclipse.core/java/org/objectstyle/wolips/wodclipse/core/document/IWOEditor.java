package org.objectstyle.wolips.wodclipse.core.document;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;

public interface IWOEditor {
  public WodParserCache getParserCache() throws Exception;

  public IWodElement getSelectedElement(boolean resolveWodElement, boolean refreshModel) throws Exception;

  public IWodElement getWodElementAtPoint(Point point, boolean resolveWodElement, boolean refreshModel) throws Exception;
  
  public Control getWOEditorControl();
}
