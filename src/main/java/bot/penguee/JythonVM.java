package bot.penguee;

import org.python.util.PythonInterpreter;

class JythonVM {
	private boolean isJythonVMLoaded = false;
	private Object jythonLoad = new Object();
	private PythonInterpreter pi = null;

	public JythonVM() {
		// TODO Auto-generated constructor stub
	}

	void load() {
		System.out.println("CORE: Loading JythonVM...");
		pi = new PythonInterpreter();
		isJythonVMLoaded = true;
		System.out.println("CORE: JythonVM loaded.");
		synchronized (jythonLoad) {
			jythonLoad.notify();
		}
	}

	void run(String script) throws Exception {
		System.out.println("CODE: Waiting for JythonVM to load");
		if (!isJythonVMLoaded)
			synchronized (jythonLoad) {
				jythonLoad.wait();
			}
		System.out.println("CORE: Running " + script + "...\n\n");
		pi.execfile(script);
		System.out.println("CORE: Script execution finished.");
	}

}
