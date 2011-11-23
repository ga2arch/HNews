package com.gabriele.hnews.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.app.ListFragment;
import android.support.v4.view.Menu;
import android.support.v4.view.MenuItem;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.gabriele.hnews.PageItemActivity;
import com.gabriele.hnews.R;
import com.gabriele.hnews.adapters.PageAdapter;
import com.gabriele.hnews.request.IHackernewsApi;
import com.gabriele.hnews.request.RequestService;
import com.gabriele.hnews.response.PageItem;
import com.gabriele.hnews.response.PageResponse;
import com.google.gson.Gson;

public class ItemListFragment extends ListFragment {
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		return inflater.inflate(R.layout.lv_items, container, false);
	}
	
	@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    	inflater.inflate(R.menu.menu, menu);
    	super.onCreateOptionsMenu(menu, inflater);
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_reload:
			loadData();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//loadData();
    }
	
	
	public void loadData() {
		Context context = getActivity().getApplicationContext();
		String location = getArguments().getString("location");

    	IHackernewsApi.retrieve(context, location, new ResultReceiver(null) {
        	@Override
        	protected void onReceiveResult(int resultCode, Bundle resultData) {
        		switch (resultCode) {
        		case RequestService.REQUEST_SUCCESS:
        			handleSuccess(resultData);
        			break;
        			
        		case RequestService.REQUEST_ERROR:
        			int errorCode = resultData.getInt("errorCode");
        			handleErrors(errorCode);
        			break;
        		}
        	}
        });
	}
	
	public void handleSuccess(Bundle resultData) {
		Context context = getActivity().getApplicationContext();

		Gson gson = new Gson();
		PageResponse resp = gson.fromJson(resultData.getString("data"), PageResponse.class);
		final PageAdapter adapter = new PageAdapter(context, R.layout.item, R.id.title, resp.items);
		
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
    			setListAdapter(adapter);
			}
		});
	}
	
	private void handleErrors(int errorCode) {
		switch (errorCode) {
		case RequestService.ERROR_404:
			Toast.makeText(getActivity(), "Api Error", Toast.LENGTH_SHORT).show();
			break;
			
		case RequestService.ERROR_NO_CONNECTION:
			Toast.makeText(getActivity(), "No Connection", Toast.LENGTH_SHORT).show();
			break;
		}
	}

	@Override
	public void onListItemClick (ListView lv, View v, int position, long id) {
		PageItem item = (PageItem) lv.getAdapter().getItem(position);
		Bundle bundle = buildBundle(item);
		Context context = getActivity().getApplicationContext();
		Intent intent = new Intent(context, PageItemActivity.class);
		intent.putExtras(bundle);
		getActivity().startActivity(intent);
	}
	
    private Bundle buildBundle(PageItem item) {
    	Bundle bundle = new Bundle();
    	
    	bundle.putString("title", item.title);
    	bundle.putString("url", item.url);
    	bundle.putString("postedBy", item.postedBy);
    	bundle.putString("postedAgo", item.postedAgo);
    	bundle.putInt("id", item.id);
    	bundle.putInt("points", item.points);
    	bundle.putInt("commentCount", item.commentCount);
    	
    	return bundle;
    }
	
}
