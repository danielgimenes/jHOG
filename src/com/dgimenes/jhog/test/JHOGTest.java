package com.dgimenes.jhog.test;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
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

import sun.awt.VerticalBagLayout;

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
		JPanel panel1 = new JPanel();
		panel1.setLayout(new BoxLayout(panel1, BoxLayout.Y_AXIS));
		JLabel title1 = new JLabel("<html><font color=red size=+1><b>Original Image</b></font></html>");
		panel1.add(title1);
		panel1.add(new JLabel(new ImageIcon(hog.getOriginalImage())));
		panel1.doLayout();
		frame.add(panel1);
		JPanel panel2 = new JPanel();
		panel2.setLayout(new BoxLayout(panel2, BoxLayout.Y_AXIS));
		JLabel title2 = new JLabel("<html><font color=red size=+1><b>Gradient Magnitudes Normalized to Gray Scale</b></font></html>");
		panel2.add(title2);
		panel2.add(new JLabel(new ImageIcon(hog.getGradientBordersImage())));
		panel2.doLayout();
		frame.add(panel2);
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
