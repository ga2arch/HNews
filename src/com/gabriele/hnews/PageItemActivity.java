package com.gabriele.hnews;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.gabriele.hnews.fragments.CommentListFragment;
import com.gabriele.hnews.fragments.ItemInfoFragment;
import com.gabriele.hnews.request.IHackernewsApi;
import com.gabriele.hnews.response.PageItem;

public class PageItemActivity extends FragmentActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.page_item);
		
		Intent intent = getIntent();
		PageItem item = (PageItem) intent.getSerializableExtra("pageitem");
		
		FragmentManager fm = getSupportFragmentManager();
		ItemInfoFragment itf = (ItemInfoFragment) fm.findFragmentById(R.id.page_item);
		itf.fillView(item);
		
		CommentListFragment clf = (CommentListFragment) fm.findFragmentById(R.id.comment_list);
		clf.loadData(IHackernewsApi.API_POST_INFO + item.id);
	}
}
