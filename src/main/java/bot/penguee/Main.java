package bot.penguee;

import bot.penguee.gui.GUI;
import bot.penguee.scripting.ScriptEngine;

import java.io.File;
import java.nio.file.Paths;
//I did not want to use Spring Boot, so some 
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
			} else if (args[i].equals("-fragments")) {
				if (Paths.get(args[i+1]).isAbsolute())
					Data.setFragmentsPath(args[i+1]);
				else
					Data.setFragmentsPath(new File("").getAbsolutePath() + File.separator + args[i+1]);
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
			@Override
			public void run() {
				try {
					Data.scriptEngine = new ScriptEngine();
					Data.scriptEngine.load();
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
			Data.scriptEngine.run(Data.getScriptFileName());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("CORE: Failed to run script " + Data.getScriptFileName());
		}
	}

	private void runUIMode() {
		GlobalProperties.load();
		new GUI();
	}

}