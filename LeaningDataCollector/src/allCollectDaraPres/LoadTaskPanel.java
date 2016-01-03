package allCollectDaraPres;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class LoadTaskPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private JButton selButton;
	private JTextField rootPathArea;
	private JPanel chooserPanel;

	public LoadTaskPanel(DataController parent) {
		chooserPanel = new JPanel();
		rootPathArea = new JTextField(30);
		selButton = new JButton("選択");
		selButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// desktopを指定
				// JFileChooser fc = new JFileChooser(new
				// File(System.getProperty("user.home"), "Desktop").toString());
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				int selected = fc.showOpenDialog(parent.getMainFrame());
				if (selected == JFileChooser.APPROVE_OPTION) {
					rootPathArea.setText(fc.getSelectedFile().toString());
				}
			}
		});

		chooserPanel.setLayout(new FlowLayout());
		chooserPanel.add(rootPathArea);
		chooserPanel.add(selButton);

		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		this.add(new JLabel("読み込むファイルのrootを選択してください"));
		this.add(chooserPanel);

	}

	public JTextField getRootPathArea() {
		return rootPathArea;
	}

}
