package diagramgenerator;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.eclipse.core.runtime.NullProgressMonitor;
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
import org.eclipse.papyrus.infra.core.resource.ModelSet;
import org.eclipse.papyrus.infra.core.resource.ModelsReader;
import org.eclipse.papyrus.infra.core.services.ExtensionServicesRegistry;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.papyrus.infra.ui.extension.commands.IModelCreationCommand;
import org.eclipse.papyrus.uml.diagram.common.commands.CreateUMLModelCommand;
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
	
	private String uniqueID ;
	private ActivityDiagramAst activityDiagram;
	private String exportFolder;
	private String diagramName;
	private String strWorkflowModel = "";
	private Model umlmodel;
	private int idInc = 0;
	private int idEdgeInc = 0;
	protected HashMap<String,String>idNode;
	protected HashMap<String, String> idEdge;
	protected HashMap<String, String> idActivity;
	private int idActvityInc = 0 ;
		
	
	public JwtActivityDiagram(ActivityDiagramAst activityDiagram, String exportFolder, String diagramName) {
		this.activityDiagram = activityDiagram;
		this.diagramName = diagramName;
		this.exportFolder = exportFolder;
		createActivityDiagram();
		idActivity = new HashMap<String,String>();
		idEdge = new HashMap<String,String>();
		idNode = new HashMap<String,String>();
		initActivity();
		initNode();		
		initEdge();
		this.uniqueID= UUID.randomUUID().toString();
		proccesActivityDiagram();

		
	}
	public void initActivity()
	{
		for(PackageableElement packElement :umlmodel.getPackagedElements()){
			if( packElement instanceof Activity ){						
				this.idActivity.put(packElement.getName(),getidActivityInc()+"A");
				idActivityIncrment();
			}
	}
	}
	public void initEdge()
	{
		
		for(PackageableElement packElement :umlmodel.getPackagedElements()){
			if( packElement instanceof Activity ){
				for (ActivityEdge edge : ((Activity) packElement).getEdges())
				{								
					this.idEdge.put(edge.getName(),getidEdgeInc()+"E");
					idEdgIncment();
					
				}
			}
		}
	}
	public void initNode()
	{
		String uniqueID = UUID.randomUUID().toString();

		for(PackageableElement packElement :umlmodel.getPackagedElements()){
			if( packElement instanceof Activity ){
				for ( ActivityNode nodeA : ((Activity) packElement).getNodes())
				{								
					this.idNode.put(nodeA.getName(), getidInc()+"A");
					idIncment();					
				}
			}
		}
	}
	public void idActivityIncrment()
	{
		this.idActvityInc++;
	}
	public void idEdgIncment()
	{
		this.idEdgeInc ++;
	}
	public String getidActivityInc()
	{
		int id = this.idActvityInc;
		return String.valueOf(id);
	}
	public String  getidEdgeInc()
	{
		int id = this.idEdgeInc;
		return String.valueOf(id);
	}
	public void idIncment()
	{
		this.idInc ++;
	}
	public String getidInc()
	{		
		int id = this.idInc;
		return String.valueOf(id);
	}
	public void proccesActivityDiagram()
	{
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
		      Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
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
	      Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
	      resource = (new ResourceSetImpl()).createResource(uriUri);
	      resource.getContents().add(root);
	      resource.save(null);
	      System.err.println("Model saved : " + uri);
	   } catch (Exception e) {
	      System.err.println("Error Saving the model : "+e);
	      e.printStackTrace();
	   }
	}
	
	
	public void writeFile(String fileName, String text){
		Path wiki_path = Paths.get(exportFolder, fileName);

        Charset charset = Charset.forName("UTF-8");
        try (BufferedWriter writer = Files.newBufferedWriter(wiki_path, charset, StandardOpenOption.CREATE)) {
            writer.write(text);
            System.err.println("File wrote : " + exportFolder + "/" + fileName);
        } catch (IOException e) {
            System.err.println(e);
        }
	}
	
	public void writeFiles(){
		writeFile( diagramName+ ".workflow", strWorkflowModel);
		writeFile( diagramName+ ".workflow_view",getElementPosition());
	}
	
	private void createModel(){
		String subpackages = getPackageableElementEntete();
		
		String elements = getElements();
		strWorkflowModel = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
				"<uml:Model xmi:version=\"20131001\" xmlns:xmi=\"http://www.omg.org/spec/XMI/20131001\" "
				+ "xmlns:uml=\"http://www.eclipse.org/uml2/5.0.0/UML\" "
				+ "xmi:id=\"_R7CvsNt_EeaaoctTxhWGnA\" name=\"RootElement\">\r\n" 
				+ subpackages +		
				"</uml:Model>";
	//	System.out.println("------------------" + strWorkflowModel);
	}
	
	
	
	public String getEdgeType(ActivityEdge nodeA){
		String result = "";
		if( nodeA instanceof ControlFlow ){
			result = "ControlFlow";
		}
		else if( nodeA instanceof ObjectFlow ){
			result = "ObjectFlow";
		}
	
		return result;
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
		for ( ActivityEdge outedge : outedges)
		{
			// <node xmi:type="uml:OpaqueAction" xmi:id="_WgaYUNt_EeaaoctTxhWGnA" name="OpaqueAction1" incoming="_Y9KuoNt_EeaaoctTxhWGnA"/>
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
	public String getIdNode(String nodeName)
	{
		return idNode.get(nodeName);
	}
	public String getIdEdge(String edgeName)
	{
		return idEdge.get(edgeName);
	}
	
	public String getPackageableElementEntete()
	{
		String packageElement =	"";
		String listnodeEdge = getElements();		
		for(PackageableElement activity : umlmodel.getPackagedElements()){
			
			if(activity instanceof Activity){
				System.out.println(activity.getName());
				String id = idActivity.get(activity.getName());
				String name = activity.getName();
				String listnodes  = getIdNodOfActivity((Activity)activity);
				packageElement += "<packagedElement xmi:type=\"uml:Activity\" xmi:id=\""+ id + "\" name=\""+ name + "\" " + "node=\""+listnodes+ "\">\n";
				packageElement += listnodeEdge + "\n";
				//packageElement += listEdges + "\n";
				packageElement += "</packagedElement>\n";
			}
		}
		System.out.println(packageElement);
		return packageElement;
		
	}
	public String getIdNodOfActivity(Activity activty )
	{
		String node ="";
		for ( ActivityNode nodeA : activty.getNodes() ){
			node+= getIdNode(nodeA.getName()) + " ";
		}
		return node;
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
//<node xmi:type="uml:OpaqueAction" xmi:id="_WgaYUNt_EeaaoctTxhWGnA"
//name="OpaqueAction1" incoming="_Y9KuoNt_EeaaoctTxhWGnA"/>						
						in = " incoming=\"" +getInNode(nodeA) +"\" ";
					}
					if(! getOutNode(nodeA).equals("") )
					{
//outgoing="_Y9KuoNt_EeaaoctTxhWGnA"/>						
						out = "outgoing=\"" + getOutNode(nodeA)+ "\" ";
					}
// <node xmi:type="uml:OpaqueAction" xmi:id="_WgaYUNt_EeaaoctTxhWGnA" name="OpaqueAction1" incoming="_Y9KuoNt_EeaaoctTxhWGnA"/>					
					nodesStr += "	<nodes xmi:type=\"uml:" + getNodeType(nodeA) + "\" xmi:id=\""+ getIdNode(nodeA.getName()) +"\" name=\""+ nodeA.getName() +"\""+
							 in +  ""+ out + "/>" + "\n";											
				}
				for (ActivityEdge edge : ((Activity) packElement).getEdges())
				{					
					
					String source ="";
					String target = "";
					if(!getIdNode(edge.getTarget().getName()).equals(""))
					{
//   <edge xmi:type="uml:ControlFlow" xmi:id="_Y9KuoNt_EeaaoctTxhWGnA" target="_WgaYUNt_EeaaoctTxhWGnA"
						//source="_TyUaYNt_EeaaoctTxhWGnA"/>					
						
						target = "target=\""+ getIdNode(edge.getTarget().getName())+"\" ";
					}
					if(!getIdNode(edge.getSource().getName()).equals(""))
					{ 
						source = getIdNode(edge.getSource().getName()) + "\" ";
					}
					 //<edge xmi:type="uml:ControlFlow" xmi:id="_Y9KuoNt_EeaaoctTxhWGnA" target="_WgaYUNt_EeaaoctTxhWGnA"
					nodesStr += "	<edges xmi:type=\"uml:"+ getEdgeType(edge) + "\" xmi:id=\"" + getIdEdge(edge.getName()) +"\" source=\"" + source + "" + target +"/>"+"\n";
				}
			}
			
		}
		
			
		return nodesStr;
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
	public HashMap<String, String> getIdNode() {
		return idNode;
	}
	public void setIdNode(HashMap<String, String> idNode) {
		this.idNode = idNode;
	}
	public HashMap<String, String> getIdEdge() {
		return idEdge;
	}
	public void setIdEdge(HashMap<String, String> idEdge) {
		this.idEdge = idEdge;
	}
	
	private void createActivityDiagram() {
		umlmodel = UMLFactory.eINSTANCE.createModel();
		
		Activity parentActivity = (Activity) umlmodel.createPackagedElement("A1", UMLPackage.eINSTANCE.getActivity());
		//ActivityNode a2 = (ActivityNode) parentActivity.createOwnedNode("A2", UMLPackage.eINSTANCE.getActivityNode());
		Activity a3 = (Activity) umlmodel.createPackagedElement("A3", UMLPackage.eINSTANCE.getActivity());	
		//createmodelTest();
		//	cba2.setName("Activity2");
		//ForkNode fn = (ForkNode) parentActivity.createOwnedNode("A3", UMLPackage.eINSTANCE.getForkNode());
		/*--------------------------------*/
		/*umlmodel.setName("ModeleDiagrammeDactivite");
		StructuredActivityNode ln =  (StructuredActivityNode) parentActivity.createOwnedNode("A3", UMLPackage.eINSTANCE.getStructuredActivityNode());
		OpaqueAction cba2 = (OpaqueAction) (ln).createNode("model",UMLPackage.eINSTANCE.getOpaqueAction());				
		DecisionNode decisionstructurednode = (DecisionNode) parentActivity.createOwnedNode("1<0",UMLPackage.eINSTANCE.getDecisionNode());
		decisionstructurednode.setName("decisionstructurednode");
		ln.getNodes().add(decisionstructurednode);*/
		
		
		
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
		saveModel("model/ActivityModelResult.xmi", umlmodel);
		
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
	public String getUniqueID() {
		return uniqueID;
	}
	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}
	
	 public void createmodelTest()
	 
	 {
		 ModelSet modelSet = new ModelSet();

		 ModelsReader reader = new ModelsReader(); //Standard ModelsReader for Di + UML + Notation
		 reader.readModel(modelSet);

	      modelSet.createModels(URI.createPlatformResourceURI("model/model.uml", true)); //Use an EMF URI instead of an Eclipse IFile
		 ServicesRegistry registry = null;
		try {
			registry = new ExtensionServicesRegistry(org.eclipse.papyrus.infra.core.Activator.PLUGIN_ID);
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 try {
		 	registry.add(ModelSet.class, Integer.MAX_VALUE, modelSet);
		 	registry.startRegistry();
		 } catch (ServiceException ex) {
		 	//Ignore
		 }

		 IModelCreationCommand creationCommand = new CreateUMLModelCommand();
		 creationCommand.createModel(modelSet);
		 try {
			modelSet.save(new NullProgressMonitor());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
	
}
