/*
 * @(#) FileCategorizeMain.java 2020. 07. 13.
 *
 */
package com.tistory.ddoriya.main;

import com.tistory.ddoriya.utill.FileListUtillity;
import com.tistory.ddoriya.utill.FileUtility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author 이상준
 */
public class FileCategorizeMain {

	static String moveFilePath = "/var/services/homes/photo/Drive/Moments";
	static String resultFilePath = "/var/services/photo/폴더별정리";

	public static void main(String[] args) {
		System.out.println("시작!!!");

		FileListUtillity fileListUtillity = new FileListUtillity();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
		fileListUtillity.getDirFileList(moveFilePath).stream().forEach(file -> {
		BasicFileAttributes attrs = null;
			try {
				attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
				FileTime time = attrs.creationTime();
				String resultDirPath = sdf.format(new Date(time.toMillis()));
				File dir = new File(resultFilePath + "/" + resultDirPath);
				if (!dir.exists()) {
					dir.mkdirs();
				}

				FileUtility.copy(file.getAbsolutePath(), resultFilePath + "/" + resultDirPath + "/" + file.getName());

				System.out.println("복사 파일 : " + resultFilePath + "/" + resultDirPath + "/" + file.getName());
			} catch (IOException e) {
				e.printStackTrace();
			}

		});
	}
}
