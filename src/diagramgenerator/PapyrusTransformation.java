package diagramgenerator;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

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

import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class PapyrusTransformation {
	private HashMap<String, String> hashEdges ;
	private HashMap<String, String> hashNodes;
	private HashMap<String, String> hashPackageable;
	private HashMap<String, String> hashInputValue;
	private HashMap<String, String> hashOutputtValue;
	private HashMap<String, String> hashUmlModel;
	private HashMap<String,String> hashParent;
	public HashMap<String,String> hashOwnedComment;

	
	private Document doc; 
	private XPath xpath;
	private NodeList edges ;
	private NodeList nodes;
	private NodeList packageable;
	private NodeList inputValue;
	private NodeList outputtValue;
	private NodeList umlModel;
	private Transformer xformer;
	private String fileModelPathLoad;
	private String fileModelPathSave;
    
	public PapyrusTransformation(String fileModelPathLoad, String fileModelPathSave){
		try {
			 doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(fileModelPathLoad));
			 xpath = XPathFactory.newInstance().newXPath();
			 changeNameNod();
			 this.fileModelPathLoad = fileModelPathLoad;
			 this.fileModelPathSave = fileModelPathSave;
			 this.hashEdges = new  HashMap<String, String>();
			 this.hashNodes = new  HashMap<String, String>();
			 this.hashPackageable = new  HashMap<String, String>();
			 this.hashInputValue = new  HashMap<String, String>();
			 this.hashOutputtValue = new  HashMap<String, String>();
			 this.hashParent =  new  HashMap<String, String>();
		     this.hashOwnedComment =  new  HashMap<String, String>();
			 //this.hashUmlModel = new  HashMap<String, String>();
			 this.xformer = TransformerFactory.newInstance().newTransformer();
			 this.edges = (NodeList) xpath.evaluate("//packagedElement/edge", this.doc, XPathConstants.NODESET);		   
			// this.umlModel = (NodeList) xpath.evaluate("//uml:Model", this.doc,XPathConstants.NODESET);
			 this.nodes = (NodeList) xpath.evaluate("//packagedElement/node", this.doc,XPathConstants.NODESET);
			 this.packageable = (NodeList) xpath.evaluate("//packagedElement", this.doc, XPathConstants.NODESET);
			 this.inputValue = (NodeList) xpath.evaluate("//node/inputValue", this.doc, XPathConstants.NODESET);		     
			 this.outputtValue = (NodeList) xpath.evaluate("//node/outputValue", this.doc, XPathConstants.NODESET);
		     this.ownedComment = (NodeList) xpath.evaluate("//packagedElement/ownedComment", this.doc,XPathConstants.NODESET);		 
			 this.umlModel = (NodeList) xpath.evaluate("//node/outputValue", this.doc,XPathConstants.NODESET);
			 //encodEdge();
			 initialiseEdge();
			 initialiseNodes();
			 initialiseEdge();
			 initialisePackageable();
			 initialisInputValue();
	         initialiseOnwedComment();
	         initialisInputValue();
	         annotatedEelemenOwnedComment();
			 addNameNodePackageable();
			 extractNodes();
			 extractEdges();
			 	    
			 this.xformer.transform(new DOMSource(doc), new StreamResult(new File(fileModelPathSave)));
			  //  removeAttributUmlModel();
		} catch (XPathExpressionException | SAXException | ParserConfigurationException
				| TransformerFactoryConfigurationError | TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
	
	public void encodEdge()
	{
		for (int idx = 0; idx < getEdges().getLength(); idx++)
		{
			Element element = (Element) getEdges().item(idx);
			String newtarget ="";
		    String target = element.getAttribute("target");
		    try {
				newtarget = java.net.URLDecoder.decode(target, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    element.setAttribute("target", newtarget);
		}
	}
	
		
	public void extractEdges() throws UnsupportedEncodingException
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
			    	
			    	if( getHashNodes().containsKey(java.net.URLDecoder.decode(item, "UTF-8"))){			   
			    		System.out.println(getHashNodes().get(java.net.URLDecoder.decode(item, "UTF-8")));
			    		items.set(i, getHashNodes().get(java.net.URLDecoder.decode(item, "UTF-8")));   				    				   			    
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
			    	
			    	if( getHashNodes().containsKey(java.net.URLDecoder.decode(item, "UTF-8"))){			    	
			    		items.set(i, getHashNodes().get(java.net.URLDecoder.decode(item)));
			    	}
			    }
			    
			    element.setAttribute("target", String.join(" ", items));
			   
			    
			    
		    }
		    //element.setAttribute("id","N"+idx);
	    }
	}
	public void extractNodes() throws IOException
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
		    		
		    		if(hashEdges.containsKey(java.net.URLDecoder.decode(item, "UTF-8"))){
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
			String key1 ="";
			String key= "";
		   for (int idx = 0; idx < getNodes().getLength(); idx++) {
		    	Element element = (Element) getNodes().item(idx);
			    element.setAttributeNS("http://www.omg.org/XMI", "xmi:id", "N"+idx);
			    String nodename = element.getAttribute("name");
			    String actname = ((Element) element.getParentNode()).getAttribute("name");
			    //String key1 = "//"+ actname +"/" + nodename;
			    
			     key1 = "//"+ actname +"/" + nodename;
			    
				try {
					key = java.net.URLDecoder.decode(key1, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    getHashNodes().put(key, "N"+idx);
			    getHashParent().put("N"+idx,actname);
			    
			    //
		    }
		   
	}
	
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
	public void annotatedEelemenOwnedComment() throws UnsupportedEncodingException	
	{		
		for (int idx = 0; idx < getOwnedComment().getLength(); idx++) {
			Element element = (Element) getOwnedComment().item(idx);		
	    String annotatedElement = element.getAttribute("annotatedElement");	    
	    	List<String> items = Arrays.asList(annotatedElement.split("\\s* \\s*"));
	    	for(int i =0; i < items.size(); i++){
		    	String item = items.get(i);		    	
		    	if( getHashNodes().containsKey(java.net.URLDecoder.decode(item, "UTF-8"))){			    	
		    		items.set(i, getHashNodes().get(java.net.URLDecoder.decode(item)));
		    	}
		    }
		    
		    element.setAttribute("annotatedElement", String.join(" ", items));
	
		}
	}
	public HashMap<String, String> getHashOwnedComment() {
		return hashOwnedComment;
	}

	public NodeList getOwnedComment() {
		return ownedComment;
	}

	public HashMap<String, String> getHashParent() {
		return hashParent;
	}

	public Transformer getXformer() {
		return xformer;
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
			 for (int idx = 0; idx < this.packageable.getLength(); idx++) {
				 String key ="";
				 String value ="";
				 	List <String> listNode = new ArrayList<String>();
			    	Element element = (Element) this.packageable.item(idx);
			    	Collection<String> hashnodeparent = getHashParent().keySet();			    	
			    	String namepackage = element.getAttribute("name");			    										
						for (Entry<String, String> e : getHashParent().entrySet()) {
						    key = e.getKey();
						    value = e.getValue();
						    if (value.equals(namepackage))
						    {
						    	listNode.add(key);
						    }
						}
				    element.setAttribute( "node", String.join(" ", listNode));	    	
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
