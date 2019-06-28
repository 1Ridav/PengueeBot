package bot.penguee.scripting;
import bot.penguee.scripting.jython.*;

public class ScriptEngine implements ScriptEngineInterface{

	ScriptEngineInterface se;
	public ScriptEngine() {
		se = new JythonVM();
	}

	public void load() {
		se.load();
	}

	public void run(String script) throws Exception {
		se.run(script);
	}
}
