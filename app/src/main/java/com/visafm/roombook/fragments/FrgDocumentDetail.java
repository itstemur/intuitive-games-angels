package com.visafm.roombook.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.visafm.roombook.Dashboard;
import com.visafm.roombook.R;
import com.visafm.roombook.common.BaseClass;
import com.visafm.roombook.common.Common;
import com.visafm.roombook.common.HttpConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Ankit Patel on 20/02/19.
 */
public class FrgDocumentDetail extends Fragment implements BaseClass {

    BaseClass delegate = this;
    TextView tvdocFK_TlkpDocTypes_Art; //getArt
    TextView tvfilLabel_Bezeichnung;  //etBezeichnung
    TextView tvfilDescription_Beschreibung; //etBeschreibung
    TextView tvdocReminder_Wiedervorlage; //etWiedervorlage
    TextView tvbinFilesize;
    TextView tvdocFK_TlkpDocTypeOptions_Unterart; //getUnterart
    ImageView IvbinData;
    TextView tvdocValidFrom;
    TextView tvdocValidTo;
    TextView tvdocIsOperatingManual;
    TextView tvdocIsMaintenance;
    TextView tvdocIsRentMgmt;
    TextView tvdocIsCostMgmt;
    String StrDocTypes_Art;
    String StrDocTypeOptions_Unterart = "";


    JSONObject jObjTemp = new JSONObject();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_documentdetail, container, false);
        try {
            tvdocFK_TlkpDocTypes_Art = rootView.findViewById(R.id.tvdocFK_TlkpDocTypes_Art);
            tvfilLabel_Bezeichnung = rootView.findViewById(R.id.tvfilLabel_Bezeichnung);
            tvfilDescription_Beschreibung = rootView.findViewById(R.id.tvfilDescription_Beschreibung);
            tvdocReminder_Wiedervorlage = rootView.findViewById(R.id.tvdocReminder_Wiedervorlage);
            tvbinFilesize = rootView.findViewById(R.id.tvbinFilesize);
            tvdocFK_TlkpDocTypeOptions_Unterart = rootView.findViewById(R.id.tvdocFK_TlkpDocTypeOptions_Unterart);
            IvbinData = rootView.findViewById(R.id.IvbinData);
            tvdocValidFrom = rootView.findViewById(R.id.tvdocValidFrom);
            tvdocValidTo = rootView.findViewById(R.id.tvdocValidTo);
            tvdocIsMaintenance = rootView.findViewById(R.id.tvdocIsMaintenance);
            tvdocIsRentMgmt = rootView.findViewById(R.id.tvdocIsRentMgmt);
            tvdocIsCostMgmt = rootView.findViewById(R.id.tvdocIsCostMgmt);
            tvdocIsOperatingManual = rootView.findViewById(R.id.tvdocIsOperatingManual);
            jObjTemp = Common.selectedDocumentDetail;
            if (Common.getSharedPreferences(getContext(), "EditDocumentDetailFromPage").equalsIgnoreCase("True")) {
                getArt();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }// onCreateView


    private void getArt() {
        try {
            HttpConnection httpConnection = new HttpConnection(delegate, getActivity());
            httpConnection.setRequestedfor("getArt");
            httpConnection.setIsloading(true);
            httpConnection.setUrl("Orders/DocTypes");
            httpConnection.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getUnterart() {
        try {
            HashMap<String, String> postDataParams = new HashMap<>();
            HttpConnection httpConnection = new HttpConnection(delegate, getActivity());
            httpConnection.setRequestedfor("getUnterart");
            postDataParams.put("docoFK_TlkpDocTypes", jObjTemp.getString("docFK_TlkpDocTypes").trim());
            httpConnection.setIsloading(false);
            httpConnection.setPostDataParams(postDataParams);
            httpConnection.setUrl("Orders/DocTypeOptions");
            httpConnection.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillDetails() {

        try {
            jObjTemp = Common.selectedDocumentDetail;
            Log.v("jObjTemp", jObjTemp.toString());
            tvdocFK_TlkpDocTypes_Art.setText(StrDocTypes_Art);
            if (!Common.isStringEmpty(jObjTemp.getString("filLabel"))) {
                tvfilLabel_Bezeichnung.setText(jObjTemp.getString("filLabel").trim());
            } else {
                tvfilLabel_Bezeichnung.setText("-");
            }
            if (!Common.isStringEmpty(jObjTemp.getString("filDescription"))) {
                tvfilDescription_Beschreibung.setText(jObjTemp.getString("filDescription").trim());
            } else {
                tvfilDescription_Beschreibung.setText("-");
            }
            if (!Common.isStringEmpty(jObjTemp.getString("docReminder"))) {
                tvdocReminder_Wiedervorlage.setText(jObjTemp.getString("docReminder").trim());
            } else {
                tvdocReminder_Wiedervorlage.setText("-");
            }
            if (!Common.isStringEmpty(StrDocTypeOptions_Unterart)) {
                tvdocFK_TlkpDocTypeOptions_Unterart.setText(StrDocTypeOptions_Unterart);
            } else {
                tvdocFK_TlkpDocTypeOptions_Unterart.setText("-");
            }
            byte[] decodedString = Base64.decode((jObjTemp.getString("binData").toString().getBytes()), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            IvbinData.setImageBitmap(decodedByte);

            if (jObjTemp.getString("docIsOperatingManual").equals("true")) {
                tvdocIsOperatingManual.setText("✓");
                tvdocIsOperatingManual.setGravity(Gravity.RIGHT);
                tvdocIsOperatingManual.setTextColor(ContextCompat.getColor(getActivity(), R.color.txt_green));
            } else {
                tvdocIsOperatingManual.setText("✗");
                tvdocIsOperatingManual.setGravity(Gravity.RIGHT);
                tvdocIsOperatingManual.setTextColor(Color.RED);
            }
            if (jObjTemp.getString("docIsMaintenance").equals("true")) {
                tvdocIsMaintenance.setText("✓");
                tvdocIsMaintenance.setGravity(Gravity.RIGHT);
                tvdocIsMaintenance.setTextColor(ContextCompat.getColor(getActivity(), R.color.txt_green));
            } else {
                tvdocIsMaintenance.setText("✗");
                tvdocIsMaintenance.setGravity(Gravity.RIGHT);
                tvdocIsMaintenance.setTextColor(Color.RED);
            }
            if (jObjTemp.getString("docIsRentMgmt").equals("true")) {
                tvdocIsRentMgmt.setText("✓");
                tvdocIsRentMgmt.setGravity(Gravity.RIGHT);
                tvdocIsRentMgmt.setTextColor(ContextCompat.getColor(getActivity(), R.color.txt_green));
            } else {
                tvdocIsRentMgmt.setText("✗");
                tvdocIsRentMgmt.setGravity(Gravity.RIGHT);
                tvdocIsRentMgmt.setTextColor(Color.RED);
            }
            if (jObjTemp.getString("docIsCostMgmt").equals("true")) {
                tvdocIsCostMgmt.setText("✓");
                tvdocIsCostMgmt.setGravity(Gravity.RIGHT);
                tvdocIsCostMgmt.setTextColor(ContextCompat.getColor(getActivity(), R.color.txt_green));
            } else {
                tvdocIsCostMgmt.setText("✗");
                tvdocIsCostMgmt.setGravity(Gravity.RIGHT);
                tvdocIsCostMgmt.setTextColor(Color.RED);
            }
            if (!Common.isStringEmpty(jObjTemp.getString("docValidFrom"))) {
                tvdocValidFrom.setText((jObjTemp.getString("docValidFrom").trim()));
                Log.v("docValidFrom", "not empty");

            } else {
                tvdocValidFrom.setText("-");
            }
            if (!Common.isStringEmpty(jObjTemp.getString("docValidTo"))) {
                Log.v("docValidTo", "not empty");
                tvdocValidTo.setText((jObjTemp.getString("docValidTo").trim()));

            } else {
                tvdocValidTo.setText("-");
            }
            Common.stopProgressDialouge("getArt");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResume() {
        Dashboard.ivDocument.setVisibility(View.GONE);
        Dashboard.ivService.setVisibility(View.GONE);
        Dashboard.ivSortArrows.setVisibility(View.GONE);
        Dashboard.ivSortDown.setVisibility(View.GONE);
        Dashboard.ivSortArrows.setVisibility(View.GONE);
        Dashboard.ivSortUp.setVisibility(View.GONE);
        Dashboard.ivEdit.setVisibility(View.GONE);
        Dashboard.ivServiceEdit.setVisibility(View.GONE);
        Dashboard.ivDrawerIndicator.setVisibility(View.GONE);
        Dashboard.ivBack.setVisibility(View.VISIBLE);
        Dashboard.ivDocumnetEdit.setVisibility(View.VISIBLE);

        Dashboard.tvTitle.setText("Dokument");
        super.onResume();
    }

    @Override
    public void httpResponse(String response, String requestedFor) throws Exception {

        if (requestedFor.equals("getArt")) {
            JSONObject jArtObj = new JSONObject(response);
            if (jArtObj.getString("ResultCode").equals("SUCCESS")) {
                JSONArray jArrayArtList = jArtObj.getJSONArray("ResultObject");
                String StrdocFK_TlkpDocTypes_Art = jObjTemp.getString("docFK_TlkpDocTypes").trim();
                if (jArrayArtList.length() > 0) {
                    for (int i = 0; i < jArrayArtList.length(); i++) {
                        if (StrdocFK_TlkpDocTypes_Art == jArrayArtList.getJSONObject(i).getString("doctID").trim()) {
                            StrDocTypes_Art = jArrayArtList.getJSONObject(i).getString("doctText1").trim();
                        }
                    }
                    getUnterart();
                }
            } else {
                Common.showAlert(getActivity(), jArtObj.getString("ResultMessage"));
            }

        }

        if (requestedFor.equals("getUnterart")) {
            JSONObject jSubArtObj = new JSONObject(response);
            if (jSubArtObj.getString("ResultCode").equals("SUCCESS")) {
                JSONArray jArrayUnterartList = jSubArtObj.getJSONArray("ResultObject");
                String StrdocFK_TlkpDocTypeOptions = jObjTemp.getString("docFK_TlkpDocTypeOptions").trim();
                for (int k = 0; k < jArrayUnterartList.length(); k++) {
                    if (StrdocFK_TlkpDocTypeOptions == jArrayUnterartList.getJSONObject(k).getString("docoID").trim()) {
                        StrDocTypeOptions_Unterart = jArrayUnterartList.getJSONObject(k).getString("docoText1").trim();

                    }
                }
                fillDetails();

            } else {
                Common.showAlert(getActivity(), jSubArtObj.getString("ResultMessage"));
            }

        }

    }

    @Override
    public void httpFailure(String response, String requestedFor) throws Exception {

    }
}
