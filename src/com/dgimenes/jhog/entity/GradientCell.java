package com.dgimenes.jhog.entity;

import java.util.Arrays;

public class GradientCell {
	private Gradient[] gradients;
	
	public GradientCell() {
	}

	public GradientCell(int quantityOfPixels) {
		this.gradients = new Gradient[quantityOfPixels];
	}

	public Gradient[] getGradients() {
		return gradients;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(gradients);
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
		if (!Arrays.equals(gradients, other.gradients))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "GradientCell [gradients=" + Arrays.toString(gradients) + "]";
	}
}
