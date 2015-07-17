package com.ulb.code.wit.graphs.approx;

import java.util.ArrayList;
import java.util.HashMap;

public class ImprovedPOAList {

	private HashMap<Integer, ArrayList<ImprovedPOApprox>> liHashMap;

	public ImprovedPOAList() {
		this.liHashMap = new HashMap<Integer, ArrayList<ImprovedPOApprox>>();
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

	public void add(ImprovedPOApprox pe) {
		if (liHashMap.containsKey(pe.getTargetNode().getNodeName()))
			liHashMap.get(pe.getTargetNode().getNodeName()).add(pe);
		else {
			ArrayList<ImprovedPOApprox> newPelist = new ArrayList<ImprovedPOApprox>();
			newPelist.add(pe);
			liHashMap.put(pe.getTargetNode().getNodeName(), newPelist);
		}
	}

	public void remove(ImprovedPOApprox pe) {
		this.liHashMap.get(pe.getTargetNode().getNodeName()).remove(pe);
	}

	public void remove(int targetNode) {
		this.liHashMap.remove(targetNode);
	}

	public ArrayList<ImprovedPOApprox> getNext() {
		for (int node : liHashMap.keySet()) {
			return liHashMap.get(node);
		}
		return null;
	}
}
