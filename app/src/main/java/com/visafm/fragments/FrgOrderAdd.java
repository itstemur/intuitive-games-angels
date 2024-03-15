package com.visafm.fragments;

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
import com.google.android.material.textfield.TextInputEditText;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.visafm.Dashboard;
import com.visafm.R;
import com.visafm.common.BaseClass;
import com.visafm.common.Common;
import com.visafm.common.HttpConnection;
import com.visafm.common.SetKeyValueCombo;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Ankit Patel on 20/02/19.
 */
public class FrgOrderAdd extends Fragment implements BaseClass, DatePickerDialog.OnDateSetListener, View.OnClickListener {

    BaseClass delegate = this;
    DatePickerDialog.OnDateSetListener dateDelegate = this;
    String selectedDateString = "";;
    TextInputEditText etKST;
    TextInputEditText etKurzbezeichnung;
    TextInputEditText etTempDatePicker;
    TextInputEditText etBezeichnung;
    TextInputEditText etPlantermin;
    TextInputEditText etfaelligkeit;
    Spinner spArtdesAuftrags;
    ImageView ivTextWithBarcode;
    TextInputEditText etKosten;
    Integer Objectpos = -1;
    Integer Beschreibungpos;
    AppCompatAutoCompleteTextView atvDrop;
    AppCompatAutoCompleteTextView atvBeschreibung;
    boolean isUpdateetfaelligkeit = true;
    boolean isetfaelligkeit = true;
    private static ArrayList<SetKeyValueCombo> AusstattungList;
    private static ArrayList<SetKeyValueCombo> ArtdesAuftragsList;
    private static ArrayList<SetKeyValueCombo> BeschreibungList;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    public static final String ALLOW_KEY = "ALLOWED";
    public static final String CAMERA_PREF = "camera_pref";
    Button btnUpdate;
    Boolean scannedExists = true;
    JSONArray BeschreibungJArray;
    JSONArray AssasinJArray = new JSONArray();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_order_add, container, false);

        JSONObject Beschreibungrec1 = new JSONObject();
        try {
            Beschreibungrec1.put("Id", "1");
            Beschreibungrec1.put("Name", "Auftrag gemäß Beschreibung ausgeführt");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        BeschreibungJArray = new JSONArray();
        BeschreibungJArray.put(Beschreibungrec1);

        try {
            Common.BARCODE_STRING = "";
            etKST = rootView.findViewById(R.id.etKST);
            etPlantermin = rootView.findViewById(R.id.etPlantermin);
            etfaelligkeit = rootView.findViewById(R.id.etfaelligkeit);
            etBezeichnung = rootView.findViewById(R.id.etBezeichnung);
            etKurzbezeichnung = rootView.findViewById(R.id.etKurzbezeichnung);
            ivTextWithBarcode = rootView.findViewById(R.id.ivTextWithBarcode);
            etKosten = rootView.findViewById(R.id.etKosten);
            atvBeschreibung = rootView.findViewById(R.id.atvBeschreibung);
            atvDrop = rootView.findViewById(R.id.atvDrop);

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

            spArtdesAuftrags = rootView.findViewById(R.id.spArtdesAuftrags);
            btnUpdate = rootView.findViewById(R.id.btnUpdate);
            btnUpdate.setText("Hinzufügen");

            etKosten.setText("0");

            if (isetfaelligkeit) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("dd.MMM.yyyy");
                c.add(Calendar.DATE, 14);
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd.MM.yyyy");
                String output = sdf1.format(c.getTime());
                etfaelligkeit.setText(output);
                isetfaelligkeit = false;
            }

            etPlantermin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        openDatePickerDefault(etPlantermin);
                    }
                }
            });

            etfaelligkeit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        openDatePickerDefault2(etfaelligkeit);
                    }
                }
            });

            atvDrop.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    atvDrop.showDropDown();
                    return false;
                }
            });

            atvBeschreibung.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    atvBeschreibung.showDropDown();
                    return false;
                }
            });


            ivTextWithBarcode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckPermission();
                }
            });

            etfaelligkeit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (isUpdateetfaelligkeit)
                        etfaelligkeit.setError(null);
                    else
                        isUpdateetfaelligkeit = true;
                }
            });


            getAusstattungID();
            btnUpdate.setOnClickListener(this);
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
        Dashboard.ivDocumnetEdit.setVisibility(View.GONE);
        Dashboard.ivService.setVisibility(View.GONE);
        Dashboard.ivServiceEdit.setVisibility(View.GONE);
        Dashboard.ivDrawerIndicator.setVisibility(View.GONE);
        Dashboard.ivBack.setVisibility(View.VISIBLE);
        Dashboard.tvTitle.setText("Auftrag hinzufügen");

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

    private void openDatePickerDefault(final TextInputEditText etTempDatePicker2) {
        this.etTempDatePicker = etTempDatePicker2;
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(dateDelegate, now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setVersion(DatePickerDialog.Version.VERSION_1);
        dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                etTempDatePicker.clearFocus();
                etBezeichnung.requestFocus();
            }
        });
        dpd.show(getActivity().getFragmentManager(), "etPlantermin");
    }

    private void openDatePickerDefault2(final TextInputEditText etTempDatePicker3) {
        this.etTempDatePicker = etTempDatePicker3;

        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(dateDelegate, now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.setMinDate(now);
        dpd.setVersion(DatePickerDialog.Version.VERSION_1);
        dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                etTempDatePicker.clearFocus();
                etBezeichnung.requestFocus();

            }
        });
        dpd.show(getActivity().getFragmentManager(), "etfaelligkeit");
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

    public static Boolean getFromPref(Context context, String key) {
        SharedPreferences myPrefs = context.getSharedPreferences(CAMERA_PREF, Context.MODE_PRIVATE);
        return (myPrefs.getBoolean(key, false));
    }

    private void checkValidation() {

        if (Common.isValidEdittext(atvDrop, 0)) {
            if (Objectpos > -1) {
                int spArtdesAuftragspos = spArtdesAuftrags.getSelectedItemPosition();
                if (spArtdesAuftragspos != 0) {
                    if (Common.isValidEdittext(etfaelligkeit, 0)
                            && Common.isValidEdittext(etKosten, 0)) {
                        InsertOrder();
                    } else {

                    }

                } else {
                    Common.showAlert(getActivity(), "ArtdesAuftrags* ");
                }

            } else {
                showObjectAlert(getActivity(), "Wählen Sie einen Wert aus der Auswahlliste \"Objekt*\" aus.");
            }

        } else {
            showObjectAlert(getActivity(), "Objekt* ");
        }


    }

    public void showObjectAlert(Context context, String Msg) {
        final Dialog dialog = new Dialog(context);
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        @SuppressWarnings("static-access")
        LayoutInflater Inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View v = Inflater.inflate(R.layout.dialog_alert, null);
        TextView tvMessage = v.findViewById(R.id.tvAlertMessage);
        tvMessage.setText(Msg);

        Button btnOk = v.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                atvDrop.setText("");
                atvDrop.requestFocus();
            }
        });

        dialog.setContentView(v);
        dialog.show();
    }

    private void InsertOrder() {
        try {

            String StrAusstattung = "", StrArtdes = "";
            HashMap<String, String> postDataParams = new HashMap<>();
            if (Objectpos > -1) {
                StrAusstattung = String.valueOf(Objectpos);
                postDataParams.put("ausstattungid", StrAusstattung);
            }
            StrArtdes = ArtdesAuftragsList.get(spArtdesAuftrags.getSelectedItemPosition()).getKey();
            postDataParams.put("Art", StrArtdes);
            postDataParams.put("userID", Common.USER_SESSION);
            if (!etPlantermin.getText().toString().equals("")) {
                postDataParams.put("datumVorgesehen", etPlantermin.getText().toString().trim());
            } else {
                postDataParams.put("datumVorgesehen", "");
            }
            if (!etfaelligkeit.getText().toString().equals("")) {
                postDataParams.put("datumfaelligkeit", etfaelligkeit.getText().toString().trim());
            }

            if (!Common.isStringEmpty(etBezeichnung.getText().toString().trim())) {
                postDataParams.put("beschreibung", etBezeichnung.getText().toString().trim());
            } else {
                postDataParams.put("beschreibung", " ");
            }
            if (!Common.isStringEmpty(etKurzbezeichnung.getText().toString().trim())) {
                postDataParams.put("beschreibungvA", etKurzbezeichnung.getText().toString().trim());
            } else {
                postDataParams.put("beschreibungvA", " ");
            }

            postDataParams.put("kosten", Common.convertDotToComma(etKosten.getText().toString().trim()));
            postDataParams.put("leistung", atvBeschreibung.getText().toString().trim());
            HttpConnection httpConnection = new HttpConnection(delegate, getActivity());
            httpConnection.setRequestedfor("InsertOrder");
            httpConnection.setIsloading(true);
            httpConnection.setPostDataParams(postDataParams);
            httpConnection.setUrl("Orders/InsertOrder");
            httpConnection.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void showAlert(String Msg) {
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
                getActivity().onBackPressed();
                dialog.dismiss();
            }
        });

        dialog.setContentView(dv);
        dialog.show();
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

    private void getMaintenance() {
        try {
            HashMap<String, String> postDataParams = new HashMap<>();
            HttpConnection httpConnection = new HttpConnection(delegate, getActivity());
            httpConnection.setRequestedfor("getMaintenance");
            httpConnection.setIsloading(false);
            httpConnection.setPostDataParams(postDataParams);
            httpConnection.setUrl("Orders/BindKeysMaintenance");
            httpConnection.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void httpResponse(String response, String requestedFor) throws Exception {
        Common.stopProgressDialouge(requestedFor);
        if (requestedFor.equals("InsertOrder")) {
            Common.stopProgressDialouge(requestedFor);
            JSONObject jObj = new JSONObject(response);
            if (jObj.getString("ResultCode").equals("SUCCESS")) {
                showAlert(jObj.getString("ResultMessage"));
            } else {
                showAlert(jObj.getString("ResultMessage"));
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

        if (requestedFor.equals("getMaintenance")) {
            {
                JSONObject jMaintenanceObj = new JSONObject(response);
                if (jMaintenanceObj.getString("ResultCode").equals("SUCCESS")) {
                    JSONArray jArrayArtdesAuftragsList = jMaintenanceObj.getJSONArray("ResultObject");
                    if (jArrayArtdesAuftragsList.length() > 0) {
                        fillArtdesAuftragsSpinner(getActivity(), jArrayArtdesAuftragsList, getResources().getString(R.string.ArtdesAuftrags));

                    }
                } else {
                    Common.showAlert(getActivity(), jMaintenanceObj.getString("ResultMessage"));
                }
            }
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
        getMaintenance();

    }

    private void fillBeschreibungSpinner(Context context, JSONArray rows) throws Exception {
        BeschreibungList = new ArrayList<>();
        JSONObject jTypeData;
        for (int i = 0; i < rows.length(); i++) {
            jTypeData = rows.getJSONObject(i);
            BeschreibungList.add(i, new SetKeyValueCombo(jTypeData.getString("Id").toString(), jTypeData.getString("Name")));
        }
        Log.d("fillTypeSpinner", "fillTypeSpinner:" + BeschreibungList);
        ArrayAdapter<SetKeyValueCombo> BeschreibungAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, BeschreibungList);
        atvBeschreibung.setAdapter(BeschreibungAdapter);
        // atvBeschreibung.setText("Auftrag gemäß Beschreibung ausgeführt");
        atvBeschreibung.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                Beschreibungpos = position;
            }
        });
    }

    private void fillArtdesAuftragsSpinner(Context context, JSONArray rows, String mFirstRow) throws Exception {
        ArtdesAuftragsList = new ArrayList<>();
        int index = 0;
        JSONObject jArtdesAuftragsData;
        if (mFirstRow != null)
            ArtdesAuftragsList.add(index, new SetKeyValueCombo("", mFirstRow));
        else
            index = -1;

        for (int i = 0; i < rows.length(); i++) {
            index++;
            jArtdesAuftragsData = rows.getJSONObject(i);
            ArtdesAuftragsList.add(index, new SetKeyValueCombo(jArtdesAuftragsData.getString("ArtID").toString(), jArtdesAuftragsData.getString("Beschreibung")));
        }
        Log.d("fillTypeSpinner", "fillTypeSpinner:" + ArtdesAuftragsList);
        ArrayAdapter<SetKeyValueCombo> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, ArtdesAuftragsList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spArtdesAuftrags.setAdapter(adapter);
        fillBeschreibungSpinner(getActivity(), BeschreibungJArray);
        Common.stopProgressDialouge("getAusstattungID");
    }

    @Override
    public void httpFailure(String response, String requestedFor) throws Exception {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpdate:
                checkValidation();
                break;

            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult
            (int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                for (int i = 0, len = permissions.length; i < len; i++) {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        boolean showRationale = ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission);
                        if (showRationale) {
                            showAlertCamera();
                        } else if (!showRationale) {
                            saveToPreferences(getActivity(), ALLOW_KEY, true);
                        }
                    }
                }
            }
        }
    }

    public static void saveToPreferences(Context context, String key, Boolean allowed) {
        SharedPreferences myPrefs = context.getSharedPreferences(CAMERA_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putBoolean(key, allowed);
        prefsEditor.commit();
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String year1 = String.valueOf(year);
        String month1 = String.format("%02d", monthOfYear + 1);
        String day1 = String.format("%02d", dayOfMonth);
        selectedDateString = day1 + "." + month1 + "." + year1;
        etTempDatePicker.setText(selectedDateString);
        etTempDatePicker.clearFocus();
        etBezeichnung.requestFocus();

    }
}
