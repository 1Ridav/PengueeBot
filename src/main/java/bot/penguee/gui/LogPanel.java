package bot.penguee.gui;

import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import bot.penguee.Data;
import bot.penguee.GlobalProperties;
import bot.penguee.Main;

public class LogPanel extends JPanel {
	/**
	 * Create the panel.
	 */
	JFrame frame;
	JLabel labelStop;
	JLabel labelRun;
	Process scriptProcess;
	Thread scriptThread;
	private JTextArea logTextArea;
	private FileDialog fileDialog;
	private JLabel currentScriptNameLabel;

	public LogPanel(JFrame frame) {
		setForeground(Color.DARK_GRAY);
		this.frame = frame;

		setBackground(new Color(255, 255, 255));
		setBounds(66, 0, 834, 585);

		JPanel logUpper = new JPanel();
		logUpper.setBounds(0, 0, 834, 55);
		logUpper.setBackground(new Color(0, 102, 102));
		logUpper.setLayout(null);

		JPanel logCentral = new JPanel();
		logCentral.setBackground(new Color(102, 153, 51));
		logCentral.setBounds(0, 55, 834, 530);
		setLayout(null);
		add(logUpper);

		labelStop = new ActiveJLabel("STOP");
		labelStop.setVisible(false);
		labelStop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (scriptProcess != null)
					scriptProcess.destroyForcibly();
				labelStop.setVisible(false);
				labelRun.setVisible(true);
			}
		});
		labelStop.setIcon(new ImageIcon(LogPanel.class.getResource("/res/stop.png")));
		labelStop.setBounds(20, 11, 113, 33);
		logUpper.add(labelStop);
		labelStop.setForeground(Color.WHITE);
		labelStop.setFont(new Font("Tahoma", Font.BOLD, 18));
		add(logCentral);
		logCentral.setLayout(null);

		logTextArea = new JTextArea();
		logTextArea.setText("");
		logTextArea.setFont(new Font("Monospaced", Font.PLAIN, 16));
		// logTextArea.setFont(new Font("123", Font.PLAIN, 16));
		logTextArea.setForeground(Color.WHITE);
		logTextArea.setBackground(Color.DARK_GRAY);
		JScrollPane scrollPane = new JScrollPane(logTextArea);
		scrollPane.setBounds(0, 0, 834, 530);
		logCentral.add(scrollPane);

		labelRun = new ActiveJLabel("RUN");
		labelRun.setVisible(true);
		labelRun.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				runScript(Data.getScriptFileName());
			}
		});
		labelRun.setIcon(new ImageIcon(LogPanel.class.getResource("/res/play.png")));
		labelRun.setForeground(Color.WHITE);
		labelRun.setFont(new Font("Tahoma", Font.BOLD, 18));
		labelRun.setBounds(20, 11, 101, 33);
		logUpper.add(labelRun);

		JLabel lblNewLabel = new ActiveJLabel("LOAD");
		lblNewLabel.setIcon(new ImageIcon(LogPanel.class.getResource("/res/diskette.png")));
		lblNewLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				fileDialog.show();
				String file = fileDialog.getFile();
				String dir = fileDialog.getDirectory();
				if (file != null && dir != null) {
					Data.setScriptFileName(file + dir);
					currentScriptNameLabel.setText(Data.getScriptFileName());
				}
			}
		});

		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setBounds(723, 11, 101, 33);
		logUpper.add(lblNewLabel);

		currentScriptNameLabel = new JLabel((String) Data.getRecentScriptsList().getFirst());
		currentScriptNameLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		currentScriptNameLabel.setForeground(Color.WHITE);
		currentScriptNameLabel.setBounds(131, 11, 588, 33);
		logUpper.add(currentScriptNameLabel);

		fileDialog = new FileDialog(frame);
		// fc.showOpenDialog(frame);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (scriptProcess != null) {
					scriptProcess.destroyForcibly();
				}
				if (scriptThread != null)
					scriptThread.interrupt();
			}
		});
	}

	// create a new process. run console mode of this app and pass arguments
	public void runScript(String name) {
		try {
			Data.setScriptFileName(name);
			logTextArea.setText("");
			currentScriptNameLabel.setText(name);

			if (scriptProcess != null)
				scriptProcess.destroyForcibly();

			String pathToJar = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();

			ArrayList<String> params = new ArrayList<String>();

			params.add("java");
			params.add("-Xmx" + Data.getXmxValue() + "M");

			// params.add("-Dsun.java2d.noddraw=true");
			// params.add("-Dsun.java2d.d3d=false");
			// params.add("-Dsun.java2d.opengl=false");
			// params.add("-Dsun.java2d.pmoffscreen=false")
			params.add("-Dfile.encoding=UTF8");
			params.add("-classpath");
			params.add(pathToJar);
			params.add("bot.penguee.Main");
			//params.add("org.springframework.boot.loader.JarLauncher");
			params.add("-nogui");
			params.add("-script");
			params.add(name);
			System.out.println(params.toString());
			if (Data.getForceUseGPU())
				params.add("-forceUseGPU");

			ProcessBuilder pb = new ProcessBuilder(params);
			scriptProcess = pb.start();
			if (scriptProcess == null)
				throw new Exception("!");
			// redirect process error and output streams to GUI log area
			IOredirect.redirectOutput(logTextArea, scriptProcess.getInputStream(), scriptProcess.getErrorStream());
			// new thread to wait until process will not quit with ok status, then change
			// buttons state
			scriptThread = new Thread() {
				@Override
				public void run() {
					try {
						scriptProcess.waitFor();
						labelStop.setVisible(false);
						labelRun.setVisible(true);
					} catch (InterruptedException e) {

					}
				}
			};
			scriptThread.setDaemon(true);
			scriptThread.start();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// push script name to front of the list in properties, so it can be run from
		// Home panel
		Data.getRecentScriptsList().addFirst(name);
		GlobalProperties.save();
		// change buttons visibility
		labelRun.setVisible(false);
		labelStop.setVisible(true);
	}
}
