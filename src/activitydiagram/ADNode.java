package activitydiagram;

public abstract class ADNode {
	
	protected Integer posParent = -1;
	protected String typeNode = "";
	
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
