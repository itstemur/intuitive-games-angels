package com.visafm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.visafm.R;

import java.util.HashMap;
import java.util.List;

public class MenuAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<String> listParent;
    private final HashMap<String, List<String>> listChild;

    public MenuAdapter(Context context, List<String> listParent, HashMap<String, List<String>> listChild) {
        this.context = context;
        this.listParent = listParent;
        this.listChild = listChild;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.listChild.get(this.listParent.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_menu_child, null);
        }
        TextView txtListChild = convertView.findViewById(R.id.lblListItem);
        txtListChild.setText(childText);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listChild.get(this.listParent.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.listParent.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.listParent.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_menu_parent, null);
        }
        TextView lblListHeader = convertView.findViewById(R.id.lblListHeader);
        ImageView indicator = convertView.findViewById(R.id.indicator);

        lblListHeader.setText(headerTitle);
        if ( getChildrenCount( groupPosition ) == 0 ) {
            indicator.setVisibility( View.INVISIBLE );
        } else {
            indicator.setVisibility( View.VISIBLE );
            indicator.setImageResource( isExpanded ? R.drawable.ic_keyboard_arrow_bottom : R.drawable.ic_keyboard_arrow_right );
        }
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}