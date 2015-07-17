package com.ulb.code.wit.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Random;

public class GenerateData {

	int nodes, edges;

	public GenerateData(int nodes, int edges) {
		this.nodes = nodes;
		this.edges = edges;

	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GenerateData gd = new GenerateData(1000, 200000);
		gd.generate("");

	}

	public void generate(String path) {
		Random randomGenerator;
		int user1, user2;
		String line = "";
		File fout = null;
		BufferedWriter writer = null;
		// String home="//Users//rk//Documents//data";
		String home = "C://data";
		HashMap<Integer, Integer> gnodes = new HashMap<Integer, Integer>();
		ArrayList<Integer> gnodesList = new ArrayList<Integer>();
		;
		try {
			fout = new File(home + "/data_" + nodes + "_" + edges + ".csv");
			if (!fout.exists()) {
				fout.createNewFile();
			}
			writer = new BufferedWriter(new FileWriter(fout));
			Random cointoss = new Random();
			for (int i = 0; i < edges; i++) {
				randomGenerator = new Random();
				cointoss = new Random();
				if (gnodes.size() < nodes - 1) {
					user1 = Math
							.abs((int) (randomGenerator.nextGaussian() * nodes)) + 10;
					gnodes.put(user1, user1);
					user2 = Math
							.abs((int) (randomGenerator.nextGaussian() * nodes)) + 10;
					gnodes.put(user1, user2);
				} else {
					if (gnodesList.size() == 0) {
						for (int n : gnodes.keySet()) {
							gnodesList.add(n);
						}
					}
					user1 = gnodesList.get(cointoss.nextInt(nodes - 1));
					user2 = gnodesList.get(cointoss.nextInt(nodes - 1));
				}

				line = user1 + "," + user2 + "," + new Date().getTime() + "\n";
				writer.write(line);
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
		System.out.println("done");
	}
}
