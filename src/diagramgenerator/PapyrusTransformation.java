package diagramgenerator;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class PapyrusTransformation {
	public HashMap<String, String> hashEdges ;
	public HashMap<String, String> hashNodes;
	public HashMap<String, String> hashPackageable;
	public HashMap<String, String> hashInputValue;
	public HashMap<String, String> hashOutputtValue;
	public HashMap<String, String> hashUmlModel;
	

	
	public Document doc; 
	XPath xpath;
    NodeList edges ;
    NodeList nodes;
    NodeList packageable;
    NodeList inputValue;
    NodeList outputtValue;
    NodeList umlModel;
    Transformer xformer;
	public PapyrusTransformation() throws SAXException, IOException, ParserConfigurationException, XPathExpressionException, TransformerFactoryConfigurationError, TransformerException {
		 doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource("model/Xmltopars.xmi"));
		 xpath = XPathFactory.newInstance().newXPath();
		 changeNameNod();
		 this.hashEdges = new  HashMap<String, String>();
		 this.hashNodes = new  HashMap<String, String>();
		 this.hashPackageable = new  HashMap<String, String>();
		 this.hashInputValue = new  HashMap<String, String>();
		 this.hashOutputtValue = new  HashMap<String, String>();
		 //this.hashUmlModel = new  HashMap<String, String>();
		 this.xformer = TransformerFactory.newInstance().newTransformer();
		 this.edges = (NodeList) xpath.evaluate("//packagedElement/edge", this.doc, XPathConstants.NODESET);		   
		// this.umlModel = (NodeList) xpath.evaluate("//uml:Model", this.doc,XPathConstants.NODESET);
		 this.nodes = (NodeList) xpath.evaluate("//packagedElement/node", this.doc,XPathConstants.NODESET);
		 this.packageable = (NodeList) xpath.evaluate("//packagedElement", this.doc, XPathConstants.NODESET);
		 this.inputValue = (NodeList) xpath.evaluate("//node/inputValue", this.doc, XPathConstants.NODESET);		     
		 this.outputtValue = (NodeList) xpath.evaluate("//node/outputValue", this.doc, XPathConstants.NODESET);
		 this.umlModel = (NodeList) xpath.evaluate("//node/outputValue", this.doc,XPathConstants.NODESET);
		 initialiseEdge();
	     initialiseNodes();
	     initialiseEdge();
	     initialisePackageable();
	     initialisInputValue();
	     addNameNodePackageable();
	     extractNodes();
	     extractEdges();
		      	
	     
		
	    
		
	
		
	   
	    

	    
	    
	    this.xformer.transform(new DOMSource(doc), new StreamResult(new File("model/ActivityModelResult.uml")));
	  //  removeAttributUmlModel();
	    
	    
	}

	
		
	public void extractEdges()
	{
	    for (int idx = 0; idx < getEdges().getLength(); idx++) {
	    	Element element = (Element) getEdges().item(idx);
		  
		    String source = element.getAttribute("source");
		    if( !source.equals("")){
		    	//System.out.print(getHashNodes().values());
			    List<String> items = Arrays.asList(source.split("\\s* \\s*"));
			    for(int i =0; i < items.size(); i++){
			    	String item = items.get(i);
			    //	S(this.hashNodes.values());
			    //	S(item);
			    	
			    	if( getHashNodes().containsKey(item) ){			   
			    		System.out.println(getHashNodes().get(item));
			    		items.set(i, getHashNodes().get(item));   				    				   			    
			    	}
			    }
			    element.setAttribute("source", String.join(" ", items));
			    
		    }
		    String target = element.getAttribute("target");
		    if( !target.equals("")){
		    	System.out.println(getHashNodes().values());
			    List<String> items = Arrays.asList(target.split("\\s* \\s*"));
			    for(int i =0; i < items.size(); i++){
			    	String item = items.get(i);
			    	//S(this.hashNodes.values());
			    	//S(item);
			    	if( getHashNodes().containsKey(item) ){
			    		
			    		
			    		//S(item);
			    		//S(hashNodes.get(item));
			    		items.set(i, getHashNodes().get(item));
			    	}
			    }
			    element.setAttribute("target", String.join(" ", items));
			   
			    
			    
		    }
		    //element.setAttribute("id","N"+idx);
	    }
	}
	public void extractNodes()
	{
	    for (int idx = 0; idx < nodes.getLength(); idx++) {
	    	Element element = (Element) nodes.item(idx);
		    //element.setAttributeNS("http://www.omg.org/XMI", "xmi:id", "N"+idx);
		  
		    String incomings = element.getAttribute("incoming");
		    if( ! incomings.equals("")){
			    List<String> items = Arrays.asList(incomings.split("\\s* \\s*"));
			    for(int i =0; i < items.size(); i++){
			    	String item = items.get(i);
			    	if( hashEdges.containsKey(item) ){
			    		//S(item);
			    		//S(hashEdges.get(item));
			    		
			    		items.set(i, hashEdges.get(item));
			    	}
			    }
			    element.setAttribute("incoming", String.join(" ", items));
			    //element.setAttribute("id","N"+idx);
		    }
		    String outgoing = element.getAttribute("outgoing");
		    if( ! outgoing.equals("")){
		    	List<String> items = Arrays.asList(outgoing.split("\\s* \\s*"));
		    	for(int i=0 ; i < items.size();i++){
		    		String item = items.get(i);
		    		
		    		if(hashEdges.containsKey(item)){
		    			items.set(i,hashEdges.get(item));
		    		}
		    	}
		    	element.setAttribute("outgoing", String.join(" ", items));
	
			    
		    }

		    	
		    
		    		
	    }
	}

	public void changeNameNod() {
		NodeList nodesachanger = (NodeList) doc.getElementsByTagName("ownedNode");
		    for (int i = 0; i < nodesachanger.getLength(); i++) {  			    
		      doc.renameNode(nodesachanger.item(i), null, "node");
		}
	}

	public void initialisePackageable()
	{
		 for (int idx = 0; idx < this.packageable.getLength(); idx++) {
		    	Element element = (Element) this.packageable.item(idx);
			    element.setAttributeNS("http://www.omg.org/XMI", "xmi:id", "P"+idx);
			    String edgename = element.getAttribute("name");
			    String actname = ((Element) element.getParentNode()).getAttribute("name");
			    String key = "//"+ actname +"/" + edgename;
			    this.hashPackageable.put(key, "P"+idx);
		 }
	}
	public void initialiseEdge()
	{		
		initialiseNodes();
	    for (int idx = 0; idx < getEdges().getLength(); idx++) {
	    	Element element = (Element) getEdges().item(idx);
		    element.setAttributeNS("http://www.omg.org/XMI", "xmi:id", "E"+idx);
		    String edgename = element.getAttribute("name");
		    String actname = ((Element) element.getParentNode()).getAttribute("name");
		    String key = "//"+ actname +"/" + edgename;
		    getHashEdges().put(key, "E"+idx);
		    
	    }
	//  System.out.print(getHashNodes().values());
	   // System.out.print(getHashEdges().values());
	}
	public void initialiseNodes()
	{
	
		   for (int idx = 0; idx < getNodes().getLength(); idx++) {
		    	Element element = (Element) getNodes().item(idx);
			    element.setAttributeNS("http://www.omg.org/XMI", "xmi:id", "N"+idx);
			    String nodename = element.getAttribute("name");
			    String actname = ((Element) element.getParentNode()).getAttribute("name");
			    String key = "//"+ actname +"/" + nodename;
			    getHashNodes().put(key, "N"+idx);
			    //
		    }
		   
	}
	public void initialisInputValue()
	{
		
		   for (int idx = 0; idx < this.inputValue.getLength(); idx++) {
		    	Element element = (Element) this.inputValue.item(idx);		    	 
			    element.setAttributeNS("http://www.omg.org/XMI", "xmi:id", "IN"+idx);
			    element.setAttributeNS("http://www.omg.org/XMI", "xmi:type", "uml:InputPin");
			    String nodename = element.getAttribute("name");
			    String actname = ((Element) element.getParentNode()).getAttribute("name");
			    String key = "//"+ actname +"/" + nodename;
			    this.hashInputValue.put(key, "IN"+idx);
			   // System.out.print();
		    }
	}

	public void initialisOutputtValue()
	{

		   for (int idx = 0; idx < this.outputtValue.getLength(); idx++) {
		    	Element element = (Element) this.outputtValue.item(idx);		    	 
			    element.setAttributeNS("http://www.omg.org/XMI", "xmi:id", "OU"+idx);
			    element.setAttributeNS("http://www.omg.org/XMI", "xmi:type", "uml:OutputPin");
			    String nodename = element.getAttribute("name");
			    String actname = ((Element) element.getParentNode()).getAttribute("name");
			    String key = "//"+ actname +"/" + nodename;
			    this.hashOutputtValue.put(key, "OU"+idx);
			   // System.out.print();
		    }
	}
	public void addNameNodePackageable(){
		String namePackagebale ="";
		Collection<String> hashnode = getHashNodes().values();
		
		for (String a : hashnode)
		{
			//String actname = ((Element) element.getParentNode()).getAttribute("name");
			namePackagebale += a +" ";
		}
		
			
			
			 for (int idx = 0; idx < this.packageable.getLength(); idx++) {
			    	Element element = (Element) this.packageable.item(idx);
				    element.setAttribute( "node", namePackagebale);
	    	//String uml = element.getAttribute("xsi:type");
	    	//element.removeAttribute("xsi:type");
	    	//element.setAttribute("name", namePackagebale);	    
	    	//element.removeAttributeNS("http://www.omg.org/XMI", "xsi:type");	    		    		    		  	    
	    	//element.setAttributeNS("http://www.omg.org/XMI", "xmi:type", namePackagebale);

	    	

		}
	}


	public HashMap<String, String> getHashInputValue() {
		return hashInputValue;
	}
	public HashMap<String, String> getHashOutputtValue() {
		return hashOutputtValue;
	}
	public HashMap<String, String> getHashUmlModel() {
		return hashUmlModel;
	}
	public NodeList getInputValue() {
		return inputValue;
	}
	public NodeList getOutputtValue() {
		return outputtValue;
	}
	public NodeList getUmlModel() {
		return umlModel;
	}
	public HashMap<String, String> getHashEdges() {
		return hashEdges;
	}



	public HashMap<String, String> getHashNodes() {
		return hashNodes;
	}



	public HashMap<String, String> getHashPackageable() {
		return hashPackageable;
	}



	public Document getDoc() {
		return doc;
	}



	public XPath getXpath() {
		return xpath;
	}



	public NodeList getEdges() {
		return edges;
	}



	public NodeList getNodes() {
		return nodes;
	}



	public NodeList getPackageable() {
		return packageable;
	}
	
	
	
}
