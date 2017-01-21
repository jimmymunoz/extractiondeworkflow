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
import org.eclipse.uml2.uml.NamedElement;
import org.eclipse.uml2.uml.OpaqueAction;
import org.eclipse.uml2.uml.OutputPin;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.StructuredActivityNode;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.ValueSpecification;
import org.eclipse.uml2.uml.Variable;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.ActivityEdge;
import org.eclipse.uml2.uml.ActivityFinalNode;
import org.eclipse.uml2.uml.ActivityNode;
import org.eclipse.uml2.uml.CallBehaviorAction;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Comment;
import org.eclipse.uml2.uml.ControlFlow;
import org.eclipse.uml2.uml.ControlNode;
import org.eclipse.uml2.uml.DecisionNode;


public class ActivityDiagramModel {
	private Model umlModel;
	private Activity parentActivity;
	private ActivityDiagramAst activityDiagram;
	private HashMap<String, Integer> listIdsActivities;
	private HashMap<String, Integer> listIdsNodes;
	private HashMap<String, Integer> listIdsEdges;
	private Integer _idActivity = 0;
	private Integer _idNode = 0;
	private Integer _idEdge = 0;
	private HashMap<Integer, Activity> listActivities;
	private HashMap<Integer, StructuredActivityNode> listSubActivities;
	private HashMap<Integer, ActivityEdge> listEdges;
	private Map<String, Map<String, ActivityNode>> listNodes;
	private String fileModelPathSave;
	private Map<String, String> configurationList;
	private String resultModel = "";
	
	public ActivityDiagramModel(ActivityDiagramAst activityDiagram, String fileModelPathSave, Map<String, String> configurationList){
		listIdsActivities = new HashMap<String, Integer>();
		listIdsNodes = new HashMap<String, Integer>();
		listIdsEdges = new HashMap<String, Integer>();
		listActivities = new HashMap<Integer, Activity>();
		listSubActivities = new HashMap<Integer, StructuredActivityNode>();
		listEdges = new HashMap<Integer, ActivityEdge>();
		listNodes = new HashMap<String, Map<String, ActivityNode>>();
		this.activityDiagram = activityDiagram;
		this.fileModelPathSave = fileModelPathSave;
		this.configurationList = configurationList;
		createActivityDiagram();
	}
	
	public String getFileModelPathSave() {
		return fileModelPathSave;
	}

	public void setFileModelPathSave(String fileModelPathSave) {
		this.fileModelPathSave = fileModelPathSave;
	}
	
	private Integer getIdActivity(String key){
		Integer id = -1;
		if( listIdsActivities.containsKey(key) ){
			id = listIdsActivities.get(key);
		}
		return id;
	}
	
	private Integer getAndIncrementIdActivity(String key){
		Integer id = 0;
		if( listIdsActivities.containsKey(key) ){
			id = listIdsActivities.get(key);
		}
		else{
			listIdsActivities.put(key, _idActivity);
			id = _idActivity;
			_idActivity++;
			System.out.println(" getAndIncrementIdActivity: " + key + " " + id);
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
		resultModel += "\n---------- createActivityDiagram() -----------------";
		System.out.println("---------- createActivityDiagram() -----------------");
		for( String key : activityDiagram.getHashInstructionsList().keySet() ){
			System.out.println(" 	key " + key);
		}
		
		umlModel = UMLFactory.eINSTANCE.createModel();
		String mainKey = activityDiagram.getKeyEntryPoint();
		Map<Integer, ADInstruction> mainHashInstructions = activityDiagram.getHashInstructions(mainKey);
		
		ADMethodInvocation daMethodInvOb = activityDiagram.getActivityInstructions(mainKey);
		
		Integer idNode = getIdNode("init2");
		Integer idActivity = getAndIncrementIdActivity(daMethodInvOb.getInstructionKey());
		parentActivity = (Activity) umlModel.createPackagedElement(daMethodInvOb.getDisplayInstruction(), UMLPackage.eINSTANCE.getActivity());
		Map<String, ActivityNode> mapActivity = new HashMap<String, ActivityNode>();
		listNodes.put(idActivity+"", mapActivity);
		
		//getListVariablesByParams(parentActivity, daMethodInvOb);
		StructuredActivityNode subActtivity = null;
		NamedElement tmparentActivity = proccessActivityInstructions(subActtivity, mainHashInstructions, idNode, idActivity);
		if( tmparentActivity instanceof Activity ){
			parentActivity = (Activity) tmparentActivity;
		}
		listActivities.put(idActivity, parentActivity);
		
		saveModel(this.fileModelPathSave, umlModel);
		System.out.println("---------- end createActivityDiagram( " + this.fileModelPathSave +  " ) -----------------\n");
		resultModel += "\n---------- end createActivityDiagram( \" + this.fileModelPathSave +  \" ) -----------------\\n";
	}
	
	
	private StructuredActivityNode proccessActivityInstructions(StructuredActivityNode subActtivity, Map<Integer, ADInstruction> mainHashInstructions, Integer indexParentNode, Integer idActivity ){
		
		for( Integer position : mainHashInstructions.keySet()){
			ADInstruction adInstruction = mainHashInstructions.get(position);
			
			ActivityNode actNode = null;
			ActivityNode sourceNode = null;
			
			Integer idSubActivity = getIdActivity(adInstruction.getInstructionKey());
			if( idSubActivity == -1 ){
				idSubActivity = idActivity;
			}
			
			if( subActtivity != null ){
				actNode = createActivityNode(subActtivity, adInstruction, idSubActivity);
				sourceNode = getNodeByPosition(adInstruction.getSource()+"", idSubActivity);
			}
	    	else{
	    		actNode = createActivityNode(parentActivity, adInstruction, idActivity);
	    		sourceNode = getNodeByPosition(adInstruction.getSource()+"", idActivity);
	    	}
			
			if( configurationList.get("create_subactivities").equals("1") && adInstruction.getTypeNode().equals("call") ){
				
				createSubActivityIfNotExists(indexParentNode, adInstruction);
				idSubActivity = getIdActivity(adInstruction.getInstructionKey());
				
				System.out.println("     idSubActivity " + idSubActivity + " idActivity " + adInstruction.getInstructionKey() + " idActivity " + idActivity );
				StructuredActivityNode subActtivity2 = listSubActivities.get(idSubActivity);
				
		    	sourceNode = getNodeByPosition(adInstruction.getSource()+"", idActivity);
		    	if( subActtivity2 != null ){
		    		Map<String, ActivityNode> mapSubActivity = listNodes.get(idSubActivity+"");
		    		ActivityNode firstSubActNode = null;
		    		for(String keySubAct  : mapSubActivity.keySet()){
		    			firstSubActNode = mapSubActivity.get(keySubAct);
		    			break;
		    		}
		    		
		    		if(firstSubActNode != null){
		    			Comment comment = parentActivity.createOwnedComment();
			    		comment.setBody("Subactivity: " + adInstruction.getInstructionKey());
			    		comment.getAnnotatedElements().add(actNode);
			    		comment.getAnnotatedElements().add(firstSubActNode);
		    		}
				}
			}
			
			if( sourceNode != null && adInstruction.getTypeNode() != "merge" ){
				//System.out.println("  Create Edge " + adInstruction.getSource() + ": " + actNode.getLabel() + "  " + sourceNode.getLabel());
				Integer idEdge = getIdEdge(idActivity + "."+adInstruction.getPosition());
				ControlFlow edgetmp = (ControlFlow) parentActivity.createEdge(null, UMLPackage.eINSTANCE.getControlFlow());
				//edgetmp.setName("edge." + idEdge);
				edgetmp.setTarget(actNode);
				edgetmp.setSource(sourceNode);
			}
			ArrayList<Integer> sourcelist = adInstruction.getListsources();
		    int i = 0;
		    for( Integer sourcetmp : sourcelist ){
		    	ActivityNode sourceNode2 = getNodeByPosition(sourcetmp+"", idActivity);
				if( sourceNode2 != null ){
					i++;
					Integer idEdge = getIdEdge(idActivity + "."+adInstruction.getPosition() + i);
					ControlFlow edgetmp = (ControlFlow) parentActivity.createEdge(null, UMLPackage.eINSTANCE.getControlFlow());
					//edgetmp.setName("-edge." + idEdge);
					edgetmp.setTarget(actNode);
					edgetmp.setSource(sourceNode2);
				}
			}
		}
		return subActtivity;
	}

	private ActivityNode getNodeByPosition(String nodeKey, Integer idActivity)
	{
		ActivityNode sourceNode = null;
		if( listNodes.containsKey(idActivity+"") ){
			if( listNodes.get(idActivity+"").containsKey(nodeKey) )
				sourceNode = listNodes.get(idActivity+"").get(nodeKey);
		}
		return sourceNode;
	}
	
	private ActivityNode createActivityNode(Activity activity, ADInstruction adInstruction, Integer idActivity) {
		ActivityNode actNode;
		switch(adInstruction.getTypeNode()){
			case "init":
				InitialNode initialNode = (InitialNode) activity.createOwnedNode(null,UMLPackage.eINSTANCE.getInitialNode());
				initialNode.setName(adInstruction.getInstructionKey().trim());
				actNode = initialNode;
				break;
			case "final":	
				ActivityFinalNode finalNode2 =  (ActivityFinalNode) activity.createOwnedNode(null,UMLPackage.eINSTANCE.getActivityFinalNode());
				finalNode2.setName(adInstruction.getInstructionKey().trim());
				actNode = finalNode2;
				break;
			case "if":
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
				//CallBehaviorAction tmpActionOpaque = (CallBehaviorAction) activity.createOwnedNode(null,UMLPackage.eINSTANCE.getCallBehaviorAction());				
				OpaqueAction tmpActionOpaque = (OpaqueAction) activity.createOwnedNode(null,UMLPackage.eINSTANCE.getOpaqueAction());
				tmpActionOpaque.setName(adInstruction.getDisplayInstruction().trim() );//+ "&#xA;"
				//http://download.eclipse.org/modeling/mdt/uml2/javadoc/4.1.0/org/eclipse/uml2/uml/OpaqueAction.html
				//InputPin inputValue = (InputPin) tmpActionOpaque.createInputValue(null,null);
				//ValueSpecification arg0 = (ValueSpecification) UMLPackage.eINSTANCE.getValueSpecification();
				//arg0.setName("input");
				//inputValue.setUpperValue(arg0);
				//adInstruction.get
				//UMLPackage.eINSTANCE.getClass().getClass()
				/*
				Type type = (Type) UMLPackage.eINSTANCE.getType();
				EClass tmpClass =  EcoreFactory.eINSTANCE.createEClass();
				tmpClass.setName("Person");
				inputValue.createUpperBound("x", type, tmpClass);
				*/
				//OutputPin outputValue = (OutputPin) tmpActionOpaque.createOutputValue(null,null);
				//System.out.println("Label2:" + tmpActionOpaque.eCrossReferences() );
				actNode = tmpActionOpaque;
				break;
		}
		if( actNode != null ){
			listNodes.get(idActivity+"").put(adInstruction.getPosition()+"", actNode);
		}
		return actNode;
	}
	
	private ActivityNode createActivityNode(StructuredActivityNode activity, ADInstruction adInstruction, Integer idActivity) {
		ActivityNode actNode;
		switch(adInstruction.getTypeNode()){
			case "init":
				InitialNode initialNode = (InitialNode) activity.createNode(null,UMLPackage.eINSTANCE.getInitialNode());
				initialNode.setName(adInstruction.getInstructionKey().trim());
				actNode = initialNode;
				break;
			case "final":	
				ActivityFinalNode finalNode2 =  (ActivityFinalNode) activity.createNode(null,UMLPackage.eINSTANCE.getActivityFinalNode());
				finalNode2.setName(adInstruction.getInstructionKey().trim());
				actNode = finalNode2;
				break;
			case "if":
				DecisionNode tmpDescitionNode = (DecisionNode) activity.createNode(null,UMLPackage.eINSTANCE.getDecisionNode());
				tmpDescitionNode.setName(adInstruction.getDisplayInstruction().trim());//+ "&#xA;"
				//http://download.eclipse.org/modeling/mdt/uml2/javadoc/4.1.0/org/eclipse/uml2/uml/OpaqueAction.html
				//InputPin inputValue = (InputPin) tmpAction.createInputValue(null,null);
				//OutputPin outputValue = (OutputPin) tmpAction.createOutputValue(null,null);
				actNode = tmpDescitionNode;
				break;
			case "merge":
				MergeNode tmpMergeNode = (MergeNode) activity.createNode(null,UMLPackage.eINSTANCE.getMergeNode());
				tmpMergeNode.setName(adInstruction.getDisplayInstruction().trim() );//+ "&#xA;"
				actNode = tmpMergeNode;
				break;
			case "call":
			default:
				//CallBehaviorAction tmpActionOpaque = (CallBehaviorAction) activity.createOwnedNode(null,UMLPackage.eINSTANCE.getCallBehaviorAction());				
				OpaqueAction tmpActionOpaque = (OpaqueAction) activity.createNode(null,UMLPackage.eINSTANCE.getOpaqueAction());
				tmpActionOpaque.setName(adInstruction.getDisplayInstruction().trim() );//+ "&#xA;"
				//http://download.eclipse.org/modeling/mdt/uml2/javadoc/4.1.0/org/eclipse/uml2/uml/OpaqueAction.html
				//InputPin inputValue = (InputPin) tmpActionOpaque.createInputValue(null,null);
				//ValueSpecification arg0 = (ValueSpecification) UMLPackage.eINSTANCE.getValueSpecification();
				//arg0.setName("input");
				//inputValue.setUpperValue(arg0);
				//adInstruction.get
				//UMLPackage.eINSTANCE.getClass().getClass()
				/*
				Type type = (Type) UMLPackage.eINSTANCE.getType();
				EClass tmpClass =  EcoreFactory.eINSTANCE.createEClass();
				tmpClass.setName("Person");
				inputValue.createUpperBound("x", type, tmpClass);
				*/
				//OutputPin outputValue = (OutputPin) tmpActionOpaque.createOutputValue(null,null);
				//System.out.println("Label2:" + tmpActionOpaque.eCrossReferences() );
				actNode = tmpActionOpaque;
				break;
		}
		if( actNode != null ){
			listNodes.get(idActivity+"").put(adInstruction.getPosition()+"", actNode);
		}
		return actNode;
	}
	
	
	public static Class getClassByName(String className, Package packagesrc){
		Class resultClass = null;
		EList<PackageableElement> packageList = packagesrc.getPackagedElements();
		
		for(PackageableElement tmpElement : packageList ){
			if( tmpElement instanceof Class ){
				if(tmpElement.getName().equals(className) ){
					System.out.println("Class: " + tmpElement.getName()); 
					resultClass = (Class) tmpElement;
				}
			}
			if( resultClass != null ){
				return resultClass;
			}
			else if( tmpElement instanceof Package ){
				resultClass = getClassByName(className, (Package) tmpElement);
			}
		}
		return resultClass;
		
	}

	private void createSubActivityIfNotExists(Integer indexParentNode, ADInstruction adInstruction) {
		StructuredActivityNode tmpActivity;
		Map<Integer, ADInstruction> hashInstructions = activityDiagram.getHashInstructions(adInstruction.getInstructionKey());
		
		System.out.println("   		createSubActivityIfNotExists " + adInstruction.getInstructionKey() + " type " + adInstruction.getTypeNode() );
		if( adInstruction.getTypeNode().equals("call") && activityDiagram.getHashInstructionsList().containsKey(adInstruction.getInstructionKey()) ){
			
			if( hashInstructions.size() > 0){//Composite Task -> new Activity
				Integer oldId = _idActivity;
				Integer idActivity = getAndIncrementIdActivity(adInstruction.getInstructionKey());
				
				if( oldId < _idActivity ){ //If did not exists
					System.out.println("  new  ........ " + adInstruction.getInstructionKey() + "  hashInstructions.size(): " +  hashInstructions.size() );
					
					//Activity tmpActivity = (Activity) umlModel.createPackagedElement("A" + idActivity, UMLPackage.eINSTANCE.getActivity());
					//tmpActivity = (Activity) umlModel.createPackagedElement("A" + idActivity, UMLPackage.eINSTANCE.getActivity());
					tmpActivity =  (StructuredActivityNode) parentActivity.createOwnedNode(adInstruction.getInstructionKey(), UMLPackage.eINSTANCE.getStructuredActivityNode());
					listSubActivities.put(idActivity, tmpActivity);
					
					Map<String, ActivityNode> mapActivity = new HashMap<String, ActivityNode>();
					listNodes.put(idActivity+"", mapActivity);
					
					//ADMethodInvocation daMethodInvOb = activityDiagram.getActivityInstructions(adInstruction.getInstructionKey());
					//getListVariablesByParams(tmpActivity, daMethodInvOb);
					//listSubActivities.put(idActivity, tmpActivity);
					//tmpActivity = proccessActivityInstructions(daMethodInvOb, tmpActivity, parentNode);
					StructuredActivityNode tmpActivity2 = proccessActivityInstructions(tmpActivity, hashInstructions, indexParentNode, idActivity);
					listSubActivities.put(idActivity, tmpActivity2);
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
	
	
	public static Resource loadModel(String uri, EPackage pack) 
	{
	   Resource resource = null;
	   try {
	      URI uriUri = URI.createURI(uri);
	      Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
	      resource = (new ResourceSetImpl()).createResource(uriUri);
	      XMLResource.XMLMap xmlMap = new XMLMapImpl();
	      xmlMap.setNoNamespacePackage(pack);
	      HashMap options = new HashMap();
	      options.put(XMLResource.OPTION_XML_MAP, xmlMap);
	      resource.load(options);
	   }
	   catch(Exception e) {
	      System.err.println("Error Loading Model : "+e);
	      e.printStackTrace();
	   }
	   return resource;
	}
	
	public static void saveModel_(String uri, Package root) {
		URI uriUri = URI.createURI("model").appendSegment("ExtendedPO2").appendFileExtension(UMLResource.FILE_EXTENSION);
		
		Resource resource = new ResourceSetImpl().createResource(uriUri);
		resource.getContents().add(root);
		 
		try {
			resource.save(null);
			System.err.println("Done.");
		} 
		catch (IOException ioe) {
			System.err.println(ioe.getMessage());
		}
	}
	public static void saveModel(String uri, EObject root) {
	   Resource resource = null;
	   try {
	      URI uriUri = URI.createURI(uri);
	      //URI uriUri = URI.createURI("model").appendSegment("ExtendedPO2").appendFileExtension(UMLResource.FILE_EXTENSION);
			
	      Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
          Map<String, Object> m = reg.getExtensionToFactoryMap();
          m.put("xmi", new XMIResourceFactoryImpl());
	      Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("xmi", new XMIResourceFactoryImpl());
	      
	      HashMap options = new HashMap<String, String>();
	      options.put(XMLResource.OPTION_ENCODING, "UTF-8"); // set encoding to utf-8
	      options.put(XMLResource.OPTION_EXTENDED_META_DATA, Boolean.TRUE);
	      options.put(XMLResource.OPTION_SAVE_TYPE_INFORMATION, false);
	      options.put(XMIResource.OPTION_USE_XMI_TYPE, Boolean.TRUE);
	      options.put(XMIResource.XMI_ID, Boolean.TRUE);
	      
	      resource = (new ResourceSetImpl()).createResource(uriUri);
	      resource.getContents().add(root);
	      
	      resource.save(options);
	      System.err.println("Model saved : " + uri);
	   } catch (Exception e) {
	      System.err.println("Error Saving the model : "+e);
	      e.printStackTrace();
	   }
	}

	public String getResultModel() {
		return resultModel;
	}

	public void setResultModel(String resultModel) {
		this.resultModel = resultModel;
	}
}
