package com.visafm.roombook.common;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.fragment.app.FragmentManager;

import android.provider.OpenableColumns;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.visafm.roombook.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@SuppressWarnings("deprecation")
@SuppressLint({"InflateParams", "SimpleDateFormat"})
public class Common {
    public final static String KEY_BASE_URL = "base_url";
    public final static String KEY_APPLICATION_ID = "application_id";
    public final static String KEY_USERNAME = "username";
    public final static String KEY_PASSWORD = "password";
    public final static String KEY_USER_SESSION = "user_session";

    private static Context context;
    public static String SERVER_URL;
    public static JSONObject selectedOrderDetail;
    public static JSONObject selectedServiceDetail;
    public static JSONObject selectedDocumentDetail;
    public static JSONObject editOrderDetail;
    public static FragmentManager mFragmentManager;
    public static String APPLICATIONID = ""; //"00000000-0000-0000-0000-000000000000";
    public static String USER_SESSION;
    public static String BARCODE_STRING = "";
    public static String SORT_COLUMN = "";
    public static String SORT_COLUMN_MENU;
    public static String SORT_TYPE;
    public static final String CHECK_INTERNET_CONNECTION = "Bitte überprüfen Sie Ihre Interneteinstellungen";
    public static String PERMISSION_DENIAL_MESSAGE = "Einige Berechtigungen fehlen, aktivieren Sie diese in den App-Einstellungen";
    public static String TECHNICAL_PROBLEM = "Es gibt ein technisches Problem.";
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 101;
    public static final int MY_PERMISSION_ACCESS_COURSE_LOCATION = 102;
    public static final String ERROR_PASSWORD_MISMATCH = "New Password and Confirm Password Mismatch";
    public static final String ERROR_EMPTY = "Feld darf nicht leer sein";
    public static String ACCESS_DENIED = "!! ACCESS DENIED !!";
    public static String SERVER_BUSY = "Internet Connection Too Slow Or The Server May Be Too Busy.";
    static Dialog loader;
    static SharedPreferences.Editor editor;
    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String CHARACTER_ONLY = "^[_A-Za-z]$";

    public static FragmentManager getmFragmentManager() {
        return mFragmentManager;
    }

    public static void setmFragmentManager(FragmentManager mFragmentManager) {
        Common.mFragmentManager = mFragmentManager;
    }

    public static void myLog(String tag, String message) {
        Log.d(tag, message);
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        Common.context = context;
    }

    public static void setSharedPreferences(Context context, String key, String data) {
        SharedPreferences prefs = context.getSharedPreferences("engross", Context.MODE_PRIVATE);
        editor = prefs.edit();
        editor.putString(key, data);
        editor.commit();
        Log.v(key, data);
    }

    public static String getSharedPreferences(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences("engross", Context.MODE_PRIVATE);
        String restoredText = prefs.getString(key, "NA");
        return restoredText;
    }

    public static boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        } //connectivity
        return false;
    }

    public static void showAlert(Context context, String Msg) {
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
            }
        });

        dialog.setContentView(v);
        dialog.show();
    }

    @SuppressWarnings("static-access")
    public static void startProgressDialouge(Context cont, String from) {
        try {
//             loader.dismiss();
            loader = new Dialog(cont, R.style.MyTheme);
            loader.setCanceledOnTouchOutside(false);
            loader.setCancelable(false);
            LayoutInflater Inflater = (LayoutInflater) cont.getSystemService(cont.LAYOUT_INFLATER_SERVICE);
            View v = Inflater.inflate(R.layout.progress_custom, null);
            loader.setContentView(v);
            loader.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopProgressDialouge() {
        try {
            loader.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static boolean isStringEmpty(String mString) {
        return mString.trim().equals("") || mString.trim() == null || mString.trim().equals("null");
    }



    public static Date addHoursToJavaUtilDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);

        return calendar.getTime();
    }


    public static Date addMinsToJavaUtilDate(Date date, int mins) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, mins);
        return calendar.getTime();
    }


    public static double calculateHoursDifference(String strStartDate, String strEndDate) {
        double doublecaltime = 0;
        try {
            if (!isStringEmpty(strStartDate) && !isStringEmpty(strEndDate)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                Calendar startDate = Calendar.getInstance();
                Calendar endDate = Calendar.getInstance();

                Log.e("strStartDate",strStartDate);
                Log.e("strEndDate",strEndDate);

                startDate.setTime(dateFormat.parse(strStartDate));
                endDate.setTime(dateFormat.parse(strEndDate));

                Log.e("endDate",endDate.getTimeInMillis()+"");
                Log.e("startDate",startDate.getTimeInMillis()+"");

                double diff = endDate.getTimeInMillis() - startDate.getTimeInMillis();


                Log.e("diff",diff+"");

                SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");
                SimpleDateFormat onlydateformat = new SimpleDateFormat("dd.MM.yyyy");
                try {


                    Date StartDate = format.parse(strStartDate);
                    Date EndDate = format.parse(strEndDate);
                    Log.e("StartDate",   StartDate+"");
                    Log.e("EndDate",   EndDate+"");

                    String[]  Startparts = strStartDate.split(" ");
                    Log.e("Startparts",   Startparts[0]+"");

                    String[]  Endparts = strEndDate.split(" ");
                    Log.e("Endparts",   Endparts[0]+"");

                    Date OnlyStartDate = onlydateformat.parse(Startparts[0]);
                    Date OnlyEndDate = onlydateformat.parse(Endparts[0]);




                    String  Endy = Endparts[1];
                    String[] Endyparts =  Endy.split(":");
                    int Intendh=Integer.parseInt(Endyparts[0]);
                    int Intendm=Integer.parseInt(Endyparts[1]);
                    Log.e("Intendh",   Intendh+"");
                    Log.e("addedHours enddate",   addHoursToJavaUtilDate(OnlyEndDate,Intendh)+"");  //Added hours to date outpu
                    String addedhourdateend  = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(addHoursToJavaUtilDate(OnlyEndDate,Intendh));
                    Log.e("addedhourdateend Str",   addedhourdateend+"");
                    Date dateaddedhourdateend = format.parse(addedhourdateend);
                    Log.e("addedhourdateend Date",   dateaddedhourdateend+"");

                    Log.e("mins output end ",   addMinsToJavaUtilDate(dateaddedhourdateend,Intendm)+"");



                    String  Starty = Startparts[1];
                    String[] Startyparts =  Starty.split(":");
                    int Intstarth=Integer.parseInt(Startyparts[0]);
                    int Intstartm=Integer.parseInt(Startyparts[1]);

                    String addedhourdatestart  = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(addHoursToJavaUtilDate(OnlyStartDate,Intstarth));
                    Log.e("addedhourdatestart Str",   addedhourdatestart+"");
                    Date dateaddedhourdatestart = format.parse(addedhourdatestart);
                    Log.e("addedhourdatestart Date",   addedhourdatestart+"");
                    Log.e("mins output start ",   addMinsToJavaUtilDate(dateaddedhourdatestart,Intstartm)+"");


                    long bhavdiffss = addMinsToJavaUtilDate(dateaddedhourdateend,Intendm).getTime() - addMinsToJavaUtilDate(dateaddedhourdatestart,Intstartm).getTime();

                    Log.e("Mins end",   addMinsToJavaUtilDate(dateaddedhourdateend,Intendm).getTime()+"");
                    Log.e("Mins Start",   addMinsToJavaUtilDate(dateaddedhourdatestart,Intstartm).getTime()+"");

                    long bhavseconds = bhavdiffss / 1000;
                    long bhavminutes = bhavseconds / 60;


                    long bhavhours = bhavminutes / 60;

                    long dMinute = bhavminutes % 60;

                    Log.e("dMinute",   dMinute+"");
                    long bhavdays = bhavhours / 24;
                    String Strtemp;
                    if(dMinute<10){
                         Strtemp = bhavhours +"." +"0"+dMinute + "";

                    }else{
                         Strtemp = bhavhours +"."+dMinute + "";

                    }


                    doublecaltime  = Double.parseDouble(Strtemp);

                    Log.e("doublecaltime",doublecaltime+"");

                    Log.e("bhavdiffss",bhavdiffss+"");
                    Log.e("bhavseconds",bhavseconds+"");
                    Log.e("bhavminutes",bhavminutes+"");
                    Log.e("bhavhours",bhavhours+"");
                    Log.e("bhavdays",bhavdays+"");

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doublecaltime;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static boolean isValidInput(EditText etEditText, String type) {
        String target = etEditText.getText().toString().trim();
        Pattern pattern = Pattern.compile(type);
        switch (type) {
            case EMAIL_PATTERN:
                if (!pattern.matcher(target).matches()) {
                    etEditText.setError("Please enter email address");
                    return false;
                } else
                    return true;

            case CHARACTER_ONLY:
                if (!pattern.matcher(target).matches()) {
                    etEditText.setError("Please use characters only");
                    return false;
                } else
                    return true;

            default:
                return false;
        }
    }

    public static boolean validationWithMessage(EditText etEditText, int length, String message) {
        boolean isValid = true;
        String value = etEditText.getText().toString().trim();

        if (value.equals("")) {
            isValid = false;
            etEditText.setError(message + " is required.");
        }

        if (length > 0 && isValid)
            if (etEditText.length() < length) {
                isValid = false;
                etEditText.setError("Minimum " + length + " Character required");
            }

        return isValid;
    }

    public static void allowCharacterSpaceOnly(EditText et) {
        et.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        if (source.equals("")) // for backspace
                            return source;
                        if (source.toString().matches("[a-zA-Z ]+"))
                            return source;
                        return "";
                    }
                }
        });
    }

    public static boolean isValidEdittext(EditText etEditText, int length) {
        boolean isValid = true;
        String value = etEditText.getText().toString().trim();

        if (value.equals("")) {
            isValid = false;
            etEditText.setError(ERROR_EMPTY);
        }

        if (length > 0 && !isValid)
            if (etEditText.length() < length) {
                isValid = false;
                etEditText.setError("Minimum " + length + " Character required");
            }
        return isValid;
    }


    public static boolean isValidEdittextatv(EditText etEditText, int length) {
        boolean isValid = true;
        String value = etEditText.getText().toString().trim();

        if (value.equals("")) {
            isValid = false;
//            etEditText.setError(ERROR_EMPTY);
        }

        if (length > 0 && !isValid)
            if (etEditText.length() < length) {
                isValid = false;
                etEditText.setError("Minimum " + length + " Character required");
            }
        return isValid;
    }


    public static String dateFormate(String str, String input, String output) {
        String dateString = "";
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(input); //dd-MMM-yyyy  hh:mm a
            Date convertedDate = dateFormat.parse(str);
            dateString = new SimpleDateFormat(output).format(convertedDate); //"dd MMMM yyyy"
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dateString;
    }

    public static String dateFormates(String str) {
        String formattedTime = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat output = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            Date d = sdf.parse(str);
            formattedTime = output.format(d);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return formattedTime;
    }


    public static String convertCommaToDot(String value) {
        return value.replace(",", ".");
    }

    public static String convertDotToComma(String value) {
        return value.replace(".", ",");
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, WindowManager.LayoutParams.WRAP_CONTENT));
            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static String convertToAmPm(String time) throws ParseException {
        DateFormat f1 = new SimpleDateFormat("HH:mm:ss"); //HH for hour of the day (0 - 23)
        Date d = f1.parse(time);
        DateFormat f2 = new SimpleDateFormat("hh:mm aa");
        return f2.format(d);
    }

    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static JSONArray removeJsonObjectAtJsonArrayIndex(JSONArray source, int index) throws JSONException {
        if (index < 0 || index > source.length() - 1) {
            throw new IndexOutOfBoundsException();
        }
        final JSONArray copy = new JSONArray();
        for (int i = 0, count = source.length(); i < count; i++) {
            if (i != index) copy.put(source.get(i));
        }
        return copy;
    }

    public static String convertArrayToJsonString(List<TextView> listData) {
        String jsonString = "[";
        TextView tv;
        for (int i = 0; i < listData.size(); i++) {
            tv = listData.get(i);
            jsonString = jsonString + "\"" + tv.getText().toString().trim() + "\"";
            if (i < listData.size() - 1)
                jsonString = jsonString + ",";
        }
        jsonString = jsonString + "]";
        return jsonString;
    }

    public static boolean isEven(int n) {
        return n % 2 == 0;
    }

    public static File convertBitmapToFile(Bitmap bmp) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;
        File file = new File(extStorageDirectory, "profile_image.png");
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, "profile_image.png");
        }
        try {
            outStream = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return file;
    }

    public static JSONArray sortJsonArray(JSONArray array) throws JSONException {
        List<JSONObject> jsons = new ArrayList<JSONObject>();
        for (int i = 0; i < array.length(); i++)
            jsons.add(array.getJSONObject(i));

        Collections.sort(jsons, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject lhs, JSONObject rhs) {
                String lid = null, rid = null;
                try {
                    lid = lhs.getString("comment_id");
                    rid = rhs.getString("comment_id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return lid.compareTo(rid);
            }
        });
        return new JSONArray(jsons);
    }

    public static void hideShowAddressFields(EditText et) {
        if (et.getText().toString().trim().equals(""))
            et.setVisibility(View.GONE);
        else
            et.setVisibility(View.VISIBLE);
    }

    public static double numberOfDigitsAfterDecimal(double Rval, int numberOfDigitsAfterDecimal) {
        double p = (float) Math.pow(10, numberOfDigitsAfterDecimal);
        Rval = Rval * p;
        double tmp = Math.floor(Rval);
        return tmp / p;
    }

    private final static LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }
    };

    public static int getDayOfWeek(String mDay) {
        int i = 0;
        switch (mDay) {
            case "Sunday":
                i = 1;
                break;

            case "Monday":
                i = 2;
                break;

            case "Tuesday":
                i = 3;
                break;

            case "Wednesday":
                i = 4;
                break;

            case "Thursday":
                i = 5;
                break;

            case "Friday":
                i = 6;
                break;

            case "Saturday":
                i = 7;
                break;
        }

        return i;
    }

    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }

    public static String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    public static void downloadFile(Context mContext, String mCatalogueName, String mUri, String mName) {
        File direct = new File(Environment.getExternalStorageDirectory() + "/Lajo" + "/" + mCatalogueName);
        if (!direct.exists()) {
            direct.mkdirs();
        } else {
            String[] children = direct.list();
            for (int i = 0; i < children.length; i++) {
                new File(direct, children[i]).delete();
            }
        }
        DownloadManager mgr = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        Uri downloadUri = Uri.parse(mUri);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(mName + ".jpg")
                .setDescription("Downloading " + mCatalogueName + " images")
                .setVisibleInDownloadsUi(true)
                .setDestinationInExternalPublicDir("/Lajo/" + mCatalogueName, mName + ".jpg");
        mgr.enqueue(request);
    }

    @SuppressWarnings("ResourceType")
    public static void openStatusBar(Context mContext) {
        try {
            Object sbservice = mContext.getSystemService("statusbar");
            Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
            Method showsb;
            if (Build.VERSION.SDK_INT >= 17) {
                showsb = statusbarManager.getMethod("expandNotificationsPanel");
            } else {
                showsb = statusbarManager.getMethod("expand");
            }
            showsb.invoke(sbservice);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getStringSizeLengthFile(long size) {

        DecimalFormat df = new DecimalFormat("0.00");

        float sizeKb = 1024.0f;
        float sizeMo = sizeKb * sizeKb;
        float sizeGo = sizeMo * sizeKb;
        float sizeTerra = sizeGo * sizeKb;


        if (size < sizeMo)
            return df.format(size / sizeKb) + " Kb";
        else if (size < sizeGo)
            return df.format(size / sizeMo) + " Mb";
        else if (size < sizeTerra)
            return df.format(size / sizeGo) + " Gb";

        return "";
    }

    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length == 0) {
            return null;
        }
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    public static long getFileSize(ContentResolver contentResolver, Uri uri){
        Cursor returnCursor = contentResolver.query(uri, null, null, null, null);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();

        return returnCursor.getLong(sizeIndex);
    }

    public static String getFileName(ContentResolver contentResolver, Uri uri){
        Cursor returnCursor = contentResolver.query(uri, null, null, null, null);
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();

        return returnCursor.getString(nameIndex);
    }
}
