package com.visafm;

import android.net.Uri;

public interface ActivityBridge {

    void checkPermission(String permission, Callback<Boolean> callback);

    void takePicture(Uri uri, Callback<Boolean> callback);

    void pickDocument(String[] mimeType, Callback<Uri> callback);

    interface Callback<O> {
        void call(O result);
    }
}
