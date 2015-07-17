package com.ulb.code.wit.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ulb.code.wit.graphs.approx.GraphSummaryApprox;
import com.ulb.code.wit.graphs.approx.ImprovedGSA;
import com.ulb.code.wit.graphs.approx.NodeApprox;
import com.ulb.code.wit.graphs.exact.GraphSummaryExact;
import com.ulb.code.wit.graphs.exact.NodeExact;
import com.ulb.code.wit.util.Constants;
import com.ulb.code.wit.util.Constants.Commands;
import com.ulb.code.wit.util.Constants.PropagationType;

public class TestHLL {
	// static String home="//Users//rk//Documents//data";

	public static void main(String[] args) {
		try {
			// int kb = 1024;
			// System.out.println(new Date());
			// // Getting the runtime reference from system
			//
			// Runtime runtime = Runtime.getRuntime();
			//
			String home = "C://data/testdata";
			int distance = 3, thread = 4;
			String testNo = "random" + Constants.numberOfBuckets;
			String inputData = "dblp_data", resultData = home + "//"
					+ inputData + "_" + testNo;
			//
			long window = 200000;
			File resultFolder = new File(resultData);
			if (!resultFolder.exists()) {
				resultFolder.mkdir();
			}
			// System.out.println(resultData);
			// testExact(PropagationType.NODEWISE, home + "//" + inputData
			// + ".csv", resultData, distance, window);
			// testExact(PropagationType.DISTANCEWISE, home + "//" + inputData
			// + ".csv", resultData, distance, window);
//			 testApprox(PropagationType.NODEWISE, home + "/" + inputData
//			 + ".csv", resultData, distance, 10000);

//			 testApprox(PropagationType.DISTANCEWISE, home + "/" + inputData
//			 + ".csv", resultData, distance, window);

//			 testApproxParallel(PropagationType.DISTANCEWISE, home + "/"
//			 + inputData + ".csv", resultData, distance, window, thread);
			// // testApproxImrpoved(PropogationType.DISTANCEWISE, home + "/"
			// + inputData + ".csv", resultData, distance, window);

			// testExact(PropogationType.DISTANCEWISE);
			// System.out.println("**********************");
			// testExact(PropogationType.NODEWISE);
			// testApprox(PropagationType.NODEWISE);

			// Print used memory
			// System.gc();// cleaning a bit
			// System.out
			// .println("Used Memory:"
			// + (runtime.totalMemory() - runtime.freeMemory())
			// / kb );
			// System.out.println(new Date());

			// testApproxHyperANF();
			// long starttime = new Date().getTime();
			// testApproxHyperANFIO("C://data/testdata/dblp_data.csv",
			// "C://data/testdata/dblp_io_anf.csv",
			// Constants.numberOfBuckets, Constants.DISTANCE);
			// long finishtime = new Date().getTime();
			// System.out.println("time taken :" + (finishtime - starttime));
			long starttime = new Date().getTime();
			int freq =800000;
//			System.out.println("C://data/Test2/dblp_anf_time_" + freq + ".csv");
//			testApproxHyperANF("C://data/testdata/dblp_data.csv",
//					"C://data/Test2/dblp_anf_estimate.csv",
//					"C://data/Test2/dblp_anf_time_" + freq + ".csv",
//					Constants.numberOfBuckets, Constants.DISTANCE, freq, freq);
//			testApproxHyperANFIO("C://data/testdata/dblp_data.csv",
//					 "C://data/testdata/dblp_io_anf.csv",
//					 Constants.numberOfBuckets, Constants.DISTANCE,freq);
			long finishtime = new Date().getTime();
//			total+=(finishtime - starttime);
//			System.out.println("time taken :" +freq+","+ (finishtime - starttime));	
			Random r=new Random();
			long total=0;
			for ( int i=0;i<1000l;i++){
				 starttime = new Date().getTime();
				 freq = r.nextInt(800000);
//				System.out.println("C://data/Test2/dblp_anf_time_" + freq + ".csv");
				testApproxHyperANFIO("C://data/testdata/dblp_data.csv",
						 "C://data/testdata/dblp_io_anf.csv",
						 Constants.numberOfBuckets, Constants.DISTANCE,freq);
				 finishtime = new Date().getTime();
				total+=(finishtime - starttime);
				System.out.println("time taken : "+i+"," +freq+","+ (finishtime - starttime));	
			}
			System.out.println("AVG: "+total/1000);
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void testApproxHyperANF() {
		ArrayList<String> data = new ArrayList<String>();

		data.add("1,2");

		data.add("2,4");

		data.add("3,5");

		data.add("4,5");

		data.add("1,3");

		data.add("1,4");

		data.add("1,5");

		data.add("1,2");

		data.add("4,5");

		data.add("1,3");
		HyperANF anf = new HyperANF(Constants.numberOfBuckets,
				Constants.DISTANCE);
		anf.parse(data);

	}

	public static void testApproxHyperANF(String filePath,
			String outfileEstimate, String outFileTime, int numberofBuckets,
			int distance, int freq, int window) throws IOException {
		ArrayList<String> data = new ArrayList<String>();
		HyperANF anf = new HyperANF(numberofBuckets, distance);
		BufferedReader br = null;
		String line;
		File fout = new File(outfileEstimate);

		try {
			br = new BufferedReader(new FileReader(filePath));
			int count = 0;
			while ((line = br.readLine()) != null) {
				data.add(line);
				count++;
//				if (count > window) {
//					break;
//				}
			}
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		int parts = data.size() / freq;
		int initial = 0;
		long starttime;
		long finishtime;
		ArrayList<Long> time = new ArrayList<Long>();
		ArrayList<String> subdata = new ArrayList<String>();
//		for (int i = 1; i <= parts; i++) {
//
//			subdata = new ArrayList<String>(data.subList(initial, i * freq));
//			starttime = new Date().getTime();
//			anf.parse(subdata);
//			finishtime = new Date().getTime();
//			time.add(finishtime - starttime);
////			if (i * freq % 10000 == 0) {
////				System.out.println("done for " + (i * freq) + new Date());
////			}
//		}
		anf.parse(data);
//		FileWriter fw = new FileWriter(fout);
//		BufferedWriter bw = new BufferedWriter(fw);
//		for (int node : anf.graph.keySet()) {
//
//			bw.write(node + "," + anf.graph.get(node).get(distance).estimate()
//					+ "\n");
//		}
//		bw.flush();
//		bw.close();
//
//		fout = new File(outFileTime);
//		fw = new FileWriter(fout);
//		bw = new BufferedWriter(fw);
//		for (long t : time) {
//
//			bw.write(t + "\n");
//		}
//		bw.flush();
//		bw.close();

	}

	public static void testApproxHyperANFIO(String filePath, String outfile,
			int numberofBuckets, int distance,int limit) throws IOException {

		HyperANF anf = new HyperANF(numberofBuckets, distance);

		File fout = new File(outfile);

		anf.parse(filePath,limit);


	}

	private static void testExact(PropagationType pt) {
		GraphSummaryExact gse = new GraphSummaryExact(3, pt);
		System.out.println("size:" + gse.addEdge(1, 2, 1));
		System.out.println("size:" + gse.addEdge(2, 4, 2));
		System.out.println("size:" + gse.addEdge(3, 5, 3));
		// System.out.println(gse.addEdge(4, 5, 4));
		// System.out.println(gse.addEdge(1, 3, 5));
		// System.out.println(gse.addEdge(1, 4, 6));
		// System.out.println(gse.addEdge(1, 5, 7));
		// System.out.println(gse.addEdge(1, 2, 8));
		// System.out.println(gse.addEdge(4, 5, 9));
		// System.out.println(gse.addEdge(1, 3, 10));
		// gse.addEdge(1, 2, 1);
		// gse.addEdge(2, 4, 2);
		// gse.addEdge(3, 5, 3);
		// gse.addEdge(4, 5, 4);
		// gse.addEdge(1, 3, 5);
		// gse.addEdge(1, 4, 6);
		// gse.addEdge(1, 5, 7);
		// gse.addEdge(1, 2, 8);
		// gse.addEdge(4, 5, 9);
		// gse.addEdge(1, 3, 10);
		// for (Entry<Integer, NodeExact> entry : gse.getGraph().entrySet()) {
		// System.out.println(entry.getKey() + ":"
		// + entry.getValue().getNodeSummary() + " : "
		// + entry.getValue().getDistanceWiseSummaries().get(0)
		// + " : "
		// + entry.getValue().getDistanceWiseSummaries().get(1)
		// + " : "
		// + entry.getValue().getDistanceWiseSummaries().get(2));
		// }
		// long window = 7l;
		//
		// System.out.println("with window :" + window);
		// for (Entry<Integer, NodeExact> entry : gse.getGraph().entrySet()) {
		// System.out.println(entry.getKey() + ":"
		// + entry.getValue().getNodeSummary(window));
		// }
	}

	private static void testApprox(PropagationType pt) {
		GraphSummaryApprox gse = new GraphSummaryApprox(3, pt, false,
				Constants.numberOfBuckets);

		gse.addEdge(1, 2, 1);
		gse.addEdge(2, 4, 2);
		gse.addEdge(3, 5, 3);
		gse.addEdge(4, 5, 4);
		gse.addEdge(1, 3, 5);
		gse.addEdge(1, 4, 6);
		gse.addEdge(1, 5, 7);
		gse.addEdge(1, 2, 8);
		gse.addEdge(4, 5, 9);
		gse.addEdge(1, 3, 10);

		for (Entry<Integer, NodeApprox> entry : gse.getGraph().entrySet()) {
			System.out.println(entry.getKey() + ":"
					+ entry.getValue().getNodeSummary().estimate());
		}
		long window = 7l;

		System.out.println("with window :" + window);
		for (Entry<Integer, NodeApprox> entry : gse.getGraph().entrySet()) {
			System.out.println(entry.getKey() + ":"
					+ entry.getValue().getNodeSummary().estimate(window));
		}
	}

	private static void testExact(PropagationType ptype, String filePath,
			String outFolder, int distance, long window, int freq)
			throws IOException {
		GraphSummaryExact gse = new GraphSummaryExact(distance, ptype);
		String[] data;
		long timestart = 0, timeend;
		ArrayList<Long> timetake = new ArrayList<Long>();
		int count = 0;
		int totalcount = 0;
		BufferedReader br = null;
		String line;
		ArrayList<Integer> propogationSize = new ArrayList<Integer>();
		long totaltime1 = 0, totaltime60 = 0, totaltime600 = 0, totaltime3600 = 0;
		try {
			br = new BufferedReader(new FileReader(filePath));

			while ((line = br.readLine()) != null) {
				totalcount++;
				if (count == 0) {
					timestart = new Date().getTime();
				}
				data = line.split(",");

				propogationSize.add(gse.addEdge(Integer.parseInt(data[0]),
						Integer.parseInt(data[1]), Long.parseLong(data[2])));
				count++;
				// if (count == 1000) {
				// timeend = new Date().getTime();
				// count = 0;
				// timetake.add(timeend - timestart);
				// System.out.println("done for " + totalcount);
				// }

				if (count % 1 == 0) {
					timestart = new Date().getTime();
					for (Entry<Integer, NodeExact> entry : gse.getGraph()
							.entrySet()) {

						line = (entry.getKey() + "," + entry.getValue()
								.getNodeSummary());

					}
					timeend = new Date().getTime();
					totaltime1 += (timeend - timestart);

				}
				if (count % 60 == 0) {
					timestart = new Date().getTime();
					for (Entry<Integer, NodeExact> entry : gse.getGraph()
							.entrySet()) {

						line = (entry.getKey() + "," + entry.getValue()
								.getNodeSummary());

					}
					timeend = new Date().getTime();
					totaltime60 += (timeend - timestart);

				}
				if (count % 600 == 0) {
					timestart = new Date().getTime();
					for (Entry<Integer, NodeExact> entry : gse.getGraph()
							.entrySet()) {

						line = (entry.getKey() + "," + entry.getValue()
								.getNodeSummary());

					}
					timeend = new Date().getTime();
					totaltime600 += (timeend - timestart);

				}
				if (count % 3600 == 0) {
					timestart = new Date().getTime();
					for (Entry<Integer, NodeExact> entry : gse.getGraph()
							.entrySet()) {

						line = (entry.getKey() + "," + entry.getValue()
								.getNodeSummary());

					}
					timeend = new Date().getTime();
					totaltime3600 += (timeend - timestart);

				}
				if (totalcount > window) {
					break;
				}
			}
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		System.out.println(" window:"+window);
		System.out.println(" 1 sec "+totaltime1);
		System.out.println(" 60 sec "+totaltime60);
		System.out.println(" 600 sec "+totaltime600);
		System.out.println(" 3600 sec "+totaltime3600);
		/*
		count = 0;
		System.out.println("Parsed all data ex" + totalcount);
		File fout1 = new File(outFolder + "//excat_" + ptype + window
				+ "estimate.csv");

		FileWriter fw1 = new FileWriter(fout1);

		for (Entry<Integer, NodeExact> entry : gse.getGraph().entrySet()) {
			timestart = new Date().getTime();
			if (window == 0l)
				line = (entry.getKey() + "," + entry.getValue()
						.getNodeSummary());
			else
				line = (entry.getKey() + ":" + entry.getValue().getNodeSummary(
						window));
			timeend = new Date().getTime();
			line = line + "," + (timeend - timestart) + "\n";
			count++;
			fw1.write(line);
		}
		fw1.flush();
		fw1.close();

		window = 1000;
		fout1 = new File(outFolder + "//excat_" + ptype + window
				+ "estimate.csv");

		fw1 = new FileWriter(fout1);
		for (Entry<Integer, NodeExact> entry : gse.getGraph().entrySet()) {
			timestart = new Date().getTime();
			if (window == 0l)
				line = (entry.getKey() + "," + entry.getValue()
						.getNodeSummary());
			else
				line = (entry.getKey() + ":" + entry.getValue().getNodeSummary(
						window));
			timeend = new Date().getTime();
			line = line + "," + (timeend - timestart) + "\n";
			count++;
			fw1.write(line);
		}
		fw1.flush();
		fw1.close();
		window = 2000;
		fout1 = new File(outFolder + "//excat_" + ptype + window
				+ "estimate.csv");

		fw1 = new FileWriter(fout1);
		for (Entry<Integer, NodeExact> entry : gse.getGraph().entrySet()) {
			timestart = new Date().getTime();
			if (window == 0l)
				line = (entry.getKey() + "," + entry.getValue()
						.getNodeSummary());
			else
				line = (entry.getKey() + ":" + entry.getValue().getNodeSummary(
						window));
			timeend = new Date().getTime();
			line = line + "," + (timeend - timestart) + "\n";
			count++;
			fw1.write(line);
		}
		fw1.flush();
		fw1.close();
		window = 3000;
		fout1 = new File(outFolder + "//excat_" + ptype + window
				+ "estimate.csv");

		fw1 = new FileWriter(fout1);
		for (Entry<Integer, NodeExact> entry : gse.getGraph().entrySet()) {
			timestart = new Date().getTime();
			if (window == 0l)
				line = (entry.getKey() + "," + entry.getValue()
						.getNodeSummary());
			else
				line = (entry.getKey() + ":" + entry.getValue().getNodeSummary(
						window));
			timeend = new Date().getTime();
			line = line + "," + (timeend - timestart) + "\n";
			count++;
			fw1.write(line);
		}
		fw1.flush();
		fw1.close();
		window = 4000;
		fout1 = new File(outFolder + "//excat_" + ptype + window
				+ "estimate.csv");

		fw1 = new FileWriter(fout1);
		for (Entry<Integer, NodeExact> entry : gse.getGraph().entrySet()) {
			timestart = new Date().getTime();
			if (window == 0l)
				line = (entry.getKey() + "," + entry.getValue()
						.getNodeSummary());
			else
				line = (entry.getKey() + ":" + entry.getValue().getNodeSummary(
						window));
			timeend = new Date().getTime();
			line = line + "," + (timeend - timestart) + "\n";
			count++;
			fw1.write(line);
		}
		window = 6000;
		fw1.flush();
		fw1.close();
		fout1 = new File(outFolder + "//excat_" + ptype + window
				+ "estimate.csv");

		fw1 = new FileWriter(fout1);
		for (Entry<Integer, NodeExact> entry : gse.getGraph().entrySet()) {
			timestart = new Date().getTime();
			if (window == 0l)
				line = (entry.getKey() + "," + entry.getValue()
						.getNodeSummary());
			else
				line = (entry.getKey() + ":" + entry.getValue().getNodeSummary(
						window));
			timeend = new Date().getTime();
			line = line + "," + (timeend - timestart) + "\n";
			count++;
			fw1.write(line);
		}
		fw1.flush();
		fw1.close();
		File fout = new File(outFolder + "//excat_" + ptype + "time.csv");
		FileWriter fw = new FileWriter(fout);
		for (Long t : timetake) {
			fw.write(t + "\n");
		}
		fw.flush();
		fw.close();

		fout = new File(outFolder + "//excat_" + ptype + "propogation.csv");
		fw = new FileWriter(fout);
		for (int t : propogationSize) {
			fw.write(t + "\n");
		}
		fw.flush();
		fw.close();
		*/
	}

	private static void testApprox(PropagationType ptype, String filePath,
			String outFolder, int distance, long window) throws IOException {
		GraphSummaryApprox gse = new GraphSummaryApprox(distance, ptype, true,
				Constants.numberOfBuckets);
		String[] data;
		long timestart = 0, timeend;
		ArrayList<Integer> propogationSize = new ArrayList<Integer>();
		int count = 0;
		int totalcount = 0;
		BufferedReader br = null;
		String line;
		File fout = new File(outFolder + "//approx_" + ptype + "time.csv");
		FileWriter fw = new FileWriter(fout);
		BufferedWriter bw = new BufferedWriter(fw);
		long totaltime1 = 0, totaltime60 = 0, totaltime600 = 0, totaltime3600 = 0,processingtime=0;
		try {
			br = new BufferedReader(new FileReader(filePath));

			while ((line = br.readLine()) != null) {

				totalcount++;
				if (count == 0) {
					timestart = new Date().getTime();
				}
				count++;
				data = line.split(",");
				timestart = new Date().getTime();
				propogationSize.add(gse.addEdge(Integer.parseInt(data[0]),
						Integer.parseInt(data[1]), Long.parseLong(data[2])));
				timeend = new Date().getTime();
				processingtime += (timeend - timestart);

//				if (totalcount % 1000 == 0) {
//					timeend = new Date().getTime();
//					count = 0;
//
//					bw.write((timeend - timestart) + "\n");
//
//				}
				if(totalcount%1000==0){
				System.out.println(totalcount);
				}
//				if (count % 1 == 0) {
//					timestart = new Date().getTime();
//					for (Entry<Integer, NodeApprox> entry : gse.getGraph()
//							.entrySet()) {
//
//						line = (entry.getKey() + "," + entry.getValue()
//								.getNodeSummary().estimate());
//
//					}
//					timeend = new Date().getTime();
//					totaltime1 += (timeend - timestart);
//
//				}
				if (count % 60 == 0) {
					timestart = new Date().getTime();
					for (Entry<Integer, NodeApprox> entry : gse.getGraph()
							.entrySet()) {

						line = (entry.getKey() + "," + entry.getValue()
								.getNodeSummary().estimate());

					}
					timeend = new Date().getTime();
					totaltime60 += (timeend - timestart);

				}
				if (count % 600 == 0) {
					timestart = new Date().getTime();
					for (Entry<Integer, NodeApprox> entry : gse.getGraph()
							.entrySet()) {

						line = (entry.getKey() + "," + entry.getValue()
								.getNodeSummary().estimate());

					}
					timeend = new Date().getTime();
					totaltime600 += (timeend - timestart);

				}
				if (count % 3600 == 0) {
					timestart = new Date().getTime();
					for (Entry<Integer, NodeApprox> entry : gse.getGraph()
							.entrySet()) {

						line = (entry.getKey() + "," + entry.getValue()
								.getNodeSummary().estimate());

					}
					timeend = new Date().getTime();
					totaltime3600 += (timeend - timestart);

				}
				if (totalcount > window) {
					break;
				}
			}
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		System.out.println(" window:"+window);
		System.out.println(" 1 sec "+totaltime1);
		System.out.println(" 60 sec "+totaltime60);
		System.out.println(" 600 sec "+totaltime600);
		System.out.println(" 3600 sec "+totaltime3600);
		System.out.println(" processing timce "+processingtime);

//		bw.flush();
//		bw.close();
//		count = 0;
//		fout = new File(outFolder + "//approx_" + ptype + "propogation.csv");
//		fw = new FileWriter(fout);
//		for (int t : propogationSize) {
//			fw.write(t + "\n");
//		}
//		fw.flush();
//		fw.close();
//		System.out.println("Parsed all data " + totalcount);

		// File fout1 = new File(outFolder + "//approx_" + ptype +
		// "estimate.csv");
		//
		// FileWriter fw1 = new FileWriter(fout1);
		//
		// for (Entry<Integer, NodeApprox> entry : gse.getGraph().entrySet()) {
		// timestart = new Date().getTime();
		// if (window == 0l)
		// line = (entry.getKey() + "," + entry.getValue()
		// .getNodeSummary().estimate());
		// else
		// line = (entry.getKey() + ":" + entry.getValue()
		// .getNodeSummary().estimate(window));
		// timeend = new Date().getTime();
		// line = line + "," + (timeend - timestart) + "\n";
		// count++;
		// fw1.write(line);
		// }
		// fw1.flush();
		// fw1.close();

	}

	private static void testApproxParallel(PropagationType ptype,
			String filePath, String outFolder, int distance, long window,
			int threads) throws IOException, InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		ExecutorCompletionService ecs = new ExecutorCompletionService(executor);
		System.out.println("started");
		GraphSummaryApprox gse = new GraphSummaryApprox(distance, ptype, false,
				Constants.numberOfBuckets);
		String[] data;
		long timestart = 0, timeend;
		ArrayList<Long> timetake = new ArrayList<Long>();
		int count = 0;
		int totalcount = 0;
		BufferedReader br = null;
		String line;
		try {
			br = new BufferedReader(new FileReader(filePath));

			while ((line = br.readLine()) != null) {

				totalcount++;
				if (count == 0) {
					timestart = new Date().getTime();
				}
				count++;
				data = line.split(",");
				Callable parser = new ParserThread(gse,
						Integer.parseInt(data[0]), Integer.parseInt(data[1]),
						Long.parseLong(data[2]));
				// executor.execute(parser);
				ecs.submit(parser);
				// gse.addEdge(Integer.parseInt(data[0]),
				// Integer.parseInt(data[1]), Long.parseLong(data[2]));

				if (count == 1000) {
					for (int i = 0; i < 1000; i++) {
						ecs.take();
					}
					timeend = new Date().getTime();
					count = 0;
					timetake.add(timeend - timestart);
					

					// executor.shutdown();
					// while (!executor.isTerminated()) {
					//
					// }
					// executor = Executors.newFixedThreadPool(threads);
				}

				 if (totalcount % 10000==0) {
					 System.out.println("done for " + totalcount);
				 }
			}

		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		count = 0;
		executor.shutdown();
		while (!executor.isTerminated()) {

		}
		System.out.println("Parsed all data in parallel" + totalcount);
		/*
		File fout = new File(outFolder + "//approx_" + ptype + "time.csv");
		FileWriter fw = new FileWriter(fout);
		for (Long t : timetake) {
			fw.write(t + "\n");
		}
		fw.flush();
		fw.close();

		File fout1 = new File(outFolder + "//approx_estimate.csv");

		FileWriter fw1 = new FileWriter(fout1);

		// for (Entry<Integer, NodeApprox> entry : gse.getGraph().entrySet()) {
		// timestart = new Date().getTime();
		// if (window == 0l)
		// line = (entry.getKey() + "," + entry.getValue()
		// .getNodeSummary().estimate());
		// else
		// line = (entry.getKey() + ":" + entry.getValue()
		// .getNodeSummary().estimate(window));
		// timeend = new Date().getTime();
		// // line = line + "," + (timeend - timestart) + "\n";
		// line = (timeend - timestart) + "\n";
		// count++;
		// fw1.write(line);
		// }
		window = 1000;
		fout1 = new File(outFolder + "//approx_estimate" + window + ".csv");

		fw1 = new FileWriter(fout1);
		for (Entry<Integer, NodeApprox> entry : gse.getGraph().entrySet()) {
			timestart = new Date().getTime();
			if (window == 0l)
				line = (entry.getKey() + "," + entry.getValue()
						.getNodeSummary().estimate());
			else
				line = (entry.getKey() + ":" + entry.getValue()
						.getNodeSummary().estimate(window));
			timeend = new Date().getTime();
			line = line + "," + (timeend - timestart) + "\n";
			count++;
			fw1.write(line);
		}
		window = 10000;
		fout1 = new File(outFolder + "//approx_estimate" + window + ".csv");

		fw1 = new FileWriter(fout1);
		for (Entry<Integer, NodeApprox> entry : gse.getGraph().entrySet()) {
			timestart = new Date().getTime();
			if (window == 0l)
				line = (entry.getKey() + "," + entry.getValue()
						.getNodeSummary().estimate());
			else
				line = (entry.getKey() + ":" + entry.getValue()
						.getNodeSummary().estimate(window));
			timeend = new Date().getTime();
			line = line + "," + (timeend - timestart) + "\n";
			count++;
			fw1.write(line);
		}
		window = 100000;
		fout1 = new File(outFolder + "//approx_estimate" + window + ".csv");
		fw1 = new FileWriter(fout1);
		for (Entry<Integer, NodeApprox> entry : gse.getGraph().entrySet()) {
			timestart = new Date().getTime();
			if (window == 0l)
				line = (entry.getKey() + "," + entry.getValue()
						.getNodeSummary().estimate());
			else
				line = (entry.getKey() + ":" + entry.getValue()
						.getNodeSummary().estimate(window));
			timeend = new Date().getTime();
			line = line + "," + (timeend - timestart) + "\n";
			count++;
			fw1.write(line);
		}
		
		 * window = 4000; fout1 = new File(outFolder + "//approx_estimate" +
		 * window + ".csv");
		 * 
		 * fw1 = new FileWriter(fout1); for (Entry<Integer, NodeApprox> entry :
		 * gse.getGraph().entrySet()) { timestart = new Date().getTime(); if
		 * (window == 0l) line = (entry.getKey() + "," + entry.getValue()
		 * .getNodeSummary().estimate()); else line = (entry.getKey() + ":" +
		 * entry.getValue() .getNodeSummary().estimate(window)); timeend = new
		 * Date().getTime(); line = line + "," + (timeend - timestart) + "\n";
		 * count++; fw1.write(line); } fw1.flush(); fw1.close(); window = 6000;
		 * fout1 = new File(outFolder + "//approx_estimate" + window + ".csv");
		 * 
		 * fw1 = new FileWriter(fout1); for (Entry<Integer, NodeApprox> entry :
		 * gse.getGraph().entrySet()) { timestart = new Date().getTime(); if
		 * (window == 0l) line = (entry.getKey() + "," + entry.getValue()
		 * .getNodeSummary().estimate()); else line = (entry.getKey() + ":" +
		 * entry.getValue() .getNodeSummary().estimate(window)); timeend = new
		 * Date().getTime(); line = line + "," + (timeend - timestart) + "\n";
		 * count++; fw1.write(line); } fw1.flush(); fw1.close();
		 */

	}

	private static void testApproxImrpoved(PropagationType ptype,
			String filePath, String outFolder, int distance, long window)
			throws IOException {
		ImprovedGSA gse = new ImprovedGSA(distance, ptype,
				Constants.numberOfBuckets);
		String[] data;
		long timestart = 0, timeend;
		ArrayList<Long> timetake = new ArrayList<Long>();
		int count = 0;
		int totalcount = 0;
		BufferedReader br = null;
		String line;
		try {
			br = new BufferedReader(new FileReader(filePath));

			while ((line = br.readLine()) != null) {

				totalcount++;
				if (count == 0) {
					timestart = new Date().getTime();
				}
				count++;
				data = line.split(",");

				gse.addEdge(Integer.parseInt(data[0]),
						Integer.parseInt(data[1]), Long.parseLong(data[2]));

				if (count == 1000) {
					timeend = new Date().getTime();
					count = 0;
					timetake.add(timeend - timestart);

				}
				// if (totalcount > 10000) {
				// break;
				// }

			}
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		count = 0;
		System.out.println("Parsed all data approx imrpoved" + totalcount);

		File fout1 = new File(outFolder + "//approx_estimate.csv");

		FileWriter fw1 = new FileWriter(fout1);

		for (Entry<Integer, NodeApprox> entry : gse.getGraph().entrySet()) {
			timestart = new Date().getTime();
			if (window == 0l)
				line = (entry.getKey() + "," + entry.getValue()
						.getNodeSummary().estimate());
			else
				line = (entry.getKey() + ":" + entry.getValue()
						.getNodeSummary().estimate(window));
			timeend = new Date().getTime();
			line = line + "," + (timeend - timestart) + "\n";
			count++;
			fw1.write(line);
		}
		fw1.flush();
		fw1.close();

		File fout = new File(outFolder + "//approx_" + ptype + "time.csv");
		FileWriter fw = new FileWriter(fout);
		for (Long t : timetake) {
			fw.write(t + "\n");
		}
		fw.flush();
		fw.close();
	}

}
