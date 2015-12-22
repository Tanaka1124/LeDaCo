package allCollectDaraPres;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CPath;
import pres.loader.model.PLFile;
import pres.loader.model.PLProject;
import pres.loader.utils.PLMetricsCalculator;

public class allDataCollectMain {
	static final String PROJECT_PATH = "H:\\H\\workspaces\\git\\LeDaCo\\LeaningDataCollector\\targetFiles";

	public static void main(String[] args) {
		CDirectory targetFiles = new CDirectory(new CPath(PROJECT_PATH));
		List<CDirectory> lectures;
		Set<String> taskName = new HashSet<>();
		Map<String, String> wrongNameFix = new HashMap<>();
		lectures = targetFiles.getDirectoryChildren();
		for (CDirectory lec : lectures) {
			List<CDirectory> students;
			students = lec.getDirectoryChildren();
			System.out.println(lec.getNameByString());
			for (CDirectory sd : students) {
				System.out.println("  " + sd.getNameByString());
				try {
					PLProject pr = new PLProject(sd.getNameByString(), sd, sd.getAbsolutePath());
					pr.load();
					for (PLFile plFile : pr.getRootPackage().getFilesRecursively()) {
						taskName.add(plFile.getName());
						PLMetricsCalculator calc = new PLMetricsCalculator(plFile);

						System.out.println("   " + pr.getRootPackage().getName() + " " + plFile.getName() + " "
								+ calc.getWorkingTime().getMinute());
					}

				} catch (Exception e) {
					System.err.println(e);
				}

			}
		}
		System.out.println("TaskNames");
		for (String tn : taskName) {
			System.out.println(tn);
		}
	}

	// CDirectory project = new CDirectory(new CPath(PROJECT_PATH));
	// PLProject plp = new PLProject(project.getNameByString(), project, new
	// CPath(PROJECT_PATH));
	// plp.load();
	// for (PLFile f : plp.getFiles()) {
	// // System.out.println(f.getName());
	// }
	// for (PLLog pl : plp.getRootPackage().getLogs()) {
	// // System.out.println(pl.getTimestamp());
	// }
	// System.out.println();
	// PLMetricsCalculator calc = new PLMetricsCalculator(plp.getRootPackage());
	// System.out.println(calc.getBEWorkingTime());

}
