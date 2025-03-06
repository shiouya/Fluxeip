package com.example.Fluxeip.dto;

public class LoginResponse {
	private Boolean success;
	private String message;

	private String token;
	private Integer employeeId;

	@Override
	public String toString() {
		return "LoginResponse [success=" + success + ", message=" + message + ", token=" + token + ", employeeId="
				+ employeeId + "]";
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
