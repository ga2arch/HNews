package com.gabriele.hnews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

import com.gabriele.hnews.fragments.CommentListFragment;
import com.gabriele.hnews.response.Comment;

public class CommentActivity extends FragmentActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.comments);
		
		Intent intent = getIntent();
		Comment comment = (Comment) intent.getSerializableExtra("comment");
		
		FragmentManager fm = getSupportFragmentManager();
		
		CommentListFragment clf = (CommentListFragment) fm.findFragmentById(R.id.children);
		clf.getListView().addHeaderView(fillView(comment), null, true);
		clf.fillListView(comment.children);
	
	}
	
	private View fillView(Comment comment) {
		View v = getLayoutInflater().inflate(R.layout.comment, null, false);
		
		TextView tvAuthor = (TextView)v.findViewById(R.id.author);
		TextView tvTime = (TextView)v.findViewById(R.id.time);
		TextView tvPoints = (TextView)v.findViewById(R.id.points);
		TextView tvNumReply = (TextView)v.findViewById(R.id.numReply);
		TextView tvContent = (TextView)v.findViewById(R.id.content);
		
		tvAuthor.setText(comment.postedBy);
		tvTime.setText(comment.postedAgo);
		tvPoints.setText(comment.points + " points");
		tvContent.setText(comment.getCleanComment());
		tvNumReply.setVisibility(View.GONE);
		
		Linkify.addLinks(tvContent, Linkify.ALL);
		
		return v;
	}
	
}
