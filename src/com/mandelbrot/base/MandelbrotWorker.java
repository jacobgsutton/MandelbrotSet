package com.mandelbrot.base;

public class MandelbrotWorker implements Runnable {
	private final int STARTING_ROW;
	private final int ROWS;
	private final int WIDTH;
	private final Mandelbrot M;
	
	public MandelbrotWorker(int startingRow, int width, int rows, Mandelbrot m) {
		STARTING_ROW = startingRow;
		WIDTH = width;
		ROWS = rows;
		M = m;
	}
	
	@Override
	public void run() {
		for(int y = STARTING_ROW; y < STARTING_ROW + ROWS; y++)
			for(int x = 0; x < WIDTH; x++) 
				M.plot(x, y);
	}
}
