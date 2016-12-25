package diagramgenerator;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import activitydiagram.ActivityDiagram;
/*
http://stackoverflow.com/questions/878573/java-multiline-string
In Eclipse if you turn on the option "Escape text when pasting into a string literal" 
(in Preferences > Java > Editor > Typing) and paste a multi-lined string whithin quotes, 
it will automatically add " and \n" + for all your lines.
*/

public class JwtActivityDiagram implements IDiagramGenerator {
	
	private ActivityDiagram activityDiagram;
	private String exportFolder;
	private String diagramName;
	private String strWorkflowModel = "";
	
	
	public JwtActivityDiagram(ActivityDiagram activityDiagram, String exportFolder, String diagramName) {
		this.activityDiagram = activityDiagram;
		this.diagramName = diagramName;
		this.exportFolder = exportFolder;
	}

	public void setActivityDiagram(ActivityDiagram activityDiagram) 
	{
		this.activityDiagram = activityDiagram;
	}
	
	public void proccesActivityDiagram()
	{
		createModel();
		System.out.println(strWorkflowModel);
		writeFiles();
	}
	
	public void writeFile(String fileName, String text){
		Path wiki_path = Paths.get(exportFolder, fileName);

        Charset charset = Charset.forName("UTF-8");
        try (BufferedWriter writer = Files.newBufferedWriter(wiki_path, charset, StandardOpenOption.CREATE)) {
            writer.write(text);
        } catch (IOException e) {
            System.err.println(e);
        }
	}
	
	public void writeFiles(){
		writeFile( diagramName+ ".workflow", strWorkflowModel);
	}
	
	private void createModel(){
		String subpackages = getSubPackages();
		String elements = getElements();
		strWorkflowModel = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
			"<core:Model xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:core=\"org.eclipse.jwt/core\" xmlns:data=\"org.eclipse.jwt/data\" xmlns:processes=\"org.eclipse.jwt/processes\" name=\"Workflow\" author=\"jimmymunoz\" version=\"1\" fileversion=\"1.3.0\">\n" + 
			"  <!--subpackages-->\n" + 
			subpackages +
			"  <!--elements-->\n" + 
			elements +
			"  <elements xsi:type=\"processes:Activity\" name=\"" +  diagramName + "\"/>\n" + 
			"</core:Model>\n" + 
			"";
		
	}
	
	private String getSubPackages(){
		String str = "<subpackages name=\"Applications\">\n" + 
				"    <ownedComment text=\"The standard package for applications\"/>\n" + 
				"  </subpackages>\n" + 
				"  <subpackages name=\"Roles\">\n" + 
				"    <ownedComment text=\"The standard package for roles\"/>\n" + 
				"  </subpackages>\n" + 
				"  <subpackages name=\"Data\">\n" + 
				"    <ownedComment text=\"The standard package for data\"/>\n" + 
				"    <subpackages name=\"Datatypes\">\n" + 
				"      <ownedComment text=\"The standard package for datatypes\"/>\n" + 
				"      <subpackages name=\"Package\">\n" + 
				"        <ownedComment/>\n" + 
				"      </subpackages>\n" + 
				"      <elements xsi:type=\"data:DataType\" name=\"URL\"/>\n" + 
				"      <elements xsi:type=\"data:DataType\" name=\"dioParameter\"/>\n" + 
				"      <elements xsi:type=\"data:DataType\" name=\"qualifier\"/>\n" + 
				"      <elements xsi:type=\"data:DataType\" name=\"searchquery\"/>\n" + 
				"      <elements xsi:type=\"data:DataType\" name=\"filename\"/>\n" + 
				"      <elements xsi:type=\"data:Data\" name=\"Directory\" icon=\"\" value=\"DirectoryValue\" dataType=\"//@subpackages.2/@subpackages.0/@elements.4\"/>\n" + 
				"      <elements xsi:type=\"data:DataType\" name=\"URL2\" icon=\"\"/>\n" + 
				"    </subpackages>\n" + 
				"    <elements xsi:type=\"data:Data\" name=\"Test\" icon=\"\" value=\"test\" dataType=\"//@subpackages.2/@subpackages.0/@elements.0\"/>\n" + 
				"    <elements xsi:type=\"data:Data\" name=\"Browser Data\" icon=\"\" value=\"http://xxxxxx.com\" dataType=\"//@subpackages.2/@subpackages.0/@elements.6\"/>\n" + 
				"  </subpackages>";
		return str;
	}
	
	private String getElements(){
		String str = "" +
				" <elements xsi:type=\"processes:Activity\" name=\"VisitorWorkflowDiagram1\">\n" + 
				"    <ownedComment text=\"This is a basic activity\"/>\n" + 
				"    <nodes xsi:type=\"processes:InitialNode\" name=\"Test\" out=\"//@elements.0/@edges.0\"/>\n" + 
				"    <nodes xsi:type=\"processes:FinalNode\" in=\"//@elements.0/@edges.1\"/>\n" + 
				"    <nodes xsi:type=\"processes:Action\" name=\"Action\" in=\"//@elements.0/@edges.0\" out=\"//@elements.0/@edges.2\"/>\n" + 
				"    <nodes xsi:type=\"processes:StructuredActivityNode\" in=\"//@elements.0/@edges.2\" out=\"//@elements.0/@edges.5\">\n" + 
				"      <nodes xsi:type=\"processes:Action\" name=\"Subprocess1\" out=\"//@elements.0/@nodes.3/@edges.0\"/>\n" + 
				"      <nodes xsi:type=\"processes:Action\" name=\"Subproccess2\" in=\"//@elements.0/@nodes.3/@edges.0\"/>\n" + 
				"      <edges source=\"//@elements.0/@nodes.3/@nodes.0\" target=\"//@elements.0/@nodes.3/@nodes.1\"/>\n" + 
				"    </nodes>\n" + 
				"    <nodes xsi:type=\"processes:DecisionNode\" name=\"Condition\" in=\"//@elements.0/@edges.5\" out=\"//@elements.0/@edges.3 //@elements.0/@edges.4\"/>\n" + 
				"    <nodes xsi:type=\"processes:Action\" name=\"YesProcess\" in=\"//@elements.0/@edges.3\" out=\"//@elements.0/@edges.1\" inputs=\"//@subpackages.2/@subpackages.0/@elements.5 //@subpackages.2/@elements.1\" outputs=\"//@subpackages.2/@subpackages.0/@elements.5\"/>\n" + 
				"    <nodes xsi:type=\"processes:Action\" name=\"NoProcess\" in=\"//@elements.0/@edges.4\"/>\n" + 
				"    <edges source=\"//@elements.0/@nodes.0\" target=\"//@elements.0/@nodes.2\"/>\n" + 
				"    <edges source=\"//@elements.0/@nodes.5\" target=\"//@elements.0/@nodes.1\"/>\n" + 
				"    <edges source=\"//@elements.0/@nodes.2\" target=\"//@elements.0/@nodes.3\"/>\n" + 
				"    <edges source=\"//@elements.0/@nodes.4\" target=\"//@elements.0/@nodes.5\"/>\n" + 
				"    <edges source=\"//@elements.0/@nodes.4\" target=\"//@elements.0/@nodes.6\"/>\n" + 
				"    <edges source=\"//@elements.0/@nodes.3\" target=\"//@elements.0/@nodes.4\"/>\n" + 
				"  </elements>\n"
				+ "";
		return str;
	}
	
	
	
}
