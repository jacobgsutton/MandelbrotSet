package com.mandelbrot.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.mandelbrot.base.Mandelbrot;

@SuppressWarnings("serial")
public class MainFrame extends JFrame {
	private boolean fullScreen = true;
	private Mandelbrot m;
	private MainComponent mc;
	public MainFrame() {
		SwingUtilities.invokeLater(new Runnable() {
			private final Dimension DIM = Toolkit.getDefaultToolkit().getScreenSize();
			private final int MC_WIDTH = (DIM.height * 3) / 2;//DIM.width; //2560
			private final int MC_HEIGHT = DIM.height; //DIM.height; //1600
			private final int WIDTH = DIM.width + 16; 
			private final int HEIGHT = DIM.height + 39; 

			@Override
			public void run() {
				mc = new MainComponent(MC_WIDTH, MC_HEIGHT, WIDTH / 2);
				getContentPane().setBackground(Color.BLACK);
				setSize(WIDTH, HEIGHT);
				setLayout(null);
				setUndecorated(true);
				setExtendedState(JFrame.MAXIMIZED_BOTH);
				setLocation(DIM.width / 2 - getSize().width / 2, DIM.height / 2 - getSize().height / 2);
				setTitle("Mandelbrot Set");
				setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				setResizable(false);
				add(mc);
			
				setVisible(true);
				
				m = new Mandelbrot(mc, MC_WIDTH, MC_HEIGHT);
				new Thread(m).start();
				
				mc.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent me) {
						m.setCenter(me.getX(), me.getY());
						new Thread(m).start();
					}
				});
			} 
		});
		
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent ke) {
				switch(ke.getKeyCode()) {
					case KeyEvent.VK_F11:  
						if(fullScreen) {
							fullScreen = false;
							dispose();
							setUndecorated(false);
							setVisible(true);
						}
						else {
							fullScreen = true;
							dispose();
							setUndecorated(true);
							setExtendedState(JFrame.MAXIMIZED_BOTH);
							setVisible(true);
						}
						break;
					case KeyEvent.VK_UP:
						m.zoom(true);
						new Thread(m).start();
						break;
					case KeyEvent.VK_DOWN:
						m.zoom(false);
						new Thread(m).start();
						break;
					case KeyEvent.VK_P:
						m.nextPalette();
						new Thread(m).start();
						break;
					case KeyEvent.VK_RIGHT:
						m.incThreads();
						break;
					case KeyEvent.VK_LEFT:
						m.decThreads();
						break;
					case KeyEvent.VK_W:
						m.incMaxIterations();
						break;
					case KeyEvent.VK_S:
						m.decMaxIterations();
						break;
					case KeyEvent.VK_D:
						mc.toggleDrawAxes();
						break;
					case KeyEvent.VK_I:
						mc.toggleInfoBar();
						break;
				}
			}	
		});
	}
}
