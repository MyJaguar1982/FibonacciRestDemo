package com.emc.test.fibonacci;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FibonacciCoreCalucaltion {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(FibonacciCoreCalucaltion.class);
	private BigInteger[][] matrix;

	public void startRun(int startNum, int size, String fileName) {
		init(2);
		matrix[0][0] = new BigInteger("1");
		matrix[0][1] = new BigInteger("1");
		matrix[1][0] = new BigInteger("1");
		matrix[1][1] = new BigInteger("0");
		BigInteger[][] temp = new BigInteger[matrix.length][matrix.length];
		PrintWriter out = null;
		try {
			out = new PrintWriter(new FileOutputStream(fileName));
			for (int i = startNum; i <= size; i = i + 3) {
				temp = pow(i);
				if (i <= size)
					out.println(temp[1][1]);
				if (i + 1 <= size)
					out.println(temp[1][0]);
				if (i + 2 <= size)
					out.println(temp[0][0]);
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}

	private void init(int n) {
		matrix = new BigInteger[n][n];
	}

	private BigInteger[][] matrixMulti(BigInteger[][] m, BigInteger[][] n) {
		BigInteger[][] temp = new BigInteger[matrix.length][matrix.length];

		for (int k = 0; k < matrix.length; k++) {
			for (int i = 0; i < matrix.length; i++) {
				for (int j = 0; j < matrix.length; j++) {
					if (temp[k][i] == null) {
						temp[k][i] = new BigInteger("0");
					}
					temp[k][i] = temp[k][i].add(m[k][j].multiply(n[j][i]));
				}
			}
		}
		return temp;
	}

	private BigInteger[][] pow(int n) {
		BigInteger[][] temp = new BigInteger[matrix.length][matrix.length];
		if (n == 1) {
			return matrix;
		} else {
			if (n % 2 != 0) {
				temp = pow((n - 1) / 2);
				temp = matrixMulti(temp, temp);
				return matrixMulti(temp, matrix);
			} else {
				temp = pow(n / 2);
				temp = matrixMulti(temp, temp);
				return temp;
			}
		}
	}
}
