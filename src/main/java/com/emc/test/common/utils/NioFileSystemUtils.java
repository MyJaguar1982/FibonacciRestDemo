package com.emc.test.common.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NioFileSystemUtils {
	private static final Logger log = LoggerFactory
			.getLogger(NioFileSystemUtils.class);

	public static int countInFolder(String path) {
		Path p = Paths.get(path);
		return p.toFile().listFiles().length;
	}

	public static void deleteFolder(String path) throws IOException {
		Path p = Paths.get(path);
		Files.walkFileTree(p, new FileVisitor<Path>() {

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc)
					throws IOException {
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult preVisitDirectory(Path dir,
					BasicFileAttributes attrs) throws IOException {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file,
					BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				log.error("Cannot delete file: " + file.toFile().getPath());
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc)
					throws IOException {
				return FileVisitResult.CONTINUE;
			}
		});
	}
	

	public static StringBuffer readByNIO(String file) throws IOException {
		FileInputStream inputStream = null;
		Scanner sc = null;
		StringBuffer sb = new StringBuffer();
		try {
			inputStream = new FileInputStream(file);
			sc = new Scanner(inputStream, "UTF-8");
			while (sc.hasNextLine()) {
				sb.append(sc.nextLine()).append(" ");
			}

			if (sc.ioException() != null) {
				throw sc.ioException();
			}
			return sb;
		} finally {
			if (inputStream != null) {
				inputStream.close();
			}
			if (sc != null) {
				sc.close();
			}
		}
	}

}
