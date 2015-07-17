package com.ulb.code.wit.main;

import java.util.HashMap;
import java.util.Map.Entry;

public class SlidingExactCounting {
	private HashMap<Integer, Long> elements = new HashMap<Integer, Long>();

	public boolean add(int obj, long timestamp) {

		boolean isadded = true;
		if (elements.containsKey(obj)) {
			if (elements.get(obj) < timestamp) {
				elements.put(obj, timestamp);
			} else {
				isadded = false;
			}
		} else {
			elements.put(obj, timestamp);
		}
		return isadded;
	}

	public int evaluate() {
		return elements.size();
	}

	public int evaluate(long window) {
		int size = 0;
		for (int key : elements.keySet()) {
			if (elements.get(key) > window) {
				size++;
			}
		}
		return size;
	}

	public HashMap<Integer, Long> getAllElements() {
		return elements;
	}

	public boolean isPresent(int key) {
		return elements.containsKey(key);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Entry<Integer, Long> entry : elements.entrySet()) {
			sb.append("[" + entry.getKey() + "," + entry.getValue() + "]");
		}
		return sb.toString();
	}
}
