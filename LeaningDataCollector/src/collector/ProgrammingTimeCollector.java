package collector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ProgrammingTimeCollector {
	private File[] logFiles;
	private long[] textFocusTime;
	private long[] blockFocusTime;

	public ProgrammingTimeCollector(File[] logFiles) throws IOException {
		this.logFiles = logFiles;
		textFocusTime = new long[logFiles.length];
		blockFocusTime = new long[logFiles.length];
		calFocusTime();
	}

	private void calFocusTime() throws IOException {

		String lastLine = null;
		for (int i = 0; i < logFiles.length; i++) {
			// if (logFiles[i] != null) // デバッグ用ファイル名表示
			// System.out.println(logFiles[i].getPath());

			int blockTime = 0;
			boolean focusing = false;
			long blockFocusGainTime = 0;
			long textFocusGainTime = 0;
			if (logFiles[i] != null) {
				BufferedReader br = new BufferedReader(
						new InputStreamReader(new FileInputStream(logFiles[i]), "SJIS"));
				long textTime = 0;
				for (String line = br.readLine(); line != null; line = br.readLine()) {
					String[] temp = line.split("\t", 0);
					// System.out.println(buf[0]);

					if (temp[2].equals("COMMAND_RECORD") && temp[3].equals("FOCUS_GAINED")) {
						if (focusing && lastLine != null) {
							String[] lastLineTime = lastLine.split("\t", 0);
							textTime += Long.parseLong(lastLineTime[0]) - textFocusGainTime;
							// System.out.println(j + "行目");
							// System.out.println((Long.parseLong(lastLineTime[0])
							// - textFocusGainTime) / 1000);

						}
						textFocusGainTime = Long.parseLong(temp[0]);
						focusing = true;
					} else if ((temp[2].equals("COMMAND_RECORD") && temp[3].equals("FOCUS_LOST"))
							|| temp[3].equals("INFO") && temp[4].equals("LoggingThread Stop")) {
						textTime += Long.parseLong(temp[0]) - textFocusGainTime;
						// System.out.println(Long.parseLong(buf[0]) -
						// textBuf);
						focusing = false;
					} else if (temp[2].equals("BLOCK_COMMAND_RECORD") && temp[3].equals("FOCUS_GAINED")) {
						if (focusing && lastLine != null) {
							String[] lastLineTime = lastLine.split("\t", 0);
							blockTime += Long.parseLong(lastLineTime[0]) - blockFocusGainTime;
						}
						blockFocusGainTime = Long.parseLong(temp[0]);
						focusing = true;
					} else if (temp[2].equals("BLOCK_COMMAND_RECORD") && temp[3].equals("FOCUS_LOST")) {
						blockTime += Long.parseLong(temp[0]) - blockFocusGainTime;
						focusing = false;
					}

					lastLine = line;

				}
				br.close();
				textFocusTime[i] = textTime;
				blockFocusTime[i] = blockTime;
			}
		}
	}

	public long[] getTextFocusTime() {
		return textFocusTime;
	}

	public long[] getBlockFocusTime() {
		return blockFocusTime;
	}

}
