package utils;

import java.io.PrintStream;

/**
 * This Logging-Class is used by this Compositor-Project so if you want to show
 * some debug-information just set the active-variable to <code>true</code>.
 * 
 * @author Roland Wallner
 *
 */
public final class Logging {

	public static boolean active = false;

	public enum LogLevel {
		SEVERE, INFO, WARNING
	}

	private static PrintStream logger = System.out;

	public static void log(LogLevel level, String msg) {
		log(level, msg, "Unknown");
	}

	public static void log(LogLevel level, String msg, String from) {
		if (active)
			logger.println("[" + from + "] [" + level.toString() + "] " + msg);
	}

}
