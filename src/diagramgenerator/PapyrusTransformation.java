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
	private HashMap<String,String> hashOwnedComment;
	private String resultTransformation = "";
	
	private Document doc; 
	private XPath xpath;
	private NodeList edges ;
	private NodeList nodes;
	private NodeList packageable;
	private NodeList inputValue;
	private NodeList outputtValue;
	private NodeList ownedComment;
	private NodeList umlModel;
	private Transformer xformer;
	private String fileModelPathLoad;
	private String fileModelPathSave;
    
	public PapyrusTransformation(String fileModelPathLoad, String fileModelPathSave){
		try {
			 //resultTransformation += "\n---------------PapyrusTransformation()-----------------"; 
			 //resultTransformation += "\n load(" + fileModelPathLoad + ")";
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
			 this.edges = (NodeList) xpath.evaluate("//edge", this.doc, XPathConstants.NODESET);		   
			// this.umlModel = (NodeList) xpath.evaluate("//uml:Model", this.doc,XPathConstants.NODESET);
			 this.nodes = (NodeList) xpath.evaluate("//node", this.doc,XPathConstants.NODESET);
			 this.packageable = (NodeList) xpath.evaluate("//packagedElement", this.doc, XPathConstants.NODESET);
			 this.inputValue = (NodeList) xpath.evaluate("//node/inputValue", this.doc, XPathConstants.NODESET);		     
			 this.outputtValue = (NodeList) xpath.evaluate("//node/outputValue", this.doc, XPathConstants.NODESET);
		     this.ownedComment = (NodeList) xpath.evaluate("//packagedElement/ownedComment", this.doc,XPathConstants.NODESET);		 
			// this.umlModel = (NodeList) xpath.evaluate("//node/outputValue", this.doc,XPathConstants.NODESET);
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
			 changeNodetoStructureNode();
			 this.xformer.transform(new DOMSource(doc), new StreamResult(new File(fileModelPathSave)));
			 //resultTransformation += "\n save(" + fileModelPathSave + ")";
			 resultTransformation += " Model Saved in:  (" + fileModelPathSave + ")\n";
			  //  removeAttributUmlModel();
		} catch (XPathExpressionException | SAXException | ParserConfigurationException
				| TransformerFactoryConfigurationError | TransformerException e) {
			e.printStackTrace();
			resultTransformation += "Error: " + e.toString() + "\n";
		} catch (IOException e) {
			resultTransformation += "Error: " + e.toString() + "\n";
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
				resultTransformation += "Error: " + e.toString() + "\n";
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
		    	List<String> items = Arrays.asList(source.split("\\s* \\s*"));
			    for(int i =0; i < items.size(); i++){
			    	String item = items.get(i);
			    	if( getHashNodes().containsKey(java.net.URLDecoder.decode(item, "UTF-8"))){			   
			    		items.set(i, getHashNodes().get(java.net.URLDecoder.decode(item, "UTF-8")));   				    				   			    
			    	}
			    }
			    element.setAttribute("source", String.join(" ", items));
			    
		    }
		    String target = element.getAttribute("target");
		    if( !target.equals("")){
		    	List<String> items = Arrays.asList(target.split("\\s* \\s*"));
			    for(int i =0; i < items.size(); i++){
			    	String item = items.get(i);
			    	if( getHashNodes().containsKey(java.net.URLDecoder.decode(item, "UTF-8"))){			    	
			    		items.set(i, getHashNodes().get(java.net.URLDecoder.decode(item, "UTF-8")));
			    	}
			    }
			    element.setAttribute("target", String.join(" ", items));
			}
	    }
	}
	public void extractNodes() throws IOException
	{
	    for (int idx = 0; idx < nodes.getLength(); idx++) {
	    	Element element = (Element) nodes.item(idx);
		  
	    	String incomings = element.getAttribute("incoming");
		    if( ! incomings.equals("")){
			    List<String> items = Arrays.asList(incomings.split("\\s* \\s*"));
			    for(int i =0; i < items.size(); i++){
			    	String item = items.get(i);
			    	item = java.net.URLDecoder.decode(item, "UTF-8");
			    	if( hashEdges.containsKey(item) ){
			    		items.set(i, hashEdges.get(item));
			    	}
			    }
			    element.setAttribute("incoming", String.join(" ", items));
			}
		    String outgoing = element.getAttribute("outgoing");
		    if( ! outgoing.equals("")){
		    	List<String> items = Arrays.asList(outgoing.split("\\s* \\s*"));
		    	for(int i=0 ; i < items.size();i++){
		    		String item = items.get(i);
		    		
		    		if(hashEdges.containsKey(java.net.URLDecoder.decode(item, "UTF-8"))){
		    			items.set(i, hashEdges.get(java.net.URLDecoder.decode(item, "UTF-8")));
		    		}
		    	}
		    	element.setAttribute("outgoing", String.join(" ", items));				    
		    }		    			    
		    		
	    }
	}
	public void changeNodetoStructureNode()
	{
		NodeList nodesachanger;
		try {
			nodesachanger = (NodeList) xpath.evaluate("//*[@type='uml:StructuredActivityNode']", this.doc,XPathConstants.NODESET);
			for (int i = 0; i < nodesachanger.getLength(); i++) {
				Element element = (Element) getNodes().item(i);	
				doc.renameNode(nodesachanger.item(i), null, "structuredNode");
			}
		} catch (XPathExpressionException e) {
			resultTransformation += "Error: " + e.toString() + "\n";
			e.printStackTrace();
		}
	}
		

	public void changeNameNod() {
		NodeList nodesachanger = (NodeList) doc.getElementsByTagName("ownedNode");
		    for (int i = 0; i < nodesachanger.getLength(); i++) {  			    
		      doc.renameNode(nodesachanger.item(i), null, "node");
		}
	}
	
	public String getAbsoluteNameWithParents(Node node, String oldname)
	{
		String name = ((Element) node).getAttribute("name");
		
		if( ! oldname.equals("") ){
			name =  name + "/" + oldname;
		}
		if( node.getParentNode() != null ){
			
			if ( node.getParentNode().hasAttributes() ) {
				name =  getAbsoluteNameWithParents(node.getParentNode(), name);
			}
		}
		return name;
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
		HashMap<String, Integer> hashNameNodes = new HashMap<String, Integer>();
		HashMap<Integer, Integer> hashNodesTmp = new HashMap<Integer, Integer>(); 
		for (int i = 0; i < getEdges().getLength(); i++) {
			Element element = (Element) getEdges().item(i);
			String edgename = element.getAttribute("name");
			int j = 0;
			if( hashNameNodes.containsKey(edgename) ){
				j = hashNameNodes.get(edgename);
				j++;
			}
			hashNameNodes.put(edgename, j);
			hashNodesTmp.put(i, j);
		}
		
	    for (int idx = 0; idx < getEdges().getLength(); idx++) {
	    	Element element = (Element) getEdges().item(idx);
		    element.setAttributeNS("http://www.omg.org/XMI", "xmi:id", "E"+idx);
		    String edgename = element.getAttribute("name");
		    String tmp = (String) (( hashNodesTmp.get(idx) == 0 )? ".0" : "." + hashNodesTmp.get(idx));
		    
		    String actname = ((Element) element.getParentNode()).getAttribute("name");
		    String key = "//"+ actname +"/" + edgename;
		    
		    key = "/" + getAbsoluteNameWithParents(element, "") + tmp;
		    if( edgename.equals("") ){
		    	edgename = "@edge";
		    	key = "/" + getAbsoluteNameWithParents(element.getParentNode(), edgename) + tmp;
		    }
		    
		    getHashEdges().put(key, "E"+idx);
		    
	    }
	}
	
	public void initialiseNodes()
	{
		String key1 ="";
		String key= "";
		HashMap<String, Integer> hashNameNodes = new HashMap<String, Integer>();
		HashMap<Integer, Integer> hashNodesTmp = new HashMap<Integer, Integer>(); 
		for (int i = 0; i < getNodes().getLength(); i++) {
			Element element = (Element) getNodes().item(i);
			String nodename = element.getAttribute("name");
			int j = 0;
			if( hashNameNodes.containsKey(nodename) ){
				j = hashNameNodes.get(nodename);
				j++;
			}
			hashNameNodes.put(nodename, j);
			hashNodesTmp.put(i, j);
			
		}
	    for (int idx = 0; idx < getNodes().getLength(); idx++) {
	    	Element element = (Element) getNodes().item(idx);
		    element.setAttributeNS("http://www.omg.org/XMI", "xmi:id", "N"+idx);
		    String nodename = element.getAttribute("name");
		    String actname = "/" + getAbsoluteNameWithParents(element, "");
		    String parentname = ((Element) element.getParentNode()).getAttribute("name");
		    String tmp = (String) (( hashNodesTmp.get(idx) == 0 )? "" : "." + hashNodesTmp.get(idx));
		    key1 = actname + tmp;
		    
			try {
				key = java.net.URLDecoder.decode(key1, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				resultTransformation += "Error: " + e.toString() + "\n";
				e.printStackTrace();
			}
		    getHashNodes().put(key, "N"+idx);
		    getHashParent().put("N"+idx,parentname);
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
		    		items.set(i, getHashNodes().get(java.net.URLDecoder.decode(item, "UTF-8")));
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
		}
	}
	
	public void addNameNodePackageable(){
		for (int idx = 0; idx < this.packageable.getLength(); idx++) {
			List <String> listNode = new ArrayList<String>();
		 	List <String> listGroupNodes = new ArrayList<String>();
	    	Element element = (Element) this.packageable.item(idx);
	    	String namepackage = element.getAttribute("name");
	    	 
	    	for (int i = 0; i < getNodes().getLength(); i++) {
				Element element2 = (Element) getNodes().item(i);
				String nodeid = element2.getAttribute("xmi:id");
				String namepackage2 = getHashParent().get(nodeid);
				if( namepackage.equals(namepackage2) ){
					listNode.add(nodeid);
					
					if( element2.getAttribute("xmi:type").equals("uml:StructuredActivityNode") ){
						listGroupNodes.add(nodeid);
					}
				}
			}
	    	element.setAttribute( "node", String.join(" ", listNode));
			element.setAttribute( "group", String.join(" ", listGroupNodes));
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

	public String getResultTransformation() {
		return resultTransformation;
	}
	
}
