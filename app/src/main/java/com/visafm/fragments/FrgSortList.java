package com.visafm.fragments;

import android.content.Context;
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
import com.visafm.common.BaseClass;
import com.visafm.common.Common;
import com.visafm.common.HttpConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Ankit Patel on 20/02/19.
 */
public class FrgSortList extends Fragment implements BaseClass {
    public static ListView lvOrder;
    TextView tvNoRecords;
    BaseClass delegate = this;
    boolean mAlreadyLoaded = false;
    JSONArray jArraySortList = new JSONArray();
    static BaseClass staticDelegate;
    private static Context myContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_sortlist, container, false);
        try {
            lvOrder = rootView.findViewById(R.id.lvOrder);
            tvNoRecords = rootView.findViewById(R.id.tvNoRecords);
            myContext = getActivity();
            staticDelegate = delegate;
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
            getSortList();
        } else {
            if (jArraySortList.length() > 0) {
                OrderListAdap adap = new OrderListAdap(getActivity(), jArraySortList);
                lvOrder.setAdapter(adap);
            } else {
                tvNoRecords.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onResume() {
        Dashboard.ivDocument.setVisibility(View.GONE);
        Dashboard.ivSortArrows.setVisibility(View.GONE);
        Dashboard.ivSortUp.setVisibility(View.GONE);
        Dashboard.ivEdit.setVisibility(View.GONE);
        Dashboard.ivSortDown.setVisibility(View.VISIBLE);
        Dashboard.ivService.setVisibility(View.GONE);
        Dashboard.ivDocumnetEdit.setVisibility(View.GONE);
        Dashboard.ivServiceEdit.setVisibility(View.GONE);

        Dashboard.ivDrawerIndicator.setVisibility(View.VISIBLE);
        Dashboard.ivBack.setVisibility(View.GONE);
        Dashboard.tvTitle.setText("Auftragsliste (sortiert)");

        Dashboard.ivSortUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dashboard.ivSortDown.setVisibility(View.VISIBLE);
                Dashboard.ivSortUp.setVisibility(View.GONE);
                OrderListAdap adap = new OrderListAdap(getActivity(), jArraySortList);
                lvOrder.setAdapter(adap);
            }

        });

        Dashboard.ivSortDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dashboard.ivSortDown.setVisibility(View.GONE);
                Dashboard.ivSortUp.setVisibility(View.VISIBLE);
                JSONArray jArrayReverse = new JSONArray();
                int length = jArraySortList.length() - 1;
                for (int i = length; i >= 0; i--) {
                    try {
                        jArrayReverse.put(jArraySortList.getJSONObject(i));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                OrderListAdap adap = new OrderListAdap(getActivity(), jArrayReverse);
                lvOrder.setAdapter(adap);
            }

        });
        super.onResume();
    }

    public static void getSortList() {
        try {

            HashMap<String, String> postDataParams = new HashMap<>();
            String auftragnr = "";
            String erpnr = "";
            String objekt = "";
            String fälligkeit = "";
            String plantermin = "";

            if (!Common.getSharedPreferences(myContext, "auftragnr").equals("NA")) {
                auftragnr = Common.getSharedPreferences(myContext, "auftragnr");
            } else if (!Common.getSharedPreferences(myContext, "erpnr").equals("NA")) {
                erpnr = Common.getSharedPreferences(myContext, "erpnr");
            } else if (!Common.getSharedPreferences(myContext, "objekt").equals("NA")) {
                objekt = Common.getSharedPreferences(myContext, "objekt");
            } else if (!Common.getSharedPreferences(myContext, "fälligkeit").equals("NA")) {
                fälligkeit = Common.getSharedPreferences(myContext, "fälligkeit");
            } else if (!Common.getSharedPreferences(myContext, "plantermin").equals("NA")) {
                plantermin = Common.getSharedPreferences(myContext, "plantermin");
            }

            postDataParams.put("userid", Common.USER_SESSION);
            postDataParams.put("sorttype", "desc");
//            postDataParams.put("auftragnr", auftragnr);
//            postDataParams.put("erpnr", erpnr);
//            postDataParams.put("objekt", objekt);
//            postDataParams.put("fälligkeit", fälligkeit);
//            postDataParams.put("plantermin", plantermin);

            postDataParams.put("auftragnr", "");
            postDataParams.put("erpnr", "");
            postDataParams.put("objekt", "");
            postDataParams.put("fälligkeit", "");
            postDataParams.put("plantermin", "");



            postDataParams.put("sortcolumn", Common.SORT_COLUMN);

            HttpConnection httpConnection = new HttpConnection(staticDelegate, myContext);
            httpConnection.setRequestedfor("getSOrderList");

            httpConnection.setPostDataParams(postDataParams);
            httpConnection.setUrl("Orders/GetAssignOrdersWithSearchAndSort");
            httpConnection.setIsloading(true);
            httpConnection.execute("");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void httpResponse(String response, String requestedFor) {

        try {
            Common.stopProgressDialouge(requestedFor);
            if (requestedFor.equals("getSOrderList")) {
                JSONObject jObj = new JSONObject(response);
                if (jObj.getString("ResultCode").equals("SUCCESS")) {
                    jArraySortList = jObj.getJSONArray("ResultObject");
                    if (jArraySortList.length() > 0) {
                        OrderListAdap adap = new OrderListAdap(getActivity(), jArraySortList);
                        lvOrder.setAdapter(adap);
                        tvNoRecords.setVisibility(View.GONE);
                    } else {
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
    public void httpFailure(String response, String requestedFor) throws Exception {
    }
}
