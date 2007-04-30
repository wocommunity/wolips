package jp.aonir.fuzzyxml.sample;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

import jp.aonir.fuzzyxml.FuzzyXMLAttribute;
import jp.aonir.fuzzyxml.FuzzyXMLDocType;
import jp.aonir.fuzzyxml.FuzzyXMLDocument;
import jp.aonir.fuzzyxml.FuzzyXMLElement;
import jp.aonir.fuzzyxml.FuzzyXMLNode;
import jp.aonir.fuzzyxml.FuzzyXMLParser;
import jp.aonir.fuzzyxml.FuzzyXMLProcessingInstruction;
import jp.aonir.fuzzyxml.XPath;
import jp.aonir.fuzzyxml.event.FuzzyXMLErrorEvent;
import jp.aonir.fuzzyxml.event.FuzzyXMLErrorListener;
import jp.aonir.fuzzyxml.event.FuzzyXMLModifyEvent;
import jp.aonir.fuzzyxml.event.FuzzyXMLModifyListener;
import jp.aonir.fuzzyxml.internal.FuzzyXMLUtil;

/**
 * FuzzyXMLのサンプルアプリケーション。
 */
public class Console extends JFrame {
	
	private static final long serialVersionUID = -5578475015251437859L;
	
	private CustomTextArea text = new CustomTextArea();
	private DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
	private DefaultTreeModel model = new DefaultTreeModel(root);
	private JTree tree = new JTree(model);
	private JPopupMenu popup;
	private FuzzyXMLDocument doc;
	private JFileChooser chooser = new JFileChooser();
	private JList errors = new JList(new DefaultListModel());
	
	private Action addNodeAction = new AddNodeAction();
	private Action insertBeforeAction = new InsertBeforeAction();
	private Action replaceNodeAction = new ReplaceNodeAction();
	private Action addAttrAction = new AddAttrAction();
	private Action editAttrAction = new EditAttrAction();
	private Action editPIAction = new EditPIAction();
	private Action removeAction = new RemoveAction();
	private Action outputXMLAction = new OutputXMLAction();
	
	public Console() throws Exception {
		super("FuzzyXML Sample");
		
		popup = new JPopupMenu();
		popup.add(addNodeAction);
		popup.add(insertBeforeAction);
		popup.add(replaceNodeAction);
		popup.add(new JSeparator());
		popup.add(addAttrAction);
		popup.add(editAttrAction);
		popup.add(new JSeparator());
		popup.add(editPIAction);
		popup.add(new JSeparator());
		popup.add(removeAction);
		popup.add(new JSeparator());
		popup.add(outputXMLAction);
		
		Container cp = getContentPane();
		
		JSplitPane split2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		split2.add(new JScrollPane(text));
		split2.add(new JScrollPane(errors));
		split2.setDividerLocation(250);
		
		errors.addListSelectionListener(new ListSelectionListener(){
			public void valueChanged(ListSelectionEvent e){
				ErrorInfo info = (ErrorInfo)errors.getSelectedValue();
				if(info!=null){
					text.setSelectionStart(info.offset);
					text.setSelectionEnd(info.offset+info.length);
					text.requestFocus();
				}
			}
		});
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.add(new JScrollPane(tree));
		split.add(split2);
		split.setDividerLocation(180);
		
		JToolBar toolbar = new JToolBar();
		JButton button1 = new JButton("Open");
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		button1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				if(chooser.showOpenDialog(Console.this)==JFileChooser.APPROVE_OPTION){
					File file = chooser.getSelectedFile();
					try {
						text.setText(readFile(file));
						FuzzyXMLParser parser = new FuzzyXMLParser();
						parser.addErrorListener(new SampleErrorListener());
						FuzzyXMLDocument doc = parser.parse(file);
						text.setCaretPosition(0);
						setTree(doc);
					} catch(Exception ex){
						
					}
				}
			}
		});
		toolbar.add(button1);
		
		JButton button2 = new JButton("Parse");
		button2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				FuzzyXMLParser parser = new FuzzyXMLParser();
				parser.addErrorListener(new SampleErrorListener());
				FuzzyXMLDocument doc = parser.parse(text.getText());
				setTree(doc);
			}
		});
		toolbar.add(button2);
		toolbar.addSeparator();
		
		toolbar.add(new JLabel("XPath:"));
		final JTextField xpath = new JTextField();
		toolbar.add(xpath);
		
		JButton button3 = new JButton("Select");
		button3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				try {
					FuzzyXMLNode node = XPath.selectSingleNode(doc.getDocumentElement(),xpath.getText());
					text.setSelectionStart(node.getOffset());
					text.setSelectionEnd(node.getOffset() + node.getLength());
					text.requestFocus();
				} catch(Exception ex){
					JOptionPane.showMessageDialog(Console.this,ex,"Error",JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		toolbar.add(button3);
		
		JButton button4 = new JButton("Value");
		button4.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent evt){
				try {
					Object obj = XPath.getValue(doc.getDocumentElement(),xpath.getText());
					JOptionPane.showMessageDialog(Console.this,obj,"XPath",JOptionPane.INFORMATION_MESSAGE);
				} catch(Exception ex){
					JOptionPane.showMessageDialog(Console.this,ex,"Error",JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		toolbar.add(button4);
		
		cp.add(toolbar,BorderLayout.NORTH);
		cp.add(split,BorderLayout.CENTER);
		
		tree.setRootVisible(false);
		tree.setShowsRootHandles(true);
		tree.addTreeSelectionListener(new TreeSelectionListener(){
			public void valueChanged(TreeSelectionEvent e){
				try {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.getNewLeadSelectionPath().getLastPathComponent();
					FuzzyXMLNode obj = (FuzzyXMLNode)node.getUserObject();
					text.fire = false;
					text.setSelectionStart(obj.getOffset());
					text.setSelectionEnd(obj.getOffset()+obj.getLength());
					text.requestFocus();
				} catch(Exception ex){
				} finally {
					text.fire = true;
				}
			}
		});
		tree.addMouseListener(new MouseAdapter(){
			public void mousePressed  (MouseEvent e) { showPopup(e); }
			public void mouseReleased (MouseEvent e) { showPopup(e); }
			public void mouseClicked  (MouseEvent e) { showPopup(e); }
		});
		tree.setSelectionModel(new CustomTreeSelectionModel());
		
		text.setText(readFile("sample.xml"));
		text.setCaretPosition(0);
		text.addCaretListener(new CaretListener(){
			public void caretUpdate(CaretEvent e){
				if(doc!=null){
					FuzzyXMLElement element = doc.getElementByOffset(text.getCaretPosition());
					if(getSelectedNode()!=element){
						TreePath path = getPath(element);
						if(path!=null){
							try {
								((CustomTreeSelectionModel)tree.getSelectionModel()).fire = false;
								tree.expandPath(path);
								tree.setSelectionPath(path);
								tree.scrollPathToVisible(path);
								tree.repaint();
							} finally {
								((CustomTreeSelectionModel)tree.getSelectionModel()).fire = true;
							}
						}
					}
				}
			}
		});
		
		FuzzyXMLParser parser = new FuzzyXMLParser();
		parser.addErrorListener(new SampleErrorListener());
		FuzzyXMLDocument doc = parser.parse(text.getText());
		setTree(doc);
	}
	
	private class CustomTextArea extends JTextArea {
		
		private static final long serialVersionUID = -4879303648059518334L;
		private boolean fire = true;
		
		protected void fireCaretUpdate(CaretEvent arg0) {
			if(fire){
				super.fireCaretUpdate(arg0);
			}
		}
	}
	
	private class CustomTreeSelectionModel extends DefaultTreeSelectionModel {
		
		private static final long serialVersionUID = -4214852190250993601L;
		private boolean fire = true;
		
		protected void fireValueChanged(TreeSelectionEvent arg0) {
			if(fire){
				super.fireValueChanged(arg0);
			}
		}
	}
	
	private TreePath getPath(FuzzyXMLNode node){
		if(doc==null){
			return null;
		}
		ArrayList list = new ArrayList();
		list.add(root);
		if(searchNode(list,root,node)){
			return new TreePath(list.toArray());
		}
		return null;
	}
	
	private boolean searchNode(ArrayList list,DefaultMutableTreeNode treeNode,FuzzyXMLNode node){
		for(int i=0;i<treeNode.getChildCount();i++){
			DefaultMutableTreeNode child = (DefaultMutableTreeNode)treeNode.getChildAt(i);
			list.add(child);
			if(child.getUserObject()==node){
				return true;
			} else {
				if(searchNode(list,child,node)){
					return true;
				}
			}
			list.remove(child);
		}
		return false;
	}
	
	
	
	/** ツリーのポップアップメニューを表示 */
	private void showPopup(MouseEvent e){
		updateActions();
		Component com = (Component)e.getSource();
		if (e.isPopupTrigger()) popup.show(com , e.getX() , e.getY());
	}
	
	/** ポップアップメニューのアクションの状態を更新 */
	private void updateActions(){
		FuzzyXMLNode node = getSelectedNode();
		addNodeAction.setEnabled(false);
		insertBeforeAction.setEnabled(false);
		addAttrAction.setEnabled(false);
		editAttrAction.setEnabled(false);
		editPIAction.setEnabled(false);
		removeAction.setEnabled(false);
		outputXMLAction.setEnabled(false);
		
		if(node==null){
			return;
		} else {
			outputXMLAction.setEnabled(true);
		}
		
		if(node.getParentNode()!=null){
			removeAction.setEnabled(true);
		}
		if(node instanceof FuzzyXMLElement){
			addNodeAction.setEnabled(true);
			addAttrAction.setEnabled(true);
		}
		
		if(node instanceof FuzzyXMLAttribute){
			editAttrAction.setEnabled(true);
		} else if(node.getParentNode()!=null){
			insertBeforeAction.setEnabled(true);
		}
		
		if(node instanceof FuzzyXMLProcessingInstruction){
			editPIAction.setEnabled(true);
		}
	}
	
	/** ノードを追加するアクション */
	private class AddNodeAction extends AbstractAction {
		
		private static final long serialVersionUID = 2982302839691482637L;
		
		public AddNodeAction(){
			super("Add node");
		}
		
		public void actionPerformed(ActionEvent e) {
			FuzzyXMLNode node = getSelectedNode();
			if(node!=null && node instanceof FuzzyXMLElement){
				ConsoleNodeDialog dialog = new ConsoleNodeDialog(Console.this,"Add node");
				if(dialog.openDialog()){
					DefaultMutableTreeNode treeNode = getSelectionTreeNode();
					String type  = dialog.getNodeType();
					String value = dialog.getValue();
					FuzzyXMLNode newNode = null;
					if(type.equals(ConsoleNodeDialog.ELEMENT)){
						newNode = doc.createElement(value);
					} else if(type.equals(ConsoleNodeDialog.COMMENT)){
						newNode = doc.createComment(value);
					} else if(type.equals(ConsoleNodeDialog.CDATA)){
						newNode = doc.createCDATASection(value);
					} else if(type.equals(ConsoleNodeDialog.TEXT)){
						newNode = doc.createText(value);
					}
					((FuzzyXMLElement)node).appendChild(newNode);
					DefaultMutableTreeNode newTreeNode = new DefaultMutableTreeNode(newNode);
					treeNode.add(newTreeNode);
					reload(newTreeNode);
				}
			}
		}
	}
	
	/** ノードを挿入するアクション */
	private class InsertBeforeAction extends AbstractAction {
		
		private static final long serialVersionUID = 2557038735660220302L;
		
		public InsertBeforeAction(){
			super("Insert node before");
		}
		
		public void actionPerformed(ActionEvent e) {
			FuzzyXMLNode node = getSelectedNode();
			if(node!=null){
				ConsoleNodeDialog dialog = new ConsoleNodeDialog(Console.this,"Add node");
				if(dialog.openDialog()){
					DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)getSelectionTreeNode().getParent();
					String type  = dialog.getNodeType();
					String value = dialog.getValue();
					int index = model.getIndexOfChild(getSelectionTreeNode().getParent(),getSelectionTreeNode());
					FuzzyXMLNode newNode = null;
					if(type.equals(ConsoleNodeDialog.ELEMENT)){
						newNode = doc.createElement(value);
					} else if(type.equals(ConsoleNodeDialog.COMMENT)){
						newNode = doc.createComment(value);
					} else if(type.equals(ConsoleNodeDialog.CDATA)){
						newNode = doc.createCDATASection(value);
					} else if(type.equals(ConsoleNodeDialog.TEXT)){
						newNode = doc.createText(value);
					}
					((FuzzyXMLElement)node.getParentNode()).insertBefore(newNode,node);
					DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(newNode);
					parentNode.insert(treeNode,index);
					reload(treeNode);
				}
			}
		}
	}
	
	/** ノードを置換するアクション */
	private class ReplaceNodeAction extends AbstractAction {

		private static final long serialVersionUID = -7017800335700643448L;
		
		public ReplaceNodeAction(){
			super("Replace node");
		}
		
		public void actionPerformed(ActionEvent evt){
			FuzzyXMLNode node = getSelectedNode();
			if(node!=null){
				ConsoleNodeDialog dialog = new ConsoleNodeDialog(Console.this,"Replace node");
				if(dialog.openDialog()){
					DefaultMutableTreeNode parent = (DefaultMutableTreeNode)getSelectionTreeNode().getParent();
					String type  = dialog.getNodeType();
					String value = dialog.getValue();
					int index = model.getIndexOfChild(getSelectionTreeNode().getParent(),getSelectionTreeNode());
					FuzzyXMLNode newNode = null;
					if(type.equals(ConsoleNodeDialog.ELEMENT)){
						newNode = doc.createElement(value);
					} else if(type.equals(ConsoleNodeDialog.COMMENT)){
						newNode = doc.createComment(value);
					} else if(type.equals(ConsoleNodeDialog.CDATA)){
						newNode = doc.createCDATASection(value);
					} else if(type.equals(ConsoleNodeDialog.TEXT)){
						newNode = doc.createText(value);
					}
					((FuzzyXMLElement)node.getParentNode()).replaceChild(newNode,node);
					DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(newNode);
					parent.remove(index);
					parent.insert(treeNode,index);
					reload(treeNode);
				}
			}
		}
	}
	
	/** 属性を追加するアクション */
	private class AddAttrAction extends AbstractAction {

		private static final long serialVersionUID = -478895766745943970L;
		
		public AddAttrAction(){
			super("Add attribute");
		}
		
		public void actionPerformed(ActionEvent evt){
			FuzzyXMLNode node = getSelectedNode();
			if(node!=null && node instanceof FuzzyXMLElement){
				ConsoleAttrDialog dialog = new ConsoleAttrDialog(Console.this,"Add attribute");
				if(dialog.openDialog()){
					DefaultMutableTreeNode treeNode = getSelectionTreeNode();
					String name  = dialog.getName();
					String value = dialog.getValue();
					FuzzyXMLAttribute attr = doc.createAttribute(name);
					attr.setValue(value);
					((FuzzyXMLElement)node).setAttribute(attr);
					
					int count = treeNode.getChildCount();
					int index = 0;
					for(int i=0;i<count;i++){
						DefaultMutableTreeNode child = (DefaultMutableTreeNode)treeNode.getChildAt(i);
						Object obj = child.getUserObject();
						if(!(obj instanceof FuzzyXMLAttribute)){
							index = i;
							break;
						}
					}
					DefaultMutableTreeNode newTreeNode = new DefaultMutableTreeNode(attr);
					treeNode.insert(newTreeNode,index);
					reload(newTreeNode);
				}
			}
		}
	}
	
	/** 属性を編集するアクション */
	private class EditAttrAction extends AbstractAction {

		private static final long serialVersionUID = 311973714010809215L;
		
		public EditAttrAction(){
			super("Edit attribute");
		}
		
		public void actionPerformed(ActionEvent evt){
			DefaultMutableTreeNode treeNode = getSelectionTreeNode();
			FuzzyXMLNode node = getSelectedNode();
			if(node!=null && node instanceof FuzzyXMLAttribute){
				FuzzyXMLAttribute attr = (FuzzyXMLAttribute)node;
				ConsoleAttrDialog dialog = new ConsoleAttrDialog(Console.this,"Edit attribute");
				dialog.setName(attr.getName());
				dialog.setValue(attr.getValue());
				if(dialog.openDialog()){
					String value = dialog.getValue();
					attr.setValue(value);
					reload(treeNode);
				}
			}
			
		}
	}
	
	private class EditPIAction extends AbstractAction {

		private static final long serialVersionUID = -4541154288446244885L;
		
		public EditPIAction(){
			super("Edit processing instruction");
		}
		
		public void actionPerformed(ActionEvent evt){
			DefaultMutableTreeNode treeNode = getSelectionTreeNode();
			FuzzyXMLNode node = getSelectedNode();
			if(node!=null && node instanceof FuzzyXMLProcessingInstruction){
				FuzzyXMLProcessingInstruction pi = (FuzzyXMLProcessingInstruction)node;
				ConsolePIDialog dialog = new ConsolePIDialog(Console.this,"Edit processing instriction");
				//dialog.setName(pi.getName());
				dialog.setData(pi.getData());
				if(dialog.openDialog()){
					String data = dialog.getData();
					pi.setData(data);
					reload(treeNode);
				}
			}
			
		}
	}
	
	/** ノードを削除するアクション */
	private class RemoveAction extends AbstractAction {

		private static final long serialVersionUID = 4764475196359373921L;
		
		public RemoveAction(){
			super("Remove");
		}
		
		public void actionPerformed(ActionEvent e) {
			FuzzyXMLNode node = getSelectedNode();
			if(node!=null && doc!=null){
				DefaultMutableTreeNode treeNode = getSelectionTreeNode();
				DefaultMutableTreeNode parent = (DefaultMutableTreeNode)treeNode.getParent();
				treeNode.removeFromParent();
				((FuzzyXMLElement)node.getParentNode()).removeChild(node);
				if(parent.getChildCount()==0){
					reload(parent);
				} else {
					reload((DefaultMutableTreeNode)parent.getChildAt(0));
				}
			}
		}
	}
	
	/** ノードをXMLとして表示するアクション */
	private class OutputXMLAction extends AbstractAction {

		private static final long serialVersionUID = 2674403704256243931L;
		
		public OutputXMLAction(){
			super("Output as XML");
		}
		
		public void actionPerformed(ActionEvent e) {
			FuzzyXMLNode node = getSelectedNode();
			JOptionPane.showMessageDialog(Console.this, node.toXMLString());
		}
	}
	
	/** ツリーをリロード */
	private void reload(DefaultMutableTreeNode node){
		model.reload();
		if(node!=null){
			tree.scrollPathToVisible(new TreePath(node.getPath()));
		} else if(root.getChildCount() > 0){
			tree.expandPath(new TreePath(new Object[]{root,root.getChildAt(0)}));
		}
	}
	
	/** ツリーで選択されているDefaultMutableTreeNodeを取得 */
	private DefaultMutableTreeNode getSelectionTreeNode(){
		return (DefaultMutableTreeNode)tree.getSelectionPath().getLastPathComponent();
	}
	
	/** ツリーで選択されているFuzzyXMLNodeを取得 */
	private FuzzyXMLNode getSelectedNode(){
		TreePath path = tree.getSelectionPath();
		if(path==null){
			return null;
		}
		DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
		if(node==null){
			return null;
		}
		if(node.getUserObject() instanceof FuzzyXMLNode){
			return (FuzzyXMLNode)node.getUserObject();
		} else {
			return null;
		}
	}
	
	/** ツリーにFuzzyXMLDocumentをセット */
	private void setTree(FuzzyXMLDocument doc){
		root.removeAllChildren();
		this.doc = doc;
		if(doc!=null){
			FuzzyXMLDocType docType = doc.getDocumentType();
			if(docType!=null){
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(docType);
				root.add(node);
			}
			
			doc.addModifyListener(new SampleListener());
			setElement(root,doc.getDocumentElement());
		}
		reload(null);
	}
	
	private void setElement(DefaultMutableTreeNode parent,FuzzyXMLElement element){
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(element);
		parent.add(node);
		
		FuzzyXMLAttribute[] attrs = element.getAttributes();
		for(int i=0;i<attrs.length;i++){
			node.add(new DefaultMutableTreeNode(attrs[i]));
		}
		
		FuzzyXMLNode[] children = element.getChildren();
		for(int i=0;i<children.length;i++){
			if(children[i] instanceof FuzzyXMLElement){
				setElement(node,(FuzzyXMLElement)children[i]);
			} else {
				node.add(new DefaultMutableTreeNode(children[i]));
			}
		}
	}
	
	/** ファイルから読み込み */
	private String readFile(File file) throws Exception {
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			byte[] buf = FuzzyXMLUtil.readStream(in);
			String encode = FuzzyXMLUtil.getEncoding(buf);
			if(encode==null){
				return new String(buf);
			} else {
				return new String(buf,encode);
			}
		} finally {
			if(in!=null){
				in.close();
			}
		}
	}
	
	/** クラスパスから読み込み */
	private String readFile(String fileName) throws Exception {
		URL url = Console.class.getResource(fileName);
		InputStream in = null;
		try {
			in = url.openStream();
			byte[] buf = FuzzyXMLUtil.readStream(in);
			String encode = FuzzyXMLUtil.getEncoding(buf);
			if(encode==null){
				return new String(buf);
			} else {
				return new String(buf,encode);
			}
		} finally {
			if(in!=null){
				in.close();
			}
		}
	}
	
	/** ツリーの変更をJTextPaneに反映するためのリスナ */
	private class SampleListener implements FuzzyXMLModifyListener {
		public void modified(FuzzyXMLModifyEvent evt) {
			text.setSelectionStart(evt.getOffset());
			text.setSelectionEnd(evt.getOffset()+evt.getLength());
			text.replaceSelection(evt.getNewText());
		}
	}
	
	public static void main(String[] args) throws Exception {
		Console sample = new Console();
		sample.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		sample.setSize(640,450);
		sample.setVisible(true);
	}
	
	/** パースエラーを処理するリスナ */
	private class SampleErrorListener implements FuzzyXMLErrorListener {
		public SampleErrorListener(){
			((DefaultListModel)errors.getModel()).removeAllElements();
		}
		public void error(FuzzyXMLErrorEvent event) {
			((DefaultListModel)errors.getModel()).addElement(
					new ErrorInfo(event.getMessage(),event.getOffset(),event.getLength()));
		}
	}
	
	/** エラー情報を格納するクラス */
	private class ErrorInfo {
		private String message;
		private int offset;
		private int length;
		public ErrorInfo(String message,int offset,int length){
			this.message = message;
			this.offset  = offset;
			this.length  = length;
		}
		public String toString(){
			return "[offset]" + offset + " [message]" + message;
		}
	}
}
