package com.example.hotelBookingSystem.util;

import lombok.Data;

@Data
public class Result {

	private boolean flag;
	private String message;
	private Object data;

	public static Result success() {
		return result(true, null, null);
	}

	public static Result success(Object data) {
		return result(true, null, data);
	}

	public static Result fail() {
		return result(false, null, null);
	}

	public static Result fail(String message) {
		return result(false, message, null);
	}
	
	public static Result exception() {
		return result(false, "系统异常，请稍后再试", null);
	}

	public static Result result(boolean flag, String message, Object data) {
		Result result = new Result();
		result.setFlag(flag);
		result.setMessage(message);
		result.setData(data);
		return result;
	}

}
