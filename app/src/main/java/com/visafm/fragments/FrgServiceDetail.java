package com.visafm.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.visafm.Dashboard;
import com.visafm.R;
import com.visafm.common.BaseClass;
import com.visafm.common.Common;
import com.visafm.common.HttpConnection;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class FrgServiceDetail extends Fragment implements BaseClass {
    BaseClass delegate = this;
    TextView tvWDetailID;
    TextView tvWartungID;
    TextView tvAbteilung;
    TextView tvArt;
    TextView tvBeschreibung1;
    TextView tvVon;
    TextView tvBIS;
    TextView tvKosten;
    TextView tvZeitaufwand;
    TextView tvFertigmeldung;
    TextView tvStatus;
    TextView tvMelder;
    TextView tvMaterialEinh;
    TextView tvMaterialArt;
    TextView tvMaterialAnzahl;

    TextView tvBeschreibung2;
    String loader;

    JSONObject jObjTemp = new JSONObject();

    @Override
/**
 * Created by Ankit Patel on 20/02/19.
 */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_servicedetail, container, false);
        try {
            tvWDetailID = rootView.findViewById(R.id.tvWDetailID);
            tvWartungID = rootView.findViewById(R.id.tvWartungID);
//            tvAbteilung = (TextView) rootView.findViewById(R.id.tvAbteilung);
            tvArt = rootView.findViewById(R.id.tvArt);
            tvBeschreibung1 = rootView.findViewById(R.id.tvBeschreibung1);
            tvVon = rootView.findViewById(R.id.tvVon);
            tvBIS = rootView.findViewById(R.id.tvBIS);
            tvMelder = rootView.findViewById(R.id.tvMelder);
            tvFertigmeldung = rootView.findViewById(R.id.tvFertigmeldung);
            tvStatus = rootView.findViewById(R.id.tvStatus);
            tvMaterialEinh = rootView.findViewById(R.id.tvMaterialEinh);
            tvMaterialArt = rootView.findViewById(R.id.tvMaterialArt);
            tvMaterialAnzahl = rootView.findViewById(R.id.tvMaterialAnzahl);
            tvZeitaufwand = rootView.findViewById(R.id.tvZeitaufwand);
            tvKosten = rootView.findViewById(R.id.tvKosten);
            tvBeschreibung2 = rootView.findViewById(R.id.tvBeschreibung2);
            jObjTemp = Common.selectedServiceDetail;
            tvWDetailID.setText(jObjTemp.getString("WDetailID").trim());
            tvWartungID.setText(jObjTemp.getString("WartungID").trim());
//            tvAbteilung.setText(jObjTemp.getString("Abteilung").trim());
            if (Common.getSharedPreferences(getContext(), "EditOrdersDetailFromPage").equalsIgnoreCase("True")) {

                if (Common.isStringEmpty(jObjTemp.getString("Art"))) {
                    tvArt.setText("");
                    if (Common.isStringEmpty(jObjTemp.getString("Beschreibung1"))) {
                        tvBeschreibung1.setText("");
                        if (Common.isStringEmpty(jObjTemp.getString("MaterialEinh"))) {
                            tvMaterialEinh.setText("");

                            if (Common.isStringEmpty(jObjTemp.getString("Melder"))) {
                                tvMelder.setText("");
                                if (Common.isStringEmpty(jObjTemp.getString("MaterialArt"))) {
                                    tvMaterialArt.setText("");
                                    loader = "noloader";
                                } else {
                                    Log.v("MaterialArt", jObjTemp.getString("MaterialArt"));
                                    loader = "getMaterialloader";
                                    getMaterial();
                                }

                            } else {
                                Log.v("Melder", jObjTemp.getString("Melder"));
                                loader = "getPersonloader";
                                getPerson();
                            }


                        } else {
                            Log.v("MaterialEinh", jObjTemp.getString("MaterialEinh"));
                            loader = "getEinheitloader";
                            getEinheit();


                        }

                    } else {
                        Log.v("Beschreibung1", jObjTemp.getString("Beschreibung1"));
                        loader = "getLeistungloader";
                        getLeistung();
                    }

                } else {
                    Log.v("Art", jObjTemp.getString("Art"));
                    loader = "getLeistungArtloader";
                    getLeistungArt();
                }
            }

            if (Common.isStringEmpty(jObjTemp.getString("Status").trim())) {
                tvStatus.setText("");
            } else {
                tvStatus.setText(jObjTemp.getString("Status").trim());
            }

            tvZeitaufwand.setText(jObjTemp.getString("Zeitaufwand").trim());

            if (Common.isStringEmpty(jObjTemp.getString("Beschreibung2").trim())) {
                tvBeschreibung2.setText("");
            } else {
                tvBeschreibung2.setText(jObjTemp.getString("Beschreibung2").trim());
            }
            if (Common.isStringEmpty(jObjTemp.getString("MaterialAnzahl").trim())) {
                tvMaterialAnzahl.setText("");
            } else {
                tvMaterialAnzahl.setText(jObjTemp.getString("MaterialAnzahl").trim());
            }






//            if (Common.isStringEmpty(jObjTemp.getString("Abteilung").trim())) {
//                tvAbteilung.setText("");
//            } else {
//                tvAbteilung.setText(jObjTemp.getString("Abteilung").trim());
//            }
            tvKosten.setText(jObjTemp.getString("Kosten").trim());


            if (jObjTemp.getString("Fertigmeldung").equals("True")) {
                tvFertigmeldung.setText("✓");
                tvFertigmeldung.setTextColor(ContextCompat.getColor(getActivity(), R.color.txt_green));
            } else {
                tvFertigmeldung.setText("✗");
                tvFertigmeldung.setTextColor(Color.RED);
            }
            tvVon.setText(Common.dateFormates(jObjTemp.getString("Von")));
            tvBIS.setText(Common.dateFormates(jObjTemp.getString("BIS")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }// onCreateView


    private void getLeistungArt() {
        try {
            HttpConnection httpConnection = new HttpConnection(delegate, getActivity());
            httpConnection.setRequestedfor("getLeistungArt");

            switch (loader) {
                case "getLeistungArtloader":
                    httpConnection.setIsloading(true);
                    break;
            }

            Log.e("getLeistungArtloading", "true");
            httpConnection.setUrl("Orders/BindArt");
            httpConnection.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getLeistung() {
        try {
            Log.v("Nu", Common.selectedOrderDetail.getString("OrderNr"));
            HashMap<String, String> postDataParams = new HashMap<>();
            HttpConnection httpConnection = new HttpConnection(delegate, getActivity());
            postDataParams.put("orderid", Common.selectedOrderDetail.getString("OrderNr"));
            httpConnection.setRequestedfor("getLeistung");
            switch (loader) {
                case "getLeistungArtloader":
                    httpConnection.setIsloading(false);
                    break;
                case "getLeistungloader":
                    httpConnection.setIsloading(true);
                    break;
            }
            httpConnection.setPostDataParams(postDataParams);
            httpConnection.setUrl("Orders/BindBeschreibung");
            httpConnection.execute("");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getPerson() {
        try {
            HashMap<String, String> postDataParams = new HashMap<>();
            HttpConnection httpConnection = new HttpConnection(delegate, getActivity());
            Log.v("Nu", Common.selectedOrderDetail.getString("OrderNr"));
            postDataParams.put("orderid", Common.selectedOrderDetail.getString("OrderNr"));
            httpConnection.setRequestedfor("getPerson");
            switch (loader) {
                case "getLeistungArtloader":
                    httpConnection.setIsloading(false);
                    break;
                case "getLeistungloader":
                    httpConnection.setIsloading(false);
                    break;
                case "getEinheitloader":
                    httpConnection.setIsloading(false);
                    break;
                case "getPersonloader":
                    httpConnection.setIsloading(true);
                    break;
            }
            httpConnection.setPostDataParams(postDataParams);
            httpConnection.setUrl("Orders/BindMelder");
            httpConnection.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getEinheit() {
        try {
            HttpConnection httpConnection = new HttpConnection(delegate, getActivity());
            httpConnection.setRequestedfor("getEinheit");
            switch (loader) {
                case "getLeistungArtloader":
                    httpConnection.setIsloading(false);
                    break;
                case "getLeistungloader":
                    httpConnection.setIsloading(false);
                    break;
                case "getEinheitloader":
                    httpConnection.setIsloading(true);
                    break;
            }
            httpConnection.setUrl("Orders/BindEinheit");
            httpConnection.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getMaterial() {
        try {
            HttpConnection httpConnection = new HttpConnection(delegate, getActivity());
            httpConnection.setRequestedfor("getMaterial");
            switch (loader) {
                case "getLeistungArtloader":
                    httpConnection.setIsloading(false);
                    break;
                case "getLeistungloader":
                    httpConnection.setIsloading(false);
                    break;
                case "getEinheitloader":
                    httpConnection.setIsloading(false);
                    break;
                case "getPersonloader":
                    httpConnection.setIsloading(false);
                    break;
                case "getMaterialloader":
                    httpConnection.setIsloading(true);
                    break;

            }

            httpConnection.setUrl("Orders/BindMaterial");

            Log.e("StringLoader", loader);
            httpConnection.execute("");
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
        Dashboard.ivDocumnetEdit.setVisibility(View.GONE);
        Dashboard.ivSortUp.setVisibility(View.GONE);
        Dashboard.ivEdit.setVisibility(View.GONE);
        Dashboard.ivServiceEdit.setVisibility(View.VISIBLE);
        Dashboard.ivDrawerIndicator.setVisibility(View.GONE);
        Dashboard.ivBack.setVisibility(View.VISIBLE);
        Dashboard.tvTitle.setText("Leistungsdetails");
        super.onResume();
    }


    @Override
    public void httpResponse(String response, String requestedFor) throws Exception {

        if (requestedFor.equals("getLeistungArt")) {
            {
                JSONObject jArtObj = new JSONObject(response);
                if (jArtObj.getString("ResultCode").equals("SUCCESS")) {
                    JSONArray jArrayLeistungArtList = jArtObj.getJSONArray("ResultObject");
                    Log.v("Art", jObjTemp.getString("Art"));

                    if (jArrayLeistungArtList.length() > 0) {
                        String strLeistungsartList = jObjTemp.getString("Art");
                        for (int i = 0; i < jArrayLeistungArtList.length(); i++) {
                            JSONObject jObjLeistungArt = jArrayLeistungArtList.getJSONObject(i);

                            if (jObjLeistungArt.getString("ArtID").equalsIgnoreCase(strLeistungsartList)) {
                                tvArt.setText(jObjLeistungArt.getString("Beschreibung").toString().trim());
                                if (Common.isStringEmpty(jObjTemp.getString("Beschreibung1"))) {
                                    tvBeschreibung1.setText("");
                                    if (Common.isStringEmpty(jObjTemp.getString("MaterialEinh"))) {
                                        tvMaterialEinh.setText("");
                                        if (Common.isStringEmpty(jObjTemp.getString("Melder"))) {
                                            tvMelder.setText("");
                                            if (Common.isStringEmpty(jObjTemp.getString("MaterialArt"))) {
                                                tvMaterialArt.setText("");
                                                switch (loader) {
                                                    case "getLeistungArtloader":
                                                        Common.stopProgressDialouge("getLeistungArt");
                                                        break;
                                                    case "getLeistungloader":
                                                        Common.stopProgressDialouge("getLeistung");
                                                        break;
                                                    case "getEinheitloader":
                                                        Common.stopProgressDialouge("getEinheit");
                                                        break;
                                                    case "getPersonloader":
                                                        Common.stopProgressDialouge("getPerson");
                                                        break;
                                                    case "getMaterialloader":
                                                        Common.stopProgressDialouge("getMaterial");
                                                        break;
                                                }
                                            } else {
                                                Log.v("MaterialArt", jObjTemp.getString("MaterialArt"));
                                                getMaterial();
                                            }

                                        } else {
                                            Log.v("Melder", jObjTemp.getString("Melder"));
                                            getPerson();
                                        }

                                    } else {
                                        Log.v("MaterialEinh", jObjTemp.getString("MaterialEinh"));
                                        getEinheit();

                                    }

                                } else {
                                    Log.v("Beschreibung1", jObjTemp.getString("Beschreibung1"));
                                    getLeistung();
                                }


                                break;

                            }
                        }


                    } else {

                        if (Common.isStringEmpty(jObjTemp.getString("Beschreibung1"))) {
                            tvBeschreibung1.setText("");
                            if (Common.isStringEmpty(jObjTemp.getString("MaterialEinh"))) {
                                tvMaterialEinh.setText("");
                                if (Common.isStringEmpty(jObjTemp.getString("Melder"))) {
                                    tvMelder.setText("");
                                    if (Common.isStringEmpty(jObjTemp.getString("MaterialArt"))) {
                                        tvMaterialArt.setText("");
                                        switch (loader) {
                                            case "getLeistungArtloader":
                                                Common.stopProgressDialouge("getLeistungArt");
                                                break;
                                            case "getLeistungloader":
                                                Common.stopProgressDialouge("getLeistung");
                                                break;
                                            case "getEinheitloader":
                                                Common.stopProgressDialouge("getEinheit");
                                                break;
                                            case "getPersonloader":
                                                Common.stopProgressDialouge("getPerson");
                                                break;
                                            case "getMaterialloader":
                                                Common.stopProgressDialouge("getMaterial");
                                                break;
                                        }
                                    } else {
                                        Log.v("MaterialArt", jObjTemp.getString("MaterialArt"));
                                        getMaterial();
                                    }

                                } else {
                                    Log.v("Melder", jObjTemp.getString("Melder"));
                                    getPerson();
                                }

                            } else {
                                Log.v("MaterialEinh", jObjTemp.getString("MaterialEinh"));
                                getEinheit();

                            }

                        } else {
                            Log.v("Beschreibung1", jObjTemp.getString("Beschreibung1"));
                            getLeistung();
                        }
                    }


                } else {
                    Common.showAlert(getActivity(), jArtObj.getString("ResultMessage"));
                }
            }
        }


        if (requestedFor.equals("getLeistung")) {

            {
                JSONObject jLeistungObj = new JSONObject(response);
                if (jLeistungObj.getString("ResultCode").equals("SUCCESS")) {
                    JSONArray jArrayLeistung = jLeistungObj.getJSONArray("ResultObject");
                    if (jArrayLeistung.length() > 0) {
                        String strLeistung = jObjTemp.getString("Beschreibung1");
                        for (int j = 0; j < jArrayLeistung.length(); j++) {
                            JSONObject jObjLeistung = jArrayLeistung.getJSONObject(j);
                            if (jObjLeistung.getString("Beschreibung1").equalsIgnoreCase(strLeistung)) {
                                tvBeschreibung1.setText(jObjLeistung.getString("Beschreibung1").toString().trim());


                                if (Common.isStringEmpty(jObjTemp.getString("MaterialEinh"))) {
                                    tvMaterialEinh.setText("");
                                    if (Common.isStringEmpty(jObjTemp.getString("Melder"))) {
                                        tvMelder.setText("");
                                        if (Common.isStringEmpty(jObjTemp.getString("MaterialArt"))) {
                                            tvMaterialArt.setText("");
                                            switch (loader) {
                                                case "getLeistungArtloader":
                                                    Common.stopProgressDialouge("getLeistungArt");
                                                    break;
                                                case "getLeistungloader":
                                                    Common.stopProgressDialouge("getLeistung");
                                                    break;
                                                case "getEinheitloader":
                                                    Common.stopProgressDialouge("getEinheit");
                                                    break;
                                                case "getPersonloader":
                                                    Common.stopProgressDialouge("getPerson");
                                                    break;
                                                case "getMaterialloader":
                                                    Common.stopProgressDialouge("getMaterial");
                                                    break;
                                            }
                                        } else {
                                            Log.v("MaterialArt", jObjTemp.getString("MaterialArt"));
                                            getMaterial();
                                        }

                                    } else {
                                        Log.v("Melder", jObjTemp.getString("Melder"));
                                        getPerson();
                                    }

                                } else {
                                    Log.v("MaterialEinh", jObjTemp.getString("MaterialEinh"));
                                    getEinheit();
                                }


                                break;
                            }
                        }
                    } else {
                        if (Common.isStringEmpty(jObjTemp.getString("MaterialEinh"))) {
                            tvMaterialEinh.setText("");
                            if (Common.isStringEmpty(jObjTemp.getString("Melder"))) {
                                tvMelder.setText("");
                                if (Common.isStringEmpty(jObjTemp.getString("MaterialArt"))) {
                                    tvMaterialArt.setText("");
                                    switch (loader) {
                                        case "getLeistungArtloader":
                                            Common.stopProgressDialouge("getLeistungArt");
                                            break;
                                        case "getLeistungloader":
                                            Common.stopProgressDialouge("getLeistung");
                                            break;
                                        case "getEinheitloader":
                                            Common.stopProgressDialouge("getEinheit");
                                            break;
                                        case "getPersonloader":
                                            Common.stopProgressDialouge("getPerson");
                                            break;
                                        case "getMaterialloader":
                                            Common.stopProgressDialouge("getMaterial");
                                            break;
                                    }
                                } else {
                                    Log.v("MaterialArt", jObjTemp.getString("MaterialArt"));
                                    getMaterial();
                                }

                            } else {
                                Log.v("Melder", jObjTemp.getString("Melder"));
                                getPerson();
                            }

                        } else {
                            Log.v("MaterialEinh", jObjTemp.getString("MaterialEinh"));
                            getEinheit();

                        }
                    }

                } else {
                    Common.showAlert(getActivity(), jLeistungObj.getString("ResultMessage"));
                }

            }
        }

        if (requestedFor.equals("getPerson")) {
            {
                JSONObject jPersonObj = new JSONObject(response);
                if (jPersonObj.getString("ResultCode").equals("SUCCESS")) {
                    JSONArray jArrayPerson = jPersonObj.getJSONArray("ResultObject");
                    if (jArrayPerson.length() > 0) {
                        String strPerson = jObjTemp.getString("Melder");
                        for (int k = 0; k < jArrayPerson.length(); k++) {
                            JSONObject jObjyPerson = jArrayPerson.getJSONObject(k);

                            if (jObjyPerson.getString("MitarbeiterID").equalsIgnoreCase(strPerson)) {
                                tvMelder.setText(jObjyPerson.getString("Durchgeführt").toString().trim());

                                Log.v("tvMelder", jObjyPerson.getString("Durchgeführt").toString().trim());

                                if (Common.isStringEmpty(jObjTemp.getString("MaterialArt"))) {
                                    tvMaterialArt.setText("");
                                    switch (loader) {
                                        case "getLeistungArtloader":
                                            Common.stopProgressDialouge("getLeistungArt");
                                            break;
                                        case "getLeistungloader":
                                            Common.stopProgressDialouge("getLeistung");
                                            break;
                                        case "getEinheitloader":
                                            Common.stopProgressDialouge("getEinheit");
                                            break;
                                        case "getPersonloader":
                                            Common.stopProgressDialouge("getPerson");
                                            break;
                                        case "getMaterialloader":
                                            Common.stopProgressDialouge("getMaterial");
                                            break;
                                    }
                                } else {
                                    Log.v("MaterialArt", jObjTemp.getString("MaterialArt"));
                                    getMaterial();
                                }

                                break;
                            }


                        }
                    } else {
                        getMaterial();
                    }

                } else {
                    Common.showAlert(getActivity(), jPersonObj.getString("ResultMessage"));
                }
            }
        }

        if (requestedFor.equals("getEinheit")) {

            {
                JSONObject jEinheitObj = new JSONObject(response);
                if (jEinheitObj.getString("ResultCode").equals("SUCCESS")) {
                    JSONArray jArrayEinheit = jEinheitObj.getJSONArray("ResultObject");
                    if (jArrayEinheit.length() > 0) {
                        String strEinheit = jObjTemp.getString("MaterialEinh").trim();
                        Log.v("Einheit", jObjTemp.getString("MaterialEinh"));

                        for (int m = 0; m < jArrayEinheit.length(); m++) {

                            JSONObject jObjEinheit = jArrayEinheit.getJSONObject(m);

                            if (jObjEinheit.getString("ID").equalsIgnoreCase(strEinheit)) {
                                tvMaterialEinh.setText(jObjEinheit.getString("Bezeichnung").toString().trim());


                                if (Common.isStringEmpty(jObjTemp.getString("Melder"))) {
                                    tvMelder.setText("");
                                    if (Common.isStringEmpty(jObjTemp.getString("MaterialArt"))) {
                                        tvMaterialArt.setText("");
                                        switch (loader) {
                                            case "getLeistungArtloader":
                                                Common.stopProgressDialouge("getLeistungArt");
                                                break;
                                            case "getLeistungloader":
                                                Common.stopProgressDialouge("getLeistung");
                                                break;
                                            case "getEinheitloader":
                                                Common.stopProgressDialouge("getEinheit");
                                                break;
                                            case "getPersonloader":
                                                Common.stopProgressDialouge("getPerson");
                                                break;
                                            case "getMaterialloader":
                                                Common.stopProgressDialouge("getMaterial");
                                                break;
                                        }
                                    } else {
                                        Log.v("MaterialArt", jObjTemp.getString("MaterialArt"));
                                        getMaterial();
                                    }

                                } else {
                                    Log.v("Melder", jObjTemp.getString("Melder"));
                                    getPerson();
                                }

                                break;
                            }

                        }

                    } else {
                        if (Common.isStringEmpty(jObjTemp.getString("Melder"))) {
                            tvMelder.setText("");
                            if (Common.isStringEmpty(jObjTemp.getString("MaterialArt"))) {
                                tvMaterialArt.setText("");
                                switch (loader) {
                                    case "getLeistungArtloader":
                                        Common.stopProgressDialouge("getLeistungArt");
                                        break;
                                    case "getLeistungloader":
                                        Common.stopProgressDialouge("getLeistung");
                                        break;
                                    case "getEinheitloader":
                                        Common.stopProgressDialouge("getEinheit");
                                        break;
                                    case "getPersonloader":
                                        Common.stopProgressDialouge("getPerson");
                                        break;
                                    case "getMaterialloader":
                                        Common.stopProgressDialouge("getMaterial");
                                        break;
                                }
                            } else {
                                Log.v("MaterialArt", jObjTemp.getString("MaterialArt"));
                                getMaterial();
                            }

                        } else {
                            Log.v("Melder", jObjTemp.getString("Melder"));
                            getPerson();
                        }
                    }
                } else {
                    Common.showAlert(getActivity(), jEinheitObj.getString("ResultMessage"));
                }
            }
        }

        if (requestedFor.equals("getMaterial")) {
            JSONObject jMaterialObj = new JSONObject(response);
            if (jMaterialObj.getString("ResultCode").equals("SUCCESS")) {
                JSONArray jArrayMaterial = jMaterialObj.getJSONArray("ResultObject");

                if (jArrayMaterial.length() > 0) {
                    String strMaterial = jObjTemp.getString("MaterialArt").trim();
                    for (int l = 0; l < jArrayMaterial.length(); l++) {
                        JSONObject jObjMaterial = jArrayMaterial.getJSONObject(l);
                        if (jObjMaterial.getString("ID").equalsIgnoreCase(strMaterial)) {

                            switch (loader) {
                                case "getLeistungArtloader":
                                    Common.stopProgressDialouge("getLeistungArt");
                                    break;
                                case "getLeistungloader":
                                    Common.stopProgressDialouge("getLeistung");
                                    break;
                                case "getEinheitloader":
                                    Common.stopProgressDialouge("getEinheit");
                                    break;
                                case "getPersonloader":
                                    Common.stopProgressDialouge("getPerson");
                                    break;
                                case "getMaterialloader":
                                    Common.stopProgressDialouge("getMaterial");
                                    break;
                            }
                            tvMaterialArt.setText(jObjMaterial.getString("Bezeichnung").toString().trim());
                            break;
                        }
                    }
                } else {
                    switch (loader) {
                        case "getLeistungArtloader":
                            Common.stopProgressDialouge("getLeistungArt");
                            break;
                        case "getLeistungloader":
                            Common.stopProgressDialouge("getLeistung");
                            break;
                        case "getEinheitloader":
                            Common.stopProgressDialouge("getEinheit");
                            break;
                        case "getPersonloader":
                            Common.stopProgressDialouge("getPerson");
                            break;
                        case "getMaterialloader":
                            Common.stopProgressDialouge("getMaterial");
                            break;
                    }
                }
            } else {
                Common.showAlert(getActivity(), jMaterialObj.getString("ResultMessage"));
            }
        }

    }

    @Override
    public void httpFailure(String response, String requestedFor) throws Exception {

    }
}
