package com.webug.imgbed.common;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class RspEntity {

	private String rspCode;
	
	private String rspMsg;
	
	private String retCode;
	
	private Map<String, Object> rspData;

	public String getRspCode() {
		return rspCode;
	}

	public void setRspCode(String rspCode) {
		this.rspCode = rspCode;
	}

	public String getRspMsg() {
		return rspMsg;
	}

	public void setRspMsg(String rspMsg) {
		this.rspMsg = rspMsg;
	}

	public String getRetCode() {
		return retCode;
	}

	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}

	public Map<String, Object> getRspData() {
		return rspData;
	}

	public void setRspData(Map<String, Object> rspData) {
		this.rspData = rspData;
	}
	
}
