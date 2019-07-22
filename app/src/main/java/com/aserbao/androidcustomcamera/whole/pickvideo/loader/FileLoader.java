package com.aserbao.androidcustomcamera.whole.pickvideo.loader;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;

/**
 * Created by Vincent Woo
 * Date: 2016/10/12
 * Time: 14:48
 */

public class FileLoader extends CursorLoader {
    private static final String[] FILE_PROJECTION = {
            //Base File
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.TITLE,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.SIZE,
            MediaStore.Files.FileColumns.DATE_ADDED,

            //Normal File
            MediaStore.Files.FileColumns.MIME_TYPE
    };

    private FileLoader(Context context, Uri uri, String[] projection, String selection,
                       String[] selectionArgs, String sortOrder) {
        super(context, uri, projection, selection, selectionArgs, sortOrder);
    }

    public FileLoader(Context context) {
        super(context);
        setProjection(FILE_PROJECTION);
        setUri(MediaStore.Files.getContentUri("external"));
        setSortOrder(MediaStore.Files.FileColumns.DATE_ADDED + " DESC");

//        setSelection(MIME_TYPE + "=? or "
////                + MIME_TYPE + "=? or "
////                + MIME_TYPE + "=? or "
//                + MIME_TYPE + "=?");
//
//        String[] selectionArgs;
//        selectionArgs = new String[] { "text/txt", "text/plain" };
//        setSelectionArgs(selectionArgs);
    }
}
