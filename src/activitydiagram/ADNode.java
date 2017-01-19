package activitydiagram;

public abstract class ADNode {
	
	protected Integer posParent = -1;
	protected String typeNode = "";
	protected String displayInstruction = "";
	protected String instructionKey;
	
	public String getDisplayInstruction() {
		return displayInstruction;
	}
	public void setDisplayInstruction(String displayInstruction) {
		this.displayInstruction = displayInstruction;
	}
	public Integer getPosParent() {
		return posParent;
	}
	public void setPosParent(Integer posParent) {
		this.posParent = posParent;
	}
	public String getTypeNode() {
		return typeNode;
	}
	public void setTypeNode(String typeNode) {
		this.typeNode = typeNode;
	}
	public String getInstructionKey() {
		return instructionKey;
	}
	public void setInstructionKey(String instructionKey) {
		this.instructionKey = instructionKey;
	}
}
