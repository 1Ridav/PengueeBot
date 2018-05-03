package bot.penguee;

import java.net.URISyntaxException;

import bot.penguee.gui.GUI;

public class Main {
	private static boolean consoleMode = false;

	public static void main(String[] args) {

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-nogui")) {
				consoleMode = true;
			} else if (args[i].equals("-script")) {
				Data.setScriptFileName(args[++i]);
			} else if (args[i].equals("-forceUseGPU")) {
				Data.setForceUseGPU(true);
			}
		}
		new Main();
	}

	Main() {

		if (consoleMode)
			runConsoleMode();
		else
			runUIMode();
	}

	private void runConsoleMode() {
		Runnable r = new Runnable() {
			public void run() {
				try {
					Data.jython = new JythonVM();
					Data.jython.load();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		Thread t = new Thread(r);
		t.setDaemon(true);
		t.start();

		System.out.println("CORE: Loading fragments. ");
		Data.loadFragments();

		System.out.println("CORE: Done. " + Data.fragments().size() + " loaded");

		try {
			Data.jython.run(Data.getScriptFileName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("CORE: Failed to run script " + Data.getScriptFileName());
		}
	}

	private void runUIMode() {
		GlobalProperties.load();
		new GUI();
	}

}