package allCollectDaraPres;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LoadTaskNamePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton selButton;
	private JTextField taskNamePathBox;

	public JTextField getTaskNamePathBox() {
		return taskNamePathBox;
	}

	private JPanel chooserPanel;

	public LoadTaskNamePanel(DataController parent) {

		chooserPanel = new JPanel();
		taskNamePathBox = new JTextField(30);
		selButton = new JButton("選択");
		selButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// // desktopを指定
				// JFileChooser fc = new JFileChooser(new
				// File(System.getProperty("user.home"), "Desktop").toString());
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

				int selected = fc.showOpenDialog(parent.getMainFrame());
				if (selected == JFileChooser.APPROVE_OPTION) {
					taskNamePathBox.setText(fc.getSelectedFile().toString());
				}
			}
		});

		chooserPanel.setLayout(new FlowLayout());
		chooserPanel.add(taskNamePathBox);
		chooserPanel.add(selButton);

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(new JLabel("課題一覧のCSVを選択してください"));
		this.add(chooserPanel);

	}

	public List<String> loadCSVString(String path) {
		List<String> loadedList = new ArrayList<String>();
		try (FileReader fr = new FileReader(path); BufferedReader br = new BufferedReader(fr)) {

			String line;
			StringTokenizer token;
			while ((line = br.readLine()) != null) {
				token = new StringTokenizer(line, ",");
				loadedList.add(token.nextToken());
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return loadedList;
	}

}