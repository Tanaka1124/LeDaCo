package allCollectDaraPres;

import java.awt.FlowLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BeforeAfterPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JLabel beforeName;

	private JComboBox<String> afterName;

	public BeforeAfterPanel(String b, String[] aftterArray, String a) {
		beforeName = new JLabel(b);
		afterName = new JComboBox<>(aftterArray);
		afterName.setSelectedItem(a);
		// System.out.println("before " + b + " after " + a + " " +
		// b.equals(a));
		this.setLayout(new FlowLayout());
		this.add(beforeName);
		this.add(afterName);

	}

	public String getBeforeName() {
		return beforeName.getText();
	}

	public String getAfterName() {
		return (String) afterName.getSelectedItem();
	}
}
