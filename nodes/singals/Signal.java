package nodes.singals;

import java.util.Iterator;

import nodes.Node;

/**
 * A {@link Signal} is an object that is used to specify when the code of a
 * {@link Node} should be executed.
 * 
 * @author Roland Wallner
 *
 */
public class Signal {
	/**
	 * Syncronizes given outputs and inputs
	 * 
	 * @param outs
	 * @param ins
	 * @return
	 */
	public static SignalSyncronizer sync(SignalOutputInterface[] outs, SignalReciever[] ins) {
		SignalSyncronizer s = new SignalSyncronizer(outs.length, ins.length);
		Iterator<SignalReciever> sIns = s.getInputs().iterator();
		Iterator<SignalOutputInterface> sOuts = s.getOutputs().iterator();

		for (SignalOutputInterface o : outs) {
			if (!sIns.hasNext())
				assert(false);

			SignalReciever r = sIns.next();

			o.setConnection(r);
		}

		for (SignalReciever i : ins) {
			if (!sOuts.hasNext())
				assert(false);

			SignalOutputInterface o = sOuts.next();

			o.setConnection(i);
		}

		return s;
	}

	/**
	 * Syncronizes given outputs and inputs
	 * 
	 * @param outs
	 * @param ins
	 * @return
	 */
	public static SignalSyncronizer sync(Node[] outs, Node[] ins) {
		SignalOutputInterface[] outInterfaces = new SignalOutputInterface[outs.length];
		for (int i = 0; i < outs.length; ++i) {
			outInterfaces[i] = outs[i].getSignalOutput();
		}
		SignalInputInterface[] inInterfaces = new SignalInputInterface[ins.length];
		for (int i = 0; i < ins.length; ++i) {
			inInterfaces[i] = ins[i].getSignalInput();
		}
		return sync(outInterfaces, inInterfaces);
	}

	/**
	 * Routes the signal from an output to the next input
	 * 
	 * @param sigOut
	 * @param sigIn
	 * @return
	 */
	public static void route(SignalOutputInterface sigOut, SignalReciever sigIn) {
		sigOut.setConnection(sigIn);
	}

	/**
	 * Routes the signal from an output to the next input
	 * 
	 * @param out
	 * @param in
	 * @return
	 */
	public static void route(Node out, Node in) {
		route(out.getSignalOutput(), in.getSignalInput());
	}
}
