package com.ulb.code.wit.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ulb.code.wit.graphs.approx.GraphSummaryApprox;
import com.ulb.code.wit.graphs.approx.NodeApprox;
import com.ulb.code.wit.graphs.exact.GraphSummaryExact;
import com.ulb.code.wit.graphs.exact.NodeExact;
import com.ulb.code.wit.util.Constants;
import com.ulb.code.wit.util.Constants.Commands;
import com.ulb.code.wit.util.Constants.PropagationType;

public class TestHandler {
	private String resultFolderPath;
	private Runtime runtime;

	public TestHandler(String resultFolderPath) {
		this.resultFolderPath = resultFolderPath;
		this.runtime = Runtime.getRuntime();
	}

	private boolean parallelVsSerialComparision(String[] params)
			throws Exception {
		boolean status = true;

		String inputFilePath = params[1];
		String inFileName = new File(inputFilePath).getName().replace(".csv",
				"");
		long[] window = { 0l };
		int numberOfBucket = Integer.parseInt(params[2]);
		String outPutFile = resultFolderPath + File.separator + "pvs_"
				+ inFileName + "_" + params[2] + ".csv";

		System.out
				.println("******Started parallel vs serial time used comparision test for "
						+ inputFilePath + " **********");
		String tempSerialFile = resultFolderPath + File.separator
				+ "tempserialFile.csv";

		testApprox(PropagationType.DISTANCEWISE, inputFilePath, tempSerialFile,
				Constants.DISTANCE, 0l, Commands.CHECK_TIME, numberOfBucket);

		String tempParallelFile = resultFolderPath + File.separator
				+ "tempParallelFile.csv";
		testApproxParallel(PropagationType.DISTANCEWISE, inputFilePath,
				tempParallelFile, Constants.DISTANCE, window,
				Constants.numberofThreads, Commands.CHECK_TIME, numberOfBucket);
		System.out
				.println("******Completed parallel vs serial time used comparision test merging results at "
						+ outPutFile + " **********");
		mergeFile(tempSerialFile, tempParallelFile, outPutFile);
		File temp = new File(tempParallelFile);
		temp.delete();
		temp = new File(tempSerialFile);
		temp.delete();

		return status;
	}

	private boolean checkPrecsion(String[] params) throws Exception {
		boolean status = true;

		String inputFilePath = params[1];
		String inFileName = new File(inputFilePath).getName().replace(".csv",
				"");
		long[] window = { 0l, 2000, 4000, 6000 };
		int numberOfBucket;
		String outPutFile;
		String exactOutPut = resultFolderPath + File.separator + inFileName
				+ "_exact.csv";
		System.out.println("******Started precision test for exact **********");
		 testExact(PropagationType.DISTANCEWISE, inputFilePath, exactOutPut,
		 Constants.DISTANCE, window);
		for (int i = 2; i < params.length; i++) {
			numberOfBucket = Integer.parseInt(params[i]);
			System.out.println("******Started precision test for "
					+ numberOfBucket + " **********");

			outPutFile = resultFolderPath + File.separator + inFileName + "_"
					+ params[0] + "_" + numberOfBucket + ".csv";
			testApproxParallel(PropagationType.DISTANCEWISE, inputFilePath,
					outPutFile, Constants.DISTANCE, window,
					Constants.numberofThreads, Commands.CHECK_PRECISION,
					numberOfBucket);

		}
		return status;
	}

	
	private boolean checkTime(String[] params) throws Exception {
		boolean status = true;

		String inputFilePath = params[1];
		String inFileName = new File(inputFilePath).getName().replace(".csv",
				"");
		String outPutFile;
		int numberOfBucket;
		long[] window = { 0l };
		int distance = Integer.parseInt(params[2]);
		for (int i = 3; i < params.length; i++) {
			numberOfBucket = Integer.parseInt(params[i]);
			System.out.println("******Starte time used comparision test for "
					+ numberOfBucket + " **********");

			outPutFile = resultFolderPath + File.separator + inFileName + "_"
					+ params[0] + "_" + numberOfBucket + "_" + distance
					+ ".csv";
			testApproxParallel(PropagationType.DISTANCEWISE, inputFilePath,
					outPutFile, distance, window, Constants.numberofThreads,
					Commands.CHECK_TIME, numberOfBucket);

		}
		return status;
	}

	private boolean checkSpace(String[] params) throws Exception {
		boolean status = true;

		String inputFilePath = params[1];
		String inFileName = new File(inputFilePath).getName().replace(".csv",
				"");
		String outPutFile;
		int numberOfBucket;
		long[] window = { 0l };

		for (int i = 2; i < params.length; i++) {
			numberOfBucket = Integer.parseInt(params[i]);
			System.out.println("******Starte time used comparision test for "
					+ numberOfBucket + " **********");

			outPutFile = resultFolderPath + File.separator + inFileName + "_"
					+ params[0] + "_" + numberOfBucket + ".csv";
			testApproxParallel(PropagationType.DISTANCEWISE, inputFilePath,
					outPutFile, Constants.DISTANCE, window,
					Constants.numberofThreads, Commands.CHECK_SPACE,
					numberOfBucket);

		}
		return status;
	}

	private boolean checkPropogation(String[] params) throws Exception {
		boolean status = true;

		String inputFilePath = params[1];
		String inFileName = new File(inputFilePath).getName().replace(".csv",
				"");
		String outPutFile;
		int numberOfBucket;
		String tempDistanceWise = null, tempNodeWise = null;
		for (int i = 2; i < params.length; i++) {
			numberOfBucket = Integer.parseInt(params[i]);
			System.out.println("******Starte propogation test for "
					+ numberOfBucket + " **********");
			tempDistanceWise = resultFolderPath + File.separator
					+ "tempserialFile.csv";
			tempNodeWise = resultFolderPath + File.separator
					+ "tempParallelFile.csv";
			outPutFile = resultFolderPath + File.separator + inFileName + "_"
					+ params[0] + "_" + numberOfBucket + ".csv";
			testApprox(PropagationType.DISTANCEWISE, inputFilePath,
					tempDistanceWise, Constants.DISTANCE, 0l,
					Commands.CHECK_PROPOGATION, numberOfBucket);
			testApprox(PropagationType.NODEWISE, inputFilePath, tempNodeWise,
					Constants.DISTANCE, 0l, Commands.CHECK_PROPOGATION,
					numberOfBucket);
			mergeFile(tempDistanceWise, tempNodeWise, outPutFile);

		}
		File temp;
		if (tempDistanceWise != null) {
			temp = new File(tempDistanceWise);
			temp.delete();
		}
		if (tempNodeWise != null) {
			temp = new File(tempNodeWise);
			temp.delete();
		}
		return status;
	}

	public void performTest() {
		File resultFolder = new File(resultFolderPath);
		if (!resultFolder.exists()) {
			resultFolder.mkdir();
		}
		BufferedReader br = null;
		String line;
		String[] params;
		boolean starttesting = false;
		try {
			br = new BufferedReader(new FileReader("TestConfiguration.txt"));
			int linenumber = 1;
			while ((line = br.readLine()) != null) {
				if (line.trim().equals("")) {
					continue;
				}
				if (line.equalsIgnoreCase("start")) {
					starttesting = true;
					continue;
				}
				if (starttesting) {
					params = line.split(",");
					if (params[0].equals(Commands.CHECK_PARALLEL_VS_SERIAL
							.value())) {
						try {
							if (params.length != 3) {
								System.out.println("Incorrect Parameters @ "
										+ line + linenumber + " : " + line);
							} else {
								parallelVsSerialComparision(params);
							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} else if (params[0].equals(Commands.CHECK_PRECISION
							.value())) {
						try {
							if (params.length < 3) {
								System.out.println("Incorrect Parameters @ "
										+ line + linenumber + " : " + line);
							} else {
								checkPrecsion(params);
							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} else if (params[0].equals(Commands.CHECK_TIME.value())) {
						try {
							if (params.length < 3) {
								System.out.println("Incorrect Parameters @ "
										+ line + linenumber + " : " + line);
							} else {
								checkTime(params);
							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} else if (params[0].equals(Commands.CHECK_PROPOGATION
							.value())) {
						try {
							if (params.length < 3) {
								System.out.println("Incorrect Parameters @ "
										+ line + linenumber + " : " + line);
							} else {
								checkPropogation(params);
							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} else if (params[0].equals(Commands.CHECK_SPACE.value())) {
						try {
							if (params.length < 4) {
								System.out.println("Incorrect Parameters @ "
										+ line + linenumber + " : " + line);
							} else {
								checkSpace(params);
							}

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} else {
						System.out.println("Unrecognised command @ " + line
								+ linenumber + " : " + line);
					}

				}
				linenumber++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (br != null)
					br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static void testExact(PropagationType ptype, String filePath,
			String outFile, int distance, long[] window) throws IOException {
		GraphSummaryExact gse = new GraphSummaryExact(distance, ptype);
		String[] data;
		int totalcount = 0;
		BufferedReader br = null;
		String line;
		try {
			br = new BufferedReader(new FileReader(filePath));

			while ((line = br.readLine()) != null) {
				totalcount++;

				data = line.split(",");

				gse.addEdge(Integer.parseInt(data[0]),
						Integer.parseInt(data[1]), Long.parseLong(data[2]));
				if (totalcount % 10000 == 0) {
					System.out.println("done for " + totalcount + " "
							+ new Date());
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

		System.out.println("Parsed all data ex" + totalcount);
		File fout1 = new File(outFile);

		FileWriter fw1 = new FileWriter(fout1);

		for (Entry<Integer, NodeExact> entry : gse.getGraph().entrySet()) {
			line = entry.getKey() + "";
			for (long win : window) {
				if (win == 0l)
					line += ("," + entry.getValue().getNodeSummary());
				else
					line += ("," + entry.getValue().getNodeSummary(win));

			}
			line = line + "\n";

			fw1.write(line);
		}
		fw1.flush();
		fw1.close();

	}

	private static void testApprox(PropagationType ptype, String filePath,
			String outFile, int distance, long window, Commands cmd,
			int numberOfBucket) throws IOException {
		GraphSummaryApprox gse;
		if (cmd == Commands.CHECK_PROPOGATION) {
			gse = new GraphSummaryApprox(distance, ptype, true, numberOfBucket);
		} else {
			gse = new GraphSummaryApprox(distance, ptype, false, numberOfBucket);
		}

		String[] data;
		long timestart = 0, timeend;
		ArrayList<Long> timetake = new ArrayList<Long>();
		int count = 0;
		int totalcount = 0;
		BufferedReader br = null;
		String line;
		ArrayList<Integer> propogationSize = new ArrayList<Integer>();
		try {
			br = new BufferedReader(new FileReader(filePath));

			while ((line = br.readLine()) != null) {

				totalcount++;
				if (count == 0) {
					timestart = new Date().getTime();
				}
				count++;
				data = line.split(",");

				propogationSize.add(gse.addEdge(Integer.parseInt(data[0]),
						Integer.parseInt(data[1]), Long.parseLong(data[2])));

				if (count == 1000) {
					timeend = new Date().getTime();
					count = 0;
					timetake.add(timeend - timestart);

				}
				if (totalcount % 10000 == 0) {
					System.out.println("done for " + totalcount + " "
							+ new Date());
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
		BufferedWriter bw;
		File fout;
		FileWriter fw;

		System.out.println("Parsed all data in serial" + totalcount);
		if (cmd == Commands.CHECK_PRECISION) {
			fout = new File(outFile);

			fw = new FileWriter(fout);
			bw = new BufferedWriter(fw);
			for (Entry<Integer, NodeApprox> entry : gse.getGraph().entrySet()) {

				if (window == 0l)
					line = (entry.getKey() + "," + entry.getValue()
							.getNodeSummary().estimate());
				else
					line = (entry.getKey() + ":" + entry.getValue()
							.getNodeSummary().estimate(window));

				line = line + "\n";
				count++;
				fw.write(line);
			}
			bw.flush();
			bw.close();
		}
		if (cmd == Commands.CHECK_TIME) {
			fout = new File(outFile);
			fw = new FileWriter(fout);
			bw = new BufferedWriter(fw);
			for (Long t : timetake) {
				fw.write(t + "\n");
			}
			bw.flush();
			bw.close();
		}
		if (cmd == Commands.CHECK_PROPOGATION) {
			fout = new File(outFile);
			fw = new FileWriter(fout);
			bw = new BufferedWriter(fw);
			for (int t : propogationSize) {
				fw.write(t + "\n");
			}
			bw.flush();
			bw.close();
		}
	}

	private void testApproxParallel(PropagationType ptype, String filePath,
			String outFile, int distance, long[] window, int threads,
			Commands cmd, int numberOfBucket) throws IOException,
			InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(threads);
		ExecutorCompletionService ecs = new ExecutorCompletionService(executor);

		GraphSummaryApprox gse = new GraphSummaryApprox(distance, ptype, false,
				numberOfBucket);
		String[] data;
		long timestart = 0, timeend;
		ArrayList<Long> timetake = new ArrayList<Long>();
		int count = 0;
		int totalcount = 0;
		BufferedReader br = null;
		String line;
		ArrayList<Long> memory = new ArrayList<Long>();
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

				if (count == 1000) {
					for (int i = 0; i < 1000; i++) {
						ecs.take();
					}
					timeend = new Date().getTime();
					count = 0;
					timetake.add(timeend - timestart);
					if (cmd == Commands.CHECK_SPACE) {
						System.gc();
						memory.add((runtime.totalMemory() - runtime
								.freeMemory()) / Constants.MB);

					}

				}
				if (totalcount % 10000 == 0) {
					System.out.println("done for " + totalcount + " "
							+ new Date());
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

		BufferedWriter bw;
		File fout;
		FileWriter fw;

		System.out.println("Parsed all data " + totalcount);
		if (cmd == Commands.CHECK_PRECISION) {
			fout = new File(outFile);

			fw = new FileWriter(fout);
			bw = new BufferedWriter(fw);
			for (Entry<Integer, NodeApprox> entry : gse.getGraph().entrySet()) {
				line = entry.getKey() + "";
				for (long win : window) {
					if (win == 0l)
						line += ("," + entry.getValue().getNodeSummary()
								.estimate());
					else
						line += ("," + entry.getValue().getNodeSummary()
								.estimate(win));
				}
				line = line + "\n";
				count++;
				fw.write(line);
			}
			bw.flush();
			bw.close();
		}
		if (cmd == Commands.CHECK_TIME) {
			fout = new File(outFile);
			fw = new FileWriter(fout);
			bw = new BufferedWriter(fw);
			for (Long t : timetake) {
				fw.write(t + "\n");
			}
			bw.flush();
			bw.close();
			System.gc();
			outFile = outFile.replace(".csv", "_mem.txt");
			fout = new File(outFile);
			fw = new FileWriter(fout);
			fw.write(((runtime.totalMemory() - runtime.freeMemory()) / Constants.MB)
					+ "");
			fw.close();

		}
		if (cmd == Commands.CHECK_SPACE) {
			fout = new File(outFile);
			fw = new FileWriter(fout);
			bw = new BufferedWriter(fw);
			for (Long m : memory) {
				fw.write(m + "\n");
			}
			bw.flush();
			bw.close();
		}

	}

	private void mergeFile(String input1, String input2, String output)
			throws IOException {

		BufferedReader br1 = new BufferedReader(new FileReader(input1));
		BufferedReader br2 = new BufferedReader(new FileReader(input2));
		BufferedWriter bw = new BufferedWriter(new FileWriter(output));
		String line1, line2;
		while ((line1 = br1.readLine()) != null) {
			line2 = br2.readLine();
			if (line2 == null) {
				throw new IOException("Rows Not matching");
			} else {
				bw.write(line1 + "," + line2 + "\n");
			}
		}
		try {
			bw.flush();
			bw.close();
		} catch (Exception e) {

		}
		try {
			br1.close();
		} catch (Exception e) {

		}
		try {
			br2.close();

		} catch (Exception e) {

		}

	}
}
