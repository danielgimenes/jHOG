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

import java.util.ArrayList;
import java.util.List;

/**
 * Group of gradients of spatially proximate pixels
 * 
 * @author danielgimenes
 * @version 1.0
 */
public class GradientCell {
	private List<Gradient> gradients;
	private int length;

	public GradientCell(int length) {
		this.length = length;
		this.gradients = new ArrayList<Gradient>(length);
	}

	public List<Gradient> getGradients() {
		return gradients;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((gradients == null) ? 0 : gradients.hashCode());
		result = prime * result + length;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GradientCell other = (GradientCell) obj;
		if (gradients == null) {
			if (other.gradients != null)
				return false;
		} else if (!gradients.equals(other.gradients))
			return false;
		if (length != other.length)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GradientCell [gradients=" + gradients + ", length=" + length + "]";
	}
}
