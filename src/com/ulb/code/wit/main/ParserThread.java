package com.ulb.code.wit.main;

import java.util.concurrent.Callable;

import com.ulb.code.wit.graphs.approx.GraphSummaryApprox;

public class ParserThread implements Callable<Integer> {

	private GraphSummaryApprox gsa;
	private int user1,user2;
	private long timestamp;
	public ParserThread(GraphSummaryApprox gsa,int user1,int user2,long time){
		this.gsa=gsa;
		this.user1=user1;
		this.user2=user2;
		this.timestamp=time;
		
	}
	public Integer call() {
		return gsa.addEdge(user1, user2, timestamp);

	}

}
