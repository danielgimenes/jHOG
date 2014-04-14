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
package com.dgimenes.jhog.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import com.dgimenes.jhog.HOGProcessor;
import com.dgimenes.jhog.test.view.HOGVisualizerView;

public class HOGVisualizer {
	private static final int WIDTH_TO_SCALE = 400;

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.out.println("Error: invalid parameters. \nSyntax: "
					+ HOGVisualizer.class.getSimpleName() + " <image_file_path>\n");
			System.exit(0);
		}
		new HOGVisualizer().processImage(args[0]);
	}

	private void processImage(String imageFilePath) throws IOException {
		BufferedImage image = this.getImageFromFile(imageFilePath);
		HOGProcessor hog = new HOGProcessor(image);
		hog.processImage();
		this.printHOGDescriptors(hog);
		new HOGVisualizerView(hog, WIDTH_TO_SCALE).show();
	}

	private BufferedImage getImageFromFile(String imageFilePath) throws IOException {
		File imageFile = new File(imageFilePath);
		if (!imageFile.exists() || imageFile.isDirectory() || imageFile.length() == 0) {
			throw new FileNotFoundException();
		}
		return ImageIO.read(imageFile);
	}

	private void printHOGDescriptors(HOGProcessor hog) {
		List<Double> hogDescriptors = hog.getHOGDescriptors();
		System.out.println("HOG DESCRIPTORS:");
		for (Double descriptor : hogDescriptors) {
			System.out.print(descriptor + "; ");
		}
	}
}
