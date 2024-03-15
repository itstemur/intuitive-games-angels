package com.visafm.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.visafm.Dashboard;
import com.visafm.R;
import com.visafm.adapter.ServiceListAdap;
import com.visafm.common.BaseClass;
import com.visafm.common.Common;
import com.visafm.common.HttpConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

/**
 * Created by Ankit Patel on 20/02/19.
 */
public class FrgServiceList extends Fragment implements BaseClass, View.OnClickListener {


    public static ListView lvService;
    public static TextView tvNoRecords;
    BaseClass delegate = this;
    WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    boolean mAlreadyLoaded = false;
    JSONArray jArrayServiceList = new JSONArray();
    FloatingActionButton fabAddService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_servicelist, container, false);
        try {
            lvService = rootView.findViewById(R.id.lvService);
            tvNoRecords = rootView.findViewById(R.id.tvNoRecords);
            fabAddService = rootView.findViewById(R.id.fabAddService);
            fabAddService.setOnClickListener(this);
            mWaveSwipeRefreshLayout = rootView.findViewById(R.id.main_swipe);
            mWaveSwipeRefreshLayout.setWaveARGBColor(255, 0, 91, 147);
            mWaveSwipeRefreshLayout.setColorSchemeColors(Color.WHITE);
            mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    JSONArray jArrayServiceList = new JSONArray();
                    ServiceListAdap adap = new ServiceListAdap(getActivity(), jArrayServiceList);
                    lvService.setAdapter(adap);
                    refreshOrderDetailsList();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }//onCreateView

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState == null && !mAlreadyLoaded) {
            mAlreadyLoaded = true;
            getServiceDetailList();
        } else {
            if (jArrayServiceList.length() > 0) {
                tvNoRecords.setVisibility(View.GONE);
                ServiceListAdap adap = new ServiceListAdap(getActivity(), jArrayServiceList);
                lvService.setAdapter(adap);

            } else {
                tvNoRecords.setVisibility(View.VISIBLE);
                ServiceListAdap adap = new ServiceListAdap(getActivity(), jArrayServiceList);
                lvService.setAdapter(adap);
            }
        }
    }


    public void refreshOrderDetailsList() {

        try {
            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("orderid", Common.selectedOrderDetail.getString("OrderNr"));
            HttpConnection httpConnection = new HttpConnection(delegate, getActivity());
            httpConnection.setRequestedfor("getOrderDetailsList");
            httpConnection.setIsloading(false);
            httpConnection.setPostDataParams(postDataParams);
            httpConnection.setUrl("Orders/GetOrderDetailsList");
            httpConnection.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getServiceDetailList() {
        try {
            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("orderid", Common.selectedOrderDetail.getString("OrderNr"));
            HttpConnection httpConnection = new HttpConnection(delegate, getActivity());
            httpConnection.setRequestedfor("getOrderDetailsList");
            httpConnection.setIsloading(true);
            httpConnection.setPostDataParams(postDataParams);
            httpConnection.setUrl("Orders/GetOrderDetailsList");
            httpConnection.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void httpResponse(String response, String requestedFor) {
        try {
            Common.stopProgressDialouge(requestedFor);
            if (requestedFor.equals("getOrderDetailsList")) {
                mWaveSwipeRefreshLayout.setRefreshing(false);
                JSONObject jObj = new JSONObject(response);
                if (jObj.getString("ResultCode").equals("SUCCESS")) {
                    jArrayServiceList = jObj.getJSONArray("ResultObject");
                    if (jArrayServiceList.length() > 0) {
                        tvNoRecords.setVisibility(View.GONE);
                        ServiceListAdap adap = new ServiceListAdap(getActivity(), jArrayServiceList);
                        lvService.setAdapter(adap);
                    } else {
                        tvNoRecords.setVisibility(View.VISIBLE);
                        ServiceListAdap adap = new ServiceListAdap(getActivity(), jArrayServiceList);
                        lvService.setAdapter(adap);
                    }
                } else {
                    Common.showAlert(getActivity(), jObj.getString("ResultMessage"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResume() {
        Dashboard.ivDocument.setVisibility(View.GONE);
        Dashboard.ivSortDown.setVisibility(View.GONE);
        Dashboard.ivSortUp.setVisibility(View.GONE);
        Dashboard.ivEdit.setVisibility(View.GONE);
        Dashboard.ivSortArrows.setVisibility(View.GONE);
        Dashboard.ivService.setVisibility(View.GONE);
        Dashboard.ivServiceEdit.setVisibility(View.GONE);
        Dashboard.ivDrawerIndicator.setVisibility(View.GONE);
        Dashboard.ivDocumnetEdit.setVisibility(View.GONE);
        Dashboard.ivBack.setVisibility(View.VISIBLE);
        Dashboard.tvTitle.setText("Leistungen");
        JSONArray jArrayServiceList = new JSONArray();
        ServiceListAdap adap = new ServiceListAdap(getActivity(), jArrayServiceList);
        lvService.setAdapter(adap);
        refreshOrderDetailsList();
        super.onResume();
    }


    @Override
    public void httpFailure(String response, String requestedFor) throws Exception {
        if (requestedFor.equals("getOrderDetailsList")) {
            mWaveSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabAddService:
                Dashboard.displayFragment(55);
                break;
            default:
                break;
        }
    }


}

