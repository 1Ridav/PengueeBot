package bot.penguee;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class GlobalProperties {
	private static String configFile = "config.properties";

	public GlobalProperties() {
		// TODO Auto-generated constructor stub
	}

	static void load() {
		boolean create = false;
		try (FileInputStream fis = new FileInputStream(configFile)) {
			Properties p = new Properties();
			p.load(fis);
			Data.setForceUseGPU(p.getProperty("force_use_GPU").equals("1") ? true : false);
			Data.setXmxValue(p.getProperty("RAM_xmx") != null ? p.getProperty("RAM_xmx") : "512");
			Data.setScriptFileName(
					p.getProperty("default_script") != null ? p.getProperty("default_script") : "script.py");
			Data.setUseInternalCache(p.getProperty("use_fragments_cache").equals("1") ? true : false);
			String[] scriptsList = p.getProperty("recent_scripts").split(":::");
			Data.getRecentScriptsList().add(scriptsList);


		} catch (FileNotFoundException e) {
			e.printStackTrace();
			create = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			create = true;
		} catch (Exception e) {
			e.printStackTrace();
			create = true;
		}
		if (create)
			create();
	}

	public static void save() {
		try (FileOutputStream fis = new FileOutputStream(configFile)) {
			Properties p = new Properties();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < Data.getRecentScriptsList().size(); i++) {
				String name = (String) Data.getRecentScriptsList().get(i);
				sb.append(name);
				sb.append(":::");
			}
			p.setProperty("recent_scripts", sb.toString());
			p.setProperty("default_script", "script.py");
			p.setProperty("RAM_xmx", Data.getXmxValue());
			p.setProperty("force_use_GPU", Data.getForceUseGPU() ? "1" : "0");
			p.setProperty("use_fragments_cache", Data.getUseInternalCache() == true ? "1" : "0");
			p.store(new FileOutputStream(configFile), null);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	static void create() {
		boolean reload = false;
		try (FileOutputStream fis = new FileOutputStream(configFile)) {
			Properties p = new Properties();
			p.setProperty("recent_scripts", "script.py");
			p.setProperty("default_script", "script.py");
			p.setProperty("RAM_xmx", "512");
			p.setProperty("force_use_GPU", "0");
			p.setProperty("use_fragments_cache", "1");

			p.store(new FileOutputStream(configFile), null);
			reload = true;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if (reload)
			load();
	}

}
