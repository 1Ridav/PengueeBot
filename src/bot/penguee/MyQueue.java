package bot.penguee;

import java.util.LinkedList;

public class MyQueue extends LinkedList{
	private int mysize;
	public MyQueue(int size) {
		this.mysize = size;
	}
	
	public void addFirst(Object s){
		remove(s);
		super.addFirst(s);
		if(size() > mysize)
			removeLast();
	}


}
