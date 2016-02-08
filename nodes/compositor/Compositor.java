package nodes.compositor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import nodes.Node;
import nodes.NodeInputInterface;
import nodes.NodeOutputInterface;
import nodes.ReturnCode;
import nodes.singals.Signal;
import nodes.singals.SignalOutputInterface;
import nodes.singals.SignalReciever;
import nodes.singals.SignalSyncronizer;
import utils.Logging;
import utils.Logging.LogLevel;

/**
 * History-Constraint: destroy() must be called at the end of this objects life
 * cycle.
 * 
 * @author Roland Wallner
 * 
 */
public class Compositor extends Node {

	private ArrayList<Node> nodes;
	private SignalOutputInterface start;
	private SignalReciever end;

	private HashMap<String, CompositorInputInterface> innerInputs;
	private HashMap<String, CompositorOutputInterface> innerOutputs;
	
	private CompositorFinishedCallback endCallback = null;

	private LinkedList<SignalSyncronizer> syncronizerList;
	
	public Compositor() {
		this.nodes = new ArrayList<>();
		this.start = new SignalOutputInterface();
		this.end = new CompositorEndSignalReciever();

		this.innerInputs = new HashMap<>();
		this.innerOutputs = new HashMap<>();
		
		this.syncronizerList = new LinkedList<>();
	}

	public void addNode(Node n) {
		if (this.nodes.contains(n))
			throw new IllegalArgumentException(
					"Node is already registered to the compositor.");

		this.nodes.add(n);
	}

	public void removeNode(Node n) {
		if (this.nodes.contains(n)) {
			if (this.nodes.remove(n)) {
				n.destroy();
			} else {
				assert (false); // should be impossible
			}
		} else {
			throw new IllegalArgumentException(
					"Node is not registered to this compositor.");
		}
	}

	public List<Node> getNodes() {
		return this.nodes;
	}

	public void addEdge(Node out, String oName, Node in, String iName) {
		if(!(this.nodes.contains(out) && this.nodes.contains(in))) {
			throw new IllegalArgumentException("A node is not owned by this compositor!");
		}
		
		in.getInput(iName).setConnection(out.getOutput(oName));
	}

	public void addSyncronizer(SignalSyncronizer sync) {
		this.syncronizerList.add(sync);
	}
	
	public void removeSyncronizer(SignalSyncronizer sync) {
		sync.destroy();
		this.syncronizerList.remove(sync);
	}

	public LinkedList<SignalSyncronizer> getSyncronizers() {
		return this.syncronizerList;
	}
	
	public SignalOutputInterface getSignalStart() {
		return this.start;
	}

	public SignalReciever getSignalEnd() {
		return this.end;
	}

	public void start() {
		this.getSignalStart().passSignal(new Signal());
		Logging.log(LogLevel.INFO, "Started compositor", this.toString());
	}

	@Override
	public ReturnCode run() {
		this.start();
		return ReturnCode.NOSIGNAL;
	}

	/**
	 * stops all Nodes (they cant recieve signals anymore)
	 */
	@Override
	public void destroy() {
		for (Node n : this.nodes) {
			n.destroy();
		}
		super.destroy();
	}

	@Override
	protected void registerInput(String name, NodeInputInterface i) {
		super.registerInput(name, i);

		this.innerInputs.put(name, new CompositorInputInterface(i));
	}

	@Override
	protected void registerOutput(String name, NodeOutputInterface o) {
		super.registerOutput(name, o);

		this.innerOutputs.put(name, new CompositorOutputInterface(o));
	}
	
	public void setEndCallback(CompositorFinishedCallback finishCallback) {
		this.endCallback = finishCallback;
	}

	private class CompositorEndSignalReciever implements SignalReciever {
		@Override
		public void sendSignal(Signal s) {
			// update outputs
			for (CompositorOutputInterface o : innerOutputs.values()) {
				o.update();
			}

			if (endCallback != null)
				endCallback.compositorFinished();

			// pass signal
			getSignalOutput().passSignal(s);
		}

		@Override
		public void removeConnection(SignalOutputInterface o) {
			// do nothing as this will not be deleted
		}

		@Override
		public void registerConnection(SignalOutputInterface o) {
			// do nothing as this will not be deleted
		}
	}

	private class CompositorInputInterface extends NodeOutputInterface {
		private NodeInputInterface realInput;

		public CompositorInputInterface(NodeInputInterface realInput) {
			super(realInput.getType(), realInput.getValue());
			this.realInput = realInput;
		}

		@Override
		public Object getValue() {
			return this.realInput.getValue();
		}
	}

	private class CompositorOutputInterface extends NodeInputInterface {
		private NodeOutputInterface realOutput;

		public CompositorOutputInterface(NodeOutputInterface realOutput) {
			super(realOutput.getType(), realOutput.getValue());
			this.realOutput = realOutput;
		}

		public void update() {
			this.realOutput.setValue(this.getValue());
		}
	}
}
