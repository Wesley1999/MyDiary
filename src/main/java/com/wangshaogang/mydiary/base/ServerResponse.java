package com.wangshaogang.mydiary.base;

import com.wangshaogang.mydiary.base.consts.ResponseCode;
import lombok.Getter;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

@Getter
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable {
	private int status;
	private String msg;
	private T data;

	private ServerResponse(int status) {
		this.status = status;
	}

	private ServerResponse(int status, String msg) {
		this.status = status;
		this.msg = msg;
	}

	private ServerResponse(int status, T data) {
		this.status = status;
		this.data = data;
	}

	public static <T> ServerResponse<T> createSuccessResponse(T data) {
		return new ServerResponse<>(0, data);
	}

	public static <T> ServerResponse<T> createSuccessResponse() {
		return new ServerResponse<>(0);
	}

	public static <T> ServerResponse<T> createErrorResponse(Integer responseCode) {
		return new ServerResponse<T>(responseCode, ResponseCode.getResponseMessageByResponseCode(responseCode));
	}

}
