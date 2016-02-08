package nodes;

import java.util.LinkedList;

public class NodeOutputInterface extends NodeInterface {

	private Object value;
	private LinkedList<NodeInputInterface> connections;

	public NodeOutputInterface(Class<?> type, Object value) {
		super(type);
		this.value = value;
		
		this.connections = new LinkedList<>();
	}

	public Object getValue() {
		return this.getType().cast(this.value);
	}

	public void setValue(Object value) {
		if (this.getType().isInstance(value)) {
			this.value = value;
		} else {
			throw new IllegalArgumentException("Invalid Type");
		}
	}

	public void registerConnection(NodeInputInterface i) {
		this.connections.add(i);
	}
	
	public void removeConnection(NodeInputInterface i) {
		this.connections.remove(i);
	}
	
	public void destroy() {
		for(NodeInputInterface i : this.connections) {
			i.setConnection(null);
		}
	}
	
}
