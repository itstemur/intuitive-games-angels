package com.visafm.roombook.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.visafm.roombook.Dashboard;
import com.visafm.roombook.R;
import com.visafm.roombook.common.Common;

import org.json.JSONArray;
import org.json.JSONObject;


public class DocListAdap extends BaseAdapter {
    Context context;
    JSONArray data;

    public DocListAdap(Context context, JSONArray data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.length();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(R.layout.list_docs, parent, false);

        TextView tvfilLabel = convertView.findViewById(R.id.tvfilLabel);
        TextView tvfilDescription = convertView.findViewById(R.id.tvfilDescription);
        TextView tvdocChangedOn = convertView.findViewById(R.id.tvdocChangedOn);
        TextView tvbinFilesize = convertView.findViewById(R.id.tvbinFilesize);
        ImageView ivDocImg = convertView.findViewById(R.id.ivDocImg);
        ImageView IvDocOptions = convertView.findViewById(R.id.IvDocOptions);


        try {
            final JSONObject jObj = data.getJSONObject(position);
            tvfilLabel.setText(jObj.getString("filLabel").trim());
            tvfilDescription.setText(jObj.getString("filDescription").trim());
            tvdocChangedOn.setText(jObj.getString("docChangedOn").trim());
            long fileSizeInBytes = jObj.getLong("binFilesize");
            tvbinFilesize.setText(Common.getStringSizeLengthFile(fileSizeInBytes));

            String str = jObj.getString("binFK_TlkpContentTypes").trim();
            byte[] decodedString = Base64.decode((jObj.getString("binData").toString().getBytes()), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);


            if (str.contains("image/")) {
                ivDocImg.setImageBitmap(decodedByte);
//                ivDocImg.setImageBitmap(Common.Bytes2Bimap(jObj.getString("binData").toString().getBytes()));
            } else if (str.contains("rtf")) {
                ivDocImg.setImageResource(R.drawable.img_documents_rtf);
            } else if (str.contains("application/pdf")) {
                ivDocImg.setImageResource(R.drawable.img_documents_pdf);
            } else if (str.contains("ifc")) {
                ivDocImg.setImageResource(R.drawable.img_documents_ifc);
            } else if (str.contains("document")) {
                ivDocImg.setImageResource(R.drawable.img_documents_docx);
            } else if (str.contains("application/msword")) {
                ivDocImg.setImageResource(R.drawable.img_documents_doc);
            } else if (str.contains("application/ms-excel")) {
                ivDocImg.setImageResource(R.drawable.img_documents_excel);
            } else if (str.contains("application/vnd.ms-excel")) {
                ivDocImg.setImageResource(R.drawable.img_documents_excel);
            } else if (str.contains("sheet")) {
                ivDocImg.setImageResource(R.drawable.img_documents_excel);
            } else if (str.contains("application/ms-powerpoint")) {
                ivDocImg.setImageResource(R.drawable.img_documents_ppt);
            } else if (str.contains("presentation")) {
                ivDocImg.setImageResource(R.drawable.img_documents_ppt);
            } else if (str.contains("text")) {
                ivDocImg.setImageResource(R.drawable.img_documents_text);
            } else {
                ivDocImg.setImageResource(R.drawable.img_documents);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Common.selectedDocumentDetail = jObj;
                    Common.setSharedPreferences(context, "EditDocumentDetailFromPage", "True");
                    Dashboard.displayFragment(61);
                }
            });

            IvDocOptions.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view.getId() == R.id.IvDocOptions) {
                        Dashboard.documentOptions(context, view, jObj);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

}