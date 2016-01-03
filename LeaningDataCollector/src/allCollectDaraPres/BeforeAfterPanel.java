package allCollectDaraPres;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class BeforeAfterPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JLabel beforeName;

	private JTextField afterName;

	public BeforeAfterPanel(String b, String a) {
		beforeName = new JLabel(b);
		afterName = new JTextField(10);
		afterName.setText(a);
		this.setLayout(new FlowLayout());
		this.add(beforeName);
		this.add(afterName);

	}

	public String getBeforeName() {
		return beforeName.getText();
	}

	public String getAfterName() {
		return afterName.getText();
	}
}
