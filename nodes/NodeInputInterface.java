package nodes;

public class NodeInputInterface extends NodeInterface {

	private NodeOutputInterface source = null;
	private final Object defaultValue;

	public NodeInputInterface(Class<?> type, Object defaultValue) {
		super(type);
		if (!type.isInstance(defaultValue)) {
			throw new IllegalArgumentException(defaultValue + " is not an object of " + type.getName());
		}
		this.defaultValue = defaultValue;
	}

	/**
	 * Returns the value of the {@link NodeInputInterface}. If no connection is
	 * established, the default value will be returned.
	 * 
	 * @return
	 */
	public Object getValue() {
		if (this.source != null) {
			return this.getType().cast(this.source.getValue());
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
	 */
	public void setConnection(NodeOutputInterface nSource) {
		if (!nSource.getType().isAssignableFrom(this.getType())) {
			throw new IllegalArgumentException("Type mismatch");
		}

		if(this.source != null) {
			this.source.removeConnection(this);
		}
		
		this.source = nSource;
		
		if(this.source != null)
			nSource.registerConnection(this);
	}

	public NodeOutputInterface getSource() {
		return this.source;
	}

	public void destroy() {
		this.setConnection(null);
	}
	
}
