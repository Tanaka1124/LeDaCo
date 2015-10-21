package collector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ProgrammingTimeCollector {
	File[] logFiles;
	int[] textFocusTime;
	int[] blockFocusTime;

	public ProgrammingTimeCollector(File[] logFiles) throws IOException {
		this.logFiles = logFiles;
		calFocusTime();
	}

	private void calFocusTime() throws IOException {
		boolean focusing = false;
		int blockTime = 0;
		int blockBuf = 0;
		int taxtTime = 0;
		int textBuf = 0;
		for (File f : logFiles) {
			if (f != null) {
				BufferedReader br = new BufferedReader(new FileReader(f));
				String[] b = br.readLine().split("\t", 0);
				if (b[2].equals("COMMAND_RECORD")) {
					if (b[3].equals("FOCUS_GAINED")) {
						if (!focusing) {
							textBuf = new Integer(b[0]).intValue();
							focusing = true;
						} else {

						}
					}
					if (b[3].equals("FOCUS_LOST")) {
						textBuf = new Integer(b[0]).intValue();
						focusing = false;
					}

				}
			}
		}

	}

	public int[] getTextFocusTime() {
		return textFocusTime;
	}

}
