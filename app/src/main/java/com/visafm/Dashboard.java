package com.visafm;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.visafm.adapter.DocListAdap;
import com.visafm.adapter.MenuAdapter;
import com.visafm.adapter.OrderListAdap;
import com.visafm.adapter.ServiceListAdap;
import com.visafm.common.BaseClass;
import com.visafm.common.BaseClassD;
import com.visafm.common.Common;
import com.visafm.common.DrawerArrowDrawable;
import com.visafm.common.HttpConnection;
import com.visafm.common.HttpConnectionD;
import com.visafm.fragments.FrgAddService;
import com.visafm.fragments.FrgBarcodeScan;
import com.visafm.fragments.FrgBarcodeSearch;
import com.visafm.fragments.FrgBarcodeSearchList;
import com.visafm.fragments.FrgDocList;
import com.visafm.fragments.FrgDocumentDetail;
import com.visafm.fragments.FrgEditOrderDocuments;
import com.visafm.fragments.FrgEditService;
import com.visafm.fragments.FrgNewOrderEdit;
import com.visafm.fragments.FrgOrderAdd;
import com.visafm.fragments.FrgOrderDetail;
import com.visafm.fragments.FrgOrderDocuments;
import com.visafm.fragments.FrgOrderEdit;
import com.visafm.fragments.FrgOrderList;
import com.visafm.fragments.FrgSearchList;
import com.visafm.fragments.FrgSearchOrder;
import com.visafm.fragments.FrgServiceDetail;
import com.visafm.fragments.FrgServiceList;
import com.visafm.fragments.FrgSortList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import timber.log.Timber;

@SuppressLint({"StaticFieldLeak", "NonConstantResourceId"})
public class Dashboard extends AppCompatActivity implements View.OnClickListener, BaseClass, BaseClassD, ActivityBridge {
    private DrawerArrowDrawable drawerArrowDrawable;
    private float offset;
    private boolean flipped;
    private static DrawerLayout drawer;

    public static ImageView ivDrawerIndicator;
    public static ImageView ivBack;
    JSONArray jArrayServiceList = new JSONArray();
    JSONArray jArrayOrderList = new JSONArray();
    JSONArray jArrayDocList = new JSONArray();
    Resources resources;
    private static LinearLayout linearLayoutDrawerMenu;
    FrameLayout flDrawer;
    private static FragmentManager frgManager;
    public static ImageView ivEdit;
    public static ImageView ivSortUp;
    public static ImageView ivSortDown;
    public static ImageView ivSortArrows;
    public static ImageView ivDocument;
    public static ImageView ivDocumnetEdit;
    public static ImageView ivService;
    public static ImageView ivServiceEdit;
    public static TextView tvTitle;
    ExpandableListAdapter listAdapter;
    ExpandableListView elvMenu;
    JSONArray jArraySortMenuList = new JSONArray();
    ListView lvOrder;
    TextView tvNoRecords;
    BaseClass delegate = this;
    BaseClassD delegates = this;

    Callback<Boolean> permissionCallback = null;
    Callback<Boolean> takePictureCallback = null;
    Callback<Uri> pickDocumentCallback = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        getSupportActionBar().hide();
        resources = getResources();

        drawer = findViewById(R.id.drawer_layout);
        ivEdit = findViewById(R.id.ivEdit);
        ivSortUp = findViewById(R.id.ivSortUp);
        ivSortDown = findViewById(R.id.ivSortDown);
        ivSortArrows = findViewById(R.id.ivSortArrows);
        ivDocument = findViewById(R.id.ivDocument);
        ivDocumnetEdit = findViewById(R.id.ivDocumnetEdit);

        ivService = findViewById(R.id.ivService);
        ivServiceEdit = findViewById(R.id.ivServiceEdit);
        ivBack = findViewById(R.id.ivBack);
        tvTitle = findViewById(R.id.tvTitle);
        ivDrawerIndicator = findViewById(R.id.ivDrawerIndicator);
        linearLayoutDrawerMenu = findViewById(R.id.linearDrawerMenu);
        flDrawer = findViewById(R.id.flDrawer);
        elvMenu = findViewById(R.id.elvMenu);


        Common.setmFragmentManager(getSupportFragmentManager());
        drawerArrowDrawable = new DrawerArrowDrawable(resources);
        drawerArrowDrawable.setStrokeColor(resources.getColor(R.color.light_gray));
        frgManager = getSupportFragmentManager();
        lvOrder = findViewById(R.id.lvOrder);
        tvNoRecords = findViewById(R.id.tvNoRecords);

        ivDrawerIndicator.setImageDrawable(drawerArrowDrawable);
        ivDrawerIndicator.setOnClickListener(this);
        ivEdit.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        ivSortUp.setOnClickListener(this);
        ivSortArrows.setOnClickListener(this);
        ivSortDown.setOnClickListener(this);
        ivDocument.setOnClickListener(this);
        ivDocumnetEdit.setOnClickListener(this);

        ivService.setOnClickListener(this);
        ivServiceEdit.setOnClickListener(this);

        drawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                offset = slideOffset;
                // Sometimes slideOffset ends up so close to but not quite 1 or 0.
                if (slideOffset >= .995) {
                    flipped = true;
                    drawerArrowDrawable.setFlip(flipped);
                } else if (slideOffset <= .005) {
                    flipped = false;
                    drawerArrowDrawable.setFlip(flipped);
                }
                drawerArrowDrawable.setParameter(offset);
            }
        });

        populateMenu();
        displayFragment(0); //Open Homepage
    }
    /*
     * Preparing the list data
     */

    private void populateMenu() {
        List<String> listDataHeader;
        HashMap<String, List<String>> listDataChild;
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<>();

        listDataHeader.add("Home");  //0

        listDataHeader.add("Auftragsliste");  //2  //My orders // make 1

        listDataHeader.add("Aufträge sortieren"); //4  Sort orders  make 2

        listDataHeader.add("Auftrag suchen"); //3  Search order // make 3

        listDataHeader.add("Auftrag mit Barcode suchen "); //6  Search order with barcode // make 4

        listDataHeader.add("Auftrag hinzufügen"); // 1  Add orders make 5

        listDataHeader.add("Auftrag mit Barcode hinzufügen"); //5  Create order with barcode //make 6

        listDataHeader.add("Anmelden"); //7  register

        listDataHeader.add("Beenden"); //8   break up


//        Home
//        Auftragsliste   // Order list
//        Aufträge sortieren   //Sort orders

//        Auftrag suchen    //Search order

//        Auftrag mit Barcode suchen  //Search order with barcode

//        Auftrag hinzufügen  //Add order

//        Auftrag mit Barcode hinzufügen  //Add order with barcode

//        Anmelden   //register
//        Beenden    //break up


        List<String> sortieren = new ArrayList<String>();
        sortieren.add("Nach Auftrag Nr.");
        sortieren.add("Nach ERP Nr.");
        sortieren.add("Nach Objekt");
        sortieren.add("Nach Fälligkeit");
        sortieren.add("Nach Plantermin");

        List<String> emptyList = new ArrayList<>();
        listDataChild.put(listDataHeader.get(0), emptyList);
        listDataChild.put(listDataHeader.get(1), emptyList);
        listDataChild.put(listDataHeader.get(2), sortieren);
        listDataChild.put(listDataHeader.get(3), emptyList);
        listDataChild.put(listDataHeader.get(4), emptyList);
        listDataChild.put(listDataHeader.get(5), emptyList);
        listDataChild.put(listDataHeader.get(6), emptyList);
        listDataChild.put(listDataHeader.get(6), emptyList);
        listDataChild.put(listDataHeader.get(7), emptyList);
        listDataChild.put(listDataHeader.get(8), emptyList);
        // On Group click listener
        elvMenu.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (groupPosition == 0) {
                    displayFragment(0);
                    return true;
                }
                if (groupPosition == 4) {
                    displayFragment(6); //changed
                    return true;
                } else if (groupPosition == 1) {
                    displayFragment(0); //Changed
                    return true;
                } else if (groupPosition == 3) {
                    displayFragment(2);
                    return true;
                } else if (groupPosition == 5) {
                    displayFragment(56);//changed
                    return true;
                } else if (groupPosition == 6) {
                    displayFragment(5);//changed
                    return true;
                } else if (groupPosition == 7) {
                    getLogout();
                    return true;
                } else if (groupPosition == 8) {
                    showExitDialog();
                    return true;
                } else {
                    return false;
                }
            }
        });

        listAdapter = new MenuAdapter(this, listDataHeader, listDataChild);
        elvMenu.setAdapter(listAdapter);

        elvMenu.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                if (childPosition == 0) {
                    Common.SORT_COLUMN = "AuftragNr";
                    displayFragment(4);
                    return true;
                } else if (childPosition == 1) {
                    Common.SORT_COLUMN = "ERP-Nr";
                    displayFragment(4);
                    return true;
                } else if (childPosition == 2) {
                    Common.SORT_COLUMN = "Objekt";
                    displayFragment(4);
                    return true;
                } else if (childPosition == 3) {
                    Common.SORT_COLUMN = "Fälligkeit";
                    displayFragment(4);
                    return true;
                } else if (childPosition == 4) {
                    Common.SORT_COLUMN = "Plantermin";
                    displayFragment(4);
                    return true;
                } else
                    return false;
            }
        });
    }

    // onCreate
    @Override
    public void onBackPressed() {
//        Fragment fragment = frgManager.findFragmentByTag("NewOrderEdit");
//        if (fragment != null && fragment.isVisible())
//        return;

        if (!drawer.isDrawerOpen(linearLayoutDrawerMenu))
            if (Common.getmFragmentManager().getBackStackEntryCount() == 0)
                showExitDialog();
            else {
                ivSortArrows.setVisibility(View.GONE);
                ivSortDown.setVisibility(View.GONE);
                ivSortUp.setVisibility(View.GONE);
                ivEdit.setVisibility(View.GONE);
                ivDocument.setVisibility(View.GONE);
                ivService.setVisibility(View.GONE);
                ivDocumnetEdit.setVisibility(View.GONE);

                super.onBackPressed();
            }
        else
            drawer.closeDrawer(linearLayoutDrawerMenu);
    }

    private void showExitDialog() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.logo)
                .setTitle("VisaFM")
                .setMessage("Sind Sie sicher, dass Sie diese App beenden möchten?")
                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("Nein", null)
                .show();
    }

    public static void OptionsServices(Context c, View v, JSONObject jObj) {
        final Context context = c;
        final JSONObject jObjService = jObj;

        PopupMenu popup = new PopupMenu(c, v);
        popup.getMenuInflater().inflate(R.menu.clipboard_popup,
                popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:
                        Common.selectedServiceDetail = jObjService;
                        Common.setSharedPreferences(context, "EditServicesFromList", "True");
                        Dashboard.displayFragment(59);
                        break;
                    case R.id.delete:
                        new AlertDialog.Builder(context)
                                .setIcon(R.drawable.logo)
                                .setTitle("VisaFM")
                                .setMessage("Möchten Sie diese Leistung wirklich löschen?")
                                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Common.selectedServiceDetail = jObjService;
                                        Dashboard d = new Dashboard();
                                        d.deleteServiceList(context);
                                    }

                                })
                                .setNegativeButton("Nein", null)
                                .show();

                        break;
                    default:
                        break;
                }

                return true;
            }
        });

    }


    public static void documentOptions(Context c, View v, JSONObject jObj) {
        final Context context = c;
        final JSONObject jObjdocument = jObj;

        PopupMenu popup = new PopupMenu(c, v);
        popup.getMenuInflater().inflate(R.menu.clipboard_popup,
                popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:
                        Common.selectedDocumentDetail = jObjdocument;
                        Common.setSharedPreferences(context, "EditDocumentsFromList", "True");
                        Dashboard.displayFragment(60);
                        break;
                    case R.id.delete:
                        new AlertDialog.Builder(context)
                                .setIcon(R.drawable.logo)
                                .setTitle("VisaFM")
                                .setMessage("Möchten Sie dieses Dokument wirklich löschen?")
                                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Common.selectedDocumentDetail = jObjdocument;
                                        Dashboard d = new Dashboard();
                                        d.deleteDocumentList(context);
                                    }
                                })
                                .setNegativeButton("Nein", null)
                                .show();

                        break;
                    default:
                        break;
                }

                return true;
            }
        });

    }


    public static void optionsOrder(Context c, View v, JSONObject jObj) {
        final Context context = c;
        final JSONObject jObjs = jObj;

        PopupMenu popup = new PopupMenu(c, v);
        popup.getMenuInflater().inflate(R.menu.clipboard_popup,
                popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.edit:
                        Common.selectedOrderDetail = jObjs;
                        Log.e("dash dit sel order", Common.selectedOrderDetail.toString());
                        Common.setSharedPreferences(context, "EditOrdersFromList", "True");
                        Dashboard.displayFragment(50);
                        break;
                    case R.id.delete:
                        new AlertDialog.Builder(context)
                                .setIcon(R.drawable.logo)
                                .setTitle("VisaFM")
                                .setMessage("Möchten Sie diesen Auftrag wirklich löschen?")
                                .setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Common.selectedOrderDetail = jObjs;
                                        Dashboard d = new Dashboard();
                                        d.deleteOrderList(context);
                                    }
                                })
                                .setNegativeButton("Nein", null)
                                .show();

                        break;
                    default:
                        break;
                }

                return true;
            }
        });

    }


    public void deleteOrderList(Context context) {
        try {
            HashMap<String, String> postDataParams = new HashMap<>();
            HttpConnectionD httpConnectiond = new HttpConnectionD(delegates, context);
            postDataParams.put("OrderID", Common.selectedOrderDetail.getString("OrderNr"));
            httpConnectiond.setRequestedfors("deleteOrder", context);
            httpConnectiond.setIsloading(true);
            httpConnectiond.setPostDataParams(postDataParams);
            httpConnectiond.setUrl("Orders/DeleteOrder");
            httpConnectiond.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteServiceList(Context context) {
        try {
            HashMap<String, String> postDataParams = new HashMap<>();
            HttpConnectionD httpConnectiond = new HttpConnectionD(delegates, context);
            postDataParams.put("OrderDetailsID", Common.selectedServiceDetail.getString("WDetailID"));
            httpConnectiond.setRequestedfors("deleteOrderDetailsByID", context);
            httpConnectiond.setIsloading(true);
            httpConnectiond.setPostDataParams(postDataParams);
            httpConnectiond.setUrl("Orders/DeleteOrderDetailsByID");
            httpConnectiond.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteDocumentList(Context context) {
        try {
            HashMap<String, String> postDataParams = new HashMap<>();
            HttpConnectionD httpConnectiond = new HttpConnectionD(delegates, context);
            postDataParams.put("original_docID", Common.selectedDocumentDetail.getString("docID"));
            postDataParams.put("userID", Common.USER_SESSION);
            if (Common.isStringEmpty(Common.APPLICATIONID)) {
                postDataParams.put("applicationID", "");
            } else {
                postDataParams.put("applicationID", Common.APPLICATIONID);
            }
            httpConnectiond.setRequestedfors("DeleteDocument", context);
            httpConnectiond.setIsloading(true);
            httpConnectiond.setPostDataParams(postDataParams);
            httpConnectiond.setUrl("Orders/DeleteDocument");
            httpConnectiond.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void displayFragment(int position) {
        Fragment fragment = null;
        String fragment_name = "";

        switch (position) {
            case 0:
                fragment_name = "home";
//                fragment = frgManager.findFragmentByTag(fragment_name);
//                if (fragment == null)
                fragment = new FrgOrderList();
                break;

            case 1:
                fragment_name = "OrderDetail";
                fragment = frgManager.findFragmentByTag(fragment_name);
                if (fragment == null)
                    fragment = new FrgOrderDetail();
                break;

            case 2:
                fragment_name = "SearchOrder";
                fragment = frgManager.findFragmentByTag(fragment_name);
                if (fragment == null)
                    fragment = new FrgSearchOrder();
                break;

            case 3:
                fragment_name = "SearchList";
                fragment = frgManager.findFragmentByTag(fragment_name);
                if (fragment == null)
                    fragment = new FrgSearchList();
                break;

            case 4:
                fragment_name = "SortAuftragNr";
                fragment = frgManager.findFragmentByTag(fragment_name);
                if (fragment == null)
                    fragment = new FrgSortList();

                break;
            case 5:
                fragment_name = "QRCodeScannen";
                fragment = frgManager.findFragmentByTag(fragment_name);
                if (fragment == null)
                    fragment = new FrgBarcodeScan();
                break;
            case 6:
                fragment_name = "QRCodeSuchauftrag";
                fragment = frgManager.findFragmentByTag(fragment_name);
                if (fragment == null)
                    fragment = new FrgBarcodeSearch();
                break;
            case 7:
                fragment_name = "BarcodeSearchList";
                fragment = frgManager.findFragmentByTag(fragment_name);
                if (fragment == null)
                    fragment = new FrgBarcodeSearchList();
                break;

            case 50:
                fragment_name = "OrderEdit";
                fragment = frgManager.findFragmentByTag(fragment_name);
                if (fragment == null)
                    fragment = new FrgOrderEdit();
                break;

            case 51:
                fragment_name = "OrderDocument";
                fragment = frgManager.findFragmentByTag(fragment_name);
                if (fragment == null)
                    fragment = new FrgOrderDocuments();

                break;
            case 52:
                fragment_name = "NewOrderEdit";
                fragment = frgManager.findFragmentByTag(fragment_name);
                if (fragment == null)
                    fragment = new FrgNewOrderEdit();
                break;

            case 53:
                fragment_name = "DocumentList";
                fragment = frgManager.findFragmentByTag(fragment_name);
                if (fragment == null)
                    break;

            case 58:
                fragment_name = "DocList";
                fragment = frgManager.findFragmentByTag(fragment_name);
                if (fragment == null)
                    fragment = new FrgDocList();
                break;

            case 54:
                fragment_name = "ServiceList";
                fragment = frgManager.findFragmentByTag(fragment_name);
                if (fragment == null)
                    fragment = new FrgServiceList();
                break;
            case 55:
                fragment_name = "AddService";
                fragment = frgManager.findFragmentByTag(fragment_name);
                if (fragment == null)
                    fragment = new FrgAddService();

                break;

            case 56:
                fragment_name = "AddOrder";
                fragment = frgManager.findFragmentByTag(fragment_name);
                if (fragment == null)
                    fragment = new FrgOrderAdd();
                break;
            case 57:
                fragment_name = "ServiceDetail";
                fragment = frgManager.findFragmentByTag(fragment_name);
                if (fragment == null)
                    fragment = new FrgServiceDetail();
                break;

            case 59:
                fragment_name = "EditService";
                fragment = frgManager.findFragmentByTag(fragment_name);
                if (fragment == null)
                    fragment = new FrgEditService();

                break;
            case 60:
                fragment_name = "EditDocument";
                fragment = frgManager.findFragmentByTag(fragment_name);
                if (fragment == null)
                    fragment = new FrgEditOrderDocuments();
                break;

            case 61:
                fragment_name = "DocumentDetail";
                fragment = frgManager.findFragmentByTag(fragment_name);
                if (fragment == null)
                    fragment = new FrgDocumentDetail();
                break;

            default:
                break;
        }

        if (fragment != null) {
            Fragment myFragment = Common.getmFragmentManager().findFragmentByTag(fragment_name);
            boolean sameFragment = false;
            if (myFragment != null) {
                if (myFragment.isVisible())
                    sameFragment = true;
                drawer.closeDrawer(linearLayoutDrawerMenu);
                if (fragment_name.equals("SortAuftragNr")) {
                    FrgSortList.getSortList();
                }
            }

            if (!sameFragment) {
                boolean fragmentPopped = Common.getmFragmentManager().popBackStackImmediate(fragment_name, 0);
                if (!fragmentPopped) {
                    drawer.closeDrawer(linearLayoutDrawerMenu);
                    FragmentTransaction ft;
                    ft = Common.getmFragmentManager().beginTransaction();
                    if (fragment_name.equals("home"))
                        Common.getmFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    else {
                        int count = Common.getmFragmentManager().getBackStackEntryCount();
                        ft.addToBackStack(String.valueOf(count));
                    }
                    ft.setCustomAnimations(R.anim.fragment_slide_in_left, R.anim.fragment_slide_out_right, 0, 0);
                    Timber.d("CHANGE FRAGMENT: %s", fragment_name);
                    ft.replace(R.id.flDrawer, fragment, fragment_name);
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                    ft.commit();
                }
            } else {
                drawer.closeDrawer(linearLayoutDrawerMenu);
            }
        } else {
            drawer.closeDrawer(linearLayoutDrawerMenu);
        }
    }

    private void getLogout() {
        Common.setSharedPreferences(getApplicationContext(), "userSession", "NA");
        Common.setSharedPreferences(getApplicationContext(), "serverUrl", "NA");
        Intent i = new Intent(Dashboard.this, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivDrawerIndicator:
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
                break;

            case R.id.ivEdit:
                displayFragment(50);
                break;
            case R.id.ivSortDown:
                ivSortDown.setVisibility(View.GONE);
                ivSortUp.setVisibility(View.VISIBLE);
                break;
            case R.id.ivSortUp:
                ivSortUp.setVisibility(View.GONE);
                ivSortDown.setVisibility(View.VISIBLE);
                break;
            case R.id.ivDocument:
                displayFragment(58);
                break;
            case R.id.ivDocumnetEdit:
                displayFragment(60);
                break;

            case R.id.ivService:
                displayFragment(54);
                break;

            case R.id.ivServiceEdit:
                displayFragment(59);
                break;
            case R.id.ivBack:
                onBackPressed();
                break;


            case R.id.ivSortArrows:
                PopupMenu popup = new PopupMenu(this, ivSortArrows);
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.itemAuftragNr:
                                Common.SORT_COLUMN_MENU = "AuftragNr";
                                Common.SORT_TYPE = "desc";
                                getSortMenuList();
                                break;
                            case R.id.itemAuftragNrAsc:
                                Common.SORT_COLUMN_MENU = "AuftragNr";
                                Common.SORT_TYPE = "asc";
                                getSortMenuList();
                                break;
                            case R.id.itemERPNr:
                                Common.SORT_COLUMN_MENU = "ERP-Nr";
                                Common.SORT_TYPE = "desc";
                                getSortMenuList();
                                break;
                            case R.id.itemERPNrAsc:
                                Common.SORT_COLUMN_MENU = "ERP-Nr";
                                Common.SORT_TYPE = "asc";
                                getSortMenuList();
                                break;
                            case R.id.itemObjekt:
                                Common.SORT_COLUMN_MENU = "Objekt";
                                Common.SORT_TYPE = "desc";
                                getSortMenuList();
                                break;
                            case R.id.itemObjektAsc:
                                Common.SORT_COLUMN_MENU = "Objekt";
                                Common.SORT_TYPE = "asc";
                                getSortMenuList();
                                break;
                            case R.id.itemFälligkeit:
                                Common.SORT_COLUMN_MENU = "Fälligkeit";
                                Common.SORT_TYPE = "desc";
                                getSortMenuList();
                                break;
                            case R.id.itemFälligkeitAsc:
                                Common.SORT_COLUMN_MENU = "Fälligkeit";
                                Common.SORT_TYPE = "asc";
                                getSortMenuList();
                                break;
                            case R.id.itemPlantermin:
                                Common.SORT_COLUMN_MENU = "Plantermin";
                                Common.SORT_TYPE = "desc";
                                getSortMenuList();
                                break;
                            case R.id.itemPlanterminAsc:
                                Common.SORT_COLUMN_MENU = "Plantermin";
                                Common.SORT_TYPE = "asc";
                                getSortMenuList();
                                break;
                        }
                        return true;
                    }
                });
                popup.show();

                break;
        }

    }


    private void getSortMenuList() {
        try {
            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("userid", Common.USER_SESSION);
            postDataParams.put("sorttype", Common.SORT_TYPE);
            postDataParams.put("auftragnr", Common.getSharedPreferences(Dashboard.this, "auftragnr"));
            postDataParams.put("erpnr", Common.getSharedPreferences(Dashboard.this, "erpnr"));
            postDataParams.put("objekt", Common.getSharedPreferences(Dashboard.this, "objekt"));
            postDataParams.put("fälligkeit", Common.getSharedPreferences(Dashboard.this, "fälligkeit"));
            postDataParams.put("plantermin", Common.getSharedPreferences(Dashboard.this, "plantermin"));
            postDataParams.put("sortcolumn", Common.SORT_COLUMN_MENU);
            Log.i("Dashboard", "post Data Params log " + postDataParams);
            HttpConnection httpConnection = new HttpConnection(delegate, Dashboard.this);
            httpConnection.setRequestedfor("getSortMenuList");
            httpConnection.setIsloading(true);
            httpConnection.setPostDataParams(postDataParams);
            httpConnection.setUrl("Orders/GetAssignOrdersWithSearchAndSort");
            httpConnection.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void httpResponse(String response, String requestedFor) throws Exception {
        try {
            Common.stopProgressDialouge(requestedFor);
            if (requestedFor.equals("getSortMenuList")) {
                JSONObject jObj = new JSONObject(response);
                if (jObj.getString("ResultCode").equals("SUCCESS")) {
                    jArraySortMenuList = jObj.getJSONArray("ResultObject");
                    if (jArraySortMenuList.length() > 0) {
//                        tvNoRecords.setVisibility(View.GONE);
                        OrderListAdap adap = new OrderListAdap(Dashboard.this, jArraySortMenuList);
                        FrgSearchList.lvOrder.setAdapter(adap);
                    } else {
                        tvNoRecords.setVisibility(View.VISIBLE);
//                        OrderListAdap adap = new OrderListAdap(Dashboard.this, jArraySortMenuList);
//                        FrgSearchList.lvOrder.setAdapter(adap);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void httpFailure(String response, String requestedFor) throws Exception {

    }

    @Override
    public void httpResponses(String response, String requestedFor, Context context) throws Exception {


        if (requestedFor.equals("deleteOrderDetailsByID")) {

            try {
                Common.stopProgressDialouge(requestedFor);
                if (requestedFor.equals("deleteOrderDetailsByID")) {
                    JSONObject jObj = new JSONObject(response);
                    if (jObj.getString("ResultCode").equals("SUCCESS")) {
                        showDeleteAlert(context, jObj.getString("ResultMessage"));

                    } else {
                        Common.showAlert(context, jObj.getString("ResultMessage"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (requestedFor.equals("getOrderDocumentsList")) {
            try {
                Common.stopProgressDialouge(requestedFor);
                JSONObject documentjObj = new JSONObject(response);
                if (documentjObj.getString("ResultCode").equals("SUCCESS")) {
                    jArrayDocList = documentjObj.getJSONArray("ResultObject");
                    if (jArrayDocList.length() > 0) {
                        FrgDocList.tvNoRecords.setVisibility(View.GONE);
                        DocListAdap adap = new DocListAdap(context, jArrayDocList);
                        FrgDocList.lvDocs.setAdapter(adap);
                    } else {
                        FrgDocList.tvNoRecords.setVisibility(View.VISIBLE);
                        DocListAdap adap = new DocListAdap(context, jArrayDocList);
                        FrgDocList.lvDocs.setAdapter(adap);
                    }
                } else {
                    Common.showAlert(context, documentjObj.getString("ResultMessage"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (requestedFor.equals("getOrderDetailsList")) {
            try {
                Common.stopProgressDialouge(requestedFor);
                JSONObject jObj = new JSONObject(response);
                if (jObj.getString("ResultCode").equals("SUCCESS")) {
                    jArrayServiceList = jObj.getJSONArray("ResultObject");
                    if (jArrayServiceList.length() > 0) {
                        FrgServiceList.tvNoRecords.setVisibility(View.GONE);
                        ServiceListAdap adap = new ServiceListAdap(context, jArrayServiceList);
                        FrgServiceList.lvService.setAdapter(adap);
                    } else {
                        FrgServiceList.tvNoRecords.setVisibility(View.VISIBLE);
                        ServiceListAdap adap = new ServiceListAdap(context, jArrayServiceList);
                        FrgServiceList.lvService.setAdapter(adap);
                    }
                } else {
                    Common.showAlert(context, jObj.getString("ResultMessage"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (requestedFor.equals("deleteOrder")) {

            try {
                Common.stopProgressDialouge(requestedFor);
                if (requestedFor.equals("deleteOrder")) {
                    JSONObject jObj = new JSONObject(response);
                    if (jObj.getString("ResultCode").equals("SUCCESS")) {
                        showDeleteConfirationorder(context, jObj.getString("ResultMessage"));
                    } else {
                        Common.showAlert(context, jObj.getString("ResultMessage"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (requestedFor.equals("getOrderList")) {

            try {
                Common.stopProgressDialouge(requestedFor);
                JSONObject orderjObj = new JSONObject(response);
                if (orderjObj.getString("ResultCode").equals("SUCCESS")) {
                    jArrayOrderList = orderjObj.getJSONArray("ResultObject");
                    if (jArrayOrderList.length() > 0) {
                        FrgOrderList.tvNoRecords.setVisibility(View.GONE);
                        OrderListAdap adap = new OrderListAdap(context, jArrayOrderList);
                        FrgOrderList.lvOrder.setAdapter(adap);
                    } else {
                        FrgOrderList.tvNoRecords.setVisibility(View.VISIBLE);
                        OrderListAdap adap = new OrderListAdap(context, jArrayOrderList);
                        FrgOrderList.lvOrder.setAdapter(adap);
                    }
                } else {
                    Common.showAlert(context, orderjObj.getString("ResultMessage"));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (requestedFor.equals("DeleteDocument")) {

            try {
                Common.stopProgressDialouge(requestedFor);
                if (requestedFor.equals("DeleteDocument")) {
                    JSONObject jObj = new JSONObject(response);
                    if (jObj.getString("ResultCode").equals("SUCCESS")) {
                        showDeleteDocumentAlert(context, jObj.getString("ResultMessage"));
                    } else {
                        Common.showAlert(context, jObj.getString("ResultMessage"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    public void showDeleteAlert(Context context, String Msg) {
        final Context context1 = context;
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
                getServiceDetailList(context1);

            }
        });

        dialog.setContentView(v);
        dialog.show();
    }

    public void showDeleteConfirationorder(Context context, String Msg) {
        final Context context1 = context;
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
                getOrderList(context1);

            }
        });

        dialog.setContentView(v);
        dialog.show();
    }


    public void showDeleteDocumentAlert(Context context, String Msg) {
        final Context context1 = context;
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
                getDocumentList(context1);

            }
        });

        dialog.setContentView(v);
        dialog.show();
    }

    private void getDocumentList(Context context) {
        try {
            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("userid", Common.USER_SESSION);
            postDataParams.put("orderid", Common.selectedOrderDetail.getString("OrderNr"));
            postDataParams.put("ApplicationID", Common.APPLICATIONID);
            HttpConnectionD httpConnectiond = new HttpConnectionD(delegates, context);
            httpConnectiond.setRequestedfors("getOrderDocumentsList", context);
            httpConnectiond.setIsloading(true);
            httpConnectiond.setPostDataParams(postDataParams);
            httpConnectiond.setUrl("Orders/GetOrderDocumentsList");
            httpConnectiond.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getServiceDetailList(Context context) {
        try {
            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("orderid", Common.selectedOrderDetail.getString("OrderNr"));
            HttpConnectionD httpConnectiond = new HttpConnectionD(delegates, context);
            httpConnectiond.setRequestedfors("getOrderDetailsList", context);
            httpConnectiond.setIsloading(true);
            httpConnectiond.setPostDataParams(postDataParams);
            httpConnectiond.setUrl("Orders/GetOrderDetailsList");
            httpConnectiond.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getOrderList(Context context) {

        try {
            HashMap<String, String> postDataParams = new HashMap<>();
            postDataParams.put("userid", Common.USER_SESSION);
            HttpConnectionD httpConnectiond = new HttpConnectionD(delegates, context);
            httpConnectiond.setRequestedfors("getOrderList", context);
            httpConnectiond.setIsloading(true);
            httpConnectiond.setPostDataParams(postDataParams);
            httpConnectiond.setUrl("Orders/GetAssignOrders");
            httpConnectiond.execute("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.d("MainActivity", "Cancelled scan");
                super.onActivityResult(requestCode, resultCode, data);
            } else {
                Log.d("MainActivity", "Scanned");
                Common.BARCODE_STRING = result.getContents();
                super.onActivityResult(requestCode, resultCode, data);
            }
        } else {
        }
    }

    @Override
    public void checkPermission(String permission, Callback<Boolean> callback) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            callback.call(true);
            return;
        }

        permissionCallback = callback;
        permissionRequester.launch(permission);
    }

    @Override
    public void takePicture(Uri uri, Callback<Boolean> callback) {
        takePictureCallback = callback;
        takePictureLauncher.launch(uri);
    }

    @Override
    public void pickDocument(String[] mimeType, Callback<Uri> callback) {
        pickDocumentCallback = callback;
        pickDocumentLauncher.launch(mimeType);
    }

    private final ActivityResultLauncher<String> permissionRequester =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), result -> {
                if(permissionCallback != null){
                    permissionCallback.call(result);
                }
            });

    private final ActivityResultLauncher<Uri> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), result -> {
                if(takePictureCallback != null){
                    takePictureCallback.call(result);
                }
            });


    private final ActivityResultLauncher<String[]> pickDocumentLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), result -> {
                if(pickDocumentCallback != null)
                {
                    pickDocumentCallback.call(result);
                }
            });
}

