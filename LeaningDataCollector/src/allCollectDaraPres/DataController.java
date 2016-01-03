package allCollectDaraPres;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CPath;
import pres.loader.model.PLFile;
import pres.loader.model.PLProject;
import pres.loader.utils.PLMetricsCalculator;

public class DataController {
	private JFrame mainFrame;
	private JFrame identificationFrame;

	private LoadTaskPanel loadTaskPanel;
	private LoadTaskNamePanel loadTNPanel;
	private LoadStudentNamePane loadSNPanel;

	private List<BeforeAfterPanel> BAPanel;
	private JPanel identificationPane;
	private JScrollPane BAScrollPane;

	private JButton taskLoadButton;
	private JLabel statusText;
	private JButton collectStartButton;

	private CDirectory taskRootPath;
	private File taskNamePath;
	private File studentNamePath;

	private Map<String, String> tmpIdentifiNames;
	private Map<String, String> identifiNames;
	private Set<String> existTaskNames;
	private List<String> mustCheckTaskNames;
	private List<Integer> studentNums;

	public DataController() {
		initializer();
	}

	void initializer() {
		mainFrame = new JFrame("DaraCollector");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(400, 200);
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			SwingUtilities.updateComponentTreeUI(mainFrame);
		} catch (Exception e) {
			e.printStackTrace();
		}

		loadSNPanel = new LoadStudentNamePane(this);
		loadTaskPanel = new LoadTaskPanel(this);
		loadTNPanel = new LoadTaskNamePanel(this);

		taskLoadButton = new JButton("名寄せ開始");
		statusText = new JLabel("待機中");
		taskLoadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String rootTmp = loadTaskPanel.getRootPathArea().getText();
				String TNTmp = loadTNPanel.getTaskNamePathBox().getText();
				String SNTmp = loadSNPanel.getTaskNamePathBox().getText();
				if (!rootTmp.equals("") && !TNTmp.equals("") && !SNTmp.equals("")) {
					taskRootPath = new CDirectory(new CPath(rootTmp));
					taskNamePath = new File(TNTmp);
					studentNamePath = new File(SNTmp);
					statusText.setText("存在する課題名の探索開始");
					mainFrame.repaint();
					studentNums = loadSNPanel.getStudentData(studentNamePath.toString());
					mustCheckTaskNames = loadTNPanel.loadCSVString(taskNamePath.toString());
					nameIdentification();

				} else {
					statusText.setText("各データのパスをセットしてから押してください");
					mainFrame.repaint();
				}

			}
		});

		mainFrame.setLayout(new BoxLayout(mainFrame.getContentPane(), BoxLayout.Y_AXIS));
		mainFrame.add(loadSNPanel);
		mainFrame.add(loadTaskPanel);
		mainFrame.add(loadTNPanel);

		mainFrame.add(taskLoadButton);
		mainFrame.add(statusText);
		mainFrame.setVisible(true);

	}

	public JFrame getMainFrame() {
		return mainFrame;
	}

	private void nameIdentification() {
		List<CDirectory> lectures;
		lectures = taskRootPath.getDirectoryChildren();
		existTaskNames = new HashSet<>();
		for (CDirectory lec : lectures) {
			List<CDirectory> students;
			students = lec.getDirectoryChildren();
			for (CDirectory sd : students) {
				try {
					PLProject pr = new PLProject(sd.getNameByString(), sd, sd.getAbsolutePath());
					pr.load();
					for (PLFile plFile : pr.getRootPackage().getFilesRecursively()) {
						existTaskNames.add(plFile.getName());
						statusText.setText("探索" + plFile.getName());
						mainFrame.repaint();
						// PLMetricsCalculator calc = new
						// PLMetricsCalculator(plFile);
					}
				} catch (Exception e) {
					System.err.println(e);
				}
			}
		}
		statusText.setText("探索完了");
		mainFrame.repaint();

		for (String tn : existTaskNames) {
			System.out.println(tn);
		}

		makeIdentificationFrame();

	}

	private void makeIdentificationFrame() {
		identificationFrame = new JFrame("名寄せウインドウ");
		identificationFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		identificationFrame.setSize(400, 200);
		identificationPane = new JPanel();
		identificationPane.setLayout(new BoxLayout(identificationPane, BoxLayout.Y_AXIS));
		BAScrollPane = new JScrollPane(identificationPane);

		autoIdentification();
		BAPanel = new ArrayList<BeforeAfterPanel>();
		// TODO保存した名寄せ結果を読み込めるようにする

		for (Map.Entry<String, String> entry : tmpIdentifiNames.entrySet()) {
			BeforeAfterPanel tmp = new BeforeAfterPanel(entry.getKey(), entry.getValue());
			BAPanel.add(tmp);
			identificationPane.add(tmp);
		}

		collectStartButton = new JButton("集計開始");
		collectStartButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				identifiNames = new HashMap<>();
				for (BeforeAfterPanel bap : BAPanel) {
					identifiNames.put(bap.getBeforeName(), bap.getAfterName());

				}
				// TODO名寄せ結果をCSVか何かに保存
				dataCollect();
			}

		});
		identificationPane.add(collectStartButton);
		identificationFrame.add(BAScrollPane);
		identificationFrame.setVisible(true);
	}

	private void autoIdentification() {
		// TODO表記ゆれを吸収する
		// 現状は一字一句正しいファイル名のみ取得
		tmpIdentifiNames = new HashMap<>();
		for (String etn : existTaskNames) {
			if (mustCheckTaskNames.contains(etn)) {
				tmpIdentifiNames.put(etn, etn);
			} else {
				tmpIdentifiNames.put(etn, "");
			}
		}
	}

	private void dataCollect() {
		// TODO Auto-generated method stub

	}

	static final String PROJECT_PATH = "H:\\H\\workspaces\\git\\LeDaCo\\LeaningDataCollector\\targetFiles";

	public void test() {
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
}
