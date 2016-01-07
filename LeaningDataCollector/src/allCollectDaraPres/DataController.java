package allCollectDaraPres;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

public class DataController {
	private JFrame mainFrame;
	private JFrame identificationFrame;
	private JFrame dataSaveFrame;

	private LoadTaskPanel loadTaskPanel;
	private LoadTaskNamePanel loadTNPanel;
	private LoadStudentNamePane loadSNPanel;

	private NameIdentification nameIdenti;
	private CollectData collectData;
	private SaveAsCSV saveAsCSV;

	private List<BeforeAfterPanel> BAPanel;
	private JPanel identificationPane;
	private JScrollPane BAScrollPane;

	private JButton taskLoadButton;
	private JLabel statusText;
	private JButton collectStartButton;

	private CDirectory taskRootPath;
	private File taskNamePath;
	private File studentNamePath;

	private Map<String, String> identifiNames;
	private Set<String> existTaskNames;
	private Map<String, String> mustCheckTaskNames;
	private List<Integer> studentNums;

	public DataController() {
		initializer();
	}

	void initializer() {
		// データを読み込む為のウインドウの作成
		mainFrame = new JFrame("DaraCollector");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setSize(400, 200);
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			SwingUtilities.updateComponentTreeUI(mainFrame);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// パネルをセット
		loadSNPanel = new LoadStudentNamePane(this);
		loadTaskPanel = new LoadTaskPanel(this);
		loadTNPanel = new LoadTaskNamePanel(this);

		taskLoadButton = new JButton("名寄せ開始");
		statusText = new JLabel("待機中");

		taskLoadButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// テキストボックス内の各pathを取得
				String rootTmp = loadTaskPanel.getRootPathArea().getText();
				String TNTmp = loadTNPanel.getTaskNamePathBox().getText();
				String SNTmp = loadSNPanel.getTaskNamePathBox().getText();
				if (!rootTmp.equals("") && !TNTmp.equals("") && !SNTmp.equals("")) {

					taskRootPath = new CDirectory(new CPath(rootTmp));
					taskNamePath = new File(TNTmp);
					studentNamePath = new File(SNTmp);

					// 学籍番号のデータの読み込み
					studentNums = loadSNPanel.getStudentData(studentNamePath.toString());
					// 提出課題名の読み込み
					mustCheckTaskNames = loadTNPanel.loadCSVString(taskNamePath.toString());
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

		mainFrame.add(taskLoadButton);
		mainFrame.add(statusText);
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
		Map<String, String> tmpIdentifiNames = nameIdenti.autoIdentification(existTaskNames, mustCheckTaskNames);

		// パネルをセット
		for (Map.Entry<String, String> entry : tmpIdentifiNames.entrySet()) {
			BeforeAfterPanel tmp = new BeforeAfterPanel(entry.getKey(), entry.getValue());
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
					if (!bap.getAfterName().equals(""))
						identifiNames.put(bap.getBeforeName(), bap.getAfterName());

				}
				// TODO名寄せ結果をCSVか何かに保存

				for (Map.Entry<String, String> entry : identifiNames.entrySet()) {
					if (!entry.getValue().equals("")) {
						System.out.println(entry.getKey() + " -> " + entry.getValue());
					}
				}
				// データ収集
				collectData = new CollectData(studentNums);
				collectData.dataCollect(taskRootPath.getDirectoryChildren(), identifiNames, mustCheckTaskNames);
				System.out.println("データ収集終了");
				makeDataSavaFrame();
			}

		});
		identificationPane.add(collectStartButton);
		identificationFrame.add(BAScrollPane);
		identificationFrame.setVisible(true);
	}

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
					saveAsCSV = new SaveAsCSV();
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
					saveAsCSV = new SaveAsCSV();
					saveAsCSV.outputCSVData(fc.getSelectedFile(), mustCheckTaskNames, studentNums,
							collectData.getCompileCount(), "RunCount");
				}

			}
		});

		dataSaveFrame.setLayout(new BoxLayout(dataSaveFrame.getContentPane(), BoxLayout.Y_AXIS));
		dataSaveFrame.add(compileCountButton);
		dataSaveFrame.add(runCountButton);
		dataSaveFrame.setVisible(true);
	}

}
