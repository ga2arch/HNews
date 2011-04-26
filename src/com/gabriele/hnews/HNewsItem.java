package com.gabriele.hnews;

import java.io.Serializable;

public class HNewsItem implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -881257977519364936L;
	private String title;
	private String author;
	private String time;
	private String points;
	private String comments;
	private String url;
	private String domain;
	private String postId;
	private String content;
	
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getAuthor() {
		return author;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getTitle() {
		return title;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getTime() {
		return time;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getComments() {
		return comments;
	}
	public void setPoints(String points) {
		this.points = points;
	}
	public String getPoints() {
		return points;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUrl() {
		return url;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getDomain() {
		return domain;
	}
	public void setPostId(String postId) {
		this.postId = postId;
	}
	public String getPostId() {
		return postId;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getContent() {
		return content;
	}
	
	
}
