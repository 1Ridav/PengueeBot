package bot.penguee;

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
		/*
		Screen scr = new Screen();
		try {
			Data.loadFragments();
			scr.grab();
			//scr.setSearchRect(98, 119, 130, 141);
			long t1 = System.currentTimeMillis();
			MatrixPosition mp = scr.findSimilar("mytest2", 98);
			long t2 = System.currentTimeMillis();
			System.out.println("Similarity find took " + (t2-t1));
			if(mp != null)
				System.out.println(mp.x + " similar " + mp.y);
			else 
				System.out.println("similar Fail");
			 mp = scr.find("mytest2");
			
			if(mp != null)
				System.out.println(mp.x + " standart " + mp.y);
			else 
				System.out.println("standart Fail");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.exit(0);*/

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