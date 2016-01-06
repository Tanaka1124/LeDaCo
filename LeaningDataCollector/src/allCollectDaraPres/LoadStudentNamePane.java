package allCollectDaraPres;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
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

public class LoadStudentNamePane extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton selButton;
	private JTextField studentNamePathBox;
	private JPanel chooserPanel;

	public LoadStudentNamePane(DataController parent) {

		chooserPanel = new JPanel();
		studentNamePathBox = new JTextField(30);
		selButton = new JButton("選択");
		selButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// // desktopを指定
				// JFileChooser fc = new JFileChooser(new
				// File(System.getProperty("user.home"), "Desktop").toString());
				JFileChooser fc = new JFileChooser(".");
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

				int selected = fc.showOpenDialog(parent.getMainFrame());
				if (selected == JFileChooser.APPROVE_OPTION) {
					studentNamePathBox.setText(fc.getSelectedFile().toString());
				}
			}
		});

		chooserPanel.setLayout(new FlowLayout());
		chooserPanel.add(studentNamePathBox);
		chooserPanel.add(selButton);

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(new JLabel("学籍番号一覧を選択してください"));
		this.add(chooserPanel);

	}

	public JTextField getTaskNamePathBox() {
		return studentNamePathBox;
	}

	public List<Integer> getStudentData(String studentListPath) {
		List<Integer> studentNum = new ArrayList<>();
		File studentList = new File(studentListPath);
		List<String> temp = new ArrayList<>();
		try (FileReader fr = new FileReader(studentList.getAbsolutePath().toString());
				BufferedReader br = new BufferedReader(fr)) {

			String line;
			StringTokenizer token;
			while ((line = br.readLine()) != null) {
				token = new StringTokenizer(line, ",");
				for (int i = 0; token.hasMoreTokens(); i++) {
					if (i == 0) {
						temp.add(token.nextToken());
						// } else if (i == 1) {
						// studentName.add(token.nextToken());
					} else {
						token.nextToken();
					}
				}
			}
			br.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		temp.remove(0);
		for (String t : temp) {
			studentNum.add(Integer.parseInt(t));
		}
		return studentNum;
		// studentName.remove(0);

	}
}
