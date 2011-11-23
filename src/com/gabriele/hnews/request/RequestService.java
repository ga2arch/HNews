package com.gabriele.hnews.request;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

public class RequestService extends IntentService {
	
	private DefaultHttpClient hp;
	
	public final static int REQUEST_SUCCESS = 0;
	public final static int REQUEST_ERROR = 1;
	
	public final static int ERROR_404 = 2;
	public final static int ERROR_NO_CONNECTION = 3;
	
	public RequestService() {
		super("ihackernews");
		hp = new DefaultHttpClient();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String url = intent.getStringExtra("url");
		ResultReceiver receiver = intent.getParcelableExtra("receiver");
		HttpGet hg = new HttpGet(url);
		HttpResponse response = null;
		
		try {
			 response = hp.execute(hg);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Bundle bundle = new Bundle();

		if (response != null) {

			switch (response.getStatusLine().getStatusCode()) {
			case 200:
				try {
					String data = EntityUtils.toString(response.getEntity());
					bundle.putString("data", data);
					receiver.send(REQUEST_SUCCESS, bundle);
				} catch (ParseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
				
			case 400:
				bundle.putInt("errorCode", ERROR_404); 
				receiver.send(REQUEST_ERROR, bundle);
				break;
			}
			
		} else {
			bundle.putInt("errorCode", ERROR_NO_CONNECTION);
			receiver.send(REQUEST_ERROR, bundle);
		}
		
	}	
	
	@Override
	public void onDestroy() {
		hp.getConnectionManager().shutdown();
	}
	
}
