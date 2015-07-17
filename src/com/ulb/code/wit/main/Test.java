package com.ulb.code.wit.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;

import com.ulb.code.wit.graphs.approx.NodeApprox;
import com.ulb.code.wit.graphs.exact.NodeExact;
import com.ulb.code.wit.util.Constants;
import com.ulb.code.wit.util.Constants.Commands;

public class Test extends ArrayList {

	// public static void main(String[] args) {
	//
	// // create an empty array list
	// Test arrlist = new Test();
	//
	// // use add() method to add values in the list
	// arrlist.add(10);
	// arrlist.add(12);
	// arrlist.add(31);
	//
	// // print the list
	// System.out.println("The list:" + arrlist);
	//
	// // removing range of 1st 2 elements
	// arrlist.removeRange( arrlist.size()-1, arrlist.size());
	// System.out.println("The list after using removeRange:" + arrlist);
	// arrlist.add(31);
	// System.out.println("The list after using removeRange:" + arrlist);
	// }

	public static void test1() throws Exception {
		throw new Exception("hello");
	}

	public static void main(String[] args) throws IOException {

		TestHandler th = new TestHandler("C:\\data\\Test2\\");
		th.performTest();
//		String line;
//		BufferedReader br = new BufferedReader(new FileReader(
//				"C:\\data\\Test2\\dblp_anf_time_500.csv"));
//		BufferedWriter bw = new BufferedWriter(new FileWriter(
//				"C:\\data\\Test2\\anf_time_500.csv"));
//		int c = 0, sum = 0;
//		while ((line = br.readLine()) != null) {
//			sum += Integer.parseInt(line);
//			c++;
//			if (c == 2) {
//				c = 0;
//				bw.write(sum + "\n");
//				sum=0;
//			}
//		}
//		bw.flush();
//		bw.close();
//		br.close();
		// HyperLogLog hll = new HyperLogLog(Constants.numberOfBuckets);
		// HyperLogLog hll1 = new HyperLogLog(Constants.numberOfBuckets);
		// for (int i = 0; i < 10000; i++)
		// hll.add(i);
		//
		// for (int i = 0; i < 10000; i++)
		// hll1.add(i);
		//
		// System.out.println(hll1.estimate());
		// System.out.println(hll.estimate());
		// hll1.union(hll.buckets());
		// System.out.println(hll.estimate());
		// System.out.println(hll1.estimate());
		// // Auto-generated method stub
		// BufferedReader br = null;
		// String line;
		// String[] data;
		// int bukcet = 1024;
		// HashSet<Integer> tset = new HashSet<>();
		// try {
		// br = new BufferedReader(new FileReader("C://data/higgs_final.csv"));
		// HyperLogLog hll = new HyperLogLog(bukcet);
		// SlidingHLL shll = new SlidingHLL(bukcet);
		// HashSet<Integer> set = new HashSet<>();
		// int num;
		// // int i = 0;
		// // while ((line = br.readLine()) != null) {
		// // data = line.split(",");
		// // num = Integer.parseInt(data[1]);
		// Random r = new Random();
		// for (int i = 0; i < 100000; i++) {
		// num = r.nextInt(1000000);
		//
		// hll.add(num);
		// shll.add(num);
		// set.add(num);
		// tset.add(num);
		// // if (i % 1000 == 0 && i != 0) {
		// if (set.size() == 2000) {
		// System.out.println(hll.estimate() + "," + shll.estimate()
		// + "," + set.size());
		// hll = new HyperLogLog(bukcet);
		// shll = new SlidingHLL(bukcet);
		// set = new HashSet<>();
		// }
		//
		// i++;
		// }
		//
		// } finally {
		// try {
		// if (br != null)
		// br.close();
		// } catch (IOException ex) {
		// ex.printStackTrace();
		// }
		// }
		// System.out.println(tset.size());
	}

	private static void analysis() {

	}

	private static void testApprox() {
		int kb = 1024 * 1024;
		System.out.println(new Date());
		// Getting the runtime reference from system
		Runtime runtime = Runtime.getRuntime();
		long initialmemory = (runtime.totalMemory() - runtime.freeMemory())
				/ kb;
		int node = 60000;
		ArrayList<NodeApprox> nalist = new ArrayList<NodeApprox>();
		SlidingHLL hll = new SlidingHLL(1024);
		for (int i = 0; i < 2000; i++) {
			hll.add(i);
		}

		for (int i = 0; i < node; i++) {
			NodeApprox na = new NodeApprox(i, 3, 1024);
			na.getDistanceWiseSummaries().remove(0);
			na.getDistanceWiseSummaries().remove(0);
			na.getDistanceWiseSummaries().remove(0);
			SlidingHLL hll1 = new SlidingHLL(8);
			for (int j = 0; j < 200; j++) {
				hll1.add(j, j);
			}
			na.getDistanceWiseSummaries().add(hll1);
			SlidingHLL hll2 = new SlidingHLL(8);
			for (int j = 200; j < 400; j++) {
				hll2.add(j, j);
			}
			na.getDistanceWiseSummaries().add(hll2);
			SlidingHLL hll3 = new SlidingHLL(8);
			for (int j = 400; j < 600; j++) {
				hll3.add(j, j);
			}
			na.getDistanceWiseSummaries().add(hll3);

			nalist.add(na);

		}
		System.out
				.println("Used Memory:"
						+ ((runtime.totalMemory() - runtime.freeMemory()) / kb - initialmemory));
		System.out.println(new Date());
	}

	private static void testExact() {
		int kb = 1024 * 1024;
		System.out.println(new Date());
		// Getting the runtime reference from system
		Runtime runtime = Runtime.getRuntime();
		long initialmemory = (runtime.totalMemory() - runtime.freeMemory())
				/ kb;
		int node = 60000;
		ArrayList<NodeExact> nalist = new ArrayList<NodeExact>();

		for (int i = 0; i < node; i++) {
			NodeExact na = new NodeExact(i, 3);
			na.getDistanceWiseSummaries().remove(0);
			na.getDistanceWiseSummaries().remove(0);
			na.getDistanceWiseSummaries().remove(0);
			SlidingExactCounting hll1 = new SlidingExactCounting();
			for (int j = 0; j < 200; j++) {
				hll1.add(j, j);
			}
			na.getDistanceWiseSummaries().add(hll1);
			SlidingExactCounting hll2 = new SlidingExactCounting();
			for (int j = 200; j < 400; j++) {
				hll2.add(j, j);
			}
			na.getDistanceWiseSummaries().add(hll2);
			SlidingExactCounting hll3 = new SlidingExactCounting();
			for (int j = 400; j < 600; j++) {
				hll3.add(j, j);
			}
			na.getDistanceWiseSummaries().add(hll3);

			nalist.add(na);

		}
		System.out
				.println("Used Memory :"
						+ ((runtime.totalMemory() - runtime.freeMemory()) / kb - initialmemory));
		System.out.println(new Date());
	}
}
