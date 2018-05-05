package bot.penguee.gui;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

import bot.penguee.Update;

public class GUI {

	private JFrame mainFrame;
	static JPanel settingsMainPanel;
	static JPanel logMainPanel;
	static JPanel grabMainPanel;
	static JPanel homeMainPanel;
	Update update = new Update();
	Point mouseDownCompCoords;
	private JLabel lblUpdateAvailable;
	int width = 900, height = 585;
	public GUI() {
		try {
			UIManager
					.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
		} catch (Exception e) {
		}
		initialize();
		showHomePanel();
	}

	private void initialize() {

		mainFrame = new JFrame();
		Rectangle screenRect = new Rectangle(Toolkit
				.getDefaultToolkit().getScreenSize());
		int x = (int) (screenRect.getCenterX()-(width/2));
		int y = (int) (screenRect.getCenterY()-(height/2));
		mainFrame.setTitle("PengueeBot");
		mainFrame.setBounds(x, y, 0, 0);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.getContentPane().setLayout(null);

		
		JPanel sidePanel = new JPanel();
		sidePanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent arg0) {
				mouseDownCompCoords = arg0.getPoint();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				mouseDownCompCoords = null;
			}
		});
		sidePanel.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent arg0) {
				Point currCoords = arg0.getLocationOnScreen();
				mainFrame.setLocation(currCoords.x - mouseDownCompCoords.x,
						currCoords.y - mouseDownCompCoords.y);
			}
		});
		sidePanel.setBounds(0, 0, 66, 585);
		sidePanel.setBackground(new Color(0, 0, 102));
		mainFrame.getContentPane().add(sidePanel);

		JLabel menuLabel = new JLabel("");
		menuLabel.setToolTipText("Settings");
		menuLabel.setBounds(17, 78, 32, 26);
		menuLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				showSettingsPanel();
			}
		});
		menuLabel.setIcon(new ImageIcon(GUI.class
				.getResource("/res/menu.png")));

		JLabel logLabel = new JLabel("");
		logLabel.setToolTipText("Console");
		logLabel.setBounds(17, 265, 32, 26);
		logLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				showLogPanel();
			}
		});
		logLabel.setIcon(new ImageIcon(GUI.class
				.getResource("/res/browser.png")));

		JLabel grabLabel = new JLabel("");
		grabLabel.setToolTipText("Fragment Create");
		grabLabel.setBounds(17, 200, 32, 32);
		grabLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				showGrabPanel();
			}
		});
		grabLabel.setIcon(new ImageIcon(GUI.class
				.getResource("/res/photo-camera.png")));

		JLabel exitLabel = new JLabel("");
		exitLabel.setToolTipText("Exit");
		exitLabel.setBounds(17, 11, 32, 32);
		exitLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				System.exit(0);
			}
		});
		exitLabel.setIcon(new ImageIcon(GUI.class
				.getResource("/res/remove.png")));
		sidePanel.setLayout(null);

		lblUpdateAvailable = new JLabel("");
		lblUpdateAvailable.setToolTipText("Update");
		lblUpdateAvailable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				Update.update();
			}
		});
		lblUpdateAvailable.setBounds(17, 531, 32, 43);
		lblUpdateAvailable.setHorizontalAlignment(SwingConstants.LEFT);
		lblUpdateAvailable.setIcon(new ImageIcon(GUI.class
				.getResource("/res/download.png")));
		lblUpdateAvailable.setForeground(Color.WHITE);
		lblUpdateAvailable.setVisible(false);

		sidePanel.add(lblUpdateAvailable);
		sidePanel.add(grabLabel);
		sidePanel.add(menuLabel);
		sidePanel.add(logLabel);
		sidePanel.add(exitLabel);

		JLabel homeLabel = new JLabel("");
		homeLabel.setToolTipText("Home");
		homeLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				showHomePanel();
			}
		});
		homeLabel.setIcon(new ImageIcon(GUI.class
				.getResource("/res/house-outline.png")));
		homeLabel.setForeground(Color.WHITE);
		homeLabel.setBounds(17, 132, 32, 32);
		sidePanel.add(homeLabel);

		settingsMainPanel = new SettingsPanel(mainFrame);
		mainFrame.getContentPane().add(settingsMainPanel);

		grabMainPanel = new GrabPanel(mainFrame);
		mainFrame.getContentPane().add(grabMainPanel);

		logMainPanel = new LogPanel(mainFrame);
		mainFrame.getContentPane().add(logMainPanel);

		homeMainPanel = new HomePanel();
		mainFrame.getContentPane().add(homeMainPanel);
		mainFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(GUI.class.getResource("/res/main_icon_64.png")));
		mainFrame.setUndecorated(true);
		mainFrame.setSize(width, height);
		mainFrame.setVisible(true);
		checkForUpdates();
	}

	private static void showLogPanel() {
		logMainPanel.setVisible(true);
		grabMainPanel.setVisible(false);
		settingsMainPanel.setVisible(false);
		homeMainPanel.setVisible(false);
		
	}
	private static void showGrabPanel() {
		logMainPanel.setVisible(false);
		grabMainPanel.setVisible(true);
		settingsMainPanel.setVisible(false);
		homeMainPanel.setVisible(false);
		
	}
	
	private static void showHomePanel() {
		logMainPanel.setVisible(false);
		grabMainPanel.setVisible(false);
		settingsMainPanel.setVisible(false);
		homeMainPanel.setVisible(true);
		
	}
	private static void showSettingsPanel() {
		logMainPanel.setVisible(false);
		grabMainPanel.setVisible(false);
		settingsMainPanel.setVisible(true);
		homeMainPanel.setVisible(false);
		
	}

	private void checkForUpdates() {
		Thread checkUpdateThread = new Thread() {
			@Override
			public void run() {
				lblUpdateAvailable.setVisible(Update.available());
			}
		};
		checkUpdateThread.setDaemon(true);
		checkUpdateThread.start();
	}
	
	public static void runScript(String name){
		showLogPanel();
		
		((LogPanel) logMainPanel).runScript(name);
	}
}
