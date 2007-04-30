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

public class ConsolePIDialog extends JDialog {
	
	private static final long serialVersionUID = 7823931569038383710L;
	
	private JTextField textData = new JTextField();
    private boolean result = false;

    public ConsolePIDialog(Frame owner, String title) throws HeadlessException {
        super(owner, title, true);
        
        Container cp = getContentPane();
        
        JPanel panel = new JPanel();
        panel.setLayout(new SpringLayout());
        panel.add(new JLabel("Data:"));
        panel.add(textData);
        SpringUtilities.makeCompactGrid(panel,1,2,6,6,6,6);
        
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
    
    public void setData(String data){
    	textData.setText(data);
    }
    
    public String getData(){
    	return textData.getText();
    }
    
}
