package com.gabriele.hnews.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gabriele.hnews.R;
import com.gabriele.hnews.response.Comment;

public class CommentFragment extends Fragment {
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			 Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.comment, container, false);
	
		return view;
	}

	public void fillView(Comment comment) {
		View v = getView();
		
		TextView tvAuthor = (TextView)v.findViewById(R.id.author);
		TextView tvTime = (TextView)v.findViewById(R.id.time);
		TextView tvPoints = (TextView)v.findViewById(R.id.points);
		TextView tvNumReply = (TextView)v.findViewById(R.id.numReply);
		TextView tvContent = (TextView)v.findViewById(R.id.content);
		
		tvAuthor.setText(comment.postedBy);
		tvTime.setText(comment.postedAgo);
		tvPoints.setText(comment.points + " points");
		tvContent.setText(comment.comment);
		tvNumReply.setText(comment.children.size() + " replies");
		
	}
	
}
