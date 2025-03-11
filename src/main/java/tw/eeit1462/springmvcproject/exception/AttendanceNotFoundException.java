package tw.eeit1462.springmvcproject.exception;

public class AttendanceNotFoundException extends RuntimeException {
	public AttendanceNotFoundException(String message) {
		super(message);
	}
}
