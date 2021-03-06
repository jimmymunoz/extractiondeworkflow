import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import activitydiagram.ActivityDiagramAst;
import activitydiagram.ActivityDiagramModel;
import activitydiagram.ActivityDiagramParser;
import diagramgenerator.PapyrusTransformation;

//http://stackoverflow.com/questions/4681090/how-do-i-find-where-jdk-is-installed-on-my-windows-machine
public class Main {

	private static String projectPath = "";
	private static String projectSourcePath = projectPath + "/src";
	private static String jrePath = "C:\\Program Files\\Java\\jre1.8.0_51\\lib\\rt.jar"; // which java
	private static String entryClass = "Main";
	private static String entryMethod = "main";
	private static String fileModelResultEmf = "model/ActivityModelResult.xmi";
	private static String fileModelResultPapyrus = "model/ActivityModelResult.uml";
	private static Map<String, String> configurationList;
	private static String responseInstructions = "";
	
	public static void main(String[] args) throws IOException
	{
		configurationList = new HashMap<String, String>();
		configurationList.put("create_subactivities", "1");
		configurationList.put("show_flot_variables", "1");
		
		setEnviromenTestMac();
		//setEnviromenTestWindows();
		//initDiagram();
		//System.exit(0);
		
		JFrame frame = new JFrame("Workflow Extractor");
	    Menu panel = new Menu();
	    frame.addWindowListener(
	      new WindowAdapter() {
	        public void windowClosing(WindowEvent e) {
	        	System.exit(0);
	          }
	        }
	    );
	    frame.getContentPane().add(panel,"Center");
	    frame.setSize(panel.getPreferredSize());
	    frame.setVisible(true);
	    //System.out.println(path);
	}
	
	
	public static String getProjectPath() {
		return projectPath;
	}

	public static String getProjectSourcePath() {
		return projectSourcePath;
	}

	public static String getJrePath() {
		return jrePath;
	}

	public static String getEntryClass() {
		return entryClass;
	}

	public static String getEntryMethod() {
		return entryMethod;
	}

	public static String getFileModelResultEmf() {
		return fileModelResultEmf;
	}

	public static String getFileModelResultPapyrus() {
		return fileModelResultPapyrus;
	}

	public static void setProjectPath(String projectPath) {
		Main.projectPath = projectPath;
	}

	public static void setProjectSourcePath(String projectSourcePath) {
		Main.projectSourcePath = projectSourcePath;
	}

	public static void setJrePath(String jrePath) {
		Main.jrePath = jrePath;
	}

	public static void setEntryClass(String entryClass) {
		Main.entryClass = entryClass;
	}

	public static void setEntryMethod(String entryMethod) {
		Main.entryMethod = entryMethod;
	}

	public static void setFileModelResultEmf(String fileModelResultEmf) {
		Main.fileModelResultEmf = fileModelResultEmf;
	}

	public static void setFileModelResultPapyrus(String fileModelResultPapyrus) {
		Main.fileModelResultPapyrus = fileModelResultPapyrus;
	}

	

	public static String initDiagram() throws IOException {
		String messageResult = "";
		ActivityDiagramParser adParser = new ActivityDiagramParser(projectPath, projectSourcePath, jrePath, entryClass, entryMethod);
		ActivityDiagramAst activityDiagram = adParser.parseActivityDiagram();
		setResponseInstructions(activityDiagram.getResponseInstructions());
		if( activityDiagram.isEntryValidated() ){
			ActivityDiagramModel activityDiagramModel = new ActivityDiagramModel(activityDiagram, fileModelResultEmf, configurationList);
			setResponseInstructions(getResponseInstructions() + activityDiagramModel.getResultModel());
			//activityDiagramModel.getUmlmodel();
			PapyrusTransformation Ptrans = new PapyrusTransformation(activityDiagramModel.getFileModelPathSave(),fileModelResultPapyrus);
			setResponseInstructions(getResponseInstructions() + Ptrans.getResultTransformation());
			messageResult = Ptrans.getResultTransformation();
		}
		else{
			messageResult = activityDiagram.getResponseValidate();
		}
		
		//JwtActivityDiagram diagramParser = new JwtActivityDiagram(activityDiagram, projectPath, "MyDiagram");
		//diagramParser.proccesActivityDiagram();
		//activityDiagram.testClassDiagram();
		return messageResult;
	}
	
	public static void setEnviromenTestMac(){
		//projectPath = "/Users/jimmymunoz/Documents/workspace-neon/visitor";
		projectPath = "/Users/jimmymunoz/Documents/workspace-neon/workFlowTest";
		projectPath = "/Users/jimmymunoz/Documents/workspace-neon/ExtractionWorkFlow";
		fileModelResultEmf = "model/ExtractionWorkFlow.xmi";
		fileModelResultPapyrus = "model/ExtractionWorkFlow.uml";
		projectSourcePath = projectPath + "/src";
		jrePath = "/Library/Java/JavaVirtualMachines/jdk1.8.0_91.jdk/Contents/Home/jre/lib/rt.jar"; // which java
		//entryClass = "Principale";
		entryClass = "Main";
		entryMethod = "main";
	}
	
	public static void setEnviromenTestWindows(){
		 projectPath = "C:\\Users\\IkbalH\\Desktop\\Composants_Reutilisation\\TP\\TestWorkflow";
		projectSourcePath = projectPath + "/src";
		jrePath = "C:\\Program Files\\Java\\jre1.8.0_51\\lib\\rt.jar"; // which java
		
		entryClass = "Main";
		entryMethod = "main";
	}


	public static String getResponseInstructions() {
		return responseInstructions;
	}


	public static void setResponseInstructions(String responseInstructions) {
		Main.responseInstructions = responseInstructions;
	}

}
