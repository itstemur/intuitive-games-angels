package com.visafm.roombook.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.visafm.roombook.Dashboard;
import com.visafm.roombook.R;
import com.visafm.roombook.adapter.OrderListAdap;
import com.visafm.roombook.common.BaseClass;
import com.visafm.roombook.common.Common;
import com.visafm.roombook.common.HttpConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

/**
 * Created by Ankit Patel on 20/02/19.
 */
public class FrgOrderList extends Fragment implements BaseClass, View.OnClickListener {

    public static  ListView lvOrder;
    public static TextView tvNoRecords;
    BaseClass delegate = this;
    WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    boolean mAlreadyLoaded = false;
    JSONArray jArrayOrderList = new JSONArray();
    FloatingActionButton fabAddOrder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_orderlist, container, false);
        try {
            lvOrder = rootView.findViewById(R.id.lvOrder);
            tvNoRecords = rootView.findViewById(R.id.tvNoRecords);
            fabAddOrder = rootView.findViewById(R.id.fabAddOrder);
            mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) rootView.findViewById(R.id.main_swipe);
            mWaveSwipeRefreshLayout.setWaveARGBColor(255, 0, 91, 147);
            mWaveSwipeRefreshLayout.setColorSchemeColors(Color.WHITE);
            mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
                @Override public void onRefresh() {
                    refreshOrderList();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }//onCreateView

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fabAddOrder.setOnClickListener(this);
        if (savedInstanceState == null && !mAlreadyLoaded) {
            mAlreadyLoaded = true;
            getOrderList();
        } else {
            if (jArrayOrderList.length() > 0) {
                Log.e("ArrayLISTANK",jArrayOrderList.toString());
                OrderListAdap adap = new OrderListAdap(getContext(), jArrayOrderList);
                lvOrder.setAdapter(adap);
            } else {
                tvNoRecords.setVisibility(View.VISIBLE);
            }
        }
    }

    private void refreshOrderList() {
        try {
            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("userid", Common.USER_SESSION);
            HttpConnection httpConnection = new HttpConnection(delegate, getContext());
            httpConnection.setRequestedfor("getOrderList");
            httpConnection.setIsloading(false);
            httpConnection.setPostDataParams(postDataParams);
            httpConnection.setUrl("Orders/GetAssignOrders");
            httpConnection.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getOrderList() {

        try {
            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("userid", Common.USER_SESSION);
            HttpConnection httpConnection = new HttpConnection(delegate, getContext());
            httpConnection.setRequestedfor("getOrderList");
            httpConnection.setIsloading(true);
            httpConnection.setPostDataParams(postDataParams);
            httpConnection.setUrl("Orders/GetAssignOrders");
            httpConnection.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void httpResponse(String response, String requestedFor) {
        try {
            Common.stopProgressDialouge();
            if (requestedFor.equals("getOrderList")) {
                mWaveSwipeRefreshLayout.setRefreshing(false);
                JSONObject jObj = new JSONObject(response);
                if (jObj.getString("ResultCode").equals("SUCCESS")) {
                    jArrayOrderList = jObj.getJSONArray("ResultObject");
                    if (jArrayOrderList.length() > 0) {
                        Log.e("ArrayLISTANK",jArrayOrderList.toString());
                        OrderListAdap adap = new OrderListAdap(getContext(), jArrayOrderList);
                        lvOrder.setAdapter(adap);
                    } else {
                        tvNoRecords.setVisibility(View.VISIBLE);
                    }
                } else {
                    Common.showAlert(getContext(), jObj.getString("ResultMessage"));
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
        Dashboard.ivDocumnetEdit.setVisibility(View.GONE);
        Dashboard.ivService.setVisibility(View.GONE);
        Dashboard.ivServiceEdit.setVisibility(View.GONE);
        Dashboard.ivDrawerIndicator.setVisibility(View.VISIBLE);
        Dashboard.ivBack.setVisibility(View.GONE);
        Dashboard.tvTitle.setText("Auftragsliste");
        super.onResume();
    }


    @Override
    public void httpFailure(String response, String requestedFor) throws Exception {
        if (requestedFor.equals("getOrderList")) {
            mWaveSwipeRefreshLayout.setRefreshing(false);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabAddOrder:
                Dashboard.displayFragment(56);
                break;
            default:
                break;
        }
    }
}
