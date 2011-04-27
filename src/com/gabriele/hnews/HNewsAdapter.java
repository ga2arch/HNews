package com.gabriele.hnews;

import java.io.Serializable;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HNewsAdapter extends ArrayAdapter<HNewsItem> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3232325033801427799L;
	private Context mContext;
	private int mResource;
	
	public HNewsAdapter(Context context, int resource, int textViewResourceId,	List<HNewsItem> objects) {
		super(context, resource, textViewResourceId, objects);
		this.mContext = context;
		this.mResource = resource;
	}
	
   	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = new ViewHolder();

		if(convertView == null) {
			LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(mResource, null);
			viewHolder.tvAuthor = (TextView)convertView.findViewById(R.id.author);
			viewHolder.tvTime = (TextView)convertView.findViewById(R.id.time);
			viewHolder.tvTitle = (TextView)convertView.findViewById(R.id.title);
			viewHolder.tvPoints = (TextView)convertView.findViewById(R.id.points);
			viewHolder.tvComments = (TextView)convertView.findViewById(R.id.comments);
			viewHolder.tvDomain = (TextView)convertView.findViewById(R.id.domain);
			viewHolder.tvPostId = (TextView)convertView.findViewById(R.id.postId);
			viewHolder.tvUrl = (TextView)convertView.findViewById(R.id.url);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		HNewsItem item = getItem(position);
		
		viewHolder.tvAuthor.setText(item.getAuthor());
		viewHolder.tvTime.setText(item.getTime());
		viewHolder.tvTitle.setText(item.getTitle());
		viewHolder.tvPoints.setText(item.getPoints() + " points");
		viewHolder.tvComments.setText(item.getComments() + " comments");
		viewHolder.tvDomain.setText(item.getDomain());
		viewHolder.tvPostId.setText(item.getPostId());
		viewHolder.tvUrl.setText(item.getUrl());
		
		return convertView;

   	}
   	
   	private static class ViewHolder {
   		public TextView tvAuthor;
   		public TextView tvTitle;
   		public TextView tvTime;
   		public TextView tvPoints;
   		public TextView tvComments;
   		public TextView tvDomain;
   		public TextView tvPostId;
   		public TextView tvUrl;
   	}
   	
}
