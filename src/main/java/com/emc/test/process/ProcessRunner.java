package com.emc.test.process;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.emc.test.common.Consts;
import com.emc.test.common.SystemException;
import com.emc.test.common.utils.ConfigurationUtils;

public class ProcessRunner {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ProcessRunner.class);
	private static final String DebugTask = System.getenv("DEBUG_TASK");
	private static final String PATH_SEPARATOR = "path.separator";

	private ProcessRunner() {

	}

	private static void logProcessParameters(String[] args) {
		LOGGER.info("Start ProcessRunner with arguments:<");
		for (String arg : args) {
			LOGGER.debug(arg);
		}
		LOGGER.info(">");
	}

	/**
	 * Run another JVM with given arguments. stdout will be handled by
	 * {@link IEngineOuputHandler} and stderr will be inheriting from this JVM.
	 * 
	 * @param stdOutFile
	 *            - the System.out redirection file path. If it is null, stdout
	 *            will inherit from this JVM.
	 * @param outputHanlder
	 *            the customized output handler, if it is not specified, a
	 *            default one {@link ProcessOuputHandler} will be created
	 * @param timeout
	 *            - the timeout in milliseconds of current run
	 * @param args
	 *            - The arguments that will be passed to java.exe command line.
	 */
	public static void run(long timeout, String[] args) {
		logProcessParameters(args);

		ProcessBuilder builder = new ProcessBuilder(getProcessParameters(args));

		Timer timer = new Timer();
		try {
			builder.redirectError(Redirect.INHERIT);

			final Process process = builder.start();

			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					LOGGER.error("Running process timeout.");
					process.destroy();
				}
			}, timeout);

			// depends on the implementation of output handler, this method may
			// not be executed until process is
			// terminated if the handler blocks the I/O
			process.waitFor();

			handleProcessReturnState(process);
		} catch (IOException | InterruptedException ex) {
			LOGGER.error("Error running process.", ex);
		} finally {
			timer.cancel();
		}
	}

	/**
	 * Connecting two string array to one.
	 * 
	 * @param first
	 *            - The first array, will appear first in the result.
	 * @param second
	 *            - The second array.
	 * @return
	 */
	public static String[] concat(String[] first, String[] second) {
		String[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	private static String[] getProcessParameters(String[] args) {
		String[] params = new String[] { getJavaExecutable(), "-classpath",
				System.getProperty("java.class.path") };
		if (DebugTask != null && args[1].endsWith(DebugTask)) {
			params = concat(
					params,
					new String[] { "-Xdebug",
							"-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000" });
		}
		String processRunnerArgLine = System
				.getProperty("process.runner.argLine");
		if (processRunnerArgLine != null && processRunnerArgLine.length() > 0) {
			params = concat(params, new String[] { processRunnerArgLine });
		}

		// add jvm parameters from user config
		params = concat(params, getDefaultJVMParameters());

		return concat(params, args);
	}

	private static String getJavaExecutable() {
		String javaHome = System.getProperty("java.home");
		String javaExe = javaHome + File.separator + "bin" + File.separator
				+ "java";
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("windows")) {
			javaExe += ".exe";
		}
		return javaExe;
	}

	private static String getClassPath() {
		StringBuilder builder = new StringBuilder();
		for (URL url : ((URLClassLoader) (Thread.currentThread()
				.getContextClassLoader())).getURLs()) {
			builder.append(new File(url.getPath()));
			builder.append(System.getProperty(PATH_SEPARATOR));
		}
		String classpath = builder.toString();
		int toIndex = classpath.lastIndexOf(System.getProperty(PATH_SEPARATOR));
		return classpath.substring(0, toIndex);
	}

	private static String[] getDefaultJVMParameters() {
		String configValue = "-Xmx512m";

		configValue = ConfigurationUtils.getValue(
				Consts.PROPERTIES_KEY_JVMMAXHEAPSIZE, configValue);
		String[] parameters = new String[] { configValue };
		return parameters;
	}

	/**
	 * Checks the process return state and throws {@link BusinessException} with
	 * key of {@code error.business.task.execution.failure} if the exit code is
	 * not 0.
	 * <p>
	 * Meanwhile, it also throws {@link IllegalThreadStateException} runtime
	 * exception is the process has not yet terminated.
	 * 
	 * @param process
	 *            the process
	 * @see Process#exitValue();
	 */
	private static void handleProcessReturnState(Process process) {
		int exitcode = process.exitValue();
		if (exitcode != 0) {
			// according to the test, it seems the exit code is 1 if timeout
			if (process.getOutputStream() != null) {
				try {
					InputStream stderr = process.getInputStream();
					InputStreamReader isr = new InputStreamReader(stderr);
					BufferedReader br = new BufferedReader(isr);
					String line = null;
					while ((line = br.readLine()) != null) {
						LOGGER.error(line);

					}
				} catch (Exception e) {
					LOGGER.error(e.getMessage(), e);
				}
			}
			throw new SystemException(
					"error.system.task.execution.failure",
					"Failed to execute task. It is due to either timeout or abnormal return. The process exit code is "
							+ exitcode);

		}

	}
}
