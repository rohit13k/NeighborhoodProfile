package com.ulb.code.wit.main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.ulb.code.wit.util.Constants;

public class SlidingHyperANF {

	public HashMap<Integer, ArrayList<SlidingHLL>> graph;
	int k, d;

	public SlidingHyperANF(int k, int d) {
		this.graph = new HashMap<Integer, ArrayList<SlidingHLL>>();
		this.k = k;
		this.d = d;
	}

	public void parse(ArrayList<String> data) {
		initialize(data);
		int[] ng;
		int node1, node2;
		String[] temp;
		// System.out.println(graph.get(11).get(0).estimate());
		// System.out.println(graph.get(4).get(0).estimate());
		for (int i = 1; i <= d; i++) {
			// System.out.println("doing distance " + i + " " + new Date());
			for (int node : graph.keySet()) {
				graph.get(node).add(new SlidingHLL(k));
				graph.get(node).get(i)
						.union(graph.get(node).get(i - 1).buckets);
			}
			// System.out.println(graph.get(11).get(0).estimate());
			// System.out.println(graph.get(4).get(0).estimate());
			// System.out.println(graph.get(11).get(1).estimate());
			// System.out.println(graph.get(4).get(1).estimate());
			for (int j = 0; j < data.size(); j++) {
				temp = data.get(j).split(",");
				node1 = Integer.parseInt(temp[0]);
				node2 = Integer.parseInt(temp[1]);
				// if(node1==11||node2==11){
				// System.out.println(graph.get(node1).get(i-1).estimate());
				// System.out.println(graph.get(node2).get(i-1).estimate());
				// System.out.println(graph.get(node1).get(i).estimate());
				// System.out.println(graph.get(node2).get(i).estimate());
				// }
				// updating node 1
				graph.get(node1).get(i)
						.union(graph.get(node2).get(i - 1).buckets);

				// updating node 2
				graph.get(node2).get(i)
						.union(graph.get(node1).get(i - 1).buckets);

			}
			remove(i - 1);
		}

	}

	public void parse(String filePath, int limit) throws IOException {
		initialize(filePath);
		int[] ng;
		int node1, node2;
		String[] temp;
		String line;
		BufferedReader br;
		int count;
		// System.out.println(graph.get(11).get(0).estimate());
		// System.out.println(graph.get(4).get(0).estimate());
		for (int i = 1; i <= d; i++) {
			// System.out.println("doing distance " + i + " " + new Date());
			for (int node : graph.keySet()) {
				graph.get(node).add(new SlidingHLL(k));
				graph.get(node).get(i)
						.union(graph.get(node).get(i - 1).buckets);
			}
			// System.out.println(graph.get(11).get(0).estimate());
			// System.out.println(graph.get(4).get(0).estimate());
			// System.out.println(graph.get(11).get(1).estimate());
			// System.out.println(graph.get(4).get(1).estimate());

			br = new BufferedReader(new FileReader(filePath));
			count = 0;
			while ((line = br.readLine()) != null) {

				temp = line.split(",");
				node1 = Integer.parseInt(temp[0]);
				node2 = Integer.parseInt(temp[1]);
				// if(node1==11||node2==11){
				// System.out.println(graph.get(node1).get(i-1).estimate());
				// System.out.println(graph.get(node2).get(i-1).estimate());
				// System.out.println(graph.get(node1).get(i).estimate());
				// System.out.println(graph.get(node2).get(i).estimate());
				// }
				// updating node 1
				graph.get(node1).get(i)
						.union(graph.get(node2).get(i - 1).buckets);

				// updating node 2
				graph.get(node2).get(i)
						.union(graph.get(node1).get(i - 1).buckets);
				count++;
				if (count == limit) {
					break;
				}

			}
			remove(i - 1);
			br.close();
		}

	}

	public void remove(int i) {
		for (int node : graph.keySet()) {
			graph.get(node).set(i, null);
		}
	}

	public void initialize(String filePath) throws IOException {
		String[] temp;
		String line;
		BufferedReader br = new BufferedReader(new FileReader(filePath));

		while ((line = br.readLine()) != null) {

			temp = line.split(",");
			if (!graph.containsKey(temp[0])) {
				SlidingHLL log = new SlidingHLL(k);
				ArrayList<SlidingHLL> ahll = new ArrayList<>();
				log.add(Integer.parseInt(temp[0]));
				ahll.add(log);
				graph.put(Integer.parseInt(temp[0]), ahll);
			}
			if (!graph.containsKey(temp[1])) {
				SlidingHLL log = new SlidingHLL(k);
				ArrayList<SlidingHLL> ahll = new ArrayList<>();
				log.add(Integer.parseInt(temp[1]));
				ahll.add(log);
				graph.put(Integer.parseInt(temp[1]), ahll);
			}
		}
		br.close();

	}

	public void initialize(ArrayList<String> data) {
		String[] temp;

		for (int i = 0; i < data.size(); i++) {
			temp = data.get(i).split(",");
			if (!graph.containsKey(temp[0])) {
				SlidingHLL log = new SlidingHLL(k);
				ArrayList<SlidingHLL> ahll = new ArrayList<>();
				log.add(Integer.parseInt(temp[0]));
				ahll.add(log);
				graph.put(Integer.parseInt(temp[0]), ahll);
			}
			if (!graph.containsKey(temp[1])) {
				SlidingHLL log = new SlidingHLL(k);
				ArrayList<SlidingHLL> ahll = new ArrayList<>();
				log.add(Integer.parseInt(temp[1]));
				ahll.add(log);
				graph.put(Integer.parseInt(temp[1]), ahll);
			}
		}

	}
}
