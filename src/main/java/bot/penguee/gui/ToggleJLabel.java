package bot.penguee.gui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class ToggleJLabel extends JLabel {

	private boolean state = false;
	ImageIcon selectedIcon;
	ImageIcon unselectedIcon;

	public ToggleJLabel() {
		// TODO Auto-generated constructor stub
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				state = !state;
				updateIcon();
			}
		});
	}

	public boolean isSelected() {
		return state;
	}

	public void setSelected(boolean s) {
		state = s;
		updateIcon();
	}

	public void setSelectedIcon(ImageIcon ii) {
		selectedIcon = ii;
	}

	public void setIcon(ImageIcon ii) {
		super.setIcon(ii);
		unselectedIcon = ii;
	}

	public void updateIcon() {
		if (state)
			super.setIcon(selectedIcon);
		else
			super.setIcon(unselectedIcon);
	}

}
