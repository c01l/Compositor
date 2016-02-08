package utils;

import java.io.PrintStream;

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
