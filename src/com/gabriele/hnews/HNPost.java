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
import org.apache.http.client.HttpClient;
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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.Spanned;
import android.text.util.Linkify;
import android.view.ContextMenu;
import android.view.LayoutInflater;
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

public class HNPost extends Activity{
	
	private HNApplication HNApp;
	private ListView lv;
	private ActionBar actionBar;
	private HNewsItem post;
	private String postId;
	private List<HNReplyItem> replies = new ArrayList<HNReplyItem>();
	private HNRepliesAdapter adapter;
	private View postView;
	private SharedPreferences prefs;
	private String type;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.postitem);
		
		HNApp = (HNApplication)getApplicationContext();
		
		prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		actionBar = (ActionBar) findViewById(R.id.actionbar);
	    actionBar.setTitle("Comments");
	    actionBar.setHomeAction(new IntentAction(this, HNApp.createIntent(this), R.drawable.ic_title_home_default));
	    actionBar.addAction(mUpdateAction);
	    actionBar.addAction(mAddAction);
	    
		Intent intent = getIntent();
		
		if(Intent.ACTION_VIEW.equals(intent.getAction()))
			postId = intent.getData().getQueryParameter("id");
		else
			postId = intent.getStringExtra("postId");
		
		type = intent.getStringExtra("type");
		
		if(savedInstanceState != null)
			post = (HNewsItem) savedInstanceState.getSerializable("post");
		else
			post = HNApp.getPostById(postId);
		
		String postTitle = post.getTitle(); 
		String postAuthor = post.getAuthor(); 
		String postTime = post.getTime();
		String postPoints = post.getPoints();
		String postUrl = post.getUrl();
		
		LayoutInflater mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		postView = mInflater.inflate(R.layout.postview, null);
	
		TextView tvTitle = (TextView)postView.findViewById(R.id.title);
		TextView tvAuthor = (TextView)postView.findViewById(R.id.author);
		TextView tvTime = (TextView)postView.findViewById(R.id.time);
		TextView tvPoints = (TextView)postView.findViewById(R.id.points);
		TextView tvUrl = (TextView)postView.findViewById(R.id.url);
		
		tvTitle.setText(postTitle);
		tvAuthor.setText(postAuthor);
		tvTime.setText(postTime);
		tvPoints.setText(postPoints + " points");
		tvUrl.setText(postUrl);
		
		adapter = new HNRepliesAdapter(HNPost.this, R.layout.replyitem, R.id.author, replies, false);
		
        lv = (ListView)findViewById(R.id.comments);
        registerForContextMenu(lv);

        lv.addHeaderView(postView);

        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(arg2 == 0) {
					TextView tvUrl = (TextView)arg1.findViewById(R.id.url);
					String url = tvUrl.getText().toString();
					if(url.startsWith("/comments"))
						url = "http://news.ycombinator.com/item?id=" + url.split("/")[2];						
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse(url));
					startActivity(intent);
				} else {
					TextView tvReplyId = (TextView)arg1.findViewById(R.id.replyId);
					Intent intent = new Intent(getApplicationContext(), HNReply.class);
					intent.putExtra("replyId", (String)tvReplyId.getText());
					startActivity(intent);
				}
			}	
        });
        new getJson().execute();
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putSerializable("post", post);
		super.onSaveInstanceState(savedInstanceState);
	}
	
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options_action_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.up:
			new HNVote().execute("up", type);
			return true;
		case R.id.down:
			new HNVote().execute("down", type);
			return true;
		case R.id.prefs:
			Intent intent = new Intent(getBaseContext(), HNPreferences.class);
			startActivity(intent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}*/
	
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
    	super.onCreateContextMenu(menu, v, menuInfo);
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.options_action_menu, menu);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	String replyId = null;
    	String text = null;
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	RelativeLayout parent = ((RelativeLayout) info.targetView);
    	TextView tvReplyId = (TextView) parent.findViewById(R.id.replyId);
    	TextView tvReplyContent = (TextView) parent.findViewById(R.id.content);
    	TextView tvPostUrl = (TextView) parent.findViewById(R.id.url);
    	if(tvReplyId != null) {
    		replyId = tvReplyId.getText().toString();
    		text = tvReplyContent.getText().toString();
    	} else 
    		text = tvPostUrl.getText().toString();
    	switch(item.getItemId()) {
		case R.id.up:
			if(replyId != null)
				new HNVote().execute("reply", "up", replyId);
			else
				new HNVote().execute("post", "up", type);
			return true;
		case R.id.down:
			if(replyId != null)
				new HNVote().execute("reply", "down", replyId);
			else
				new HNVote().execute("post", "down", type);
			return true;
		case R.id.share:
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent.putExtra(Intent.EXTRA_TEXT, text);
			startActivity(Intent.createChooser(shareIntent, "Share"));
			return true;
    	default:
    		return super.onContextItemSelected(item);
    	}
    }
	
	private void parseJson(String raw) {
    	try {
			JSONObject json = new JSONObject(raw);
			
			String text = json.getString("text");
			if(!text.equals("")) {
				TextView tvContent = (TextView)postView.findViewById(R.id.content);
				tvContent.setAutoLinkMask(Linkify.ALL);
				Spanned content = Html.fromHtml(text);
				tvContent.setText(content);
				tvContent.setVisibility(View.VISIBLE);
				adapter.notifyDataSetChanged();
			}
			
			JSONArray replies = json.getJSONArray("comments");
			for(int i=0; i<replies.length(); i++) {
				HNReplyItem comment = new HNReplyItem();
				
				JSONObject singleReply = replies.getJSONObject(i);
				JSONArray subReplies = singleReply.getJSONArray("children");
				
				String commentId = singleReply.getString("id");
				if(subReplies != null) {
					HNApp.setReplies(commentId, subReplies);
				}
				
				comment.setReplyId(commentId);
				comment.setAuthor(singleReply.getString("postedBy"));
				comment.setPoints(singleReply.getString("points"));
				comment.setTime(singleReply.getString("postedAgo"));
				comment.setContent(singleReply.getString("comment"));
				comment.setNumReply(Integer.toString(subReplies.length()));
				comment.setPostId(postId);
				
				HNApp.setItem(commentId, comment);
				adapter.add(comment);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
    }
    
    final MyAction mUpdateAction = new MyAction() {
    	@Override
    	public int getDrawable() {
    		return R.drawable.ic_title_refresh_default;
    	}
    	
    	@Override
    	public void performAction(View view) {
    		new getJson().execute();
    		lv.setSelectionAfterHeaderView();
    	}
    };
    
    final MyAction mAddAction = new MyAction() {
    	@Override
    	public int getDrawable() {
    		return R.drawable.ic_title_add_default;
    	}
    	
    	@Override
    	public void performAction(View view) {
    		final Dialog dialog = new Dialog(HNPost.this);
    		dialog.setContentView(R.layout.reply_dialog);
    		dialog.setTitle("Reply");
    		
    		Button btSubmit = (Button)dialog.findViewById(R.id.submit);
    		btSubmit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					EditText etText = (EditText) dialog.findViewById(R.id.text);
					
					String text = etText.getText().toString();
					
					new HNSubmit().execute(text);
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
	
    private class getJson extends AsyncTask<Void, String, Integer> {
    	
    	protected void onPreExecute () {
    		actionBar.setTitle("Loading ...");
    	}

    	protected Integer doInBackground(Void... args) {
			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet request = new HttpGet();
				URI targetUri = new URI("http://api.ihackernews.com/post/" + postId);
				request.setURI(targetUri);
				String result = httpClient.execute(request, mResponseHandler);
				publishProgress(result);
				return 0;
			} catch(Exception e) {
				e.printStackTrace();
				return 1;
			}
		}
    	
    	protected void onProgressUpdate(String... result) {
    		parseJson(result[0]);
    	}
    	
    	protected void onPostExecute(Integer v) {
    		if(v == 0) {
    			actionBar.setTitle("HackerNews");
    			adapter.notifyDataSetChanged();
    		} else {
    			actionBar.setTitle("No Internet Connection");
    		}
    	}
    };
    
    private class HNVote extends AsyncTask<String, String, Integer> {

		@Override
		protected Integer doInBackground(String... args) {
			try {
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpContext ctx = HNApp.getCookie();
				
				if(ctx == null) {
					publishProgress("Login Error");
					return 3;
				}
				
				HttpGet grequest = new HttpGet();
				String voteUrl;
				
				if(args[0] == "post")
					voteUrl = "http://news.ycombinator.com/vote?for=" + postId 
							   + "&dir=" + args[1] + "&whence=" + args[2];
				else
					voteUrl = "http://news.ycombinator.com/vote?for=" + args[2]
					   + "&dir=" + args[1] + "&whence=item?id=" + postId;
				
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
			if(args[1] == "up")
				return 0;
			else
				return 1;
		}
		
		protected void onProgressUpdate(String... error) {
			Toast.makeText(HNPost.this, error[0], Toast.LENGTH_SHORT).show();
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
					publishProgress("Login Error");
					return 1;
				}
				
				HttpGet grequest = new HttpGet();
				grequest.setURI(new URI("http://news.ycombinator.com/item?id=" + postId));
				String content = httpClient.execute(grequest, mResponseHandler, ctx);
				
				Document doc = Jsoup.parse(content);
				String fnid = doc.getElementsByTag("input").first().attr("value");
				//System.out.println(fnid);
				
				HttpPost prequest = new HttpPost();
				prequest.setURI(new URI("http://news.ycombinator.com/r"));
				List<NameValuePair> nvp = new ArrayList<NameValuePair>();
				nvp.add(new BasicNameValuePair("fnid", fnid));
				nvp.add(new BasicNameValuePair("text", args[0]));
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
			Toast.makeText(HNPost.this, error[0], Toast.LENGTH_SHORT).show();
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
