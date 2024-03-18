package com.visafm.roombook.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.visafm.roombook.ActivityBridge;
import com.visafm.roombook.Dashboard;
import com.visafm.roombook.R;
import com.visafm.roombook.common.BaseClass;
import com.visafm.roombook.common.Common;
import com.visafm.roombook.common.FilePath;
import com.visafm.roombook.common.HttpConnection;
import com.visafm.roombook.common.HttpConnectionImageUploading;
import com.visafm.roombook.common.SetKeyValueCombo;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ankit Patel on 20/02/19.
 */
public class FrgEditOrderDocuments extends Fragment implements BaseClass, DatePickerDialog.OnDateSetListener, View.OnClickListener {
    BaseClass delegate = this;
    String selectedDateString = "";
    String StrUnterartType = "";
    DatePickerDialog.OnDateSetListener dateDelegate = this;
    TextInputEditText etGültigAb;
    TextInputEditText etGültigBis;
    TextInputEditText etWiedervorlage;
    TextInputEditText etBeschreibung;
    TextInputEditText etBezeichnung;
    TextInputEditText etTempDatePicker;
    Button btnUpdate;
    Button btnUpload;
    TextInputEditText etEingestellt;
    public Spinner spFileTyp;
    Spinner spArt;
    Spinner spUnterart;
    ImageView ivDocumentImage;
    TextView tvImageMessage;
    CheckBox cbGebrauchsanweisung;
    CheckBox cbInstandhaltung;
    CheckBox cbVermietung;
    CheckBox cbBetriebsKosten;
    String strBinID = "";
    HttpConnectionImageUploading httpConnectionImageUploading;
    private static ArrayList<SetKeyValueCombo> docTypeList;
    private static ArrayList<SetKeyValueCombo> ArtTypeList;
    public static ArrayList<SetKeyValueCombo> UnterartTypeList;
    public static boolean isDocUploaded = false;
    private static final String TAG = "At FrgOrder Documets";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frg_order_documents, container, false);
        tvImageMessage = rootView.findViewById(R.id.tvImageMessage);
        try {
            cbGebrauchsanweisung = rootView.findViewById(R.id.cbGebrauchsanweisung);
            cbInstandhaltung = rootView.findViewById(R.id.cbInstandhaltung);
            cbVermietung = rootView.findViewById(R.id.cbVermietung);
            cbBetriebsKosten = rootView.findViewById(R.id.cbBetriebsKosten);
            etBeschreibung = rootView.findViewById(R.id.etBeschreibung);
            etBezeichnung = rootView.findViewById(R.id.etBezeichnung);
            etGültigAb = rootView.findViewById(R.id.etGültigAb);
            etGültigBis = rootView.findViewById(R.id.etGültigBis);
            etWiedervorlage = rootView.findViewById(R.id.etWiedervorlage);
            etEingestellt = rootView.findViewById(R.id.etEingestellt);
            ivDocumentImage = rootView.findViewById(R.id.ivDocumentImage);
            btnUpdate = rootView.findViewById(R.id.btnUpdate);
            btnUpdate.setText("Aktualisieren");
            btnUpload = rootView.findViewById(R.id.btnUpload);
            spArt = rootView.findViewById(R.id.spArt);
            spFileTyp = rootView.findViewById(R.id.spFileTyp);
            btnUpload.setOnClickListener(this);
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat mdformat = new SimpleDateFormat("dd.MM.yyyy");
            String strDate = mdformat.format(calendar.getTime());
            etEingestellt.setText(strDate);
            spFileTyp.setPrompt("Pick One");
            spUnterart = rootView.findViewById(R.id.spUnterart);

            etGültigAb.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        openDatePicker(etGültigAb, false);
                    }
                }
            });

            etGültigBis.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        openDatePicker(etGültigBis, true);
                    }
                }
            });

            etWiedervorlage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        openDatePickerDefault(etWiedervorlage);
                    }
                }
            });

            spArt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("", "Id" + getArtTypeID());
                    getUnterart();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            btnUpdate.setOnClickListener(this);
            getDocType();
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
        Dashboard.ivDocumnetEdit.setVisibility(View.GONE);
        Dashboard.ivDrawerIndicator.setVisibility(View.GONE);
        Dashboard.ivBack.setVisibility(View.VISIBLE);
        Dashboard.tvTitle.setText("Dokument bearbeiten");
        super.onResume();
    }

    private void openDatePickerDefault(final TextInputEditText etTempDatePicker2) {
        this.etTempDatePicker = etTempDatePicker2;
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
            }
        });
        dpd.show(getActivity().getFragmentManager(), "etWiedervorlage");
    }

    private void openDatePicker(final TextInputEditText etTempDatePicker1, boolean isEndDate) {
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
            if (Common.isStringEmpty(etGültigAb.getText().toString().trim())) {
                Common.showAlert(getActivity(), "Bitte zuerst DatumStart auswählen.");
                etTempDatePicker.clearFocus();
                return;
            } else {
                String[] tempStr = etGültigAb.getText().toString().trim().split(" ");
                String[] strStartTime = tempStr[0].split("\\.");
                int day = Integer.parseInt(strStartTime[0]);
                int month = Integer.parseInt(strStartTime[1]) - 1;
                int year = Integer.parseInt(strStartTime[2]);
                Calendar c = Calendar.getInstance();
                c.set(year, month, day);
                dpd.setMinDate(c); // If start date is present > set min Date
            }
        } else { // start date selected
            if (!Common.isStringEmpty(etGültigBis.getText().toString().trim())) {
                String[] tempStr = etGültigBis.getText().toString().trim().split(" ");
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
                etGültigAb.setText("");
                etGültigBis.setText("");
            }
        });
    }

    private void getDocType() {
        try {
            HttpConnection httpConnection = new HttpConnection(delegate, getActivity());
            httpConnection.setRequestedfor("getDocType");
            httpConnection.setIsloading(true);
            httpConnection.setUrl("Orders/ContentTypes");
            httpConnection.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getArt() {
        try {
            HttpConnection httpConnection = new HttpConnection(delegate, getActivity());
            httpConnection.setRequestedfor("getArt");
            httpConnection.setIsloading(false);
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
            postDataParams.put("docoFK_TlkpDocTypes", getArtTypeID());
            httpConnection.setRequestedfor("getUnterart");
            httpConnection.setIsloading(false);
            httpConnection.setPostDataParams(postDataParams);
            httpConnection.setUrl("Orders/DocTypeOptions");
            httpConnection.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
        String year1 = String.valueOf(year);
        String month1 = String.format("%02d", monthOfYear + 1);
        String day1 = String.format("%02d", dayOfMonth);
        selectedDateString = day1 + "." + month1 + "." + year1;
        etTempDatePicker.clearFocus();
        etTempDatePicker.setText(selectedDateString);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpdate:
                checkValidation();
                break;
            case R.id.btnUpload:
                fileUploadDialog();
                break;
            default:
                break;
        }
    }



    private void fillDocContentTypeSpinner(Context context, JSONArray rows, String mFirstRow) throws Exception {
        docTypeList = new ArrayList<>();
        int index = 0;
        JSONObject jData;

        if (mFirstRow != null)
            docTypeList.add(index, new SetKeyValueCombo("", mFirstRow));
        else
            index = -1;

        for (int i = 0; i < rows.length(); i++) {
            index++;
            jData = rows.getJSONObject(i);
            if (!jData.getString("contID").equals("") && !jData.getString("contText1").equals(""))
                docTypeList.add(index, new SetKeyValueCombo(jData.getString("contID"), jData.getString("contText1")));
        }
        Log.d("fillDocContTypeSpinner", "fillDocContTypeSpinner:" + docTypeList);
        ArrayAdapter<SetKeyValueCombo> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, docTypeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spFileTyp.setAdapter(adapter);
        getArt();
    }

    private void fillArtTypeSpinner(Context context, JSONArray rows, String mFirstRow) throws Exception {
        ArtTypeList = new ArrayList<>();
        int index = 0;
        JSONObject jTypeData;
        if (mFirstRow != null)
            ArtTypeList.add(index, new SetKeyValueCombo("", mFirstRow));
        else
            index = -1;

        for (int i = 0; i < rows.length(); i++) {
            index++;
            jTypeData = rows.getJSONObject(i);
            if (!jTypeData.getString("doctID").equals("") && !jTypeData.getString("doctText1").equals(""))
                ArtTypeList.add(index, new SetKeyValueCombo(jTypeData.getString("doctID"), jTypeData.getString("doctText1")));
        }
        Log.d("fillTypeSpinner", "fillTypeSpinner:" + ArtTypeList);
        ArrayAdapter<SetKeyValueCombo> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, ArtTypeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spArt.setAdapter(adapter);
        fillDetails(Common.selectedDocumentDetail);
    }

    private String getArtTypeID() {
        return ArtTypeList.get(spArt.getSelectedItemPosition()).getKey();
    }


    private void setArtTypeID(String key) {
        for (int i = 0; i < docTypeList.size(); i++) {
            if (docTypeList.get(i).getKey().equals(key))
                spFileTyp.setSelection(i);
        }
    }

    private void fillUnterartType(Context context, JSONArray rows, String mFirstRow) throws Exception {
        UnterartTypeList = new ArrayList<>();
        int index = 0;
        JSONObject jSubTypeData;
        if (mFirstRow != null)
            UnterartTypeList.add(index, new SetKeyValueCombo("", mFirstRow));
        else
            index = -1;

        for (int i = 0; i < rows.length(); i++) {
            index++;
            jSubTypeData = rows.getJSONObject(i);
            if (!jSubTypeData.getString("docoID").equals("") && !jSubTypeData.getString("docoText1").equals(""))
                UnterartTypeList.add(index, new SetKeyValueCombo(jSubTypeData.getString("docoID"), jSubTypeData.getString("docoText1")));


        }
        Log.d("fillTypeSpinner", "fillTypeSpinner:" + UnterartTypeList);
        ArrayAdapter<SetKeyValueCombo> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, UnterartTypeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUnterart.setAdapter(adapter);

        if (StrUnterartType.length() > 0) {
            setUnterartTypeList(StrUnterartType);
        }

    }

    private void uploadImage(File imageFile, Uri imageUri) {
        HashMap<String, String> postDataParams = new HashMap<>();

        postDataParams.put("ApplicationID", Common.APPLICATIONID);
        postDataParams.put("userID", Common.USER_SESSION);

        httpConnectionImageUploading = new HttpConnectionImageUploading(this, getActivity());
        httpConnectionImageUploading.setRequestedfor("UploadFile");
        httpConnectionImageUploading.setIsloading(true);
        httpConnectionImageUploading.setIsImageUploading(true);
        httpConnectionImageUploading.setImageFile(imageFile);
        httpConnectionImageUploading.setImageUri(imageUri);
        httpConnectionImageUploading.setPostDataParams(postDataParams);
        httpConnectionImageUploading.setUrl("Orders/UploadDocument");
        httpConnectionImageUploading.execute("");
        spFileTyp.setEnabled(false);
    }

    private void checkValidation() {

        int pos = spArt.getSelectedItemPosition();
//        if (isDocUploaded) {
        if (pos != 0) {
            update();
        } else {
            Common.showAlert(getActivity(), " Wählen Art* ");
        }
//        } else {
//            Common.showAlert(getActivity(), "\n" + "Bitte zuerst ein Dokument hochladen");
//        }
    }


    private void update() {
        try {
            String StrSubArtTypeId = "", StrArtTypeId = "";

            StrSubArtTypeId = UnterartTypeList.get(spUnterart.getSelectedItemPosition()).getKey();
            StrArtTypeId = ArtTypeList.get(spArt.getSelectedItemPosition()).getKey();

            HashMap<String, String> postDataParams = new HashMap<>();

            Log.v("selectedDocumentDetail", Common.selectedDocumentDetail.toString());
            postDataParams.put("docFK_TlkpDocTypes", StrArtTypeId);
            postDataParams.put("docFK_TlkpDocTypeOptions", StrSubArtTypeId);
            postDataParams.put("filLabel", etBezeichnung.getText().toString().trim());
            postDataParams.put("filDescription", etBeschreibung.getText().toString().trim());
            postDataParams.put("docReminder", etWiedervorlage.getText().toString().trim());
            postDataParams.put("docValidFrom", etGültigAb.getText().toString().trim());
            postDataParams.put("docValidTo", etGültigBis.getText().toString().trim());
            postDataParams.put("docChangedOn", etEingestellt.getText().toString().trim());
            postDataParams.put("docChangedBy", Common.USER_SESSION);

            if (!Common.isStringEmpty(strBinID)) {
                postDataParams.put("binID", strBinID);
            } else {
                postDataParams.put("binID", Common.selectedDocumentDetail.getString("binID"));
            }

            if (cbGebrauchsanweisung.isChecked())
                postDataParams.put("docIsOperatingManual", "true");
            else
                postDataParams.put("docIsOperatingManual", "false");
            postDataParams.put("original_docID", Common.selectedDocumentDetail.getString("docID"));

            postDataParams.put("docIsSignature", "");
            if (cbInstandhaltung.isChecked())
                postDataParams.put("docIsMaintenance", "true");
            else
                postDataParams.put("docIsMaintenance", "false");

            if (cbVermietung.isChecked())
                postDataParams.put("docIsRentMgmt", "true");
            else
                postDataParams.put("docIsRentMgmt", "false");


            if (cbBetriebsKosten.isChecked())
                postDataParams.put("docIsCostMgmt", "true");
            else
                postDataParams.put("docIsCostMgmt", "false");

            postDataParams.put("userID", Common.USER_SESSION);
            postDataParams.put("ApplicationID", Common.APPLICATIONID);

            HttpConnection httpConnection = new HttpConnection(delegate, getActivity());
            httpConnection.setRequestedfor("UpdateDocument");
            httpConnection.setIsloading(true);
            httpConnection.setPostDataParams(postDataParams);
            httpConnection.setUrl("Orders/UpdateDocument");
            httpConnection.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                if (Common.getSharedPreferences(getContext(), "EditDocumentsFromList").equalsIgnoreCase("True")) {
                    Common.setSharedPreferences(getContext(), "EditDocumentDetailFromPage", "False");
                    Common.setSharedPreferences(getContext(), "EditDocumentsFromList", "False");
                    getFragmentManager().popBackStack();

                } else {
                    if (Common.getSharedPreferences(getContext(), "EditDocumentDetailFromPage").equalsIgnoreCase("True")) {
                        Common.setSharedPreferences(getContext(), "EditDocumentDetailFromPage", "False");
                        getFragmentManager().popBackStack();
                        getFragmentManager().popBackStack();
                    }
                }

            }
        });

        dialog.setContentView(dv);
        dialog.show();
    }

    private void fileUploadDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        @SuppressWarnings("static-access")
        LayoutInflater I = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View dv = I.inflate(R.layout.docupload_dialog_alert, null);
        TextView tvChooseFile = dv.findViewById(R.id.tvChooseFile);
        TextView tvTakePicture = dv.findViewById(R.id.tvTakePicture);
        dialog.setCanceledOnTouchOutside(true);
        tvChooseFile.setOnClickListener(v -> {
            launchFilePicker();
            dialog.dismiss();
        });
        tvTakePicture.setOnClickListener(v -> {
            launchTakePicture();
            dialog.dismiss();
            tvImageMessage.setText("");
        });
        dialog.setContentView(dv);
        dialog.show();
    }

    private void launchFilePicker() {
        ActivityBridge.Callback<Boolean> action = granted -> {
            if (granted) {
                ((ActivityBridge) getActivity()).pickDocument(new String[]{"*/*"}, result -> {
                    if (result != null) {
//                        File pickedFile = new File(FilePath.getPath(getContext(), result));
                        handleDocument(null, result);
                    }
                });
            }
        };

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
            action.call(true);
        } else {
            ((ActivityBridge) getActivity()).checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, action);
        }
    }

    private void launchTakePicture() {

        ActivityBridge.Callback<Boolean> action = granted -> {
            if (granted) {
                File imageFile = null;
                try {
                    imageFile = createImageFile();
                } catch (IOException e) {
                    Common.showAlert(getActivity(), Common.TECHNICAL_PROBLEM);
                    return;
                }

                // Open camera
                if (getActivity() instanceof ActivityBridge) {
                    Uri uri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", imageFile);
                    File pickedFile = imageFile;
                    ((ActivityBridge) getActivity()).takePicture(uri, result -> {
                        if (result) {
                            // successful -> use `pickedFile`
                            handleDocument(pickedFile, null);
                        }
                    });
                }
            }
        };

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
            action.call(true);
        } else {
            ((ActivityBridge) getActivity()).checkPermission(Manifest.permission.CAMERA, action);
        }
    }

    private void handleDocument(File file, Uri uri){
        if (file != null && file.exists()) {
            int file_size = Integer.parseInt(String.valueOf(file.length() / 1024));
            if (file_size < 10000) {
                uploadImage(file, uri);
            } else {
                Common.showAlert(getActivity(), "Datei zu groß. maximal 10MB erlaubt");
            }
        }
        else if(uri != null)
        {
            int file_size = Integer.parseInt(String.valueOf(Common.getFileSize(getContext().getContentResolver(), uri) / 1024));
            if (file_size < 10000) {
                uploadImage(file, uri);
            } else {
                Common.showAlert(getActivity(), "Datei zu groß. maximal 10MB erlaubt");
            }
        }
    }


    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }



    private void fillDetails(JSONObject jObj) throws Exception {

        etBezeichnung.setText(jObj.getString("filLabel"));
        etBeschreibung.setText(jObj.getString("filDescription"));
        etEingestellt.setText(jObj.getString("docChangedOn"));


        etWiedervorlage.setText(jObj.getString("docReminder"));
        etGültigAb.setText(jObj.getString("docValidFrom"));
        etGültigBis.setText(jObj.getString("docValidTo"));


//        postDataParams.put("binID", strBinID);

        cbGebrauchsanweisung.setChecked(jObj.getBoolean("docIsOperatingManual"));
        cbInstandhaltung.setChecked(jObj.getBoolean("docIsMaintenance"));
        cbVermietung.setChecked(jObj.getBoolean("docIsRentMgmt"));
        cbBetriebsKosten.setChecked(jObj.getBoolean("docIsCostMgmt"));

        String str = jObj.getString("binFK_TlkpContentTypes").trim();
        byte[] decodedString = Base64.decode((jObj.getString("binData").toString().getBytes()), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);


        if (str.contains("image/")) {
            ivDocumentImage.setImageBitmap(decodedByte);
            tvImageMessage.setText("");
//                ivDocumentImage.setImageBitmap(Common.Bytes2Bimap(jObj.getString("binData").toString().getBytes()));
        } else if (str.contains("rtf")) {
            ivDocumentImage.setImageResource(R.drawable.img_documents_rtf);
            tvImageMessage.setText("");
        } else if (str.contains("application/pdf")) {
            ivDocumentImage.setImageResource(R.drawable.img_documents_pdf);
            tvImageMessage.setText("");
        } else if (str.contains("ifc")) {
            ivDocumentImage.setImageResource(R.drawable.img_documents_ifc);
            tvImageMessage.setText("");
        } else if (str.contains("document")) {
            ivDocumentImage.setImageResource(R.drawable.img_documents_docx);
            tvImageMessage.setText("");
        } else if (str.contains("application/msword")) {
            ivDocumentImage.setImageResource(R.drawable.img_documents_doc);
            tvImageMessage.setText("");
        } else if (str.contains("application/ms-excel")) {
            ivDocumentImage.setImageResource(R.drawable.img_documents_excel);
            tvImageMessage.setText("");
        } else if (str.contains("application/vnd.ms-excel")) {
            ivDocumentImage.setImageResource(R.drawable.img_documents_excel);
            tvImageMessage.setText("");
        } else if (str.contains("sheet")) {
            ivDocumentImage.setImageResource(R.drawable.img_documents_excel);
            tvImageMessage.setText("");
        } else if (str.contains("application/ms-powerpoint")) {
            ivDocumentImage.setImageResource(R.drawable.img_documents_ppt);
            tvImageMessage.setText("");
        } else if (str.contains("presentation")) {
            ivDocumentImage.setImageResource(R.drawable.img_documents_ppt);
            tvImageMessage.setText("");
        } else if (str.contains("text")) {
            ivDocumentImage.setImageResource(R.drawable.img_documents_text);
            tvImageMessage.setText("");
        } else {
            ivDocumentImage.setImageResource(R.drawable.img_documents);
            tvImageMessage.setText("");
        }

        setFileTypeList(jObj.getString("binFK_TlkpContentTypes"));
        setArtTypeList(jObj.getString("docFK_TlkpDocTypes"));
        Log.v("", jObj.getString("docFK_TlkpDocTypeOptions"));
        StrUnterartType = jObj.getString("docFK_TlkpDocTypeOptions");
        Common.stopProgressDialouge("getLeistungArt");


    }


    private void setFileTypeList(String key) {
        for (int i = 0; i < docTypeList.size(); i++) {
            if (docTypeList.get(i).getKey().equals(key))
                spFileTyp.setSelection(i);

        }

    }

    private void setArtTypeList(String key) {
        for (int i = 0; i < ArtTypeList.size(); i++) {
            if (ArtTypeList.get(i).getKey().equals(key))
                spArt.setSelection(i);
            spFileTyp.setEnabled(false);

        }
        getUnterart();

    }

    private void setUnterartTypeList(String key) {
        for (int i = 0; i < UnterartTypeList.size(); i++) {
            if (UnterartTypeList.get(i).getKey().equals(key))
                spUnterart.setSelection(i);

        }

    }


    //************************************************** Override Method ************************************************

    @Override
    public void httpResponse(String response, String requestedFor) throws Exception {

        if (requestedFor.equals("getDocType")) {
            {
//                Common.stopProgressDialouge(requestedFor);
                JSONObject jObj = new JSONObject(response);
                if (jObj.getString("ResultCode").equals("SUCCESS")) {
                    JSONArray jArrayDocTypeList = jObj.getJSONArray("ResultObject");
                    fillDocContentTypeSpinner(getActivity(), jArrayDocTypeList, getResources().getString(R.string.type));
                } else {
                    Common.showAlert(getActivity(), jObj.getString("ResultMessage"));
                }
            }
        }

        if (requestedFor.equals("getArt")) {
            {
//                Common.stopProgressDialouge(requestedFor);
                JSONObject jArtObj = new JSONObject(response);
                if (jArtObj.getString("ResultCode").equals("SUCCESS")) {
                    JSONArray jArrayArtList = jArtObj.getJSONArray("ResultObject");
                    if (jArrayArtList.length() > 0) {
                        fillArtTypeSpinner(getActivity(), jArrayArtList, getResources().getString(R.string.typeArt));
                    }
                } else {
                    Common.showAlert(getActivity(), jArtObj.getString("ResultMessage"));
                }
            }
        }

        if (requestedFor.equals("UpdateDocument")) {
//            Common.stopProgressDialouge(requestedFor);
            JSONObject jObj = new JSONObject(response);
            if (jObj.getString("ResultCode").equals("SUCCESS")) {
                showAlert(jObj.getString("ResultMessage"));
            } else {
                Common.showAlert(getActivity(), jObj.getString("ResultMessage"));
            }
        }

        if (requestedFor.equals("getUnterart")) {
            {
//                Common.stopProgressDialouge(requestedFor);
                JSONObject jSubArtObj = new JSONObject(response);
                if (jSubArtObj.getString("ResultCode").equals("SUCCESS")) {
                    JSONArray jArrayArtList = jSubArtObj.getJSONArray("ResultObject");
                    fillUnterartType(getActivity(), jArrayArtList, getResources().getString(R.string.SubtypeArt));
                } else {
                    Common.showAlert(getActivity(), jSubArtObj.getString("ResultMessage"));
                }
            }
        }

        if (requestedFor.equals("UploadFile")) {
            Common.stopProgressDialouge(requestedFor);
            JSONObject jObj = new JSONObject(response);
            if (jObj.getString("ResultCode").equals("SUCCESS")) {


//                String strType = jObj.getString("ResultObject");
//                String[] separated = strType.split("#");
//                String str = separated[separated.length - 1];
//                Common.myLog(TAG, "str: " + str);
//                setArtTypeID(str);
//                tvImageMessage.setText("");


                String strTypes = jObj.getString("ResultObject");
                JSONObject jObjt = new JSONObject(strTypes);
                strTypes =     jObjt.getString("filePath");

                String[] separated = strTypes.split("#");
                String str = separated[separated.length - 1];
                Common.myLog(TAG, "str: " + str);

                setArtTypeID(str);
                tvImageMessage.setText("");
                byte[] decodedString = Base64.decode((jObjt.getString("fileData").toString().getBytes()), Base64.DEFAULT);
                Log.e("decodedString", decodedString + "");
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                Log.e("decodedByte", decodedByte + "");


                if (str.contains("image/")) {
//                    ivDocumentImage.setImageURI(selectedFileUri);
                    ivDocumentImage.setImageBitmap(decodedByte);
                } else if (str.contains("rtf")) {
                    ivDocumentImage.setImageResource(R.drawable.img_documents_rtf);
                } else if (str.contains("application/pdf")) {
                    ivDocumentImage.setImageResource(R.drawable.img_documents_pdf);
                } else if (str.contains("ifc")) {
                    ivDocumentImage.setImageResource(R.drawable.img_documents_ifc);
                } else if (str.contains("document")) {
                    ivDocumentImage.setImageResource(R.drawable.img_documents_docx);
                } else if (str.contains("application/msword")) {
                    ivDocumentImage.setImageResource(R.drawable.img_documents_doc);
                } else if (str.contains("application/ms-excel")) {
                    ivDocumentImage.setImageResource(R.drawable.img_documents_excel);
                } else if (str.contains("application/vnd.ms-excel")) {
                    ivDocumentImage.setImageResource(R.drawable.img_documents_excel);
                } else if (str.contains("sheet")) {
                    ivDocumentImage.setImageResource(R.drawable.img_documents_excel);
                } else if (str.contains("application/ms-powerpoint")) {
                    ivDocumentImage.setImageResource(R.drawable.img_documents_ppt);
                } else if (str.contains("presentation")) {
                    ivDocumentImage.setImageResource(R.drawable.img_documents_ppt);
                } else if (str.contains("text")) {
                    ivDocumentImage.setImageResource(R.drawable.img_documents_text);
                } else {
                    ivDocumentImage.setImageResource(R.drawable.img_documents);
                    tvImageMessage.setText("Unbekannter Dateityp");
                }

                String[] split = strTypes.split("&");
                String firstSubString = split[0];
                String[] BinIDtemp = firstSubString.split("ID=");
                strBinID = BinIDtemp[1];
                Common.myLog(TAG, "firstSubString: " + firstSubString);
                Common.myLog(TAG, "strBinID: " + strBinID);
                isDocUploaded = true;
            } else {
                Common.showAlert(getActivity(), jObj.getString("ResultMessage"));
            }
        }
    }

    @Override
    public void httpFailure(String response, String requestedFor) throws Exception {
    }

}


