package nodes;

import java.util.HashMap;

import nodes.signals.Signal;
import nodes.signals.SignalInputInterface;
import nodes.signals.SignalOutputInterface;
import utils.Logging;
import utils.Logging.LogLevel;

/**
 * A node is the central component of this compositor architecture.
 * 
 * Each {@link Node} has the following elements:
 * <ul>
 * <li>A set of input and output interfaces for data.</li>
 * <li>A {@link SignalInputInterface} for recieving signals that trigger the
 * code of the {@link Node} to execute.</li>
 * <li>A {@link SignalOutputInterface} which will get a {@link Signal} as soon
 * as the node has finished executing its code and want to pass the signal. You
 * can specify that the {@link Signal} is kept and this execution flow will not
 * be executed any further.</li>
 * <li>An Exception-{@link SignalOutputInterface} that will recieve a
 * {@link Signal} in case your code returns the exception status.</li>
 * <li>Code that is executed, when a {@link Signal} is recieved.</li>
 * </ul>
 * 
 * @author Roland Wallner
 *
 */
public abstract class Node {

	private SignalInputInterface sig_in;
	private SignalOutputInterface sig_out, ex_out;

	private HashMap<String, NodeInputInterface> inputs;
	private HashMap<String, NodeOutputInterface> outputs;

	public Node() {
		this.sig_in = new SignalInputInterface(this);
		this.sig_out = new SignalOutputInterface();
		this.ex_out = new SignalOutputInterface();

		this.inputs = new HashMap<>();
		this.outputs = new HashMap<>();
	}

	/**
	 * This method contains the code the Node needs to run. It can read from its
	 * NodeInputInterface(s) and write to NodeOutputInterface(s)
	 * 
	 * This method gets called when a Signal is given to the Node.
	 */
	public ReturnCode run() {
		return ReturnCode.SUCCESS;
	};

	public void start(Signal s, SignalInputInterface caller) {
		Logging.log(LogLevel.INFO, "Started", this.getClass().getName());
		ReturnCode ret = this.run();
		Logging.log(LogLevel.INFO, "Finished with " + ret.name(), this.getClass().getName());

		switch (ret) {
		case NOSIGNAL:
			break;
		case SUCCESS:
			this.sig_out.passSignal(s);
			break;
		case EXCEPTION:
			this.ex_out.passSignal(s);
			break;
		default:
			assert(false);
		}

	}

	public SignalOutputInterface getSignalOutput() {
		return this.sig_out;
	}

	public SignalInputInterface getSignalInput() {
		return this.sig_in;
	}

	/**
	 * This method registers a new {@link NodeInputInterface} in the node.
	 * 
	 * @param name
	 *            A name that is assigned to the interface (must be UNIQUE)
	 * @param i
	 *            The interface you want to store
	 * @throws IllegalArgumentException
	 *             if the name of the interface is already registered
	 */
	protected void registerInput(String name, NodeInputInterface i) {
		if (this.inputs.containsKey(name)) {
			throw new IllegalArgumentException(name + " is already registered.");
		} else {
			this.inputs.put(name, i);
		}
	}

	/**
	 * This method registers a new {@link NodeOutputInterface} in the node.
	 * 
	 * @param name
	 *            A name that is assigned to the interface (must be UNIQUE)
	 * @param o
	 *            The interface you want to store
	 * @throws IllegalArgumentException
	 *             if the name of the interface is already registered
	 */
	protected void registerOutput(String name, NodeOutputInterface o) {
		if (this.outputs.containsKey(name)) {
			throw new IllegalArgumentException(name + " is already registered.");
		} else {
			this.outputs.put(name, o);
		}
	}

	/**
	 * This method removes a {@link NodeInputInterface} from the node by its
	 * name
	 * 
	 * @param name
	 *            The name of the interface
	 * @return The removed interface is returned, but it is already destroyed,
	 *         so it can't be assigned to a new {@link Node}. If the
	 *         {@link Node} does not contain an interface with this name,
	 *         <code>null</code> is returned.
	 */
	protected NodeInputInterface removeInput(String name) {
		NodeInputInterface ret = this.inputs.remove(name);
		if (ret == null) {
			return null;
		} else {
			ret.destroy();
			return ret;
		}
	}

	/**
	 * This method removes a {@link NodeOutputInterface} from the node by its
	 * name.
	 * 
	 * @param name
	 *            The name of the interface
	 * @return The removed interface is returned, but it is already destroyed,
	 *         so it can't be assigned to a new {@link Node}. If the
	 *         {@link Node} does not contain an interface with this name,
	 *         <code>null</code> is returned.
	 */
	protected NodeOutputInterface removeOutput(String name) {
		NodeOutputInterface ret = this.outputs.remove(name);
		if (ret == null) {
			return null;
		} else {
			ret.destroy();
			return ret;
		}
	}

	/**
	 * This method searches for a {@link NodeInputInterface} that is registered
	 * to the {@link Node}.
	 * 
	 * @param name
	 *            The interface-name you want to look for
	 * @return the interface if the interface is found, <code>null</code>
	 *         otherwise
	 */
	public NodeInputInterface getInput(String name) {
		return this.inputs.get(name);
	}

	/**
	 * This method searches for a {@link NodeOutputInterface} that is registered
	 * to the {@link Node}.
	 * 
	 * @param name
	 *            The interface-name you want to look for
	 * @return the interface if the interface is found, <code>null</code>
	 *         otherwise
	 */
	public NodeOutputInterface getOutput(String name) {
		return this.outputs.get(name);
	}

	/**
	 * This method stops the {@link SignalInputInterface}, so no new
	 * {@link Signal}s can be received.
	 * 
	 * This method has to be called when this node is freed!
	 */
	public void destroy() {
		this.sig_in.destroy();
	}

	/**
	 * Returns all {@link NodeInputInterface}s with their names as a
	 * {@link HashMap}.
	 * 
	 * @return
	 */
	public HashMap<String, NodeInputInterface> getInputs() {
		return this.inputs;
	}

	/**
	 * Returns all {@link NodeOutputInterface}s with their names as a
	 * {@link HashMap}.
	 * 
	 * @return
	 */
	public HashMap<String, NodeOutputInterface> getOutputs() {
		return this.outputs;
	}

}
