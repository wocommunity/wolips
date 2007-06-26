package entitymodeler.model;

import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelException;
import org.objectstyle.wolips.eomodeler.core.model.IEOClassLoaderFactory;

public class SimpleEOClassLoaderFactory implements IEOClassLoaderFactory {

  @SuppressWarnings("unused")
  public ClassLoader createClassLoaderForModel(EOModel model) throws EOModelException {
    return getClass().getClassLoader();
  }

}
