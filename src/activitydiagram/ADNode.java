package activitydiagram;

public abstract class ADNode {
	
	protected Integer posParent = -1;
	protected String typeNode = "";
	protected String displayInstruction = "";
	
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
}
