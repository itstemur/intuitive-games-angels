package com.visafm.roombook.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.zxing.integration.android.IntentIntegrator;
import com.visafm.roombook.Dashboard;
import com.visafm.roombook.R;
import com.visafm.roombook.common.BaseClass;
import com.visafm.roombook.common.Common;
import com.visafm.roombook.common.HttpConnection;
import com.visafm.roombook.common.SetKeyValueCombo;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class FrgSearchOrder extends Fragment implements BaseClass, DatePickerDialog.OnDateSetListener, View.OnClickListener {

    BaseClass delegate = this;
    DatePickerDialog.OnDateSetListener dateDelegate = this;
    String selectedDateString = "";
    TextInputEditText etAuftragNr;
    TextInputEditText etERPNr;
    TextInputEditText etObjekt;
    TextInputEditText etFälligkeitVon;
    TextInputEditText etFälligkeitNach;
    ImageView ivTextWithBarcode;
    public static final String ALLOW_KEY = "ALLOWED";
    public static final String CAMERA_PREF = "camera_pref";
    Integer Objectpos = -1;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 650;
    TextInputEditText etPlandatumVon;
    TextInputEditText etPlandatumNach;
    private static ArrayList<SetKeyValueCombo> AusstattungList;
    AppCompatAutoCompleteTextView atvDrop;
    TextInputEditText etTempDatePicker;
    JSONArray AssasinJArray = new JSONArray();
    Button btnSearch;
    JSONArray jArraySearchList = new JSONArray();
    /**
     * Created by Ankit Patel on 20/02/19.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_search_order, container, false);
        try {
            etAuftragNr = rootView.findViewById(R.id.etAuftragNr);
            etAuftragNr.setText("");
            etERPNr = rootView.findViewById(R.id.etERPNr);
//            etObjekt = (TextInputEditText) rootView.findViewById(R.id.etObjekt);
            atvDrop = rootView.findViewById(R.id.atvDrop);
            etFälligkeitVon = rootView.findViewById(R.id.etFälligkeitVon);
            ivTextWithBarcode = rootView.findViewById(R.id.ivTextWithBarcode);
            etFälligkeitNach = rootView.findViewById(R.id.etFälligkeitNach);
            etPlandatumVon = rootView.findViewById(R.id.etPlandatumVon);
            etPlandatumNach = rootView.findViewById(R.id.etPlandatumNach);
            btnSearch = rootView.findViewById(R.id.btnSearch);
            etFälligkeitVon.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        openDatePickerFälligkeit(etFälligkeitVon, false);
                    }
                }
            });


            etFälligkeitNach.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        openDatePickerFälligkeit(etFälligkeitNach, true);
                    }
                }
            });

            etPlandatumVon.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        openDatePickerPlandatum(etPlandatumVon, false);
                    }
                }
            });

            etPlandatumNach.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        openDatePickerPlandatum(etPlandatumNach, true);
                    }
                }
            });


            atvDrop.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    Objectpos = -1;
                    Log.e("onChangeObjectpos", Objectpos + "");
                }
            });

            atvDrop.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    atvDrop.showDropDown();
                    return false;
                }
            });

            ivTextWithBarcode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckPermission();
                }
            });

            getAusstattungID();

            btnSearch.setOnClickListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }// onCreateView


    public boolean scannedExists(String BarcodeString) {
        boolean isvalueexists = false;
        for (int j = 0; j < AusstattungList.size(); j++) {
            SetKeyValueCombo tempObj = AusstattungList.get(j);
            if (tempObj.getKey().equals(BarcodeString)) {
                atvDrop.setText(tempObj.getValue());
                Objectpos = Integer.parseInt(BarcodeString);
                Log.e("initialObjectpos", Objectpos + "");
                isvalueexists = true;
                break;
            } else {
                isvalueexists = false;
            }
        }
        return isvalueexists;

    }

    private void showAlertCamera() {
        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(getActivity()).create();
        alertDialog.setMessage("App muss auf die Kamera zugreifen.");
        alertDialog.setButton(android.app.AlertDialog.BUTTON_NEGATIVE, "NICHT ERLAUBEN", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "ZULASSEN", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
            }
        });
        alertDialog.show();
    }
    public static Boolean getFromPref(Context context, String key) {
        SharedPreferences myPrefs = context.getSharedPreferences(CAMERA_PREF, Context.MODE_PRIVATE);
        return (myPrefs.getBoolean(key, false));
    }

    public static void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

    private void showSettingsAlert() {
        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(getActivity()).create();
        alertDialog.setMessage("App muss auf die Kamera zugreifen.");
        alertDialog.setButton(android.app.AlertDialog.BUTTON_NEGATIVE, "NICHT ERLAUBEN",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE, "EINSTELLUNGEN", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                startInstalledAppDetailsActivity(getActivity());
            }
        });
        alertDialog.show();
    }

    private void CheckPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (getFromPref(getActivity(), ALLOW_KEY)) {
                showSettingsAlert();
            } else if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.CAMERA)) {
                    showAlertCamera();
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                }
            }
        } else {

            IntentIntegrator integrator = new IntentIntegrator(getActivity());
            integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
            integrator.setPrompt("Einen Barcode durchsuchen");
            integrator.setOrientationLocked(false);
            integrator.setCameraId(0);  // Use a specific camera of the device
            integrator.setBeepEnabled(false);
            integrator.initiateScan();
        }
    }


    private void fillAusstattungSpinner(Context context, JSONArray rows) throws Exception {
        AusstattungList = new ArrayList<>();
        JSONObject jTypeData;
        AssasinJArray = rows;
        for (int i = 0; i < rows.length(); i++) {
            jTypeData = rows.getJSONObject(i);
            AusstattungList.add(i, new SetKeyValueCombo(jTypeData.getString("Objkey").toString(), jTypeData.getString("Name")));
        }
        Log.d("fillTypeSpinner", "fillTypeSpinner:" + AusstattungList);
        ArrayAdapter<SetKeyValueCombo> myAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, AusstattungList);
        atvDrop.setAdapter(myAdapter);

        atvDrop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                SetKeyValueCombo selection = (SetKeyValueCombo) parent.getItemAtPosition(position);
                try {
                    Objectpos = Integer.parseInt(selection.getKey());
                    Log.e("onselectObjectpos", Objectpos + "");
                } catch (NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }

            }
        });
        Common.stopProgressDialouge("getAusstattungID");

    }

    private void openDatePickerFälligkeit(TextInputEditText etTempDatePicker1, boolean isEndDate) {
        this.etTempDatePicker = etTempDatePicker1;
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                dateDelegate,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setVersion(DatePickerDialog.Version.VERSION_1);
        if (isEndDate) { // End date selected
            if (Common.isStringEmpty(etFälligkeitVon.getText().toString().trim())) {
                Common.showAlert(getActivity(), "Bitte zuerst DatumStart auswählen.");
                etTempDatePicker.clearFocus();
                return;
            } else {
                String[] tempStr = etFälligkeitVon.getText().toString().trim().split(" ");
                String[] strStartTime = tempStr[0].split("\\.");
                int day = Integer.parseInt(strStartTime[0]);
                int month = Integer.parseInt(strStartTime[1]) - 1;
                int year = Integer.parseInt(strStartTime[2]);
                Calendar c = Calendar.getInstance();
                c.set(year, month, day);

                dpd.setMinDate(c); // If start date is present > set min Date
            }
        } else { // start date selected
            if (!Common.isStringEmpty(etFälligkeitNach.getText().toString().trim())) {
                String[] tempStr = etFälligkeitNach.getText().toString().trim().split(" ");
                String[] strStartTime = tempStr[0].split("\\.");
                int day = Integer.parseInt(strStartTime[0]);
                int month = Integer.parseInt(strStartTime[1]) - 1;
                int year = Integer.parseInt(strStartTime[2]);
                Calendar c = Calendar.getInstance();
                c.set(year, month, day);
                dpd.setMaxDate(c); // If start date is present > set max Date
            }
        }

        dpd.show(getActivity().getFragmentManager(), "DatepickerdialogStart");
        dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                etTempDatePicker.clearFocus();
                etFälligkeitNach.setText("");
                etFälligkeitVon.setText("");
            }
        });
    }

    private void openDatePickerPlandatum(final TextInputEditText etTempDatePicker1, boolean isEndDate) {
        this.etTempDatePicker = etTempDatePicker1;
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                dateDelegate,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setVersion(DatePickerDialog.Version.VERSION_1);
        if (isEndDate) { // End date selected
            if (Common.isStringEmpty(etPlandatumVon.getText().toString().trim())) {
                Common.showAlert(getActivity(), "Bitte zuerst DatumStart auswählen.");
                etTempDatePicker.clearFocus();
                return;
            } else {
                String[] tempStr = etPlandatumVon.getText().toString().trim().split(" ");
                String[] strStartTime = tempStr[0].split("\\.");
                int day = Integer.parseInt(strStartTime[0]);
                int month = Integer.parseInt(strStartTime[1]) - 1;
                int year = Integer.parseInt(strStartTime[2]);
                Calendar c = Calendar.getInstance();
                c.set(year, month, day);
                dpd.setMinDate(c); // If start date is present > set min Date
            }
        } else { // start date selected
            if (!Common.isStringEmpty(etPlandatumNach.getText().toString().trim())) {
                String[] tempStr = etPlandatumNach.getText().toString().trim().split(" ");
                String[] strStartTime = tempStr[0].split("\\.");
                int day = Integer.parseInt(strStartTime[0]);
                int month = Integer.parseInt(strStartTime[1]) - 1;
                int year = Integer.parseInt(strStartTime[2]);
                Calendar c = Calendar.getInstance();
                c.set(year, month, day);
                dpd.setMaxDate(c); // If start date is present > set max Date
            }
        }

        dpd.show(getActivity().getFragmentManager(), "DatepickerdialogStart");
        dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                etTempDatePicker.clearFocus();
                etPlandatumNach.setText("");
                etPlandatumVon.setText("");
            }
        });
    }

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
        Dashboard.tvTitle.setText("Aufträge suchen");
        if (Common.isStringEmpty(Common.BARCODE_STRING)) {
            atvDrop.setText("");
        } else {
            if (scannedExists(Common.BARCODE_STRING)) {
                Log.e("true", scannedExists(Common.BARCODE_STRING) + "");
            } else {
                atvDrop.setText("");
                Common.showAlert(getActivity(), "Es gibt kein Objekt mit der angegebenen ID");

            }
        }
        super.onResume();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearch:
                getSearchDetails();
                break;

            default:
                break;
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String year1 = String.valueOf(year);
        String month1 = String.format("%02d", monthOfYear + 1);
        String day1 = String.format("%02d", dayOfMonth);
        selectedDateString = day1 + "." + month1 + "." + year1;
        etTempDatePicker.clearFocus();
        etTempDatePicker.setText(selectedDateString);
    }

    private void getSearchDetails() {
        try {
            String StrAusstattung = "";
            HashMap<String, String> postDataParams = new HashMap<>();
            String strFälligkeit = "";
            String strPlandatum = "";
            if (!etFälligkeitVon.getText().toString().equals("") && !etFälligkeitNach.getText().toString().equals("")) {
                strFälligkeit = etFälligkeitVon.getText().toString() + " - " + etFälligkeitNach.getText().toString();
            }
            if (!etPlandatumVon.getText().toString().equals("") && !etPlandatumNach.getText().toString().equals("")) {
                strPlandatum = etPlandatumVon.getText().toString() + " - " + etPlandatumNach.getText().toString();
            }
            Common.setSharedPreferences(getActivity(), "auftragnr", etAuftragNr.getText().toString().trim());
            Common.setSharedPreferences(getActivity(), "erpnr", Common.convertDotToComma(etERPNr.getText().toString().trim()));
//            Common.setSharedPreferences(getActivity(), "objekt", etObjekt.getText().toString().trim());
            Common.setSharedPreferences(getActivity(), "fälligkeit", strFälligkeit);
            Common.setSharedPreferences(getActivity(), "plantermin", strPlandatum);
            postDataParams.put("userID", Common.USER_SESSION);
            postDataParams.put("auftragnr", etAuftragNr.getText().toString().trim());
            // postDataParams.put("sorttype","desc");
            postDataParams.put("erpnr", Common.convertDotToComma(etERPNr.getText().toString().trim()));

            if (Objectpos > -1) {
                StrAusstattung = String.valueOf(Objectpos);
                postDataParams.put("objekt", StrAusstattung);
                Common.setSharedPreferences(getActivity(), "objekt",StrAusstattung.trim());
            }else{
                postDataParams.put("objekt", "");
            }
//            postDataParams.put("objekt", etObjekt.getText().toString().trim());
            postDataParams.put("fälligkeit", strFälligkeit);
            postDataParams.put("plantermin", strPlandatum);


            HttpConnection httpConnection = new HttpConnection(delegate, getActivity());
            httpConnection.setRequestedfor("getSearchList");
            httpConnection.setIsloading(true);
            httpConnection.setPostDataParams(postDataParams);
            httpConnection.setUrl("Orders/GetAssignOrdersWithSearchAndSort");
            httpConnection.execute("");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getAusstattungID() {
        try {
            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("userID", Common.USER_SESSION);
            HttpConnection httpConnection = new HttpConnection(delegate, getActivity());
            httpConnection.setRequestedfor("getAusstattungID");
            httpConnection.setIsloading(true);
            httpConnection.setPostDataParams(postDataParams);
            httpConnection.setUrl("Orders/BindAusstattungID");
            httpConnection.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void httpResponse(String response, String requestedFor) throws Exception {
        if (requestedFor.equals("getSearchList")) {
            Common.stopProgressDialouge(requestedFor);
            JSONObject jObj = new JSONObject(response);
            if (jObj.getString("ResultCode").equals("SUCCESS")) {
                jArraySearchList = jObj.getJSONArray("ResultObject");
                Common.setSharedPreferences(getActivity(), "searchResult", jArraySearchList.toString());
                Dashboard.displayFragment(3);
            } else {
                Common.showAlert(getActivity(), jObj.getString("ResultMessage"));
            }
        }

        if (requestedFor.equals("getAusstattungID")) {
            {
                JSONObject jAusstattungObj = new JSONObject(response);
                if (jAusstattungObj.getString("ResultCode").equals("SUCCESS")) {
                    JSONArray jArrayAusstattungList = jAusstattungObj.getJSONArray("ResultObject");
                    if (jArrayAusstattungList.length() > 0) {
                        fillAusstattungSpinner(getActivity(), jArrayAusstattungList);
                    }
                } else {
                    Common.showAlert(getActivity(), jAusstattungObj.getString("ResultMessage"));
                }
            }
        }
    }

    @Override
    public void httpFailure(String response, String requestedFor) throws Exception {

    }


}