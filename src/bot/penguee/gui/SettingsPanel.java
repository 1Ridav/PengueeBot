package bot.penguee.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import bot.penguee.Data;
import bot.penguee.MyProperties;

public class SettingsPanel extends JPanel {
	Frame frame;

	/**
	 * Create the panel.
	 */
	public SettingsPanel(Frame frame) {
		this.frame = frame;
		init();
	}

	private void init() {
		setBackground(new Color(0, 51, 0));
		setBounds(66, 0, 834, 585);
		setLayout(null);

		JPanel menuUpper = new JPanel();
		menuUpper.setBounds(0, 0, 834, 55);
		menuUpper.setBackground(new Color(0, 102, 102));
		menuUpper.setLayout(null);

		JPanel menuCentral = new JPanel();
		menuCentral.setBackground(Color.DARK_GRAY);
		menuCentral.setBounds(0, 55, 834, 530);

		add(menuUpper);
		add(menuCentral);

		menuCentral.setLayout(null);

		final JLabel ramSliderLabel = new JLabel("2048");
		ramSliderLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		ramSliderLabel.setForeground(Color.WHITE);
		ramSliderLabel.setBounds(177, 11, 64, 52);
		menuCentral.add(ramSliderLabel);
		final JSlider ramSlider = new JSlider();
		ramSlider.setForeground(Color.WHITE);
		ramSlider.setBackground(Color.DARK_GRAY);
		ramSlider.setPaintLabels(true);
		ramSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider) e.getSource();
				int v = source.getValue();
				if (v < 128)
					v = 128;
				if (!source.getValueIsAdjusting()) {
					source.setValue(v);
				}
				ramSliderLabel.setText(String.valueOf(v));

			}
		});
		ramSlider.setMajorTickSpacing(256);
		ramSlider.setMinorTickSpacing(64);
		ramSlider.setMaximum(2048);
		ramSlider.setToolTipText("Script RAM limit");
		ramSlider.setPaintTicks(true);
		ramSlider.setBounds(251, 11, 549, 52);
		menuCentral.add(ramSlider);
		ramSlider.setValue(Integer.parseInt(Data.xmxValue));

		final JLabelToggle useCacheToggle = new JLabelToggle();
		useCacheToggle.setIcon(new ImageIcon(SettingsPanel.class.getResource("/res/switch_off.png")));
		useCacheToggle.setSelectedIcon(new ImageIcon(SettingsPanel.class.getResource("/res/switch_on.png")));
		useCacheToggle.setSelected(Data.useInternalCache);
		useCacheToggle.setBounds(456, 74, 64, 52);
		menuCentral.add(useCacheToggle);

		JLabel lblNewLabel = new JLabel("Script RAM limit");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setBounds(28, 9, 135, 54);
		menuCentral.add(lblNewLabel);

		JLabel lblNewLabel_1 = new JLabel("Use Fragment Cache");
		lblNewLabel_1.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblNewLabel_1.setForeground(Color.WHITE);
		lblNewLabel_1.setBounds(28, 74, 299, 52);
		menuCentral.add(lblNewLabel_1);

		JLabel lblNewLabel_2 = new JLabel(
				"Force use GPU");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblNewLabel_2.setForeground(Color.WHITE);
		lblNewLabel_2.setBounds(28, 137, 330, 50);
		menuCentral.add(lblNewLabel_2);

		final JLabelToggle forceGPUToggle = new JLabelToggle();
		forceGPUToggle.setIcon(new ImageIcon(SettingsPanel.class.getResource("/res/switch_off.png")));
		forceGPUToggle.setSelectedIcon(new ImageIcon(SettingsPanel.class.getResource("/res/switch_on.png")));
		forceGPUToggle.setSelected(Data.isGPUForced());
		forceGPUToggle.setBounds(456, 137, 64, 50);
		menuCentral.add(forceGPUToggle);

		JLabel lblNewLabel_3 = new MyJLabel("Save");
		lblNewLabel_3.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				Data.xmxValue = String.valueOf(ramSlider.getValue());
				Data.forceGPU(forceGPUToggle.isSelected() ? true : false);
				Data.useInternalCache = useCacheToggle.isSelected() ? true
						: false;
				MyProperties.save();
			}
		});
		lblNewLabel_3.setIcon(new ImageIcon(SettingsPanel.class
				.getResource("/res/diskette.png")));
		lblNewLabel_3.setForeground(Color.WHITE);
		lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNewLabel_3.setBounds(22, 11, 101, 33);
		menuUpper.add(lblNewLabel_3);

	}
}
