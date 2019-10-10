package com.portsip.main;
/**
 * Property of IT Man AS, Bryne Norway
 * (c) 2015 IT Man AS
 * Created by Nigussie on 26.03.2015.
 */
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.portsip.R;

public class CustomAdapterTag extends BaseAdapter {
	private static final String LOG_TAG = CustomAdapterTag.class
			.getSimpleName();

	private final String[] _items;
	private final LayoutInflater _inflater;

	public CustomAdapterTag(Context context, String... items) {
		_inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		_items = items;
	}

	@Override
	public int getCount() {
		return _items.length;
	}

	@Override
	public Object getItem(int position) {
		return _items[position];
	}

	@Override
	public long getItemId(int position) {
		return position + 1;
	}

	static class ViewHolder {
		TextView positionText;
		TextView idText;
		TextView itemText;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		Log.d(LOG_TAG, "position: " + position + ", convert view: "
                + (convertView != null) + ", parent: " + parent.getId());

		if (convertView == null) {
			// 1.
			convertView = _inflater.inflate(R.layout.itempbook, null);

			// 2.
			holder = new ViewHolder();
			holder.positionText = (TextView) convertView
					.findViewById(R.id.position);
			holder.idText = (TextView) convertView.findViewById(R.id.id);
			holder.itemText = (TextView) convertView.findViewById(R.id.item);

			convertView.setTag(holder);
		} else {
			// 2.
			holder = (ViewHolder) convertView.getTag();
		}

		// 3.
		holder.positionText.setText("Call: " + position);
		holder.idText.setText("Sms: " + getItemId(position));
		holder.itemText.setText(" " + getItem(position));
		return convertView;
	}
}