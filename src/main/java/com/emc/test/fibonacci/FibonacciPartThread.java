package com.emc.test.fibonacci;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emc.test.common.Consts;

public class FibonacciPartThread implements Runnable {
	private final int start;
	private final int countNum;
	private final String fileName;

	private static final Logger LOGGER = LoggerFactory
			.getLogger(FibonacciPartThread.class);
	/**
	 * count number
	 */
	private final static AtomicLong count = new AtomicLong();
	/**
	 * sum the float
	 */
	private final static AtomicFloat sum = new AtomicFloat();

	public FibonacciPartThread(int start, int countNum, String fileName) {
		this.start = start;
		this.countNum = countNum;
		this.fileName = fileName;
	}

	public void run() {
		execute();
	}

	/**
	 * Execute and parsing. This is a core function.
	 */
	public void execute() {
		FibonacciCoreCalucaltion core = new FibonacciCoreCalucaltion();
		core.startRun(start, countNum, fileName);
	}

	public static int getCountThread(int calToNumCount) {
		int threadNum = calToNumCount / Consts.MAX_THREADHOLD;
		if (calToNumCount % Consts.MAX_THREADHOLD == 0)
			return threadNum;
		else
			return threadNum + 1;
	}

	/**
	 * get the start index and end index.
	 * 
	 * @param threadIndex
	 *            start from 1
	 * @param calToNumCount
	 * @return
	 */
	public static int[] getStartEndIndex(int threadIndex, int totalThreadNum,
			int calToNumCount) {
		int[] data = new int[2];
		if (threadIndex < totalThreadNum) {
			data[0] = (threadIndex - 1) * Consts.MAX_THREADHOLD + 1;
			data[1] = threadIndex * Consts.MAX_THREADHOLD;
		} else if (threadIndex == totalThreadNum) {
			data[0] = (threadIndex - 1) * Consts.MAX_THREADHOLD + 1;
			data[1] = calToNumCount;
		}
		return data;
	}

	public static void main(String[] args) throws java.io.IOException {
		long startTime = System.currentTimeMillis();
		// use apache-cli.jar as dependency could be good choice to format the
		// command line coding.
		if (args.length < 1) {
			System.out.println("Usage: <first #N> [file_prefix]");
			System.exit(1);
		}
		int calToNumCount = Integer.valueOf(args[0]);
		String folderName = "";
		if (args.length >= 2) {
			folderName = args[1];
		} else {
			String name = ManagementFactory.getRuntimeMXBean().getName();
			folderName = name.split("@")[0];
		}
		int threadNum = getCountThread(calToNumCount);

		ExecutorService pool = Executors.newFixedThreadPool(calToNumCount);

		try {
			Files.createDirectories(Paths.get(folderName));
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}

		for (int i = 0; i < threadNum; i++) {
			int[] data = getStartEndIndex(i + 1, threadNum, calToNumCount);
			pool.execute(new FibonacciPartThread(data[0], data[1], folderName
					+ "/" + "f.p" + i));
		}

		pool.shutdown();
		try {
			pool.awaitTermination(1, TimeUnit.DAYS);
		} catch (InterruptedException e) {
			LOGGER.info("Pool interrupted!");
			System.exit(1);
		}

		long endTime = System.currentTimeMillis();
		// add duration.
		LOGGER.info(String.format("Total execution time %d ms.",
				(endTime - startTime)));
	}

	/**
	 * @return the count
	 */
	public static long getCount() {
		return count.get();
	}

	/**
	 * @return the sum
	 */
	public static float getSum() {
		return sum.get();
	}

}