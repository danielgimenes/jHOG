package com.dgimenes.jhog.entity;

public class Gradient {
	private double orientation;
	private double magnitude;
	
	public Gradient() {
	}

	public Gradient(double orientation, double magnitude) {
		super();
		this.orientation = orientation;
		this.magnitude = magnitude;
	}

	public double getOrientation() {
		return orientation;
	}

	public double getMagnitude() {
		return magnitude;
	}

	public void setOrientation(double orientation) {
		this.orientation = orientation;
	}

	public void setMagnitude(double magnitude) {
		this.magnitude = magnitude;
	}

	@Override
	public String toString() {
		return "Gradient [orientation=" + orientation + ", magnitude=" + magnitude + "]";
	}
}
