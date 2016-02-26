package nodes.signals;

import nodes.NodeInterface;
import utils.Logging;
import utils.Logging.LogLevel;

public class SignalOutputInterface extends NodeInterface {

	private SignalInputInterface next;

	public SignalOutputInterface() {
		super(Signal.class);
	}

	public void passSignal(Signal s) {
		if (this.next == null) {
			Logging.log(LogLevel.INFO, "Token (" + s.toString() + ") lost");
		} else {
			this.next.sendSignal(s);
		}
	}

	/**
	 * Specifies where the {@link SignalOutputInterface} sends its signal token
	 * to.
	 * 
	 * @param nSource
	 *            you can pass <code>null</code> if you want to remove any
	 *            established connection.
	 */
	public void setConnection(SignalInputInterface target) {
		if(this.next != null) {
			this.next.removeConnection(this);
		}
		
		this.next = target;
		
		if(this.next != null) {
			this.next.registerConnection(this);
		}
	}

	public SignalInputInterface getTarget() {
		return this.next;
	}
	
}
