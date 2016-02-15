package nodes.signals;

import java.util.LinkedList;
import java.util.Queue;

import nodes.Node;
import nodes.NodeInterface;
import utils.Logging;
import utils.Logging.LogLevel;

public class SignalInputInterface extends NodeInterface implements
		SignalReciever {

	private Node parent;
	private Queue<Signal> signalQueue;
	private Thread myThread;
	private SignalInputInterface self;

	private LinkedList<SignalOutputInterface> connections;

	public SignalInputInterface(Node parent) {
		super(Signal.class);
		this.self = this;

		this.parent = parent;
		this.signalQueue = new LinkedList<Signal>();

		this.myThread = new SignalHandlerThread();
		this.myThread.start();

		this.connections = new LinkedList<>();
	}

	@Override
	public void sendSignal(Signal s) {
		synchronized (this.signalQueue) {
			this.signalQueue.offer(s);
			this.signalQueue.notifyAll();
		}
	}

	private class SignalHandlerThread extends Thread {

		public SignalHandlerThread() {
			Logging.log(LogLevel.INFO, "Thread started", parent.getClass()
					.getName() + " -> " + getName());
		}

		@Override
		public void run() {

			mainLoop: while (!this.isInterrupted()) {
				synchronized (signalQueue) {
					while (signalQueue.isEmpty()) {
						try {
							signalQueue.wait();
						} catch (InterruptedException e) {
							break mainLoop; // CHECKME other cases?
						}
					}

					// handle signal
					parent.start(signalQueue.poll(), self);
				}
			}

			Logging.log(LogLevel.WARNING, "Signal handler thread stopped",
					parent.getClass().getName());
		}
	}

	@Override
	public void destroy() {
		this.myThread.interrupt();

		for (SignalOutputInterface o : this.connections) {
			o.setConnection(null);
		}
	}

	@Override
	public void registerConnection(SignalOutputInterface o) {
		this.connections.add(o);
	}

	@Override
	public void removeConnection(SignalOutputInterface o) {
		this.connections.remove(o);
	}

}
