package nodes.signals;

import nodes.NodeInterface;

public abstract class SignalInputInterface extends NodeInterface implements SignalReciever {

	public SignalInputInterface(Class<?> c) {
		super(c);
	}
	
}
