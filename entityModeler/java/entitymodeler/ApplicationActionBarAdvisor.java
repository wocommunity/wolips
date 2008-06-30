package entitymodeler;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuListener2;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;

import entitymodeler.actions.NewEOModelAction;
import entitymodeler.actions.OpenEOModelAction;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
  // Actions - important to allocate these only in makeActions, and then use them
  // in the fill methods.  This ensures that the actions aren't recreated
  // when fillActionBars is called with FILL_PROXY.
  private IWorkbenchAction _newAction;
  private IWorkbenchAction _openAction;
  private IWorkbenchAction _saveAction;
  private IWorkbenchAction _exitAction;

  private IWorkbenchAction _undoAction;
  private IWorkbenchAction _redoAction;
  private IWorkbenchAction _cutAction;
  private IWorkbenchAction _copyAction;
  private IWorkbenchAction _pasteAction;
  private IWorkbenchAction _deleteAction;
  private IWorkbenchAction _selectAllAction;

  private IWorkbenchAction _aboutAction;
  private IWorkbenchAction _preferencesAction;

  //private OpenViewAction openViewAction;
  //private IWorkbenchAction messagePopupAction;

  public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
    super(configurer);
  }

  @Override
  protected void makeActions(final IWorkbenchWindow window) {
    // File
    _newAction = ApplicationActionBarAdvisor.NEW.create(window);
    register(_newAction);

    _openAction = ApplicationActionBarAdvisor.OPEN.create(window);
    register(_openAction);

    _saveAction = ActionFactory.SAVE.create(window);
    register(_saveAction);

    _exitAction = ActionFactory.QUIT.create(window);
    register(_exitAction);

    // Edit
    _undoAction = ActionFactory.UNDO.create(window);
    register(_undoAction);

    _redoAction = ActionFactory.REDO.create(window);
    register(_redoAction);

    _cutAction = ActionFactory.CUT.create(window);
    register(_cutAction);

    _copyAction = ActionFactory.COPY.create(window);
    register(_copyAction);

    _pasteAction = ActionFactory.PASTE.create(window);
    register(_pasteAction);

    _deleteAction = ActionFactory.DELETE.create(window);
    register(_deleteAction);

    _selectAllAction = ActionFactory.SELECT_ALL.create(window);
    register(_selectAllAction);

    // Help
    _aboutAction = ActionFactory.ABOUT.create(window);
    register(_aboutAction);

    _preferencesAction = ActionFactory.PREFERENCES.create(window);
    register(_preferencesAction);

    //openViewAction = new OpenViewAction(window, "Open Another Message View", View.ID);
    //register(openViewAction);

    //messagePopupAction = ActionFactory.ABOUT.create(window);
    //register(messagePopupAction);
  }

  public static final ActionFactory NEW = new ActionFactory("new") {//$NON-NLS-1$
    @Override
    public IWorkbenchAction create(IWorkbenchWindow window) {
      if (window == null) {
        throw new IllegalArgumentException();
      }
      NewEOModelAction action = new NewEOModelAction(window);
      action.setId(getId());
      return action;
    }
  };

  public static final ActionFactory OPEN = new ActionFactory("open") {//$NON-NLS-1$
    @Override
    public IWorkbenchAction create(IWorkbenchWindow window) {
      if (window == null) {
        throw new IllegalArgumentException();
      }
      IWorkbenchAction action = new OpenEOModelAction(window);
      action.setId(getId());
      return action;
    }
  };

  @Override
  protected void fillMenuBar(IMenuManager menuBar) {
    MenuManager fileMenu = new MenuManager(IDEWorkbenchMessages.Workbench_file, IWorkbenchActionConstants.M_FILE);
    MenuManager editMenu = new MenuManager(IDEWorkbenchMessages.Workbench_edit, IWorkbenchActionConstants.M_EDIT);
    MenuManager helpMenu = new MenuManager(IDEWorkbenchMessages.Workbench_help, IWorkbenchActionConstants.M_HELP);

    menuBar.add(fileMenu);
    menuBar.add(editMenu);
    // Add a group marker indicating where action set menus will appear.
    menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
    menuBar.add(helpMenu);

    // File
    fileMenu.add(_newAction);
    fileMenu.add(_openAction);
    fileMenu.add(_saveAction);
    //fileMenu.add(new Separator());
    //fileMenu.add(messagePopupAction);
    //fileMenu.add(openViewAction);
    //fileMenu.add(new Separator());
    ActionContributionItem exitItem = new ActionContributionItem(_exitAction);
    exitItem.setVisible(!"carbon".equals(SWT.getPlatform()));
    fileMenu.add(exitItem);

    //MenuManager windowMenu = new MenuManager(IDEWorkbenchMessages.Workbench_window, IWorkbenchActionConstants.M_WINDOW);
    ActionContributionItem preferencesItem = new ActionContributionItem(_preferencesAction);
    preferencesItem.setVisible(!"carbon".equals(SWT.getPlatform()));
    helpMenu.add(preferencesItem);

    // MS: Eclipse core plugins inject some dumb shit into
    // the File menu that there doesn't appear to be a
    // good way to remove.  So I'm going to do it
    // the hard way ...
    fileMenu.addMenuListener(new IMenuListener2() {
      public void menuAboutToHide(IMenuManager manager) {
      }

      public void menuAboutToShow(IMenuManager manager) {
        IContributionItem[] items = manager.getItems();
        for (IContributionItem item : items) {
          String id = item.getId();
          if ("new.ext".equals(id) || "org.eclipse.ui.openLocalFile".equals(id)) {
            manager.remove(item);
          }
        }
      }
    });

    editMenu.add(_undoAction);
    editMenu.add(_redoAction);
    editMenu.add(new Separator());
    editMenu.add(_cutAction);
    editMenu.add(_copyAction);
    editMenu.add(_pasteAction);
    editMenu.add(new Separator());
    editMenu.add(_deleteAction);
    editMenu.add(_selectAllAction);

    // Help
    ActionContributionItem aboutItem = new ActionContributionItem(_aboutAction);
    aboutItem.setVisible(!"carbon".equals(SWT.getPlatform())); //$NON-NLS-1$
    helpMenu.add(aboutItem);

    // MS: Most Mac apps don't have icons on menu items ...
    for (IContributionItem menu : menuBar.getItems()) {
      if (menu instanceof MenuManager) {
        for (IContributionItem menuItem : ((MenuManager) menu).getItems()) {
          if (menuItem instanceof ActionContributionItem) {
            ((ActionContributionItem) menuItem).getAction().setImageDescriptor(null);
          }
        }
      }
    }
  }

  @Override
  protected void fillCoolBar(ICoolBarManager coolBar) {
    //IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
    //coolBar.add(new ToolBarContributionItem(toolbar, "main"));
    //toolbar.add(openViewAction);
    //toolbar.add(messagePopupAction);
    //coolBar.setLockLayout(true);
  }
}
