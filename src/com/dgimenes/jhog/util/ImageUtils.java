package com.dgimenes.jhog.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;

public class ImageUtils {
	public static int[] adaptMinAndMaxValuesToGrayScale(double[][] intensityMatrix) {
		int matrixSize = intensityMatrix.length * intensityMatrix[0].length;
		int[] equalizedImage = new int[matrixSize];
		double minMagnitude = 0;
		double maxMagnitude = 0;
		for (int i = 0; i < intensityMatrix.length; i++) {
			for (int j = 0; j < intensityMatrix[0].length; j++) {
				minMagnitude = minMagnitude > intensityMatrix[i][j] ? intensityMatrix[i][j] : minMagnitude;
				maxMagnitude = maxMagnitude > intensityMatrix[i][j] ? maxMagnitude : intensityMatrix[i][j];
			}
		}
		double normalizationRate = (maxMagnitude - minMagnitude) / 255;
		for (int i = 0; i < matrixSize; i++) {
			int x = i % intensityMatrix.length;
			int y = i / intensityMatrix.length;
			equalizedImage[i] = (int) (intensityMatrix[x][y] / normalizationRate);
		}
		return equalizedImage;
	}

	public static int[] getHistogramEqualizeGrayScaleImage(int[] imagePixels) {
		int imageSize = imagePixels.length;
		int[] equalizedImage = new int[imageSize];
		double equalizationAlpha = 255D / (imageSize);
		int sizeOfHistogram = 256;
		int[] intensityHistogram = new int[sizeOfHistogram];
		int[] cumulativeIntensityHistogram = new int[sizeOfHistogram];
		for (int q = 0; q < imageSize; q++) {
			int value = imagePixels[q];
			intensityHistogram[value]++;
		}
		cumulativeIntensityHistogram[0] = (int) (intensityHistogram[0] * equalizationAlpha);
		for (int i = 1; i < sizeOfHistogram; i++) {
			cumulativeIntensityHistogram[i] = (int) (cumulativeIntensityHistogram[i - 1] + (intensityHistogram[i] * equalizationAlpha));
		}
		for (int i = 0; i < imageSize; i++) {
			equalizedImage[i] = (int) (cumulativeIntensityHistogram[(imagePixels[i])]);
		}
		return equalizedImage;
	}

	public static Image getBufferedImageFrom3bytePixelArray(int[] rgb, int width, int height) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		image.setRGB(0, 0, width, height, rgb, 0, width);
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				int[] c;
				c = image.getRaster().getPixel(i, j, new int[4]);
				if (c[0] != c[1] || c[0] != c[2] || c[1] != c[2]) {
					System.out.println(c[0] + " " + c[1] + " " + c[2]);
					System.out.println(rgb[i + (i * j)]);
				}
			}
		}
		return image;
	}
}
