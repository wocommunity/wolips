package entitymodeler;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import entitymodeler.actions.OpenAction;

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

  //private OpenViewAction openViewAction;
  //private Action messagePopupAction;

  public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
    super(configurer);
  }

  @Override
  protected void makeActions(final IWorkbenchWindow window) {
    // Creates the actions and registers them.
    // Registering is needed to ensure that key bindings work.
    // The corresponding commands keybindings are defined in the plugin.xml file.
    // Registering also provides automatic disposal of the actions when
    // the window is closed.

    // File
    _newAction = ActionFactory.NEW.create(window);
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

    //openViewAction = new OpenViewAction(window, "Open Another Message View", View.ID);
    //register(openViewAction);

    //messagePopupAction = new MessagePopupAction("Open Message", window);
    //register(messagePopupAction);
  }

  public static final ActionFactory OPEN = new ActionFactory("open") {//$NON-NLS-1$
    @Override
    public IWorkbenchAction create(IWorkbenchWindow window) {
      if (window == null) {
        throw new IllegalArgumentException();
      }
      IWorkbenchAction action = new OpenAction(window);
      action.setId(getId());
      return action;
    }
  };

  @Override
  protected void fillMenuBar(IMenuManager menuBar) {
    MenuManager fileMenu = new MenuManager("&File", IWorkbenchActionConstants.M_FILE);
    MenuManager editMenu = new MenuManager("&Edit", IWorkbenchActionConstants.M_EDIT);
    MenuManager helpMenu = new MenuManager("&Help", IWorkbenchActionConstants.M_HELP);

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
    fileMenu.add(new Separator());
    fileMenu.add(_exitAction);

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
    helpMenu.add(_aboutAction);
  }

  @Override
  protected void fillCoolBar(ICoolBarManager coolBar) {
    IToolBarManager toolbar = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
    coolBar.add(new ToolBarContributionItem(toolbar, "main"));
    //toolbar.add(openViewAction);
    //toolbar.add(messagePopupAction);
  }
}
