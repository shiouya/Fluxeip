package com.example.fluxeip.exception;

public class AttendanceTodayNotFoundException extends RuntimeException {
	public AttendanceTodayNotFoundException(String message) {
		super(message);
	}
}
