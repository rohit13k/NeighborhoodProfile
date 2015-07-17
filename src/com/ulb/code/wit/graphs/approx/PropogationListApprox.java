package com.ulb.code.wit.graphs.approx;

import java.util.ArrayList;
import java.util.HashMap;

public class PropogationListApprox {

	private HashMap<Integer, ArrayList<PropogationObjectApprox>> liHashMap;

	public PropogationListApprox() {
		this.liHashMap = new HashMap<Integer, ArrayList<PropogationObjectApprox>>();
	}

	public int size() {
		int size = 0;
		for (int node : liHashMap.keySet()) {
			size += liHashMap.get(node).size();

		}
		return size;
	}

	public boolean isEmpty() {
		if (liHashMap.size() == 0)
			return true;
		else
			return false;
	}

	public String toString() {
		return liHashMap.toString();
	}

	public void add(PropogationObjectApprox pe) {
		if (liHashMap.containsKey(pe.getTargetNode().getNodeName()))
			liHashMap.get(pe.getTargetNode().getNodeName()).add(pe);
		else {
			ArrayList<PropogationObjectApprox> newPelist = new ArrayList<PropogationObjectApprox>();
			newPelist.add(pe);
			liHashMap.put(pe.getTargetNode().getNodeName(), newPelist);
		}
	}

	public void remove(PropogationObjectApprox pe) {
		this.liHashMap.get(pe.getTargetNode().getNodeName()).remove(pe);
	}

	public void remove(int targetNode) {
		this.liHashMap.remove(targetNode);
	}

	public ArrayList<PropogationObjectApprox> getNext() {
		for (int node : liHashMap.keySet()) {
			return liHashMap.get(node);
		}
		return null;
	}
}
