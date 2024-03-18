package com.visafm.roombook.fragments;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.visafm.roombook.Dashboard;
import com.visafm.roombook.R;
import com.visafm.roombook.common.BaseClass;
import com.visafm.roombook.common.Common;
import com.visafm.roombook.common.HttpConnection;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Ankit Patel on 24/02/17.
 */
public class FrgOrderDetail extends Fragment implements BaseClass {

    BaseClass delegate = this;
    TextView tvOrderNrERP;
    TextView tvGebäude;
    TextView tvStockwerk;
    TextView tvObjekt;
    TextView tvOrt;
    TextView tvFälligkeit;
    TextView tvPlandatum;
    TextView tvDurchfuehrung;
    TextView tvFertigmeldung;
    TextView tvAuftragsart;
    TextView tvStatus;
    TextView tvGewerk;
    TextView tvAusführung;
    TextView tvPriority;
    TextView tvAuftragskurzbeschreibung;
    TextView tvAuftragsbezeichnung;
    TextView tvKST;
    TextView tvZeitaufwand;
    TextView tvDatumStart;
    TextView tvDatumEnde;
    TextView tvKosten;
    TextView tvLeistung;

    JSONObject jObjTemp = new JSONObject();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_orderdetail, container, false);
        try {
            if (Common.getSharedPreferences(getContext(), "EditOrdersFromPage").equalsIgnoreCase("True")) {
                tvOrderNrERP = rootView.findViewById(R.id.tvOrderNrERP);
                tvGebäude = rootView.findViewById(R.id.tvGebäude);
                tvStockwerk = rootView.findViewById(R.id.tvStockwerk);
                tvObjekt = rootView.findViewById(R.id.tvObjekt);
                tvOrt = rootView.findViewById(R.id.tvOrt);
                tvFälligkeit = rootView.findViewById(R.id.tvFälligkeit);
                tvPlandatum = rootView.findViewById(R.id.tvPlandatum);
                tvDurchfuehrung = rootView.findViewById(R.id.tvDurchfuehrung);
                tvFertigmeldung = rootView.findViewById(R.id.tvFertigmeldung);
                tvAuftragsart = rootView.findViewById(R.id.tvAuftragsart);
                tvStatus = rootView.findViewById(R.id.tvStatus);
                tvGewerk = rootView.findViewById(R.id.tvGewerk);
                tvAusführung = rootView.findViewById(R.id.tvAusführung);
                tvPriority = rootView.findViewById(R.id.tvPriority);
                tvAuftragskurzbeschreibung = rootView.findViewById(R.id.tvAuftragskurzbeschreibung);
                tvAuftragsbezeichnung = rootView.findViewById(R.id.tvAuftragsbezeichnung);
                tvKST = rootView.findViewById(R.id.tvKST);
                tvZeitaufwand = rootView.findViewById(R.id.tvZeitaufwand);
                tvDatumStart = rootView.findViewById(R.id.tvDatumStart);
                tvDatumEnde = rootView.findViewById(R.id.tvDatumEnde);
                tvKosten = rootView.findViewById(R.id.tvKosten);
                tvLeistung = rootView.findViewById(R.id.tvLeistung);

                JSONObject jObjTemp = Common.selectedOrderDetail;

                tvOrderNrERP.setText(jObjTemp.getString("OrderNrERP").trim());
                tvGebäude.setText(jObjTemp.getString("Gebäude").trim());
                tvStockwerk.setText(jObjTemp.getString("Stockwerk").trim());
                tvObjekt.setText(jObjTemp.getString("Objekt").trim());
                tvOrt.setText(jObjTemp.getString("Ort").trim());
                tvAuftragsart.setText(jObjTemp.getString("Auftragsart").trim());
                tvStatus.setText(jObjTemp.getString("Status").trim());
                tvGewerk.setText(jObjTemp.getString("Gewerk").trim());
                tvAusführung.setText(jObjTemp.getString("Ausführung").trim());
                tvPriority.setText(jObjTemp.getString("Priority").trim());
                tvAuftragskurzbeschreibung.setText(jObjTemp.getString("Auftragskurzbeschreibung").trim());
                tvAuftragsbezeichnung.setText(jObjTemp.getString("Auftragsbezeichnung").trim());

                if (jObjTemp.getString("Durchfuehrung").equals("True")) {
                    tvDurchfuehrung.setText("✓");
                    tvDurchfuehrung.setTextColor(ContextCompat.getColor(getActivity(), R.color.txt_green));
                } else {
                    tvDurchfuehrung.setText("✗");
                    tvDurchfuehrung.setTextColor(Color.RED);
                }
                if (jObjTemp.getString("Fertigmeldung").equals("True")) {
                    tvFertigmeldung.setText("✓");
                    tvFertigmeldung.setTextColor(ContextCompat.getColor(getActivity(), R.color.txt_green));
                } else {
                    tvFertigmeldung.setText("✗");
                    tvFertigmeldung.setTextColor(Color.RED);
                }
                getOrderDetail();

                tvFälligkeit.setText(Common.dateFormate(jObjTemp.getString("Fälligkeit"), "dd.MM.yyyy hh:mm:ss", "dd.MM.yyyy"));
                tvPlandatum.setText(Common.dateFormate(jObjTemp.getString("Plandatum"), "dd.MM.yyyy hh:mm:ss", "dd.MM.yyyy"));
            }
            //TODO
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }// onCreateView

    @Override
    public void onResume() {
        JSONObject jObjTemp = Common.selectedOrderDetail;
        Dashboard.ivDocument.setVisibility(View.VISIBLE);
        Dashboard.ivService.setVisibility(View.VISIBLE);
        Dashboard.ivSortArrows.setVisibility(View.GONE);
        Dashboard.ivSortDown.setVisibility(View.GONE);
        Dashboard.ivSortArrows.setVisibility(View.GONE);
        Dashboard.ivSortUp.setVisibility(View.GONE);
        Dashboard.ivEdit.setVisibility(View.VISIBLE);
        Dashboard.ivDocumnetEdit.setVisibility(View.GONE);
        Dashboard.ivDrawerIndicator.setVisibility(View.GONE);
        Dashboard.ivBack.setVisibility(View.VISIBLE);
        Dashboard.ivServiceEdit.setVisibility(View.GONE);
        try {
            Dashboard.tvTitle.setText(("Auftrag Nr.") + ":" + jObjTemp.getString("OrderNr").trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ViewGroup viewGroup = (ViewGroup) getView();
        viewGroup.removeAllViewsInLayout();
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        populateViewForOrientation(inflater, viewGroup, newConfig.orientation);
    }

    private void getOrderDetail() {
        try {
            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("orderid", Common.selectedOrderDetail.getString("OrderNr"));
            HttpConnection httpConnection = new HttpConnection(delegate, getActivity());
            httpConnection.setRequestedfor("getOrderDetail");
            httpConnection.setIsloading(false);
            httpConnection.setPostDataParams(postDataParams);
            httpConnection.setUrl("Orders/GetAssignOrdersDetail");
            httpConnection.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateViewForOrientation(LayoutInflater inflater, ViewGroup viewGroup, Integer orientation) {
        View rootView;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            rootView = inflater.inflate(R.layout.frg_orderdetail_horizontal, viewGroup);
        } else {
            rootView = inflater.inflate(R.layout.frg_orderdetail, viewGroup);
        }
        try {
            tvOrderNrERP = rootView.findViewById(R.id.tvOrderNrERP);
            tvGebäude = rootView.findViewById(R.id.tvGebäude);
            tvStockwerk = rootView.findViewById(R.id.tvStockwerk);
            tvObjekt = rootView.findViewById(R.id.tvObjekt);
            tvOrt = rootView.findViewById(R.id.tvOrt);
            tvFälligkeit = rootView.findViewById(R.id.tvFälligkeit);
            tvPlandatum = rootView.findViewById(R.id.tvPlandatum);
            tvDurchfuehrung = rootView.findViewById(R.id.tvDurchfuehrung);
            tvFertigmeldung = rootView.findViewById(R.id.tvFertigmeldung);
            tvAuftragsart = rootView.findViewById(R.id.tvAuftragsart);
            tvStatus = rootView.findViewById(R.id.tvStatus);
            tvGewerk = rootView.findViewById(R.id.tvGewerk);
            tvAusführung = rootView.findViewById(R.id.tvAusführung);
            tvPriority = rootView.findViewById(R.id.tvPriority);
            tvAuftragskurzbeschreibung = rootView.findViewById(R.id.tvAuftragskurzbeschreibung);
            tvAuftragsbezeichnung = rootView.findViewById(R.id.tvAuftragsbezeichnung);

            tvKST = rootView.findViewById(R.id.tvKST);
            tvZeitaufwand = rootView.findViewById(R.id.tvZeitaufwand);
            tvDatumStart = rootView.findViewById(R.id.tvDatumStart);
            tvDatumEnde = rootView.findViewById(R.id.tvDatumEnde);
            tvKosten = rootView.findViewById(R.id.tvKosten);
            tvLeistung = rootView.findViewById(R.id.tvLeistung);

            JSONObject jObjTemp = Common.selectedOrderDetail;

            tvOrderNrERP.setText(jObjTemp.getString("OrderNrERP").trim());
            tvGebäude.setText(jObjTemp.getString("Gebäude").trim());
            tvStockwerk.setText(jObjTemp.getString("Stockwerk").trim());
            tvObjekt.setText(jObjTemp.getString("Objekt").trim());
            tvOrt.setText(jObjTemp.getString("Ort").trim());
            tvFälligkeit.setText(Common.dateFormate(jObjTemp.getString("Fälligkeit"), "dd.MM.yyyy hh:mm:ss", "dd.MM.yyyy"));
            tvPlandatum.setText(Common.dateFormate(jObjTemp.getString("Plandatum"), "dd.MM.yyyy hh:mm:ss", "dd.MM.yyyy"));
            tvAuftragsart.setText(jObjTemp.getString("Auftragsart").trim());
            tvStatus.setText(jObjTemp.getString("Status").trim());
            tvGewerk.setText(jObjTemp.getString("Gewerk").trim());
            tvAusführung.setText(jObjTemp.getString("Ausführung").trim());
            tvPriority.setText(jObjTemp.getString("Priority").trim());
            tvAuftragskurzbeschreibung.setText(jObjTemp.getString("Auftragskurzbeschreibung").trim());
            tvAuftragsbezeichnung.setText(jObjTemp.getString("Auftragsbezeichnung").trim());

            getOrderDetail();

            if (jObjTemp.getString("Durchfuehrung").equals("True")) {
                tvDurchfuehrung.setText("✓");
                tvDurchfuehrung.setTextColor(ContextCompat.getColor(getActivity(), R.color.txt_green));
            } else {
                tvDurchfuehrung.setText("✗");
                tvDurchfuehrung.setTextColor(Color.RED);
            }
            if (jObjTemp.getString("Fertigmeldung").equals("True")) {
                tvFertigmeldung.setText("✓");
                tvFertigmeldung.setTextColor(ContextCompat.getColor(getActivity(), R.color.txt_green));
            } else {
                tvFertigmeldung.setText("✗");
                tvFertigmeldung.setTextColor(Color.RED);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    } //populateViewForOrientation()

    private void fillDetails(JSONObject jObj) throws Exception {
        Common.editOrderDetail = jObj;
        tvKST.setText(jObj.getString("KST"));
        tvKosten.setText(jObj.getString("Kosten"));
        tvDatumStart.setText(jObj.getString("DatumStart"));
        tvDatumEnde.setText(jObj.getString("DatumEnde"));
        tvLeistung.setText(jObj.getString("Leistung"));
        tvZeitaufwand.setText(jObj.getString("Zeitaufwand"));
        //Application ID is set here
        if (Common.isStringEmpty(Common.APPLICATIONID)) {
            if (Common.isStringEmpty(jObj.getString("ApplicationID"))) {
                Common.APPLICATIONID = "";
            } else {
                Common.APPLICATIONID = jObj.getString("ApplicationID");
                Log.e("ordDetAPPID",Common.APPLICATIONID);
            }
        }

        if (jObj.getString("Fertigmeldung").equals("True")) {
            tvFertigmeldung.setText("✓");
            tvFertigmeldung.setTextColor(ContextCompat.getColor(getActivity(), R.color.txt_green));
        } else {
            tvFertigmeldung.setText("✗");
            tvFertigmeldung.setTextColor(Color.RED);
        }
    }

    @Override
    public void httpResponse(String response, String requestedFor) throws Exception {

        if (requestedFor.equals("getOrderDetail")) {
            Common.stopProgressDialouge(requestedFor);
            JSONObject jObj = new JSONObject(response);
            if (jObj.getString("ResultCode").equals("SUCCESS")) {
                jObjTemp = jObj.getJSONObject("ResultObject");
                fillDetails(jObj.getJSONObject("ResultObject"));

            }
        }
    }

    @Override
    public void httpFailure(String response, String requestedFor) throws Exception {
    }

}
