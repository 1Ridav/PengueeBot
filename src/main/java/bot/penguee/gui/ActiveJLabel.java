/*
 * This class make JLabel more interactive, provide reaction to mouse motions
 * 
 * */

package bot.penguee.gui;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

public class ActiveJLabel extends JLabel{
	Color normalColor = null;
	Color activeColor = null;
	public ActiveJLabel(String s) {
		super(s);
		activeColor = Color.LIGHT_GRAY;
		addMouseListener(new MouseAdapter() {
		
			@Override
			public void mouseEntered(MouseEvent e) {
				normalColor = getForeground();
				setForeground(activeColor);
			}
			@Override
			public void mouseExited(MouseEvent e) {
				setForeground(normalColor);
			}
		});
	}
	
	public void setActiveColor(Color c){
		activeColor = c;
	}
	

}
