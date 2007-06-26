package entitymodeler.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

public class NewEOModelPage extends WizardPage {
  public NewEOModelPage(String pageName) {
    super(pageName);
  }

  public NewEOModelPage(String pageName, String title, ImageDescriptor titleImage) {
    super(pageName, title, titleImage);
  }

  public void createControl(Composite parent) {
  }
  
  @Override
  public void setVisible(boolean visible) {
    // TODO Auto-generated method stub
    super.setVisible(visible);
  }
}
