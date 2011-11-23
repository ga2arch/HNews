package com.gabriele.hnews.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gabriele.hnews.R;
import com.gabriele.hnews.response.PageItem;

public class PageAdapter extends ArrayAdapter<PageItem> {
	
	/**
	 * 
	 */
	private Context mContext;
	private int mResource;
	
	public PageAdapter(Context context, int resource, int textViewResourceId,	List<PageItem> objects) {
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
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		PageItem item = getItem(position);
		
		viewHolder.tvAuthor.setText(item.postedBy);
		viewHolder.tvTime.setText(item.postedAgo);
		viewHolder.tvTitle.setText(item.title);
		viewHolder.tvPoints.setText(item.points + " points");
		viewHolder.tvComments.setText(item.commentCount + " comments");
		viewHolder.tvDomain.setText(item.getDomain());
		
		return convertView;

   	}
   	
   	private static class ViewHolder {
   		public TextView tvAuthor;
   		public TextView tvTitle;
   		public TextView tvTime;
   		public TextView tvPoints;
   		public TextView tvComments;
   		public TextView tvDomain;
   	}
   	
}
