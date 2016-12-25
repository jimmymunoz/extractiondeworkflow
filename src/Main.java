import java.io.IOException;

import activitydiagram.ActivityDiagram;
import activitydiagram.ActivityDiagramParser;
import diagramgenerator.JwtActivityDiagram;

public class Main {

	public static final String projectPath = "/Users/jimmymunoz/Documents/workspace-neon/visitor";
	public static final String projectSourcePath = projectPath + "/src";
	public static final String jrePath = "/Java/JavaVirtualMachines/jdk1.8.0_60.jdk/Contents/Home/jre/lib/rt.jar";
	public static final String entryClass = "Principale";
	public static final String entryMethod = "main";

	
	public static void main(String[] args) throws IOException
	{
		ActivityDiagramParser adParser = new ActivityDiagramParser(projectPath, projectSourcePath, jrePath, entryClass, entryMethod);
		ActivityDiagram activityDiagram = adParser.parseActivityDiagram();
		JwtActivityDiagram diagramParser = new JwtActivityDiagram();
		diagramParser.setActivityDiagram(activityDiagram);
		diagramParser.proccesActivityDiagram();
		//activityDiagram.testClassDiagram();
	}

}
