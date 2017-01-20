

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/*
 * SwingFileChooserDemo.java is a 1.4 application that uses these files:
 * images/Open16.gif images/Save16.gif
 */

public class Menu extends JPanel implements ActionListener {
   
   private JButton go;
   private JButton btnAnalyse;
   private String path ; 
   private JFileChooser chooser;
   private JTextField textFieldPath;
   private String choosertitle;
   private JLabel labelClass;
   private JTextField textFieldClass;
   private JTextField textFieldMethod;
   
  public Menu() {
	this.path = "";
	
    go = new JButton("Please choose the directory with the code source.");
    go.addActionListener(this);
    add(go);
    
    textFieldPath = new JTextField(50);
	textFieldPath.setEnabled(false);
	add(textFieldPath);
	
	labelClass = new JLabel();
	labelClass.setText("Entry Class:");
	add(labelClass);
	
	textFieldClass = new JTextField(20);
	textFieldClass.setText("Main");
	add(textFieldClass);
	
	labelClass = new JLabel();
	labelClass.setText("Entry Method:");
	add(labelClass);
	
	textFieldMethod = new JTextField(20);
	textFieldMethod.setText("main");
	add(textFieldMethod);
    
    btnAnalyse = new JButton("Analyser");
    btnAnalyse.addActionListener(new AnalyseButActionListener());
    add(btnAnalyse);
    btnAnalyse.setEnabled(false);
  }
  
  public class AnalyseButActionListener implements ActionListener
  {
	  public void actionPerformed(ActionEvent e)
      {
		try {
			Main.initDiagram();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
      }
  }
  
  public String getPath() {
	return path;
  }
  
  public void setPaht(String path)
  {
	  this.path = path;
  }
  
  public void actionPerformed(ActionEvent e) {
    String path ="";        
    chooser = new JFileChooser(); 
    chooser.setCurrentDirectory(new java.io.File("."));
    chooser.setDialogTitle(choosertitle);
    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    //
    // disable the "All files" option.
    //
    chooser.setAcceptAllFileFilterUsed(false);
    //    
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
      System.out.println("getCurrentDirectory(): " 
         +  chooser.getCurrentDirectory());
      
      System.out.println("getSelectedFile() : " 
         +  chooser.getSelectedFile());
      path = chooser.getSelectedFile().toString();
      setPaht(path);
      startDiagramAnalysis(path);
    }
    else {
      System.out.println("No Selection ");
    }
    
  }

 private void startDiagramAnalysis(String path) {
	  Main.setProjectPath(path);
	  Main.setProjectSourcePath(path);
	  btnAnalyse.setEnabled(true);
	  textFieldPath.setText(path);
	  Main.setEntryClass(textFieldClass.getText());
	  Main.setEntryMethod(textFieldMethod.getText());
 }
   
  public Dimension getPreferredSize(){
    return new Dimension(700, 300);
  }
    
}
