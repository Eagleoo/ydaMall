package com.mall.util;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

/**
 * java 代码实现组合的算法 从n个数里取出m个数的组合是n*(n-1)*...*(n-m+1)/m*(m-1)*...2*1
 * 
 * @author
 * 
 */
public class Combination {
	/**
	 * @param a
	 *            :组合数组
	 * @param m
	 *            :生成组合长度
	 * @return :所有可能的组合数组列表
	 */
	public static List<String> combination(String[] a, int m, String symbol) {

		List<String> list = new ArrayList<String>();
		int n = a.length;
		boolean end = false; // 是否是最后一种组合的标记
		// 生成辅助数组。首先初始化，将数组前n个元素置1，表示第一个组合为前n个数。
		int[] tempNum = new int[n];
		for (int i = 0; i < n; i++) {
			if (i < m) {
				tempNum[i] = 1;
			} else {
				tempNum[i] = 0;
			}
		}
		// printVir(tempNum);// 打印首个辅助数组
		list.add(createResultToString(a, tempNum, m, symbol));// 打印第一种默认组合
		int k = 0;// 标记位
		while (!end) {
			boolean findFirst = false;
			boolean swap = false;
			// 然后从左到右扫描数组元素值的"10"组合，找到第一个"10"组合后将其变为"01"
			for (int i = 0; i < n; i++) {
				int l = 0;
				if (!findFirst && tempNum[i] == 1) {
					k = i;
					findFirst = true;
				}
				if (tempNum[i] == 1 && tempNum[i + 1] == 0) {
					tempNum[i] = 0;
					tempNum[i + 1] = 1;
					swap = true;
					for (l = 0; l < i - k; l++) { // 同时将其左边的所有"1"全部移动到数组的最左端。
						tempNum[l] = tempNum[k + l];
					}
					for (l = i - k; l < i; l++) {
						tempNum[l] = 0;
					}
					if (k == i && i + 1 == n - m) {// 假如第一个"1"刚刚移动到第n-m+1个位置,则终止整个寻找
						end = true;
					}
				}
				if (swap) {
					break;
				}
			}
			// printVir(tempNum);// 打印辅助数组
			list.add(createResultToString(a, tempNum, m, symbol));// 添加下一种默认组合
		}
		return list;
	}

	public static List<String[]> combinationStringArray(String[] a, int m) {

		List<String[]> list = new ArrayList<String[]>();
		int n = a.length;
		boolean end = false; // 是否是最后一种组合的标记
		// 生成辅助数组。首先初始化，将数组前n个元素置1，表示第一个组合为前n个数。
		int[] tempNum = new int[n];
		for (int i = 0; i < n; i++) {
			if (i < m) {
				tempNum[i] = 1;
			} else {
				tempNum[i] = 0;
			}
		}
		// printVir(tempNum);// 打印首个辅助数组
		list.add(createResultToStringArray(a, tempNum, m));// 打印第一种默认组合
		int k = 0;// 标记位
		while (!end) {
			boolean findFirst = false;
			boolean swap = false;
			// 然后从左到右扫描数组元素值的"10"组合，找到第一个"10"组合后将其变为"01"
			for (int i = 0; i < n; i++) {
				int l = 0;
				if (!findFirst && tempNum[i] == 1) {
					k = i;
					findFirst = true;
				}
				if (tempNum[i] == 1 && tempNum[i + 1] == 0) {
					tempNum[i] = 0;
					tempNum[i + 1] = 1;
					swap = true;
					for (l = 0; l < i - k; l++) { // 同时将其左边的所有"1"全部移动到数组的最左端。
						tempNum[l] = tempNum[k + l];
					}
					for (l = i - k; l < i; l++) {
						tempNum[l] = 0;
					}
					if (k == i && i + 1 == n - m) {// 假如第一个"1"刚刚移动到第n-m+1个位置,则终止整个寻找
						end = true;
					}
				}
				if (swap) {
					break;
				}
			}
			// printVir(tempNum);// 打印辅助数组
			list.add(createResultToStringArray(a, tempNum, m));// 添加下一种默认组合
		}
		return list;
	}

	// 根据辅助数组和原始数组生成结果数组
	private static String createResultToString(String[] a, int[] temp, int m,
			String symbol) {
		String[] result = new String[m];
		int j = 0;
		for (int i = 0; i < a.length; i++) {
			if (temp[i] == 1) {
				result[j] = a[i];
				// System.out.println("result[" + j + "]:" + result[j]);
				j++;
			}
		}
		return arrayToString(result, symbol);
	}

	private static String[] createResultToStringArray(String[] a, int[] temp,
			int m) {
		String[] result = new String[m];
		int j = 0;
		for (int i = 0; i < a.length; i++) {
			if (temp[i] == 1) {
				result[j] = a[i];
				// System.out.println("result[" + j + "]:" + result[j]);
				j++;
			}
		}
		return result;
	}

	/**
	 * 将字符串数组变成字符
	 * 
	 * @param str
	 * @return
	 */
	public static String arrayToString(String[] str, String symbol) {
		if (str.length > 0) {

			str = stringSort(str);
			String string = str[0];
			for (int i = 1; i < str.length; i++) {
				string += symbol + str[i];
			}
			return string;
		}
		return null;

	}

	/**
	 * 将list转成字符串数组
	 * 
	 * @param list
	 * @return
	 */
	public static String[] listToString(List<String> list) {
		String[] str = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {

			str[i] = list.get(i);

		}

		return str;

	}

	/**
	 * 将两个字符串数组合并成一个
	 * 
	 * @param str1
	 * @param str2
	 * @return
	 */
	public static String[] combineStringArray(String[] str1, String[] str2) {
		int size = str1.length + str2.length;
		String[] str = new String[size];
		for (int i = 0; i < str1.length; i++) {
			str[i] = str1[i];
		}
		int num = str1.length;
		for (int i = 0; i < str2.length; i++) {
			str[(num + i)] = str2[i];
		}
		return str;
	}

	/**
	 * 字符串排序
	 * 
	 * @param str
	 * @return
	 */
	public static String[] stringSort(String[] str) {
		for (int i = 0; i < str.length; i++) {
			for (int j = i; j < str.length; j++) {
				if (str[i].compareTo(str[j]) > 0) {
					String temp = str[i];
					str[i] = str[j];
					str[j] = temp;
				}
			}
		}
		return str;
	}

	/**
	 * 用递归避免随机数重复
	 * 
	 * @param list
	 * @param count
	 * @param num
	 * @return
	 */
	private static int randomNumber(List list, int count, int num) {
		for (int j = 0; j < list.size(); j++) {

			String string = (String) list.get(j);
			boolean equals = (num == Integer.parseInt(string));
			if (equals) {
				while (equals) {

					num = (int) (Math.random() * (count - 1) + 1);
					equals = (num == Integer.parseInt(string));
					Log.i("result", num + "*****");
					num = randomNumber(list, count, num);
					break;

				}

			}

		}
		return num;
	}

	/**
	 * 生成随机数
	 * 
	 * @param list
	 *            装随机数的list
	 * @param n
	 *            随机数的数量
	 * @param count
	 *            选随机数的范围
	 */
	public static List<String> machineNumber(int n, int count) {
		List<String> list = new ArrayList<String>();
		if (n == count) {
			for (int i = 0; i < n; i++) {
				int num = i+1;
				if (num < 10) {
					list.add("0" + num);
				} else {
					list.add(num + "");
				}
			}
			return list;
		}
		for (int i = 0; i < n; i++) {
			int num = (int) (Math.random() * (count - 1) + 1);
			Log.i("result", num + "------");
			int size = list.size();
			if (size > 0) {
				num = randomNumber(list, count, num);
			}
			Log.i("result", num + "////");
			if (num < 10) {
				list.add("0" + num);
			} else {
				list.add(num + "");
			}

		}
		return list;
	}

	// 打印整组数组
	public void print(List<String[]> list) {
		System.out.println("具体组合结果为:");
		for (int i = 0; i < list.size(); i++) {
			String[] temp = (String[]) list.get(i);
			for (int j = 0; j < temp.length; j++) {
				java.text.DecimalFormat df = new java.text.DecimalFormat("00");// 将输出格式化
				System.out.print(df.format(temp[j]) + " ");
			}
			System.out.println();
		}
	}

	// 打印辅助数组的方法
	public void printVir(int[] a) {
		System.out.println("生成的辅助数组为：");
		for (int i = 0; i < a.length; i++) {
			System.out.print(a[i]);
		}
		System.out.println();
	}

}
