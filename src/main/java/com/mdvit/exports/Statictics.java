package com.mdvit.exports;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="statictics")
public class Statictics implements Serializable {

	@XmlElement
	private Long totalTime;
	@XmlElement
	private Map<String, Object> data;
	@XmlElement
	private Exception exception;

	public Long getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(Long totalTime) {
		this.totalTime = totalTime;
	}

	public Map<String, Object> getData() {
		if (data == null) {
			data = new TreeMap<String, Object>();
		}
		return data;
	}

	public void setData(Map<String, Object> data) {
		this.data = data;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

	@Override
	public String toString() {
		return "Statictics [totalTime=" + totalTime + ", data=" + data
				+ ", exception=" + exception + "]";
	}

}
