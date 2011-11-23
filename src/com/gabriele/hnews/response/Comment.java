package com.gabriele.hnews.response;

import java.util.ArrayList;

public class Comment {

	public String postedBy;
	public String postedAgo;
	public String comment;
	public int id;
	public int points;
	public int parentId;
	public int postId;
	public ArrayList<Comment> children;
	
}
