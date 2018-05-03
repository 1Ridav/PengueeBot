package bot.penguee.gui;

import java.util.LinkedList;

public class ScriptQuickrunQueue extends LinkedList{
	private int lengthLimit;
	public ScriptQuickrunQueue(int size) {
		this.lengthLimit = size;
	}
	

	public void addFirst(Object s){
		remove(s);
		super.addFirst(s);
		if(size() > lengthLimit)
			removeLast();
	}
	
	public void add(Object[] list){
		for (Object s : list)
			add(s);
	}


}
