package jp.aonir.fuzzyxml.sample;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

/**
 * ノードを追加・挿入するためのダイアログ。
 */
public class ConsoleNodeDialog extends JDialog {
    
	private static final long serialVersionUID = 5459353262333735855L;
	
	public static final String ELEMENT = "Element";
    public static final String COMMENT = "Comment";
    public static final String CDATA   = "CDATA";
    public static final String TEXT    = "Text";
    
    private JComboBox combo = new JComboBox();
    private JTextField text = new JTextField();
    private boolean result = false;
    
    public ConsoleNodeDialog(Frame owner, String title) throws HeadlessException {
        super(owner, title, true);
        
        JPanel panel = new JPanel();
        panel.setLayout(new SpringLayout());
        panel.add(new JLabel("Node type:"));
        panel.add(combo);
        combo.addItem(ELEMENT);
        combo.addItem(COMMENT);
        combo.addItem(CDATA);
        combo.addItem(TEXT);
        panel.add(new JLabel("Value:"));
        panel.add(text);
        SpringUtilities.makeCompactGrid(panel,2,2,6,6,6,6);
        
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(panel,BorderLayout.NORTH);
        
        JPanel buttons = new JPanel();
        buttons.setLayout(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = new JButton("OK");
        ok.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                result = true;
                dispose();
            }
        });
        buttons.add(ok);
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent evt){
                result = false;
                dispose();
            }
        });
        buttons.add(cancel);
        
        cp.add(buttons,BorderLayout.SOUTH);
        pack();
        setSize(300,getSize().height);
    }
    
    public boolean openDialog(){
        setLocationRelativeTo(getOwner());
        setVisible(true);
        return result;
    }
    
    public String getNodeType(){
        return (String)combo.getSelectedItem();
    }
    
    public String getValue(){
        return text.getText();
    }

}
