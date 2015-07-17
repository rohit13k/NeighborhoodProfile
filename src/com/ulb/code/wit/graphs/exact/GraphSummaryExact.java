package com.ulb.code.wit.graphs.exact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import jdk.internal.org.objectweb.asm.tree.IntInsnNode;

import com.ulb.code.wit.main.SlidingExactCounting;
import com.ulb.code.wit.util.Constants.PropagationType;

public class GraphSummaryExact {

	private final int distance;
	private HashMap<Integer, NodeExact> graph;
	private final PropagationType ptype;

	public GraphSummaryExact(int distance, PropagationType ptype) {
		this.distance = distance;
		this.graph = new HashMap<Integer, NodeExact>();
		this.ptype = ptype;
	}

	public HashMap<Integer, NodeExact> getGraph() {
		return graph;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Entry<Integer, NodeExact> entry : graph.entrySet()) {
			sb.append(entry.getKey() + ":" + entry.getValue().getNodeSummary()
					+ "\n");
		}
		return sb.toString();
	}

	public int addEdge(int node1, int node2, long time) {
		NodeExact firstNode, secondNode;
		int propsize;
		if (graph.containsKey(node1)) {
			firstNode = graph.get(node1);
		} else {
			firstNode = new NodeExact(node1, distance);
			graph.put(node1, firstNode);
		}

		if (graph.containsKey(node2)) {
			secondNode = graph.get(node2);
		} else {
			secondNode = new NodeExact(node2, distance);
			graph.put(node2, secondNode);
		}

		// updating the nigbourhood
		firstNode.addNeighbour(secondNode, time);
		secondNode.addNeighbour(firstNode, time);

		// processing the propagation list
		if (ptype == PropagationType.DISTANCEWISE) {
			propsize = distancewisePropogation(firstNode, secondNode, time);
		} else {
			propsize = nodeWisePropogation(firstNode, secondNode, time);
		}
		return propsize;
	}

	private int nodeWisePropogation(NodeExact firstNode, NodeExact secondNode,
			long time) {

		PropogationListExact propogationList = new PropogationListExact();
		int propogationSize = 0, initialsize, increasedsize;
		// adding first node's summary to second
		SlidingExactCounting d_1_a;
		if (firstNode.getDistanceWiseSummaries().size() != 0) {
			d_1_a = firstNode.getDistanceWiseSummaries().get(0);
		}

		else {
			d_1_a = new SlidingExactCounting();
		}
		d_1_a.add(secondNode.getNodeName(), time);
		removeIfPresentInHigher(secondNode.getNodeName(), 2, firstNode, time);

		for (int i = 2; i < distance; i++) {
			if (secondNode.getDistanceWiseSummaries().get(i - 1)
					.getAllElements().size() != 0)
				merge(new PropogationObjectExact(firstNode,
						secondNode.getNodeName(), secondNode
								.getDistanceWiseSummaries().get(i - 1), time,
						i + 1));

		}

		// second node with first's summary
		SlidingExactCounting d_1_b;
		if (secondNode.getDistanceWiseSummaries().size() != 0) {
			d_1_b = secondNode.getDistanceWiseSummaries().get(0);
		}

		else {
			d_1_b = new SlidingExactCounting();
		}
		d_1_b.add(firstNode.getNodeName(), time);
		removeIfPresentInHigher(firstNode.getNodeName(), 2, secondNode, time);

		for (int i = 2; i < distance; i++) {
			if (firstNode.getDistanceWiseSummaries().get(i - 1)
					.getAllElements().size() != 0)
				merge(new PropogationObjectExact(secondNode,
						firstNode.getNodeName(), firstNode
								.getDistanceWiseSummaries().get(i - 1), time,
						i + 1));
		}

		// adding neighbours of first and second node
		for (int i = 1; i < distance; i++) {
			addNeighbours(firstNode, i, propogationList);
		}

		for (int i = 1; i < distance; i++) {
			addNeighbours(secondNode, i, propogationList);
		}
		propogationSize += propogationList.size();
//		System.out.println(firstNode.getNodeName() + " "
//				+ secondNode.getNodeName() + " @ " + time);
//		System.out.println(propogationList);
		// processing the propogation list till there is nothing to propogate
		while (!propogationList.isEmpty()) {

			ArrayList<PropogationObjectExact> pe = propogationList.getNext();
			int targetNode = pe.get(0).getTargetNode().getNodeName();

			int changed = 0;
			for (int i = 0; i < pe.size(); i++) {
				if (merge(pe.get(i))) {
					changed++;
				}
			}
			propogationList.remove(targetNode);
			// completedNode.add(graph.get(targetNode));
			initialsize = propogationList.size();
			if (changed > 0) {
				for (int i = 1; i < distance; i++) {
					addNeighbours(graph.get(targetNode), i, propogationList);
				}

			}
			increasedsize = propogationList.size() - initialsize;
			if (increasedsize > 0)
				propogationSize += increasedsize;
//			System.out.println("increased size:" + increasedsize);
//			if (!propogationList.isEmpty()) {
//				System.out.println(firstNode.getNodeName() + " "
//						+ secondNode.getNodeName() + " @ " + time);
//				System.out.println(propogationList);
//			}

		}
		return propogationSize;
	}

	private int distancewisePropogation(NodeExact firstNode,
			NodeExact secondNode, long time) {
		PropogationListExact propogationListCurrent, propogationListNext;
		propogationListNext = new PropogationListExact();
		int propogationsize;
		// updating distance 1 for both the nodes and adding their neighbours in
		// propogation list
		SlidingExactCounting d_1_a;
		if (firstNode.getDistanceWiseSummaries().size() != 0) {
			d_1_a = firstNode.getDistanceWiseSummaries().get(0);
		}

		else {
			d_1_a = new SlidingExactCounting();
		}
		d_1_a.add(secondNode.getNodeName(), time);
		removeIfPresentInHigher(secondNode.getNodeName(), 2, firstNode, time);
		for (int i = 2; i < distance; i++) {
			if (secondNode.getDistanceWiseSummaries().get(i - 1)
					.getAllElements().size() != 0)
				merge(new PropogationObjectExact(firstNode,
						secondNode.getNodeName(), secondNode
								.getDistanceWiseSummaries().get(i - 1), time,
						i + 1));

		}

		SlidingExactCounting d_1_b;
		if (secondNode.getDistanceWiseSummaries().size() != 0) {
			d_1_b = secondNode.getDistanceWiseSummaries().get(0);
		}

		else {
			d_1_b = new SlidingExactCounting();
		}
		d_1_b.add(firstNode.getNodeName(), time);
		removeIfPresentInHigher(firstNode.getNodeName(), 2, secondNode, time);
		for (int i = 2; i < distance; i++) {
			if (firstNode.getDistanceWiseSummaries().get(i - 1)
					.getAllElements().size() != 0)
				merge(new PropogationObjectExact(secondNode,
						firstNode.getNodeName(), firstNode
								.getDistanceWiseSummaries().get(i - 1), time,
						i + 1));
		}

		addNeighbours(firstNode, 1, propogationListNext);
		addNeighbours(secondNode, 1, propogationListNext);
		propogationsize = propogationListNext.size();
//		System.out.println(firstNode.getNodeName() + " "
//				+ secondNode.getNodeName() + " @ " + time);
//		System.out.println(propogationListNext);
		for (int r = 2; r <= distance; r++) {
			propogationListCurrent = propogationListNext;
			propogationListNext = new PropogationListExact();
			while (!propogationListCurrent.isEmpty()) {
				ArrayList<PropogationObjectExact> pe = propogationListCurrent
						.getNext();
				int targetNode = pe.get(0).getTargetNode().getNodeName();

				int changed = 0;
				for (int i = 0; i < pe.size(); i++) {
					if (merge(pe.get(i))) {
						changed++;
					}
				}
				propogationListCurrent.remove(targetNode);
				if (changed > 0) {
					addNeighbours(graph.get(targetNode), r, propogationListNext);

				}
			}
			if (propogationListNext.isEmpty())
				break;
			propogationsize += propogationListNext.size();
//			System.out.println(firstNode.getNodeName() + " "
//					+ secondNode.getNodeName() + " @ " + time);
//			System.out.println(propogationListNext);
		}
		return propogationsize;

	}

	private void addNeighbours(NodeExact sourceNode, int distance,
			PropogationListExact pList) {
		for (NodeExact nn : sourceNode.getNeighbours().keySet()) {
			if (sourceNode.getDistanceWiseSummaries().get(distance - 1)
					.getAllElements().size() != 0)
				pList.add(new PropogationObjectExact(nn, sourceNode
						.getNodeName(), sourceNode.getDistanceWiseSummaries()
						.get(distance - 1), sourceNode.getNeighbours().get(nn),
						distance + 1));
		}
	}

	private boolean merge(PropogationObjectExact pe) {

		SlidingExactCounting targetSketch = pe.getTargetNode()
				.getDistanceWiseSummaries().get(pe.getDistance() - 1);
		SlidingExactCounting sourceSketch = pe.getSourceSketch();

		boolean changed = false;
		int timesChanged = 0;
		// need to complete
		for (Entry<Integer, Long> element : sourceSketch.getAllElements()
				.entrySet()) {
			if (!element.getKey().equals(pe.getTargetNode().getNodeName())) {
				long time;
				if (targetSketch.isPresent(element.getKey())) {
					long oldtimestamp = targetSketch.getAllElements().get(
							element.getKey());
					time = Math.max(oldtimestamp,
							Math.min(element.getValue(), pe.getTimestamp()));

				} else {
					time = Math.min(element.getValue(), pe.getTimestamp());
				}
				long oldtime = isPresentInLower(element.getKey(),
						pe.getDistance() - 1, pe.getTargetNode());
				if (time > oldtime) {

					if (targetSketch.add(element.getKey(), time)) {
						timesChanged++;
					}
				}
				removeIfPresentInHigher(element.getKey(), pe.getDistance() + 1,
						pe.getTargetNode(), time);
			}
		}
		if (timesChanged > 0) {
			changed = true;
		}
		return changed;
	}

	private void removeIfPresentInHigher(int key, int distance,
			NodeExact targetNode, long time) {
		// boolean present = false;

		for (int i = distance - 1; i < targetNode.getDistanceWiseSummaries()
				.size(); i++) {
			if (targetNode.getDistanceWiseSummaries().get(i).isPresent(key)) {
				if (targetNode.getDistanceWiseSummaries().get(i)
						.getAllElements().get(key) < time) {
					targetNode.getDistanceWiseSummaries().get(i)
							.getAllElements().remove(key);
				}

				// present = true;

			}
		}

		// return present;
	}

	private long isPresentInLower(int key, int distance, NodeExact targetNode) {
		long mintime = Long.MIN_VALUE;
		boolean present = false;
		if (distance > targetNode.getDistanceWiseSummaries().size()) {
			distance = targetNode.getDistanceWiseSummaries().size();
		}

		for (int i = 0; i < distance; i++) {
			if (targetNode.getDistanceWiseSummaries().get(i).isPresent(key)) {
				mintime = Math.max(mintime, targetNode
						.getDistanceWiseSummaries().get(i).getAllElements()
						.get(key));
				present = true;
			}
		}

		if (!present) {
			mintime = 0;
		}
		return mintime;
	}
}
