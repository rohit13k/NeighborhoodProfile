package com.ulb.code.wit.graphs.exact;

import com.ulb.code.wit.graphs.approx.PropogationObjectApprox;
import com.ulb.code.wit.main.SlidingExactCounting;

public class PropogationObjectExact {

	private NodeExact targetNode;

	private SlidingExactCounting sourceSkecth;
	private int sourceNodeName;
	private long timestamp;
	private int distance;

	public PropogationObjectExact(NodeExact targetNode, int sourceNode,
			SlidingExactCounting sourceSkecth, long timestamp, int distance) {
		this.targetNode = targetNode;
		this.sourceNodeName = sourceNode;
		this.sourceSkecth = sourceSkecth;
		this.timestamp = timestamp;
		this.distance = distance;

	}

	public NodeExact getTargetNode() {
		return targetNode;
	}

	public void setTargetNode(NodeExact targetNode) {
		this.targetNode = targetNode;
	}

	public SlidingExactCounting getSourceSketch() {
		return sourceSkecth;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public int getDistance() {
		return distance;
	}

	public String toString() {
		return "src : " + this.sourceNodeName + " target : " + this.targetNode
				+ " distance : " + this.distance + " time : " + this.timestamp;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof PropogationObjectApprox))
			return false;
		if (obj == this)
			return true;
		else {
			PropogationObjectExact pe = (PropogationObjectExact) obj;
			if (pe.targetNode.equals(targetNode)
					&& pe.distance == this.distance
					&& pe.sourceNodeName == (this.sourceNodeName)
					&& pe.timestamp == this.timestamp) {
				return true;
			} else {
				return false;
			}

		}
	}

}
