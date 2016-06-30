package MakeSwitchTimeline;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class DrawTL {

	public DrawTL(List<List<String>> TLList) {
		JFrame frame = new JFrame();
		frame.setTitle("switchTL");
		frame.setBounds(0, 0, 1000, 1000);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.setLayout(null);

		JPanel cp = new JPanel();
		cp.setLayout(null);
		frame.add(cp);
		cp.setBounds(38, 20, 3000, 1000);

		TestCanvas canvas = new TestCanvas(TLList);
		cp.add(canvas);
		canvas.setBounds(0, 0, 3000, 1000);
		frame.setVisible(true);
	}
	/*
	 * public static void main(String[] args) { List<List<String>> test = new
	 * ArrayList<>(); List<String> tes = new ArrayList<>(); tes.add("studentA");
	 * tes.add("startLang"); tes.add("123000"); tes.add("234000");
	 * tes.add("345000"); tes.add("456000"); test.add(tes); test.add(tes);
	 * test.add(tes); test.add(tes); new DrawTL(test); }
	 */
}

class TestCanvas extends Canvas {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	List<List<String>> TLData;

	public TestCanvas(List<List<String>> TLData) {
		this.TLData = TLData;
	}

	public void paint(Graphics g) {
		Font font2 = new Font("Calibri Light", Font.PLAIN, 12);
		g.setFont(font2);
		setBackground(Color.white);
		// TL
		for (int i = 0; i < TLData.size(); i++) {
			// System.out.println((i + 1) + "本目" + TLData.get(i).get(0));
			g.setColor(new Color(0, 0, 0));
			g.drawString(TLData.get(i).get(0) + "", 0, i * 60 + 45);
			for (int j = 2; j < (TLData.get(i).size() - 1); j++) {
				int x = (((int) Long.parseLong(TLData.get(i).get(j))) / (1000 * 5)) + 100;
				int y = i * 60 + 50;
				int width = ((int) (Long.parseLong(TLData.get(i).get(j + 1)) - Long.parseLong(TLData.get(i).get(j))))
						/ (1000 * 5) + 1;
				int height = 30;
				if (j % 2 == 0) {
					g.setColor(new Color(118, 255, 255));
				} else {
					g.setColor(new Color(198, 172, 87));
				}
				g.fillRect(x, y, width, height);
			}

		}
		// めもり
		for (int i = 0; i < 10; i++) {
			g.setColor(Color.black);
			g.drawString(i * 10 + "", 95 + i * 120, TLData.size() * 60 + 100);
			g.drawLine(100 + i * 120, TLData.size() * 60 + 110, 100 + i * 120, TLData.size() * 60 + 120);
		}
		g.drawLine(100, TLData.size() * 60 + 120, 100 + 9 * 120, TLData.size() * 60 + 120);
	}

}
