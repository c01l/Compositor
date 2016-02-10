package nodes;

/**
 * A {@link NodeInterface} is used as a mounting point for edges. An edge is the
 * connection of a {@link NodeOutputInterface} and a {@link NodeInputInterface}.
 * 
 * For a successful connection the type of the output must be a sub-type of the
 * input. To validate this precondition a type must be specified for each
 * {@link NodeInterface}.
 * 
 * @author Roland Wallner
 *
 */
public abstract class NodeInterface {
	private Class<?> clazz;

	public NodeInterface(Class<?> c) {
		this.clazz = c;
	}

	public Class<?> getType() {
		return this.clazz;
	}
}
