package com.ulb.code.wit.graphs.approx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import com.ulb.code.wit.graphs.approx.NodeApprox;
import com.ulb.code.wit.graphs.approx.ImprovedPOAList;
import com.ulb.code.wit.graphs.approx.ImprovedPOApprox;
import com.ulb.code.wit.main.SlidingHLL;
import com.ulb.code.wit.util.Constants;
import com.ulb.code.wit.util.Element;
import com.ulb.code.wit.util.ElementList;
import com.ulb.code.wit.util.Constants.PropagationType;

public class ImprovedGSA {

	private int distance;
	private ConcurrentHashMap<Integer, NodeApprox> graph;

	private final PropagationType ptype;
	private final int numberOfBucket;

	public ImprovedGSA(int distance, PropagationType ptype, int numberOfBucket) {
		this.distance = distance;
		this.graph = new ConcurrentHashMap<Integer, NodeApprox>();
		this.ptype = ptype;
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
			distancewisePropogation(firstNode, secondNode, time);
		} else if (ptype == PropagationType.NODEWISE) {
			nodeWisePropogation(firstNode, secondNode, time);
		} else {

		}

		return 0;
	}

	private void NewPropagation(NodeApprox firstNode, NodeApprox secondNode,
			long time) {
		int id = firstNode.getDistanceWiseSummaries().get(0)
				.add(secondNode.getNodeName(), time);
		if (id != -1) {

			for (NodeApprox na : firstNode.getNeighbours().keySet()) {
				propagate(secondNode.getNodeName(), na, 2, na
						.getDistanceWiseSummaries().get(1), Math.min(time,
						firstNode.getNeighbours().get(na)));
			}

		}
		id = secondNode.getDistanceWiseSummaries().get(0)
				.add(firstNode.getNodeName(), time);
		if (id != -1) {

			for (NodeApprox na : secondNode.getNeighbours().keySet()) {
				propagate(firstNode.getNodeName(), na, 2, na
						.getDistanceWiseSummaries().get(1), Math.min(time,
						secondNode.getNeighbours().get(na)));
			}

		}
	}

	private void propagate(int sourceNode, NodeApprox targetNode, int r,
			SlidingHLL targetSketch, long time) {
		int id = targetSketch.add(sourceNode, time);
		if (id != -1) {
			if (r + 1 <= distance) {
				for (NodeApprox na : targetNode.getNeighbours().keySet()) {
					propagate(sourceNode, na, r + 1, na
							.getDistanceWiseSummaries().get(r), Math.min(time,
							targetNode.getNeighbours().get(na)));
				}
			}
		}
	}

	private void nodeWisePropogation(NodeApprox firstNode,
			NodeApprox secondNode, long time) {

		ImprovedPOAList propogationList = new ImprovedPOAList();

		// adding first node's summary to second
		SlidingHLL d_1_a;
		if (firstNode.getDistanceWiseSummaries().size() != 0) {
			d_1_a = firstNode.getDistanceWiseSummaries().get(0);
		}

		else {
			d_1_a = new SlidingHLL(numberOfBucket);
		}

		d_1_a.add(secondNode.getNodeName(), time);

		for (int i = 2; i < distance; i++) {
			if (!secondNode.getDistanceWiseSummaries().get(i - 1).isEmpty())
				merge(new ImprovedPOApprox(firstNode, secondNode.getNodeName(),
						secondNode.getDistanceWiseSummaries().get(i - 1), time,
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

		for (int i = 2; i < distance; i++) {
			if (!firstNode.getDistanceWiseSummaries().get(i - 1).isEmpty())
				merge(new ImprovedPOApprox(secondNode, firstNode.getNodeName(),
						firstNode.getDistanceWiseSummaries().get(i - 1), time,
						i + 1));
		}
		// HashSet<Integer> exclude = new HashSet<Integer>();
		// exclude.add(firstNode.getNodeName());
		// exclude.add(secondNode.getNodeName());

		// adding neighbours of first and second node
		for (int i = 1; i < distance; i++) {
			addNeighbours(firstNode, i, propogationList, null);
		}

		for (int i = 1; i < distance; i++) {
			addNeighbours(secondNode, i, propogationList, null);
		}

		// processing the propogation list till there is nothing to propogate
		while (!propogationList.isEmpty()) {

			ArrayList<ImprovedPOApprox> pe = propogationList.getNext();
			int targetNode = pe.get(0).getTargetNode().getNodeName();

			int changed = 0;
			for (int i = 0; i < pe.size(); i++) {
				if (merge(pe.get(i))) {
					changed++;
				}
			}
			propogationList.remove(targetNode);
			// completedNode.add(graph.get(targetNode));
			if (changed > 0) {
				for (int i = 1; i < distance; i++) {
					addNeighbours(graph.get(targetNode), i, propogationList,
							null);
				}

			}

		}

	}

	private void distancewisePropogation(NodeApprox firstNode,
			NodeApprox secondNode, long time) {
		ImprovedPOAList propogationListCurrent, propogationListNext;
		propogationListNext = new ImprovedPOAList();

		// updating distance 1 for both the nodes and adding their neighbours in
		// propogation list
		SlidingHLL d_1_a;

		d_1_a = firstNode.getDistanceWiseSummaries().get(0);
		d_1_a.add(secondNode.getNodeName(), time);

		for (int i = 1; i < distance; i++) {
			if (!secondNode.getDistanceWiseSummaries().get(i - 1).isEmpty())
				merge(new ImprovedPOApprox(firstNode, secondNode.getNodeName(),
						secondNode.getDistanceWiseSummaries().get(i - 1), time,
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
				merge(new ImprovedPOApprox(secondNode, firstNode.getNodeName(),
						firstNode.getDistanceWiseSummaries().get(i - 1), time,
						i + 1));
		}
		HashSet<Integer> exclude = new HashSet<Integer>();
		exclude.add(firstNode.getNodeName());
		exclude.add(secondNode.getNodeName());

		addNeighbours(firstNode, 1, propogationListNext, exclude);
		addNeighbours(secondNode, 1, propogationListNext, exclude);
		exclude = null;
		ArrayList<ImprovedPOApprox> pe;
		for (int r = 2; r <= distance; r++) {
			propogationListCurrent = propogationListNext;
			propogationListNext = new ImprovedPOAList();
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

		}
	}

	private boolean merge(ImprovedPOApprox pe) {
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
					if (targetSketch
							.merge(bucketNo,
									element.getValue(),
									Math.min(element.getTimestamp(),
											pe.getTimestamp()))) {
						timesChanged++;
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
			ImprovedPOAList pList, HashSet<Integer> exclude) {
		ImprovedPOApprox poa;
		for (NodeApprox nn : sourceNode.getNeighbours().keySet()) {
			if (!sourceNode.getDistanceWiseSummaries().get(distance - 1)
					.isEmpty()) {
				if (null == exclude || !exclude.contains(nn.getNodeName())) {
					poa = new ImprovedPOApprox(nn, sourceNode.getNodeName(),
							sourceNode.getDistanceWiseSummaries().get(
									distance - 1), sourceNode.getNeighbours()
									.get(nn), distance + 1);
					pList.add(poa);
				}
			}

		}
	}
}
