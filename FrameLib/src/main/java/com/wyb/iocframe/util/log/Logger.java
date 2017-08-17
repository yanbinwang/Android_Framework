package com.wyb.iocframe.util.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日志输出类
 * 
 * @author wyb
 * 
 */
public class Logger {
	// 是否输出日志
	private static final boolean isLog = true;
	private static File logFile;

	/**
	 * 根据tagName和日期生成本地日志文件
	 * 
	 * @param tagName
	 */
	public Logger(String tagName) {
		try {
			if (isLog && FileUtil.hasSDCard()) {
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy_MM_dd_hh_mm_ss");
				logFile = new File(FileUtil.createCacheDir() + File.separator
						+ tagName + format.format(new Date()) + ".log");
				logFile.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 输出日志
	 * 
	 * @param log
	 */
	public void log(String log) {
		try {
			BufferedWriter bw = new BufferedWriter(
					new FileWriter(logFile, true));
			bw.write(log);
			bw.write("\n");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
