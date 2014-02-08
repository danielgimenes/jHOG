package com.dgimenes.jhog.entity;

import java.util.ArrayList;
import java.util.List;

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
