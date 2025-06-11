/**
 * 
 */
package com.iyuba.core.common.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.core.R;
import com.iyuba.core.common.sqlite.mode.FileInfo;

import java.util.List;

/**
 * @author zhuch
 * 
 * 文件浏览器相关类
 */
public class FileAdapter extends BaseAdapter {

	private LayoutInflater _inflater;
	private List<FileInfo> _files;

	public FileAdapter(Context context, List<FileInfo> files) {
		_files = files;
		_inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return _files.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return _files.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) { 
			convertView = _inflater.inflate(R.layout.item_file, null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.file_name);
			holder.icon = (ImageView) convertView.findViewById(R.id.file_icon);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		FileInfo f = _files.get(position);
		holder.name.setText(f.Name);
		holder.icon.setBackgroundResource(f.getIconResourceId());
		return convertView;
	}

	/* class ViewHolder */
	private class ViewHolder {
		TextView name;
		ImageView icon;
	}
}
