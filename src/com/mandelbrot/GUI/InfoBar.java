package com.mandelbrot.GUI;

import java.awt.Color;
import java.awt.Font;
import java.math.BigDecimal;

import javax.swing.JTextPane;

@SuppressWarnings("serial")
public class InfoBar extends JTextPane {
	private int threads;
	private long maxIterations;
	private int colorPalette;
	private BigDecimal currentZoom;
	private String drawTime;
	private String text;
	private static long tempStartTime;
	private static long tempEndTime;
	
	public InfoBar() {
		text = "Threads: " + threads + " Max Interations: " + maxIterations + 
				" Color Palette: " + colorPalette + " Current Zoom: " + currentZoom + "x";
		setBounds(0,0, 2000, 80);
		setText(text);
		setOpaque(false);
		setForeground(new Color(0, 230, 0));
		setFont(new Font("Sans-serif", Font.PLAIN, 32));
		setEditable(false);
		setFocusable(false);
	}
	
	public void update(int t, long mI, int cP, BigDecimal cZ, boolean updateTime) {
		if(updateTime)
			drawTime = formatNanosecondsToTimeString(tempEndTime - tempStartTime);
		else
			drawTime = formatNanosecondsToTimeString(0);
		threads = t;
		maxIterations = mI;
		colorPalette = cP;
		currentZoom = cZ;
		text = "Threads: " + t + " Max Interations: " + mI + " Color Palette: " + cP + " Current Zoom: " + cZ + " Draw Time: " + drawTime;
		setText(text);
	}
	
	public static void startTimer() {
		tempStartTime = System.nanoTime();
	}
	
	public static void endTimer() {
		tempEndTime = System.nanoTime();
	}
	
	private String formatNanosecondsToTimeString(long nanoseconds) {
		int seconds = 0, minutes = 0, hours = 0, milliseconds = (int)(nanoseconds / 1000000);
		nanoseconds %= 1000000;
		if(milliseconds != 0)
			 seconds = milliseconds / 1000;
		milliseconds %= 1000;
		if(seconds != 0)
			minutes = seconds / 60;
		seconds %= 60;
		if(minutes != 0)
			hours = minutes / 60;
		minutes %= 60;
		
		return hours + ":" + minutes + ":" + seconds + "." + milliseconds + "." + (int)nanoseconds;
	}
}
