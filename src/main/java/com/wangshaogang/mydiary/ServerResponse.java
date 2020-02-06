/**
 * @author 王少刚
 * @create 2019-01-21 14:50
 */
package com.wangshaogang.mydiary;

import lombok.Getter;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

@Getter
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class ServerResponse<T> implements Serializable {
	private int status;
	private String msg;
	// 泛型数据对象，为了通用，可以返回不同类型
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

}
