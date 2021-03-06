package com.teamcaffeine.hotswap.navigation.homeAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.teamcaffeine.hotswap.R;
import com.teamcaffeine.hotswap.swap.ActiveTransactionInfo;

import java.util.ArrayList;
import java.util.HashMap;

public class RentingPendingItemsAdapter extends BaseAdapter {

    Context context;
    private HashMap<String, ActiveTransactionInfo> items;
    private ArrayList<ActiveTransactionInfo> itemList;

    public RentingPendingItemsAdapter(Context aContext) {
        context = aContext;
        items = new HashMap<>();
        itemList = new ArrayList<ActiveTransactionInfo> (items.values());
    }

    public void putItems(HashMap<String, ActiveTransactionInfo> items) {
        this.items = items;
        this.itemList = new ArrayList<>(items.values());
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.listview_row, parent, false);
        } else {
            row = convertView;
        }

        TextView itemName = (TextView) row.findViewById(R.id.itemTitle);
        TextView swapDates = (TextView) row.findViewById(R.id.itemDescription);

        itemName.setText(itemList.get(position).getItem().getName());
        swapDates.setText(itemList.get(position).getDate().toString());
        return row;
    }

    public ActiveTransactionInfo getActiveTransactionInfoAtPosition(int position) {
        return itemList.get(position);
    }
}
