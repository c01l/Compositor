package nodes;

import java.util.LinkedList;

public class NodeOutputInterface extends NodeInterface {

	private Object value;
	private LinkedList<NodeInputInterface> connections;

	/**
	 * Each {@link NodeOutputInterface} needs to have a type and a default value
	 * that is given to any connected {@link NodeInputInterface} when requested.
	 * 
	 * @param type
	 *            The data type for this interface
	 * @param defaultValue
	 *            The default value that is given to the connected
	 *            {@link NodeInputInterface}
	 * @throws IllegalArgumentException
	 *             if the default value is not of the type specified for this
	 *             {@link Node}
	 */
	public NodeOutputInterface(Class<?> type, Object value) {
		super(type);
		this.value = value;

		this.connections = new LinkedList<>();
	}

	public Object getValue() {
		return this.value;
	}

	/**
	 * This method sets the value of the {@link NodeOutputInterface}. Any
	 * connected {@link NodeInputInterface} will recieve it until this method is
	 * called with a different {@link Object}.
	 * 
	 * @param value
	 *            The curent value for this interface (<code>null</code> is allowed!)
	 * @throws IllegalArgumentException
	 *             in case mismatching data is supplied
	 */
	public void setValue(Object value) {
		if (value == null || this.getType().isInstance(value)) {
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

	/**
	 * This method removes all outgoing connections.
	 * 
	 * This method needs to be called at the end of the lifecycle of this
	 * object!
	 */
	public void destroy() {
		for (NodeInputInterface i : this.connections) {
			i.setConnection(null);
		}
		this.connections.clear();
	}

}
