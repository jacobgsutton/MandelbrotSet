package com.mandelbrot.base;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.mandelbrot.GUI.InfoBar;
import com.mandelbrot.GUI.MainComponent;

public class Mandelbrot implements Runnable {
	private final MathContext MC;
	private final BigDecimal ZOOM;
	private BigDecimal scale;
	private final BigDecimal SCALE_CAP;
	private int mX;
	private int mY;
	private BigDecimal beforeZoomWorldX;
	private BigDecimal beforeZoomWorldY;
	private BigDecimal afterZoomWorldX;
	private BigDecimal afterZoomWorldY;
	private BigDecimal offsetX;
	private BigDecimal offsetY;
	private final BigDecimal RE_START;
	private final BigDecimal RE_END;
	private final BigDecimal IM_START;
	private final BigDecimal IM_END;
	private final String COLORS_FILE = System.getProperty("user.dir") + "\\src\\resources\\Colors.txt";
	private long maxIterations;
	private int currentWorkerThreads;
	private final MainComponent CANVAS;
	private final int WIDTH;
	private final int HEIGHT;
	private final BigDecimal BD_WIDTH;
	private final BigDecimal BD_HEIGHT;
	private final int CENTER_X;
	private final int CENTER_Y;
	private final BigDecimal BD_CENTER_X;
	private final BigDecimal BD_CENTER_Y;
	private final int COLOR_FILE_LENGTH;
	private int colors;
	private final int PALETTE_1[];
	private final int PALETTE_2[];
	private final int PALETTE_3[];
	private final int PALETTES[][];
	private int currentPalette[];
	private int currentPaletteN;
	
	public Mandelbrot(MainComponent mc, int width, int height) {
		MC = new MathContext(100, RoundingMode.HALF_UP);
		CANVAS = mc;
		WIDTH = width;
		HEIGHT = height;
		BD_WIDTH = new BigDecimal(width);
		BD_HEIGHT = new BigDecimal(height);
		CENTER_X = width / 2;
		CENTER_Y = height /2;
		BD_CENTER_X = new BigDecimal(CENTER_X);
		BD_CENTER_Y = new BigDecimal(CENTER_Y);
		
		maxIterations = 500;
		currentWorkerThreads = 270;
		
		ZOOM = new BigDecimal(2);
		SCALE_CAP = new BigDecimal(1e13);
		scale = BigDecimal.ONE;
		offsetX = BigDecimal.ZERO;
		offsetY = BigDecimal.ZERO;
		
		mX = CENTER_X;
		mY = CENTER_Y;
		
		RE_START = new BigDecimal(-2);
		RE_END = new BigDecimal(1);
		IM_START = new BigDecimal(-1);
		IM_END = new BigDecimal(1);
		
		COLOR_FILE_LENGTH = 554;
		PALETTE_1 = new int[] { 
			0x421e0f, 0x19071a, 0x09012f, 
			0x040449, 0x000764, 0x0c2c8a, 
			0x1852b1, 0x397DD1, 0x86b5e5, 
			0xd3ecf8, 0xf1e9bf, 0xf8c95f, 
			0xffaa00, 0xcc8000, 0x995700, 
			0x6a3403, 
		};
		PALETTE_2 = loadColors(COLOR_FILE_LENGTH);
		PALETTE_3 = new int[] {
		    0x000000, 0x001000, 0x011800, 0x012000,
		    0x022800, 0x023000, 0x033900, 0x034100,
		    0x044900, 0x045100, 0x055900, 0x056100,
		    0x066a00, 0x067200, 0x067a00, 0x078200,
		    0x078a00, 0x089200, 0x089b00, 0x09a300,
		    0x09ab00, 0x0ab300, 0x0abb00, 0x0bc300,
		    0x0bcc00, 0x0cd400, 0x0cdc00, 0x0ce400,
		    0x0dec00, 0x0df400, 0x0efc00, 0x14ff06,
		    0x1bff0e, 0x23ff16, 0x00ff00, 0x2bff1e, 
		    0x33ff26, 0x3aff2e, 0x42ff37, 0x4aff3f, 
		    0x51ff47, 0x59ff4f, 0x61ff57, 0x68ff5f, 
		    0x70ff68, 0x78ff70, 0x7fff78, 0x87ff80, 
		    0x8fff88, 0x97ff90, 0x9eff99, 0xa6ffa1, 
		    0xaeffa9, 0xb5feb1, 0xbdfeb9, 0xc5fec1, 
		    0xccfec9, 0xd4fed2, 0xdcfeda, 0xe4fee2, 
		    0xebffea, 0xf3fff2, 0xffffff,
		};
		
		PALETTES = new int[][] {
			PALETTE_1,
			PALETTE_2,
			PALETTE_3
		};
		currentPalette = PALETTE_1;
		currentPaletteN = 0;
		colors = currentPalette.length;
	}
	
	public void setCenter(int mX, int mY) {
		this.mX =  mX;
		this.mY =  mY;
		offsetX = offsetX.subtract(BD_CENTER_X.subtract(new BigDecimal(this.mX)).divide(scale, MC));
		offsetY = offsetY.subtract(BD_CENTER_Y.subtract(new BigDecimal(this.mY)).divide(scale, MC));
	}
	
	public void zoom(boolean in) {
		beforeZoomWorldX = screenToWorldX(CENTER_X);
		beforeZoomWorldY = screenToWorldY(CENTER_Y);
		
		if(in) 
			scale = scale.multiply(ZOOM);
		else
			scale = scale.divide(ZOOM, MC);
		
		afterZoomWorldX = screenToWorldX(CENTER_X);
		afterZoomWorldY = screenToWorldY(CENTER_Y);
		offsetX = offsetX.add(beforeZoomWorldX.subtract(afterZoomWorldX));
		offsetY = offsetY.add(beforeZoomWorldY.subtract(afterZoomWorldY));
	}
	
	public void incThreads() {
		currentWorkerThreads *= 2;
		updateInfoBar(false);
	}
	
	public void decThreads() {
		if(currentWorkerThreads > 1)
			currentWorkerThreads /= 2;
		updateInfoBar(false);
	}
	
	public void incMaxIterations() {
		maxIterations *= 2;
		updateInfoBar(false);
	}
	
	public void decMaxIterations() {
		if(maxIterations > 1) {
			maxIterations /= 2;
			updateInfoBar(false);
		}
	}
	
	public void nextPalette() {
		if(++currentPaletteN == PALETTES.length)
			currentPaletteN = 0;
		currentPalette = PALETTES[currentPaletteN];
		colors = currentPalette.length;
	}
	
	public void updateInfoBar(boolean updateTime) {
		CANVAS.updateInfoBar(currentWorkerThreads, maxIterations, currentPaletteN + 1, scale, updateTime);
	}

	private int[] loadColors(int amount) {
		int list[] = new int[amount];
		String currLine = null;
		int i = 0;
		try(BufferedReader br = new BufferedReader(new FileReader(COLORS_FILE));) {
			while(i < list.length && (currLine = br.readLine()) != null)  
				list[i++] = Integer.parseInt(currLine.substring(2), 16);
		} catch(FileNotFoundException e) {
			System.out.println("File " + COLORS_FILE + " can not be found and/or opened.");
			e.printStackTrace();
			System.out.println("Exiting...");
			System.exit(-1);
		} catch(IOException e) {
			System.out.println("Failed to close the file.");
			e.printStackTrace();
			System.out.println("Exiting...");
			System.exit(-1);
		}
		return list;
	}
	
	private BigDecimal screenToWorldX(int x) {
		return new BigDecimal(x).divide(scale, MC).add(offsetX);
	}
	
	private BigDecimal screenToWorldY(int y) {
		return new BigDecimal(y).divide(scale, MC).add(offsetY);
	}
	
	private BigDecimal relX(BigDecimal Px) {
		return Px.divide(BD_WIDTH, MC).multiply(RE_END.subtract(RE_START)).add(RE_START);
	}
	
	private BigDecimal relY(BigDecimal Py) {
		return Py.divide(BD_HEIGHT, MC).multiply(IM_END.subtract(IM_START)).add(IM_START);
	}

	protected void plot(int Px, int Py) {
		final BigDecimal bd_scaledX = relX(screenToWorldX(Px)),
					     bd_scaledY = relY(screenToWorldY(Py));
		BigDecimal bd_x    = BigDecimal.ZERO,
				   bd_y    = BigDecimal.ZERO,
				   bd_x2   = BigDecimal.ZERO, //x squared
				   bd_y2   = BigDecimal.ZERO, //y squared
				   bd_four = new BigDecimal(4),
				   bd_two  = new BigDecimal(2);
		
		double scaledX = bd_scaledX.doubleValue(),
			   scaledY = bd_scaledY.doubleValue(),
			   x 	   = 0.0,
			   y 	   = 0.0,
			   x2	   = 0.0,
			   y2 	   = 0.0;
		
		int iteration = 0;
		if(scale.compareTo(SCALE_CAP) <= 0)
			while(x2 + y2 <= 4 && iteration < maxIterations) {
				y = 2 * x * y + scaledY;
				x = x2 - y2 + scaledX;
				x2 = x * x;
				y2 = y * y;
				iteration++;
			}
		else 
			while(bd_x2.add(bd_y2).round(MC).compareTo(bd_four) <= 0 && iteration < maxIterations) {
				bd_y = bd_two.multiply(bd_x).multiply(bd_y).add(bd_scaledY).round(MC); //2 * x * y + scaledY
				bd_x = bd_x2.subtract(bd_y2).add(bd_scaledX).round(MC); //x2 - y2 + scaledX
				bd_x2 = bd_x.pow(2).round(MC); //x * x
				bd_y2 = bd_y.pow(2).round(MC); //y * y
				iteration++;
			}
		
        CANVAS.setPixel(Px, Py, iteration == maxIterations ? 0x0 : currentPalette[iteration % colors]);
	}

	@Override
	public void run() {
		updateInfoBar(false);
		final ExecutorService ES = Executors.newCachedThreadPool();
		final int WORKERS = currentWorkerThreads;
		final int ROWS = HEIGHT / WORKERS;
		final int LEFT_OVER_ROWS = HEIGHT % WORKERS;
		InfoBar.startTimer();
		if(LEFT_OVER_ROWS != 0)
			ES.execute(new MandelbrotWorker(ROWS * (WORKERS - 1), WIDTH, ROWS + LEFT_OVER_ROWS, this));
		for(int i = 0; i < WORKERS; i++) 
			ES.execute(new MandelbrotWorker(ROWS * i, WIDTH, ROWS, this));
		ES.shutdown();
		try {
			if(ES.awaitTermination(10000, TimeUnit.MINUTES)) {
				InfoBar.endTimer();
				updateInfoBar(true);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(scale + " " + offsetX + " " + offsetY);
	}
}
