package jp.aonir.fuzzyxml.sample;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

/**
 * 属性を追加・編集するためのダイアログ。
 */
public class ConsoleAttrDialog extends JDialog {
    
	private static final long serialVersionUID = 8687413644382817149L;
	
	private JTextField textName  = new JTextField();
    private JTextField textValue = new JTextField();
    private boolean result = false;
    
    public ConsoleAttrDialog(Frame owner, String title) throws HeadlessException {
        super(owner, title, true);
        
        Container cp = getContentPane();
        
        JPanel panel = new JPanel();
        panel.setLayout(new SpringLayout());
        panel.add(new JLabel("Name:"));
        panel.add(textName);
        panel.add(new JLabel("Value:"));
        panel.add(textValue);
        SpringUtilities.makeCompactGrid(panel,2,2,6,6,6,6);
        
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
        
        cp.add(panel,BorderLayout.NORTH);
        cp.add(buttons,BorderLayout.SOUTH);
        
        pack();
        setSize(300,getSize().height);
    }
    
    public boolean openDialog(){
        setLocationRelativeTo(getOwner());
        setVisible(true);
        return result;
    }
    
    public String getName(){
        return textName.getText();
    }
    
    public String getValue(){
        return textValue.getText();
    }
    
    public void setName(String name){
        this.textName.setText(name);
        this.textName.setEditable(false);
    }
    
    public void setValue(String value){
        this.textValue.setText(value);
    }
}
