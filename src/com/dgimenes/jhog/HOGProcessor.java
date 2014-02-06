package com.dgimenes.jhog;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.List;

import com.dgimenes.jhog.exception.ImageNotProcessedException;

public class HOGProcessor {
	private boolean processed;
	private List<Double> descriptors;
	private BufferedImage image;
	private double[][] pixelLuminMatrix;
	private double[][] gradientsOrientation;
	private double[][] gradientsMagnitute;
	private int[][][] histograms;
	private int cellHeight;
	private int cellWidth;

	public HOGProcessor(BufferedImage image) {
		this.image = image;
		this.processed = false;
		this.descriptors = new ArrayList<Double>();
		this.cellHeight = image.getWidth()/1;
		this.cellWidth = 16;
	}

	public void processImage() {
		// get pixels luminosity matrix
		this.createPixelLuminosityMatrix();

		// calculate gradient strenght for each pixel
		this.calculateGradientStrenghts();

		// create histograms for each cell
		this.createHistograms();

		// create blocks and normalize histograms

		// create descriptors

		this.processed = true;
	}

	private void calculateGradientStrenghts() {
		gradientsOrientation = new double[this.image.getWidth() - 2][this.image.getHeight() - 2];
		gradientsMagnitute = new double[this.image.getWidth() - 2][this.image.getHeight() - 2];
		// do not consider 1 pixel from image border to facilitate
		for (int i = 1; i < pixelLuminMatrix.length - 1; i++) {
			for (int j = 1; j < pixelLuminMatrix[0].length - 1; j++) {
				double dx = pixelLuminMatrix[i + 1][j] - pixelLuminMatrix[i + 1][j];
				double dy = pixelLuminMatrix[i][j - 1] - pixelLuminMatrix[i][j + 1];
				gradientsMagnitute[i-1][j-1] = Math.sqrt((dx * dx) + (dy * dy));
				if (dx == 0) {
					dx = 0.1;
				}
				gradientsOrientation[i-1][j-1] = Math.toDegrees(Math.atan(dy/dx));
			}
		}
	}

	private void createHistograms() {
		this.histograms = new int[this.image.getWidth() / this.cellWidth][this.image.getHeight() / this.cellHeight][9];
		// TODO Auto-generated method stub
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
		return (0.2126 * r) + (0.7152 * g) + (0.0722 * b);
	}

	public List<Double> getHOGDescriptors() {
		if (!processed) {
			throw new ImageNotProcessedException();
		}
		return descriptors;
	}

	public BufferedImage getOriginalImage() {
		return this.image;
	}

	public Image getLuminosityImage() {
		if (!processed) {
			throw new ImageNotProcessedException();
		}
		byte[] buffer = new byte[this.image.getWidth() * this.image.getHeight()];
		int x = 0;
		int y = 0;
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = (byte) this.pixelLuminMatrix[x][y];
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
		BufferedImage image = new BufferedImage(this.image.getWidth(), this.image.getHeight(), BufferedImage.TYPE_INT_RGB);
		image.setRGB(0, 0, this.image.getWidth(), this.image.getHeight(), rgb, 0, this.image.getWidth());
		return image;
	}
	
	public Image getGradientBordersImage() {
		if (!processed) {
			throw new ImageNotProcessedException();
		}
		byte[] buffer = new byte[this.image.getWidth() * this.image.getHeight()];
		int x = 0;
		int y = 0;
		for (int i = 0; i < buffer.length; i++) {
			buffer[i] = (byte) this.pixelLuminMatrix[x][y];
			x++;
			if (x == this.image.getWidth()) {
				x = 0;
				y++;
			}
		}
		double threshold = 13;
		int[] rgb = new int[buffer.length];
		for (int i = 0; i < rgb.length; ++i) {
			x = i % this.image.getWidth();
			y = i / this.image.getWidth();
			if (x != 0 && x != this.image.getWidth() - 1 && y != 0 && y != this.image.getHeight() - 1 && (gradientsMagnitute[x-1][y-1]) > threshold) {
				System.out.println(gradientsMagnitute[x-1][y-1] + " ");
				rgb[i] = 0xFF0000;
			} else {
				rgb[i] = ((buffer[i] << 16) | (buffer[i] << 8) | buffer[i]);
			}
		}
		BufferedImage image = new BufferedImage(this.image.getWidth(), this.image.getHeight(), BufferedImage.TYPE_INT_RGB);
		image.setRGB(0, 0, this.image.getWidth(), this.image.getHeight(), rgb, 0, this.image.getWidth());
		return image;
	}

}
