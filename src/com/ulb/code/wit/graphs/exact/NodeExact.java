package com.ulb.code.wit.graphs.exact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import com.ulb.code.wit.main.SlidingExactCounting;

public class NodeExact {
	private int nodeName;
	private int nodeSummary;
	private ArrayList<SlidingExactCounting> distanceWiseSummaries;
	private HashMap<NodeExact, Long> neighbours;

	public NodeExact(int name, int distance) {
		this.nodeName = name;
		this.nodeSummary = 0;
		this.distanceWiseSummaries = new ArrayList<SlidingExactCounting>();
		this.neighbours = new HashMap<NodeExact, Long>();
		for (int i = 0; i < distance; i++) {
			this.distanceWiseSummaries.add(new SlidingExactCounting());
		}

	}

	public int getNodeSummary() {
		HashSet<Integer> data = new HashSet<Integer>();

		for (int i = 0; i < distanceWiseSummaries.size(); i++) {
			for (Entry<Integer, Long> entry : distanceWiseSummaries.get(i)
					.getAllElements().entrySet()) {
				data.add(entry.getKey());

			}
		}
		this.nodeSummary = data.size();
		return this.nodeSummary;
	}

	public int getNodeSummary(long window) {
		HashSet<Integer> data = new HashSet<Integer>();

		for (int i = 0; i < distanceWiseSummaries.size(); i++) {
			for (Entry<Integer, Long> entry : distanceWiseSummaries.get(i)
					.getAllElements().entrySet()) {
				if (entry.getValue() > window) {
					data.add(entry.getKey());
				}
			}
		}
		this.nodeSummary = data.size();
		return this.nodeSummary;
	}

	public ArrayList<SlidingExactCounting> getDistanceWiseSummaries() {
		return distanceWiseSummaries;
	}

	public void setDistanceWiseSummaries(
			ArrayList<SlidingExactCounting> distanceWiseSummaries) {
		this.distanceWiseSummaries = distanceWiseSummaries;
	}

	public HashMap<NodeExact, Long> getNeighbours() {
		return neighbours;
	}

	public void setNeighbours(HashMap<NodeExact,Long> neighbours) {
		this.neighbours = neighbours;
	}

	public boolean addNeighbour(NodeExact node, long timestamp) {
		boolean isadded = true;
		// checking if the node is already present if yes removing it
		if (neighbours.containsKey(node)) {

			if (neighbours.get(node) < timestamp) {
				neighbours.put(node, timestamp);
			} else {
				isadded = false;
			}

		} else {
			this.neighbours.put(node, timestamp);
		}

		return isadded;

	}

	public int getNodeName() {
		return nodeName;
	}

	@Override
	public String toString() {
		return this.nodeName + "";
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof NodeExact))
			return false;
		if (obj == this)
			return true;
		else {
			NodeExact newNode = (NodeExact) obj;
			if (newNode.getNodeName() == (this.nodeName)) {
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public int hashCode() {
		return this.getNodeName();
	}

}
