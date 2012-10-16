package tk.eclipse.plugin.htmleditor.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * PaletteView.
 * <p>
 * When HTMLSourceEditor or IPaletteTarget actives,
 * inserts a tag that is selected in the palette to the calet position.
 * </p>
 */
public class PaletteView extends ViewPart {

  @Override
  public void createPartControl(Composite parent) {
  }

  @Override
  public void setFocus() {
  }
  
//  private PaletteViewer viewer;
//  private TreeMap items = new TreeMap();
//  private HashMap tools = new HashMap();
//  private String[] defaultCategories;
//
//  public PaletteView() {
//    addPaletteItem("HTML", new DefaultPaletteItem("form", HTMLPlugin.getDefault().getImageRegistry().getDescriptor(HTMLPlugin.ICON_FORM), "<form action=\"\" method=\"\">${selection}${cursor}</form>"));
//    addPaletteItem("HTML", new DefaultPaletteItem("text", HTMLPlugin.getDefault().getImageRegistry().getDescriptor(HTMLPlugin.ICON_TEXT), "<input type=\"text\" name=\"${cursor}\" value=\"\" />"));
//    addPaletteItem("HTML", new DefaultPaletteItem("textarea", HTMLPlugin.getDefault().getImageRegistry().getDescriptor(HTMLPlugin.ICON_TEXTAREA), "<textarea name=\"\" rows=\"\" cols=\"\"></textarea>"));
//    addPaletteItem("HTML", new DefaultPaletteItem("password", HTMLPlugin.getDefault().getImageRegistry().getDescriptor(HTMLPlugin.ICON_PASS), "<input type=\"password\" name=\"\" value=\"\" />"));
//    addPaletteItem("HTML", new DefaultPaletteItem("radio", HTMLPlugin.getDefault().getImageRegistry().getDescriptor(HTMLPlugin.ICON_RADIO), "<input type=\"radio\" name=\"\" value=\"\" />"));
//    addPaletteItem("HTML", new DefaultPaletteItem("checkbox", HTMLPlugin.getDefault().getImageRegistry().getDescriptor(HTMLPlugin.ICON_CHECK), "<input type=\"checkbox\" name=\"\" value=\"\" />"));
//    addPaletteItem("HTML", new DefaultPaletteItem("button", HTMLPlugin.getDefault().getImageRegistry().getDescriptor(HTMLPlugin.ICON_BUTTON), "<input type=\"button\" name=\"\" value=\"\" />"));
//    addPaletteItem("HTML", new DefaultPaletteItem("submit", HTMLPlugin.getDefault().getImageRegistry().getDescriptor(HTMLPlugin.ICON_BUTTON), "<input type=\"submit\" name=\"\" value=\"\" />"));
//    addPaletteItem("HTML", new DefaultPaletteItem("reset", HTMLPlugin.getDefault().getImageRegistry().getDescriptor(HTMLPlugin.ICON_BUTTON), "<input type=\"reset\" value=\"\" />"));
//
//    // add items contributed from other plugins
//    String[] groups = HTMLPlugin.getDefault().getPaletteContributerGroups();
//    for (int i = 0; i < groups.length; i++) {
//      IPaletteContributer contributer = HTMLPlugin.getDefault().getPaletteContributer(groups[i]);
//      IPaletteItem[] items = contributer.getPaletteItems();
//      for (int j = 0; j < items.length; j++) {
//        addPaletteItem(groups[i], items[j]);
//      }
//    }
//
//    // save default categories
//    defaultCategories = getCategories();
//  }
//
//  private void createToolBar() {
//    Action customize = new Action("Configuration", HTMLPlugin.getDefault().getImageRegistry().getDescriptor(HTMLPlugin.ICON_PROPERTY)) {
//      public void run() {
//        PaletteCustomizeDialog dialog = new PaletteCustomizeDialog(getViewSite().getShell());
//        dialog.open();
//      }
//    };
//    customize.setToolTipText("Configuration");
//
//    IToolBarManager mgr = getViewSite().getActionBars().getToolBarManager();
//    mgr.add(customize);
//  }
//
//  /**
//   * create controls and apply configurations.
//   */
//  public void createPartControl(Composite parent) {
//    viewer = new PaletteViewer();
//    viewer.createControl(parent);
//
//    PaletteRoot root = new PaletteRoot();
//
//    String[] category = getCategories();
//    for (int i = 0; i < category.length; i++) {
//      PaletteDrawer group = new PaletteDrawer(category[i]);
//      IPaletteItem[] items = getPaletteItems(category[i]);
//      for (int j = 0; j < items.length; j++) {
//        HTMLPaletteEntry entry = new HTMLPaletteEntry(items[j].getLabel(), null, items[j].getImageDescriptor());
//        tools.put(entry, items[j]);
//        group.add(entry);
//      }
//      root.add(group);
//    }
//
//    viewer.setPaletteRoot(root);
//
//    viewer.addPaletteListener(new PaletteListener() {
//      public void activeToolChanged(PaletteViewer palette, ToolEntry tool) {
//        Object obj = palette.getEditPartRegistry().get(tool);
//        if (!(obj instanceof ToolEntryEditPart)) {
//          return;
//        }
//        ToolEntryEditPart part = (ToolEntryEditPart) obj;
//        if (part != null) {
//          // get the active editor
//          IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//          IEditorPart editorPart = page.getActiveEditor();
//          // execute processing of the palette item
//          if (editorPart != null) {
//            IPaletteItem item = (IPaletteItem) tools.get(tool);
//            if (editorPart instanceof HTMLSourceEditor) {
//              item.execute((HTMLSourceEditor) editorPart);
//            }
//            else if (editorPart instanceof IPaletteTarget) {
//              item.execute(((IPaletteTarget) editorPart).getPaletteTarget());
//            }
//            else if (editorPart instanceof IComponentEditor) {
//              IEditorPart activeEditorPart = ((IComponentEditor) editorPart).getActiveEditor();
//              if (activeEditorPart instanceof TemplateEditor) {
//                TemplateEditor templateEditor = (TemplateEditor) activeEditorPart;
//                item.execute(templateEditor.getPaletteTarget());
//              }
//            }
//          }
//          // unset palette selection
//          part.setToolSelected(false);
//        }
//      }
//    });
//    viewer.getControl().addMouseListener(new MouseAdapter() {
//      public void mouseUp(MouseEvent e) {
//        // set focus to the active editor
//        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
//        IEditorPart editorPart = page.getActiveEditor();
//        if (editorPart != null) {
//          editorPart.setFocus();
//        }
//      }
//    });
//
//    // apply configuration (too long!!)
//    IPreferenceStore store = HTMLPlugin.getDefault().getPreferenceStore();
//    String xml = store.getString(HTMLPlugin.PREF_PALETTE_ITEMS);
//    if (xml != null) {
//      FuzzyXMLDocument doc = new FuzzyXMLParser().parse(xml);
//      // apply visible
//      FuzzyXMLNode[] groups = HTMLUtil.selectXPathNodes(doc.getDocumentElement(), "/palette/groups/group");
//      for (int i = 0; i < groups.length; i++) {
//        FuzzyXMLElement group = (FuzzyXMLElement) groups[i];
//
//        String name = group.getAttributeNode("name").getValue();
//        boolean visible = Boolean.valueOf(group.getAttributeNode("visible").getValue()).booleanValue();
//
//        List entries = viewer.getPaletteRoot().getChildren();
//        PaletteDrawer drawer = null;
//
//        for (int j = 0; j < entries.size(); j++) {
//          drawer = (PaletteDrawer) entries.get(j);
//          if (drawer.getLabel().equals(name)) {
//            drawer.setVisible(visible);
//            break;
//          }
//          else {
//            drawer = null;
//          }
//        }
//        if (drawer == null) {
//          drawer = new PaletteDrawer(name);
//          drawer.setVisible(visible);
//          viewer.getPaletteRoot().add(drawer);
//        }
//      }
//      // add user items
//      FuzzyXMLNode[] items = HTMLUtil.selectXPathNodes(doc.getDocumentElement(), "/palette/items/item");
//      String[] categories = getCategories();
//      for (int i = 0; i < items.length; i++) {
//        FuzzyXMLElement item = (FuzzyXMLElement) items[i];
//        String name = item.getAttributeNode("name").getValue();
//        String group = item.getAttributeNode("group").getValue();
//        String text = item.getValue();
//        if (Arrays.binarySearch(categories, group) < 0) {
//          addPaletteItem(group, new DefaultPaletteItem(name, HTMLPlugin.getDefault().getImageRegistry().getDescriptor(HTMLPlugin.ICON_TAG), text));
//        }
//      }
//    }
//
//    List entries = viewer.getPaletteRoot().getChildren();
//    for (int i = 0; i < entries.size(); i++) {
//      PaletteDrawer group = (PaletteDrawer) entries.get(i);
//      if (Arrays.binarySearch(defaultCategories, group.getLabel()) < 0) {
//        IPaletteItem[] items = getPaletteItems(group.getLabel());
//        ArrayList itemList = new ArrayList();
//        for (int j = 0; j < items.length; j++) {
//          HTMLPaletteEntry entry = new HTMLPaletteEntry(items[j].getLabel(), null, items[j].getImageDescriptor());
//          tools.put(entry, items[j]);
//          itemList.add(entry);
//        }
//        group.setChildren(itemList);
//      }
//    }
//
//    // create toolbar
//    createToolBar();
//  }
//
//  /**
//   * Adds PaletteItem to the specified category.
//   * 
//   * @param category the category
//   * @param item the item
//   */
//  private void addPaletteItem(String category, IPaletteItem item) {
//    if (items.get(category) == null) {
//      ArrayList list = new ArrayList();
//      items.put(category, list);
//    }
//    ArrayList list = (ArrayList) items.get(category);
//    list.add(item);
//  }
//
//  /**
//   * Update the category information.
//   * <p>
//   * If the category already exists, overwrites the category infomation.
//   * Otherwise, creates the new category and appends it to the palette.
//   * 
//   * @param category the category
//   * @param items the map contains items
//   */
//  private void updateCategory(String category, List items) {
//
//    viewer.setActiveTool(null);
//
//    // remove all items
//    ArrayList list = (ArrayList) this.items.get(category);
//    if (list != null) {
//      list.clear();
//    }
//
//    List entries = viewer.getPaletteRoot().getChildren();
//    PaletteDrawer group = null;
//
//    for (int i = 0; i < entries.size(); i++) {
//      group = (PaletteDrawer) entries.get(i);
//      if (group.getLabel().equals(category)) {
//        break;
//      }
//      else {
//        group = null;
//      }
//    }
//
//    if (group == null) {
//      group = new PaletteDrawer(category);
//      viewer.getPaletteRoot().add(group);
//    }
//
//    // add items
//    for (int i = 0; i < items.size(); i++) {
//      Map map = (Map) items.get(i);
//      addPaletteItem(category, new DefaultPaletteItem((String) map.get("name"), HTMLPlugin.getDefault().getImageRegistry().getDescriptor(HTMLPlugin.ICON_TAG), (String) map.get("text")));
//    }
//
//    ArrayList itemList = new ArrayList();
//    IPaletteItem[] newItems = getPaletteItems(category);
//    for (int i = 0; i < newItems.length; i++) {
//      HTMLPaletteEntry entry = new HTMLPaletteEntry(newItems[i].getLabel(), null, newItems[i].getImageDescriptor());
//      tools.put(entry, newItems[i]);
//      itemList.add(entry);
//    }
//    group.setChildren(itemList);
//  }
//
//  /**
//   * Removes the category.
//   * 
//   * @param category the category
//   */
//  private void removeCategory(String category) {
//
//    viewer.setActiveTool(null);
//
//    this.items.remove(category);
//
//    List entries = viewer.getPaletteRoot().getChildren();
//    PaletteDrawer group = null;
//
//    for (int i = 0; i < entries.size(); i++) {
//      group = (PaletteDrawer) entries.get(i);
//      if (group.getLabel().equals(category)) {
//        List children = group.getChildren();
//        for (int j = 0; j < children.size(); j++) {
//          tools.remove((PaletteEntry) children.get(j));
//          group.remove((PaletteEntry) children.get(j));
//        }
//        viewer.getPaletteRoot().remove(group);
//        break;
//      }
//    }
//  }
//
//  /**
//   * Returns PaletteItems which are contained by the specified category.
//   * 
//   * @param category the category
//   * @return the array of items which are contained by the category
//   */
//  private IPaletteItem[] getPaletteItems(String category) {
//    ArrayList list = (ArrayList) items.get(category);
//    if (list == null) {
//      return new IPaletteItem[0];
//    }
//    return (IPaletteItem[]) list.toArray(new IPaletteItem[list.size()]);
//  }
//
//  /**
//   * Returns all categories.
//   * 
//   * @return the array which contains all categories
//   */
//  private String[] getCategories() {
//    return (String[]) items.keySet().toArray(new String[0]);
//  }
//
//  public void setFocus() {
//    viewer.getControl().setFocus();
//  }
//
//  /** ToolEntry for HTML tag palette */
//  private class HTMLPaletteEntry extends ToolEntry {
//
//    public HTMLPaletteEntry(String label, String shortDescription, ImageDescriptor icon) {
//      super(label, shortDescription, icon, icon);
//    }
//
//    public Tool createTool() {
//      return null;
//    }
//  }
//
//  /** Returns palette configuration as XML. */
//  private String getPreferenceXML() {
//    StringBuffer sb = new StringBuffer();
//    sb.append("<palette>");
//    List entries = viewer.getPaletteRoot().getChildren();
//    sb.append("<groups>");
//    for (int i = 0; i < entries.size(); i++) {
//      PaletteDrawer group = (PaletteDrawer) entries.get(i);
//      sb.append("<group name=\"" + HTMLUtil.escapeXML(group.getLabel()) + "\"" + " visible=\"" + HTMLUtil.escapeXML(String.valueOf(group.isVisible())) + "\" />");
//    }
//    sb.append("</groups>");
//    sb.append("<items>");
//    String[] categories = getCategories();
//    for (int i = 0; i < categories.length; i++) {
//      if (Arrays.binarySearch(defaultCategories, categories[i]) < 0) {
//        IPaletteItem[] items = getPaletteItems(categories[i]);
//        for (int j = 0; j < items.length; j++) {
//          sb.append("<item group=\"" + HTMLUtil.escapeXML(categories[i]) + "\"" + " name=\"" + HTMLUtil.escapeXML(items[j].getLabel()) + "\">" + HTMLUtil.escapeXML(((DefaultPaletteItem) items[j]).getContent()) + "</item>");
//        }
//      }
//    }
//    sb.append("</items>");
//    sb.append("</palette>");
//    return sb.toString();
//  }
//
//  /** The dialog for palette customization */
//  private class PaletteCustomizeDialog extends Dialog {
//
//    private Table table;
//    private Button add;
//    private Button edit;
//    private Button remove;
//    private HashMap operations = new HashMap();
//
//    public PaletteCustomizeDialog(Shell parentShell) {
//      super(parentShell);
//      setShellStyle(getShellStyle() | SWT.RESIZE);
//    }
//
//    protected Point getInitialSize() {
//      return new Point(300, 300);
//    }
//
//    protected Control createDialogArea(Composite parent) {
//      getShell().setText(HTMLPlugin.getResourceString("Dialog.PaletteConfig"));
//
//      Composite container = new Composite(parent, SWT.NULL);
//      container.setLayout(new GridLayout(2, false));
//      container.setLayoutData(new GridData(GridData.FILL_BOTH));
//
//      table = new Table(container, SWT.BORDER | SWT.CHECK);
//      table.setLayoutData(new GridData(GridData.FILL_BOTH));
//      List entries = viewer.getPaletteRoot().getChildren();
//
//      for (int i = 0; i < entries.size(); i++) {
//        TableItem item = new TableItem(table, SWT.LEFT);
//        item.setText(((PaletteDrawer) entries.get(i)).getLabel());
//        item.setChecked(((PaletteDrawer) entries.get(i)).isVisible());
//      }
//
//      table.addSelectionListener(new SelectionAdapter() {
//        public void widgetSelected(SelectionEvent evt) {
//          // Can't modify default categories
//          TableItem[] items = table.getSelection();
//          if (items.length == 0 || Arrays.binarySearch(defaultCategories, items[0].getText()) >= 0) {
//            edit.setEnabled(false);
//            remove.setEnabled(false);
//          }
//          else {
//            edit.setEnabled(true);
//            remove.setEnabled(true);
//          }
//        }
//      });
//
//      Composite buttons = new Composite(container, SWT.NULL);
//      buttons.setLayout(new GridLayout());
//      buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
//
//      add = new Button(buttons, SWT.PUSH);
//      add.setText(HTMLPlugin.getResourceString("Button.AddGroup"));
//      add.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//      add.addSelectionListener(new SelectionAdapter() {
//        public void widgetSelected(SelectionEvent evt) {
//          PaletteGroupDialog dialog = new PaletteGroupDialog(getShell());
//          if (dialog.open() == Dialog.OK) {
//            TableItem item = new TableItem(table, SWT.NULL);
//            item.setText(dialog.getGroupName());
//            item.setChecked(true);
//            operations.put(dialog.getGroupName(), dialog.getPaletteItems());
//          }
//        }
//      });
//
//      edit = new Button(buttons, SWT.PUSH);
//      edit.setText(HTMLPlugin.getResourceString("Button.EditGroup"));
//      edit.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//      edit.setEnabled(false);
//      edit.addSelectionListener(new SelectionAdapter() {
//        public void widgetSelected(SelectionEvent evt) {
//          TableItem[] items = table.getSelection();
//
//          List initItems = new ArrayList();
//          Object obj = operations.get(items[0].getText());
//          if (obj != null && obj instanceof List) {
//            initItems = (List) obj;
//          }
//          else {
//            IPaletteItem[] paletteItems = getPaletteItems(items[0].getText());
//            for (int i = 0; i < paletteItems.length; i++) {
//              HashMap map = new HashMap();
//              map.put("name", paletteItems[i].getLabel());
//              map.put("text", ((DefaultPaletteItem) paletteItems[i]).getContent());
//              initItems.add(map);
//            }
//          }
//          PaletteGroupDialog dialog = new PaletteGroupDialog(getShell(), items[0].getText(), initItems);
//          if (dialog.open() == Dialog.OK) {
//            items[0].setText(dialog.getGroupName());
//            operations.put(items[0].getText(), dialog.getPaletteItems());
//          }
//        }
//      });
//
//      remove = new Button(buttons, SWT.PUSH);
//      remove.setText(HTMLPlugin.getResourceString("Button.RemoveGroup"));
//      remove.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//      remove.setEnabled(false);
//      remove.addSelectionListener(new SelectionAdapter() {
//        public void widgetSelected(SelectionEvent evt) {
//          TableItem[] items = table.getSelection();
//          operations.put(items[0].getText(), "remove");
//          table.remove(table.getSelectionIndex());
//        }
//      });
//
//      return container;
//    }
//
//    protected void okPressed() {
//      Iterator ite = operations.keySet().iterator();
//      while (ite.hasNext()) {
//        String key = (String) ite.next();
//        Object obj = operations.get(key);
//        if (obj.equals("remove")) {
//          removeCategory(key);
//        }
//        else {
//          updateCategory(key, (List) obj);
//        }
//      }
//      //			String[] groups = getCategories();
//      List entries = viewer.getPaletteRoot().getChildren();
//      for (int i = 0; i < entries.size(); i++) {
//        ((PaletteDrawer) entries.get(i)).setVisible(table.getItem(i).getChecked());
//      }
//      IPreferenceStore store = HTMLPlugin.getDefault().getPreferenceStore();
//      store.setValue(HTMLPlugin.PREF_PALETTE_ITEMS, getPreferenceXML());
//
//      super.okPressed();
//    }
//  }
//
//  /** The dialog to edit a palette group */
//  private class PaletteGroupDialog extends Dialog {
//
//    private Text name;
//    private Table table;
//    private Button add;
//    private Button addFromTLD;
//    private Button edit;
//    private Button remove;
//
//    private String initialName = null;
//    private List initialItems = null;
//    private String inputedName = null;
//    private List inputedItems = null;
//
//    public PaletteGroupDialog(Shell parentShell) {
//      this(parentShell, null, new ArrayList());
//    }
//
//    public PaletteGroupDialog(Shell parentShell, String name, List items) {
//      super(parentShell);
//      setShellStyle(getShellStyle() | SWT.RESIZE);
//      initialName = name;
//      initialItems = items;
//    }
//
//    protected Point getInitialSize() {
//      return new Point(450, 350);
//    }
//
//    protected Control createDialogArea(Composite parent) {
//      if (initialName == null) {
//        getShell().setText(HTMLPlugin.getResourceString("Dialog.AddPaletteGroup"));
//      }
//      else {
//        getShell().setText(HTMLPlugin.getResourceString("Dialog.EditPaletteGroup"));
//      }
//
//      Composite container = new Composite(parent, SWT.NULL);
//      container.setLayout(new GridLayout(3, false));
//      container.setLayoutData(new GridData(GridData.FILL_BOTH));
//
//      Label label = new Label(container, SWT.NULL);
//      label.setText(HTMLPlugin.getResourceString("Label.GroupName"));
//
//      name = new Text(container, SWT.BORDER);
//      if (initialName != null) {
//        name.setText(initialName);
//        name.setEditable(false);
//      }
//      //			GridData gd = new GridData(GridData.FILL_HORIZONTAL);
//      name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//
//      // fill GridLayout
//      label = new Label(container, SWT.NULL);
//
//      label = new Label(container, SWT.NULL);
//      label.setText(HTMLPlugin.getResourceString("Label.Items"));
//
//      table = new Table(container, SWT.BORDER | SWT.FULL_SELECTION);
//      table.setLayoutData(new GridData(GridData.FILL_BOTH));
//      table.setHeaderVisible(true);
//      table.setLinesVisible(true);
//      table.addSelectionListener(new SelectionAdapter() {
//        public void widgetSelected(SelectionEvent evt) {
//          TableItem[] items = table.getSelection();
//          if (items.length == 0) {
//            edit.setEnabled(false);
//            remove.setEnabled(false);
//          }
//          else {
//            edit.setEnabled(true);
//            remove.setEnabled(true);
//          }
//        }
//      });
//
//      TableColumn col1 = new TableColumn(table, SWT.LEFT);
//      col1.setText(HTMLPlugin.getResourceString("Message.ItemName"));
//      col1.setWidth(100);
//
//      TableColumn col2 = new TableColumn(table, SWT.LEFT);
//      col2.setText(HTMLPlugin.getResourceString("Message.InsertText"));
//      col2.setWidth(250);
//
//      if (initialName != null) {
//        for (int i = 0; i < initialItems.size(); i++) {
//          Map map = (Map) initialItems.get(i);
//          TableItem item = new TableItem(table, SWT.NULL);
//          item.setText(new String[] { (String) map.get("name"), (String) map.get("text") });
//        }
//      }
//
//      Composite buttons = new Composite(container, SWT.NULL);
//      buttons.setLayout(new GridLayout());
//      buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
//
//      add = new Button(buttons, SWT.PUSH);
//      add.setText(HTMLPlugin.getResourceString("Button.AddItem"));
//      add.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//      add.addSelectionListener(new SelectionAdapter() {
//        public void widgetSelected(SelectionEvent evt) {
//          PaletteItemDialog dialog = new PaletteItemDialog(getShell());
//          if (dialog.open() == Dialog.OK) {
//            String name = dialog.getItemName();
//            String text = dialog.getInsertText();
//            TableItem item = new TableItem(table, SWT.NULL);
//            item.setText(new String[] { name, text });
//          }
//        }
//      });
//
//      addFromTLD = new Button(buttons, SWT.PUSH);
//      addFromTLD.setText(HTMLPlugin.getResourceString("Button.AddFromTLD"));
//      addFromTLD.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//      addFromTLD.addSelectionListener(new SelectionAdapter() {
//        public void widgetSelected(SelectionEvent evt) {
//          FileDialog openDialog = new FileDialog(getShell(), SWT.OPEN);
//          openDialog.setFilterExtensions(new String[] { "*.tld" });
//          String openFile = openDialog.open();
//          if (openFile != null) {
//            try {
//              TLDParser parser = new TLDParser(null);
//              parser.parse(new FileInputStream(new File(openFile)));
//              List tagInfoList = parser.getResult();
//              for (int i = 0; i < tagInfoList.size(); i++) {
//                TagInfo info = (TagInfo) tagInfoList.get(i);
//                TableItem item = new TableItem(table, SWT.NULL);
//                StringBuffer sb = new StringBuffer();
//                sb.append("<").append(info.getTagName());
//                AttributeInfo[] attrs = info.getRequiredAttributeInfo();
//                for (int j = 0; j < attrs.length; j++) {
//                  sb.append(" ").append(attrs[j].getAttributeName()).append("=\"\"");
//                }
//                if (info.hasBody()) {
//                  sb.append("></").append(info.getTagName()).append(">");
//                }
//                else {
//                  sb.append("/>");
//                }
//                item.setText(new String[] { info.getTagName(), sb.toString() });
//              }
//            }
//            catch (Exception ex) {
//              HTMLPlugin.openAlertDialog(ex.getMessage());
//            }
//          }
//        }
//      });
//
//      edit = new Button(buttons, SWT.PUSH);
//      edit.setText(HTMLPlugin.getResourceString("Button.EditItem"));
//      edit.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//      edit.setEnabled(false);
//      edit.addSelectionListener(new SelectionAdapter() {
//        public void widgetSelected(SelectionEvent evt) {
//          TableItem[] item = table.getSelection();
//          PaletteItemDialog dialog = new PaletteItemDialog(getShell(), item[0].getText(0), item[0].getText(1));
//          if (dialog.open() == Dialog.OK) {
//            String name = dialog.getItemName();
//            String text = dialog.getInsertText();
//            item[0].setText(new String[] { name, text });
//          }
//        }
//      });
//
//      remove = new Button(buttons, SWT.PUSH);
//      remove.setText(HTMLPlugin.getResourceString("Button.RemoveItem"));
//      remove.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//      remove.setEnabled(false);
//      remove.addSelectionListener(new SelectionAdapter() {
//        public void widgetSelected(SelectionEvent evt) {
//          table.remove(table.getSelectionIndices());
//        }
//      });
//
//      return container;
//    }
//
//    protected void okPressed() {
//      if (name.getText().equals("")) {
//        HTMLPlugin.openAlertDialog(HTMLPlugin.createMessage(HTMLPlugin.getResourceString("Error.Required"), new String[] { HTMLPlugin.getResourceString("Message.GroupName") }));
//        return;
//      }
//
//      if (initialName == null) {
//        String[] categories = getCategories();
//        for (int i = 0; i < categories.length; i++) {
//          if (categories[i].equals(name.getText())) {
//            HTMLPlugin.openAlertDialog(HTMLPlugin.createMessage(HTMLPlugin.getResourceString("Error.AlreadyExists"), new String[] { name.getText() }));
//            return;
//          }
//        }
//      }
//
//      inputedName = name.getText();
//      inputedItems = new ArrayList();
//
//      TableItem[] items = table.getItems();
//      for (int i = 0; i < items.length; i++) {
//        HashMap map = new HashMap();
//        map.put("name", items[i].getText(0));
//        map.put("text", items[i].getText(1));
//        inputedItems.add(map);
//      }
//
//      super.okPressed();
//    }
//
//    public String getGroupName() {
//      return inputedName;
//    }
//
//    public List getPaletteItems() {
//      return inputedItems;
//    }
//  }
//
//  /** The dialog yo edit a palette item */
//  private class PaletteItemDialog extends Dialog {
//
//    private Text itemName;
//    private Text insertText;
//    private String inputedName;
//    private String inputedText;
//    private String initialName;
//    private String initialText;
//
//    public PaletteItemDialog(Shell parentShell) {
//      this(parentShell, null, null);
//    }
//
//    public PaletteItemDialog(Shell parentShell, String name, String text) {
//      super(parentShell);
//      setShellStyle(getShellStyle() | SWT.RESIZE);
//      initialName = name;
//      initialText = text;
//    }
//
//    protected Point getInitialSize() {
//      return new Point(400, 180);
//    }
//
//    protected Control createDialogArea(Composite parent) {
//      if (initialName == null) {
//        getShell().setText(HTMLPlugin.getResourceString("Dialog.AddPaletteItem"));
//      }
//      else {
//        getShell().setText(HTMLPlugin.getResourceString("Dialog.EditPaletteItem"));
//      }
//
//      Composite container = new Composite(parent, SWT.NULL);
//      container.setLayout(new GridLayout(2, false));
//      container.setLayoutData(new GridData(GridData.FILL_BOTH));
//
//      Label label = new Label(container, SWT.NULL);
//      label.setText(HTMLPlugin.getResourceString("Label.ItemName"));
//
//      itemName = new Text(container, SWT.BORDER);
//      itemName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//      if (initialName != null) {
//        itemName.setText(initialName);
//      }
//
//      label = new Label(container, SWT.NULL);
//      label.setText(HTMLPlugin.getResourceString("Label.InsertText"));
//
//      insertText = new Text(container, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
//      insertText.setLayoutData(new GridData(GridData.FILL_BOTH));
//      if (initialText != null) {
//        insertText.setText(initialText);
//      }
//
//      return container;
//    }
//
//    protected void okPressed() {
//      if (itemName.getText().equals("")) {
//        HTMLPlugin.openAlertDialog(HTMLPlugin.createMessage(HTMLPlugin.getResourceString("Error.Required"), new String[] { HTMLPlugin.getResourceString("Message.ItemName") }));
//        return;
//      }
//      if (insertText.getText().equals("")) {
//        HTMLPlugin.openAlertDialog(HTMLPlugin.createMessage(HTMLPlugin.getResourceString("Error.Required"), new String[] { HTMLPlugin.getResourceString("Message.InsertText") }));
//        return;
//      }
//      inputedName = itemName.getText();
//      inputedText = insertText.getText();
//      super.okPressed();
//    }
//
//    public String getItemName() {
//      return inputedName;
//    }
//
//    public String getInsertText() {
//      return inputedText;
//    }
//  }
//
}