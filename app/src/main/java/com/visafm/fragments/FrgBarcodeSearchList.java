package com.visafm.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.visafm.Dashboard;
import com.visafm.R;
import com.visafm.adapter.OrderListAdap;
import com.visafm.common.Common;

import org.json.JSONArray;
/**
 * Created by Ankit Patel on 20/02/19.
 */
public class FrgBarcodeSearchList extends Fragment {

    public static ListView lvBarcodeOrder;
    TextView tvNoRecords;
    JSONArray jArraySearchLists = new JSONArray();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_barcode_searchlist, container, false);
        try {
            lvBarcodeOrder = rootView.findViewById(R.id.lvBarcodeOrder);
            tvNoRecords = rootView.findViewById(R.id.tvNoRecords);
            jArraySearchLists = new JSONArray(Common.getSharedPreferences(getActivity(), "barcodesearchResult"));
            if (jArraySearchLists.length() > 0) {
                OrderListAdap adap = new OrderListAdap(getActivity(), jArraySearchLists);
                lvBarcodeOrder.setAdapter(adap);
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
        Dashboard.ivSortArrows.setVisibility(View.GONE);
        Dashboard.ivSortDown.setVisibility(View.GONE);
        Dashboard.ivSortUp.setVisibility(View.GONE);
        Dashboard.ivEdit.setVisibility(View.GONE);
        Dashboard.ivService.setVisibility(View.GONE);
        Dashboard.ivServiceEdit.setVisibility(View.GONE);
        Dashboard.ivDrawerIndicator.setVisibility(View.VISIBLE);
        Dashboard.ivBack.setVisibility(View.GONE);
        Dashboard.ivDocumnetEdit.setVisibility(View.GONE);
        Dashboard.tvTitle.setText("Barcode Suche Liste");
        super.onResume();
    }

}

