package com.gabriele.hnews;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import com.gabriele.hnews.fragments.CommentFragment;
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
		CommentFragment cf = (CommentFragment) fm.findFragmentById(R.id.comment);
		cf.fillView(comment);
		
		CommentListFragment clf = (CommentListFragment) fm.findFragmentById(R.id.children);
		clf.fillListView(comment.children);
	}

	@SuppressWarnings("unchecked")
	private Comment buildComment(Bundle bundle) {
		Comment comment = new Comment();
		
		comment.postedBy = bundle.getString("postedBy");
		comment.postedAgo = bundle.getString("postedAgo");
		comment.points = bundle.getInt("points");
		comment.id = bundle.getInt("id");
		comment.comment = bundle.getString("comment");
		comment.children = (ArrayList<Comment>) bundle.getSerializable("children");
		
		return comment;
	}
	
}
