package activitydiagram;

public class ADInstruction extends ADNode {
	protected Integer position;
	protected String flotType = "";
	protected Integer source = -1;
	
	public ADInstruction(String instructionKey, String displayInstruction, String typeNode, Integer position, Integer posParent) {
		super();
		this.instructionKey = instructionKey;
		this.position = position;
		this.posParent = posParent;
		this.typeNode = typeNode;
		this.displayInstruction = displayInstruction;
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
