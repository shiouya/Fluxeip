package tw.eeit1462.springmvcproject.exception;

public class EmployeeNotFoundException extends RuntimeException {
	public EmployeeNotFoundException(String message) {
		super(message);
	}
}
