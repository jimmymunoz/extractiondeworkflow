#Extraction de workflow
=======

Generation d'un diagrame d'activité à partir d'un code source.

##Processus:
	
- Seleccion de code source java
- Selection du point d'entré 
- Recuperation des information avec AST (Abstract Syntax Tree) de Java
- Generation du modèle UML avec EMF (Eclipse Modeling Framework) 
- Transformation du modèle UML au format de Papyrus
- Visualiser le diagramme avec Papyrus

###Processus:
![Processus](images/processus.png?raw=true "Processus")
###Diagrame Activité:
![AD](images/Diagram-Activite.png?raw=true "Diagram-Activite")
###Metamodèle:
![Metamodele](images/meta-model-uml.png?raw=true "Metamodèle")
###Elements diagram activité:
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


##Screenshots

###Interface:
![Index Action](src/Jimmy/BlogBundle/Resources/public/screentshots/home.png?raw=true "Home")
###Modèle:
![Index Action](src/Jimmy/BlogBundle/Resources/public/screentshots/responsive.png?raw=true "Home Responsive")
###Diagrame:
![Edit Action](src/Jimmy/BlogBundle/Resources/public/screentshots/pagination.png?raw=true "Pagination")
