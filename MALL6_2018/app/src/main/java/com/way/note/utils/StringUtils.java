package com.way.note.utils;

/**
 * 字符串管理工具类
 * 
 * @author way
 * 
 */
public class StringUtils {

	public static String deleteEndSpace(String oldString) {
		if (oldString.trim().equals("")) {
			return "";
		}

		String content = null;
		int j = oldString.length();
		if (j > 0) {
			for (int i = oldString.length() - 1; i >= 0; i--) {
				if (oldString.charAt(i) == ' ' || oldString.charAt(i) == '\n') {
					continue;

				} else {
					j = i;
					break;
				}
			}
			if (j < oldString.length()) {
				content = oldString.substring(0, j + 1);
			} else {
				content = oldString.substring(0, j);
			}
		} else {
			content = oldString;
		}
		return content;
	}
}
