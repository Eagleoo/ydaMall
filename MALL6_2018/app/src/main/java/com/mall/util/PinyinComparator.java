package com.mall.util;
import java.util.Comparator;

import com.mall.model.FilmCityModel;
public class PinyinComparator implements Comparator<FilmCityModel> {
	public int compare(FilmCityModel o1, FilmCityModel o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return -1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return 1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}
}

