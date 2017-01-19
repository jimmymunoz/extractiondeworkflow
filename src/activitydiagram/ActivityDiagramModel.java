package activitydiagram;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLMapImpl;
import org.eclipse.uml2.uml.InitialNode;
import org.eclipse.uml2.uml.InputPin;
import org.eclipse.uml2.uml.MergeNode;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.OpaqueAction;
import org.eclipse.uml2.uml.OutputPin;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.Variable;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.ActivityEdge;
import org.eclipse.uml2.uml.ActivityFinalNode;
import org.eclipse.uml2.uml.ActivityNode;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.ControlFlow;
import org.eclipse.uml2.uml.ControlNode;
import org.eclipse.uml2.uml.DecisionNode;


public class ActivityDiagramModel {
	private Model umlModel;
	private ActivityDiagramAst activityDiagram;
	private HashMap<String, Integer> listIdsActivities;
	private HashMap<String, Integer> listIdsNodes;
	private HashMap<String, Integer> listIdsEdges;
	private Integer _idActivity = 0;
	private Integer _idNode = 0;
	private Integer _idEdge = 0;
	private HashMap<Integer, Activity> listActivities;
	private HashMap<Integer, ActivityEdge> listEdges;
	private Map<String, Map<String, ActivityNode>> listNodes;
	
	
	public ActivityDiagramModel(ActivityDiagramAst activityDiagram){
		listIdsActivities = new HashMap<String, Integer>();
		listIdsNodes = new HashMap<String, Integer>();
		listIdsEdges = new HashMap<String, Integer>();
		listActivities = new HashMap<Integer, Activity>();
		listEdges = new HashMap<Integer, ActivityEdge>();
		listNodes = new HashMap<String, Map<String, ActivityNode>>();
		this.activityDiagram = activityDiagram;
		createActivityDiagram();
	}
	
	private Integer getIdActivity(String key){
		Integer id = 0;
		if( listIdsActivities.containsKey(key) ){
			id = listIdsActivities.get(key);
		}
		else{
			listIdsActivities.put(key, _idActivity);
			id = _idActivity;
			_idActivity++;
		}
		return id;
	}
	
	private Integer getIdEdge(String key){
		Integer id = 0;
		if( listIdsEdges.containsKey(key) ){
			id = listIdsEdges.get(key);
		}
		else{
			listIdsEdges.put(key, _idEdge);
			id = _idEdge;
			_idEdge++;
			
		}
		return id;
	}
	
	private Integer getIdNode(String key){
		Integer id = 0;
		if( listIdsNodes.containsKey(key) ){
			id = listIdsNodes.get(key);
		}
		else{
			listIdsNodes.put(key, _idNode);
			id = _idNode;
			_idNode++;
		}
		return id;
	}
	
	public Model getUmlmodel() {
		return umlModel;
	}
	
	private void createActivityDiagram() {
		System.out.println("---------- createActivityDiagram() -----------------");
		umlModel = UMLFactory.eINSTANCE.createModel();
		/*
		Resource resource = loadModel("model/model_empty.uml", UMLPackage.eINSTANCE);
		if (resource == null) 
			System.err.println("Error Loading model");
		umlModel = (Model) resource.getContents().get(0);
		*/
		//Instruction récupérant le modèle sous forme d'arbre à partir de la classe racine "Model"
		
		Integer idNode = getIdNode("init");
		Integer idActivity = getIdActivity("MainActivity");
		Activity parentActivity = (Activity) umlModel.createPackagedElement("A" + idActivity, UMLPackage.eINSTANCE.getActivity());
		Map<String, ActivityNode> mapActivity = new HashMap<String, ActivityNode>();
		listNodes.put(idActivity+"", mapActivity);
		
		String mainKey = activityDiagram.getKeyEntryPoint();
		Map<Integer, ADInstruction> mainHashInstructions = activityDiagram.getMainHashInstructions(mainKey);
		ADMethodInvocation daMethodInvOb = activityDiagram.getActivityInstructions(mainKey);
		
		getListVariablesByParams(parentActivity, daMethodInvOb);
		
		InitialNode initialNode = (InitialNode) parentActivity.createOwnedNode("init",UMLPackage.eINSTANCE.getInitialNode());
		
		listNodes.get(idActivity+"").put("init", initialNode);
		parentActivity.getNodes().add(initialNode);
		listActivities.put(idActivity, parentActivity);
		
		parentActivity = proccessActivityInstructions(mainHashInstructions, parentActivity, idNode, idActivity);
		listActivities.put(idActivity, parentActivity);
		
		ActivityFinalNode finalNode2 =  (ActivityFinalNode) parentActivity.createOwnedNode(null,UMLPackage.eINSTANCE.getActivityFinalNode());
		//finalNode.setName("final");
		
		if( parentActivity.getNodes().size() > 0 ){
			Integer idEdge = getIdEdge(idActivity + ".edgeFinal");
			ControlFlow edgeFinal = (ControlFlow) parentActivity.createEdge(null,UMLPackage.eINSTANCE.getControlFlow());
			listNodes.get(idActivity+"").put("edgeFinal", initialNode);
			edgeFinal.setSource(parentActivity.getNodes().get(parentActivity.getNodes().size() - 1));
			edgeFinal.setTarget(finalNode2);
		}
		for(int i = 0; i < parentActivity.getOwnedNodes().size(); i++){
			System.out.println("i " + i + " -> " + parentActivity.getOwnedNodes().get(i).toString() );
		}
		saveModel("model/ActivityModelResult.xmi", umlModel);
		
		
		/*
		//ActivityNode a2 = (ActivityNode) parentActivity.createOwnedNode("A2", UMLPackage.eINSTANCE.getActivityNode());
		Activity a3 = (Activity) umlModel.createPackagedElement("A3", UMLPackage.eINSTANCE.getActivity());	
		
		//	cba2.setName("Activity2");
		//ForkNode fn = (ForkNode) parentActivity.createOwnedNode("A3", UMLPackage.eINSTANCE.getForkNode());
		
		StructuredActivityNode ln =  (StructuredActivityNode) parentActivity.createOwnedNode("A3", UMLPackage.eINSTANCE.getStructuredActivityNode());
		CallBehaviorAction cba2 = (CallBehaviorAction) (ln).createNode(null,UMLPackage.eINSTANCE.getCallBehaviorAction());				
		DecisionNode decisionstructurednode = (DecisionNode) parentActivity.createOwnedNode("1<0",UMLPackage.eINSTANCE.getDecisionNode());
		decisionstructurednode.setName("decisionstructurednode");
		ln.getNodes().add(decisionstructurednode);
		
		
		//parentActivity.getNodes().add(a3);
		
		
		//parentActivity.createOwnedNode("a3",a3);
		/*CallBehaviorAction actionactivite2 = (CallBehaviorAction) a3.createOwnedNode(null,UMLPackage.eINSTANCE.getCallBehaviorAction());
		actionactivite2.setBehavior(a3);
		
		
		ControlFlow init = (ControlFlow) a3.createEdge("edgeinit",UMLPackage.eINSTANCE.getControlFlow());
		init.setSource(cba2);
		init.setTarget(actionactivite2);
		
		CallBehaviorAction sumActionA3 = (CallBehaviorAction) a3.createOwnedNode(null,UMLPackage.eINSTANCE.getCallBehaviorAction());
		
		//Process p = (Process) m.createPackagedElement("A1", UMLPackage.eINSTANCE.get());
		
		
		
		//parentActivity.createOwnedNode("A3",UMLPackage.eINSTANCE.getActivity());
		//Activity parentActivity =  UMLFactory.eINSTANCE.createActivity(); // m.createPackagedElement("A1", UMLPackage.eINSTANCE.getActivity());
		
			
		//CallBehaviorAction sumAction = UMLFactory.eINSTANCE.createCallBehaviorAction();
		CallBehaviorAction sumAction = (CallBehaviorAction) parentActivity.createOwnedNode(null,UMLPackage.eINSTANCE.getCallBehaviorAction());
		//cba1.setBehavior(a2);
		
		//sumAction.setName("sum(x,y);");
		
		//parentActivity.getNodes().add(sumAction);
		sumAction.setBehavior(parentActivity);
		
		ControlFlow edgeinit = (ControlFlow) parentActivity.createEdge("edgeinit",UMLPackage.eINSTANCE.getControlFlow());
		edgeinit.setSource(initialNode);
		edgeinit.setTarget(sumAction);
		
		
		DecisionNode decisionnode = (DecisionNode) parentActivity.createOwnedNode(null,UMLPackage.eINSTANCE.getDecisionNode());
		//decisionnode.setName("7>5");
		parentActivity.getNodes().add(decisionnode);
		
		ControlFlow cf = (ControlFlow) parentActivity.createEdge("cf",UMLPackage.eINSTANCE.getControlFlow());
		cf.setSource(sumAction);
		cf.setTarget(decisionnode);
		
		
		CallBehaviorAction sumAction1 = (CallBehaviorAction) parentActivity.createOwnedNode(null,UMLPackage.eINSTANCE.getCallBehaviorAction());
		
		//CallBehaviorAction sumAction1 = UMLFactory.eINSTANCE.createCallBehaviorAction();
		//sumAction.setName("sum(x,y);");
		
		parentActivity.getNodes().add(sumAction1);
		
		ControlFlow edg = (ControlFlow) parentActivity.createEdge("cf",UMLPackage.eINSTANCE.getControlFlow());
		edg.setSource(decisionnode);
		edg.setTarget(sumAction1);	
		
		CallBehaviorAction cbsubactivity = (CallBehaviorAction) (a3).createOwnedNode("Action1subactivity",UMLPackage.eINSTANCE.getCallBehaviorAction());
		cbsubactivity.setBehavior(a3);
		
		CallBehaviorAction cbsubactivity1 = (CallBehaviorAction) (a3).createOwnedNode("Action2subactivity",UMLPackage.eINSTANCE.getCallBehaviorAction());
		cbsubactivity1.setBehavior(a3);
		
		ControlFlow edgeinitsubactivity = (ControlFlow) a3.createEdge("edgeinit",UMLPackage.eINSTANCE.getControlFlow());
		edgeinitsubactivity.setSource(cbsubactivity);
		edgeinitsubactivity.setTarget(cbsubactivity1);
		
		ControlFlow edg2b = (ControlFlow) parentActivity.createEdge("cf",UMLPackage.eINSTANCE.getControlFlow());
		edg2b.setSource(decisionnode);
		edg2b.setTarget(cbsubactivity);
		
		
		MergeNode merge = (MergeNode) parentActivity.createOwnedNode(null,UMLPackage.eINSTANCE.getMergeNode()); 
		//merge.setName("endif");
		
		ControlFlow edg2 = (ControlFlow) parentActivity.createEdge("cf",UMLPackage.eINSTANCE.getControlFlow());
		edg2.setSource(sumAction1);
		edg2.setTarget(merge);
		
		ControlFlow edg3 = (ControlFlow) parentActivity.createEdge("cf",UMLPackage.eINSTANCE.getControlFlow());
		edg3.setSource(cbsubactivity1);
		cf.setTarget(merge);
		
		CallBehaviorAction printAction =  (CallBehaviorAction) parentActivity.createOwnedNode(null,UMLPackage.eINSTANCE.getCallBehaviorAction());
		//printAction.setName("print(z);");
		parentActivity.getNodes().add(printAction);
		
		ActivityFinalNode finalNode =  (ActivityFinalNode) parentActivity.createOwnedNode(null,UMLPackage.eINSTANCE.getActivityFinalNode());
		//finalNode.setName("final");
		
		
		ControlFlow edg4 = (ControlFlow) parentActivity.createEdge("cf",UMLPackage.eINSTANCE.getControlFlow());
		edg4.setSource(printAction);
		cf.setTarget(finalNode);
	
	
		//saveModel("model/ActivityModelResult.uml", umlModel);
		
		/*
		Activity parentActivity = (Activity) m.createPackagedElement("A1", UMLPackage.eINSTANCE.getActivity());
		Activity a2 = (Activity) m.createPackagedElement("A2", UMLPackage.eINSTANCE.getActivity());
		Activity a3 = (Activity) m.createPackagedElement("A3", UMLPackage.eINSTANCE.getActivity());

		ActivityPartition p1 = parentActivity.createPartition("P1");
		CallBehaviorAction cba0 = (CallBehaviorAction) parentActivity.createNode(null,UMLPackage.eINSTANCE.getCallBehaviorAction());
		cba0.setBehavior(a2);
		cba0.setName(a2.getName());
		cba0.getInPartitions().add(p1);
		
		// create CallBehaviorActions
		CallBehaviorAction cba1 = (CallBehaviorAction) parentActivity.createNode(null,UMLPackage.eINSTANCE.getCallBehaviorAction());
		cba1.setBehavior(a2);
		cba1.setName(a2.getName());
		
		CallBehaviorAction cba2 = (CallBehaviorAction) parentActivity.createNode(null,UMLPackage.eINSTANCE.getCallBehaviorAction());
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
	
	private Activity proccessActivityInstructions(Map<Integer, ADInstruction> mainHashInstructions, Activity activity, Integer indexParentNode, Integer idActivity ){
		
		for( Integer position : mainHashInstructions.keySet()){
			ADInstruction adInstruction = mainHashInstructions.get(position);
			System.out.println("	 ----- " +  "." + adInstruction.getDisplayInstruction());
			
			createSubActivityIfNotExists(indexParentNode, adInstruction);
		
	    	Integer idNode = getIdNode(adInstruction.getDisplayInstruction());
	    	indexParentNode = idNode - 1;
	    	
	    	//CallBehaviorAction tmpAction = (CallBehaviorAction) activity.createOwnedNode(null,UMLPackage.eINSTANCE.getCallBehaviorAction());
			//tmpAction.setName(methodInvWithVars.trim() );//+ "&#xA;"
	    	ActivityNode actNode = createActivityNodes(activity, adInstruction);
			//tmpAction.creat
	    	//tmpAction.setBehavior(activity);
	    	//listNodes.put(idActivity +"-"+idNode, actNode);
	    	listNodes.get(idActivity+"").put(adInstruction.getPosition()+"", actNode);
	    	//activity.getNodes().add(tmpAction);
			
			Integer idEdge = getIdEdge(idActivity + "."+adInstruction.getPosition());
			ControlFlow edgetmp = (ControlFlow) activity.createEdge(null, UMLPackage.eINSTANCE.getControlFlow());
			edgetmp.setName("edge." + idEdge);
			edgetmp.setTarget(actNode);
			
			ActivityNode sourceNode = getNodeByPosition(adInstruction.getSource()+"", idActivity);
			if( sourceNode != null ){
				//ActivityNode sourceNode = listNodes.get(idActivity +"-"+idNode);
				edgetmp.setSource(sourceNode);
				//edgetmp.setSource(activity.getNodes().get(indexParentNode));
			}
			for(Integer source : adInstruction.getListsources()){
				ActivityNode sourceNode2 = getNodeByPosition(source+"", idActivity);
				if( sourceNode != null ){
					edgetmp.setSource(sourceNode);
				}
			}
			
			//parentNode = (ActivityNode) tmpAction;
			
	    }
		
	    /*
	    for(String param : daMethodInvOb.getParamList()){
	    	System.out.println("   - param: " + param);
	    }
	    */
	    return activity;
	}

	private ActivityNode getNodeByPosition(String nodeKey, Integer idActivity)
	{
		ActivityNode sourceNode = null;
		//Integer idNode = getIdNode(nodeKey);
		if( listNodes.containsKey(idActivity+"") ){
			if( listNodes.get(idActivity+"").containsKey(nodeKey) )
				sourceNode = listNodes.get(idActivity+"").get(nodeKey);
		}
		return sourceNode;
	}
	
	private ActivityNode createActivityNodes(Activity activity, ADInstruction adInstruction) {
		ActivityNode actNode;
		switch(adInstruction.getTypeNode()){
			case "if":
				//activity.getOwnedNode(arg0)
				DecisionNode tmpDescitionNode = (DecisionNode) activity.createOwnedNode(null,UMLPackage.eINSTANCE.getDecisionNode());
				tmpDescitionNode.setName(adInstruction.getDisplayInstruction().trim());//+ "&#xA;"
				//http://download.eclipse.org/modeling/mdt/uml2/javadoc/4.1.0/org/eclipse/uml2/uml/OpaqueAction.html
				//InputPin inputValue = (InputPin) tmpAction.createInputValue(null,null);
				//OutputPin outputValue = (OutputPin) tmpAction.createOutputValue(null,null);
				actNode = tmpDescitionNode;
				break;
			case "merge":
				MergeNode tmpMergeNode = (MergeNode) activity.createOwnedNode(null,UMLPackage.eINSTANCE.getMergeNode());
				tmpMergeNode.setName(adInstruction.getDisplayInstruction().trim() );//+ "&#xA;"
				actNode = tmpMergeNode;
				break;
			case "call":
			default:
				OpaqueAction tmpActionOpaque = (OpaqueAction) activity.createOwnedNode(null,UMLPackage.eINSTANCE.getOpaqueAction());
				tmpActionOpaque.setName(adInstruction.getDisplayInstruction().trim() );//+ "&#xA;"
				//http://download.eclipse.org/modeling/mdt/uml2/javadoc/4.1.0/org/eclipse/uml2/uml/OpaqueAction.html
				InputPin inputValue = (InputPin) tmpActionOpaque.createInputValue(null,null);
				//adInstruction.get
				//inputValue.createUpperBound(arg0, arg1, arg2)
				OutputPin outputValue = (OutputPin) tmpActionOpaque.createOutputValue(null,null);
				//System.out.println("Label2:" + tmpActionOpaque.eCrossReferences() );
				actNode = tmpActionOpaque;
				break;
		}
		/*
		System.out.println("Label:" + actNode.getLabel() );
		System.out.println("Name:" + actNode.getName() );
		System.out.println("QName:" + actNode.getQualifiedName() );
		System.out.println("Label:" + actNode.eContainmentFeature() );
		*/
		return actNode;
	}

	private void createSubActivityIfNotExists(Integer indexParentNode, ADInstruction adInstruction) {
		if( adInstruction.getTypeNode().equals("call") && activityDiagram.getHashInstructionsList().containsKey(adInstruction.getInstructionKey()) ){
			System.out.println("	 	- exists " + adInstruction.getInstructionKey());
			if( activityDiagram.getHashInvocationMethods().size() > 0){//Composite Task -> new Activity
				Integer oldId = _idActivity;
				Integer idActivity = getIdActivity(adInstruction.getInstructionKey());
				if( oldId < _idActivity ){ //If did not exists
					System.out.println("	 	- create " + adInstruction.getInstructionKey());
					Activity tmpActivity = (Activity) umlModel.createPackagedElement("A" + idActivity, UMLPackage.eINSTANCE.getActivity());
					
					Map<String, ActivityNode> mapActivity = new HashMap<String, ActivityNode>();
					listNodes.put(idActivity+"", mapActivity);
					
					Map<Integer, ADInstruction> hashInstructions = activityDiagram.getMainHashInstructions(adInstruction.getInstructionKey());
					ADMethodInvocation daMethodInvOb = activityDiagram.getActivityInstructions(adInstruction.getInstructionKey());
					getListVariablesByParams(tmpActivity, daMethodInvOb);
					listActivities.put(idActivity, tmpActivity);
					//tmpActivity = proccessActivityInstructions(daMethodInvOb2, tmpActivity, parentNode);
					tmpActivity = proccessActivityInstructions(hashInstructions, tmpActivity, indexParentNode, idActivity);
					listActivities.put(idActivity, tmpActivity);
				}
			}
		}
	}
	
	private List<Variable> getListVariablesByParams(Activity activity, ADMethodInvocation daMethodInvOb){
		List<Variable> listVariables = new ArrayList<Variable>();
		for(String param : daMethodInvOb.getParamList()){
			//Type arg1 = (Type) UMLPackage.eINSTANCE.getType();
			String[] pramssplit = param.split(" ");
			//arg1.setName(pramssplit[0]);
			Variable tmpVar = activity.createVariable(pramssplit[1], null);
					//.createVariable(null, arg1 );
			//Variable tmpVar = (Variable) activity.createVariable(null, UMLPackage.eINSTANCE.getVariable());
			//tmpVar.setName(pramssplit[1]);
			listVariables.add(tmpVar);
		}
		
		return listVariables;
	}
	
	
	public static Resource loadModel(String uri, EPackage pack) {
	   Resource resource = null;
	   try {
	      URI uriUri = URI.createURI(uri);
	      Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("uml", new XMIResourceFactoryImpl());
	      resource = (new ResourceSetImpl()).createResource(uriUri);
	      XMLResource.XMLMap xmlMap = new XMLMapImpl();
	      xmlMap.setNoNamespacePackage(pack);
	      HashMap options = new HashMap();
	      //options.put(XMLResource.OPTION_XML_MAP, xmlMap);
	      resource.load(options);
	   }
	   catch(Exception e) {
	      System.err.println("Error Loading Model : "+e);
	      e.printStackTrace();
	   }
	   return resource;
	}
	
	public static void saveModel_(String uri, Package root) {
		//URI uriUri = URI.createURI(uri);
		URI uriUri = URI.createURI("model").appendSegment("ExtendedPO2").appendFileExtension(UMLResource.FILE_EXTENSION);
		// package_;
		
		Resource resource = new ResourceSetImpl().createResource(uriUri);
		resource.getContents().add(root);
		 
		try {
			resource.save(null);
			System.err.println("Done.");
		} catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
		//save(root, URI.createURI(args[0]).appendSegment("ExtendedPO2")
		//.appendFileExtension(UMLResource.FILE_EXTENSION));
	}
	public static void saveModel(String uri, EObject root) {
	   Resource resource = null;
	   try {
	      URI uriUri = URI.createURI(uri);
	      //URI uriUri = URI.createURI("model").appendSegment("ExtendedPO2").appendFileExtension(UMLResource.FILE_EXTENSION);
			
	      Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
          Map<String, Object> m = reg.getExtensionToFactoryMap();
          m.put("xmi", new XMIResourceFactoryImpl());
	      
          //Map<String, DiagramCategoryDescriptor> categories = DiagramCategoryRegistry.getInstance().getDiagramCategoryMap();
	      Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
	      
	     // categories.get("uml").getCommand();
	      
	      HashMap options = new HashMap<String, String>();
	      options.put(XMLResource.OPTION_ENCODING, "UTF-8"); // set encoding to utf-8
	      options.put(XMLResource.OPTION_EXTENDED_META_DATA, Boolean.TRUE);
	      options.put(XMLResource.OPTION_SAVE_TYPE_INFORMATION, false);
	      
	      /*
	      options.put(XMLResource.OPTION_EXTENDED_META_DATA, true);
	      options.put(XMLResource.OPTION_SUPPRESS_DOCUMENT_ROOT, true);
	      options.put(XMLResource.OPTION_SCHEMA_LOCATION, true);
	      options.put(XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE, true);
	      options.put(XMLResource.OPTION_ELEMENT_HANDLER, new ElementHandlerImpl(true));
	      options.put(XMLResource.OPTION_USE_LEXICAL_HANDLER, true);
	      */
	      
	      options.put(XMIResource.OPTION_USE_XMI_TYPE, Boolean.TRUE);
	      options.put(XMIResource.XMI_ID, Boolean.TRUE);
	      //options.put(XMIResource., Boolean.TRUE);
	      
	       
	      // options.put(XMLResource.OPTION_SCHEMA_LOCATION, xsdUri.toString()); // not working
	      //options.put(XMLResource.OPTION_SCHEMA_LOCATION_IMPLEMENTATION, ServerPackage.eINSTANCE.getNsURI()); // not working
	      // save to file
	      
	      //XMLResource.XMLMap xmlMap = new XMLMapImpl();
	      //xmlMap.setNoNamespacePackage(UMLPackage.eINSTANCE);
	      //options.put(XMLResource.OPTION_XML_MAP, xmlMap);
	      
	      resource = (new ResourceSetImpl()).createResource(uriUri);
	      resource.getContents().add(root);
	      
	      
	      resource.save(options);
	      /*
	      final XMLHelper xmlHelper = new XMIHelperImpl();
	      XMISaveImpl x = new XMISaveImpl(xmlHelper);

	      StringWriter sw = new StringWriter();
	      x.save((XMLResource) resource, sw, options);
	      */
	    //resource.save(null);
	      //resource.save(Collections.EMPTY_MAP);
	      System.err.println("Model saved : " + uri);
	   } catch (Exception e) {
	      System.err.println("Error Saving the model : "+e);
	      e.printStackTrace();
	   }
	}
}
