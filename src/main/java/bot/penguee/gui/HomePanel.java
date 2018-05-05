package bot.penguee.gui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URISyntaxException;

import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import bot.penguee.Data;
import bot.penguee.Update;

public class HomePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JEditorPane newsPane;

	/**
	 * Create the panel.
	 */
	public HomePanel() {
		setBounds(66, 0, 834, 585);
		setLayout(null);
		
		JPanel homeUpper = new JPanel();
		homeUpper.setBackground(new Color(0, 102, 102));
		homeUpper.setBounds(0, 0, 834, 55);
		add(homeUpper);
		homeUpper.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("PengueeBot   v" + Update.version);
		lblNewLabel.setIcon(new ImageIcon(HomePanel.class.getResource("/res/main_icon.png")));
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNewLabel.setForeground(Color.WHITE);
		lblNewLabel.setBounds(24, 11, 368, 32);
		homeUpper.add(lblNewLabel);
		
		JPanel homeCentral = new JPanel();
		homeCentral.setBackground(Color.DARK_GRAY);
		homeCentral.setBounds(0, 55, 834, 533);
		add(homeCentral);
		homeCentral.setLayout(null);
		
		newsPane = new JEditorPane();
		newsPane.setFont(new Font("Tahoma", Font.PLAIN, 13));
		newsPane.setBackground(Color.WHITE);
		newsPane.setContentType("text/html");
		newsPane.setEditable(false);
		newsPane.setBounds(0, 0, 834, 282);
		newsPane.addHyperlinkListener(new HyperlinkListener() {
		    @Override
			public void hyperlinkUpdate(HyperlinkEvent e) {
		        if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
		            if(Desktop.isDesktopSupported()) {
		                try {
		                    Desktop.getDesktop().browse(e.getURL().toURI());
		                }
		                catch (IOException | URISyntaxException e1) {
		                    e1.printStackTrace();
		                }
		            }
		        }
		    }
		}
		);
		
		JScrollPane scrollPane = new JScrollPane(newsPane);
		scrollPane.setBounds(0, 0, 834, 300);
		scrollPane.setPreferredSize(new Dimension(834, 300));
		homeCentral.add(scrollPane);
		
		int yy = 311;
		for(Object o: Data.getRecentScriptsList().toArray()){
			String s = (String) o;
			JLabel lbl = new ActiveJLabel(s);
			lbl.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					JLabel l = (JLabel) e.getSource();
					System.out.println(l.getText());
					GUI.runScript(l.getText());
				}
			});
			lbl.setFont(new Font("Tahoma", Font.PLAIN, 15));
			lbl.setForeground(Color.WHITE);
			lbl.setBounds(10, yy, 400, 27);
			yy+=30;
			homeCentral.add(lbl);
		}
		
		//homeCentral.add(newsPane);
		
		getNews();
	}
	
	private void getNews() {
		Thread checkUpdateThread = new Thread() {
			@Override
			public void run() {
				new Update();
				newsPane.setText(Update.getNews());
			}
		};
		checkUpdateThread.setDaemon(true);
		checkUpdateThread.start();
	}
}
