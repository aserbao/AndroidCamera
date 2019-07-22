package com.aserbao.androidcustomcamera.whole.pickvideo.loader;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

import static android.provider.MediaStore.MediaColumns.MIME_TYPE;

/**
 * Created by Vincent Woo
 * Date: 2016/10/10
 * Time: 17:55
 */

public class ImageLoader extends CursorLoader {
    private static final String[] IMAGE_PROJECTION = {
            //Base File
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.TITLE,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            //Image File
            MediaStore.Images.Media.ORIENTATION
    };

    private ImageLoader(Context context, Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    public ImageLoader(Context context) {
        super(context);
        setProjection(IMAGE_PROJECTION);
        setUri(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        setSortOrder(MediaStore.Images.Media.DATE_ADDED + " DESC");

        setSelection(MIME_TYPE + "=? or " + MIME_TYPE + "=? or "+ MIME_TYPE + "=? or " + MIME_TYPE + "=?");

        String[] selectionArgs;
        selectionArgs = new String[] { "image/jpeg", "image/png", "image/jpg","image/gif" };
        setSelectionArgs(selectionArgs);
    }
}
