package com.dgimenes.jhog.test;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

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
		this.printHOGDescriptors(hog.getHOGDescriptors());
		this.showImagesRepresentingHOGProcessing(hog);
	}

	private void showImagesRepresentingHOGProcessing(HOGProcessor hog) {
		JFrame frame = new JFrame(JHOGTest.class.getSimpleName());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new FlowLayout());
		frame.add(new JLabel(new ImageIcon(hog.getOriginalImage())));
		// frame.add(new JLabel(new ImageIcon(hog.getLuminosityImage())));
		frame.add(new JLabel(new ImageIcon(hog.getGradientBordersImage())));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private void printHOGDescriptors(List<Double> hogDescriptors) {
		System.out.println("HOG DESCRIPTORS");
		for (Double descriptor : hogDescriptors) {
			System.out.print(descriptor + " ");
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
