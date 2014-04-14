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
package com.dgimenes.jhog.entity;

/**
 * Gradient of a pixel in a direction (orientation) with a certain magnitude (intensity).
 * 
 * @author danielgimenes
 * @version 1.0
 */
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
