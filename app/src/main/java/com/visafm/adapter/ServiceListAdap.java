package com.visafm.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.visafm.Dashboard;
import com.visafm.R;
import com.visafm.common.Common;

import org.json.JSONArray;
import org.json.JSONObject;

public class ServiceListAdap extends BaseAdapter {
    Context context;
    JSONArray data;

    public ServiceListAdap(Context context, JSONArray data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.length();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.list_services, parent, false);

        TextView tvWDetailID = convertView.findViewById(R.id.tvWDetailID);
        TextView tvBeschreibung1 = convertView.findViewById(R.id.tvBeschreibung1);
        TextView tvBeschreibung2 = convertView.findViewById(R.id.tvBeschreibung2);

        ImageView IvServiceOptions = convertView.findViewById(R.id.IvServiceOptions);

        try {
            final JSONObject jObj = data.getJSONObject(position);
            tvWDetailID.setText(jObj.getString("WDetailID").trim());

            if (Common.isStringEmpty(jObj.getString("Beschreibung1").trim())) {
                tvBeschreibung1.setText("");
            } else {
                tvBeschreibung1.setText(jObj.getString("Beschreibung1").trim());
            }

            if (Common.isStringEmpty(jObj.getString("Beschreibung2").trim())) {
                tvBeschreibung2.setText("");
            } else {
                tvBeschreibung2.setText(jObj.getString("Beschreibung2").trim());
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Common.setSharedPreferences(context, "EditOrdersDetailFromPage", "True");
                    Common.selectedServiceDetail = jObj;
                    Log.e("ad edit selected obj",Common.selectedServiceDetail.toString());
                    Dashboard.displayFragment(57);
                }
            });
            IvServiceOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.IvServiceOptions:
                            Dashboard.OptionsServices(context, view, jObj);
                            break;

                        default:
                            break;
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }


}