package nodes.signals;

public interface SignalReciever {
	/**
	 * This method is called when you recieved a {@link Signal}.
	 * 
	 * @param s
	 */
	void sendSignal(Signal s);

	/**
	 * Implementing this method allows you to monitor any incoming edges. At the
	 * end of your lifecycle you need to remove these!
	 * 
	 * @param o
	 */
	void registerConnection(SignalOutputInterface o);

	/**
	 * This method is called to tell the {@link SignalReciever} that a specific
	 * connection is no longer established.
	 * 
	 * @param o
	 */
	void removeConnection(SignalOutputInterface o);

	/**
	 * This method is called at the end of the lifecycle of this
	 * {@link SignalReciever}. The {@link SignalReciever} is supposed to
	 * terminate any incoming connections.
	 */
	void destroy();

}
