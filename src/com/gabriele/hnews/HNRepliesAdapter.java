package com.gabriele.hnews;

import java.util.List;

import android.content.Context;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class HNRepliesAdapter extends ArrayAdapter<HNReplyItem> {

	private Context mContext;
	private int mResource;
	private boolean hideFirst;
	
	public HNRepliesAdapter(Context context, int resource,	int textViewResourceId, List<HNReplyItem> objects, boolean hideFirst) {
		super(context, resource, textViewResourceId, objects);
		this.mContext = context;
		this.mResource = resource;
		this.hideFirst = hideFirst;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = new ViewHolder();

		if(convertView == null) {
			LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(mResource, null);
			viewHolder.tvAuthor = (TextView)convertView.findViewById(R.id.author);
			viewHolder.tvTime = (TextView)convertView.findViewById(R.id.time);
			viewHolder.tvPoints = (TextView)convertView.findViewById(R.id.points);
			viewHolder.tvReplyId = (TextView)convertView.findViewById(R.id.replyId);
			viewHolder.tvNumReply = (TextView)convertView.findViewById(R.id.numReply);
			viewHolder.tvContent = (TextView)convertView.findViewById(R.id.content);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		HNReplyItem comment = getItem(position);
		
		viewHolder.tvAuthor.setText(comment.getAuthor());
		viewHolder.tvTime.setText(comment.getTime());
		viewHolder.tvPoints.setText(comment.getPoints() + " points");
		viewHolder.tvReplyId.setText(comment.getReplyId());
		viewHolder.tvContent.setText(comment.getContent());
		
		if(hideFirst && (position == 0)) {
			viewHolder.tvNumReply.setText("");
			Linkify.addLinks(viewHolder.tvContent, Linkify.ALL);
		}
		else
			viewHolder.tvNumReply.setText(comment.getNumReply() + " replies");
		
		return convertView;

   	}
   	
   	private static class ViewHolder {
   		public TextView tvAuthor;
   		public TextView tvTime;
   		public TextView tvPoints;
   		public TextView tvReplyId;
   		public TextView tvNumReply;
   		public TextView tvContent;
   	}
   	
	
}
