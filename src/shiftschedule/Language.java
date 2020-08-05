package shiftschedule;

/**
 * 
 * Use <a href="https://en.wikipedia.org/wiki/List_of_ISO_639-1_codes">ISO
 * 639-1</a>
 * 
 * @author Chongju Mai
 *
 */
public enum Language {

	DEFAULT, ZH, ENG;

	/**
	 * Convert the string to Language
	 * 
	 * @param value
	 * @return
	 */
	public static Language getLanguage(String value) {
		for (Language lang : values()) {
			if (lang.name().equals(value.toUpperCase())) {
				return lang;
			}
		}
		return DEFAULT;
	}

	public String toString() {
		return name();
	}
}
