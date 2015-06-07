package com.lezo.iscript.resulter.cluster;

import lombok.Data;

import org.apache.commons.math3.ml.clustering.Clusterable;

import com.lezo.iscript.resulter.token.CharPart;

@Data
public class CharPartPoint implements Clusterable {
	private CharPart charPart;

	public CharPartPoint(CharPart charPart) {
		super();
		this.charPart = charPart;
	}

	@Override
	public double[] getPoint() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		return "CharPartPoint [charPart=" + charPart + "]";
	}

}
