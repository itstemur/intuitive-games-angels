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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

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
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


/**
 * Created by Ankit Patel on 20/02/19.
 */

public class FrgOrderEdit extends Fragment implements BaseClass, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, View.OnClickListener {

    BaseClass delegate = this;
    DatePickerDialog.OnDateSetListener dateDelegate = this;
    TimePickerDialog.OnTimeSetListener timeDelegate = this;
    String selectedDateString = "";;
    AppCompatAutoCompleteTextView atvDrop;
    Spinner spArtdesAuftrags;
    String stArtdesAuftrags;
    String strAusstattung;
    TextInputEditText etKurzbezeichnung;
    ImageView ivTextWithBarcode;
    TextInputEditText etBezeichnung;
    TextInputEditText etPlantermin;
    TextInputEditText etfaelligkeit;
    Integer Objectposition = -1;
    Integer Beschreibungpos = -1;
    //    TextInputEditText etKST;
    TextInputEditText etZeitaufwand;
    TextInputEditText etDatumStart;
    TextInputEditText etDatumEnde;
    TextInputEditText etKosten;
    private static ArrayList<SetKeyValueCombo> AusstattungList;
    private static ArrayList<SetKeyValueCombo> BeschreibungList;
    private static ArrayList<SetKeyValueCombo> ArtdesAuftragsList;
    AppCompatAutoCompleteTextView atvBeschreibung;
    TextInputEditText etTempDatePicker;
    TextInputEditText etTempDatePickerPlantermin;
    TextInputEditText etTempDatePickerfaelligkeit;

    Switch sFertigmeldung;
    boolean isEndDate;
    boolean isUpdateZeitaufwand = true;
    boolean isbooleanPlantermin = false;
    boolean isbooleanfaell = false;
    boolean isbooleanStartEnde = false;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    public static final String ALLOW_KEY = "ALLOWED";
    public static final String CAMERA_PREF = "camera_pref";
    Button btnUpdate;
    boolean isUpdateetDatumStart = true;
    boolean isUpdateetDatumEnde = true;
    String tempAtv;
    JSONArray BeschreibungJArray;
    JSONObject jObjOrderDetailTemp = new JSONObject();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.frg_order_edit, container, false);


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
            etDatumStart = rootView.findViewById(R.id.etDatumStart);
            etDatumEnde = rootView.findViewById(R.id.etDatumEnde);
            etZeitaufwand = rootView.findViewById(R.id.etZeitaufwand);
            etKosten = rootView.findViewById(R.id.etKosten);
            atvDrop = rootView.findViewById(R.id.atvDrop);
            ivTextWithBarcode = rootView.findViewById(R.id.ivTextWithBarcode);
            spArtdesAuftrags = rootView.findViewById(R.id.spArtdesAuftrags);
            etPlantermin = rootView.findViewById(R.id.etPlantermin);
            etfaelligkeit = rootView.findViewById(R.id.etfaelligkeit);
            etBezeichnung = rootView.findViewById(R.id.etBezeichnung);
            etKurzbezeichnung = rootView.findViewById(R.id.etKurzbezeichnung);
            atvBeschreibung = rootView.findViewById(R.id.atvBeschreibung);
            sFertigmeldung = rootView.findViewById(R.id.sFertigmeldung);
            btnUpdate = rootView.findViewById(R.id.btnUpdate);

            etfaelligkeit.clearFocus();
            etPlantermin.clearFocus();
            etDatumEnde.clearFocus();
            etDatumStart.clearFocus();

            etPlantermin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        isbooleanPlantermin = true;
                        isbooleanStartEnde = false;
                        isbooleanfaell = false;
                        openDatePickerPlantermin(etPlantermin);
                        Log.e("etPlantermin -Plan", isbooleanPlantermin + "");
                        Log.e("etPlantermin -faell", isbooleanfaell + "");
                    }
                }
            });


            etfaelligkeit.setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        isbooleanPlantermin = false;
                        isbooleanStartEnde = false;
                        isbooleanfaell = true;
                        openDatePickerfaelligkeit(etfaelligkeit);

                        Log.e("etfaelligkeit -Plan", isbooleanPlantermin + "");
                        Log.e("etfaelligkeit -faell", isbooleanfaell + "");
                    }
                }
            });


            etDatumStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        isbooleanStartEnde = true;
                        isbooleanPlantermin = false;
                        isbooleanfaell = false;


                        openDatePicker(etDatumStart, false);

                        Log.e("etDatumStart -Plan", isbooleanPlantermin + "");
                        Log.e("etDatumStart -faell", isbooleanfaell + "");

                    }
                }
            });

            etDatumEnde.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        isbooleanStartEnde = true;
                        isbooleanPlantermin = false;
                        isbooleanfaell = false;
                        openDatePicker(etDatumEnde, true);
                        Log.e("etDatumEnde -Plan", isbooleanPlantermin + "");
                        Log.e("etDatumEnde -faell", isbooleanfaell + "");
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

            sFertigmeldung.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(Common.isStringEmpty(etDatumStart.getText().toString().trim()) && Common.isStringEmpty(etDatumEnde.getText().toString().trim()) ){

                        if(isChecked){
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat mdformat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                            String strDate = mdformat.format(calendar.getTime());
                            etDatumStart.setText(strDate);
                            etDatumEnde.setText(strDate);
                        }
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
                    Objectposition = -1;
                    Log.e("onChangeArtpos", Objectposition + "");
                }
            });
            etDatumStart.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (isUpdateetDatumStart)
                        etDatumStart.setError(null);
                    else
                        isUpdateetDatumStart = true;
                }
            });

            etDatumEnde.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (isUpdateetDatumEnde)
                        etDatumEnde.setError(null);
                    else
                        isUpdateetDatumEnde = true;
                }
            });

            ivTextWithBarcode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CheckPermission();
                }
            });
            etZeitaufwand.addTextChangedListener(new TextWatcher() {

                @Override
                public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (isUpdateZeitaufwand)
                        setDateOnHourDifference();
                    else
                        isUpdateZeitaufwand = true;
                }
            });

            if (Common.getSharedPreferences(getContext(), "EditOrdersFromList").equalsIgnoreCase("True")) {
                getOrderDetail();
            }else{
                getAusstattungID();
                //TODO
//                Log.e("editOrderDetail", Common.editOrderDetail.toString());
            }

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
        Dashboard.ivService.setVisibility(View.GONE);
        Dashboard.ivServiceEdit.setVisibility(View.GONE);
        Dashboard.ivDocumnetEdit.setVisibility(View.GONE);
        Dashboard.ivDrawerIndicator.setVisibility(View.GONE);
        Dashboard.ivBack.setVisibility(View.VISIBLE);
        try {
            Dashboard.tvTitle.setText(("Auftrag Nr.") + ":" +Common.selectedOrderDetail.getString("OrderNr").trim() + " " +"bearbeiten");
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Dashboard.tvTitle.setText("Auftrag bearbeiten");

        if (Common.isStringEmpty(Common.BARCODE_STRING)) {
            atvDrop.setText(tempAtv);
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
                Objectposition = Integer.parseInt(BarcodeString);
                Log.e("initialObjectpos", Objectposition + "");
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
            integrator.setPrompt("Einen Barcode scannen");
            integrator.setOrientationLocked(false);
            integrator.setCameraId(0);  // Use a specific camera of the device
            integrator.setBeepEnabled(false);
            integrator.initiateScan();
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
        alertDialog.setMessage("Die App muss auf die Kamera zugreifen.");
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

    public static Boolean getFromPref(Context context, String key) {
        SharedPreferences myPrefs = context.getSharedPreferences(CAMERA_PREF, Context.MODE_PRIVATE);
        return (myPrefs.getBoolean(key, false));
    }

    private void openDatePickerPlantermin(final TextInputEditText etTempDatePicker2) {
        Log.e("openDatePlantermin","openDatePickerPlantermin");
        etfaelligkeit.clearFocus();
        etPlantermin.clearFocus();
        this.etTempDatePickerPlantermin = etTempDatePicker2;
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(dateDelegate, now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setVersion(DatePickerDialog.Version.VERSION_1);
        dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                etfaelligkeit.clearFocus();
                etPlantermin.clearFocus();
                etTempDatePickerPlantermin.clearFocus();
                etKurzbezeichnung.requestFocus();

            }
        });
        dpd.show(getActivity().getFragmentManager(), "etPlantermin");
    }


    private void openDatePickerfaelligkeit(final TextInputEditText etTempDatePicker3) {
        Log.e("openDaterfaelligkeit","openDatePickerfaelligkeit");
        this.etTempDatePickerfaelligkeit = etTempDatePicker3;

        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(dateDelegate, now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.setVersion(DatePickerDialog.Version.VERSION_1);
        dpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                etfaelligkeit.clearFocus();
                etPlantermin.clearFocus();
                etTempDatePickerfaelligkeit.clearFocus();
                etKurzbezeichnung.requestFocus();
            }
        });
        dpd.show(getActivity().getFragmentManager(), "etfaelligkeit");
    }


    public void setDateOnHourDifference() {
        try {
            String strDifference = etZeitaufwand.getText().toString().trim();
            if (!Common.isStringEmpty(strDifference)) {
                strDifference = Common.convertCommaToDot(strDifference);
                double temp = Double.parseDouble(strDifference);
                int hours = (int) (temp * 60 * 60 * 1000);
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                Calendar startDate = Calendar.getInstance();
                Calendar endDate = Calendar.getInstance();
                endDate.add(Calendar.MILLISECOND, hours);
                String dateString = dateFormat.format(new Date(startDate.getTimeInMillis()));
                etDatumStart.setText(dateString);
                dateString = dateFormat.format(new Date(endDate.getTimeInMillis()));
                etDatumEnde.setText(dateString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkValidation() {

        if ((Common.isValidEdittextatv(atvDrop, 0))) {
            if (Objectposition > -1) {
                int spArtdesAuftragspos = spArtdesAuftrags.getSelectedItemPosition();
                if (spArtdesAuftragspos != 0) {
                    if (Common.isValidEdittext(etfaelligkeit, 0)
                            && Common.isValidEdittext(etKosten, 0)) {
                        updateOrder();
                    } else {

                    }

                } else {
                    Common.showAlert(getActivity(), "ArtdesAuftrags* ");
                }
            } else {
                showObjectAlert(getActivity(), "Wählen Sie ein Objekt aus der Auswahlliste");
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
        atvBeschreibung.setText("Auftrag gemäß Beschreibung ausgeführt");
        atvBeschreibung.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                SetKeyValueCombo Beschreibungselection = (SetKeyValueCombo) parent.getItemAtPosition(position);
                try {
                    Beschreibungpos = Integer.parseInt(Beschreibungselection.getKey());
                    Log.e("onselectbungpos", Beschreibungpos + "");
                } catch (NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }
            }
        });

        fillDetails(Common.editOrderDetail);

    }

    private void updateOrder() {
        try {
            String StrArtdes = "";
            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("wartungid", Common.selectedOrderDetail.getString("OrderNr"));
            postDataParams.put("datumVorgesehen", etPlantermin.getText().toString().trim());
            postDataParams.put("datumfaelligkeit", etfaelligkeit.getText().toString().trim());
            String BezeichnungEncoded = URLEncoder.encode(etBezeichnung.getText().toString().trim(), "UTF-8");
            Common.APPLICATIONID = Common.editOrderDetail.getString("ApplicationID");
            if (Common.isStringEmpty(Common.APPLICATIONID)) {
                postDataParams.put("ApplicationID", "");
            } else {
                postDataParams.put("ApplicationID", Common.APPLICATIONID);
            }

//            if (Beschreibungpos > -1) {
//                StrBeschreibung = String.valueOf(Beschreibungpos);
//            }
            
            postDataParams.put("leistungsbeschreibung", atvBeschreibung.getText().toString().trim());

            StrArtdes = ArtdesAuftragsList.get(spArtdesAuftrags.getSelectedItemPosition()).getKey();
            postDataParams.put("Art", StrArtdes);
            String KurzbezeichnungEncoded = URLEncoder.encode(etKurzbezeichnung.getText().toString().trim(), "UTF-8");
            postDataParams.put("userID", Common.USER_SESSION);
            postDataParams.put("beschreibung", BezeichnungEncoded);
            postDataParams.put("beschreibungvA", KurzbezeichnungEncoded);
            postDataParams.put("datumstart", etDatumStart.getText().toString().trim());
            postDataParams.put("datumende", etDatumEnde.getText().toString().trim());
            postDataParams.put("zeitaufwand", Common.convertDotToComma(etZeitaufwand.getText().toString().trim()));
            postDataParams.put("kosten", Common.convertDotToComma(etKosten.getText().toString().trim()));
            Log.e("pos", Objectposition + "");
            if (Objectposition > -1) {
                strAusstattung = String.valueOf(Objectposition);
                postDataParams.put("ausstattungid", strAusstattung);
            }
            ;
            if (sFertigmeldung.isChecked())
                postDataParams.put("fertigmeldung", "true");
            else
                postDataParams.put("fertigmeldung", "false");

            HttpConnection httpConnection = new HttpConnection(delegate, getActivity());
            httpConnection.setRequestedfor("UpdateOrder");
            httpConnection.setIsloading(true);
            httpConnection.setPostDataParams(postDataParams);
            httpConnection.setUrl("Orders/UpdateOrder");
            httpConnection.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillDetails(JSONObject jObj) throws Exception {
        etPlantermin.setText(jObj.getString("DatumVorgesehen"));
        etfaelligkeit.setText(jObj.getString("Datumfaelligkeit"));
        etBezeichnung.setText(jObj.getString("Beschreibung"));
        etKurzbezeichnung.setText(jObj.getString("BeschreibungvA"));
        etKosten.setText(jObj.getString("Kosten"));
        etDatumStart.setText(jObj.getString("DatumStart"));
        Log.v("DatumStart", jObj.getString("DatumStart"));
        etDatumEnde.setText(jObj.getString("DatumEnde"));

        if (Common.isStringEmpty(jObj.getString("Leistungsbeschreibung"))) {
            atvBeschreibung.setText("Auftrag gemäß Vorgaben ausgeführt");
        } else {
            atvBeschreibung.setText(jObj.getString("Leistungsbeschreibung"));
        }

        isUpdateZeitaufwand = false;

        etZeitaufwand.setText(jObj.getString("Zeitaufwand"));

        strAusstattung = jObj.getString("AusstattungID").trim();
        stArtdesAuftrags = jObj.getString("Art").trim();

        for (int i = 1; i < ArtdesAuftragsList.size(); i++) {
            SetKeyValueCombo tempObj = ArtdesAuftragsList.get(i);
            if (tempObj.getKey().equals(stArtdesAuftrags)) {
                spArtdesAuftrags.setSelection(i);
                break;
            }
        }

        for (int j = 1; j < AusstattungList.size(); j++) {
            SetKeyValueCombo tempObj = AusstattungList.get(j);
            if (tempObj.getKey().equals(strAusstattung)) {
                atvDrop.setText(tempObj.getValue());
                tempAtv = tempObj.getValue();
                Objectposition = Integer.parseInt(strAusstattung);
                Log.e("initialArtpos", Objectposition + "");
                break;
            }
        }

        if (jObj.getBoolean("Fertigmeldung"))
            sFertigmeldung.setChecked(true);
        else
            sFertigmeldung.setChecked(false);
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
        Common.stopProgressDialouge();
    }


    private void openDatePicker(TextInputEditText etTempDatePicker1, boolean isEndDate) {
        Log.e("openDatePicker","openDatePicker");
        isbooleanPlantermin = false;
        isbooleanfaell = false;
        this.isEndDate = isEndDate;
        this.etTempDatePicker = etTempDatePicker1;
        etfaelligkeit.clearFocus();
        etPlantermin.clearFocus();
        Calendar now = Calendar.getInstance();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                dateDelegate,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.setVersion(DatePickerDialog.Version.VERSION_1);
        etfaelligkeit.clearFocus();
        etPlantermin.clearFocus();
        if (isEndDate) { // End date selected
            if (Common.isStringEmpty(etDatumStart.getText().toString().trim())) {
                Common.showAlert(getActivity(), "Bitte erst den Anfang eingeben.");
                return;
            } else {
                etfaelligkeit.clearFocus();
                etPlantermin.clearFocus();
                String[] tempStr = etDatumStart.getText().toString().trim().split(" ");
                String[] strStartTime = tempStr[0].split("\\.");
                int day = Integer.parseInt(strStartTime[0]);
                int month = Integer.parseInt(strStartTime[1]) - 1;
                int year = Integer.parseInt(strStartTime[2]);
                Calendar c = Calendar.getInstance();
                c.set(year, month, day);
                dpd.setMinDate(c); // If start date is present > set min Date
            }
        } else { // start date selected
            if (!Common.isStringEmpty(etDatumEnde.getText().toString().trim())) {
                etfaelligkeit.clearFocus();
                etPlantermin.clearFocus();
                String[] tempStr = etDatumEnde.getText().toString().trim().split(" ");
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
                etfaelligkeit.clearFocus();
                etPlantermin.clearFocus();
                etKurzbezeichnung.requestFocus();

            }
        });

    }


    private void openTimePickerstart(boolean isEndDate) {
        isbooleanPlantermin = false;
        isbooleanfaell = false;
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                timeDelegate,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );

        if (isEndDate) { // End date selected
            String[] tempStr = etDatumStart.getText().toString().trim().split(" ");
            String[] startDateString = tempStr[0].split("\\.");
            String[] endDateString = selectedDateString.split("\\.");
            if (startDateString[0].equals(endDateString[0]) && startDateString[1].equals(endDateString[1]) && startDateString[2].equals(endDateString[2])) {
                String[] strStartTime = tempStr[1].split(":");
                int hour = Integer.parseInt(strStartTime[0]);
                int minute = Integer.parseInt(strStartTime[1]);
                tpd.setMinTime(hour, minute, 0);// If start date is present > set minTime
                isEndDate= false;
            }
        } else { // start date selected
            if (!Common.isStringEmpty(etDatumEnde.getText().toString().trim())) {
                String[] tempStr = etDatumEnde.getText().toString().trim().split(" ");
                String[] startDateString = selectedDateString.split("\\.");
                String[] endDateString = tempStr[0].split("\\.");
                if (startDateString[0].equals(endDateString[0]) && startDateString[1].equals(endDateString[1]) && startDateString[2].equals(endDateString[2])) {
                    String[] strStartTime = tempStr[1].split(":");
                    int hour = Integer.parseInt(strStartTime[0]);
                    int minute = Integer.parseInt(strStartTime[1]);
                    tpd.setMaxTime(hour, minute, 0);// If end date is present > set maxTime
                }
            }
        }
        etKurzbezeichnung.requestFocus();
        tpd.setVersion(TimePickerDialog.Version.VERSION_1);
        tpd.show(getActivity().getFragmentManager(), "TimePickerDialogStart");
        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

                etKurzbezeichnung.requestFocus();
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {

        String year1 = String.valueOf(year);
        String month1 = String.format("%02d", monthOfYear + 1);
        String day1 = String.format("%02d", dayOfMonth);
        selectedDateString = day1 + "." + month1 + "." + year1;
        Log.e("isbooleanPlantermin", isbooleanPlantermin + "");
        Log.e("isbooleanStartEnde", isbooleanStartEnde + "");
        Log.e("isbooleanfaell", isbooleanfaell + "");

        if (isbooleanPlantermin) {

            isbooleanPlantermin = false;
            isbooleanStartEnde = false;
            isbooleanfaell = false;

            selectedDateString = day1 + "." + month1 + "." + year1;
            etTempDatePickerPlantermin.setText(selectedDateString);
            etTempDatePickerPlantermin.clearFocus();

            Log.e("isbooleanPlantermin", isbooleanPlantermin + "");
            Log.e("isbooleanStartEnde", isbooleanStartEnde + "");
            Log.e("isbooleanfaell", isbooleanfaell + "");
        }

        if (isbooleanfaell) {

            isbooleanPlantermin = false;
            isbooleanStartEnde = false;
            isbooleanfaell = false;

            selectedDateString = day1 + "." + month1 + "." + year1;
            etTempDatePickerfaelligkeit.setText(selectedDateString);
            etTempDatePickerfaelligkeit.clearFocus();

            Log.e("isbooleanPlantermin", isbooleanPlantermin + "");
            Log.e("isbooleanStartEnde", isbooleanStartEnde + "");
            Log.e("isbooleanfaell", isbooleanfaell + "");
        }




        if (isbooleanStartEnde) {
            isbooleanPlantermin = false;
            isbooleanStartEnde = false;
            isbooleanfaell = false;
                openTimePickerstart(isEndDate);
            etTempDatePicker.clearFocus();
            Log.e("isbooleanPlantermin", isbooleanPlantermin + "");
            Log.e("isbooleanStartEnde", isbooleanStartEnde + "");
            Log.e("isbooleanfaell", isbooleanfaell + "");

        }


    }

    @Override
    public void onTimeSet(TimePickerDialog timePickerDialog, int hourOfDay, int minute, int second) {
        etTempDatePicker.setText(selectedDateString + " " + hourOfDay + ":" + minute );
        isUpdateZeitaufwand = false;
//        etZeitaufwand.setText(Common.calculateHoursDifference(etDatumStart.getText().toString().trim(), etDatumEnde.getText().toString().trim()) + "");
        double temphours = Common.calculateHoursDifference(etDatumStart.getText().toString().trim(), etDatumEnde.getText().toString().trim());
        etZeitaufwand.setText(Common.round(temphours,2)+"");
        etTempDatePicker.clearFocus();
        etKurzbezeichnung.requestFocus();
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
                dialog.dismiss();
                Log.e("EditOrdersFromList", Common.getSharedPreferences(getContext(), "EditOrdersFromList"));
                Log.e("EditOrdersFromPage", Common.getSharedPreferences(getContext(), "EditOrdersFromPage"));

                if (Common.getSharedPreferences(getContext(), "EditOrdersFromList").equalsIgnoreCase("True")) {
                    Common.setSharedPreferences(getContext(), "EditOrdersFEditOrdersFromPageromList", "False");
                    Common.setSharedPreferences(getContext(), "EditOrdersFromPage", "False");
//                    getFragmentManager().popBackStack();
                    Intent i = new Intent(getActivity(), Dashboard.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);

                } else {
                    if (Common.getSharedPreferences(getContext(), "EditOrdersFromPage").equalsIgnoreCase("True")) {
                        Common.setSharedPreferences(getContext(), "EditOrdersFromPage", "False");
                        Common.setSharedPreferences(getContext(), "EditOrdersFromList", "False");
                        Intent i = new Intent(getActivity(), Dashboard.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }

                }

            }
        });
        dialog.setContentView(dv);
        dialog.show();
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

    private void getMaintenance() {
        try {
            HashMap<String, String> postDataParams = new HashMap<>();
            HttpConnection httpConnection = new HttpConnection(delegate, getContext());
            httpConnection.setRequestedfor("getMaintenance");
            httpConnection.setIsloading(false);
            httpConnection.setPostDataParams(postDataParams);
            httpConnection.setUrl("Orders/BindKeysMaintenance");
            httpConnection.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAusstattungID() {
        try {
            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("userID", Common.USER_SESSION);
            HttpConnection httpConnection = new HttpConnection(delegate, getContext());
            httpConnection.setRequestedfor("getAusstattungID");
            httpConnection.setIsloading(true);
            httpConnection.setPostDataParams(postDataParams);
            httpConnection.setUrl("Orders/BindAusstattungID");
            httpConnection.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillAusstattungSpinner(Context context, JSONArray rows, String mFirstRow) throws Exception {
        AusstattungList = new ArrayList<>();
        JSONObject jTypeData;
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
                    Objectposition = Integer.parseInt(selection.getKey());
                    Log.e("onselectArtpos", Objectposition + "");
                } catch (NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }
            }
        });
        getMaintenance();
    }
    @Override
    public void httpResponse(String response, String requestedFor) throws Exception {


        Common.stopProgressDialouge();
        if (requestedFor.equals("UpdateOrder")) {
            Common.stopProgressDialouge();
            JSONObject jObj = new JSONObject(response);
            if (jObj.getString("ResultCode").equals("SUCCESS")) {
                showAlert(jObj.getString("ResultMessage"));

            } else {
                showAlert(jObj.getString("ResultMessage"));
            }
        }

        if (requestedFor.equals("getOrderDetail")) {
            Common.stopProgressDialouge();
            JSONObject jObj = new JSONObject(response);
            if (jObj.getString("ResultCode").equals("SUCCESS")) {
                jObjOrderDetailTemp = jObj.getJSONObject("ResultObject");
                Common.editOrderDetail = jObjOrderDetailTemp;
                Log.e("editOrderDetail", Common.editOrderDetail.toString());
                getAusstattungID();

            }
        }


        if (requestedFor.equals("getAusstattungID")) {
            {
                Common.stopProgressDialouge();
                JSONObject jAusstattungObj = new JSONObject(response);
                if (jAusstattungObj.getString("ResultCode").equals("SUCCESS")) {
                    JSONArray jArrayAusstattungList = jAusstattungObj.getJSONArray("ResultObject");
                    if (jArrayAusstattungList.length() > 0) {
                        fillAusstattungSpinner(getContext(), jArrayAusstattungList, getResources().getString(R.string.Objekt));

                    }
                } else {
                    Common.showAlert(getContext(), jAusstattungObj.getString("ResultMessage"));
                }
            }
        }



        if (requestedFor.equals("getMaintenance")) {
            {
                JSONObject jMaintenanceObj = new JSONObject(response);
                if (jMaintenanceObj.getString("ResultCode").equals("SUCCESS")) {
                    JSONArray jArrayArtdesAuftragsList = jMaintenanceObj.getJSONArray("ResultObject");
                    if (jArrayArtdesAuftragsList.length() > 0) {
                        fillArtdesAuftragsSpinner(getContext(), jArrayArtdesAuftragsList, getResources().getString(R.string.ArtdesAuftrags));

                    }
                } else {
                    Common.showAlert(getContext(), jMaintenanceObj.getString("ResultMessage"));
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
            case R.id.btnUpdate:
                checkValidation();
                break;

            default:
                break;
        }
    }
}
