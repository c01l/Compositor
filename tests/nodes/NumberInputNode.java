package tests.nodes;

import nodes.Node;
import nodes.NodeOutputInterface;

/**
 * A {@link NumberInputNode} is a {@link Node} that returns a constant
 * {@link Integer}.
 * 
 * @author Roland Wallner
 *
 */
public class NumberInputNode extends Node {

	NodeOutputInterface output;

	public NumberInputNode(Integer value) {
		this.output = new NodeOutputInterface(Integer.class, value);
		this.registerOutput("Output", this.output);
	}

}
