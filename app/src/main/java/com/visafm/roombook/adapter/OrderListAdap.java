package com.visafm.roombook.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.visafm.roombook.Dashboard;
import com.visafm.roombook.R;
import com.visafm.roombook.common.BaseClass;
import com.visafm.roombook.common.Common;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OrderListAdap extends BaseAdapter implements BaseClass {

    Context context;
    JSONArray data;

    public OrderListAdap(Context context, JSONArray data) {
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
        convertView = inflater.inflate(R.layout.list_requests, parent, false);

        TextView tvOrderNr = convertView.findViewById(R.id.tvOrderNr);
        TextView tvBezeichnung = convertView.findViewById(R.id.tvBezeichnung);
        TextView tvObjekt = convertView.findViewById(R.id.tvObjekt);
        TextView tvOrt = convertView.findViewById(R.id.tvOrt);
        TextView tvGebaude = convertView.findViewById(R.id.tvGebaude);


        try {
            final JSONObject jObj = data.getJSONObject(position);
            tvOrderNr.setText(jObj.getString("OrderNr").trim());
            tvBezeichnung.setText(jObj.getString("AuftragsbezeichnungShort").trim());
            tvObjekt.setText(jObj.getString("Objekt").trim());
            tvOrt.setText(jObj.getString("Ort").trim());
            tvGebaude.setText(jObj.getString("Gebäude").trim());
            ImageView Ivoptions = convertView.findViewById(R.id.Ivoptions);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Common.setSharedPreferences(context, "EditOrdersFromPage", "True");
                    Common.selectedOrderDetail = jObj;
                    Log.e("ad edit selected obj",Common.selectedOrderDetail.toString());
                    Dashboard.displayFragment(1);
                }
            });

            Ivoptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()) {
                        case R.id.Ivoptions:
                            final JSONObject jObj;
                            try {
                                jObj = data.getJSONObject(position);
                                Dashboard.optionsOrder(context,view,jObj);
                                Log.e("obj",jObj.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

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



    @Override
    public void httpResponse(String response, String requestedFor) throws Exception {

    }

    @Override
    public void httpFailure(String response, String requestedFor) throws Exception {

    }


}