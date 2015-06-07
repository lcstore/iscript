package com.lezo.iscript.resulter.cluster;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.lezo.iscript.resulter.token.CharPart;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Node {
	private CharPart charPart;

	public double getDistance(Node other) {
		return 0;
	}
}
