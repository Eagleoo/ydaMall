package com.mall.util;
import java.util.Comparator;

import com.mall.model.PlaneCityModel;
public class PlaneCityCompartor implements Comparator<PlaneCityModel> {
	public int compare(PlaneCityModel o1, PlaneCityModel o2) {
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

