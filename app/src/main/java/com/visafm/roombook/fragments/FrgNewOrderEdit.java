package com.visafm.roombook.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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

/**
 * Created by Ankit Patel on 20/02/19.
 */
public class FrgNewOrderEdit extends Fragment implements BaseClass, DatePickerDialog.OnDateSetListener, View.OnClickListener {

    BaseClass delegate = this;
    DatePickerDialog.OnDateSetListener dateDelegate = this;
    String selectedDateString = "";
//    TextInputLayout fletAusführungsfensterStart;
//    TextInputLayout fletAusführungsfensterEnde;
//    TextInputEditText fletPlantermin;
//    TextInputEditText fletFälligkeit;
    public static final String ALLOW_KEY = "ALLOWED";
    public static final String CAMERA_PREF = "camera_pref";
    TextInputEditText etAuftragNr;
    Spinner spArt;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    TextInputEditText etInventarNr;
    TextInputEditText etAusführungsfensterStart;
    TextInputEditText etAusführungsfensterEnde;
    TextInputEditText etPlantermin;
    ImageView ivTextWithBarcode;
    TextInputEditText etFälligkeit;
    TextInputEditText etBezeichnung;
    TextInputEditText etKurzbeschreibung;
    TextInputEditText etBeschreibung;
    Spinner spAusführung;
    Spinner spPriorität;
    Spinner spLeistung;
    Spinner spFehlerbild;
    TextInputEditText etTempDatePicker;
    TextInputEditText etTempDatePicker3;
    Button btnSave;
    boolean secondpicker = false;
    boolean firstpicker = false;
    private static ArrayList<SetKeyValueCombo> spArtList;
    private static ArrayList<SetKeyValueCombo> spAusführungList;
    private static ArrayList<SetKeyValueCombo> spLeistungList;
    private static ArrayList<SetKeyValueCombo> spFehlerbildList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_new_order_edit, container, false);
        setHasOptionsMenu(false);
        try {

            etAuftragNr = rootView.findViewById(R.id.etAuftragNr);
            spArt = rootView.findViewById(R.id.spArt);
            etInventarNr = rootView.findViewById(R.id.etInventarNr);
            etAusführungsfensterStart = rootView.findViewById(R.id.etAusführungsfensterStart);
            etAusführungsfensterEnde = rootView.findViewById(R.id.etAusführungsfensterEnde);
            etPlantermin = rootView.findViewById(R.id.etPlantermin);
            ivTextWithBarcode = rootView.findViewById(R.id.ivTextWithBarcode);
            etFälligkeit = rootView.findViewById(R.id.etFälligkeit);
            etBezeichnung = rootView.findViewById(R.id.etBezeichnung);
            etKurzbeschreibung = rootView.findViewById(R.id.etKurzbeschreibung);
            etBeschreibung = rootView.findViewById(R.id.etBeschreibung);
            spAusführung = rootView.findViewById(R.id.spAusführung);
            spPriorität = rootView.findViewById(R.id.spPriorität);
            spLeistung = rootView.findViewById(R.id.spLeistung);
            spFehlerbild = rootView.findViewById(R.id.spFehlerbild);
            btnSave = rootView.findViewById(R.id.btnSave);
//            fletAusführungsfensterStart = (TextInputLayout) rootView.findViewById(R.id.fletAusführungsfensterStart);
//            fletAusführungsfensterEnde = (TextInputLayout) rootView.findViewById(R.id.fletAusführungsfensterEnde);
//            fletPlantermin = (TextInputEditText) rootView.findViewById(R.id.fletPlantermin);
//            fletFälligkeit = (TextInputEditText) rootView.findViewById(R.id.fletFälligkeit);

            btnSave.setOnClickListener(this);
            etAusführungsfensterStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        openDatePicker(etAusführungsfensterStart, false);
                    }
                }
            });

            etAusführungsfensterEnde.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        openDatePicker(etAusführungsfensterEnde, true);
                    }
                }
            });
            etPlantermin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        openDatePicker2(etPlantermin, false);
                    }
                }
            });
            etFälligkeit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        openDatePicker2(etFälligkeit, true);
                    }
                }
            });

//            ivTextWithBarcode.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    CheckPermission();
//                }
//            });
            BindCommonDropDown();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }// onCreateView

    @Override
    public void onResume() {
        Dashboard.ivSortUp.setVisibility(View.GONE);
        Dashboard.ivSortDown.setVisibility(View.GONE);
        Dashboard.ivSortArrows.setVisibility(View.GONE);
        Dashboard.ivEdit.setVisibility(View.GONE);
        Dashboard.ivDocument.setVisibility(View.GONE);
        Dashboard.ivService.setVisibility(View.GONE);
        Dashboard.ivServiceEdit.setVisibility(View.GONE);
        Dashboard.ivDrawerIndicator.setVisibility(View.GONE);
        Dashboard.ivBack.setVisibility(View.VISIBLE);
        Dashboard.ivDocumnetEdit.setVisibility(View.GONE);
        Dashboard.tvTitle.setText("Aufträge bearbeiten");
        super.onResume();
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

    private void openDatePicker(final TextInputEditText etTempDatePicker1, boolean isEndDate) {
        firstpicker = true;
        secondpicker = false;
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
            if (Common.isStringEmpty(etAusführungsfensterStart.getText().toString().trim())) {
                Common.showAlert(getActivity(), "Bitte zuerst DatumStart auswählen.");
//                etTempDatePicker.clearFocus();
                etBezeichnung.requestFocus();
                return;
            } else {
                String[] tempStr = etAusführungsfensterStart.getText().toString().trim().split(" ");
                String[] strStartTime = tempStr[0].split("\\.");
                int day = Integer.parseInt(strStartTime[0]);
                int month = Integer.parseInt(strStartTime[1]) - 1;
                int year = Integer.parseInt(strStartTime[2]);
                Calendar c = Calendar.getInstance();
                c.set(year, month, day);
                dpd.setMinDate(c); // If start date is present > set min Date
            }
        } else { // start date selected
            if (!Common.isStringEmpty(etAusführungsfensterEnde.getText().toString().trim())) {
                String[] tempStr = etAusführungsfensterEnde.getText().toString().trim().split(" ");
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
//                etTempDatePicker.clearFocus();
                etBezeichnung.requestFocus();
                etAusführungsfensterStart.setText("");
                etAusführungsfensterEnde.setText("");
            }
        });
    }

    private void openDatePicker2(final TextInputEditText etTempDatePicker2, boolean isEndDate2) {
        secondpicker = true;
        firstpicker = false;
        this.etTempDatePicker3 = etTempDatePicker2;
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpdd = DatePickerDialog.newInstance(
                dateDelegate,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpdd.setVersion(DatePickerDialog.Version.VERSION_1);
        if (isEndDate2) { // End date selected
            if (Common.isStringEmpty(etPlantermin.getText().toString().trim())) {
                Common.showAlert(getActivity(), "Bitte zuerst DatumStart auswählen.");
//                etTempDatePicker3.clearFocus();
                etBezeichnung.requestFocus();
                return;
            } else {
                String[] tempStr = etPlantermin.getText().toString().trim().split(" ");
                String[] strStartTime = tempStr[0].split("\\.");
                int day = Integer.parseInt(strStartTime[0]);
                int month = Integer.parseInt(strStartTime[1]) - 1;
                int year = Integer.parseInt(strStartTime[2]);
                Calendar c = Calendar.getInstance();
                c.set(year, month, day);
                dpdd.setMinDate(c); // If start date is present > set min Date
            }
        } else { // start date selected
            if (!Common.isStringEmpty(etFälligkeit.getText().toString().trim())) {
                String[] tempStr = etFälligkeit.getText().toString().trim().split(" ");
                String[] strStartTime = tempStr[0].split("\\.");
                int day = Integer.parseInt(strStartTime[0]);
                int month = Integer.parseInt(strStartTime[1]) - 1;
                int year = Integer.parseInt(strStartTime[2]);
                Calendar c = Calendar.getInstance();
                c.set(year, month, day);
                dpdd.setMaxDate(c); // If start date is present > set max Date
            }
        }

        dpdd.show(getActivity().getFragmentManager(), "DatepickerdialogStart");
        dpdd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
//                etTempDatePicker2.clearFocus();
                etBezeichnung.requestFocus();
                etPlantermin.setText("");
                etFälligkeit.setText("");
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String year1 = String.valueOf(year);
        String month1 = String.format("%02d", monthOfYear + 1);
        String day1 = String.format("%02d", dayOfMonth);
        selectedDateString = day1 + "." + month1 + "." + year1;
        if (firstpicker) {
//            etTempDatePicker.clearFocus();
            etBezeichnung.requestFocus();
            etTempDatePicker.setText(selectedDateString);
        }
        if (secondpicker) {
//            etTempDatePicker3.clearFocus();
            etBezeichnung.requestFocus();
            etTempDatePicker3.setText(selectedDateString);
        }
    }

    private void getDetails() {
        try {

            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("orderid", Common.getSharedPreferences(getActivity(), "neworderid"));
            HttpConnection httpConnection = new HttpConnection(delegate, getActivity());
            httpConnection.setRequestedfor("getDetails");
            httpConnection.setIsloading(false);
            httpConnection.setPostDataParams(postDataParams);
            httpConnection.setUrl("Orders/GetAssignOrdersDetail");
            httpConnection.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateOrder() {
        try {
            String StrFehlerId = "", StrArtTypeId = "", StrmelderId = "", StrPrioritätId = "", StrLeistungId = "";
            StrFehlerId = spFehlerbildList.get(spFehlerbild.getSelectedItemPosition()).getKey();
            StrArtTypeId = spArtList.get(spArt.getSelectedItemPosition()).getKey();
            StrmelderId = spAusführungList.get(spAusführung.getSelectedItemPosition()).getKey();
            StrLeistungId = spLeistungList.get(spLeistung.getSelectedItemPosition()).getKey();
            StrPrioritätId = spPriorität.getSelectedItem().toString();
            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("wartungid", etAuftragNr.getText().toString());
            postDataParams.put("Art", StrArtTypeId);
            postDataParams.put("userID", Common.USER_SESSION);
            postDataParams.put("AusstattungID", etInventarNr.getText().toString());
            postDataParams.put("FensterStart", etAusführungsfensterStart.getText().toString());
            postDataParams.put("FensterEnde", etAusführungsfensterEnde.getText().toString());
            postDataParams.put("ApplicationID", Common.APPLICATIONID);
            postDataParams.put("DatumVorgesehen", etPlantermin.getText().toString());
            postDataParams.put("Datumfaelligkeit", etFälligkeit.getText().toString());
            postDataParams.put("Beschreibung", etBezeichnung.getText().toString());
            postDataParams.put("BeschreibungvA", etKurzbeschreibung.getText().toString());
            postDataParams.put("Leistung", etBeschreibung.getText().toString());
            postDataParams.put("Melder", StrmelderId);
            if (spPriorität.getSelectedItemPosition() == 0) {
                postDataParams.put("Feld1", "");
            } else {
                postDataParams.put("Feld1", StrPrioritätId);
            }
            postDataParams.put("PruefVerf", StrLeistungId);
            postDataParams.put("Fehler", StrFehlerId);
            HttpConnection httpConnection = new HttpConnection(delegate, getActivity());
            httpConnection.setRequestedfor("UpdateOrder");
            httpConnection.setIsloading(true);
            httpConnection.setPostDataParams(postDataParams);
            httpConnection.setUrl("Orders/UpdateNewOrder");
            httpConnection.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void BindCommonDropDown() {
        try {

            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("userid", Common.USER_SESSION);
            HttpConnection httpConnection = new HttpConnection(delegate, getActivity());
            httpConnection.setRequestedfor("BindCommonDropDown");
            httpConnection.setIsloading(true);
            httpConnection.setPostDataParams(postDataParams);
            httpConnection.setUrl("Orders/BindCommonDropDown");
            httpConnection.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillDocContentTypeSpinner(Context context, JSONArray rows, String mFirstRow) throws Exception {
        spArtList = new ArrayList<>();
        int index = 0;
        JSONObject jData;

        if (mFirstRow != null)
            spArtList.add(index, new SetKeyValueCombo("0", mFirstRow));
        else
            index = -1;

        for (int i = 0; i < rows.length(); i++) {
            index++;
            jData = rows.getJSONObject(i);
            if (!jData.getString("value").equals("") && !jData.getString("text").equals(""))
                spArtList.add(index, new SetKeyValueCombo(jData.getString("value"), jData.getString("text")));
        }
        Log.d("fillDocContTypeSpinner", "fillDocContTypeSpinner:" + spArtList);
        ArrayAdapter<SetKeyValueCombo> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spArtList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spArt.setAdapter(adapter);
    }

    private void fillAusführungSpinner(Context context, JSONArray rows, String mFirstRow) throws Exception {
        spAusführungList = new ArrayList<>();
        int index = 0;
        JSONObject jData;

        if (mFirstRow != null)
            spAusführungList.add(index, new SetKeyValueCombo("0", mFirstRow));
        else
            index = -1;

        for (int i = 0; i < rows.length(); i++) {
            index++;
            jData = rows.getJSONObject(i);
            if (!jData.getString("value").equals("") && !jData.getString("text").equals(""))
                spAusführungList.add(index, new SetKeyValueCombo(jData.getString("value"), jData.getString("text")));
        }
        Log.d("fillAusführungSpinner", "fillAusführungSpinner:" + spAusführungList);
        ArrayAdapter<SetKeyValueCombo> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spAusführungList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spAusführung.setAdapter(adapter);


    }

    private void fillLeistungSpinner(Context context, JSONArray rows, String mFirstRow) throws Exception {
        spLeistungList = new ArrayList<>();
        int index = 0;
        JSONObject jData;

        if (mFirstRow != null)
            spLeistungList.add(index, new SetKeyValueCombo("11", mFirstRow));
        else
            index = -1;

        for (int i = 0; i < rows.length(); i++) {
            index++;
            jData = rows.getJSONObject(i);
            if (!jData.getString("value").equals("") && !jData.getString("text").equals(""))
                spLeistungList.add(index, new SetKeyValueCombo(jData.getString("value"), jData.getString("text")));
        }
        Log.d("fillLeistungSpinner", "fillLeistungSpinner:" + spLeistungList);
        ArrayAdapter<SetKeyValueCombo> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spLeistungList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLeistung.setAdapter(adapter);
    }

    private void fillFehlerbildlistSpinner(Context context, JSONArray rows, String mFirstRow) throws Exception {
        spFehlerbildList = new ArrayList<>();
        int index = 0;
        JSONObject jData;

        if (mFirstRow != null)
            spFehlerbildList.add(index, new SetKeyValueCombo("0", mFirstRow));
        else
            index = -1;

        for (int i = 0; i < rows.length(); i++) {
            index++;
            jData = rows.getJSONObject(i);
            if (!jData.getString("value").equals("") && !jData.getString("text").equals(""))
                spFehlerbildList.add(index, new SetKeyValueCombo(jData.getString("value"), jData.getString("text")));
        }
        Log.d("fillFehlerbildSpinner", "fillFehlerbildlistSpinner" + spFehlerbildList);
        ArrayAdapter<SetKeyValueCombo> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spFehlerbildList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFehlerbild.setAdapter(adapter);
    }

    @Override
    public void httpResponse(String response, String requestedFor) throws Exception {
        Common.stopProgressDialouge();
        if (requestedFor.equals("UpdateOrder")) {
            Common.stopProgressDialouge();
            JSONObject jObj = new JSONObject(response);
            if (jObj.getString("ResultCode").equals("SUCCESS")) {
                DisplayAlert(jObj.getString("ResultMessage"));

            } else {
                Common.showAlert(getActivity(), jObj.getString("ResultMessage"));
            }
        }

        if (requestedFor.equals("BindCommonDropDown")) {
            {
                Common.stopProgressDialouge();
                JSONObject jObj = new JSONObject(response);
                if (jObj.getString("ResultCode").equals("SUCCESS")) {
                    JSONObject tempjobj = jObj.getJSONObject("ResultObject");
                    JSONArray jArrayDocTypeList = tempjobj.getJSONArray("artlist");
                    JSONArray jArrayfehlerList = tempjobj.getJSONArray("fehlerlist");
                    JSONArray jArraymelderList = tempjobj.getJSONArray("melderlist");
                    JSONArray jArrayPruefverfList = tempjobj.getJSONArray("pruefverflist");
                    fillDocContentTypeSpinner(getActivity(), jArrayDocTypeList, getResources().getString(R.string.typeArtNewOrder));
                    fillAusführungSpinner(getActivity(), jArraymelderList, getResources().getString(R.string.Ausführung));
                    fillLeistungSpinner(getActivity(), jArrayPruefverfList, getResources().getString(R.string.Leistung));
                    fillFehlerbildlistSpinner(getActivity(), jArrayfehlerList, getResources().getString(R.string.Fehler));
                    getDetails();
                } else {
                    Common.showAlert(getActivity(), jObj.getString("ResultMessage"));
                }
            }
        }

        if (requestedFor.equals("getDetails")) {
//            Common.stopProgressDialouge();
            JSONObject jObj = new JSONObject(response);

            JSONObject resultObject = jObj.getJSONObject("ResultObject");
            if (jObj.getString("ResultCode").equals("SUCCESS")) {
                etAuftragNr.setText(resultObject.getString("WartungID"));
                etInventarNr.setText(resultObject.getString("AusstattungID"));
                etAusführungsfensterStart.setText(resultObject.getString("FensterStart"));
                etAusführungsfensterEnde.setText(resultObject.getString("FensterEnde"));
                etPlantermin.setText(resultObject.getString("DatumVorgesehen"));
                etFälligkeit.setText(resultObject.getString("Datumfaelligkeit"));
                etBezeichnung.setText(resultObject.getString("Beschreibung"));
                etKurzbeschreibung.setText(resultObject.getString("BeschreibungvA"));
                etBeschreibung.setText(resultObject.getString("Leistung"));

                String strPriorität = resultObject.getString("Feld1");
                if (strPriorität.equals("hoch")) {
                    spPriorität.setSelection(1);
                }
                if (strPriorität.equals("mittel")) {
                    spPriorität.setSelection(2);
                }
                if (strPriorität.equals("niedrig")) {
                    spPriorität.setSelection(3);
                }

                String strSelectedArt = resultObject.getString("Art");
                for (int i = 0; i < spArtList.size(); i++) {
                    SetKeyValueCombo tempObj = spArtList.get(i);
                    if (tempObj.getKey().equals(strSelectedArt)) {
                        spArt.setSelection(i);
                        break;
                    }
                }
                String strSelectedAusführung = resultObject.getString("Melder");
                for (int j = 0; j < spAusführungList.size(); j++) {
                    SetKeyValueCombo tempObj = spAusführungList.get(j);
                    if (tempObj.getKey().equals(strSelectedAusführung)) {
                        spAusführung.setSelection(j);
                        break;
                    }
                }
                String strSelectedLeistungId = resultObject.getString("PruefVerf");
                for (int l = 0; l < spLeistungList.size(); l++) {
                    SetKeyValueCombo tempObj = spLeistungList.get(l);
                    if (tempObj.getKey().equals(strSelectedLeistungId)) {
                        spLeistung.setSelection(l);
                        break;
                    }
                }
                String strSelectedFehlerbild = resultObject.getString("Fehler");
                for (int k = 0; k < spFehlerbildList.size(); k++) {
                    SetKeyValueCombo tempObj = spFehlerbildList.get(k);
                    if (tempObj.getKey().equals(strSelectedFehlerbild)) {
                        spFehlerbild.setSelection(k);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void httpFailure(String response, String requestedFor) throws Exception {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSave:
                checkValidation();
                break;
            default:
                break;
        }
    }

    private void DisplayAlert(String Msg) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        @SuppressWarnings("static-access")
        LayoutInflater I = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View dv = I.inflate(R.layout.dialog_alert, null);
        TextView tvMessage = dv.findViewById(R.id.tvAlertMessage);
        tvMessage.setText(Msg);
        Button btnOk = dv.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                getActivity().onBackPressed();
                dialog.dismiss();
                Dashboard.displayFragment(0);
            }
        });

        dialog.setContentView(dv);
        dialog.show();
    }

    private void checkValidation() {
        int pos1 = spArt.getSelectedItemPosition();
        int pos2 = spAusführung.getSelectedItemPosition();

        int pos3 = spFehlerbild.getSelectedItemPosition();
        int pos4 = spPriorität.getSelectedItemPosition();



        if ((etAusführungsfensterStart.getText().toString().equals(""))) {
            Common.showAlert(getActivity(), "Ausführung von ist obligatorisch");
            return;
        }
        if ((etAusführungsfensterEnde.getText().toString().equals(""))) {
            Common.showAlert(getActivity(), "Ausführung bis ist obligatorisch ");
            return;
        }
        if ((etPlantermin.getText().toString().equals(""))) {
            Common.showAlert(getActivity(), "Plantermin ist obligatorisch ");
            return;
        }
        if ((etFälligkeit.getText().toString().equals(""))) {
            Common.showAlert(getActivity(), "Fälligkeit ist obligatorisch ");
            return;
        }
        if (pos1 == 0) {
            Common.showAlert(getActivity(), "Art des Auftrags ist obligatorisch");
            return;
        }
        if (pos2 == 0) {
            Common.showAlert(getActivity(), "Ausführung ist obligatorisch");
            return;
        }
        if (pos4 == 0) {
            Common.showAlert(getActivity(), "Priorität ist obligatorisch");
            return;
        }
        if (pos3 == 0) {
            Common.showAlert(getActivity(), "Fehler ist obligatorisch");
            return;
        }


        boolean isvalidtext = true;
        if (!Common.isValidEdittext(etInventarNr, 0)) {
            isvalidtext = false;
        }
        if (!Common.isValidEdittext(etKurzbeschreibung, 0)) {
            isvalidtext = false;
        }
        if (!Common.isValidEdittext(etBezeichnung, 0)) {
            isvalidtext = false;
        }
        if (!Common.isValidEdittext(etBezeichnung, 0)) {
            isvalidtext = false;
        }

        if (isvalidtext) {
            updateOrder();
        } else {
            Common.showAlert(getActivity(), "Bitte füllen Sie die erforderlichen Felder aus");
        }
    }
}
