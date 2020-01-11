package bot.penguee.scripting.jython;

import bot.penguee.scripting.ScriptEngineInterface;
import org.python.util.PythonInterpreter;

public class JythonVM implements ScriptEngineInterface{
	private boolean isJythonVMLoaded = false;
	private Object jythonLoad = new Object();
	private PythonInterpreter pi = null;

	public JythonVM() {
		// TODO Auto-generated constructor stub
	}

	public void load() {
		System.out.println("CORE: Loading JythonVM...");
		pi = new PythonInterpreter();
		isJythonVMLoaded = true;
		System.out.println("CORE: JythonVM loaded.");
		synchronized (jythonLoad) {
			jythonLoad.notify();
		}
	}

	public void run(String script) throws Exception {
		System.out.println("CODE: Waiting for JythonVM to load");
		if (!isJythonVMLoaded)
			synchronized (jythonLoad) {
				jythonLoad.wait();
			}
		System.out.println("CORE: Running " + script + "...\n\n");
		pi.exec("import sys");
		pi.exec("sys.path.append(\"./\")");
		pi.execfile(script);
		System.out.println("CORE: Script execution finished.");
	}

}
