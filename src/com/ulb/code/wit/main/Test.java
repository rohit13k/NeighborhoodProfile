package com.ulb.code.wit.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Map.Entry;

import com.ulb.code.wit.graphs.approx.NodeApprox;
import com.ulb.code.wit.graphs.exact.GraphSummaryExact;
import com.ulb.code.wit.graphs.exact.NodeExact;
import com.ulb.code.wit.util.Constants;
import com.ulb.code.wit.util.Constants.Commands;
import com.ulb.code.wit.util.Constants.PropagationType;

public class Test {

	public static void main(String[] args) throws IOException {
		String inputFilePath = args[1];
		String resultFolderPath = "";
		String inFileName = new File(inputFilePath).getName().replace(".csv",
				"");
		long[] window = { 0l};

		String exactOutPut = resultFolderPath + File.separator + inFileName
				+ "_exact.csv";
		testExact(PropagationType.DISTANCEWISE, inputFilePath, exactOutPut,
				Constants.DISTANCE, window);

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
}
