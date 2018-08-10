package com.rosamohammadi.notetaking;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

// allows access to db to rest of app
public class NotesProvider extends ContentProvider {

    private static final String AUTHORITY = "com.rosamohammadi.notetaking.notesprovider";
    // name of table
    private static final String BASE_PATH = "notes";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH );

    // constant to identify the requested operation
    private static final int NOTES = 1;
    private static final int NOTES_ID = 2;

    // parses UI and tells which operation is requested
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // executes first time this class is called
    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, NOTES);
        // find item with id
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", NOTES);
    }

    private SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        DBOpenHelper helper = new DBOpenHelper(getContext());
        db = helper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    // get data from database table
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable
            String s, @Nullable String[] strings1, @Nullable String s1) {

        // only querying a specific note
        if (uriMatcher.match(uri) == NOTES_ID) {
            s = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();
        }

        // order data in descending order by date
        return db.query(DBOpenHelper.TABLE_NOTES, DBOpenHelper.ALL_COLUMNS,
                s, null, null, null,
                DBOpenHelper.NOTE_CREATED + " DESC");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        long id = db.insert(
                DBOpenHelper.TABLE_NOTES, null, contentValues);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    public static final String CONTENT_ITEM_TYPE = "Note";

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s,
                      @Nullable String[] strings) {
        // return number of rows deleted
        return db.delete(DBOpenHelper.TABLE_NOTES, s, strings);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String s, @Nullable String[] strings) {
        return db.update(DBOpenHelper.TABLE_NOTES, contentValues, s, strings);
    }
}
