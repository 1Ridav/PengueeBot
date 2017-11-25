package bot.penguee.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;

import bot.penguee.Frag;
import bot.penguee.FragMono;
import bot.penguee.MatrixPosition;
import bot.penguee.Screen;

public class GrabPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BufferedImage image;
	private Point p1 = null;
	private Point p2 = null;

	private BufferedImage imageFragment = null;
	private BufferedImage imageCursorZoomFragment = null;
	private Screen scr;
	private final static short previewScaleRate = 4;
	private final static short zoomScaleRate = 5;
	private int pixel_color_num = 0;
	private JFrame frame;
	private JPanel panel_cursor_zoom;
	private JPanel panel_fragment_zoom;
	private JScrollPane scrollPane;
	Frag testFrag;
	MatrixPosition testFragMP;

	JLabel testDelayLabel;
	private JLabel lblXAndY;
	private JPanel panel_pixel_color;

	GrabPanel(JFrame frame) {
		this.frame = frame;
		this.scr = new Screen(false);
		try {
			scr.grab();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		image = scr.getImage();

		init();
	}

	private void init() {
		setLayout(null);
		setBackground(new Color(0, 255, 0));
		setBounds(66, 0, 834, 585);
		JPanel grabUpper = new JPanel();
		grabUpper.setBounds(0, 0, 834, 55);
		grabUpper.setBackground(new Color(0, 102, 102));
		grabUpper.setLayout(null);

		JPanel grabCentral = new JPanel();
		grabCentral.setBackground(Color.DARK_GRAY);
		grabCentral.setBounds(0, 55, 834, 530);
		grabCentral.setLayout(null);

		final JPanel grabPanelScreenshot = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(getImage(), 0, 0, null);
				if (p1 != null && p2 != null)
					g.drawRect(p1.x, p1.y, p2.x - p1.x, p2.y - p1.y);
				if (testFragMP != null) {
					MatrixPosition c = testFrag.center();
					MatrixPosition mp = testFragMP.relative(-c.x, -c.y);
					g.setColor(Color.RED);
					g.drawRect(mp.x, mp.y, testFrag.getWidth(),
							testFrag.getHeight());
				}
			}

		};
		Rectangle rect = new Rectangle(Toolkit.getDefaultToolkit()
				.getScreenSize());
		grabPanelScreenshot.setPreferredSize(new Dimension(rect.width,
				rect.height));
		JScrollPane grabPanelInternalScroller = new JScrollPane(
				grabPanelScreenshot);
		grabPanelInternalScroller.setBounds(0, 0, 834, 430);
		grabCentral.add(grabPanelInternalScroller);
		grabPanelInternalScroller.setPreferredSize(new Dimension(600, 400));

		JLabel lblNewLabel = new MyJLabel("GRAB");
		lblNewLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				updateImage();
			}
		});
		lblNewLabel.setForeground(new Color(255, 255, 255));
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNewLabel.setIcon(new ImageIcon(GUI.class
				.getResource("/res/grab.png")));
		lblNewLabel.setBounds(10, 11, 107, 32);
		grabUpper.add(lblNewLabel);

		JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.setLabel("");
		popupMenu.setBackground(UIManager.getColor("Button.background"));
		addPopup(lblNewLabel, popupMenu);

		MyJLabel btnNewButton = new MyJLabel("Delay 3 sec");
		btnNewButton.setActiveColor(new Color(0, 153, 153));
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnNewButton.setForeground(new Color(0, 102, 102));
		btnNewButton.setBackground(new Color(0, 102, 102));
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				updateImage(3000);
			}
		});
		popupMenu.add(btnNewButton);

		MyJLabel btnNewButton_1 = new MyJLabel("Delay 5 sec");
		btnNewButton_1.setActiveColor(new Color(0, 153, 153));
		btnNewButton_1.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnNewButton_1.setForeground(new Color(0, 102, 102));
		btnNewButton_1.setBackground(new Color(0, 102, 102));
		btnNewButton_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				updateImage(5000);
			}
		});
		popupMenu.add(btnNewButton_1);

		MyJLabel btnNewButton_2 = new MyJLabel("Delay 8 sec");
		btnNewButton_2.setActiveColor(new Color(0, 153, 153));
		btnNewButton_2.setFont(new Font("Tahoma", Font.BOLD, 15));
		btnNewButton_2.setForeground(new Color(0, 102, 102));
		btnNewButton_2.setBackground(new Color(0, 102, 102));
		btnNewButton_2.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				updateImage(8000);
			}
		});
		popupMenu.add(btnNewButton_2);
		add(grabUpper);

		JLabel lblTest = new MyJLabel("TEST");
		lblTest.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					testFrag = new Frag(copyImage(imageFragment));
					long t1 = System.nanoTime();
					testFragMP = scr.find(testFrag);
					long t2 = System.nanoTime();
					testDelayLabel.setText(((t2 - t1) / 1000000) + " ms");
					grabPanelScreenshot.repaint();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					 e1.printStackTrace();
				}

			}
		});
		lblTest.setIcon(new ImageIcon(GrabPanel.class
				.getResource("/res/search.png")));
		lblTest.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblTest.setForeground(new Color(255, 255, 255));
		lblTest.setBounds(146, 9, 107, 37);
		grabUpper.add(lblTest);
		
		JPopupMenu popupMenu_1 = new JPopupMenu();
		addPopup(lblTest, popupMenu_1);
		
		MyJLabel labelCountAllMatches = new MyJLabel("Count all matches");
		labelCountAllMatches.setActiveColor(new Color(0, 153, 153));
		labelCountAllMatches.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					
					testFrag = new Frag(copyImage(imageFragment));
					long t1 = System.nanoTime();
					MatrixPosition[] list = scr.find_all(testFrag);
					long t2 = System.nanoTime();
					testDelayLabel.setText(((t2 - t1) / 1000000) + " ms");
					grabPanelScreenshot.repaint();
					JOptionPane.showMessageDialog(frame, (list != null ? list.length : 0) + " matches found");
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					// e1.printStackTrace();
				}
			}
		});
		labelCountAllMatches.setForeground(new Color(0, 102, 102));
		labelCountAllMatches.setFont(new Font("Tahoma", Font.BOLD, 15));
		labelCountAllMatches.setBackground(new Color(0, 102, 102));
		popupMenu_1.add(labelCountAllMatches);
		
		MyJLabel labelTestMono = new MyJLabel("Test mono");
		labelTestMono.setActiveColor(new Color(0, 153, 153));
		labelTestMono.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					
					
					
					testFrag = new FragMono(copyImage(imageFragment), pixel_color_num);
					long t1 = System.nanoTime();
					MatrixPosition[] list = scr.find_all(testFrag);
					long t2 = System.nanoTime();
					testDelayLabel.setText(((t2 - t1) / 1000000) + " ms");
					grabPanelScreenshot.repaint();
					JOptionPane.showMessageDialog(frame, (list != null ? list.length : 0) + " matches found");
					
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		labelTestMono.setForeground(new Color(0, 102, 102));
		labelTestMono.setFont(new Font("Tahoma", Font.BOLD, 15));
		labelTestMono.setBackground(new Color(0, 102, 102));
		popupMenu_1.add(labelTestMono);


		JLabel lblNewLabel_1 = new MyJLabel("SAVE");
		lblNewLabel_1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				saveImage();
			}
		});

		lblNewLabel_1.setForeground(Color.WHITE);
		lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblNewLabel_1.setIcon(new ImageIcon(GrabPanel.class
				.getResource("/res/diskette.png")));
		lblNewLabel_1.setBounds(288, 11, 107, 32);
		grabUpper.add(lblNewLabel_1);

		JLabel lblSaveMono = new MyJLabel("SAVE MONO");
		lblSaveMono.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				saveMonoImage();
			}
		});
		lblSaveMono.setIcon(new ImageIcon(GrabPanel.class
				.getResource("/res/diskette.png")));
		lblSaveMono.setForeground(Color.WHITE);
		lblSaveMono.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblSaveMono.setBounds(423, 11, 164, 32);
		grabUpper.add(lblSaveMono);

		JLabel lblShowMono = new MyJLabel("SHOW MONO");
		lblShowMono.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				showMonoImage();
			}
		});
		lblShowMono.setIcon(new ImageIcon(GrabPanel.class
				.getResource("/res/fingerprint.png")));
		lblShowMono.setForeground(Color.WHITE);
		lblShowMono.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblShowMono.setBounds(631, 11, 177, 32);
		grabUpper.add(lblShowMono);
		add(grabCentral);
		grabPanelInternalScroller.getVerticalScrollBar().setUnitIncrement(16);
		grabPanelInternalScroller.getHorizontalScrollBar().setUnitIncrement(16);

		grabPanelScreenshot.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				// System.out.println("mouseClicked    " + evt.getX() + "   " +
				// evt.getY());
				p1 = p2 = null;
				pixel_color_num = image.getRGB(evt.getX(), evt.getY());
				// panel_pixel_color.repaint();

			}

			public void mousePressed(java.awt.event.MouseEvent evt) {
				/*System.out.println("mousePressed    " + evt.getX() + "   "
						+ evt.getY());*/
				p1 = p2 = null;
				p1 = evt.getPoint();
			}

			public void mouseReleased(java.awt.event.MouseEvent evt) {
				/*System.out.println("mouseReleased    " + evt.getX() + "   "
						+ evt.getY());*/
				p2 = evt.getPoint();

				if (p2.getX() != p1.getX() || p2.getY() != p1.getY()) {
					try {
						imageFragment = getImage().getSubimage(p1.x, p1.y,
								p2.x - p1.x, p2.y - p1.y);
						panel_fragment_zoom.setPreferredSize(new Dimension(
								imageFragment.getWidth() * previewScaleRate,
								imageFragment.getHeight() * previewScaleRate));
						grabPanelScreenshot.repaint();
						// SwingUtilities.updateComponentTreeUI(frame);
						scrollPane.revalidate();
						scrollPane.repaint();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				try {
					imageCursorZoomFragment = getImage().getSubimage(p2.x - 10,
							p2.y - 10, 20, 20);
					panel_cursor_zoom.repaint();
				} catch (Exception e) {
					// e.printStackTrace();
				}
			}
		});

		grabPanelScreenshot
				.addMouseMotionListener(new java.awt.event.MouseAdapter() {
					public void mouseDragged(java.awt.event.MouseEvent evt) {
						// System.out.println("mouseDragged    " + evt.getX() +
						// "   " + evt.getY());
						p2 = evt.getPoint();
						lblXAndY.setText("X: " + evt.getX() + "   Y: "
								+ evt.getY());
						if (p2.x != p1.x || p2.y != p1.y) {
							// System.out.println("   " + (p2.x - p1.x) + " " +
							// (p2.y - p1.y));
							if ((p2.x - p1.x) > 0 && (p2.y - p1.y) > 0) {
								imageFragment = getImage().getSubimage(p1.x,
										p1.y, p2.x - p1.x, p2.y - p1.y);

								panel_fragment_zoom
										.setPreferredSize(new Dimension(
												imageFragment.getWidth()
														* previewScaleRate,
												imageFragment.getHeight()
														* previewScaleRate));

								// SwingUtilities.updateComponentTreeUI(frame);
								grabPanelScreenshot.repaint();
								scrollPane.revalidate();
								scrollPane.repaint();
							}
						}

						try {
							imageCursorZoomFragment = getImage().getSubimage(
									p2.x - 10, p2.y - 10, 20, 20);
							panel_cursor_zoom.repaint();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					public void mouseMoved(java.awt.event.MouseEvent evt) {
						// System.out.println("mouseMoved    " + evt.getX() +
						// "   " + evt.getY());
						int x = evt.getX();
						int y = evt.getY();
						try {
							lblXAndY.setText("X: " + x + "   Y: " + y);
							imageCursorZoomFragment = getImage().getSubimage(
									x - 10, y - 10, 20, 20);
							panel_cursor_zoom.repaint();
						} catch (Exception e) {
							// e.printStackTrace();
						}
					}
				});

		panel_fragment_zoom = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (imageFragment != null)
					try {
						g.drawImage(imageFragment, 0, 0,
								imageFragment.getWidth() * previewScaleRate,
								imageFragment.getHeight() * previewScaleRate,
								null);
					} catch (Exception e) {
						e.printStackTrace();
					}
			}
		};
		panel_fragment_zoom.setBackground(Color.DARK_GRAY);

		scrollPane = new JScrollPane(panel_fragment_zoom);
		panel_fragment_zoom.setLayout(null);
		scrollPane.setBounds(346, 430, 488, 100);
		grabCentral.add(scrollPane);
		scrollPane.setPreferredSize(new Dimension(600, 400));

		panel_cursor_zoom = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				// if (imageCursorZoomFragment != null)
				try {
					g.drawImage(
							imageCursorZoomFragment,
							0,
							0,
							imageCursorZoomFragment.getWidth() * zoomScaleRate,
							imageCursorZoomFragment.getHeight() * zoomScaleRate,
							null);
					int x = imageCursorZoomFragment.getWidth() * zoomScaleRate
							/ 2;
					int y = imageCursorZoomFragment.getHeight() * zoomScaleRate
							/ 2;
					g.drawLine(x, 0, x, imageCursorZoomFragment.getHeight()
							* zoomScaleRate);
					g.drawLine(0, y, imageCursorZoomFragment.getWidth()
							* zoomScaleRate, y);
				} catch (Exception e) {
					// e.printStackTrace();
				}
			}
		};
		panel_cursor_zoom.setBounds(0, 430, 100, 100);
		grabCentral.add(panel_cursor_zoom);
		panel_cursor_zoom.setBackground(Color.DARK_GRAY);
		panel_cursor_zoom.setLayout(null);

		testDelayLabel = new JLabel("----");
		testDelayLabel.setForeground(Color.WHITE);
		testDelayLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		testDelayLabel.setBackground(Color.WHITE);
		testDelayLabel.setBounds(207, 441, 87, 22);
		grabCentral.add(testDelayLabel);

		JLabel lblNewLabel_2 = new JLabel("Test delay:");
		lblNewLabel_2.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblNewLabel_2.setForeground(Color.WHITE);
		lblNewLabel_2.setBounds(110, 441, 87, 22);
		grabCentral.add(lblNewLabel_2);

		lblXAndY = new JLabel("X: ---   Y: ---");
		lblXAndY.setForeground(Color.WHITE);
		lblXAndY.setFont(new Font("Tahoma", Font.PLAIN, 16));
		lblXAndY.setBounds(110, 474, 184, 22);
		grabCentral.add(lblXAndY);

		panel_pixel_color = new JPanel();
		panel_pixel_color.setBorder(new CompoundBorder());
		panel_pixel_color.setBackground(Color.DARK_GRAY);
		panel_pixel_color.setBounds(325, 430, 22, 100);
		grabCentral.add(panel_pixel_color);

		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(16);

		panel_fragment_zoom.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				// System.out.println("mouseClicked    " + evt.getX() + "   " +
				// evt.getY());
				try {
					pixel_color_num = imageFragment.getRGB(evt.getX()
							/ previewScaleRate, evt.getY() / previewScaleRate);
					System.out.println(pixel_color_num);
					panel_pixel_color.setBackground(new Color(pixel_color_num));
				} catch (Exception e) {

				}
			}
		});
	}

	private BufferedImage getImage() {
		return image;
	}

	private void updateImage(int delay) {
		Timer timer = new Timer(delay, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateImage();
			}
		});
		timer.setRepeats(false); // Only execute once
		timer.start(); // Go go go!
	}

	private void updateImage() {
		try {
			scr.grab();
			image = scr.getImage();
			testFragMP = null;
			repaint();
			imageFragment = getImage().getSubimage(p1.x, p1.y, p2.x - p1.x,
					p2.y - p1.y);
			panel_fragment_zoom.repaint();
		} catch (Exception e) {

		}
		System.gc();
		Runtime.getRuntime().freeMemory();
	}

	private void saveImage() {
		try {
			String s = (String) JOptionPane.showInputDialog(frame,
					"Fragment name...", "Colored fragment",
					JOptionPane.PLAIN_MESSAGE);
			if (s != null)
				new Frag(imageFragment).makeFile(s);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(frame,
					"Error occured while trying to save");
		}
	}

	private void saveMonoImage() {
		try {
			imageFragment = optimizeMonoImage(imageFragment);
			String s = (String) JOptionPane.showInputDialog(frame,
					"Fragment name...", "One colored fragment",
					JOptionPane.PLAIN_MESSAGE);
			if (s != null)
				new Frag(imageFragment).makeFile(s + "_((" + pixel_color_num
						+ "))");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(frame,
					"Error occured while trying to save");
		}
	}

	private void showMonoImage() {
		try {
			imageFragment = copyImage(imageFragment);
			final int bgColor = (pixel_color_num == -16777216) ? -1 : -16777216;
			for (int i = 0; i < imageFragment.getHeight(); i++) {
				for (int j = 0; j < imageFragment.getWidth(); j++) {
					if (imageFragment.getRGB(j, i) != pixel_color_num)
						imageFragment.setRGB(j, i, bgColor);
				}
			}

			imageFragment = optimizeMonoImage(imageFragment);
			panel_fragment_zoom.setPreferredSize(new Dimension(imageFragment
					.getWidth() * previewScaleRate, imageFragment.getHeight()
					* previewScaleRate));
			SwingUtilities.updateComponentTreeUI(frame);
			// panel_fragment_zoom.repaint();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private BufferedImage copyImage(BufferedImage source) {
		BufferedImage b = new BufferedImage(source.getWidth(),
				source.getHeight(), source.getType());
		Graphics g = b.getGraphics();
		g.drawImage(source, 0, 0, null);
		g.dispose();
		return b;
	}

	private BufferedImage optimizeMonoImage(BufferedImage img) {
		boolean canOptimizeLine = true;
		// OPTIMIZE UPPER SIDE
		while (canOptimizeLine) {
			for (int i = 0; i < img.getWidth(); i++) {
				if (img.getRGB(i, 0) == pixel_color_num) {
					canOptimizeLine = false;
					break;
				}
			}
			if (canOptimizeLine) {
				img = img
						.getSubimage(0, 1, img.getWidth(), img.getHeight() - 1);
			}

		}
		// OPTIMIZE BOTTOM SIDE
		canOptimizeLine = true;
		while (canOptimizeLine) {
			for (int i = 0; i < img.getWidth(); i++) {
				if (img.getRGB(i, img.getHeight() - 1) == pixel_color_num) {
					canOptimizeLine = false;
					break;
				}
			}
			if (canOptimizeLine) {
				img = img
						.getSubimage(0, 0, img.getWidth(), img.getHeight() - 1);
			}

		}
		// OPTIMIZE LEFT SIDE
		canOptimizeLine = true;
		while (canOptimizeLine) {
			for (int i = 0; i < img.getHeight(); i++) {
				if (img.getRGB(0, i) == pixel_color_num) {
					canOptimizeLine = false;
					break;
				}
			}
			if (canOptimizeLine) {
				img = img
						.getSubimage(1, 0, img.getWidth() - 1, img.getHeight());
			}

		}
		// OPTIMIZE RIGHT SIDE
		canOptimizeLine = true;
		while (canOptimizeLine) {
			for (int i = 0; i < img.getHeight(); i++) {
				if (img.getRGB(img.getWidth() - 1, i) == pixel_color_num) {
					canOptimizeLine = false;
					break;
				}
			}
			if (canOptimizeLine) {
				img = img
						.getSubimage(0, 0, img.getWidth() - 1, img.getHeight());
			}

		}

		return img;

	}

	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
