import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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
   private JLabel labelSource;
	private JLabel labelMethod;
	private JTextField textFieldModel;
	private JLabel labelUmlModel;
	private JTextField textFieldModelUml;
	private JTextArea textArea;
   
   
   
  public Menu() {
	this.path = "";
	
	
	Panel psc = new Panel();
	labelSource = new JLabel();
	labelSource.setText("Source Code:");
	//add(labelClass);
    
    textFieldPath = new JTextField(40);
	textFieldPath.setEnabled(false);
	//add(textFieldPath);
	psc.add(labelSource);
	psc.add(textFieldPath);
	
	go = new JButton("...");
	go.addActionListener(this);
	psc.add(go);
	
	Panel pep = new Panel();
	pep.setLayout(new GridLayout(5, 1));
	
	JPanel prow = new JPanel(new GridLayout(1,2));
	labelClass = new JLabel();
	labelClass.setText("Entry Class:");
	prow.add(labelClass);
	
	textFieldClass = new JTextField(15);
	textFieldClass.setText("Main");
	prow.add(textFieldClass);
	pep.add(prow);
	
	prow = new JPanel(new GridLayout(1,2));
	labelClass = new JLabel();
	labelClass.setText("Entry Method:");
	prow.add(labelClass);
	
	textFieldMethod = new JTextField(20);
	textFieldMethod.setText("main");
	prow.add(textFieldMethod);
	pep.add(prow);
	
	prow = new JPanel(new GridLayout(1,2));
	labelMethod = new JLabel();
	labelMethod.setText("Model Name:");
	prow.add(labelMethod);
	
	textFieldModel = new JTextField(20);
	textFieldModel.setText("ActivityModel.xmi");
	prow.add(textFieldModel);
	pep.add(prow);
	
	prow = new JPanel(new GridLayout(1,2));
	labelUmlModel = new JLabel();
	labelUmlModel.setText("Uml Model Name:");
	prow.add(labelUmlModel);
	
	textFieldModelUml = new JTextField(15);
	textFieldModelUml.setText("ActivityModel.uml");
	prow.add(textFieldModelUml);
	pep.add(prow);
    
	prow = new JPanel(new GridLayout(1,1));
    btnAnalyse = new JButton("Analyser");
    btnAnalyse.addActionListener(new AnalyseButActionListener());
    btnAnalyse.setEnabled(false);
    prow.add(btnAnalyse);
    pep.add(prow);
    
    prow = new JPanel(new GridLayout(1,1));
    textArea = new JTextArea(
	    ""
	);
    textArea.setEditable(false);
	//textArea.setFont(new Font("Serif", Font.ITALIC, 16));
	textArea.setLineWrap(true);
	textArea.setWrapStyleWord(true);
	textArea.setSize(new Dimension(400, 250));
	JScrollPane scrollPane = new JScrollPane( textArea );
	
	//pep.add(textArea);
	
	
	setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
	add("North", psc);
	//add("West", new Button(" bouton gauche "));
	//add("East", new Button(" bouton droite "));
	add("Center", pep);
	add("South", scrollPane);
	//pep.pack();
	
	//this.setLayout(layout);
  }
  
  public class AnalyseButActionListener implements ActionListener
  {
	  public void actionPerformed(ActionEvent e)
      {
		try {
			Main.setEntryClass(textFieldClass.getText());
			Main.setEntryMethod(textFieldMethod.getText());
			Main.setFileModelResultEmf(textFieldModel.getText());
			Main.setFileModelResultPapyrus(textFieldModelUml.getText());
			String messageResult = Main.initDiagram();
			startDiagramAnalysis(path);
			textArea.setText(Main.getResponseInstructions());
			JOptionPane.showMessageDialog(null, messageResult);
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
	  System.out.println("Entry Point: (" + textFieldClass.getText() + "." + textFieldMethod.getText() + ")");
 }
   
  public Dimension getPreferredSize(){
    return new Dimension(700, 300);
  }
    
}
