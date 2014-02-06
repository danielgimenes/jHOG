package com.dgimenes.jhog;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

import com.dgimenes.jhog.entity.Gradient;
import com.dgimenes.jhog.entity.GradientCell;
import com.dgimenes.jhog.util.ImageUtils;

public class HOGProcessor {
	private static final int NUM_OF_CELLS_SQRT = 10;
	private static final int NUM_OF_CELLS = NUM_OF_CELLS_SQRT * NUM_OF_CELLS_SQRT;
	private boolean processed;
	private List<Double> descriptors;
	private BufferedImage image;
	private double[][] pixelLuminMatrix;
	private double[][] gradientsOrientation;
	private double[][] gradientsMagnitute;
	private GradientCell[] cells;
	private int cellHeight;
	private int cellWidth;
	private int[][] histograms;

	public HOGProcessor(BufferedImage image) {
		this.image = image;
		this.processed = false;
		this.descriptors = new ArrayList<Double>();
		this.cellHeight = (image.getHeight() - 2) / NUM_OF_CELLS_SQRT;
		this.cellWidth = (image.getWidth() - 2) / NUM_OF_CELLS_SQRT;
	}

	public void processImage() {
		// get pixels luminosity matrix
		this.createPixelLuminosityMatrix();

		// calculate gradients for each pixel
		this.calculateGradients();

		// create histograms for each cell
		this.createCellsAndHistograms();

		// create blocks and normalize histograms

		// create descriptors

		this.processed = true;
	}

	private void calculateGradients() {
		gradientsOrientation = new double[this.image.getWidth() - 2][this.image.getHeight() - 2];
		gradientsMagnitute = new double[this.image.getWidth() - 2][this.image.getHeight() - 2];
		// do not consider 1 pixel from image border to facilitate
		for (int i = 1; i < pixelLuminMatrix.length - 1; i++) {
			for (int j = 1; j < pixelLuminMatrix[0].length - 1; j++) {
				double dx = pixelLuminMatrix[i + 1][j] - pixelLuminMatrix[i - 1][j];
				double dy = pixelLuminMatrix[i][j - 1] - pixelLuminMatrix[i][j + 1];
				gradientsMagnitute[i - 1][j - 1] = Math.sqrt((dx * dx) + (dy * dy));
				if (dx == 0) {
					dx = 0.1;
				}
				gradientsOrientation[i - 1][j - 1] = Math.toDegrees(Math.atan(dy / dx));
			}
		}
	}

	private void createCellsAndHistograms() {
		this.cells = new GradientCell[NUM_OF_CELLS];
		for (int i = 0; i < NUM_OF_CELLS; i++) {
			this.cells[i] = new GradientCell(cellWidth * cellHeight);
			for (int j = 0; j < cellWidth * cellHeight; j++) {
				int offsetX = (j % cellWidth) + (i % NUM_OF_CELLS_SQRT);
				int offsetY = (j / cellWidth) + (i / NUM_OF_CELLS_SQRT);
				this.cells[i].getGradients()[j] = new Gradient(this.gradientsOrientation[offsetX][offsetY], this.gradientsMagnitute[offsetX][offsetY]);
			}
		}

		// VALIDATION
		// System.out.println(Math.abs(this.gradientsOrientation[0][0]) -
		// Math.abs(this.cells[0].getGradients()[0].getOrientation()) <
		// 0.000001);
		// System.out.println(Math.abs(this.gradientsMagnitute[0][0]) -
		// Math.abs(this.cells[0].getGradients()[0].getMagnitude()) < 0.000001);
		// System.out.println(Math.abs(this.gradientsOrientation[1][0]) -
		// Math.abs(this.cells[0].getGradients()[1].getOrientation()) <
		// 0.000001);
		// System.out.println(Math.abs(this.gradientsMagnitute[1][0]) -
		// Math.abs(this.cells[0].getGradients()[1].getMagnitude()) < 0.000001);
		// System.out.println(Math.abs(this.gradientsOrientation[0][1]) -
		// Math.abs(this.cells[NUM_OF_CELLS_SQRT].getGradients()[0].getOrientation())
		// < 0.000001);
		// System.out.println(Math.abs(this.gradientsMagnitute[0][1]) -
		// Math.abs(this.cells[NUM_OF_CELLS_SQRT].getGradients()[0].getMagnitude())
		// < 0.000001);

		this.histograms = new int[NUM_OF_CELLS][9];
		for (int i = 0; i < NUM_OF_CELLS; i++) {
			for (int j = 0; j < cellWidth * cellHeight; j++) {
				Gradient grad = this.cells[i].getGradients()[j];
				int histogramValueCategory = ((int) grad.getOrientation() + 90) / 20;
				int histogramValueWeight = (int) grad.getMagnitude();
				this.histograms[i][histogramValueCategory] += histogramValueWeight;
			}
//			System.out.println("HISTOGRAMA DA CÉLULA[" + i + "]:");
//			for (int z = 0; z < 9; z++) {
//				System.out.println("\t" + (z*20) + " até " + (z*20+20) + " = " + this.histograms[i][z]);
//			}
		}
	}

	private void createPixelLuminosityMatrix() {
		this.pixelLuminMatrix = new double[this.image.getWidth()][this.image.getHeight()];
		byte[] imagePixelBytes = (((DataBufferByte) image.getRaster().getDataBuffer()).getData());
		int imageWidth = this.image.getWidth();
		boolean hasAlpha = image.getAlphaRaster() != null;
		int x = 0;
		int y = 0;
		if (hasAlpha) {
			for (int i = 0; i < imagePixelBytes.length; i += 4) {
				this.pixelLuminMatrix[x][y] = calculateLuminosity(imagePixelBytes[i + 1], imagePixelBytes[i + 2], imagePixelBytes[i + 3]);
				x++;
				if (x == imageWidth) {
					x = 0;
					y++;
				}

			}
		} else {
			for (int i = 0; i < imagePixelBytes.length; i += 3) {
				this.pixelLuminMatrix[x][y] = calculateLuminosity(imagePixelBytes[i], imagePixelBytes[i + 1], imagePixelBytes[i + 2]);
				x++;
				if (x == imageWidth) {
					x = 0;
					y++;
				}
			}
		}
	}

	private double calculateLuminosity(byte r, byte g, byte b) {
		int red = r & 0xff;
		int green = g & 0xff;
		int blue = b & 0xff;
		return (0.2126 * red) + (0.7152 * green) + (0.0722 * blue);
	}

	public List<Double> getHOGDescriptors() {
		if (!this.processed) {
			this.processImage();
		}
		return descriptors;
	}

	public BufferedImage getOriginalImage() {
		return this.image;
	}

	public Image getLuminosityImage() {
		if (!this.processed) {
			this.processImage();
		}
		int[] buffer = new int[this.image.getWidth() * this.image.getHeight()];
		int x = 0;
		int y = 0;
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = (int) this.pixelLuminMatrix[x][y];
			x++;
			if (x == this.image.getWidth()) {
				x = 0;
				y++;
			}
		}
		int[] rgb = new int[buffer.length];
		for (int i = 0; i < rgb.length; ++i) {
			rgb[i] = ((buffer[i] << 16) | (buffer[i] << 8) | buffer[i]);
		}
		return ImageUtils.getBufferedImageFrom3bytePixelArray(rgb, this.image.getWidth(), this.image.getHeight());
	}

	public Image getLuminosityImageHistogramEqualized() {
		if (!this.processed) {
			this.processImage();
		}
		int[] buffer = new int[this.image.getWidth() * this.image.getHeight()];
		int x = 0;
		int y = 0;
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = (int) this.pixelLuminMatrix[x][y];
			x++;
			if (x == this.image.getWidth()) {
				x = 0;
				y++;
			}
		}
		buffer = ImageUtils.getHistogramEqualizeGrayScaleImage(buffer);
		int[] rgb = new int[buffer.length];
		for (int i = 0; i < rgb.length; ++i) {
			rgb[i] = ((buffer[i] << 16) | (buffer[i] << 8) | buffer[i]);
		}
		return ImageUtils.getBufferedImageFrom3bytePixelArray(rgb, this.image.getWidth(), this.image.getHeight());
	}

	public Image getGradientMagnitudeImage() {
		if (!this.processed) {
			this.processImage();
		}
		// min-max equalization
		double minMagnitude = 0;
		double maxMagnitude = 0;
		for (int i = 0; i < gradientsMagnitute.length; i++) {
			for (int j = 0; j < gradientsMagnitute[0].length; j++) {
				minMagnitude = minMagnitude > gradientsMagnitute[i][j] ? gradientsMagnitute[i][j] : minMagnitude;
				maxMagnitude = maxMagnitude > gradientsMagnitute[i][j] ? maxMagnitude : gradientsMagnitute[i][j];
			}
		}
		double normalizationRate = (maxMagnitude - minMagnitude) / 255;
		int[] rgb = new int[this.image.getWidth() * this.image.getHeight()];
		for (int i = 0; i < rgb.length; ++i) {
			int x = i % this.image.getWidth();
			int y = i / this.image.getWidth();
			if (x != 0 && x != this.image.getWidth() - 1 && y != 0 && y != this.image.getHeight() - 1) {
				int intensity = (int) (gradientsMagnitute[x - 1][y - 1] / normalizationRate);
				rgb[i] = ((intensity << 16) | (intensity << 8) | intensity);
			} else {
				rgb[i] = 0x00;
			}
		}
		return ImageUtils.getBufferedImageFrom3bytePixelArray(rgb, this.image.getWidth(), this.image.getHeight());
	}

	public Image getLuminosityImageWithCells() {
		if (!this.processed) {
			this.processImage();
		}
		int[] buffer = new int[this.image.getWidth() * this.image.getHeight()];
		int x = 0;
		int y = 0;
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = (int) this.pixelLuminMatrix[x][y];
			x++;
			if (x == this.image.getWidth()) {
				x = 0;
				y++;
			}
		}
		int[] rgb = new int[buffer.length];
		for (int i = 0; i < rgb.length; ++i) {
			rgb[i] = ((buffer[i] << 16) | (buffer[i] << 8) | buffer[i]);
		}
		Image image = ImageUtils.getBufferedImageFrom3bytePixelArray(rgb, this.image.getWidth(), this.image.getHeight());
		Graphics graphics = image.getGraphics();
		graphics.setColor(Color.GREEN);
		for (int i = 0; i < NUM_OF_CELLS; i++) {
			x = (i % NUM_OF_CELLS_SQRT) * cellWidth;
			y = (i / NUM_OF_CELLS_SQRT) * cellHeight;
			graphics.drawRect(x, y, cellWidth, cellHeight);
			graphics.drawString(""+i, x+2, y+13);
		}
		return image;
	}
}
