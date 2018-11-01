package com.mall.model;

import com.mall.util.Util;

public class LMSJScoreAndCommentCount implements java.io.Serializable {

	private static final long serialVersionUID = -8251498854271451163L;
	private String id;
	private String score;
	private String person;
	private String sumScore;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

	public String getPerson() {
		if(Util.isNull(person))
			this.person = "0";
		return person;
	}

	public void setPerson(String person) {
		this.person = person;
	}

	public String getSumScore() {
		return sumScore;
	}

	public void setSumScore(String sumScore) {
		this.sumScore = sumScore;
	}

}
