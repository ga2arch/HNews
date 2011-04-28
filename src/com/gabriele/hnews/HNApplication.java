package com.gabriele.hnews;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.RedirectHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class HNApplication extends Application {
	
	private Map<String, HNewsItem> postsArray = new HashMap<String, HNewsItem>();
	private Map<String, JSONArray> repliesArray = new HashMap<String, JSONArray>();
	private Map<String, HNReplyItem> itemsArray = new HashMap<String, HNReplyItem>(); 
	private HttpContext ctx = null;
	
	public JSONArray getRepliesById(String id) {
		return repliesArray.get(id);
	}
	
	public void setReplies(String id, JSONArray replies) {
		this.repliesArray.put(id, replies);
	}

	public void setPost(String id, HNewsItem post) {
		this.postsArray.put(id, post);
	}

	public HNewsItem getPostById(String id) {
		return postsArray.get(id);
	}
	
	public void setItem(String id, HNReplyItem item) {
		this.itemsArray.put(id, item);
	}
	
	public HNReplyItem getItemById(String id) {
		return itemsArray.get(id);
	}

	public Intent createIntent(Context context) {
        Intent i = new Intent(context, HNews.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return i;
    }
    
    public Intent createShareIntent() {
        final Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Shared from the ActionBar widget.");
        return Intent.createChooser(intent, "Share");
    }
    
    private final ResponseHandler<String> mResponseHandler = new ResponseHandler<String>() {
    	@Override
    	public String handleResponse(HttpResponse response) 
    		throws ClientProtocolException, IOException{
    		InputStream content = response.getEntity().getContent();
    		byte[] buffer = new byte[1024];
    		int numRead = 0;
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		while((numRead=content.read(buffer)) != -1) {
    			baos.write(buffer, 0, numRead);
    		}
    		content.close();
    		String result = new String(baos.toByteArray());
    		return result;
    	}
    };
    
    public HttpContext getCookie() {
    	return ctx;
    }
    
    public HttpContext login() {
    	try {
    		//Log.i("LOGIN", "YES");
    		
    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			String username = prefs.getString("username", null);
			String password = prefs.getString("password", null);
			
			if(username == null || password == null)
				return null;
    		
    		DefaultHttpClient httpClient = new DefaultHttpClient();
			httpClient.setRedirectHandler(new RedirectHandler() {

				@Override
				public URI getLocationURI(HttpResponse response,
						HttpContext context) throws ProtocolException {
					return null;
				}

				@Override
				public boolean isRedirectRequested(HttpResponse response,
						HttpContext context) {
					return false;
				}
				
			});
			
			CookieStore cookieStore = new BasicCookieStore();
			ctx = new BasicHttpContext();
			ctx.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
			
			HttpGet grequest = new HttpGet("http://news.ycombinator.com/");
			String content = httpClient.execute(grequest, mResponseHandler, ctx);
			
			Document doc = Jsoup.parse(content);
			String gLoginUrl = doc.select("a[href~=\\/x\\?fnid=(.+)]").first().attr("href");
			
			grequest.setURI(new URI("http://news.ycombinator.com" + gLoginUrl));
			content = httpClient.execute(grequest, mResponseHandler, ctx);
			
			doc = Jsoup.parse(content);
			String fnid = doc.getElementsByTag("input").first().attr("value");
			
			HttpPost prequest = new HttpPost("http://news.ycombinator.com/y");
			List<NameValuePair> nvp = new ArrayList<NameValuePair>();
			nvp.add(new BasicNameValuePair("fnid", fnid));
			nvp.add(new BasicNameValuePair("u", username));
			nvp.add(new BasicNameValuePair("p", password));
			
			prequest.setEntity(new UrlEncodedFormEntity(nvp));
			HttpResponse response = httpClient.execute(prequest, ctx);
			try{
				response.getHeaders("Set-Cookie")[0].getName();
			}catch(Exception e){
				ctx = null;
			}
			return ctx;
    	} catch(Exception e ){
    		e.printStackTrace();
    		return null;
    	}
    }

}
