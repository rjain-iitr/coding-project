package com.calendly.model;

import java.time.LocalDateTime;

public class Availability {
    private String id;
    private String userId;
    private LocalDateTime end;
    private LocalDateTime start;
    public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
    public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
    public LocalDateTime getStart() {
		return start;
	}
	public void setStart(LocalDateTime start) {
		this.start = start;
	}
	
	public LocalDateTime getEnd() {
		return end;
	}
	public void setEnd(LocalDateTime end) {
		this.end = end;
	}

   
}
