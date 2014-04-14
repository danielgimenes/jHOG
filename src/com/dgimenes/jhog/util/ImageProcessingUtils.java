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
package com.dgimenes.jhog.util;

import java.awt.image.BufferedImage;

/**
 * Provides transformations for images such as luminosity normalization, and
 * converting image format
 * 
 * @author danielgimenes
 * @version 1.0
 */
public class ImageProcessingUtils {
	/**
	 * Gets maximum and minimum luminosity values in image (from
	 * intensityMatrix) and normalize image in that range
	 * 
	 * @param intensityMatrix
	 * @return equalized image as an array of luminosity intensities
	 */
	public static int[] adaptMinAndMaxValuesToGrayScale(double[][] intensityMatrix) {
		int matrixSize = intensityMatrix.length * intensityMatrix[0].length;
		int[] equalizedImage = new int[matrixSize];
		double minMagnitude = Double.MAX_VALUE;
		double maxMagnitude = 0;
		for (int i = 0; i < intensityMatrix.length; i++) {
			for (int j = 0; j < intensityMatrix[0].length; j++) {
				minMagnitude = Math.min(minMagnitude, intensityMatrix[i][j]);
				maxMagnitude = Math.max(maxMagnitude, intensityMatrix[i][j]);
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

	/**
	 * Equalizes image using Histogram Equalization.
	 * "Histogram equalization is a method in image processing of contrast adjustment using the image's histogram."
	 * 
	 * http://en.wikipedia.org/wiki/Histogram_equalization
	 * 
	 * @param pixelsLuminosisity
	 * @return histogram equalized image
	 */
	public static int[] getHistogramEqualizedGrayScaleImage(int[] pixelsLuminosisity) {
		int imageSize = pixelsLuminosisity.length;
		int[] equalizedImage = new int[imageSize];
		double equalizationAlpha = 255D / (imageSize);
		int sizeOfHistogram = 256;
		int[] intensityHistogram = new int[sizeOfHistogram];
		int[] cumulativeIntensityHistogram = new int[sizeOfHistogram];
		for (int q = 0; q < imageSize; q++) {
			int value = pixelsLuminosisity[q];
			intensityHistogram[value]++;
		}
		cumulativeIntensityHistogram[0] = (int) (intensityHistogram[0] * equalizationAlpha);
		for (int i = 1; i < sizeOfHistogram; i++) {
			cumulativeIntensityHistogram[i] = (int) (cumulativeIntensityHistogram[i - 1] + (intensityHistogram[i] * equalizationAlpha));
		}
		for (int i = 0; i < imageSize; i++) {
			equalizedImage[i] = (int) (cumulativeIntensityHistogram[(pixelsLuminosisity[i])]);
		}
		return equalizedImage;
	}

	/**
	 * Converts an integer array with Red, Green and Blue values of each pixel,
	 * sequentially, to a BufferedImage
	 * 
	 * @param rgb
	 * @param width
	 * @param height
	 * @return correspondent buffered image
	 */
	public static BufferedImage getBufferedImageFrom3bytePixelArray(int[] rgb, int width, int height) {
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
