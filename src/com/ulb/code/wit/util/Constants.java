package com.ulb.code.wit.util;

public class Constants {

	public static final int numberOfBuckets = 128;
	public static final int numberofThreads = 4;
	public static final int MB = 1024 * 1024;

	public enum PropagationType {
		DISTANCEWISE, NODEWISE
	};

	public static final int DISTANCE = 4;

	public enum Commands {
		CHECK_PARALLEL_VS_SERIAL("checkParalleAndSerial"), CHECK_PRECISION(
				"checkPrecision"), CHECK_TIME("checkTime"), CHECK_SPACE(
				"checkSpace"), CHECK_PROPOGATION("checkPropogation"),CHECK_QUERYTIME("checkQueryTIme");
		private String value;

		private Commands(String value) {
			this.value = value;
		}

		public String value() {
			return value;
		}
	};
}
