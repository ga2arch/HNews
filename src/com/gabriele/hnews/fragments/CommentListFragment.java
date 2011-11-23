package com.gabriele.hnews.fragments;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.gabriele.hnews.R;
import com.gabriele.hnews.adapters.CommentAdapter;
import com.gabriele.hnews.request.IHackernewsApi;
import com.gabriele.hnews.request.RequestService;
import com.gabriele.hnews.response.Comment;
import com.gabriele.hnews.response.ItemInfoResponse;
import com.google.gson.Gson;

public class CommentListFragment extends ListFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		return inflater.inflate(R.layout.lv_items, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//loadData();
    }
	
	public void handleSuccess(Bundle resultData) {
		Gson gson = new Gson();
		ItemInfoResponse resp = gson.fromJson(resultData.getString("data"), ItemInfoResponse.class);
		fillListView(resp.comments);
	}
	
	private void fillListView(ArrayList<Comment> comments) {
		Context context = getActivity().getApplicationContext();

		final CommentAdapter adapter = new CommentAdapter(context, R.layout.comment, 
				  						   				  R.id.title, comments);

		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				setListAdapter(adapter);
			}
		});
	}
	
	public void loadData(String location) {
		Context context = getActivity().getApplicationContext();

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
		Comment comment = (Comment) lv.getAdapter().getItem(position);
		
	}

}
