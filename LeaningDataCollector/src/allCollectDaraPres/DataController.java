package allCollectDaraPres;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import clib.common.filesystem.CDirectory;
import clib.common.filesystem.CPath;
import clib.common.io.CStreamWriter;

public class DataController {
	static final String TEMP_FILE_NAME = "tmp.txt";
	private JFrame mainFrame;
	private JFrame identificationFrame;
	private JFrame dataSaveFrame;

	private LoadTaskPanel loadTaskPanel;
	private LoadTaskNamePanel loadTNPanel;
	private LoadTaskNamePanel loadExamplePanel;
	private LoadStudentNamePane loadSNPanel;

	private NameIdentification nameIdenti;
	private CollectData collectData;

	private List<BeforeAfterPanel> BAPanel;
	private JPanel identificationPane;
	private JScrollPane BAScrollPane;

	private JButton taskLoadButton;
	private JLabel statusText;
	private JButton collectStartButton;

	private CDirectory taskRootPath;
	private File taskNamePath;
	private File studentNamePath;
	private File exampleNamePath;
	private CDirectory tempDir;

	private Map<String, String> identifiNames;
	private Set<String> existTaskNames;
	private Map<String, String> mustCheckTaskNames;
	private Set<String> ignoreTaskNames;
	private List<String> studentNums;

	public DataController() {
		initializer();
	}

	void initializer() {
		// データを読み込む為のウインドウの作成
		mainFrame = new JFrame("DataCollector");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(400, 400);
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			SwingUtilities.updateComponentTreeUI(mainFrame);
		} catch (Exception e) {
			e.printStackTrace();
		}

		tempDir = new CDirectory(new CPath(new File(".")));
		List<String> tempPaths = new ArrayList<>();
		tempPaths.add(".");
		tempPaths.add(".");
		tempPaths.add(".");
		tempPaths.add(".");

		if (tempDir.exists()) {
			if (tempDir.findOrCreateFile(TEMP_FILE_NAME).loadTextAsList().size() >= 4) {
				tempPaths = tempDir.findOrCreateFile(TEMP_FILE_NAME).loadTextAsList();
			}
		}

		// パネルをセット
		loadSNPanel = new LoadStudentNamePane(this, tempPaths.get(0));
		loadTaskPanel = new LoadTaskPanel(this, tempPaths.get(1));
		loadTNPanel = new LoadTaskNamePanel(this, true, tempPaths.get(2));
		loadExamplePanel = new LoadTaskNamePanel(this, false, tempPaths.get(3));

		taskLoadButton = new JButton("名寄せ開始");
		statusText = new JLabel("待機中");

		taskLoadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// テキストボックス内の各pathを取得
				String rootTmp = loadTaskPanel.getRootPathArea().getText();
				String TNTmp = loadTNPanel.getTaskNamePathBox().getText();
				String TENTmp = loadExamplePanel.getTaskNamePathBox().getText();
				String SNTmp = loadSNPanel.getTaskNamePathBox().getText();
				if (!rootTmp.equals("") && !TNTmp.equals("") && !SNTmp.equals("")) {

					taskRootPath = new CDirectory(new CPath(rootTmp));
					taskNamePath = new File(TNTmp);
					exampleNamePath = new File(TENTmp);
					studentNamePath = new File(SNTmp);

					tempDir.findOrCreateFile(TEMP_FILE_NAME).delete();
					tempDir.findOrCreateFile(TEMP_FILE_NAME).appendText(studentNamePath.getAbsolutePath());
					tempDir.findOrCreateFile(TEMP_FILE_NAME).appendText(taskRootPath.getAbsolutePath().toString());
					tempDir.findOrCreateFile(TEMP_FILE_NAME).appendText(taskNamePath.getAbsolutePath());
					tempDir.findOrCreateFile(TEMP_FILE_NAME).appendText(exampleNamePath.getAbsolutePath());

					// 学籍番号のデータの読み込み
					studentNums = loadSNPanel.getStudentData(studentNamePath.toString());
					// 提出課題名の読み込み
					mustCheckTaskNames = loadTNPanel.loadCSVString(taskNamePath.toString());
					// 例題名の読み込み
					ignoreTaskNames = loadExamplePanel.loadCSVString4Ignore(exampleNamePath.toString());

					// 名寄せ開始
					nameIdenti = new NameIdentification();
					existTaskNames = nameIdenti.collectExistTaskName(taskRootPath.getDirectoryChildren());
					makeIdentificationFrame();
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
		mainFrame.add(loadExamplePanel);

		mainFrame.add(taskLoadButton);
		// mainFrame.add(statusText);
		mainFrame.setVisible(true);

	}

	public JFrame getMainFrame() {
		return mainFrame;
	}

	private void makeIdentificationFrame() {
		// 名寄せ用のウインドウ生成
		identificationFrame = new JFrame("名寄せウインドウ");
		identificationFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		identificationFrame.setSize(400, 200);
		identificationPane = new JPanel();
		identificationPane.setLayout(new BoxLayout(identificationPane, BoxLayout.Y_AXIS));
		BAScrollPane = new JScrollPane(identificationPane);

		BAPanel = new ArrayList<BeforeAfterPanel>();
		// TODO保存した名寄せ結果を読み込めるようにする

		// 正しい課題名であれば自動で埋める
		Map<String, String> tmpIdentifiNames = nameIdenti.autoIdentification(existTaskNames, mustCheckTaskNames,
				ignoreTaskNames);

		// パネルをセット
		Set<String> mustCheckSet = new TreeSet<>();
		for (String key : mustCheckTaskNames.keySet()) {
			mustCheckSet.add(key);
		}
		mustCheckSet.add(" ");
		String[] mustCheckArray = mustCheckSet.toArray(new String[mustCheckSet.size()]);
		for (Map.Entry<String, String> entry : tmpIdentifiNames.entrySet()) {
			BeforeAfterPanel tmp = new BeforeAfterPanel(entry.getKey(), mustCheckArray, entry.getValue());
			BAPanel.add(tmp);
			identificationPane.add(tmp);
		}

		collectStartButton = new JButton("データ収集開始");
		collectStartButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// 名寄せした名前をMapへ
				identifiNames = new HashMap<>();
				for (BeforeAfterPanel bap : BAPanel) {
					if (!bap.getAfterName().equals(" "))
						identifiNames.put(bap.getBeforeName(), bap.getAfterName());

				}
				// 名寄せ結果をtxtに保存
				CStreamWriter writer = tempDir.findOrCreateFile(System.currentTimeMillis() + "NayoseMap.txt")
						.openWriter();
				for (Map.Entry<String, String> entry : identifiNames.entrySet()) {
					writer.writeLineFeed(entry.getValue() + " -> " + entry.getKey());
				}

				// データ収集
				collectData = new CollectData(studentNums);
				collectData.dataCollect(taskRootPath.getDirectoryChildren(), identifiNames, mustCheckTaskNames);
				System.out.println("データ収集終了");
				// makeDataSavaFrame();
			}

		});
		identificationPane.add(collectStartButton);
		identificationFrame.add(BAScrollPane);
		identificationFrame.setVisible(true);
	}

	@SuppressWarnings("unused")
	private void makeDataSavaFrame() {
		dataSaveFrame = new JFrame("CSVに保存");
		dataSaveFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		dataSaveFrame.setSize(400, 200);
		JButton compileCountButton = new JButton("コンパイル回数");
		compileCountButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// CSVに出力
				System.out.println("出力先のフォルダを選択");
				JFileChooser fc = new JFileChooser(".");
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				int selected = fc.showOpenDialog(identificationFrame);
				if (selected == JFileChooser.APPROVE_OPTION) {
					SaveAsCSV saveAsCSV = new SaveAsCSV();
					saveAsCSV.outputCSVData(fc.getSelectedFile(), mustCheckTaskNames, studentNums,
							collectData.getCompileCount(), "CompileCount");
				}

			}
		});
		JButton runCountButton = new JButton("実行回数");
		runCountButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// CSVに出力
				System.out.println("出力先のフォルダを選択");
				JFileChooser fc = new JFileChooser(".");
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				int selected = fc.showOpenDialog(identificationFrame);
				if (selected == JFileChooser.APPROVE_OPTION) {
					SaveAsCSV saveAsCSV = new SaveAsCSV();
					saveAsCSV.outputCSVData(fc.getSelectedFile(), mustCheckTaskNames, studentNums,
							collectData.getRunCount(), "RunCount");
				}

			}
		});

		JButton blockTimeButton = new JButton("ブロック編集時間");
		blockTimeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// CSVに出力
				System.out.println("出力先のフォルダを選択");
				JFileChooser fc = new JFileChooser(".");
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				int selected = fc.showOpenDialog(identificationFrame);
				if (selected == JFileChooser.APPROVE_OPTION) {
					SaveAsCSV saveAsCSV = new SaveAsCSV();
					saveAsCSV.outputCSVData(fc.getSelectedFile(), mustCheckTaskNames, studentNums,
							collectData.getBlockWorkTime(), "BlockTime");
				}

			}
		});
		JButton textTimeButton = new JButton("テキスト編集時間");
		textTimeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// CSVに出力
				System.out.println("出力先のフォルダを選択");
				JFileChooser fc = new JFileChooser(".");
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				int selected = fc.showOpenDialog(identificationFrame);
				if (selected == JFileChooser.APPROVE_OPTION) {
					SaveAsCSV saveAsCSV = new SaveAsCSV();
					saveAsCSV.outputCSVData(fc.getSelectedFile(), mustCheckTaskNames, studentNums,
							collectData.getTextWorkTime(), "textTime");
				}

			}
		});

		dataSaveFrame.setLayout(new BoxLayout(dataSaveFrame.getContentPane(), BoxLayout.Y_AXIS));
		dataSaveFrame.add(compileCountButton);
		dataSaveFrame.add(runCountButton);
		dataSaveFrame.add(blockTimeButton);
		dataSaveFrame.add(textTimeButton);
		dataSaveFrame.setVisible(true);
	}

}
