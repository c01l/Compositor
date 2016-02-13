package tests.nodes;

import nodes.Node;
import nodes.NodeInputInterface;
import nodes.ReturnCode;

/**
 * A {@link NumberOutputNode} is a {@link Node} that prints an {@link Integer}
 * to stdout.
 * 
 * @author Roland Wallner
 *
 */
public class NumberOutputNode extends Node {

	private NodeInputInterface input;

	public NumberOutputNode() {
		this.input = new NodeInputInterface(Integer.class, 0);
		this.registerInput("Input", this.input);
	}

	@Override
	public ReturnCode run() {
		System.out.println(this.input.getValue());
		return ReturnCode.SUCCESS;
	}

}
