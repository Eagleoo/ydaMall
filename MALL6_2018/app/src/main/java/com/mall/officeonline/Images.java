package com.mall.officeonline;

import java.util.ArrayList;
import java.util.List;

public class Images {
    public static List<String> imageUrlList=new ArrayList<String>();

	public static List<String> getImageUrlList() {
		return imageUrlList;
	}

	public static void setImageUrlList(List<String> imageUrlList) {
		Images.imageUrlList.addAll(imageUrlList);
	}
    
}
