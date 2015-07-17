package com.ulb.code.wit.graphs.approx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import com.ulb.code.wit.main.SlidingHLL;
import com.ulb.code.wit.util.*;

public class NodeApprox {

	private int nodeName;
	private SlidingHLL nodeSummary;
	private ArrayList<SlidingHLL> distanceWiseSummaries;
	private ConcurrentHashMap<NodeApprox, Long> neighbours;
	private final int numberOfBucket;

	public NodeApprox(int name, int distance, int numberOfBucket) {
		this.nodeName = name;
		this.numberOfBucket = numberOfBucket;
		this.nodeSummary = new SlidingHLL(numberOfBucket);
		this.distanceWiseSummaries = new ArrayList<SlidingHLL>();

		this.neighbours = new ConcurrentHashMap<NodeApprox, Long>();

		for (int i = 0; i < distance; i++) {
			this.distanceWiseSummaries.add(new SlidingHLL(numberOfBucket));
		}

	}

	public synchronized SlidingHLL getNodeSummary() {
		nodeSummary = new SlidingHLL(numberOfBucket);
		SlidingHLL oldSummary;
		int bucketNo = 0;

		Element elem;
		for (int i = 0; i < distanceWiseSummaries.size(); i++) {
			oldSummary = distanceWiseSummaries.get(i);
			bucketNo = 0;
			for (ElementList el : oldSummary.getBuckets()) {
				if (null != el) {
					for (int j = 0; j < el.size(); j++) {
						elem = el.getElement(j);
						if (elem != null)
							nodeSummary.merge(bucketNo, elem);
					}

				}

				bucketNo++;
			}
		}

		return nodeSummary;
	}

	public ArrayList<SlidingHLL> getDistanceWiseSummaries() {
		return distanceWiseSummaries;
	}

	public void setDistanceWiseSummaries(
			ArrayList<SlidingHLL> distanceWiseSummaries) {
		this.distanceWiseSummaries = distanceWiseSummaries;
	}

	public synchronized ConcurrentHashMap<NodeApprox, Long> getNeighbours() {
		return neighbours;
	}

	public synchronized void setNeighbours(
			ConcurrentHashMap<NodeApprox, Long> neighbours) {
		this.neighbours = neighbours;
	}

	public synchronized boolean addNeighbour(NodeApprox node, long timestamp) {
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
		if (!(obj instanceof NodeApprox))
			return false;
		if (obj == this)
			return true;
		else {
			NodeApprox newNode = (NodeApprox) obj;
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
