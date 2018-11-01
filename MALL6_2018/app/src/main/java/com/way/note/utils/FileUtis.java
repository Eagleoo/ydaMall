package com.way.note.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;

import android.content.Context;
import android.os.Environment;

/**
 * 文件管理工具
 * 
 * @author way
 * 
 */
public class FileUtis {
	// 判断SD卡是否存在
	public static boolean isSDCardExist() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	public static void saveStringWithAppendMode(File filePath, String value)
			throws IOException {
		BufferedWriter bfWriter = null;
		StringReader stringReader = null;
		try {
			FileWriter writer = new FileWriter(filePath, true);
			bfWriter = new BufferedWriter(writer);

			stringReader = new StringReader(value);
			char[] buffer = new char[1024 * 8];
			int len = 0;
			while ((len = stringReader.read(buffer)) != -1) {
				bfWriter.write(buffer, 0, len);
			}
			bfWriter.newLine();
			bfWriter.flush();
		} finally {
			if (bfWriter != null) {
				bfWriter.close();
			}

			if (stringReader != null) {
				stringReader.close();
			}
		}
	}

	public static void saveStringWithAppendMode(String filePath, String value)
			throws IOException {
		saveStringWithAppendMode(new File(filePath), value);
	}

	/**
	 * 在SD卡上创建文件保存路径
	 * 
	 * @param context
	 * @return
	 * @throws IOException
	 */
	public static File makeFilePath(Context context) throws IOException {
		File sdcardFile = Environment.getExternalStorageDirectory();
		String fileName = "notes.txt";
		File filePath = new File(sdcardFile, fileName);

		int i = 1;

		while (filePath.exists()) {
			fileName = "notes(" + i + ").txt";
			i++;
			filePath = new File(sdcardFile, fileName);
		}
		filePath.createNewFile();
		return filePath;
	}

}
