/*
 * The MIT License (MIT)
 * Copyright (c) 2014 Daniel Costa Gimenes
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this 
 * software and associated documentation files (the "Software"), to deal in the Software 
 * without restriction, including without limitation the rights to use, copy, modify, 
 * merge, publish, distribute, sublicense, and/or sell copies of the Software, and to 
 * permit persons to whom the Software is furnished to do so, subject to the following 
 * conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER 
 * DEALINGS IN THE SOFTWARE.
 */
package com.dgimenes.jhog;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

import com.dgimenes.jhog.entity.Gradient;
import com.dgimenes.jhog.entity.GradientCell;
import com.dgimenes.jhog.util.HexPrintUtil;
import com.dgimenes.jhog.util.ImageProcessingUtils;

public class HOGProcessor {
	private static final int NUM_OF_CELLS_SQRT = 10;
	private static final int NUM_OF_CELLS = NUM_OF_CELLS_SQRT * NUM_OF_CELLS_SQRT;
	private boolean processed;
	private List<Double> descriptor;
	private BufferedImage image;
	private double[][] pixelLuminMatrix;
	private GradientCell[] cells;
	private Gradient[] gradients;
	private int cellHeight;
	private int cellWidth;
	private double[][] histograms;
	private int verticalDiscardBorderSize;
	private int horizontalDiscardBorderSize;

	public HOGProcessor(BufferedImage image) {
		this.image = image;
		this.processed = false;
		this.descriptor = new ArrayList<Double>();
		this.cellHeight = (image.getHeight() - 2) / NUM_OF_CELLS_SQRT;
		this.verticalDiscardBorderSize = (image.getHeight() - 2) % NUM_OF_CELLS_SQRT;
		this.cellWidth = (image.getWidth() - 2) / NUM_OF_CELLS_SQRT;
		this.horizontalDiscardBorderSize = (image.getWidth() - 2) % NUM_OF_CELLS_SQRT;
	}

	public void processImage() {
		// get pixels luminosity matrix
		this.createPixelLuminosityMatrix();

		// calculate gradients for each pixel
		this.calculateGradientsAndCells();

		// create histograms for each cell
		this.createHistograms();

		// normalize histograms
		this.globalNormalization();
		// this.localBlockBasedNormalization();

		// create descriptor
		this.createDescriptors();

		// mark as processed
		this.processed = true;
	}

	private void createDescriptors() {
		this.descriptor = new ArrayList<Double>(NUM_OF_CELLS * 9);
		for (int i = 0; i < NUM_OF_CELLS; i++) {
			for (int z = 0; z < 9; z++) {
				this.descriptor.add(this.histograms[i][z]);
			}
		}
	}

	private void localBlockBasedNormalization() {
		// TODO Auto-generated method stub
	}

	private void globalNormalization() {
		double maxGradMagnitude = 0.0;
		double minGradMagnitude = Double.MAX_VALUE;
		double magnitude = 0.0;
		for (int i = 0; i < NUM_OF_CELLS; i++) {
			for (int z = 0; z < 9; z++) {
				magnitude = this.histograms[i][z];
				maxGradMagnitude = Math.max(magnitude, maxGradMagnitude);
				minGradMagnitude = Math.min(magnitude, minGradMagnitude);
			}
		}
		double normalizationRate = 1.0 / (maxGradMagnitude - minGradMagnitude);
		for (int i = 0; i < NUM_OF_CELLS; i++) {
			for (int z = 0; z < 9; z++) {
				this.histograms[i][z] *= normalizationRate;
			}
		}
	}

	private void calculateGradientsAndCells() {
		this.gradients = new Gradient[NUM_OF_CELLS * cellWidth * cellHeight];
		int x = 1; // discard 1 column of pixels
		int y = 1; // discard 1 line of pixels
		for (int i = 0; i < this.gradients.length; i++) {
			double dx = pixelLuminMatrix[x + 1][y] - pixelLuminMatrix[x - 1][y];
			double dy = pixelLuminMatrix[x][y - 1] - pixelLuminMatrix[x][y + 1];
			double magnitude = Math.sqrt((dx * dx) + (dy * dy));
			double orientation = Math.toDegrees(Math.atan(dy / (dx == 0 ? 0.001 : dx)));
			this.gradients[i] = new Gradient(orientation, magnitude);
			if (x == NUM_OF_CELLS_SQRT * cellWidth) {
				x = 1;
				y++;
			} else {
				x++;
			}
		}

		// THIS MUST BE REFACTORED
		this.cells = new GradientCell[NUM_OF_CELLS];
		for (int i = 0; i < NUM_OF_CELLS; i++) {
			this.cells[i] = new GradientCell(cellHeight * cellWidth);
		}
		int cellIndex;
		int lineOfGradInsideCell;
		int globalIndexOfGrad;
		int firstCellIndexInCurrentLine;
		int quantityOfGradientsInLineOfCells = NUM_OF_CELLS_SQRT * cellWidth * cellHeight;
		for (int lineOfCellOffset = 0; lineOfCellOffset < NUM_OF_CELLS_SQRT; lineOfCellOffset++) {
			lineOfGradInsideCell = 0;
			firstCellIndexInCurrentLine = (lineOfCellOffset * NUM_OF_CELLS_SQRT);
			for (int localIndexOfGrad = 0; localIndexOfGrad < quantityOfGradientsInLineOfCells; localIndexOfGrad++) {
				cellIndex = ((localIndexOfGrad - (lineOfGradInsideCell * NUM_OF_CELLS_SQRT * cellWidth)) / this.cellWidth)
						+ (lineOfCellOffset * NUM_OF_CELLS_SQRT);
				if (cellIndex == ((lineOfCellOffset + 1) * NUM_OF_CELLS_SQRT)) {
					lineOfGradInsideCell++;
					cellIndex = firstCellIndexInCurrentLine;
				}
				globalIndexOfGrad = localIndexOfGrad
						+ (lineOfCellOffset * quantityOfGradientsInLineOfCells);
				this.cells[cellIndex].getGradients().add(this.gradients[globalIndexOfGrad]);
			}
		}
	}

	private void createHistograms() {
		this.histograms = new double[NUM_OF_CELLS][9];
		for (int cellIndex = 0; cellIndex < NUM_OF_CELLS; cellIndex++) {
			for (int z = 0; z < 9; z++) {
				this.histograms[cellIndex][z] = 0.0;
			}
			for (Gradient grad : this.cells[cellIndex].getGradients()) {
				int histogramValueCategory = ((int) grad.getOrientation() + 90) / 20;
				int histogramValueWeight = (int) Math.round(grad.getMagnitude());
				this.histograms[cellIndex][histogramValueCategory] += histogramValueWeight;
			}
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
				this.pixelLuminMatrix[x][y] = desaturatePixel(imagePixelBytes[i + 1],
						imagePixelBytes[i + 2], imagePixelBytes[i + 3]);
				x++;
				if (x == imageWidth) {
					x = 0;
					y++;
				}

			}
		} else {
			for (int i = 0; i < imagePixelBytes.length; i += 3) {
				this.pixelLuminMatrix[x][y] = desaturatePixel(imagePixelBytes[i],
						imagePixelBytes[i + 1], imagePixelBytes[i + 2]);
				x++;
				if (x == imageWidth) {
					x = 0;
					y++;
				}
			}
		}
	}

	private double desaturatePixel(byte r, byte g, byte b) {
		int red = r & 0xff;
		int green = g & 0xff;
		int blue = b & 0xff;

		// method 1 (Luma)
		return (0.2126 * red) + (0.7152 * green) + (0.0722 * blue);

		// method 2
		// return (0.3 * red) + (0.59 * green) + (0.11 * blue);

		// method 3 (BT.601)
		// return (0.299 * red) + (0.587 * green) + (0.114 * blue);

		// method 4 (GIMP Luminosity)
		// return (0.2126 * red) + (0.7152 * green) + (0.0722 * blue) + 0.5;

		// method 5 (GIMP Lightness)
		// int max = Math.max(red, green);
		// max = Math.max(max, blue);
		// int min = Math.min(red, green);
		// min = Math.min(min, blue);
		// return (max + min) / 2;

		// method 6 (GIMP Average)
		// return (red + green + blue) / 3;
	}

	public List<Double> getHOGDescriptors() {
		if (!this.processed) {
			this.processImage();
		}
		return descriptor;
	}

	public BufferedImage getOriginalImage() {
		return this.image;
	}

	private int[] matrixToArray(double[][] matrix) {
		int[] array = new int[matrix.length * matrix[0].length];
		int x = 0;
		int y = 0;
		for (int i = 0; i < array.length; i++) {
			array[i] = (int) matrix[x][y];
			x++;
			if (x == matrix.length) {
				x = 0;
				y++;
			}
		}
		return array;
	}

	public BufferedImage getLuminosityImage() {
		if (!this.processed) {
			this.processImage();
		}
		int[] buffer = this.matrixToArray(this.pixelLuminMatrix);
		int[] rgb = new int[buffer.length];
		for (int i = 0; i < rgb.length; ++i) {
			rgb[i] = ((buffer[i] << 16) | (buffer[i] << 8) | buffer[i]);
		}
		return ImageProcessingUtils.getBufferedImageFrom3bytePixelArray(rgb, this.image.getWidth(),
				this.image.getHeight());
	}

	public BufferedImage getLuminosityImageMinMaxEqualized() {
		if (!this.processed) {
			this.processImage();
		}
		int[] buffer = ImageProcessingUtils.adaptMinAndMaxValuesToGrayScale(this.pixelLuminMatrix);
		int[] rgb = new int[buffer.length];
		for (int i = 0; i < rgb.length; ++i) {
			rgb[i] = ((buffer[i] << 16) | (buffer[i] << 8) | buffer[i]);
		}
		return ImageProcessingUtils.getBufferedImageFrom3bytePixelArray(rgb, this.image.getWidth(),
				this.image.getHeight());
	}

	public BufferedImage getLuminosityImageHistogramEqualized() {
		if (!this.processed) {
			this.processImage();
		}
		int[] buffer = this.matrixToArray(this.pixelLuminMatrix);
		buffer = ImageProcessingUtils.getHistogramEqualizedGrayScaleImage(buffer);
		int[] rgb = new int[buffer.length];
		for (int i = 0; i < rgb.length; ++i) {
			rgb[i] = ((buffer[i] << 16) | (buffer[i] << 8) | buffer[i]);
		}
		return ImageProcessingUtils.getBufferedImageFrom3bytePixelArray(rgb, this.image.getWidth(),
				this.image.getHeight());
	}

	public BufferedImage getGradientMagnitudeImage() {
		if (!this.processed) {
			this.processImage();
		}
		// min-max equalization
		double minMagnitude = 0;
		double maxMagnitude = 0;
		for (Gradient grad : this.gradients) {
			minMagnitude = Math.min(minMagnitude, grad.getMagnitude());
			maxMagnitude = Math.max(maxMagnitude, grad.getMagnitude());
		}
		double normalizationRate = (maxMagnitude - minMagnitude) / 255;
		int[] rgb = new int[this.gradients.length];
		int i = 0;
		for (Gradient grad : this.gradients) {
			int intensity = (int) (grad.getMagnitude() / normalizationRate);
			rgb[i++] = ((intensity << 16) | (intensity << 8) | intensity);
		}
		return ImageProcessingUtils.getBufferedImageFrom3bytePixelArray(rgb, NUM_OF_CELLS_SQRT
				* cellWidth, NUM_OF_CELLS_SQRT * cellHeight);
	}

	public BufferedImage getLuminosityImageWithCells(boolean drawCellIndex) {
		if (!this.processed) {
			this.processImage();
		}
		int[] buffer = this.matrixToArray(this.pixelLuminMatrix);
		int[] rgb = new int[buffer.length];
		for (int i = 0; i < rgb.length; ++i) {
			rgb[i] = ((buffer[i] << 16) | (buffer[i] << 8) | buffer[i]);
		}
		BufferedImage image = ImageProcessingUtils.getBufferedImageFrom3bytePixelArray(rgb,
				this.image.getWidth(), this.image.getHeight());
		Graphics graphics = image.getGraphics();
		graphics.setColor(Color.GREEN);
		int x = 0;
		int y = 0;
		for (int i = 0; i < NUM_OF_CELLS; i++) {
			x = (i % NUM_OF_CELLS_SQRT) * cellWidth;
			y = (i / NUM_OF_CELLS_SQRT) * cellHeight;
			graphics.drawRect(x, y, cellWidth, cellHeight);
			if (drawCellIndex) {
				graphics.drawString("" + i, x + 2, y + 13);
			}
		}
		return image;
	}

	public BufferedImage getHOGDescriptorsRepresentation() {
		BufferedImage image = this.getLuminosityImageWithCells(false);
		Graphics graphics = image.getGraphics();
		graphics.setColor(Color.RED);
		double x1;
		double y1;
		double x2;
		double y2;
		int orientation;
		double magnitude;
		int xOffset;
		int yOffset;
		double scale = Math.min(this.cellWidth, this.cellHeight) / 1.2;
		for (int cellIndex = 0; cellIndex < NUM_OF_CELLS; cellIndex++) {
			for (int i = 0; i < 9; i++) {
				orientation = 20 * i;
				magnitude = (double) (this.histograms[cellIndex][i] * scale);
				if (magnitude > scale) {
					System.out.println();
				}
				x1 = (int) (Math.sin(Math.toRadians(orientation)) * magnitude);
				y1 = (int) (Math.cos(Math.toRadians(orientation)) * magnitude);
				x2 = x1 * -1;
				y2 = y1 * -1;
				// center
				x1 += this.cellWidth / 2;
				x2 += this.cellWidth / 2;
				y1 += this.cellHeight / 2;
				y2 += this.cellHeight / 2;
				// position on cell
				xOffset = cellWidth * (cellIndex % NUM_OF_CELLS_SQRT);
				x1 += xOffset;
				x2 += xOffset;
				yOffset = cellHeight * (cellIndex / NUM_OF_CELLS_SQRT);
				y1 += yOffset;
				y2 += yOffset;
				graphics.drawLine((int) x1, (int) y1, (int) x2, (int) y2);
			}
		}
		return image;
	}

}
