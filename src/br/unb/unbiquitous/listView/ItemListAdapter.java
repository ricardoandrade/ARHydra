package br.unb.unbiquitous.listView;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import br.unb.unbiquitous.activity.R;

public class ItemListAdapter extends ArrayAdapter<Item> {

	private LayoutInflater li;

	public ItemListAdapter(Context context, List<Item> items) {
		super(context, 0, items);
		li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final Item item = getItem(position);

		View view = convertView;
		
		if (view == null) {
			view = li.inflate(R.layout.item, null);
		}
		final TextView captionView = (TextView) view.findViewById(R.id.driverName);
		if (captionView != null) {
			captionView.setText(item.getCaption());
		}

		return view;
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}
}
