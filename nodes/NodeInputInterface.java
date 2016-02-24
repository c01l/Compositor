package nodes;

public class NodeInputInterface extends NodeInterface {

	private NodeOutputInterface source = null;
	private final Object defaultValue;

	/**
	 * Each {@link NodeInputInterface} needs to have a type and a default value
	 * that is given to the Parent-{@link Node} as long as no connection to
	 * another {@link Node} is established.
	 * 
	 * @param type
	 *            The data type for this interface
	 * @param defaultValue
	 *            The default value that is given to the {@link Node} as long as
	 *            no connection is established (<code>null</code> is allowed!)
	 * @throws IllegalArgumentException
	 *             if the default value is not of the type specified for this
	 *             {@link Node}
	 */
	public NodeInputInterface(Class<?> type, Object defaultValue) {
		super(type);
		if (defaultValue != null && !type.isInstance(defaultValue)) {
			throw new IllegalArgumentException(defaultValue + " is not an object of " + type.getName());
		}
		this.defaultValue = defaultValue;
	}

	/**
	 * Returns the value of the {@link NodeInputInterface}. If no connection is
	 * established, the default value will be returned.
	 * 
	 * @return the current value of this {@link NodeInputInterface}
	 */
	public Object getValue() {
		if (this.source != null) {
			return this.source.getValue();
		} else {
			return this.defaultValue;
		}
	}

	/**
	 * Specifies where the {@link NodeInputInterface} gets its Information from.
	 * 
	 * @param nSource
	 *            you can pass <code>null</code> if you want to remove any
	 *            established connection.
	 * @throws IllegalArgumentException
	 *             if the source is not a sub-type of the current type
	 */
	public void setConnection(NodeOutputInterface nSource) {
		if (!nSource.getType().isAssignableFrom(this.getType())) {
			throw new IllegalArgumentException("Type mismatch");
		}

		if (this.source != null) {
			this.source.removeConnection(this);
		}

		this.source = nSource;

		if (this.source != null)
			nSource.registerConnection(this);
	}

	public NodeOutputInterface getSource() {
		return this.source;
	}

	/**
	 * This method removes the edge leading to it if there is any
	 * 
	 * This method needs to be called at the end of the lifecycle of an object!
	 */
	public void destroy() {
		this.setConnection(null);
	}

}
