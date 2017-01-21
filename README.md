#Extraction de workflow
=======

Creation de diagrame d'activité à partir d'un code source Java.
Construction des appels en utilisant RTA et ajoute des instructions de flux de control (IF) pour representer d'une manière plus claire
Genere des modèles standard UML manipulables avec plusiurs outils.

##Processus:
	
- Seleccion de code source java
- Selection du point d'entré 
- Recuperation des information avec AST (Abstract Syntax Tree) de Java
- Generation du modèle UML avec EMF (Eclipse Modeling Framework) 
- Transformation du modèle UML au format de Papyrus
- Visualiser le diagramme avec Papyrus

###Processus:
![Processus](images/processus.png?raw=true "Processus")
###Code Source à Diagrame Activité:
![AD](images/Diagram-Activite.png?raw=true "Diagram-Activite")
###Metamodèle UML Diagrame d'activité:
![Metamodele](images/meta-model-uml.png?raw=true "Metamodèle")
###Elements du diagrame d'activité:
![Diagram](images/500px-VIATRA2_Examples_A2PN_Activity.png?raw=true "Elements Diagrame d'activité")
###AST:
![AST](images/md-astview.png?raw=true "Ast view")

##Instalation:

###Requires:
- AST
- Papyrus
- Eclipse EMF

##Cloner le projet:
```
cd /xxxxxxxxxx
git clone https://github.com/jimmymunoz/extractiondeworkflow.git extractiondeworkflow
cd extractiondeworkflow
```

###Importer avec Eclipse
- File
- Import
- Existing Projects into Workspace
![Import](images/import-eclipse.png?raw=true "Import")

###Configurer JAR's:

- Click droit Project
- Properties
- Java Build Path
- Selectionner les jars dans le dossier (libstoadd)

![Add Jars](images/eclipse-add-jars.png?raw=true "Add Jars")

###Configurer JRE

Modifier l'attribute de la class main dans le package par defaut:
```
private static String jrePath = "C:\\Program Files\\Java\\jre1.8.0_51\\lib\\rt.jar"; //Windows
private static String jrePath = "/Library/Java/JavaVirtualMachines/jdk1.8.0_91.jdk/Contents/Home/jre/lib/rt.jar"; // OS / Linux
```
http://stackoverflow.com/questions/4681090/how-do-i-find-where-jdk-is-installed-on-my-windows-machine

###Installer Papyrus
https://eclipse.org/papyrus/
https://www.eclipse.org/papyrus/download.html

##Examples code

###AST
Example recuperation des invocations des methodes:
```
public class MethodInvocationVisitor extends ASTVisitor 
{
	List<MethodInvocation> methods = new ArrayList<MethodInvocation>();
	List<SuperMethodInvocation> superMethods = new ArrayList<SuperMethodInvocation>();
	Type returnType;
	
	public boolean visit(MethodInvocation node) {
		methods.add(node);
		//returnType = this.; //.getReturnType2();
		return super.visit(node);
	}
	
	@Override
	public boolean visit(SuperMethodInvocation node) {
		superMethods.add(node);
		return super.visit(node);
	}
	
	public List<MethodInvocation> getMethods() {
		return methods;
	}
	
	public List<SuperMethodInvocation> getSuperMethods() {
		return superMethods;
	}
}
```

###EMF
Methode de creation de diagrame d'activité avec EMF:
```
private void createActivityDiagram() {
	resultModel += "\n---------- createActivityDiagram() -----------------";
	System.out.println("---------- createActivityDiagram() -----------------");
	
	umlModel = UMLFactory.eINSTANCE.createModel();
	String mainKey = activityDiagram.getKeyEntryPoint();
	Map<Integer, ADInstruction> mainHashInstructions = activityDiagram.getHashInstructions(mainKey);
	
	ADMethodInvocation daMethodInvOb = activityDiagram.getActivityInstructions(mainKey);
	
	Integer idNode = getIdNode("init2");
	Integer idActivity = getAndIncrementIdActivity(daMethodInvOb.getInstructionKey());
	parentActivity = (Activity) umlModel.createPackagedElement(daMethodInvOb.getDisplayInstruction(), UMLPackage.eINSTANCE.getActivity());
	Map<String, ActivityNode> mapActivity = new HashMap<String, ActivityNode>();
	listNodes.put(idActivity+"", mapActivity);
	
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
```

###Xpath
Methode de transformation des Commentaires:
```
public void  initialiseOnwedComment()
{		
		
    for (int idx = 0; idx < getOwnedComment().getLength(); idx++) {
    	Element element = (Element) getOwnedComment().item(idx);	    	  
    	element.setAttribute( "xmi:type", "uml:Comment");
	    element.setAttributeNS("http://www.omg.org/XMI", "xmi:id", "OW"+idx);
	    String edgename = element.getAttribute("name");
	    String actname = ((Element) element.getParentNode()).getAttribute("name");
	    String key = "//"+ actname +"/" + edgename;			
	    getHashOwnedComment().put(key, "Ow"+idx);		    
    }	
}
```

##Manual

###Interface
![interface](images/01-interface.png?raw=true "01-interface.png")
###Choisir un fichier
![selectfile](images/02-selectfile.png?raw=true "02-selectfile.png")
###Nomer le fichier de resultat
![name-model](images/03-name-model.png?raw=true "03-name-model.png")
###Message de sucess
![model-saved](images/04-model-saved.png?raw=true "04-model-saved.png")
###Console
![console](images/05-console.png?raw=true "05-console.png")
###Modèle resultat
![model-result](images/06-model-result.png?raw=true "06-model-result.png")
###Visualiser le modèle
![view-model](images/07-view-model.png?raw=true "07-view-model.png")
###Papyrus
Show Perspective -> Papyrus
![open-papyrus-perspective](images/08-open-papyrus-perspective.png?raw=true "08-open-papyrus-perspective.png")
###Papyrus perspective
![papyrus-perspective](images/09-papyrus-perspective.png?raw=true "09-papyrus-perspective.png")
###Creation du modèle papyrus
Model Explorer 
New -> Papyrus Model ->
UML Next -> 
![new-papyrus-model](images/10-new-papyrus-model.png?raw=true "10-new-papyrus-model.png")
###Choisir diagram d'activité
Choose
Activity Diagram
Finish
![select-activity-diagram](images/11-select-activity-diagram.png?raw=true "11-select-activity-diagram.png")
###Nouveau Diagrama à partir du modèle
Click Right -> Main Activity -> New Diagram
![new-activity-diagram](images/12-new-activity-diagram.png?raw=true "12-new-activity-diagram.png")
###Drag and drop les elements à visualiser
Drag and drop the elements into the activity
![drag-and-drop](images/13-drag-and-drop.png?raw=true "13-drag-and-drop.png")
###Click - Select All
Select All (ctrl + A)
![select-all](images/14-select-all.png?raw=true "14-select-all.png")
###Click - Arrange-all
Arrange All
![arrange-all](images/15-arrange-all.png?raw=true "15-arrange-all.png")
###Voir le resultat
![view-result](images/16-view-result.png?raw=true "16-view-result.png")
###Enregistrer le fichier
![save-file](images/17-save-file.png?raw=true "17-save-file.png")
###Resultat
![WorkFlowTest](images/18-WorkFlowTest.png?raw=true "18-WorkFlowTest.png")
