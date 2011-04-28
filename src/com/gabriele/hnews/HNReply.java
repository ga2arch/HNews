package com.gabriele.hnews;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.markupartist.android.widget.ActionBar;
import com.markupartist.android.widget.ActionBar.IntentAction;
import com.markupartist.android.widget.actionbar.R;

public class HNReply extends Activity {
	
	private HNApplication HNApp;
	private SharedPreferences prefs;
	private String replyId;
	private String postId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.subreplyitem);
		
		HNApp = (HNApplication)getApplicationContext();
		
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		ActionBar actionBar = (ActionBar) findViewById(R.id.actionbar);
	    actionBar.setTitle("Replies");
	    actionBar.setHomeAction(new IntentAction(this, HNApp.createIntent(this), R.drawable.ic_title_home_default));
	    actionBar.addAction(mAddAction);
	    
		Intent intent = getIntent();
		
		replyId = intent.getStringExtra("replyId");
		HNReplyItem reply = HNApp.getItemById(replyId);
		postId = reply.getPostId();
		
		JSONArray replyReplies = HNApp.getRepliesById(replyId);
		
		List<HNReplyItem> data = new ArrayList<HNReplyItem>();
		
		data.add(reply);
		
		for(int i=0; i<replyReplies.length(); i++) {
			HNReplyItem replyItem = new HNReplyItem();
			
			try {
				JSONObject singleReply = replyReplies.getJSONObject(i);
				JSONArray subReplies = singleReply.getJSONArray("children");
				String singleReplyId = singleReply.getString("id");
				
				HNApp.setReplies(singleReplyId, subReplies);
				
				replyItem.setAuthor(singleReply.getString("postedBy"));
				replyItem.setContent(singleReply.getString("comment"));
				replyItem.setNumReply(Integer.toString(subReplies.length()));
				replyItem.setPoints(singleReply.getString("points"));
				replyItem.setTime(singleReply.getString("postedAgo"));
				replyItem.setReplyId(singleReplyId);
				replyItem.setPostId(singleReply.getString("postId"));
				
				HNApp.setItem(singleReplyId, replyItem);
				data.add(replyItem);
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		ListView lv = (ListView)findViewById(R.id.replies);
		registerForContextMenu(lv);
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(arg2 == 0) 
					return;
				
				TextView tvReplyId = (TextView)arg1.findViewById(R.id.replyId);
				TextView tvNumReply = (TextView)arg1.findViewById(R.id.numReply);
					
				Intent intent = new Intent(getApplicationContext(), HNReply.class);
				intent.putExtra("replyId", (String)tvReplyId.getText());
				intent.putExtra("replyNumReply", (String)tvNumReply.getText());
				
				startActivity(intent);
			}
		});
		
		HNRepliesAdapter adapter = new HNRepliesAdapter(this, R.layout.replyitem, R.id.content, data, true);
		lv.setAdapter(adapter);
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_news_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.prefs:
			Intent intent = new Intent(getBaseContext(), HNPreferences.class);
			startActivity(intent);
			return true;
		case R.id.credits:
			Toast.makeText(this, "Thanks to ronnieroller.com for the hackernews api, newsyc.me for the ui idea and johannilsson for the actionbar lib", Toast.LENGTH_LONG).show();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_action_menu, menu);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	RelativeLayout parent = ((RelativeLayout) info.targetView);
    	TextView tvId = (TextView) parent.findViewById(R.id.replyId);
    	String replyId = tvId.getText().toString();
    	TextView tvReplyContent = (TextView) parent.findViewById(R.id.content);
    	String text = tvReplyContent.getText().toString();
		switch(item.getItemId()) {
		case R.id.up:
			new HNVote().execute("up", replyId);
			return true;
		case R.id.down:
			new HNVote().execute("down", replyId);
			return true;
		case R.id.share:
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent.putExtra(Intent.EXTRA_TEXT, text);
			startActivity(Intent.createChooser(shareIntent, "Share"));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	final MyAction mAddAction = new MyAction() {
    	@Override
    	public int getDrawable() {
    		return R.drawable.ic_title_add_default;
    	}
    	
    	@Override
    	public void performAction(View view) {
    		final Dialog dialog = new Dialog(HNReply.this);
    		dialog.setContentView(R.layout.reply_dialog);
    		dialog.setTitle("Reply");
    		
    		Button btSubmit = (Button)dialog.findViewById(R.id.submit);
    		btSubmit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					EditText etText = (EditText) dialog.findViewById(R.id.text);
					String text = etText.getText().toString();
					new HNSubmit().execute(replyId, postId, text);
					dialog.dismiss();
				}
    		});
    		
    		Button btCancel = (Button) dialog.findViewById(R.id.cancel);
    		btCancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
    		});
    		
    		dialog.show();
    	}
    };
	
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
    
    
	
	private class HNVote extends AsyncTask<String, String, Integer> {

		@Override
		protected Integer doInBackground(String... args) {
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpContext ctx = HNApp.getCookie();
				
				if(ctx == null) {
					publishProgress("Loggin in ...");
					ctx = HNApp.login();
					if(ctx == null) {
						publishProgress("Login error");
						return 3;
					}
				}				
				
				HttpGet grequest = new HttpGet();
				String voteUrl = "http://news.ycombinator.com/vote?for=" + args[1]
							   + "&dir=" + args[0] + "&whence=item?id=" + postId;
				grequest.setURI(new URI(voteUrl));
				String content = httpClient.execute(grequest, mResponseHandler, ctx);
				if(content.equals("Can't make that vote.")) {
					publishProgress(content);
					return 3;
				}
				
				httpClient.getConnectionManager().shutdown();
			} catch(Exception e) {
				e.printStackTrace();
				return 2;
			}
			if(args[0] == "up")
				return 0;
			else
				return 1;
		}
		
		protected void onProgressUpdate(String... error) {
			Toast.makeText(HNReply.this, error[0], Toast.LENGTH_SHORT).show();
		}
		
		protected void onPostExecute(Integer arg) {
			switch(arg) {
			case 0:
				Toast.makeText(getApplicationContext(), "Upvoted", Toast.LENGTH_SHORT).show();
				return;
			case 1:
				Toast.makeText(getApplicationContext(), "Downvoted", Toast.LENGTH_SHORT).show();
				return;
			case 2:
				Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
				return;
			}
		}
    }
	
	private class HNSubmit extends AsyncTask<String, String, Integer> {

		@Override
		protected Integer doInBackground(String... args) {
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpContext ctx = HNApp.getCookie();
				
				if(ctx == null) {
					publishProgress("Loggin in ...");
					ctx = HNApp.login();
					if(ctx == null) {
						publishProgress("Login error");
						return 1;
					}
				}
				
				HttpGet grequest = new HttpGet();
				String url = "http://news.ycombinator.com/reply?id=" + args[0]+ "&whence=item?id=" + args[1];
				//System.out.println(url);
				grequest.setURI(new URI(url));
				String content = httpClient.execute(grequest, mResponseHandler, ctx);
				//System.out.println(content);
				Document doc = Jsoup.parse(content);
				String fnid = doc.getElementsByTag("input").first().attr("value");
				//System.out.println(fnid);
				
				HttpPost prequest = new HttpPost();
				prequest.setURI(new URI("http://news.ycombinator.com/r"));
				List<NameValuePair> nvp = new ArrayList<NameValuePair>();
				nvp.add(new BasicNameValuePair("fnid", fnid));
				nvp.add(new BasicNameValuePair("text", args[2]));
				//System.out.println(nvp.toString());
				prequest.setEntity(new UrlEncodedFormEntity(nvp));
				content =  httpClient.execute(prequest, mResponseHandler, ctx);
				
				doc = Jsoup.parse(content);
				String error = doc.select("span[class=admin]").text();
				httpClient.getConnectionManager().shutdown();

				if(!error.equals("")) {
					publishProgress(error);
					return 1;
				}
				else
					return 0;
			} catch(Exception e) {
				e.printStackTrace();
				return 2;
			}
		}
    	
		protected void onProgressUpdate(String... error) {
			Toast.makeText(HNReply.this, error[0], Toast.LENGTH_SHORT).show();
		}
		
		protected void onPostExecute(Integer arg) {
			switch(arg) {
			case 0:
				Toast.makeText(getApplicationContext(), "Submitted", Toast.LENGTH_SHORT).show();
				return;
			case 2:
				Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
				return;
			}
			
		}
	}
}
