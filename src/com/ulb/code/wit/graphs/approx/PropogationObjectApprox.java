package com.ulb.code.wit.graphs.approx;

import com.ulb.code.wit.main.SlidingHLL;

public class PropogationObjectApprox {

	private NodeApprox targetNode;

	private SlidingHLL sourceSkecth;
	private int sourceNodeName;
	private long timestamp;
	private int distance;

	public PropogationObjectApprox(NodeApprox targetNode, int sourceNode,
			SlidingHLL sourceSkecth, long timestamp, int distance) {
		this.targetNode = targetNode;
		this.sourceNodeName = sourceNode;
		this.sourceSkecth = sourceSkecth;
		this.timestamp = timestamp;
		this.distance = distance;

	}

	public NodeApprox getTargetNode() {
		return targetNode;
	}

	public void setTargetNode(NodeApprox targetNode) {
		this.targetNode = targetNode;
	}

	public SlidingHLL getSourceElement() {
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
			PropogationObjectApprox pe = (PropogationObjectApprox) obj;
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
