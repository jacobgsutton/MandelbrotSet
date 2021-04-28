package com.mandelbrot.GUI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;

import javax.swing.JPanel;
import javax.swing.JLayeredPane;

@SuppressWarnings("serial")
public class MainComponent extends JLayeredPane {
	private final BufferedImage image;
	private final JPanel mainPanel;
	private final int width;
	private final int height;
	private boolean drawAxes;
	private boolean activeInfoBar;
	private final InfoBar infoBar;
	
	public MainComponent(int width, int height, int centerX) {
		this.width = width;
		this.height = height;
		drawAxes = false;
		activeInfoBar = true;
		setPreferredSize(new Dimension(width, height));
		setBounds(centerX - width / 2, 0, this.width, this.height);
		mainPanel = new JPanel() {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				g.drawImage(image, 0, 0, width, height, null);
				if(drawAxes) {
					g.setColor(Color.RED);
					g.drawLine(0, height / 2, width, height / 2);
					g.drawLine(width / 2, 0, width / 2, height);
				}
			}
		};
		
		mainPanel.setBounds(0, 0, width, height);
		
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB); //TYPE_INT_RGB: 4 bytes per pixel and no alpha channel
		infoBar = new InfoBar();
	
		add(mainPanel);
		add(infoBar, 0);
	}
	
	public void setPixel(int x, int y, int rgb) {
		image.setRGB(x, y, rgb);
		repaint();
	}
	
	public void toggleDrawAxes() {
		drawAxes = !drawAxes;
		repaint();
	}
	
	public void toggleInfoBar() {
		if(activeInfoBar) 
			remove(infoBar);
		else
			add(infoBar, 0);
		activeInfoBar = !activeInfoBar;
		repaint();
	}
	
	public void updateInfoBar(int t, long mI, int cP, BigDecimal cZ, boolean updateTime) {
		infoBar.update(t, mI, cP, cZ, updateTime);
	}
	
}
