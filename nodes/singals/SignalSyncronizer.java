package nodes.singals;

import java.util.ArrayList;
import java.util.LinkedList;

public class SignalSyncronizer {

	private ArrayList<SyncronizerInput> sig_ins;
	private ArrayList<SignalOutputInterface> sig_outs;

	/**
	 * 
	 * @param inputs
	 *            >= 1
	 * @param outputs
	 *            >= 1
	 */
	public SignalSyncronizer(int inputs, int outputs) {

		this.sig_ins = new ArrayList<>(inputs);
		this.sig_outs = new ArrayList<>(outputs);

		for (int i = 0; i < inputs; ++i) {
			this.sig_ins.add(new SyncronizerInput());
		}

		for (int i = 0; i < outputs; ++i) {
			this.sig_outs.add(new SignalOutputInterface());
		}

	}

	public ArrayList<SignalReciever> getInputs() {
		ArrayList<SignalReciever> ret = new ArrayList<>();

		for (SignalReciever r : this.sig_ins) {
			ret.add(r);
		}

		return ret;
	}

	public ArrayList<SignalOutputInterface> getOutputs() {
		return this.sig_outs;
	}

	private synchronized void checkContinue() {
		// System.out.println("Check input (" + sig_ins.size() + "," +
		// sig_outs.size() +")");

		boolean done = true;
		for (SyncronizerInput i : this.sig_ins) {
			if (i.getSignal() == null) {
				done = false;
				break;
			}
		}

		if (done) {
			// System.out.println("Check done (" + sig_ins.size() + ","
			// + sig_outs.size() + ")");

			for (SyncronizerInput i : this.sig_ins) {
				i.reset();
			}
			for (SignalOutputInterface o : this.sig_outs) {
				o.passSignal(new Signal()); // CHECKME new?
			}
		}
	}

	public void destroy() {
		for (SyncronizerInput i : this.sig_ins) {
			i.destroy();
		}
	}

	private class SyncronizerInput implements SignalReciever {

		private Signal currentSignal = null;

		private LinkedList<SignalOutputInterface> connection;

		public SyncronizerInput() {
			this.connection = new LinkedList<>();
		}

		@Override
		public synchronized void sendSignal(Signal s) {
			while (this.currentSignal != null) {
				try {
					System.out.println("Starting to wait");
					wait();
				} catch (InterruptedException e) {
					// do nonthing
				}
			}

			this.currentSignal = s;
			checkContinue();
			this.notifyAll();
		}

		public Signal getSignal() {
			return this.currentSignal;
		}

		public void reset() {
			this.currentSignal = null;
		}

		@Override
		public void registerConnection(SignalOutputInterface o) {
			this.connection.add(o);
		}

		@Override
		public void removeConnection(SignalOutputInterface o) {
			this.connection.remove(o);
		}

		public void destroy() {
			for (SignalOutputInterface o : this.connection) {
				o.setConnection(null);
			}
		}
	}

}
