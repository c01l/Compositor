package tests;

import nodes.Node;
import nodes.compositor.Compositor;
import nodes.signals.Signal;
import nodes.signals.SignalInputInterface;
import nodes.signals.SignalOutputInterface;
import tests.nodes.NumberAddNode;
import tests.nodes.NumberInputNode;
import tests.nodes.NumberOutputNode;

public class FirstMathTestMain {

	public static void main(String[] args) throws InterruptedException {

		/*
		 * Enable this if you want to see logging information in stdout.
		 */
		// Logging.active = true;

		/*
		 * First I create a new Compositor. This makes many things easier as the
		 * compositor does all the Node-Managment for us. Like destroying Nodes
		 * at the end.
		 */
		Compositor comp = new Compositor();

		/*
		 * Now I create a couple of nodes. The functionality of each Node is
		 * specified in their class.
		 */
		NumberInputNode in1 = new NumberInputNode(5);
		NumberInputNode in2 = new NumberInputNode(4);
		NumberAddNode add = new NumberAddNode();
		NumberOutputNode out = new NumberOutputNode();

		/*
		 * Of course you need to add the Nodes to the Compositor to get any
		 * benefit.
		 */
		comp.addNode(in1);
		comp.addNode(in2);
		comp.addNode(add);
		comp.addNode(out);

		/*
		 * In the next step I connect all data lanes.
		 */
		// in1 -> add
		comp.addEdge(in1, "Output", add, "Input1");

		// in2 -> add
		comp.addEdge(in2, "Output", add, "Input2");

		// add -> out
		comp.addEdge(add, "Sum", out, "Input");

		/*
		 * Now the signal lanes. For the creation of the SignalSyncronizers I
		 * used the helper-function Signal.sync
		 * 
		 * You can either pass the SignalInterfaces or Nodes. In cas of the
		 * Nodes the Standart-Signal-Input/Output is used.
		 */
		// start -> in1 & in2
		SignalOutputInterface[] start = { comp.getSignalStart() };
		SignalInputInterface[] insIn = { in1.getSignalInput(), in2.getSignalInput() };
		comp.addSyncronizer(Signal.sync(start, insIn));

		// in1 & in2 -> add
		comp.addSyncronizer(Signal.sync(new Node[] { in1, in2 }, new Node[] { add }));

		// add -> out
		Signal.route(add, out);

		/*
		 * With start() we send a Signal through the ComositorStartNode.
		 */
		comp.start();

		/*
		 * Now we "wait" for the program flow to finish and clean up afterwards.
		 */
		Thread.sleep(2000);

		// clean up
		System.out.println("\nQuitting");
		comp.destroy();
		System.out.println("Finished");
	}

}
