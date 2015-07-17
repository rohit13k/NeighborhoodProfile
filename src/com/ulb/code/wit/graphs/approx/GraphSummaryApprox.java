package com.ulb.code.wit.graphs.approx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import com.ulb.code.wit.graphs.approx.NodeApprox;
import com.ulb.code.wit.graphs.approx.PropogationListApprox;
import com.ulb.code.wit.graphs.approx.PropogationObjectApprox;
import com.ulb.code.wit.main.SlidingHLL;
import com.ulb.code.wit.util.Constants;
import com.ulb.code.wit.util.Element;
import com.ulb.code.wit.util.ElementList;
import com.ulb.code.wit.util.Constants.PropagationType;

public class GraphSummaryApprox {

	private int distance;
	private ConcurrentHashMap<Integer, NodeApprox> graph;

	private final PropagationType ptype;
	private boolean monitorPropogation;
	private final int numberOfBucket;

	public GraphSummaryApprox(int distance, PropagationType ptype,
			boolean monitorPropogation, int numberOfBucket) {
		this.distance = distance;
		this.graph = new ConcurrentHashMap<Integer, NodeApprox>();
		this.ptype = ptype;
		this.monitorPropogation = monitorPropogation;
		this.numberOfBucket = numberOfBucket;

	}

	public ConcurrentHashMap<Integer, NodeApprox> getGraph() {
		return graph;
	}

	public int addEdge(int node1, int node2, long time) {
		NodeApprox firstNode, secondNode;

		if (graph.containsKey(node1)) {
			firstNode = graph.get(node1);
		} else {
			firstNode = new NodeApprox(node1, distance, numberOfBucket);
			graph.put(node1, firstNode);
		}

		if (graph.containsKey(node2)) {
			secondNode = graph.get(node2);
		} else {
			secondNode = new NodeApprox(node2, distance, numberOfBucket);
			graph.put(node2, secondNode);
		}

		// updating the nigbourhood
		if (!firstNode.addNeighbour(secondNode, time)) {
			// neighbour didnt got changed so no need to do anything
			return 0;
		}
		secondNode.addNeighbour(firstNode, time);

		// processing the propagation list
		if (ptype == PropagationType.DISTANCEWISE) {
			return distancewisePropogation(firstNode, secondNode, time);
		} else {
			return nodeWisePropogation(firstNode, secondNode, time);
		}

	}

	private int nodeWisePropogation(NodeApprox firstNode,
			NodeApprox secondNode, long time) {

		PropogationListApprox propogationList = new PropogationListApprox();
		int propogationSize = 0, initialsize = 0, increasedsize;
		// adding first node's summary to second
		SlidingHLL d_1_a;
		if (firstNode.getDistanceWiseSummaries().size() != 0) {
			d_1_a = firstNode.getDistanceWiseSummaries().get(0);
		}

		else {
			d_1_a = new SlidingHLL(numberOfBucket);
		}

		d_1_a.add(secondNode.getNodeName(), time);

		for (int i = 1; i < distance; i++) {
			if (!secondNode.getDistanceWiseSummaries().get(i - 1).isEmpty())
				merge(new PropogationObjectApprox(firstNode,
						secondNode.getNodeName(), secondNode
								.getDistanceWiseSummaries().get(i - 1), time,
						i + 1));

		}

		// second node with first's summary
		SlidingHLL d_1_b;
		if (secondNode.getDistanceWiseSummaries().size() != 0) {
			d_1_b = secondNode.getDistanceWiseSummaries().get(0);
		}

		else {
			d_1_b = new SlidingHLL(numberOfBucket);
		}
		d_1_b.add(firstNode.getNodeName(), time);

		for (int i = 1; i < distance; i++) {
			if (!firstNode.getDistanceWiseSummaries().get(i - 1).isEmpty())
				merge(new PropogationObjectApprox(secondNode,
						firstNode.getNodeName(), firstNode
								.getDistanceWiseSummaries().get(i - 1), time,
						i + 1));
		}
		HashSet<Integer> exclude = new HashSet<Integer>();
		exclude.add(firstNode.getNodeName());
		exclude.add(secondNode.getNodeName());

		// adding neighbours of first and second node
		for (int i = 1; i < distance; i++) {
			addNeighbours(firstNode, i, propogationList, exclude);
		}

		for (int i = 1; i < distance; i++) {
			addNeighbours(secondNode, i, propogationList, exclude);
		}

		// processing the propogation list till there is nothing to propogate
		while (!propogationList.isEmpty()) {

			ArrayList<PropogationObjectApprox> pe = propogationList.getNext();
			int targetNode = pe.get(0).getTargetNode().getNodeName();

			int changed = 0;
			for (int i = 0; i < pe.size(); i++) {
				if (merge(pe.get(i))) {
					changed++;
				}
			}
			propogationList.remove(targetNode);
			// completedNode.add(graph.get(targetNode));
			if (monitorPropogation) {
				initialsize = propogationList.size();
			}
			if (changed > 0) {
				for (int i = 1; i < distance; i++) {
					addNeighbours(graph.get(targetNode), i, propogationList,
							null);
				}

			}
			if (monitorPropogation) {
				increasedsize = propogationList.size() - initialsize;
				if (increasedsize > 0)
					propogationSize += increasedsize;
			}
		}
		return propogationSize;
	}

	private int distancewisePropogation(NodeApprox firstNode,
			NodeApprox secondNode, long time) {
		PropogationListApprox propogationListCurrent, propogationListNext;
		propogationListNext = new PropogationListApprox();

		// updating distance 1 for both the nodes and adding their neighbours in
		// propogation list
		SlidingHLL d_1_a;
		int propogationsize = 0;
		d_1_a = firstNode.getDistanceWiseSummaries().get(0);
		d_1_a.add(secondNode.getNodeName(), time);

		for (int i = 1; i < distance; i++) {
			if (!secondNode.getDistanceWiseSummaries().get(i - 1).isEmpty())
				merge(new PropogationObjectApprox(firstNode,
						secondNode.getNodeName(), secondNode
								.getDistanceWiseSummaries().get(i - 1), time,
						i + 1));

		}

		SlidingHLL d_1_b;
		if (secondNode.getDistanceWiseSummaries().size() != 0) {
			d_1_b = secondNode.getDistanceWiseSummaries().get(0);
			d_1_b.add(firstNode.getNodeName(), time);
		}

		else {
			d_1_b = new SlidingHLL(numberOfBucket);
			d_1_b.add(firstNode.getNodeName(), time);
			secondNode.getDistanceWiseSummaries().add(0, d_1_b);
		}

		for (int i = 1; i < distance; i++) {
			if (!firstNode.getDistanceWiseSummaries().get(i - 1).isEmpty())
				merge(new PropogationObjectApprox(secondNode,
						firstNode.getNodeName(), firstNode
								.getDistanceWiseSummaries().get(i - 1), time,
						i + 1));
		}
		HashSet<Integer> exclude = new HashSet<Integer>();
		exclude.add(firstNode.getNodeName());
		exclude.add(secondNode.getNodeName());

		addNeighbours(firstNode, 1, propogationListNext, exclude);
		addNeighbours(secondNode, 1, propogationListNext, exclude);
		exclude = null;
		if (monitorPropogation)
			propogationsize = propogationListNext.size();
		ArrayList<PropogationObjectApprox> pe;
		for (int r = 2; r <= distance; r++) {
			propogationListCurrent = propogationListNext;
			propogationListNext = new PropogationListApprox();
			while (!propogationListCurrent.isEmpty()) {
				pe = propogationListCurrent.getNext();
				int targetNode = pe.get(0).getTargetNode().getNodeName();

				int changed = 0;
				for (int i = 0; i < pe.size(); i++) {
					if (merge(pe.get(i))) {
						changed++;
					}
				}
				propogationListCurrent.remove(targetNode);
				if (changed > 0) {
					addNeighbours(graph.get(targetNode), r,
							propogationListNext, null);

				}

			}
			
			if (monitorPropogation)
				propogationsize += propogationListNext.size();

		}
		return propogationsize;
	}

	private boolean merge(PropogationObjectApprox pe) {
		// avoiding propogating if lower is empty
		if (pe.getDistance() - 2 > 0) {
			if (pe.getTargetNode().getDistanceWiseSummaries()
					.get(pe.getDistance() - 2).isEmpty()) {
				return false;
			}
		}
		SlidingHLL targetSketch = pe.getTargetNode().getDistanceWiseSummaries()
				.get(pe.getDistance() - 1);
		SlidingHLL sourceSketch = pe.getSourceElement();
		Element element;
		ElementList<Element> el;
		boolean changed = false;
		int timesChanged = 0;

		ArrayList<ElementList<Element>> sourceBuckets = sourceSketch
				.getBuckets();
		for (int bucketNo = 0; bucketNo < sourceBuckets.size(); bucketNo++) {
			el = sourceBuckets.get(bucketNo);
			if (el != null) {
				for (int i = 0; i < el.size(); i++) {
					element = el.getElement(i);

					if (pe.getTimestamp() < element.getTimestamp()) {
						if (targetSketch.merge(bucketNo, element.getValue(),
								pe.getTimestamp())) {
							timesChanged++;
						}
					} else {
						if (targetSketch.merge(bucketNo, element)) {
							timesChanged++;
						}
					}

				}
			}
		}
		if (timesChanged > 0) {
			changed = true;
		}
		return changed;
	}

	private void addNeighbours(NodeApprox sourceNode, int distance,
			PropogationListApprox pList, HashSet<Integer> exclude) {
		PropogationObjectApprox poa;
		for (NodeApprox nn : sourceNode.getNeighbours().keySet()) {
			if (!sourceNode.getDistanceWiseSummaries().get(distance - 1)
					.isEmpty()) {
				if (null == exclude || !exclude.contains(nn.getNodeName())) {
					poa = new PropogationObjectApprox(nn,
							sourceNode.getNodeName(), sourceNode
									.getDistanceWiseSummaries().get(
											distance - 1), sourceNode
									.getNeighbours().get(nn), distance + 1);
					pList.add(poa);
				}
			}

		}
	}
}
