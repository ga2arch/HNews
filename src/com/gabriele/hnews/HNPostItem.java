package com.gabriele.hnews;

import java.io.Serializable;
import java.util.List;

public class HNPostItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String title;
	private String author;
	private String time;
	private String points;
	private String content;
	private List<HNReplyItem> comments;
	
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getAuthor() {
		return author;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getTime() {
		return time;
	}
	public void setPoints(String points) {
		this.points = points;
	}
	public String getPoints() {
		return points;
	}
	public void setComments(List<HNReplyItem> comments) {
		this.comments = comments;
	}
	public List<HNReplyItem> getComments() {
		return comments;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getContent() {
		return content;
	}
}
