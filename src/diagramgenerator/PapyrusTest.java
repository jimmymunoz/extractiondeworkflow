package diagramgenerator;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.papyrus.infra.core.resource.ModelMultiException;
//import org.eclipse.papyrus.infra.core.editor.IMultiDiagramEditor;
//import org.eclipse.papyrus.infra.core.extension.commands.IModelCreationCommand;
import org.eclipse.papyrus.infra.core.resource.ModelSet;
import org.eclipse.papyrus.infra.core.resource.ModelsReader;
import org.eclipse.papyrus.infra.core.resource.sasheditor.DiModel;
import org.eclipse.papyrus.infra.core.sashwindows.di.service.IPageManager;
import org.eclipse.papyrus.infra.core.services.ExtensionServicesRegistry;
import org.eclipse.papyrus.infra.core.services.ServiceException;
import org.eclipse.papyrus.infra.core.services.ServicesRegistry;
import org.eclipse.papyrus.uml.diagram.wizards.category.DiagramCategoryDescriptor;
import org.eclipse.papyrus.uml.diagram.wizards.category.DiagramCategoryRegistry;
import org.eclipse.papyrus.uml.diagram.wizards.command.NewPapyrusModelCommand;
import org.eclipse.papyrus.uml.tools.model.UmlModel;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.util.UMLUtil;

public class PapyrusTest {
	
	public static void test(Model model){
		createModels("model", model);
		/*
		try {
			//testSave();
			
		} catch (ModelMultiException | IOException | CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
	}
	
	
	public static void testSave() throws ModelMultiException, IOException, CoreException{
		ModelSet modelSet = new ModelSet();
		String path = "model";
		
		ModelsReader reader = new ModelsReader(); //Standard ModelsReader for Di + UML + Notation
		reader.readModel(modelSet);
		
		String sourceUmlPath = "model/model_empty.uml";
		String diFilePath = path+".di";
		String umlFilePath = path+".uml";

		URI diFileURI =  URI.createFileURI(diFilePath);
		URI umlFileURI = URI.createFileURI(umlFilePath);
		
		URI diPResURI =  URI.createPlatformResourceURI(diFilePath, true);
		URI umlPResURI =  URI.createPlatformResourceURI(umlFilePath, true);
		
		URI resolvedFile = CommonPlugin.resolve(diFileURI);
		IFile ifile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(resolvedFile.toFileString()));

		modelSet.createModels(diPResURI);
		
		/*
		ServicesRegistry registry = new ExtensionServicesRegistry(org.eclipse.papyrus.infra.core.Activator.PLUGIN_ID);
		try {
			registry.add(ModelSet.class, Integer.MAX_VALUE, modelSet);
			registry.startRegistry();
		} catch (ServiceException ex) {
			//Ignore
		}
		 */
		
		try {
			modelSet.save(new NullProgressMonitor());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		modelSet.loadModels(umlPResURI);
		

		URI UmlFileResURI = CommonPlugin.resolve(umlFileURI);
		IFile UmlFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(UmlFileResURI.toFileString()));
		InputStream  is = new FileInputStream(sourceUmlPath);
		
		modelSet.save(new NullProgressMonitor());
		UmlFile.setContents(is, true, false, new NullProgressMonitor());
		modelSet.loadModels(umlPResURI);

		//IModelCreationCommand creationCommand = new CreateUMLModelCommand();
		//creationCommand.createModel(modelSet);
	}
	
	private static void createModels(String path, Model model) {
		String sourceUmlPath = "model/model_empty.uml";
		String diFilePath = path+".di";
		String umlFilePath = path+".uml";
		URI diFileURI =  URI.createFileURI(diFilePath);
		URI umlFileURI = URI.createFileURI(umlFilePath);
		URI diPResURI =  URI.createPlatformResourceURI(diFilePath, true);
		URI umlPResURI =  URI.createPlatformResourceURI(umlFilePath, true);
		URI UmlFileResURI = CommonPlugin.resolve(umlFileURI);
    	UmlModel umlModel = new UmlModel();
    	DiModel diModel = new DiModel();
    	//NotationModel notationModel = new NotationModel();
    	ModelSet modelset = new ModelSet();
		try {
			modelset.registerModel(umlModel);
			modelset.registerModel(diModel);
			//modelset.registerModel(notationModel);
			modelset.createModels(umlFileURI);
			InputStream  is = new FileInputStream(sourceUmlPath);
			
			Resource resource = new ResourceSetImpl().createResource(umlFileURI);
		    resource.getContents().add(model);
			
			resource.save(null);
			//IFile UmlFile = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(UmlFileResURI.toFileString()));
			//modelset.save(new NullProgressMonitor());
			
			//UmlFile.setContents(is, true, false, new NullProgressMonitor());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public static void testModel() throws IOException, ServiceException{
		//Starting the registry
		ServicesRegistry registry = new ExtensionServicesRegistry(org.eclipse.papyrus.infra.core.Activator.PLUGIN_ID);
		
		try {
			// have to create the model set and populate it with the DI model
			// before initializing other services that actually need the DI
			// model, such as the SashModel Manager service
			registry.startServicesByClassKeys(ModelSet.class);
		} catch (ServiceException ex) {
			// Ignore this exception: some services may not have been loaded,
			// which is probably normal at this point
		}
		
		ModelSet modelSet = registry.getService(ModelSet.class);
		//The modelname is the name of your model
		String modelname = "model";
		URI diPResURI =  URI.createPlatformResourceURI(modelname+".di", true);
		
		RecordingCommand command = new NewPapyrusModelCommand(modelSet, diPResURI);
		//modelSet.getTransactionalEditingDomain().ge .getCommandStack().execute(command);

		//Initializing the registry
		try {
			registry.startRegistry();
		} catch (ServiceException ex) {
			// Ignore this exception: some services may not have been loaded,
			// which is probably normal at this point
		}
		registry.getService(IPageManager.class);

		modelSet.save(new NullProgressMonitor());

		//Creating specific model
		
		Map<String, DiagramCategoryDescriptor> categories = DiagramCategoryRegistry.getInstance().getDiagramCategoryMap();
		String key = "sysml"; //the three possibilities are: uml, sysml and profile
		/*
		IModelCreationCommand creationCommand = categories.get("uml").getCommand();
		creationCommand.createModel(modelSet);
		*/
	}
}
