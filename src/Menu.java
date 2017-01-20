

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/*
 * SwingFileChooserDemo.java is a 1.4 application that uses these files:
 * images/Open16.gif images/Save16.gif
 */

public class Menu extends JPanel
   implements ActionListener {
   JButton go;
   String path ; 
   JFileChooser chooser;
   String choosertitle;
   
  public Menu() {
    go = new JButton("Séléctionner un répertoire d'un code source ");
    go.addActionListener(this);
    add(go);
    this.path = "";
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
      path = chooser.getSelectedFile().getPath();
      setPaht(path);
      }
    else {
      System.out.println("No Selection ");
      }
    
     }
  
   
  public Dimension getPreferredSize(){
    return new Dimension(400, 600);
    }
    
}
