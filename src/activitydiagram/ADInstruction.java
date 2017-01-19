package activitydiagram;

import java.util.ArrayList;

public class ADInstruction extends ADNode {
	protected Integer position;
	protected String flotType = "";
	protected Integer source = -1;
	protected boolean isFirstThen = false;
	protected boolean isLastThen = false;
	protected boolean isFirstElse = false;
	protected boolean isLastElse = false;
	protected ArrayList<Integer> Listsources;
	
	public ADInstruction(String instructionKey, String displayInstruction, String typeNode, Integer position, Integer posParent) {
		super();
		this.instructionKey = instructionKey;
		this.position = position;
		this.posParent = posParent;
		this.typeNode = typeNode;
		this.displayInstruction = displayInstruction;
		this.Listsources = new ArrayList<Integer>();
	}
	
	public void addListSources(Integer s){
		this.Listsources.add(s);
	}
	
	public ArrayList<Integer> getListsources() {
		return Listsources;
	}

	public void setListsources(ArrayList<Integer> listsources) {
		Listsources = listsources;
	}
	
	public boolean isFirstThen() {
		return isFirstThen;
	}

	public boolean isLastThen() {
		return isLastThen;
	}

	public boolean isFirstElse() {
		return isFirstElse;
	}

	public boolean isLastElse() {
		return isLastElse;
	}

	public void setFirstThen(boolean isFirstThen) {
		this.isFirstThen = isFirstThen;
	}

	public void setLastThen(boolean isLastThen) {
		this.isLastThen = isLastThen;
	}

	public void setFirstElse(boolean isFirstElse) {
		this.isFirstElse = isFirstElse;
	}

	public void setLastElse(boolean isLastElse) {
		this.isLastElse = isLastElse;
	}

	
	
	public Integer getSource() {
		return source;
	}

	public void setSource(Integer source) {
		this.source = source;
	}
	
	public String getFlotType() {
		return flotType;
	}

	public void setFlotType(String flotType) {
		this.flotType = flotType;
	}
	
	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	
	
	
}
