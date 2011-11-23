package com.gabriele.hnews.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gabriele.hnews.R;
import com.gabriele.hnews.response.Comment;

public class CommentAdapter extends ArrayAdapter<Comment> {

	private Context mContext;
	private int mResource;
	
	public CommentAdapter(Context context, int resource,	int textViewResourceId, List<Comment> objects) {
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
			viewHolder.tvPoints = (TextView)convertView.findViewById(R.id.points);
			viewHolder.tvNumReply = (TextView)convertView.findViewById(R.id.numReply);
			viewHolder.tvContent = (TextView)convertView.findViewById(R.id.content);
			
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		Comment comment = getItem(position);
		
		viewHolder.tvAuthor.setText(comment.postedBy);
		viewHolder.tvTime.setText(comment.postedAgo);
		viewHolder.tvPoints.setText(comment.points + " points");
		viewHolder.tvContent.setText(comment.comment);
		viewHolder.tvNumReply.setText(comment.children.size() + " replies");
		
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