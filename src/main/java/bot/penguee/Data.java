package bot.penguee;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import bot.penguee.fragments.Frag;
import bot.penguee.fragments.FragMono;
import bot.penguee.fragments.FragTransparent;
import bot.penguee.gui.ScriptQuickrunQueue;
import bot.penguee.screen.cpu.Screen;
import bot.penguee.scripting.ScriptEngine;

public class Data {
	String s = new File("").getAbsolutePath();
	//Default fragments path value could be overriden in Main
	private static String fragmentsPath = new File("").getAbsolutePath() + File.separator + "frag";
	private static HashMap<String, Frag> fragments = new HashMap<String, Frag>();
	private static String absPath;
	private static String colorRegex = "_[(]{2}[-]?[0-9]+[)]{2}";
	private static String colorRegex2 = ".*" + colorRegex + ".*";
	private static String transparentRegex = "_[(]{2}TRANSPARENT[)]{2}";
	private static String transparentRegex2 = ".*" + transparentRegex + ".*";
	private static String scriptFileName = "script.py";


	static ScriptEngine scriptEngine = null;
	private static boolean forceUseGPU = false;
	private static boolean useInternalCache = true;
	private static ArrayList<File> failedToLoadFragmentsList = null;
	private static ScriptQuickrunQueue recentScriptsList = new ScriptQuickrunQueue(7);
	private static String xmxValue;

	public Data() {

	}
	private static String[] scriptArgs = null;
	public static String getFragmentsPath() {
		return fragmentsPath;
	}
	public static void setFragmentsPath(String path) {
		Data.fragmentsPath = path;
	}

	public static Object[] getFragmentKeys() {
		return fragments.keySet().toArray();
	}

	public static String getScriptFileName() {
		return scriptFileName;
	}

	public static void setScriptFileName(String scriptFileName) {
		Data.scriptFileName = scriptFileName;
	}
	public static void setScriptArgs(String [] args){
		scriptArgs = args;
	}
	public static String[] getScriptArgs(){
		return scriptArgs;
	}
	public static ScriptEngine initScriptEngine(){
		scriptEngine = new ScriptEngine();
		return scriptEngine;
	}
	public static boolean getForceUseGPU() {
		return forceUseGPU;
	}

	public static void setForceUseGPU(boolean forceUseGPU) {
		Data.forceUseGPU = forceUseGPU;
	}

	public static boolean getUseInternalCache() {
		return useInternalCache;
	}

	public static void setUseInternalCache(boolean useInternalCache) {
		Data.useInternalCache = useInternalCache;
	}

	public static String getXmxValue() {
		return xmxValue;
	}

	public static void setXmxValue(String xmxValue) {
		Data.xmxValue = xmxValue;
	}

	public static ScriptQuickrunQueue getRecentScriptsList() {
		return recentScriptsList;
	}

	public static HashMap<String, Frag> fragments() {
		return fragments;
	}

	static void loadFragments(boolean log) {

		File f = new File(Data.getFragmentsPath());
		Data.absPath = f.getAbsolutePath();
		loadFragments(f, true);

		if (failedToLoadFragmentsList != null) {
			System.out.println("CORE: Error occured while loading " + failedToLoadFragmentsList.size()
					+ " fragments. Here is the list:");
			for (File k : failedToLoadFragmentsList) {
				System.out.println("     " + k.getAbsolutePath());
			}
		}
	}

	static void loadFragments() {
		loadFragments(true);
	}

	private static void loadFragments(File folder, boolean log) {
		System.out.println(folder.getAbsolutePath());
		if (!folder.exists())
			folder.mkdir();

		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				loadFragments(fileEntry, log);
			} else {// is file
				String key = fileEntry.getAbsolutePath().replace(absPath, "").replace(File.separator, ".")
						.replace(".bmp", "").replace(".png", "").replaceAll(colorRegex, "")
						.replaceAll(transparentRegex, "");
				key = key.substring(1);// remove starting dot . character
				if (log)
					System.out.println(fileEntry.getName() + " " + key);
				try {
					String absolutePath = fileEntry.getAbsolutePath();
					if (absolutePath.matches(colorRegex2))
						fragments.put(key, new FragMono(absolutePath));
					else if (absolutePath.matches(transparentRegex2))
						fragments.put(key, new FragTransparent(absolutePath));
					else
						fragments.put(key, new Frag(absolutePath));
				} catch (Exception e) {
					e.printStackTrace();
					if (failedToLoadFragmentsList == null)
						failedToLoadFragmentsList = new ArrayList<File>();
					failedToLoadFragmentsList.add(fileEntry);
				}
			}
		}
	}
}
