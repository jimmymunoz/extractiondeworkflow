import java.io.IOException;

import activitydiagram.ActivityDiagram;
import activitydiagram.ActivityDiagramParser;
import diagramgenerator.JwtActivityDiagram;

public class Main {

	public static final String projectPath = "/Users/jimmymunoz/Documents/workspace-neon/visitor";
	public static final String projectSourcePath = projectPath + "/src";
	public static final String jrePath = "/Library/Java/JavaVirtualMachines/jdk1.8.0_91.jdk/Contents/Home/jre/lib/rt.jar"; // which java
	// http://stackoverflow.com/questions/4681090/how-do-i-find-where-jdk-is-installed-on-my-windows-machine
	// ls /System/Library/Frameworks/JavaVM.framework/Versions
	// /System/Library/Frameworks/JavaVM.framework/Versions/CurrentJDK/Commands/java
	public static final String entryClass = "Principale";
	public static final String entryMethod = "main";

	
	public static void main(String[] args) throws IOException
	{
		ActivityDiagramParser adParser = new ActivityDiagramParser(projectPath, projectSourcePath, jrePath, entryClass, entryMethod);
		ActivityDiagram activityDiagram = adParser.parseActivityDiagram();
		JwtActivityDiagram diagramParser = new JwtActivityDiagram(activityDiagram, projectPath, "MyDiagram");
		diagramParser.proccesActivityDiagram();
		//activityDiagram.testClassDiagram();
	}

}
