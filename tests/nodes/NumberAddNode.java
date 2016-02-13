package tests.nodes;

import nodes.Node;
import nodes.NodeInputInterface;
import nodes.NodeOutputInterface;
import nodes.ReturnCode;

/**
 * A {@link NumberAddNode} is a {@link Node} that adds two numbers.
 * 
 * @author Roland Wallner
 *
 */
public class NumberAddNode extends Node {

	private NodeInputInterface in1, in2;
	private NodeOutputInterface out;

	public NumberAddNode() {

		this.in1 = new NodeInputInterface(Integer.class, 0);
		this.in2 = new NodeInputInterface(Integer.class, 0);
		this.out = new NodeOutputInterface(Integer.class, 0);

		this.registerInput("Input1", this.in1);
		this.registerInput("Input2", this.in2);
		this.registerOutput("Sum", this.out);

	}

	@Override
	public ReturnCode run() {
		this.out.setValue(((Integer) this.in1.getValue()) + (Integer) this.in2.getValue());
		return ReturnCode.SUCCESS;
	}

	public NodeInputInterface getInput1() {
		return this.in1;
	}

	public NodeInputInterface getInput2() {
		return this.in2;
	}

	public NodeOutputInterface getOutput() {
		return this.out;
	}

}
