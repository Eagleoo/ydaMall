package com.mall.serving.query.model;

import java.io.Serializable;

public class WeatherCityInfo implements Serializable{

	private int id;
	private String city="";
	private int rid;
	private String weather="";
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
	public int getRid() {
		return rid;
	}
	public void setRid(int rid) {
		this.rid = rid;
	}
	public String getWeather() {
		return weather;
	}
	public void setWeather(String weather) {
		this.weather = weather;
	}

	@Override
	public String toString() {
		return "WeatherCityInfo{" +
				"id=" + id +
				", city='" + city + '\'' +
				", rid=" + rid +
				", weather='" + weather + '\'' +
				'}';
	}
}
