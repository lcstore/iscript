package com.lezo.iscript.resulter.similar;


public class SimiarPart {
	private String leftString;
	private String rightString;
	private String commonString;
	private Float score;

	public String getLeftString() {
		return leftString;
	}

	public void setLeftString(String leftString) {
		this.leftString = leftString;
	}

	public String getRightString() {
		return rightString;
	}

	public void setRightString(String rightString) {
		this.rightString = rightString;
	}

	public Float getScore() {
		return score;
	}

	public void setScore(Float score) {
		this.score = score;
	}

	@Override
	public String toString() {
		return "SimiarPart [leftString=" + leftString + ", rightString=" + rightString + ", score=" + score + "]";
	}

	public String getCommonString() {
		return commonString;
	}

	public void setCommonString(String commonString) {
		this.commonString = commonString;
	}
}
