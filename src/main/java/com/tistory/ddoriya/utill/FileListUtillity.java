/*
 * @(#) FileListUtillity.java 2020. 07. 14.
 *
 */
package com.tistory.ddoriya.utill;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 이상준
 */
public class FileListUtillity {

	public List<File> dirFileList;

	private String ext = "png,jpg,HEIC";

	public FileListUtillity() {
		this.dirFileList = new ArrayList<>();
	}

	private void showFilesInDIr(String dirPath) {
		System.out.println("dirPath : " + dirPath);
		File dir = new File(dirPath);
		File files[] = dir.listFiles();

		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory()) {
				showFilesInDIr(file.getPath());
			} else {
				if (FileUtility.chkExtsFilter(file.getName(), ext)) {
					dirFileList.add(file);
				}
			}
		}
	}

	public List<File> getDirFileList(String dirPath) {
		System.out.println("폴더 색인 시작....");
		showFilesInDIr(dirPath);
		System.out.println("폴더 색인 끝....");
		return dirFileList;
	}
}
