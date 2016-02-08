package tests;

import nodes.Node;
import nodes.compositor.Compositor;
import nodes.singals.Signal;
import nodes.singals.SignalOutputInterface;
import nodes.singals.SignalReciever;
import tests.nodes.NumberAddNode;
import tests.nodes.NumberInputNode;
import tests.nodes.NumberOutputNode;

public class FirstMathTestMain {

	public static void main(String[] args) throws InterruptedException {

		// Logging.active = true;

		Compositor comp = new Compositor();

		NumberInputNode in1 = new NumberInputNode(5);
		NumberInputNode in2 = new NumberInputNode(4);
		comp.addNode(in1);
		comp.addNode(in2);

		NumberAddNode add = new NumberAddNode();
		comp.addNode(add);

		NumberOutputNode out = new NumberOutputNode();
		comp.addNode(out);

		// connect data lanes
		// in1 -> add
		comp.addEdge(in1, "Output", add, "Input1");

		// in2 -> add
		comp.addEdge(in2, "Output", add, "Input2");

		// add -> out
		comp.addEdge(add, "Sum", out, "Input");

		// connect signal lanes
		// start -> in1 & in2
		SignalOutputInterface[] start = { comp.getSignalStart() };
		SignalReciever[] insIn = { in1.getSignalInput(), in2.getSignalInput() };
		Signal.sync(start, insIn);

		// in1 & in2 -> add
		Signal.sync(new Node[] { in1, in2 }, new Node[] { add });

		// add -> out
		Signal.route(add, out);

		comp.start();

		// clean up
		Thread.sleep(2000);

		System.out.println("\nQuitting");
		comp.destroy();
	}

}
