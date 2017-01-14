package activitydiagram;

import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.*;
import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLMapImpl;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.InitialNode;
import org.eclipse.uml2.uml.MergeNode;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.StructuredActivityNode;
import org.eclipse.uml2.uml.TypedElement;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.VisibilityKind;
import org.eclipse.uml2.uml.internal.impl.OperationImpl;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Action;
import org.eclipse.uml2.uml.Activity;
import org.eclipse.uml2.uml.ActivityFinalNode;
import org.eclipse.uml2.uml.ActivityNode;
import org.eclipse.uml2.uml.CallBehaviorAction;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.ControlFlow;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.DecisionNode;


public class ActivityDiagramModel {
	private Model umlModel;
	private ActivityDiagram activityDiagram;
	private HashMap<String, Integer> listIdsActivities;
	private HashMap<String, Integer> listIdsNodes;
	private HashMap<String, Integer> listIdsEdges;
	private Integer _idActivity = 0;
	private Integer _idNode = 0;
	private Integer _idEdge = 0;
	
	
	public ActivityDiagramModel(ActivityDiagram activityDiagram){
		listIdsActivities = new HashMap<String, Integer>();
		listIdsNodes = new HashMap<String, Integer>();
		listIdsEdges = new HashMap<String, Integer>();
		this.activityDiagram = activityDiagram;
		umlModel = UMLFactory.eINSTANCE.createModel();
		createActivityDiagram();
	}
	
	private Integer getIdActivity(String key){
		Integer id = 0;
		if( listIdsActivities.containsKey(key) ){
			id = listIdsActivities.get(key);
		}
		else{
			_idActivity++;
			listIdsActivities.put(key, _idActivity);
			id = _idActivity;
		}
		return id;
	}
	
	private Integer getIdEdge(String key){
		Integer id = 0;
		if( listIdsEdges.containsKey(key) ){
			id = listIdsEdges.get(key);
		}
		else{
			_idEdge++;
			listIdsEdges.put(key, _idEdge);
			id = _idEdge;
		}
		return id;
	}
	
	private Integer getIdNode(String key){
		Integer id = 0;
		if( listIdsNodes.containsKey(key) ){
			id = listIdsNodes.get(key);
		}
		else{
			_idNode++;
			listIdsNodes.put(key, _idNode);
			id = _idNode;
		}
		return id;
	}
	
	public Model getUmlmodel() {
		return umlModel;
	}
	
	private Activity proccessActivityInstructions(ADMethodInvocation daMethodInvOb, Activity activity, ActivityNode parentNode ){
		
		Activity newActivity = null;
		List<String> invocationMethodList = daMethodInvOb.getInvocationMethodList();
		List<String> invocationMethodWithVarsList = daMethodInvOb.getInvocationMethodListWithVars();
		System.out.println("     - params: " +  daMethodInvOb.getMethodNameWithVars());
		for(int i = 0; i < invocationMethodList.size(); i++) {
			String methodInv = invocationMethodList.get(i);
			String methodInvWithVars = invocationMethodWithVarsList.get(i);
			
			System.out.println("	 ----- " + methodInv);
	    	if( activityDiagram.getHashInvocationMethods().containsKey(methodInv)  ){
	    		System.out.println("	 	- exists " + methodInv);
	    		Integer oldId = _idActivity;
	    		Integer idActivity = getIdActivity(methodInv);
	    		if( oldId < _idActivity ){ //If did not exists
	    			System.out.println("	 	- create " + methodInv);
	    			newActivity = (Activity) umlModel.createPackagedElement("A" + idActivity, UMLPackage.eINSTANCE.getActivity());
	    			ADMethodInvocation daMethodInvOb2 = activityDiagram.getMainActivityInstructions();
	    			newActivity = proccessActivityInstructions(daMethodInvOb2, newActivity, parentNode);
	    		}
	    		else{
	    			System.out.println("	 	- get " + methodInv);
	    		}
	    	}
	    	Integer idNode = getIdNode(methodInv);
	    	CallBehaviorAction tmpAction = (CallBehaviorAction) activity.createOwnedNode(null,UMLPackage.eINSTANCE.getCallBehaviorAction());
			tmpAction.setName(methodInvWithVars.trim() );//+ "&#xA;"
	    	tmpAction.setBehavior(activity);
			activity.getNodes().add(tmpAction);
			
			Integer idEdge = getIdEdge(methodInv);
			ControlFlow edgetmp = (ControlFlow) activity.createEdge("edge." + idEdge,UMLPackage.eINSTANCE.getControlFlow());
			//edgetmp.setSource(parentNode);
			//edgetmp.setTarget(tmpAction);
			parentNode = tmpAction;
	    }
		
	    /*
	    for(String param : daMethodInvOb.getParamList()){
	    	System.out.println("   - param: " + param);
	    }
	    */
	    return activity;
	}
	
	
	
	private void createActivityDiagram() {
		System.out.println("---------- createActivityDiagram() -----------------");
		Integer idActivity = getIdActivity("MainActivity");
		Activity parentActivity = (Activity) umlModel.createPackagedElement("A" + idActivity, UMLPackage.eINSTANCE.getActivity());
		InitialNode initialNode = (InitialNode) parentActivity.createOwnedNode(null,UMLPackage.eINSTANCE.getInitialNode());
		initialNode.setName("init");
		parentActivity.getNodes().add(initialNode);
		
		ADMethodInvocation daMethodInvOb = activityDiagram.getMainActivityInstructions();
		parentActivity = proccessActivityInstructions(daMethodInvOb, parentActivity, initialNode);
		
		saveModel("model/ActivityModelResult.uml", umlModel);
		
		
		
		//ActivityNode a2 = (ActivityNode) parentActivity.createOwnedNode("A2", UMLPackage.eINSTANCE.getActivityNode());
		Activity a3 = (Activity) umlModel.createPackagedElement("A3", UMLPackage.eINSTANCE.getActivity());	
		
		//	cba2.setName("Activity2");
		//ForkNode fn = (ForkNode) parentActivity.createOwnedNode("A3", UMLPackage.eINSTANCE.getForkNode());
		/*--------------------------------*/
		
		StructuredActivityNode ln =  (StructuredActivityNode) parentActivity.createOwnedNode("A3", UMLPackage.eINSTANCE.getStructuredActivityNode());
		CallBehaviorAction cba2 = (CallBehaviorAction) (ln).createNode(null,UMLPackage.eINSTANCE.getCallBehaviorAction());				
		DecisionNode decisionstructurednode = (DecisionNode) parentActivity.createOwnedNode("1<0",UMLPackage.eINSTANCE.getDecisionNode());
		decisionstructurednode.setName("decisionstructurednode");
		ln.getNodes().add(decisionstructurednode);
		
		
		
		/*--------------------------------*/
		
		//parentActivity.getNodes().add(a3);
		
		
		//parentActivity.createOwnedNode("a3",a3);
		/*CallBehaviorAction actionactivite2 = (CallBehaviorAction) a3.createOwnedNode(null,UMLPackage.eINSTANCE.getCallBehaviorAction());
		actionactivite2.setBehavior(a3);
		
		
		ControlFlow init = (ControlFlow) a3.createEdge("edgeinit",UMLPackage.eINSTANCE.getControlFlow());
		init.setSource(cba2);
		init.setTarget(actionactivite2);
		
		CallBehaviorAction sumActionA3 = (CallBehaviorAction) a3.createOwnedNode(null,UMLPackage.eINSTANCE.getCallBehaviorAction());
		*/
		
		
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
	
	public static Class getClassByName(String className, Package packagesrc){
		Class resultClass = null;
		EList<PackageableElement> packageList = packagesrc.getPackagedElements();
		
		for(PackageableElement tmpElement : packageList ){
			//System.out.println("tmpElement: " + tmpElement.getName());
			if( tmpElement instanceof Class ){
				//System.out.println("tmpElement is class");
				if(tmpElement.getName().equals(className) ){
					System.out.println("Class: " + tmpElement.getName()); 
					resultClass = (Class) tmpElement;
					
				}
			}
			if( resultClass != null ){
				return resultClass;
			}
			else if( tmpElement instanceof Package ){
				//System.out.println("tmpElement is Package");
				resultClass = getClassByName(className, (Package) tmpElement);
			}
		}
		return resultClass;
		
	}
	
	public static void saveModel(String uri, EObject root) {
	   Resource resource = null;
	   try {
	      URI uriUri = URI.createURI(uri);
	      Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("uml", new XMIResourceFactoryImpl());
	      resource = (new ResourceSetImpl()).createResource(uriUri);
	      resource.getContents().add(root);
	      resource.save(null);
	   } catch (Exception e) {
	      System.err.println("Error Saving the model : "+e);
	      e.printStackTrace();
	   }
	}
}
