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

import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.dgimenes.jhog.HOGProcessor;

public class JHOGTest {

	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.out.println("Error: invalid parameters. \nSyntax: jHOG <image_file_path>\n");
			System.exit(0);
		}
		new JHOGTest().executeTest(args[0]);
	}

	private void executeTest(String imageFilePath) throws IOException {
		BufferedImage image = this.getImageFromFile(imageFilePath);
		HOGProcessor hog = new HOGProcessor(image);
		hog.processImage();
		this.printHOGDescriptors(hog.getHOGDescriptor());
		this.showImagesRepresentingHOGProcessing(hog);
	}

	private void showImagesRepresentingHOGProcessing(HOGProcessor hog) {
		JFrame frame = new JFrame(JHOGTest.class.getSimpleName());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(1, 2));
		this.addPanelWithImage(frame, "Original Image", hog.getOriginalImage());
		// this.addPanelWithImage(frame, "Luminosity Image",
		// hog.getLuminosityImage());
		// this.addPanelWithImage(frame, "Luminosity Image (HistogramEq)",
		// hog.getLuminosityImageHistogramEqualized());
		// this.addPanelWithImage(frame, "Luminosity Image (MinMaxEq)",
		// hog.getLuminosityImageMinMaxEqualized());
		// this.addPanelWithImage(frame, "Gradient Magnitudes (MinMaxEq)",
		// hog.getGradientMagnitudeImage());
		// this.addPanelWithImage(frame, "Gradient Cells",
		// hog.getLuminosityImageWithCells(true));
		this.addPanelWithImage(frame, "HOG Descriptors Representation", hog.getHOGDescriptorsRepresentation());
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private void addPanelWithImage(JFrame frame, String name, Image originalImage) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		JLabel title = new JLabel("<html><font color=red size=-1><b>" + name + "</b></font></html>");
		panel.add(title);
		// originalImage = originalImage.getScaledInstance(400, -1,
		// java.awt.Image.SCALE_SMOOTH);
		panel.add(new JLabel(new ImageIcon(originalImage)));
		panel.doLayout();
		frame.add(panel);
	}

	private void printHOGDescriptors(List<Double> hogDescriptors) {
		System.out.println("HOG DESCRIPTORS:");
		for (Double descriptor : hogDescriptors) {
			System.out.print(descriptor + "; ");
		}
	}

	private BufferedImage getImageFromFile(String imageFilePath) throws IOException {
		File imageFile = new File(imageFilePath);
		if (!imageFile.exists() || imageFile.isDirectory() || imageFile.length() == 0) {
			throw new FileNotFoundException();
		}
		return ImageIO.read(imageFile);
	}
}
