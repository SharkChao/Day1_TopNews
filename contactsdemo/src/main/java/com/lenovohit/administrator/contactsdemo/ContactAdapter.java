package com.lenovohit.administrator.contactsdemo;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.List;

/**
 * Created by SharkChao on 2017-08-18.
 */

public class ContactAdapter extends ArrayAdapter{

    private int list_item;
    private SectionIndexer mSectionIndexer;
    private List<Contact>mContactList;
    public ContactAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Contact> contactList,SectionIndexer sectionIndexer) {
        super(context, resource, contactList);
        this.mContactList = contactList;
        this.list_item = resource;
        this.mSectionIndexer = sectionIndexer;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Contact contact = (Contact) getItem(position);
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item,null);
        }
        TextView name = (TextView) convertView.findViewById(R.id.name);
        LinearLayout sortKeyLayout = (LinearLayout) convertView.findViewById(R.id.sort_key_layout);
        TextView sortKey = (TextView) convertView.findViewById(R.id.sort_key);
        name.setText(contact.getName());
        int section = mSectionIndexer.getSectionForPosition(position);
        if (position == mSectionIndexer.getPositionForSection(section)) {
            sortKey.setText(contact.getSortKey());
            sortKeyLayout.setVisibility(View.VISIBLE);
        } else {
            sortKeyLayout.setVisibility(View.GONE);
        }
        return convertView;
    }

}
