package com.visafm.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import android.widget.Spinner;
import android.widget.TextView;

import com.visafm.Dashboard;
import com.visafm.R;
import com.google.android.material.textfield.TextInputEditText;
import com.visafm.common.BaseClass;
import com.visafm.common.Common;
import com.visafm.common.HttpConnection;
import com.visafm.common.SetKeyValueCombo;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Ankit Patel on 20/02/19.
 */
@SuppressLint("DefaultLocale")
public class FrgAddService extends Fragment implements BaseClass, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, View.OnClickListener {
    BaseClass delegate = this;
    String selectedDateString = "";
    DatePickerDialog.OnDateSetListener dateDelegate = this;
    TimePickerDialog.OnTimeSetListener timeDelegate = this;
    TextInputEditText etVon;
    boolean isEndDate;
    Integer Leistungpos = -1;
    TextInputEditText etTempDatePicker;
    TextInputEditText etBis;
    AutoCompleteTextView atvLeistung;
    boolean isUpdateZeitaufwand = true;
    Button btnAddService;
    private static final String TAG = "At FrgService Details";
    private static ArrayList<SetKeyValueCombo> LeistungsartList;
    private static ArrayList<SetKeyValueCombo> LeistungList;
    private static ArrayList<SetKeyValueCombo> PersonList;
    private static ArrayList<SetKeyValueCombo> EinheitList;
    private static ArrayList<SetKeyValueCombo> MaterialList;
    boolean isUpdateetVon = true;
    boolean isUpdateetBis = true;
    Spinner spLeistungsart;
    Spinner spLeistung;
    TextInputEditText etBeschreibung;
    Spinner spPerson;
    TextInputEditText etAnzahl;
    Spinner spEinheit;
    Spinner spMaterial;
    Spinner spStatus;
    TextInputEditText etKosten;
    TextInputEditText etZeitaufwand;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_add_service, container, false);
        try {

            etVon = rootView.findViewById(R.id.etVon);
            atvLeistung = rootView.findViewById(R.id.atvLeistung);
            etBis = rootView.findViewById(R.id.etBis);
            btnAddService = rootView.findViewById(R.id.btnAddService);
            spLeistungsart = rootView.findViewById(R.id.spLeistungsart);
            spLeistung = rootView.findViewById(R.id.spLeistung);
            etBeschreibung = rootView.findViewById(R.id.etBeschreibung);
            spPerson = rootView.findViewById(R.id.spPerson);
            etAnzahl = rootView.findViewById(R.id.etAnzahl);
            spEinheit = rootView.findViewById(R.id.spEinheit);
            spMaterial = rootView.findViewById(R.id.spMaterial);
            spStatus = rootView.findViewById(R.id.spStatus);
            spStatus.setSelection(1);
            etKosten = rootView.findViewById(R.id.etKosten);
            etZeitaufwand = rootView.findViewById(R.id.etZeitaufwand);
            etZeitaufwand.setKeyListener(null);
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
            String now = dateFormatter.format(calendar.getTime());
            etVon.setText(now);
            etBis.setText(now);
            etVon.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        openDatePicker(etVon, false);
                    }
                }
            });

            etBis.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        openDatePicker(etBis, true);
                    }
                }
            });


            atvLeistung.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    atvLeistung.showDropDown();
                    return false;
                }
            });

            atvLeistung.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    Leistungpos = -1;
                    Log.e("Leistungpos", Leistungpos + "");
                }
            });


            etVon.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (isUpdateetVon)
                        etVon.setError(null);
                    else
                        isUpdateetVon = true;
                }
            });

            etBis.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (isUpdateetBis)
                        etBis.setError(null);
                    else
                        isUpdateetBis = true;
                }
            });
            etKosten.setText("0");
            getLeistungArt();
            btnAddService.setOnClickListener(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return rootView;
    }// onCreateView

    @Override
    public void onResume() {
        Dashboard.ivSortArrows.setVisibility(View.GONE);
        Dashboard.ivSortDown.setVisibility(View.GONE);
        Dashboard.ivSortUp.setVisibility(View.GONE);
        Dashboard.ivEdit.setVisibility(View.GONE);
        Dashboard.ivDocument.setVisibility(View.GONE);
        Dashboard.ivService.setVisibility(View.GONE);
        Dashboard.ivServiceEdit.setVisibility(View.GONE);
        Dashboard.ivDrawerIndicator.setVisibility(View.GONE);
        Dashboard.ivBack.setVisibility(View.VISIBLE);
        Dashboard.ivDocumnetEdit.setVisibility(View.GONE);
        Dashboard.tvTitle.setText("Leistung hinzufügen");
        super.onResume();
    }

    private void openDatePicker(TextInputEditText etTempDatePicker1, boolean isEndDate) {
        this.isEndDate = isEndDate;
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
            if (Common.isStringEmpty(etVon.getText().toString().trim())) {
                Common.showAlert(getActivity(), "Bitte zuerst DatumStart auswählen.");
                return;
            } else {
                String[] tempStr = etVon.getText().toString().trim().split(" ");
                String[] strStartTime = tempStr[0].split("\\.");
                int day = Integer.parseInt(strStartTime[0]);
                int month = Integer.parseInt(strStartTime[1]) - 1;
                int year = Integer.parseInt(strStartTime[2]);
                Calendar c = Calendar.getInstance();
                c.set(year, month, day);
                dpd.setMinDate(c); // If start date is present > set min Date
            }
        } else { // start date selected
            if (!Common.isStringEmpty(etBis.getText().toString().trim())) {
                String[] tempStr = etBis.getText().toString().trim().split(" ");
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
            }
        });
    }

    private void openTimePicker() {
        Calendar now = Calendar.getInstance();
        TimePickerDialog tpd = TimePickerDialog.newInstance(
                timeDelegate,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false
        );
        if (isEndDate) { // End date selected
            String[] tempStr = etVon.getText().toString().trim().split(" ");
            String[] startDateString = tempStr[0].split("\\.");
            String[] endDateString = selectedDateString.split("\\.");
            if (startDateString[0].equals(endDateString[0]) && startDateString[1].equals(endDateString[1]) && startDateString[2].equals(endDateString[2])) {
                String[] strStartTime = tempStr[1].split(":");
                int hour = Integer.parseInt(strStartTime[0]);
                int minute = Integer.parseInt(strStartTime[1]);
                tpd.setMinTime(hour, minute, 0);// If start date is present > set minTime
            }
        } else { // start date selected
            if (!Common.isStringEmpty(etBis.getText().toString().trim())) {
                String[] tempStr = etBis.getText().toString().trim().split(" ");
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

        tpd.setVersion(TimePickerDialog.Version.VERSION_1);
        tpd.show(getActivity().getFragmentManager(), "TimePickerDialogStart");
        tpd.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                etTempDatePicker.clearFocus();
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
        String year1 = String.valueOf(year);
        String month1 = String.format("%02d", monthOfYear + 1);
        String day1 = String.format("%02d", dayOfMonth);
        selectedDateString = day1 + "." + month1 + "." + year1;
        openTimePicker();
        etTempDatePicker.clearFocus();
    }

    @Override
    public void onTimeSet(TimePickerDialog timePickerDialog, int hourOfDay, int minute, int second) {
        etTempDatePicker.setText(String.format("%s %d:%d:%d", selectedDateString, hourOfDay, minute, second));
        isUpdateZeitaufwand = false;
        etZeitaufwand.setText(Common.calculateHoursDifference(etVon.getText().toString().trim(), etBis.getText().toString().trim()) + "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddService:
                checkValidation();
                break;
            default:
                break;
        }
    }

    private void showAlertConfirmation(String Msg) {
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
                getActivity().onBackPressed();
            }
        });

        dialog.setContentView(dv);
        dialog.show();
    }

    private void fillLeistungArtTypeSpinner(Context context, JSONArray rows, String mFirstRow) throws Exception {
        LeistungsartList = new ArrayList<>();
        int index = 0;
        JSONObject jTypeData;
        if (mFirstRow != null)
            LeistungsartList.add(index, new SetKeyValueCombo("", mFirstRow));
        else
            index = -1;

        for (int i = 0; i < rows.length(); i++) {
            index++;
            jTypeData = rows.getJSONObject(i);
            if (!jTypeData.getString("ArtID").equals("") && !jTypeData.getString("Beschreibung").equals(""))
                LeistungsartList.add(index, new SetKeyValueCombo(jTypeData.getString("ArtID"), jTypeData.getString("Beschreibung")));
        }
        Log.d("fillTypeSpinner", "fillTypeSpinner:" + LeistungsartList);
        ArrayAdapter<SetKeyValueCombo> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, LeistungsartList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLeistungsart.setAdapter(adapter);
        getLeistung();

    }

    private void fillLeistungSpinner(Context context, JSONArray rows) throws Exception {
        LeistungList = new ArrayList<>();
        JSONObject jTypeData;
        for (int i = 0; i < rows.length(); i++) {
            jTypeData = rows.getJSONObject(i);
            LeistungList.add(i, new SetKeyValueCombo(String.valueOf(i), jTypeData.getString("Beschreibung1")));
        }
        Log.d("fillTypeSpinner", "fillTypeSpinner:" + LeistungList);
        ArrayAdapter<SetKeyValueCombo> LeistungAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, LeistungList);

        atvLeistung.setAdapter(LeistungAdapter);
        atvLeistung.setText("Leistung gemäß Auftrag ausgeführt");
        atvLeistung.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {
                SetKeyValueCombo selection = (SetKeyValueCombo) parent.getItemAtPosition(position);
                try {
                    Leistungpos = Integer.parseInt(selection.getKey());
                    Log.e("Leistungpos", Leistungpos + "");
                } catch (NumberFormatException nfe) {
                    System.out.println("Could not parse " + nfe);
                }
            }

        });

        fillLeistung();

    }

    private void fillPersonSpinner(Context context, JSONArray rows, String mFirstRow) throws Exception {
        PersonList = new ArrayList<>();
        int index = 0;
        JSONObject jTypeData;
        if (mFirstRow != null)
            PersonList.add(index, new SetKeyValueCombo("", mFirstRow));
        else
            index = -1;
        for (int i = 0; i < rows.length(); i++) {
            index++;
            jTypeData = rows.getJSONObject(i);
            if (!jTypeData.getString("MitarbeiterID").equals("") && !jTypeData.getString("Durchgeführt").equals(""))
                PersonList.add(index, new SetKeyValueCombo(jTypeData.getString("MitarbeiterID"), jTypeData.getString("Durchgeführt")));
        }
        Log.d("fillTypeSpinner", "fillTypeSpinner:" + PersonList);
        ArrayAdapter<SetKeyValueCombo> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, PersonList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPerson.setAdapter(adapter);
        fillMelder();
    }

    private void fillMelder() throws Exception {
        String strSpPersonSignedIn = Common.getSharedPreferences(getContext(), "previousUsername");
        Log.v("strSpPersonSignedIn", strSpPersonSignedIn);
        for (int j = 0; j < PersonList.size(); j++) {
            SetKeyValueCombo PersonObj = PersonList.get(j);
            Log.v("PersonObj", PersonObj.getValue().toString());
            if (PersonObj.getValue().contains(strSpPersonSignedIn)) {
                spPerson.setSelection(j);
                break;
            }
        }
        Common.stopProgressDialouge("getLeistungArt");
    }

    private void fillLeistung() throws Exception {
        if (strContains("Leistung gemäß Auftrag ausgeführt")) {

        } else {
            atvLeistung.setText("Leistung gemäß Auftrag ausgeführt");
        }
        getEinheit();

    }

    public boolean strContains(String str) {
        boolean isvalueexists = false;
        for (int j = 0; j < LeistungList.size(); j++) {
            SetKeyValueCombo tempObj = LeistungList.get(j);
            if (tempObj.getValue().contains(str)) {
                atvLeistung.setText(tempObj.getValue());
                Leistungpos = Integer.parseInt(tempObj.getKey());
                Log.e("initialObjectpos", Leistungpos + "");
                isvalueexists = true;
                break;
            } else {
                isvalueexists = false;
            }
        }
        return isvalueexists;

    }

    private void fillEinheitSpinner(Context context, JSONArray rows, String mFirstRow) throws Exception {
        EinheitList = new ArrayList<>();
        int index = 0;
        JSONObject jTypeData;
        if (mFirstRow != null)
            EinheitList.add(index, new SetKeyValueCombo("", mFirstRow));
        else
            index = -1;

        for (int i = 0; i < rows.length(); i++) {
            index++;
            jTypeData = rows.getJSONObject(i);
            if (!jTypeData.getString("ID").equals("") && !jTypeData.getString("Bezeichnung").equals(""))
                EinheitList.add(index, new SetKeyValueCombo(jTypeData.getString("ID"), jTypeData.getString("Bezeichnung")));
        }
        Log.d("fillTypeSpinner", "fillTypeSpinner:" + EinheitList);
        ArrayAdapter<SetKeyValueCombo> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, EinheitList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEinheit.setAdapter(adapter);
        getMaterial();
    }

    private void checkValidation() {
        if (Common.isValidEdittext(etVon, 0) && Common.isValidEdittext(etBis, 0)
                && Common.isValidEdittext(etKosten, 0)) {
            int spStatuspos = spStatus.getSelectedItemPosition();
            int spPersonpos = spPerson.getSelectedItemPosition();
            if (spPersonpos != 0) {
                if (spStatuspos != 0) {
                    addService();
                } else {
                    Common.showAlert(getActivity(), " Status* ");
                }

            } else {
                Common.showAlert(getActivity(), " Melder* ");
            }

        }

    }

    private void addService() {
        try {
            String StrLeistungsart = "", StrPerson = "", StrEinheit = "", StrMaterial = "", StrStatus = "", StrLeistung = "";
            StrLeistungsart = LeistungsartList.get(spLeistungsart.getSelectedItemPosition()).getKey();
            StrPerson = PersonList.get(spPerson.getSelectedItemPosition()).getKey();
            StrEinheit = EinheitList.get(spEinheit.getSelectedItemPosition()).getKey();
            StrMaterial = MaterialList.get(spMaterial.getSelectedItemPosition()).getKey();
            StrStatus = spStatus.getSelectedItem().toString();
            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("Zeitaufwand", etZeitaufwand.getText().toString().trim());
            postDataParams.put("Von", etVon.getText().toString().trim());
            postDataParams.put("BIS", etBis.getText().toString().trim());
            postDataParams.put("Kosten", etKosten.getText().toString().trim());
            postDataParams.put("ApplicationID", Common.APPLICATIONID);
            postDataParams.put("WartungID", Common.selectedOrderDetail.getString("OrderNr"));
            postDataParams.put("Art", StrLeistungsart);

            if (Leistungpos > -1) {
                StrLeistung = LeistungList.get(Leistungpos).getValue();

                if (Common.isStringEmpty(StrLeistung)) {
                    postDataParams.put("Beschreibung1", "");
                } else {
                    postDataParams.put("Beschreibung1", StrLeistung);
                }
            } else {
                if (Common.isStringEmpty(atvLeistung.getText().toString().trim())) {
                    postDataParams.put("Beschreibung1", "");
                } else {
                    postDataParams.put("Beschreibung1", atvLeistung.getText().toString().trim());
                }
            }
            postDataParams.put("Beschreibung2", etBeschreibung.getText().toString().trim());
            postDataParams.put("Melder", StrPerson);
            postDataParams.put("MaterialAnzahl", etAnzahl.getText().toString().trim());
            postDataParams.put("MaterialEinh", StrEinheit);
            postDataParams.put("MaterialArt", StrMaterial);

            if (StrStatus.contains("status*")) {
                postDataParams.put("Status", "");
            } else {
                postDataParams.put("Status", StrStatus);
            }
            postDataParams.put("userID", Common.USER_SESSION);
            postDataParams.put("Abteilung", "");
            postDataParams.put("WartungIDAlt", "0");
            postDataParams.put("Fertigmeldung", "True");
            HttpConnection httpConnection = new HttpConnection(delegate, getActivity());
            httpConnection.setRequestedfor("insertOrderDetails");
            httpConnection.setIsloading(true);
            httpConnection.setPostDataParams(postDataParams);
            httpConnection.setUrl("Orders/InsertOrderDetails");
            httpConnection.execute("");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillMaterialSpinner(Context context, JSONArray rows, String mFirstRow) throws Exception {
        MaterialList = new ArrayList<>();
        int index = 0;
        JSONObject jTypeData;
        if (mFirstRow != null)
            MaterialList.add(index, new SetKeyValueCombo("", mFirstRow));
        else
            index = -1;

        for (int i = 0; i < rows.length(); i++) {
            index++;
            jTypeData = rows.getJSONObject(i);
            if (!jTypeData.getString("ID").equals("") && !jTypeData.getString("Bezeichnung").equals(""))
                MaterialList.add(index, new SetKeyValueCombo(jTypeData.getString("ID"), jTypeData.getString("Bezeichnung")));
        }
        Log.d("fillTypeSpinner", "fillTypeSpinner:" + MaterialList);
        ArrayAdapter<SetKeyValueCombo> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, MaterialList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMaterial.setAdapter(adapter);
        getPerson();
    }

    private void getLeistungArt() {
        try {
            HttpConnection httpConnection = new HttpConnection(delegate, getActivity());
            httpConnection.setRequestedfor("getLeistungArt");
            httpConnection.setIsloading(true);
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
            httpConnection.setIsloading(false);
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
            httpConnection.setIsloading(false);
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
            httpConnection.setIsloading(false);
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
            httpConnection.setIsloading(false);
            httpConnection.setUrl("Orders/BindMaterial");
            httpConnection.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void httpResponse(String response, String requestedFor) throws Exception {

        if (requestedFor.equals("getLeistungArt")) {
            {
                JSONObject jArtObj = new JSONObject(response);
                if (jArtObj.getString("ResultCode").equals("SUCCESS")) {
                    JSONArray jArrayLeistungArtList = jArtObj.getJSONArray("ResultObject");
                    if (jArrayLeistungArtList.length() > 0) {
                        fillLeistungArtTypeSpinner(getActivity(), jArrayLeistungArtList, getResources().getString(R.string.Leistungsart));
                    } else {
                        fillLeistungArtTypeSpinner(getActivity(), jArrayLeistungArtList, getResources().getString(R.string.Leistungsart));
                        getLeistung();
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
                        fillLeistungSpinner(getActivity(), jArrayLeistung);
                    } else {
                        fillLeistungSpinner(getActivity(), jArrayLeistung);
                        getEinheit();
                    }
                } else {
                    Common.showAlert(getActivity(), jLeistungObj.getString("ResultMessage"));
                }
            }
        }

        if (requestedFor.equals("insertOrderDetails")) {
            JSONObject jObj = new JSONObject(response);
            if (jObj.getString("ResultCode").equals("SUCCESS")) {
                showAlertConfirmation(jObj.getString("ResultMessage"));
            } else {
                Common.showAlert(getActivity(), jObj.getString("ResultMessage"));
            }
        }


        if (requestedFor.equals("getPerson")) {
            {
                JSONObject jPersonObj = new JSONObject(response);
                if (jPersonObj.getString("ResultCode").equals("SUCCESS")) {
                    JSONArray jArrayPerson = jPersonObj.getJSONArray("ResultObject");
                    if (jArrayPerson.length() > 0) {
                        fillPersonSpinner(getActivity(), jArrayPerson, getResources().getString(R.string.Person));

                    } else {
                        fillPersonSpinner(getActivity(), jArrayPerson, getResources().getString(R.string.Person));
                        fillMelder();
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
                        fillEinheitSpinner(getActivity(), jArrayEinheit, getResources().getString(R.string.Einheit));
                    } else {
                        fillEinheitSpinner(getActivity(), jArrayEinheit, getResources().getString(R.string.Einheit));
                        getMaterial();
                    }
                } else {
                    Common.showAlert(getActivity(), jEinheitObj.getString("ResultMessage"));
                }
            }
        }

        if (requestedFor.equals("getMaterial")) {
            {
                JSONObject jMaterialObj = new JSONObject(response);
                if (jMaterialObj.getString("ResultCode").equals("SUCCESS")) {
                    JSONArray jArrayMaterial = jMaterialObj.getJSONArray("ResultObject");
                    if (jArrayMaterial.length() > 0) {
                        fillMaterialSpinner(getActivity(), jArrayMaterial, getResources().getString(R.string.Material));
                    } else {
                        fillMaterialSpinner(getActivity(), jArrayMaterial, getResources().getString(R.string.Material));
                        getPerson();
                    }
                } else {
                    Common.showAlert(getActivity(), jMaterialObj.getString("ResultMessage"));
                }
            }
        }
    }

    @Override
    public void httpFailure(String response, String requestedFor) throws Exception {

    }
}

