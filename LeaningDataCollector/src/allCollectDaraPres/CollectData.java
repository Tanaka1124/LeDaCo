package allCollectDaraPres;

import java.util.List;
import java.util.Map;

import clib.common.filesystem.CDirectory;
import pres.loader.model.PLFile;
import pres.loader.model.PLProject;
import pres.loader.utils.PLMetricsCalculator;

public class CollectData {
	private DataSets compileCount;
	private DataSets runCount;

	public CollectData(List<Integer> studentNums) {
		compileCount = new DataSets(studentNums);
		runCount = new DataSets(studentNums);
	}

	public void dataCollect(List<CDirectory> lectures, Map<String, String> identifiNames) {
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
						if (identifiNames.containsKey(plFile.getName())) {
							PLMetricsCalculator calc = new PLMetricsCalculator(plFile);
							compileCount.get(Integer.parseInt(sd.toString().split("-", 0)[0]))
									.put(identifiNames.get(plFile.getName()), calc.getCompileCount());
							runCount.get(Integer.parseInt(sd.toString().split("-", 0)[0]))
									.put(identifiNames.get(plFile.getName()), calc.getCompileCount());
						}

					}
				} catch (Exception e) {
					System.err.println(e);
				}
			}
		}

	}

	public DataSets getCompileCount() {
		return compileCount;
	}

	public DataSets getRunCount() {
		return runCount;
	}

}
