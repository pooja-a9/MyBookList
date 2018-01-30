package com.example.android.mybooklist;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by chaitanya on 8/29/2016.
 */
public class BookAdapter extends ArrayAdapter<Book> {


    public BookAdapter(Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //1
        View listItem = convertView;
        if (listItem == null) {
            listItem = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        Book b = getItem(position);

        TextView t1 = (TextView) listItem.findViewById(R.id.title);
        t1.setText(b.getTitle());

        TextView t2 = (TextView) listItem.findViewById(R.id.authors);
        t2.setText(TextUtils.join(", ", b.getAuthors().toArray()));

        return listItem;
    }
}
