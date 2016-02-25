package nodes.compositor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import nodes.Node;
import nodes.NodeInputInterface;
import nodes.NodeInterface;
import nodes.NodeOutputInterface;
import nodes.ReturnCode;
import nodes.signals.Signal;
import nodes.signals.SignalOutputInterface;
import nodes.signals.SignalReciever;
import nodes.signals.SignalSyncronizer;
import utils.Logging;
import utils.Logging.LogLevel;

/**
 * A compositor organizes and combines {@link Node}s to form a new {@link Node}.
 * In other words: Its a {@link Node} with a workflow that is specified by other
 * {@link Node}s.
 * 
 * HC: destroy() must be called at the end of this objects life cycle.
 * 
 * @author Roland Wallner
 * 
 */
public class Compositor extends Node {

	/**
	 * Nodes that are managed by this {@link Compositor}
	 */
	private ArrayList<Node> nodes;

	/**
	 * Syncronizers that are used by this {@link Compositor}
	 */
	private LinkedList<SignalSyncronizer> syncronizerList;

	/**
	 * This {@link SignalOutputInterface} is used to start the program flow
	 * within the {@link Compositor}.
	 */
	private SignalOutputInterface start;

	/**
	 * This {@link SignalReciever} is used to pass any recieved/generated
	 * {@link Signal}s outside of the compositor.
	 */
	private SignalReciever end, exception;

	/**
	 * This {@link HashMap} organizes all inputs the {@link Compositor} can
	 * have.
	 */
	private HashMap<String, CompositorInputInterface> innerInputs;

	/**
	 * This {@link HashMap} organizes all outputs this {@link Compositor} can
	 * have.
	 */
	private HashMap<String, CompositorOutputInterface> innerOutputs;

	/**
	 * This callback-Object is used so you can do an action as soon a
	 * {@link Signal} is passed to the output.
	 */
	private CompositorFinishedCallback endCallback = null;

	public Compositor() {
		this.nodes = new ArrayList<>();
		this.start = new SignalOutputInterface();
		this.end = new CompositorEndSignalReciever();
		this.exception = new CompositorExceptionSignalReciever();

		this.innerInputs = new HashMap<>();
		this.innerOutputs = new HashMap<>();

		this.syncronizerList = new LinkedList<>();
	}

	/**
	 * This method adds a new {@link Node} to this {@link Compositor}.
	 * 
	 * @param n
	 *            A new {@link Node}
	 * @throws IllegalArgumentException
	 *             in case of that the given {@link Node} is already registered
	 *             in this {@link Compositor}
	 */
	public void addNode(Node n) {
		if (this.nodes.contains(n))
			throw new IllegalArgumentException("Node is already registered to the compositor.");

		this.nodes.add(n);
	}

	/**
	 * This method removes a {@link Node} from this {@link Compositor}
	 * 
	 * Attention: This method calles destroy() on the {@link Node}!
	 * 
	 * @param n
	 * 
	 * @throws IllegalArgumentException
	 *             in case of that the {@link Node} is not registered at this
	 *             {@link Compositor}
	 */
	public void removeNode(Node n) {
		if (this.nodes.remove(n)) {
			n.destroy();
		} else {
			throw new IllegalArgumentException("Node is not registered to this compositor.");
		}
	}

	public List<Node> getNodes() {
		return this.nodes;
	}

	/**
	 * This method helps with constructing edges between two
	 * {@link NodeInterface}s.
	 * 
	 * @param out
	 * @param oName
	 * @param in
	 * @param iName
	 * 
	 * @throws IllegalArgumentException
	 *             in case of:
	 *             <ul>
	 *             <li>One of the {@link Node}s is not part of this
	 *             {@link Compositor}</li>
	 *             <li>The {@link NodeInputInterface} cannot be found!</li>
	 *             <li>The {@link NodeOutputInterface} cannot be found!</li>
	 *             </ul>
	 */
	public void addEdge(Node out, String oName, Node in, String iName) {
		if (!(this.nodes.contains(out) && this.nodes.contains(in))) {
			throw new IllegalArgumentException("A node is not owned by this compositor!");
		}

		NodeInputInterface input = in.getInput(iName);
		if (input == null) {
			throw new IllegalArgumentException("InputInterface not found!");
		}

		NodeOutputInterface output = out.getOutput(oName);
		if (output == null) {
			throw new IllegalArgumentException("OutputInterface not found!");
		}
		input.setConnection(output);
	}

	public void addSyncronizer(SignalSyncronizer sync) {
		this.syncronizerList.add(sync);
	}

	/**
	 * This method removes a {@link SignalSyncronizer} from this
	 * {@link Compositor}.
	 * 
	 * Attention: destroy() is called on the {@link SignalSyncronizer}
	 * 
	 * @param sync
	 *            the {@link SignalSyncronizer} you want to remove
	 */
	public void removeSyncronizer(SignalSyncronizer sync) {
		sync.destroy();
		this.syncronizerList.remove(sync);
	}

	public LinkedList<SignalSyncronizer> getSyncronizers() {
		return this.syncronizerList;
	}

	/**
	 * This method returns the inner start interface that sends a {@link Signal}
	 * when the whole {@link Compositor} receives a {@link Signal}.
	 * 
	 * @return
	 */
	public SignalOutputInterface getSignalStart() {
		return this.start;
	}

	/**
	 * Any {@link Signal} passed to the returned {@link SignalReciever} by this
	 * method, will be passed to the {@link SignalOutputInterface} of the whole
	 * {@link Compositor}.
	 * 
	 * @return
	 */
	public SignalReciever getSignalEnd() {
		return this.end;
	}

	/**
	 * Returns the inner exception handler
	 * 
	 * @return
	 */
	public SignalReciever getInnerExceptionReciever() {
		return this.exception;
	}

	public HashMap<String, NodeOutputInterface> getInnerInputs() {
		HashMap<String, NodeOutputInterface> ret = new HashMap<>();

		for (Entry<String, CompositorInputInterface> e : this.innerInputs.entrySet()) {
			ret.put(e.getKey(), e.getValue());
		}

		return ret;
	}

	public HashMap<String, NodeInputInterface> getInnerOutputs() {
		HashMap<String, NodeInputInterface> ret = new HashMap<>();

		for (Entry<String, CompositorOutputInterface> e : this.innerOutputs.entrySet()) {
			ret.put(e.getKey(), e.getValue());
		}

		return ret;
	}
	
	/**
	 * This method should be used to start the program flow in this
	 * {@link Compositor}.
	 */
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
		this.end.destroy();
		this.exception.destroy();
		super.destroy();
	}

	/**
	 * 
	 */
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

	@Override
	protected NodeInputInterface removeInput(String name) {
		NodeInputInterface ret = super.removeInput(name);

		Iterator<Entry<String, CompositorInputInterface>> iter = this.innerInputs.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, CompositorInputInterface> e = iter.next();
			if (e.getKey().equals(name)) {
				iter.remove();
				break;
			}
		}

		return ret;
	}

	@Override
	protected NodeOutputInterface removeOutput(String name) {
		NodeOutputInterface ret = super.removeOutput(name);

		Iterator<Entry<String, CompositorOutputInterface>> iter = this.innerOutputs.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, CompositorOutputInterface> e = iter.next();
			if (e.getKey().equals(name)) {
				iter.remove();
				break;
			}
		}

		return ret;
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

		@Override
		public void destroy() {
			// do nothing as this will not be deleted
			// any remaining inner edges will be removed by the time this is
			// called. (so no work here...)
		}
	}

	private class CompositorExceptionSignalReciever implements SignalReciever {
		@Override
		public void sendSignal(Signal s) {
			if (endCallback != null)
				endCallback.compositorRaisedAnException();

			getExceptionOutput().passSignal(s);
		}

		@Override
		public void removeConnection(SignalOutputInterface o) {
			// do nothing as this will not be deleted
		}

		@Override
		public void registerConnection(SignalOutputInterface o) {
			// do nothing as this will not be deleted
		}

		@Override
		public void destroy() {
			// do nothing as this will not be deleted
			// any remaining inner edges will be removed by the time this is
			// called. (so no work here...)
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
