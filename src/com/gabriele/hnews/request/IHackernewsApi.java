package com.gabriele.hnews.request;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;

public class IHackernewsApi {
	
	public final static String API_URL = "http://api.ihackernews.com/";
	public final static String API_PAGE = "page";
	public final static String API_NEW = "new";
	public final static String API_BY_USER = "by/";
	public final static String API_ASK = "ask";
	
	public final static String API_POST_INFO = "post/";
	
	public static void retrieve(Context context, String location, ResultReceiver receiver) {
		makeRequest(context, location, receiver);
	}
	
	private static void makeRequest(Context context, String url, ResultReceiver receiver) {
		Intent intent = new Intent(context, RequestService.class);
		intent.putExtra("url", API_URL + url);
		intent.putExtra("receiver", receiver);
		context.startService(intent);
	}

}
