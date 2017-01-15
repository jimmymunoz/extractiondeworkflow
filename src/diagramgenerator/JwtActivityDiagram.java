package diagramgenerator;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import org.eclipse.gmf.runtime.diagram.ui.services.layout.LayoutType;
import org.eclipse.emf.*;
import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.util.*;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLMapImpl;
import org.eclipse.uml2.uml.internal.impl.ActivityImpl;
import org.eclipse.uml2.uml.internal.impl.DirectedRelationshipImpl;
import org.eclipse.uml2.uml.internal.impl.GeneralizationImpl;
import org.eclipse.uml2.uml.internal.impl.OperationImpl;
import org.eclipse.uml2.uml.profile.standard.Process;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Class;
//import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.*;
import org.eclipse.uml2.common.util.CacheAdapter;
//import org.eclipse.ocl.uml.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import activitydiagram.ActivityDiagramAst;
/*
http://stackoverflow.com/questions/878573/java-multiline-string
In Eclipse if you turn on the option "Escape text when pasting into a string literal" 
(in Preferences > Java > Editor > Typing) and paste a multi-lined string whithin quotes, 
it will automatically add " and \n" + for all your lines.
*/

public class JwtActivityDiagram implements IDiagramGenerator {
	
	
	private ActivityDiagramAst activityDiagram;
	private String exportFolder;
	private String diagramName;
	private String strWorkflowModel = "";
	private Model umlmodel;
	private int idInc = 0;
	private int idEdgeInc = 0;
	protected HashMap<String,Integer>idNode;
	protected HashMap<String,Integer>idEdge;
	
	public JwtActivityDiagram(ActivityDiagramAst activityDiagram, String exportFolder, String diagramName) {
		this.activityDiagram = activityDiagram;
		this.diagramName = diagramName;
		this.exportFolder = exportFolder;
		createActivityDiagram();
		idEdge = new HashMap<String,Integer>();
		idNode = new HashMap<String,Integer>();
		initNode();		
		initEdge();

		
	}
	public void initEdge()
	{
		for(PackageableElement packElement :umlmodel.getPackagedElements()){
			if( packElement instanceof Activity ){
				for (ActivityEdge edge : ((Activity) packElement).getEdges())
				{								
					this.idEdge.put(edge.getName(), getidEdgeInc());
					idEdgIncment();
					
				}
			}
		}
	}
	public void initNode()
	{
		for(PackageableElement packElement :umlmodel.getPackagedElements()){
			if( packElement instanceof Activity ){
				for ( ActivityNode nodeA : ((Activity) packElement).getNodes())
				{								
					this.idNode.put(nodeA.getName(), getidInc());
					idIncment();					
				}
			}
		}
	}
	public void idEdgIncment()
	{
		this.idEdgeInc ++;
	}
	public int getidEdgeInc()
	{
		return this.idEdgeInc;
	}
	public void idIncment()
	{
		this.idInc ++;
	}
	public int getidInc()
	{
		return this.idInc;
	}
	public void proccesActivityDiagram()
	{
		createActivityDiagram();
		createModel();
		System.out.println(strWorkflowModel);
		writeFiles();
	}
	
	public void setActivityDiagram(ActivityDiagramAst activityDiagram) 
	{
		this.activityDiagram = activityDiagram;
	}
	public static Resource chargerModele(String uri, EPackage pack) {
		   Resource resource = null;
		   try {
		      URI uriUri = URI.createURI(uri);
		      Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("uml", new XMIResourceFactoryImpl());
		      resource = (new ResourceSetImpl()).createResource(uriUri);
		      XMLResource.XMLMap xmlMap = new XMLMapImpl();
		      xmlMap.setNoNamespacePackage(pack);
		      java.util.Map options = new java.util.HashMap();
		      options.put(XMLResource.OPTION_XML_MAP, xmlMap);
		      resource.load(options);
		   }
		   catch(Exception e) {
		      System.err.println("ERREUR chargement du modèle : "+e);
		      e.printStackTrace();
		   }
		   return resource;
		}
	
	
	public static void saveModel(String uri, EObject root) {
	   Resource resource = null;
	   try {
	      URI uriUri = URI.createURI(uri);
	      Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("uml", new XMIResourceFactoryImpl());
	      resource = (new ResourceSetImpl()).createResource(uriUri);
	      resource.getContents().add(root);
	      resource.save(null);
	      System.err.println("Model saved : " + uri);
	   } catch (Exception e) {
	      System.out.println("Error Saving the model : " + e);
	      e.printStackTrace();
	   }
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
		
		writeFile( diagramName+ ".workflow_view",getElementPosition());
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
	
	public String getNodeType(ActivityNode nodeA){
		String result = "";
		if( nodeA instanceof DecisionNode ){
			result = "DecisionNode";
		}
		else if( nodeA instanceof InitialNode ){
			result = "InitialNode";
		}
		else if( nodeA instanceof ActivityFinalNode ){
			result = "FinalNode";
		}
		else if( nodeA instanceof StructuredActivityNode ){
			result = "StructuredActivityNode";
		}
		else if( nodeA instanceof Action ){
			result = "Action";
		}
		else if( nodeA instanceof OpaqueAction ){
			result = "Action";
		}
		else if( nodeA instanceof MergeNode ){
			result = "MergeNode";
		}
		return result;
	}
	public String getSourceEdge(ActivityEdge edge)
	{
		
		return edge.getSource().getName();
	}
	public String getTargetEdge(ActivityEdge edge)
	{
		return edge.getTarget().getName();
	}
	public String getOutNode(ActivityNode node)
	{
		String result = "";
		EList<ActivityEdge> outedges = node.getOutgoings();
		// <nodes xsi:type=\"processes:DecisionNode\" name=\"Condition\" in=\"//@elements.0/@edges.5\" 
		//out=\"//@elements.0/@edges.3 //@elements.0/@edges.4\"/>
		
		for ( ActivityEdge outedge : outedges)
		{
			result = String.valueOf(idEdge.get(outedge.getName()));
			
			
		}
			
		return result;
	}
	public String getInNode(ActivityNode node)
	{
		String result = "";
		EList<ActivityEdge> inedges = node.getIncomings();
		
		for ( ActivityEdge inedge : inedges)
		{
			result = String.valueOf(idEdge.get(inedge.getName()));
			//result += id;
			//"      <nodes xsi:type=\"processes:Action\" name=\"Subprocess1\" out=\"//@elements.0/@nodes.3/@edges.0\"/>\n" +
		}
		return result;
		
		
	}
	public String getNameEdgeSource(ActivityNode node)
	{
		String result = "";
		 
		EList<ActivityEdge> inedges = node.getIncomings();
		
		
		for ( ActivityEdge inedge : inedges)
		{
	
			
			//"      <nodes xsi:type=\"processes:Action\" name=\"Subprocess1\" out=\"//@elements.0/@nodes.3/@edges.0\"/>\n" +
		
			
		}
			
		return result;
		
		
	}
	public  String getIdNodeEdge(String name)
	{
		String result = "";
		
		return  String.valueOf((idNode.get(name)));
	}
	
	private String getElements(){
		
		String nodesStr = "";	
		for(PackageableElement packElement :umlmodel.getPackagedElements()){
			if( packElement instanceof Activity ){
				//nodesStr += packElement.getName() + "\n";		
				
				for ( ActivityNode nodeA : ((Activity) packElement).getNodes() ){									
					String in = " ";
					String out = " ";					
					if(! getInNode(nodeA).equals("") )
					{
						in = " in=\"//@elements.0/@edges."+getInNode(nodeA) +"\" ";
					}
					if(! getOutNode(nodeA).equals("") )
					{
						out = "out=\"//@elements.0/@edges."+ getOutNode(nodeA)+ "\" ";
					}
					nodesStr += "	<nodes xsi:type=\"processes:" + getNodeType(nodeA) + "\" name=\""+ nodeA.getName() +"\""+
							 in +  ""+ out + "/>" + "\n";											
				}
				for (ActivityEdge edge : ((Activity) packElement).getEdges())
				{					
					
					String source ="";
					String target = "";
					if(!getIdNodeEdge(edge.getTarget().getName()).equals(""))
					{
						target = " target=\"//@element.0/@nodes."+ getIdNodeEdge(edge.getTarget().getName())+"\" ";
					}
					if(!getIdNodeEdge(edge.getSource().getName()).equals(""))
					{
						source = " source=\"//@elements.0/@nodes."+ getIdNodeEdge(edge.getSource().getName()) + "\" ";
					}
					//"    <edges source=\"//@elements.0/@nodes.4\" target=\"//@elements.0/@nodes.5\"/>\n" +
					nodesStr += "	<edges"+ source + "" + target +"/>"+"\n";
				}
			}
			
		}
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
				nodesStr + "\n" + 
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
	
	public String getElementPosition()
	{
		
			
		 String pos = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
				"<view:Diagram xmi:version=\"2.0\" xmlns:xmi=\"http://www.omg.org/XMI\" xmlns:view=\"org.eclipse.jwt/view\">\r\n" + 
				"  <describesModel href=\"MyDiagram.workflow#/\"/>";
		/*/<layoutData viewid="Technical">
	    <describesElement href="MyDiagram.workflow#//@elements.0/@nodes.0"/>
	    <layoutData viewid="Technical" x="210" y="3" initialized="true">
	    </layoutData>*/
		
		for(PackageableElement packElement :umlmodel.getPackagedElements()){
			int valuey = 3;
			if( packElement instanceof Activity ){
				for ( ActivityNode nodeA : ((Activity) packElement).getNodes())
				{								
					pos += "	    <layoutData viewid=\"Technical\" x=\"210\" y=\""+ valuey + "\" initialized=\"true\">\r\n" + 
							"	    <describesElement href=\"MyDiagram.workflow#//@elements.0/@nodes."+getIdNode().get(nodeA.getName()) +"\"/>\r\n" + 
							
							"	    </layoutData>\n";
				
					valuey += 50;
				}
			}
			pos+="</view:Diagram>";
		}
		
		
		return pos;
	
	}
	
	public int getIdInc() {
		return idInc;
	}
	public void setIdInc(int idInc) {
		this.idInc = idInc;
	}
	public int getIdEdgeInc() {
		return idEdgeInc;
	}
	public void setIdEdgeInc(int idEdgeInc) {
		this.idEdgeInc = idEdgeInc;
	}
	public HashMap<String, Integer> getIdNode() {
		return idNode;
	}
	public void setIdNode(HashMap<String, Integer> idNode) {
		this.idNode = idNode;
	}
	public HashMap<String, Integer> getIdEdge() {
		return idEdge;
	}
	public void setIdEdge(HashMap<String, Integer> idEdge) {
		this.idEdge = idEdge;
	}
	private void createActivityDiagram() {
		Model m = UMLFactory.eINSTANCE.createModel();
		
		Activity parentActivity = (Activity) m.createPackagedElement("A1", UMLPackage.eINSTANCE.getActivity());
		//ActivityNode a2 = (ActivityNode) parentActivity.createOwnedNode("A2", UMLPackage.eINSTANCE.getActivityNode());
		Activity a3 = (Activity) m.createPackagedElement("A3", UMLPackage.eINSTANCE.getActivity());	
		
		//	cba2.setName("Activity2");
		//ForkNode fn = (ForkNode) parentActivity.createOwnedNode("A3", UMLPackage.eINSTANCE.getForkNode());
		/*--------------------------------*/
		
		StructuredActivityNode ln =  (StructuredActivityNode) parentActivity.createOwnedNode("A3", UMLPackage.eINSTANCE.getStructuredActivityNode());
		OpaqueAction cba2 = (OpaqueAction) (ln).createNode("model",UMLPackage.eINSTANCE.getOpaqueAction());				
		DecisionNode decisionstructurednode = (DecisionNode) parentActivity.createOwnedNode("1<0",UMLPackage.eINSTANCE.getDecisionNode());
		decisionstructurednode.setName("decisionstructurednode");
		ln.getNodes().add(decisionstructurednode);
		
		
		
		/*--------------------------------*/
		
		//parentActivity.getNodes().add(a3);
		
		
		//parentActivity.createOwnedNode("a3",a3);
		/*OpaqueAction actionactivite2 = (OpaqueAction) a3.createOwnedNode(null,UMLPackage.eINSTANCE.getOpaqueAction());
		actionactivite2.setBehavior(a3);
		
		
		ControlFlow init = (ControlFlow) a3.createEdge("edgeinit",UMLPackage.eINSTANCE.getControlFlow());
		init.setSource(cba2);
		init.setTarget(actionactivite2);
		
		OpaqueAction sumActionA3 = (OpaqueAction) a3.createOwnedNode(null,UMLPackage.eINSTANCE.getOpaqueAction());
		*/
		
		
		//Process p = (Process) m.createPackagedElement("A1", UMLPackage.eINSTANCE.get());
		
		
		
		//parentActivity.createOwnedNode("A3",UMLPackage.eINSTANCE.getActivity());
		//Activity parentActivity =  UMLFactory.eINSTANCE.createActivity(); // m.createPackagedElement("A1", UMLPackage.eINSTANCE.getActivity());
		
		InitialNode initialNode = (InitialNode) parentActivity.createOwnedNode("node",UMLPackage.eINSTANCE.getInitialNode());
		initialNode.setName("init");
		parentActivity.getNodes().add(initialNode);
		
	
		
		
		//OpaqueAction sumAction = UMLFactory.eINSTANCE.createOpaqueAction();
		OpaqueAction sumAction = (OpaqueAction) parentActivity.createOwnedNode("node0",UMLPackage.eINSTANCE.getOpaqueAction());
		//cba1.setBehavior(a2);
		parentActivity.getNodes().add(sumAction);
		//sumAction.setName("sum(x,y);");
		
		//parentActivity.getNodes().add(sumAction);
		//sumAction.setBehavior(parentActivity);
		
		ControlFlow edgeinit = (ControlFlow) parentActivity.createEdge("edgeinit",UMLPackage.eINSTANCE.getControlFlow());
		edgeinit.setSource(initialNode);
		edgeinit.setTarget(sumAction);
		
		
		DecisionNode decisionnode = (DecisionNode) parentActivity.createOwnedNode("node1",UMLPackage.eINSTANCE.getDecisionNode());
		//decisionnode.setName("7>5");
		parentActivity.getNodes().add(decisionnode);
		
		
		ControlFlow cf = (ControlFlow) parentActivity.createEdge("cf",UMLPackage.eINSTANCE.getControlFlow());
		cf.setSource(sumAction);
		cf.setTarget(decisionnode);
		
		
		OpaqueAction sumAction1 = (OpaqueAction) parentActivity.createOwnedNode("node5",UMLPackage.eINSTANCE.getOpaqueAction());
		
		//OpaqueAction sumAction1 = UMLFactory.eINSTANCE.createOpaqueAction();
		//sumAction.setName("sum(x,y);");
		
		parentActivity.getNodes().add(sumAction1);
		
		ControlFlow edg = (ControlFlow) parentActivity.createEdge("edge",UMLPackage.eINSTANCE.getControlFlow());
		edg.setSource(decisionnode);
		edg.setTarget(sumAction1);	
		
		OpaqueAction cbsubactivity = (OpaqueAction) (a3).createOwnedNode("Action1subactivity",UMLPackage.eINSTANCE.getOpaqueAction());
		//cbsubactivity.setBehavior(a3);
		
		OpaqueAction cbsubactivity1 = (OpaqueAction) (a3).createOwnedNode("Action2subactivity",UMLPackage.eINSTANCE.getOpaqueAction());
		//cbsubactivity1.setBehavior(a3);
		
		ControlFlow edgeinitsubactivity = (ControlFlow) a3.createEdge("edge0",UMLPackage.eINSTANCE.getControlFlow());
		edgeinitsubactivity.setSource(cbsubactivity);
		edgeinitsubactivity.setTarget(cbsubactivity1);
		
		ControlFlow edg2b = (ControlFlow) parentActivity.createEdge("edge1",UMLPackage.eINSTANCE.getControlFlow());
		edg2b.setSource(decisionnode);
		edg2b.setTarget(cbsubactivity);
		
		
		MergeNode merge = (MergeNode) parentActivity.createOwnedNode("n",UMLPackage.eINSTANCE.getMergeNode()); 
		//merge.setName("endif");
		
		ControlFlow edg2 = (ControlFlow) parentActivity.createEdge("edge0",UMLPackage.eINSTANCE.getControlFlow());
		edg2.setSource(sumAction1);
		edg2.setTarget(merge);
		
		ControlFlow edg3 = (ControlFlow) parentActivity.createEdge("edge2",UMLPackage.eINSTANCE.getControlFlow());
		edg3.setSource(cbsubactivity1);
		edg3.setTarget(merge);
		
		OpaqueAction printAction =  (OpaqueAction) parentActivity.createOwnedNode("node2",UMLPackage.eINSTANCE.getOpaqueAction());
		//printAction.setName("print(z);");
		parentActivity.getNodes().add(printAction);
		
		ActivityFinalNode finalNode =  (ActivityFinalNode) parentActivity.createOwnedNode("node3",UMLPackage.eINSTANCE.getActivityFinalNode());
		//finalNode.setName("final");
		
		
		ControlFlow edg4 = (ControlFlow) parentActivity.createEdge("edge3",UMLPackage.eINSTANCE.getControlFlow());
		edg4.setSource(printAction);
		edg4.setTarget(finalNode);
	
		InputPin inputpin = (InputPin) printAction.createInputValue(null, null);
	
		//parentActivity.getNodes().add(inputpin);						
		//printAction.getInputs().add(inputpin);			
		
		OutputPin outputpin = (OutputPin) printAction.createOutputValue(null, null);

		//parentActivity.getNodes().add(outputpin);
		
	//	printAction.getOutputs().add(outputpin);
		//printAction.inputParameters().add((Parameter) inputpin);
		umlmodel = m;
		//saveModel("model/ActivityModelResult.uml", m);
		
		/*
		ActiviInputty parentActivity = (Activity) m.createPackagedElement("A1", UMLPackage.eINSTANCE.getActivity());
		Activity a2 = (Activity) m.createPackagedElement("A2", UMLPackage.eINSTANCE.getActivity());
		Activity a3 = (Activity) m.createPackagedElement("A3", UMLPackage.eINSTANCE.getActivity());

		ActivityPartition p1 = parentActivity.createPartition("P1");
		OpaqueAction cba0 = (OpaqueAction) parentActivity.createNode(null,UMLPackage.eINSTANCE.getOpaqueAction());
		cba0.setBehavior(a2);
		cba0.setName(a2.getName());
		cba0.getInPartitions().add(p1);
		
		// create OpaqueActions
		OpaqueAction cba1 = (OpaqueAction) parentActivity.createNode(null,UMLPackage.eINSTANCE.getOpaqueAction());
		cba1.setBehavior(a2);
		cba1.setName(a2.getName());
		
		OpaqueAction cba2 = (OpaqueAction) parentActivity.createNode(null,UMLPackage.eINSTANCE.getOpaqueAction());
		cba2.setBehavior(a3);
		cba2.setName(a3.getName());
		
		// connect them with a control flow
		ControlFlow cf = (ControlFlow) parentActivity.createEdge("cf",UMLPackage.eINSTANCE.getControlFlow());
		cf.setSource(cba1);
		cf.setTarget(cba2);
		
		// create the diagram		
		Diagram d = UMLModeler.getUMLDiagramHelper().createDiagram(parentActivity, UMLDiagramKind.ACTIVITY_LITERAL,parentActivity);
		d.setName("My Activity diagram");
		UMLModeler.getUMLDiagramHelper().openDiagramEditor(d);
		*/
	}
	
}
