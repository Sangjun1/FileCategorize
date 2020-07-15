/*
 * @(#) FileUtility.java 2020. 07. 13.
 *
 */
package com.tistory.ddoriya.utill;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

/**
 * @author 이상준
 */
@Slf4j
public class FileUtility {

	public static final int DEFAULT_BUFFER_SIZE = 1448;
	private static final String DEFAULT_CHARSET = "UTF-8";

	public static boolean copy(String source, String target) {
		return copy(new File(source), new File(target));
	}

	public static boolean copy(File source, File target) {
		boolean result = false;

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		try {
			File file = new File(target.getParent());
			if (!file.exists()) {
				file.mkdirs();
			}

			bis = new BufferedInputStream(new FileInputStream(source),
					DEFAULT_BUFFER_SIZE);
//			System.out.println(target.canWrite());
			bos = new BufferedOutputStream(new FileOutputStream(target),
					DEFAULT_BUFFER_SIZE);

			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

			int readBytes;
			while ((readBytes = bis.read(buffer)) != -1) {
				bos.write(buffer, 0, readBytes);
			}

			bis.close();
			bos.close();

			result = true;
		} catch (IOException e) {
			e.printStackTrace();
			log.error("copy error[{}][{}]\n {}", source, target, e);
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
				}
			}
		}

		return result;
	}

	public static boolean copy(InputStream input, File target) {
		boolean result = false;

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		try {
			File file = new File(target.getParent());
			if (!file.exists()) {
				file.mkdirs();
			}

			bis = new BufferedInputStream(input, DEFAULT_BUFFER_SIZE);
			bos = new BufferedOutputStream(new FileOutputStream(target),
					DEFAULT_BUFFER_SIZE);

			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

			int readBytes;
			while ((readBytes = bis.read(buffer)) != -1) {
				bos.write(buffer, 0, readBytes);
			}

			bis.close();
			bos.close();

			result = true;
		} catch (IOException e) {
			log.error("copy error [{}] \r\n {}", target, e);
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
				}
			}
		}

		return result;
	}

	public static boolean appendCopy(File source, File target) {
		boolean result = false;

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		try {
			File file = new File(target.getParent());
			if (!file.exists()) {
				file.mkdirs();
			}

			bis = new BufferedInputStream(new FileInputStream(source),
					DEFAULT_BUFFER_SIZE);
			bos = new BufferedOutputStream(new FileOutputStream(target, true),
					DEFAULT_BUFFER_SIZE);

			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

			int readBytes;
			while ((readBytes = bis.read(buffer)) != -1) {
				bos.write(buffer, 0, readBytes);
			}

			bis.close();
			bos.close();

			result = true;
		} catch (IOException e) {
			log.error("copy error [{}] to [{}] \n {}", source, target, e);
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
				}
			}
		}

		return result;
	}

	public static boolean renameTo(String source, String target) {
		return renameTo(new File(source), new File(target));
	}

	public static boolean renameTo(File source, File target) {
		boolean result = false;

		if (source.exists()) {
			File file = new File(target.getParent());
			if (!file.exists()) {
				file.mkdirs();
			}

			if (target.exists()) {
				target.delete();
			}

			result = source.renameTo(target);
			if (result) {
				log.debug("renameTo success [{}]==>[{}]", source, target);
			} else {
				result = copy(source, target);
				if (result) {
					result = delete(source);
					if (result) {
						log.debug("copy/delete success [{}]==>[{}]", source, target);
					} else {
						log.error("renameTo failed [{}]==>[{}]", source, target);

						delete(target);
					}
				} else {
					log.error("renameTo failed [{}]==>[{}]", source, target);
				}
			}
		} else {
			log.error("renameTo error (source file not found) [{}]", source);
		}

		return result;
	}

	public static boolean write(String source, InputStream is) {
		return write(new File(source), is);
	}

	public static boolean write(File source, InputStream is) {
		boolean result = false;

		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;

		try {
			bis = new BufferedInputStream(is, DEFAULT_BUFFER_SIZE);
			bos = new BufferedOutputStream(new FileOutputStream(source),
					DEFAULT_BUFFER_SIZE);

			byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

			int readBytes;
			while ((readBytes = bis.read(buffer)) != -1) {
				bos.write(buffer, 0, readBytes);
			}

			bis.close();
			bos.close();

			result = true;
		} catch (IOException e) {
			if (log.isErrorEnabled()) {
				log.error("write error: " + e);
			}
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
				}
			}
		}

		return result;
	}

	public static boolean write(String source, String body) {
		return write(new File(source), body, DEFAULT_CHARSET);
	}

	public static boolean write(String source, String body, String charset) {
		return write(new File(source), body, charset);
	}

	public static boolean write(File source, String body) {
		return write(source, body, DEFAULT_CHARSET);
	}

	public static boolean write(File source, String body, String charset) {
		boolean result = false;

		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(source), charset), DEFAULT_BUFFER_SIZE);
			writer.write(body);
			writer.flush();

			result = true;
		} catch (IOException e) {
			log.error("write error: {}", e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
		}

		return result;
	}

	public static boolean write(String source, List<String> bodies,
								String charset, String seperation) {
		String[] body = new String[bodies.size()];
		return write(new File(source), bodies.toArray(body), charset,
				seperation);
	}

	public static boolean write(File source, String[] bodies, String charset,
								String seperation) {
		boolean result = false;

		BufferedWriter writer = null;

		try {
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(source), charset), DEFAULT_BUFFER_SIZE);
			for (String body : bodies) {
				writer.write(body + (seperation == null ? "" : seperation));
			}
			writer.flush();

			result = true;
		} catch (IOException e) {
			log.error("write error: {}", e);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
				}
			}
		}

		return result;
	}

	public static boolean delete(String target) {
		return delete(new File(target));
	}

	public static boolean delete(File target) {
		if (target.exists() && target.isFile()) {
			boolean result = target.delete();

			log.debug("delete [{}][{}]", target.getAbsolutePath(), (result ? "success" : "failed"));

			return result;
		} else {
			log.warn("delete error(File Not Found) [{}]", target.getAbsolutePath());
			return false;
		}
	}

	public static void delete(File[] target) {
		for (int i = 0; i < target.length; i++) {
			if (target[i].exists() && target[i].isFile()) {
				boolean result = target[i].delete();

				log.debug("delete [{}][{}]", target[i].getName(), (result ? "success" : "failed"));
			} else {
				if (log.isErrorEnabled()) {
					log.error("delete error(File Not Found) [{}]", target[i].getAbsolutePath());
				}
			}
		}
	}

	public static long size(String target) {
		return size(new File(target));
	}

	public static long size(File target) {
		long fileSize = 0;

		if (target.exists() && target.isFile()) {
			fileSize = target.length();
		}

		return fileSize;
	}

	public static List<File> getFileList(String baseDir, boolean getSubDir) {
		List<File> fileList = new ArrayList<File>();

		File root = new File(baseDir);
		File[] list = root.listFiles();

		for (File f : list) {
			if (getSubDir && f.isDirectory()) {
				fileList.add(f);
				List<File> subFileList = getFileList(f.getAbsolutePath(), true);
				fileList.addAll(subFileList);
			} else {
				fileList.add(f);
			}
		}

		return fileList;
	}

	public static String getMD5Value(String filename) throws Exception {
		String md5_digest = "";

		MessageDigest md5 = MessageDigest.getInstance("MD5");

		FileInputStream inFile = null;
		InputStream inBuffered = null;
		InputStream inputStream = null;
		try {
			inFile = new FileInputStream(filename);
			inBuffered = new BufferedInputStream(inFile);
			inputStream = new DigestInputStream(inBuffered, md5);

			byte[] buffer = new byte[32768];
			while (inputStream.read(buffer) >= 0)
				;
		} finally {
			if (inFile != null)
				inFile.close();
			if (inBuffered != null)
				inBuffered.close();
			if (inputStream != null)
				inputStream.close();
		}

		byte[] digest = md5.digest();
		for (int i = 0; i < digest.length; i++) {
			String value = Integer.toHexString(digest[i] & 0xFF);
			if (value.length() == 1)
				value = "0" + value;
			md5_digest = md5_digest + value;
		}

		return md5_digest;
	}

	public static long getCRC32Value(String filename) throws IOException {
		File f = new File(filename);
		Checksum crc = new CRC32();

		if (f.exists()) {
			BufferedInputStream in = null;
			try {
				in = new BufferedInputStream(new FileInputStream(filename));
				byte buffer[] = new byte[32768];
				int length = 0;

				while ((length = in.read(buffer)) >= 0)
					crc.update(buffer, 0, length);
			} catch (IOException e) {
				throw e;
			} finally {
				if( in != null){
					in.close();
				}
			}
		}
		return crc.getValue();
	}

	public static boolean chkExtsFilter(String fileName, String exts) {
		String fileExt = FilenameUtils.getExtension(fileName);
		String[] extArray = exts.split(",");
		for (String ext : extArray) {
			if (ext.equalsIgnoreCase(fileExt)) {
				return true;
			}
		}
		return false;
	}

}