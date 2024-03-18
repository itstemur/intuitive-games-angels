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
import com.visafm.roombook.adapter.DocListAdap;
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
public class FrgDocList extends Fragment implements BaseClass, View.OnClickListener {

    public static ListView lvDocs;
    public static TextView tvNoRecords;
    BaseClass delegate = this;
    WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;
    boolean mAlreadyLoaded = false;
    JSONArray jArrayDocList = new JSONArray();
    FloatingActionButton fabAddDocument;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_doclist, container, false);
        try {
            lvDocs = rootView.findViewById(R.id.lvDocs);
            tvNoRecords = rootView.findViewById(R.id.tvNoRecords);
            fabAddDocument = rootView.findViewById(R.id.fabAddDocument);
            mWaveSwipeRefreshLayout = rootView.findViewById(R.id.main_swipe);
            mWaveSwipeRefreshLayout.setWaveARGBColor(255, 0, 91, 147);
            mWaveSwipeRefreshLayout.setColorSchemeColors(Color.WHITE);
            mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshDocumentList();

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
        fabAddDocument.setOnClickListener(this);
        if (savedInstanceState == null && !mAlreadyLoaded) {
            mAlreadyLoaded = true;
            getDocList();
        } else {
            if (jArrayDocList.length() > 0) {
                DocListAdap adap = new DocListAdap(getActivity(), jArrayDocList);
                tvNoRecords.setVisibility(View.GONE);
                lvDocs.setAdapter(adap);
            } else {
                tvNoRecords.setVisibility(View.VISIBLE);
            }
        }
    }

    private void refreshDocumentList() {
        try {
            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("userid", Common.USER_SESSION);
            postDataParams.put("orderid", Common.selectedOrderDetail.getString("OrderNr"));
            if (Common.isStringEmpty(Common.APPLICATIONID)) {
                postDataParams.put("ApplicationID", "");
            } else {
                postDataParams.put("ApplicationID", Common.APPLICATIONID);
            }
            HttpConnection httpConnection = new HttpConnection(delegate, getActivity());
            httpConnection.setRequestedfor("getOrderDocumentsList");
            httpConnection.setIsloading(false);
            httpConnection.setPostDataParams(postDataParams);
            httpConnection.setUrl("Orders/GetOrderDocumentsList");
            httpConnection.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDocList() {
        try {
            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("userid", Common.USER_SESSION);
            postDataParams.put("orderid", Common.selectedOrderDetail.getString("OrderNr"));
            if (Common.isStringEmpty(Common.APPLICATIONID)) {
                postDataParams.put("ApplicationID", "");
            } else {
                postDataParams.put("ApplicationID", Common.APPLICATIONID);
            }
            HttpConnection httpConnection = new HttpConnection(delegate, getActivity());
            httpConnection.setRequestedfor("getOrderDocumentsList");
            httpConnection.setIsloading(true);
            httpConnection.setPostDataParams(postDataParams);
            httpConnection.setUrl("Orders/GetOrderDocumentsList");
            httpConnection.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void httpResponse(String response, String requestedFor) {
        try {
            Common.stopProgressDialouge();
            if (requestedFor.equals("getOrderDocumentsList")) {
                mWaveSwipeRefreshLayout.setRefreshing(false);
                JSONObject jObj = new JSONObject(response);
                if (jObj.getString("ResultCode").equals("SUCCESS")) {
                    jArrayDocList = jObj.getJSONArray("ResultObject");
                    if (jArrayDocList.length() > 0) {
                        Log.e("jArrayDocList", jArrayDocList.length() + "");
                        DocListAdap adap = new DocListAdap(getActivity(), jArrayDocList);
                        tvNoRecords.setVisibility(View.GONE);
                        lvDocs.setAdapter(adap);
                    } else {
                        DocListAdap adap = new DocListAdap(getActivity(), jArrayDocList);
                        lvDocs.setAdapter(adap);
                        tvNoRecords.setVisibility(View.VISIBLE);
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
        Dashboard.ivBack.setVisibility(View.VISIBLE);
        Dashboard.tvTitle.setText("Dokumente");
        Dashboard.ivDocumnetEdit.setVisibility(View.GONE);
        JSONArray jArrayDocList = new JSONArray();
        DocListAdap adap = new DocListAdap(getActivity(), jArrayDocList);
        lvDocs.setAdapter(adap);
        refreshDocumentList();
        super.onResume();
    }


    @Override
    public void httpFailure(String response, String requestedFor) throws Exception {
        if (requestedFor.equals("getOrderDocumentsList")) {
            mWaveSwipeRefreshLayout.setRefreshing(false);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabAddDocument:
                Dashboard.displayFragment(51);
                break;
            default:
                break;
        }
    }
}
