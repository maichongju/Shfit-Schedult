package log;

public enum Log {
	WARNING("W. "), ERROR("E. "), INFO("I. ");

	private String value;

	private Log(String value) {
		this.value = value;
	}

	public String toString() {
		return value;
	}
}
