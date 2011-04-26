package com.gabriele.hnews;

import android.text.Html;
import android.text.Spanned;

public class HNReplyItem {
	private String author;
	private String content;
	private String time;
	private String points;
	private String numReply;
	private String replyId;
	private String postId;
	
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getAuthor() {
		return author;
	}
	public void setContent(String content) {
		Spanned raw = Html.fromHtml(content);
		this.content = raw.toString();
	}
	public String getContent() {
		return content;
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
	public void setNumReply(String numReply) {
		this.numReply = numReply;
	}
	public String getNumReply() {
		return numReply;
	}
	public void setReplyId(String replyId) {
		this.replyId = replyId;
	}
	public String getReplyId() {
		return replyId;
	}
	public void setPostId(String postId) {
		this.postId = postId;
	}
	public String getPostId() {
		return postId;
	}
}
