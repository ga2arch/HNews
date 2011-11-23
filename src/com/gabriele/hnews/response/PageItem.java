package com.gabriele.hnews.response;

import java.net.MalformedURLException;
import java.net.URL;

public class PageItem {

	public String title;
	public String url;
	public int id;
	public int commentCount;
	public int points;
	public String postedAgo;
	public String postedBy;
	
	public String getDomain() {
		String domain = null;
		try {
			domain = new URL(url).getHost();
		} catch (MalformedURLException e) {
			domain = "news.ycombinator.com";
		}
		return domain;
	}
	
}
