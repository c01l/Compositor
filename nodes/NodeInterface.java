package nodes;

public abstract class NodeInterface {
	private Class<?> clazz;
	
	public NodeInterface(Class<?> c) {
		this.clazz = c;
	}
	
	public Class<?> getType() {
		return this.clazz;
	}
}
