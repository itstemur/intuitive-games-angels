package com.visafm.roombook.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.visafm.roombook.Dashboard;
import com.visafm.roombook.R;
import com.visafm.roombook.adapter.OrderListAdap;
import com.visafm.roombook.common.BaseClass;
import com.visafm.roombook.common.Common;

import org.json.JSONArray;
import org.json.JSONObject;
/**
 * Created by Ankit Patel on 20/02/19.
 */
public class FrgSearchList extends Fragment implements BaseClass {

    public static ListView lvOrder;
    TextView tvNoRecords;
    JSONArray jArraySearchLists = new JSONArray();
    JSONArray jArraySortMenuList = new JSONArray();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        setHasOptionsMenu(true);
        View rootView = inflater.inflate(R.layout.frg_searchlist, container, false);

        try {
            lvOrder = rootView.findViewById(R.id.lvOrder);
            tvNoRecords = rootView.findViewById(R.id.tvNoRecords);

            jArraySearchLists = new JSONArray(Common.getSharedPreferences(getActivity(), "searchResult"));

            if (jArraySearchLists.length() > 0) {
                OrderListAdap adap = new OrderListAdap(getActivity(), jArraySearchLists);
                lvOrder.setAdapter(adap);
            } else {
                tvNoRecords.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }//onCreateView

    @Override
    public void onResume() {
        Dashboard.ivDocument.setVisibility(View.GONE);
        Dashboard.ivSortArrows.setVisibility(View.VISIBLE);
        Dashboard.ivSortDown.setVisibility(View.GONE);
        Dashboard.ivSortUp.setVisibility(View.GONE);
        Dashboard.ivEdit.setVisibility(View.GONE);
        Dashboard.ivService.setVisibility(View.GONE);
        Dashboard.ivServiceEdit.setVisibility(View.GONE);
        Dashboard.ivDocumnetEdit.setVisibility(View.GONE);
        Dashboard.ivDrawerIndicator.setVisibility(View.GONE);
        Dashboard.ivBack.setVisibility(View.VISIBLE);
        Dashboard.tvTitle.setText("Aufträge (Ergebnisliste)");
        super.onResume();
    }

    @Override
    public void httpResponse(String response, String requestedFor) throws Exception {
        if (requestedFor.equals("getSortMenuList")) {
            JSONObject jObj = new JSONObject(response);
            if (jObj.getString("ResultCode").equals("SUCCESS")) {
                jArraySortMenuList = jObj.getJSONArray("ResultObject");
                if (jArraySortMenuList.length() > 0) {
                    OrderListAdap adap = new OrderListAdap(getActivity(), jArraySortMenuList);
                    lvOrder.setAdapter(adap);
                } else {
                    tvNoRecords.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void httpFailure(String response, String requestedFor) throws Exception {

    }
}

