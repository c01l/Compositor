package nodes;

import java.util.HashMap;

import nodes.singals.Signal;
import nodes.singals.SignalInputInterface;
import nodes.singals.SignalOutputInterface;
import utils.Logging;
import utils.Logging.LogLevel;

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
		Logging.log(LogLevel.INFO, "Finished with " + ret.name(), this
				.getClass().getName());

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
			assert (false);
		}

	}

	public SignalOutputInterface getSignalOutput() {
		return this.sig_out;
	}

	public SignalInputInterface getSignalInput() {
		return this.sig_in;
	}

	protected void registerInput(String name, NodeInputInterface i) {
		if (this.inputs.containsKey(name)) {
			throw new IllegalArgumentException(name + " is already registered.");
		} else {
			this.inputs.put(name, i);
		}
	}

	protected void registerOutput(String name, NodeOutputInterface o) {
		if (this.outputs.containsKey(name)) {
			throw new IllegalArgumentException(name + " is already registered.");
		} else {
			this.outputs.put(name, o);
		}
	}

	protected NodeInputInterface removeInput(String name) {
		NodeInputInterface ret = this.inputs.remove(name);
		if (ret == null) {
			throw new IllegalArgumentException(name + " is not registered.");
		} else {
			ret.destroy();
			return ret;
		}
	}

	protected NodeOutputInterface removeOutput(String name) {
		NodeOutputInterface ret = this.outputs.remove(name);
		if (ret == null) {
			throw new IllegalArgumentException(name + " is not registered.");
		} else {
			ret.destroy();
			return ret;
		}
	}

	public NodeInputInterface getInput(String name) {
		NodeInputInterface ret = this.inputs.get(name);
		if (ret == null) {
			throw new IllegalArgumentException(name + " is not registered.");
		} else {
			return ret;
		}
	}

	public NodeOutputInterface getOutput(String name) {
		NodeOutputInterface ret = this.outputs.get(name);
		if (ret == null) {
			throw new IllegalArgumentException(name + " is not registered.");
		} else {
			return ret;
		}
	}

	public void destroy() {
		this.sig_in.stop();
	}

	public HashMap<String, NodeInputInterface> getInputs() {
		return this.inputs;
	}

	public HashMap<String, NodeOutputInterface> getOutputs() {
		return this.outputs;
	}

}
