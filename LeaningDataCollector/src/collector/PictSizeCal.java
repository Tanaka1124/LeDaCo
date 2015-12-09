package collector;

import java.io.File;

public class PictSizeCal {
	private long[] printSizes;
	private long totalSise;

	public long[] getSize(File rootFile, int length) {
		printSizes = new long[length];

		printLog("BlockPrint計量開始");
		for (File f : rootFile.listFiles()) {
			File buf = new File(f, "\\.pres2\\BlockPrint");

			if (buf.exists()) {
				int i = 0;
				for (File p : f.listFiles()) {
					printSizes[i] += p.length();

				}
				printSizes[i] = printSizes[i] / 1000;

			} else {

			}
		}
		printLog("BlockPrint計量終了");

		for (long ps : printSizes) {
			totalSise += ps;
		}

		return printSizes;

	}

	public long[] getPrintSizes() {
		return printSizes;
	}

	public long getTotalSise() {
		return totalSise;
	}

	private void printLog(String s) {

		System.out.println("---------------" + s + "---------------");

	}

}
