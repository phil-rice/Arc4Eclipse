package org.softwareFm.utilities.strings;

import java.util.List;

import org.softwareFm.utilities.aggregators.IAggregator;
import org.softwareFm.utilities.functions.IFunction1;

public class Strings {
	public static <T> String join(Iterable<T> from, String separator) {
		StringBuilder builder = new StringBuilder();
		boolean addSeparator = false;
		for (T f : from) {
			if (addSeparator)
				builder.append(separator);
			builder.append(f);
			addSeparator = true;
		}
		return builder.toString();
	}

	public static String join(List<String> from, List<Integer> indicies, String separator) {
		StringBuilder builder = new StringBuilder();
		boolean addSeparator = false;
		for (int i = 0; i < indicies.size(); i++) {
			String f = from.get(indicies.get(i));
			if (addSeparator)
				builder.append(separator);
			builder.append(f);
			addSeparator = true;
		}
		return builder.toString();
	}

	public static String asData(Object value, int length) {
		if (value == null)
			return asData("", length);
		String valueAsString = value instanceof Double ? String.format("%" + length + ".3f", value) : value.toString();
		int deficit = length - valueAsString.length();
		if (deficit <= 0)
			return valueAsString;
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < deficit; i++)
			builder.append(' ');
		builder.append(valueAsString);
		return builder.toString();
	}

	public static String oneLine(Object object) {
		return nullSafeToString(object).replaceAll("\n", " ");
	}

	public static IAggregator<String, String> strJoin(final String separator) {
		return Strings.<String> join(separator);
	}

	public static <T> IAggregator<T, String> join(final String separator) {
		return new IAggregator<T, String>() {
			private final StringBuilder builder = new StringBuilder();
			private boolean addSeparator;

			@Override
			public void add(T t) {
				if (addSeparator)
					builder.append(separator);
				addSeparator = true;
				builder.append(t);
			}

			@Override
			public String result() {
				return builder.toString();
			}
		};

	}

	public static IFunction1<String, Integer> length() {
		return new IFunction1<String, Integer>() {
			@Override
			public Integer apply(String from) throws Exception {
				return from.length();
			}
		};
	}

	public static IFunction1<Object, String> toStringFn() {
		return new IFunction1<Object, String>() {
			@Override
			public String apply(Object from) throws Exception {
				return from.toString();
			}
		};
	}

	public static String bracket(String raw, String brackets) {
		assert brackets.length() == 2;
		return brackets.charAt(0) + raw + brackets.charAt(1);
	}

	public static String quote(String raw) {
		return '"' + raw + '"';
	}

	public static String onlyKeep(String raw, String chars) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < raw.length(); i++) {
			char ch = raw.charAt(i);
			if (chars.indexOf(ch) != -1)
				builder.append(ch);
		}
		return builder.toString();
	}

	public static IFunction1<String, Boolean> startsWith(final String prefix) {
		return new IFunction1<String, Boolean>() {
			@Override
			public Boolean apply(String from) throws Exception {
				return from.startsWith(prefix);
			}
		};
	}

	public static String addToRollingLog(List<String> logger, int size, String separator, String value) {
		logger.add(0, value);
		if (logger.size() >= size)
			logger.remove(logger.size() - 1);
		return join(logger, separator);
	}

	public static String nullSafeToString(Object value) {
		if (value == null)
			return "";
		else
			return value.toString();
	}

	public static boolean safeEquals(String string1, String string2) {
		if (string1 == string2)
			return true;
		if (string1 == null || string2 == null)
			return false;
		return string1.equals(string2);
	}

}
