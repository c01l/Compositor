package nodes.singals;

public interface SignalReciever {
	void sendSignal(Signal s);
	void removeConnection(SignalOutputInterface o);
	void registerConnection(SignalOutputInterface o);
}
