package bot.penguee.scripting;

public interface ScriptEngineInterface
{
	void load();
	void run(String script) throws Exception;
}
