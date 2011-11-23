package com.gabriele.hnews.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gabriele.hnews.R;
import com.gabriele.hnews.response.PageItem;

public class ItemInfoFragment extends Fragment {
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			 				 Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.item_info, container, false);
		
		return view;
	}
	
	public void fillView(PageItem item) {
		View v = getView();
		
		TextView tvAuthor =   (TextView) v.findViewById(R.id.author);
		TextView tvTime = 	  (TextView) v.findViewById(R.id.time);
		TextView tvTitle = 	  (TextView) v.findViewById(R.id.title);
		TextView tvPoints =   (TextView) v.findViewById(R.id.points);
		TextView tvComments = (TextView) v.findViewById(R.id.comments);
		TextView tvDomain =   (TextView) v.findViewById(R.id.domain);
		
		tvAuthor.setText(item.postedBy);
		tvTime.setText(item.postedAgo);
		tvTitle.setText(item.title);
		tvPoints.setText(item.points + " points");
		//tvComments.setText(item.commentCount + " comments");
		//tvDomain.setText(item.getDomain());
	}
}
