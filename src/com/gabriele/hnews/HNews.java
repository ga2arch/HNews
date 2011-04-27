package com.gabriele.hnews;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
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
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
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

public class HNews extends Activity  {
	
	private HNApplication HNApp;
	private ListView lv;
	private ActionBar actionBar;
    private HNewsAdapter adapter;
    private SharedPreferences prefs;

    private String API_HOME = "http://api.ihackernews.com/page";
	private String API_NEW = "http://api.ihackernews.com/new";
	private String API_NEXT;
	
	private boolean homePage = true;
    private boolean loading = true;
	
    static final int DIALOG_ADD_ENTRY = 0;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        HNApp = (HNApplication)getApplicationContext();
        	
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        
        Intent intent = getIntent();
        if(Intent.ACTION_VIEW.equals(intent.getAction())) {
        	String loc = intent.getData().getLastPathSegment();
        	if(loc.equals("news"))
        		homePage = true;
        	else
        		homePage = false;
        }
    
        actionBar = (ActionBar) findViewById(R.id.actionbar);
        if(homePage)
        	actionBar.setTitle("News");
        else
        	actionBar.setTitle("Newest");
        actionBar.setHomeAction(new IntentAction(this, HNApp.createIntent(this), R.drawable.ic_title_home_default));
        actionBar.addAction(mUpdateAction);
        actionBar.addAction(mLoadNewAction);
        actionBar.addAction(mAddAction);
		adapter = new HNewsAdapter(getApplicationContext(), R.layout.newsitem, R.id.title, new ArrayList<HNewsItem>());
		
        lv = (ListView)findViewById(R.id.news);
        registerForContextMenu(lv);
        
        lv.setAdapter(adapter);

        lv.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (!loading && (firstVisibleItem + visibleItemCount) >= (totalItemCount - 15)) {
					new getJson(true).execute(API_NEXT);//, true);
					loading = true;
				}
			}
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
        	
        });
        lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				TextView tvPostId = (TextView)arg1.findViewById(R.id.postId);
				String postId = (String)tvPostId.getText();
				Intent intent = new Intent(getApplicationContext(), HNPost.class);
				intent.putExtra("postId", postId);
				if(homePage)
					intent.putExtra("type", "news");
				else
					intent.putExtra("type", "newest");
				startActivity(intent);
			}
        });
        if(homePage)
        	new getJson(false).execute(API_HOME);//, false);
        else
        	new getJson(false).execute(API_NEW);
        
        
        if(Intent.ACTION_SEND.equals(intent.getAction())) {
        	String t = intent.getStringExtra(Intent.EXTRA_TEXT);
        	Bundle bundle = new Bundle();
        	if(t.startsWith("http"))
        		bundle.putString("url", t);
        	else
        		bundle.putString("text", t);
        	showDialog(DIALOG_ADD_ENTRY, bundle);
        }
        
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.context_item_menu, menu);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
    	RelativeLayout parent = ((RelativeLayout) info.targetView);
    	TextView tvUrl = (TextView) parent.findViewById(R.id.url);
    	String url = (String)tvUrl.getText();
    	if(url.startsWith("/")) {
    		url = "http://news.ycombinator.com/item?id=" + url.split("/")[2] ;
    	}
    	switch (item.getItemId()) {
    	case R.id.open:
    		Intent intent = new Intent(Intent.ACTION_VIEW);
    		intent.setData(Uri.parse(url));
    		startActivity(intent);  
    		return true;
    	case R.id.share:
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent.putExtra(Intent.EXTRA_TEXT, url);
			startActivity(Intent.createChooser(shareIntent, "Share"));
			return true;
    	default:
    		return super.onContextItemSelected(item);
    	}
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
	protected void onPrepareDialog(int id, Dialog dialog, Bundle bundle) {
		String title = bundle.getString("title");
		String url = bundle.getString("url");
		String text = bundle.getString("text");
		
		EditText etTitle = (EditText) dialog.findViewById(R.id.title);
		EditText etUrl = (EditText) dialog.findViewById(R.id.url);
		EditText etText = (EditText) dialog.findViewById(R.id.text);
		
		etTitle.setText(title);
		etUrl.setText(url);
		etText.setText(text);
		
	}
	
	protected Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch(id) {
		case DIALOG_ADD_ENTRY:
			final Dialog dialogEntry = new Dialog(this);
    		dialogEntry.setContentView(R.layout.submit_dialog);
    		dialogEntry.setTitle("Add Entry");
    		
    		Button btSubmit = (Button)dialogEntry.findViewById(R.id.submit);
    		btSubmit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					EditText etTitle = (EditText) dialogEntry.findViewById(R.id.title);
					EditText etUrl = (EditText) dialogEntry.findViewById(R.id.url);
					EditText etText = (EditText) dialogEntry.findViewById(R.id.text);
					
					String title = etTitle.getText().toString();
					String url = etUrl.getText().toString();
					String text = etText.getText().toString();
					
					new HNSubmit().execute(title, url, text);
					dialogEntry.dismiss();
				}
    		});
    		
    		Button btCancel = (Button) dialogEntry.findViewById(R.id.cancel);
    		btCancel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialogEntry.dismiss();
				}
    		});
    		dialog = dialogEntry;
			break;
			default:
				dialog = null;
		}
		return dialog;
	}
	
    private void parseJson(String raw, boolean appendData) {
    	try {
    		if(!appendData)
    			adapter.clear();
			JSONObject json = new JSONObject(raw);
			JSONArray news = json.getJSONArray("items");
			
			if(homePage)
				API_NEXT = API_HOME + "/" + json.getString("nextId");
			else
				API_NEXT = API_NEW + "/" + json.getString("nextId");
				
			for(int i=0; i<news.length(); i++) {
				HNewsItem item = new HNewsItem();
				
				JSONObject singleNews = news.getJSONObject(i);
				String domain;

				try {
					domain = new URL(singleNews.getString("url")).getHost();
				} catch (MalformedURLException e) {
					domain = "news.ycombinator.com";
				}
				
				item.setTitle(singleNews.getString("title"));
				item.setAuthor(singleNews.getString("postedBy"));
				item.setComments(singleNews.getString("commentCount"));
				item.setUrl(singleNews.getString("url"));
				item.setPoints(singleNews.getString("points"));
				item.setTime(singleNews.getString("postedAgo"));
				item.setPostId(singleNews.getString("id"));
				item.setDomain(domain);
				
				HNApp.setPost(singleNews.getString("id"), item);
				adapter.add(item);

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
    		if(homePage)
    			new getJson(false).execute(API_HOME);
    		else
    			new getJson(false).execute(API_NEW);
    		lv.setSelectionAfterHeaderView();
    	}
    	
    	@Override
    	public Animation getAnimation() {
    		return AnimationUtils.loadAnimation(getApplicationContext(), R.anim.loading_animation);
    	}
    };
    
    final MyAction mLoadNewAction = new MyAction() {
    	@Override
    	public int getDrawable() {
    		return R.drawable.ic_title_change_default;
    	}
    	    	
    	@Override
    	public void performAction(View view) {
    		if(homePage) {
    			homePage = false;
    			new getJson(false).execute(API_NEW);//, false);
    		}
    		else {
    			homePage = true;
    			new getJson(false).execute(API_HOME);//, false);
    		}
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
    		showDialog(DIALOG_ADD_ENTRY, new Bundle());
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
    
    
    private class getJson extends AsyncTask<String, String, Integer> {
    	
    	boolean appendData;
    	
    	public getJson(boolean appendData) {
    		this.appendData = appendData;
    	}
    	
    	protected void onPreExecute () {
    		actionBar.setTitle("Loading ...");
    	}

    	protected Integer doInBackground(String... args) {
			try {
				HttpClient httpClient = new DefaultHttpClient();
				HttpGet request = new HttpGet();
				URI targetUri = new URI((String)args[0]);
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
    		parseJson(result[0], appendData);
    	}
    	
    	protected void onPostExecute(Integer v) {
    		if(v == 0) {
    			loading = false;
    			if(homePage)
    				actionBar.setTitle("News");
    			else
    				actionBar.setTitle("Newest");
    			adapter.notifyDataSetChanged();
    		} else {
    			actionBar.setTitle("No Internet Connection");
    		}
    	}
    };
    
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
				grequest.setURI(new URI("http://news.ycombinator.com/submit"));
				String content = httpClient.execute(grequest, mResponseHandler, ctx);
				
				Document doc = Jsoup.parse(content);
				String fnid = doc.getElementsByTag("input").first().attr("value");
				//System.out.println(fnid);
				
				HttpPost prequest = new HttpPost();
				prequest.setURI(new URI("http://news.ycombinator.com/r"));
				List<NameValuePair> nvp = new ArrayList<NameValuePair>();
				nvp.add(new BasicNameValuePair("fnid", fnid));
				nvp.add(new BasicNameValuePair("t", args[0]));
				nvp.add(new BasicNameValuePair("u", args[1]));
				nvp.add(new BasicNameValuePair("x", args[2]));
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
			Toast.makeText(HNews.this, error[0], Toast.LENGTH_SHORT).show();
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


