package com.gabriele.hnews.response;

import java.io.Serializable;
import java.util.ArrayList;

public class Comment implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public String postedBy;
	public String postedAgo;
	public String comment;
	public int id;
	public int points;
	public int parentId;
	public int postId;
	public ArrayList<Comment> children;
	
}
