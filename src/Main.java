import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import Interface.Menu;
import activitydiagram.ActivityDiagramAst;
import activitydiagram.ActivityDiagramModel;
import activitydiagram.ActivityDiagramParser;
import diagramgenerator.JwtActivityDiagram;
import diagramgenerator.PapyrusTransformation;

public class Main {

	//public static final String projectPath = "/Users/jimmymunoz/Documents/workspace-neon/visitor";
	public static final String projectPath = "/Users/jimmymunoz/Documents/workspace-neon/workFlowTest";
	public static final String projectSourcePath = projectPath + "/src";
	public static final String jrePath = "/Library/Java/JavaVirtualMachines/jdk1.8.0_91.jdk/Contents/Home/jre/lib/rt.jar"; // which java

	//public static final String projectPath = "C:\\Users\\IkbalH\\Desktop\\Composants_Reutilisation\\TP\\TestWorkflow";
	//public static final String projectSourcePath = projectPath + "/src";
	//public static final String jrePath = "C:\\Program Files\\Java\\jre1.8.0_51\\lib\\rt.jar"; // which java
	
	// http://stackoverflow.com/questions/4681090/how-do-i-find-where-jdk-is-installed-on-my-windows-machine
	// ls /System/Library/Frameworks/JavaVM.framework/Versions
	// /System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Commands/java
	//public static final String entryClass = "Principale";
	public static final String entryClass = "Main";
	public static final String entryMethod = "main";
	

	
	public static void main(String[] args) throws IOException
	{
		/*Menu m =new Menu();
		   javax.swing.SwingUtilities.invokeLater(new Runnable() {
			      public void run() {
			        Menu.createAndShowGUI();
			      }
			    });*/
			  
		ActivityDiagramParser adParser = new ActivityDiagramParser(projectPath, projectSourcePath, jrePath, entryClass, entryMethod);
		ActivityDiagramAst activityDiagram = adParser.parseActivityDiagram();
		ActivityDiagramModel activityDiagramModel = new ActivityDiagramModel(activityDiagram);
		//activityDiagramModel.getUmlmodel();
		/*
		JwtActivityDiagram diagramParser = new JwtActivityDiagram(activityDiagram, projectPath, "MyDiagram");
		*/
		try {
			PapyrusTransformation Ptrans = new PapyrusTransformation();
		} catch (XPathExpressionException | SAXException | ParserConfigurationException
				| TransformerFactoryConfigurationError | TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//diagramParser.proccesActivityDiagram();
		//activityDiagram.testClassDiagram();
		/**/
	}

}
