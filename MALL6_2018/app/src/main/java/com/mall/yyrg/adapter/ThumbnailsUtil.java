package com.mall.yyrg.adapter;

import java.util.HashMap;

import android.annotation.SuppressLint;

/**    
 * 
 * @author GuiLin
 */
public class ThumbnailsUtil {
	
	@SuppressLint("UseSparseArrays")
	private static HashMap<Integer,String> hash = new HashMap<Integer, String>();
	
	/**
	 * 
	 * @param key
	 * @return
	 */
	public static String MapgetHashValue(int key,String defalt){
		if(hash==null||!hash.containsKey(key))return defalt;
		return hash.get(key);
	}
	
	/**
	 */
	public static void put(Integer key,String value){
		hash.put(key, value);
	}
	
	public static void clear(){
		hash.clear();
	}
}
