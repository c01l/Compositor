package tests.nodes;

import nodes.Node;
import nodes.NodeOutputInterface;

public class NumberInputNode extends Node {
	
	NodeOutputInterface output;
	
	public NumberInputNode(Integer value) {
		this.output = new NodeOutputInterface(Integer.class, value);
		this.registerOutput("Output", this.output);
	}
	
}
