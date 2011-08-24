package org.arc4eclipse.displayLists;

import java.text.MessageFormat;

public interface IEncodeDecodeNameAndUrl {

	NameAndValue fromString(String encoded);

	String toString(NameAndValue nameAndValue);

	public static class Utils {
		public static IEncodeDecodeNameAndUrl defaultEncoder() {
			return new IEncodeDecodeNameAndUrl() {

				@Override
				public NameAndValue fromString(String encoded) {
					if (encoded.trim().length() == 0)
						return null;
					int index = encoded.indexOf("$");
					if (index == -1)
						throw new IllegalArgumentException(MessageFormat.format(DisplayListsConstants.cannotDecodeString, encoded));
					String name = encoded.substring(0, index);
					String url = encoded.substring(index + 1);
					return new NameAndValue(name, url);
				}

				@Override
				public String toString(NameAndValue nameAndValue) {
					return nameAndValue.name.replaceAll("\\$", "") + "$" + nameAndValue.url;
				}
			};
		}
	}
}
