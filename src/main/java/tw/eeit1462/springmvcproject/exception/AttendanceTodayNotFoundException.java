package tw.eeit1462.springmvcproject.exception;

public class AttendanceTodayNotFoundException extends RuntimeException {
	public AttendanceTodayNotFoundException(String message) {
		super(message);
	}
}
