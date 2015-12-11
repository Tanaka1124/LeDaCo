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

			boolean textFocusing = false;
			boolean blockFocusing = false;
			long textTime = 0;
			int blockTime = 0;
			long blockFocusGainTime = 0;
			long textFocusGainTime = 0;
			if (logFiles[i] != null) {
				BufferedReader br = new BufferedReader(
						new InputStreamReader(new FileInputStream(logFiles[i]), "UTF-8"));
				System.out.println(logFiles[i]);
				for (String line = br.readLine(); line != null; line = br.readLine()) {
					String[] temp = line.split("\t", 0);
					// System.out.println(line);
					if (temp[2].equals("COMMAND_RECORD") && temp[3].equals("FOCUS_GAINED")) {
						if (textFocusing && lastLine != null) {
							String[] lastLineTime = lastLine.split("\t", 0);
							textTime += Long.parseLong(lastLineTime[0]) - textFocusGainTime;

						}
						textFocusGainTime = Long.parseLong(temp[0]);
						textFocusing = true;
					} else if (textFocusing && ((temp[2].equals("COMMAND_RECORD") && temp[3].equals("FOCUS_LOST")))) {
						textTime += Long.parseLong(temp[0]) - textFocusGainTime;

						textFocusing = false;
					} else if (temp[2].equals("BLOCK_COMMAND_RECORD") && temp[3].equals("FOCUS_GAINED")) {
						if (blockFocusing && lastLine != null) {
							String[] lastLineTime = lastLine.split("\t", 0);
							blockTime += Long.parseLong(lastLineTime[0]) - blockFocusGainTime;
						}
						blockFocusGainTime = Long.parseLong(temp[0]);
						blockFocusing = true;
					} else if (blockFocusing
							&& (temp[2].equals("BLOCK_COMMAND_RECORD") && temp[3].equals("FOCUS_LOST"))) {
						blockTime += Long.parseLong(temp[0]) - blockFocusGainTime;
						if (i == 24) {

						}
						blockFocusing = false;
					} else if ((textFocusing || blockFocusing)
							&& (temp[3].equals("INFO") && temp[4].equals("LoggingThread Stop")) && (lastLine != null)) {
						if (textFocusing) {
							textTime += Long.parseLong(temp[0]) - textFocusGainTime;
						} else {
							blockTime += Long.parseLong(temp[0]) - blockFocusGainTime;
						}
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
