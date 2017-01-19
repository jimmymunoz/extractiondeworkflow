package Interface;

import java.awt.BorderLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
public class Menu extends JPanel implements ActionListener {
  static private final String newline = "\n";

  JButton openButton, saveButton;

  JTextArea log;

  JFileChooser fc;

  public Menu() {
    super(new BorderLayout());

    //Create the log first, because the action listeners
    //need to refer to it.
    log = new JTextArea(5, 20);
    log.setMargin(new Insets(5, 5, 5, 5));
    log.setEditable(false);
    JScrollPane logScrollPane = new JScrollPane(log);

    //Create a file chooser
    fc = new JFileChooser();

    
    openButton = new JButton("Choisisez un fichier  ");
    openButton.addActionListener(this);

    //Create the save button. We use the image from the JLF
    //Graphics Repository (but we extracted it from the jar).
    saveButton = new JButton("generer le Workflow...");
    saveButton.addActionListener(this);

    //For layout purposes, put the buttons in a separate panel
    JPanel buttonPanel = new JPanel(); //use FlowLayout
    buttonPanel.add(openButton);
    buttonPanel.add(saveButton);

    //Add the buttons and the log to this panel.
    add(buttonPanel, BorderLayout.PAGE_START);
    add(logScrollPane, BorderLayout.CENTER);
  }

  public void actionPerformed(ActionEvent e) {

    //Handle open button action.
    if (e.getSource() == openButton) {
		  int returnVal = fc.showOpenDialog(Menu.this);		
		  if (returnVal == JFileChooser.DIRECTORIES_ONLY) {
		
			  
		    File file = fc.getSelectedFile();
		   // String path = file.getPath();
		    //This is where a real application would open the file.
		log.append("Opening: " + file.getName() + "." + newline);
		  } else {
		    log.append("Open command cancelled by user." + newline);
		  }
		  log.setCaretPosition(log.getDocument().getLength());
		  
      //Handle save button action.
    } else if (e.getSource() == saveButton) {
      int returnVal = fc.showSaveDialog(Menu.this);
      if (returnVal == JFileChooser.APPROVE_OPTION) {
        File file = fc.getSelectedFile();
        //This is where a real application would save the file.
        log.append("Saving: " + file.getName() + "." + newline);
      } else {
        log.append("Save command cancelled by user." + newline);
      }
      log.setCaretPosition(log.getDocument().getLength());
    }
  }

  /** Returns an ImageIcon, or null if the path was invalid. */
  protected static ImageIcon createImageIcon(String path) {
    java.net.URL imgURL = Menu.class.getResource(path);
    if (imgURL != null) {
      return new ImageIcon(imgURL);
    } else {
      System.err.println("Couldn't find file: " + path);
      return null;
    }
  }


  public static void createAndShowGUI() {
    //
    JFrame.setDefaultLookAndFeelDecorated(true);
    JDialog.setDefaultLookAndFeelDecorated(true);

    //Create and set up the window.
    JFrame frame = new JFrame("Generate Workflow");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    //Create and set up the content pane.
    JComponent newContentPane = new Menu();
    newContentPane.setOpaque(true); //content panes must be opaque
    frame.setContentPane(newContentPane);

    //Display the window.
    frame.pack();
    frame.setVisible(true);
  }

}