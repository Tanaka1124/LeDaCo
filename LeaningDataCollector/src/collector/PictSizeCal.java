package collector;

import java.io.File;

public class PictSizeCal extends PrintTemplate {
	private long[] printSizes;
	private long totalSise;

	public long[] getSize(File rootFile, int length) {
		printSizes = new long[length];

		printLog("BlockPrint計量開始");
		int studentNum = 0;
		for (File f : rootFile.listFiles()) {
			File buf = new File(f, "\\.pres2\\BlockPrint");

			if (buf.exists()) {
				for (File p : buf.listFiles()) {
					printSizes[studentNum] += p.length();
				}
				printSizes[studentNum] = printSizes[studentNum] / 1000;
			} else {
				printSizes[studentNum] = 0;
			}
			// System.out.println(printSizes[studentNum] + "KB");
			studentNum++;
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

}
