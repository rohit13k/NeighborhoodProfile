package com.ulb.code.wit.graphs.exact;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.ulb.code.wit.graphs.approx.PropogationObjectApprox;

public class PropogationListExact {

	private HashMap<Integer, ArrayList<PropogationObjectExact>> liHashMap;

	public int size() {
		int size = 0;
		for (int node : liHashMap.keySet()) {
			size += liHashMap.get(node).size();

		}
		return size;
	}

	public PropogationListExact() {
		this.liHashMap = new HashMap<Integer, ArrayList<PropogationObjectExact>>();
	}

	public boolean isEmpty() {
		if (liHashMap.size() == 0)
			return true;
		else
			return false;
	}

	public void add(PropogationObjectExact pe) {
		if (liHashMap.containsKey(pe.getTargetNode().getNodeName()))
			liHashMap.get(pe.getTargetNode().getNodeName()).add(pe);
		else {
			ArrayList<PropogationObjectExact> newPelist = new ArrayList<PropogationObjectExact>();
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

	public ArrayList<PropogationObjectExact> getNext() {
		for (int node : liHashMap.keySet()) {
			return liHashMap.get(node);
		}
		return null;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Entry<Integer, ArrayList<PropogationObjectExact>> entry : liHashMap
				.entrySet()) {
			sb.append(entry.getKey() + ":" + entry.getValue() + ",");
		}
		return sb.toString();
	}
}
