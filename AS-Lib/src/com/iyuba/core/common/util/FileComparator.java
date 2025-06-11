package com.iyuba.core.common.util;

import java.util.Comparator;

import com.iyuba.core.common.sqlite.mode.FileInfo;

/**
 * 锟斤拷锟斤拷 **
 * 
 * 文件浏览器相关类
 */
public class FileComparator implements Comparator<FileInfo> {

	public int compare(FileInfo file1, FileInfo file2) {
		// 锟侥硷拷锟斤拷锟斤拷锟斤拷前锟斤拷
		if (file1.IsDirectory && !file2.IsDirectory) {
			return -1000;
		} else if (!file1.IsDirectory && file2.IsDirectory) {
			return 1000;
		}
		String name1 = file1.Name.toLowerCase();
		String name2 = file2.Name.toLowerCase();
		return name1.compareTo(name2);
	}
}