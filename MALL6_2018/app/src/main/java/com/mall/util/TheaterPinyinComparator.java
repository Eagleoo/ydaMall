package com.mall.util;
import java.util.Comparator;

import com.mall.model.MovieTheaterModel;
public class TheaterPinyinComparator implements Comparator<MovieTheaterModel> {
	public int compare(MovieTheaterModel o1, MovieTheaterModel o2) {
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

