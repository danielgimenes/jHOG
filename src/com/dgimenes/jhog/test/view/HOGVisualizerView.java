package com.dgimenes.jhog.test.view;

import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.dgimenes.jhog.HOGProcessor;
import com.dgimenes.jhog.test.HOGVisualizer;

public class HOGVisualizerView {
	private static final int MINIMUM_SCALE_WIDTH = 0;
	private static final Integer MAXIMUM_SCALE_WIDTH = 500;
	private HOGProcessor hog;
	private Integer widthToScale;

	public HOGVisualizerView(HOGProcessor hog, Integer widthToScale) {
		this.hog = hog;
		this.widthToScale = widthToScale;
	}

	public HOGVisualizerView(HOGProcessor hog) {
		this(hog, null);
	}

	public void show() {
		JFrame frame = new JFrame(HOGVisualizer.class.getSimpleName());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new GridLayout(2, 2));
		this.addPanelWithImage(frame, "Original Image", hog.getOriginalImage());
		this.addPanelWithImage(frame, "Luminosity Image", hog.getLuminosityImage());
		this.addPanelWithImage(frame, "Luminosity Image (HistogramEq)",
				hog.getLuminosityImageHistogramEqualized());
		this.addPanelWithImage(frame, "Luminosity Image (MinMaxEq)",
				hog.getLuminosityImageMinMaxEqualized());
		this.addPanelWithImage(frame, "Gradient Magnitudes (MinMaxEq)",
				hog.getGradientMagnitudeImage());
		this.addPanelWithImage(frame, "Gradient Cells", hog.getLuminosityImageWithCells(true));
		this.addPanelWithImage(frame, "HOG Descriptors Representation",
				hog.getHOGDescriptorsRepresentation());
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private void addPanelWithImage(JFrame frame, String name, Image originalImage) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		JLabel title = new JLabel("<html><font color=red size=-1><b>" + name + "</b></font></html>");
		panel.add(title);
		if (widthToScale != null && widthToScale > MINIMUM_SCALE_WIDTH
				&& widthToScale < MAXIMUM_SCALE_WIDTH) {
			originalImage = originalImage.getScaledInstance(widthToScale, -1,
					java.awt.Image.SCALE_SMOOTH);
		}
		panel.add(new JLabel(new ImageIcon(originalImage)));
		panel.doLayout();
		frame.add(panel);
	}
}
