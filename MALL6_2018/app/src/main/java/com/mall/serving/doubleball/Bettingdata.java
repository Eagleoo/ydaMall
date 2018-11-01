package com.mall.serving.doubleball;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Bettingdata implements Serializable {

	private List<String> redBallStraightList = new ArrayList<String>();
	private List<String> redBallCourageList = new ArrayList<String>();
	private List<String> redBallDragList = new ArrayList<String>();
	private List<String> blueBallStraightList = new ArrayList<String>();
	private List<String> blueBallDragList = new ArrayList<String>();

	private int count;
	private String money;

	private boolean isStraight;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public List<String> getRedBallStraightList() {
		return redBallStraightList;
	}

	public void setRedBallStraightList(List<String> redBallStraightList) {
		this.redBallStraightList = redBallStraightList;
	}

	public List<String> getRedBallCourageList() {
		return redBallCourageList;
	}

	public void setRedBallCourageList(List<String> redBallCourageList) {
		this.redBallCourageList = redBallCourageList;
	}

	public List<String> getRedBallDragList() {
		return redBallDragList;
	}

	public void setRedBallDragList(List<String> redBallDragList) {
		this.redBallDragList = redBallDragList;
	}

	public List<String> getBlueBallStraightList() {
		return blueBallStraightList;
	}

	public void setBlueBallStraightList(List<String> blueBallStraightList) {
		this.blueBallStraightList = blueBallStraightList;
	}

	public List<String> getBlueBallDragList() {
		return blueBallDragList;
	}

	public void setBlueBallDragList(List<String> blueBallDragList) {
		this.blueBallDragList = blueBallDragList;
	}

	public boolean isStraight() {
		return isStraight;
	}

	public void setStraight(boolean isStraight) {
		this.isStraight = isStraight;
	}

}
