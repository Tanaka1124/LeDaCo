package allCollectDaraPres;

import java.util.List;
import java.util.Map;

import clib.common.filesystem.CDirectory;
import pres.loader.model.PLFile;
import pres.loader.model.PLProject;
import pres.loader.utils.PLMetricsCalculator;

public class CollectData {
	private DataSetsInteger compileCount;
	private DataSetsInteger runCount;
	private DataSetsLong textWorkTime;
	private DataSetsLong blockWorkTime;

	public CollectData(List<Integer> studentNums) {
		compileCount = new DataSetsInteger(studentNums);
		runCount = new DataSetsInteger(studentNums);
		textWorkTime = new DataSetsLong(studentNums);
		blockWorkTime = new DataSetsLong(studentNums);
	}

	public void dataCollect(List<CDirectory> lectures, Map<String, String> identifiNames,
			Map<String, String> mustCheckTaskNames) {
		// makeDataSet();
		// lectures = taskRootPath.getDirectoryChildren();

		for (CDirectory lec : lectures) {
			System.out.println(lec.toString());
			List<CDirectory> students;
			students = lec.getDirectoryChildren();
			for (CDirectory sd : students) {

				try {
					PLProject pr = new PLProject(sd.getNameByString(), sd, sd.getAbsolutePath());
					pr.load();
					for (PLFile plFile : pr.getRootPackage().getFilesRecursively()) {
						// 他のlectureに同名のJavaファイルがあっても無視
						if (identifiNames.containsKey(plFile.getName())
								&& mustCheckTaskNames.get(identifiNames.get(plFile.getName())).equals(lec.toString())) {
							PLMetricsCalculator calc = new PLMetricsCalculator(plFile);
							compileCount.get(Integer.parseInt(sd.toString().split("-", 0)[0]))
									.put(identifiNames.get(plFile.getName()), calc.getCompileCount());
							runCount.get(Integer.parseInt(sd.toString().split("-", 0)[0]))
									.put(identifiNames.get(plFile.getName()), calc.getCompileCount());
							textWorkTime.get(Integer.parseInt(sd.toString().split("-", 0)[0]))
									.put(identifiNames.get(plFile.getName()), calc.getWorkingTime().getTime() / 1000);
							blockWorkTime.get(Integer.parseInt(sd.toString().split("-", 0)[0]))
									.put(identifiNames.get(plFile.getName()), calc.getBEWorkingTime().getTime() / 1000);
						}

					}
				} catch (Exception e) {

					System.err.println(e);
				}
			}
		}

	}

	public DataSetsInteger getCompileCount() {
		return compileCount;
	}

	public DataSetsInteger getRunCount() {
		return runCount;
	}

	public DataSetsLong getTextWorkTime() {
		return textWorkTime;
	}

	public DataSetsLong getBlockWorkTime() {
		return blockWorkTime;
	}

}
