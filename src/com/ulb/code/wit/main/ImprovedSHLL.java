package com.ulb.code.wit.main;

import java.util.ArrayList;
import java.util.Date;

import com.google.common.base.Preconditions;
import com.ulb.code.wit.util.BucketAndHash;
import com.ulb.code.wit.util.ElementList;
import com.ulb.code.wit.util.HyperLogLogUtil;
import com.ulb.code.wit.util.Numbers;

import static com.ulb.code.wit.util.HyperLogLogUtil.computeHash;

public class ImprovedSHLL {

	private ArrayList<ElementList> buckets;
	int nonZeroBucket;

	// The current sum of 1 / (1L << buckets[i]). Updated as new items are added
	// and used for
	// estimation
	private double currentSum = 0;
	private boolean isempty = true;

	public ArrayList<ElementList> getBuckets() {
		return buckets;
	}

	public void setBuckets(ArrayList<ElementList> buckets) {
		this.buckets = buckets;
	}

	public ImprovedSHLL(int numberOfBuckets) {
		Preconditions.checkArgument(Numbers.isPowerOf2(numberOfBuckets),
				"numberOfBuckets must be a power of 2");
		Preconditions.checkArgument(numberOfBuckets > 0,
				"numberOfBuckets must be > 0");

		buckets = new ArrayList<ElementList>(numberOfBuckets);
		initializeBucket(numberOfBuckets);

	}

	public boolean isEmpty() {
		return isempty;
	}

	public String toString() {
		return this.buckets.toString();
	}

	public int add(long value, long timestamp) {
		if (isempty) {
			isempty = false;
		}
		BucketAndHash bucketAndHash = BucketAndHash.fromHash(
				computeHash(value), buckets.size());
		int bucket = bucketAndHash.getBucket();

		int lowestBitPosition = Long.numberOfTrailingZeros(bucketAndHash
				.getHash()) + 1;

		ElementList preElementList;
		if (null == buckets.get(bucket)) {

			preElementList = new ElementList();
		} else {
			preElementList = buckets.get(bucket);

		}
		preElementList.addNewElement(lowestBitPosition, timestamp);
		buckets.set(bucket, preElementList);
		return bucket;
	}

	public boolean merge(int bucketno, int lowestbitposition, long timestamp) {
		if (isempty) {
			isempty = false;
		}
		ElementList preElementList;
		if (null == buckets.get(bucketno)) {

			preElementList = new ElementList();
		} else {
			preElementList = buckets.get(bucketno);

		}
		boolean ischanged = preElementList.addNewElement(lowestbitposition,
				timestamp);
		buckets.set(bucketno, preElementList);
		return ischanged;
	}

	public void add(long value) {
		add(value, new Date().getTime());
	}

	public long estimate() {
		return calculate(-1);
	}

	public long estimate(long window) {
		return calculate(window);
	}

	private long calculate(long window) {
		double alpha = HyperLogLogUtil.computeAlpha(buckets.size());
		if (window == -1)
			currentSum = getCurrentSum();
		else
			currentSum = getCurrentSum(window);
		double result = alpha * buckets.size() * buckets.size() / currentSum;

		if (result <= 2.5 * buckets.size()) {
			// adjust for small cardinalities
			int zeroBuckets = buckets.size() - nonZeroBucket;
			if (zeroBuckets > 0) {
				result = buckets.size()
						* Math.log(buckets.size() * 1.0 / zeroBuckets);
			}
		}

		return Math.round(result);
	}

	private double getCurrentSum() {
		// TODO Auto-generated method stub
		double sum = 0;
		for (int i = 0; i < buckets.size(); i++) {
			if (buckets.get(i) != null) {
				sum += 1.0 / (1L << buckets.get(i).getTopElementValue());
				nonZeroBucket++;
			} else {
				sum += 1.0;
			}

		}
		return sum;
	}

	private double getCurrentSum(long window) {
		// TODO Auto-generated method stub
		double sum = 0;
		int value;
		for (int i = 0; i < buckets.size(); i++) {
			if (buckets.get(i) != null) {
				value = buckets.get(i).getElementValue(window);
				if (value != -1) {
					sum += 1.0 / (1L << value);
					nonZeroBucket++;
				} else
					sum += 1.0;
			} else {
				sum += 1.0;
			}

		}
		return sum;
	}

	private void initializeBucket(int numberOfBuckets) {
		for (int i = 0; i < numberOfBuckets; i++) {
			buckets.add(null);
		}

	}

	// private int getNonZeroBucket() {
	// int nonZeroBucket = 0;
	// for (int i = 0; i < buckets.size(); i++) {
	// if (null != buckets.get(i)) {
	// nonZeroBucket++;
	//
	// }
	// }
	// return nonZeroBucket;
	// }

}
