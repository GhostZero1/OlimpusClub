package data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class OlympusContentProvider extends ContentProvider {

    OlympusDbOpenHelper dbOpenHelper;
    private static final int MEMBERS = 111;
    private static final int MEMBER_ID = 222;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(ClubOlympusContract.AUTHORITY, ClubOlympusContract.PATH_MEMBERS, MEMBERS);
        uriMatcher.addURI(ClubOlympusContract.AUTHORITY, ClubOlympusContract.PATH_MEMBERS + "/#", MEMBER_ID);
    }


    @Override
    public boolean onCreate() {
        dbOpenHelper = new OlympusDbOpenHelper(getContext());
        return true;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbOpenHelper.getReadableDatabase();
        Cursor cursor;
        int match = uriMatcher.match(uri);
        switch (match) {
            case MEMBERS:
                cursor = db.query(ClubOlympusContract.MemberEntery.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case MEMBER_ID:
                selection = ClubOlympusContract.MemberEntery._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ClubOlympusContract.MemberEntery.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:

                throw new IllegalArgumentException("Can't query incorrect URI" + uri);

        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        String firstName = values.getAsString(ClubOlympusContract.MemberEntery.COLUMN_FIRST_NAME);
        if (firstName == null) {
            throw new IllegalArgumentException("You have to input first name");

        }
        String lastName = values.getAsString(ClubOlympusContract.MemberEntery.COLUMN_LAST_NAME);
        if (lastName == null) {
            throw new IllegalArgumentException("You have to input last name");

        }

        Integer gender = values.getAsInteger(ClubOlympusContract.MemberEntery.COLUMN_GENDER);
        if (gender == null || !(gender == ClubOlympusContract.MemberEntery.GENDER_UNKNOWN || gender ==
                ClubOlympusContract.MemberEntery.GENDER_MALE || gender == ClubOlympusContract.MemberEntery.GENDER_FEMALE)) {
            throw new IllegalArgumentException("You have to input correct gender");

        }
        String sport = values.getAsString(ClubOlympusContract.MemberEntery.COLUMN_SPORT);
        if (sport == null) {
            throw new IllegalArgumentException("You have to input sport");

        }

        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        switch (match) {
            case MEMBERS:
                long id = db.insert(ClubOlympusContract.MemberEntery.TABLE_NAME, null, values);
                if (id == -1) {
                    Log.e("insertMethod", "Insertion of data in the table failed for " + uri);
                    return null;

                }

                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);

            default:

                throw new IllegalArgumentException("Insertion of data in the table failed for " + uri);

        }

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        int rowsDeleted;

        int match = uriMatcher.match(uri);
        switch (match) {
            case MEMBERS:
                rowsDeleted = db.delete(ClubOlympusContract.MemberEntery.TABLE_NAME, selection, selectionArgs);
                break;


            case MEMBER_ID:
                selection = ClubOlympusContract.MemberEntery._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(ClubOlympusContract.MemberEntery.TABLE_NAME, selection, selectionArgs);
                break;
            default:

                throw new IllegalArgumentException("Can't delete URI" + uri);


        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return  rowsDeleted;
    }



    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        if (values.containsKey(ClubOlympusContract.MemberEntery.COLUMN_FIRST_NAME)) {
            String firstName = values.getAsString(ClubOlympusContract.MemberEntery.COLUMN_FIRST_NAME);
            if (firstName == null) {
                throw new IllegalArgumentException("You have to input first name");
            }
        }

        if (values.containsKey(ClubOlympusContract.MemberEntery.COLUMN_LAST_NAME)) {
            String lastName = values.getAsString(ClubOlympusContract.MemberEntery.COLUMN_LAST_NAME);
            if (lastName == null) {
                throw new IllegalArgumentException("You have to input last name");
            }
        }
        if (values.containsKey(ClubOlympusContract.MemberEntery.COLUMN_GENDER)) {
            Integer gender = values.getAsInteger(ClubOlympusContract.MemberEntery.COLUMN_GENDER);
            if (gender == null || !(gender == ClubOlympusContract.MemberEntery.GENDER_UNKNOWN || gender ==
                    ClubOlympusContract.MemberEntery.GENDER_MALE || gender == ClubOlympusContract.MemberEntery.GENDER_FEMALE)) {
                throw new IllegalArgumentException("You have to input correct gender");
            }
        }
        if (values.containsKey(ClubOlympusContract.MemberEntery.COLUMN_SPORT)) {
            String sport = values.getAsString(ClubOlympusContract.MemberEntery.COLUMN_SPORT);
            if (sport == null) {
                throw new IllegalArgumentException("You have to input sport");
            }
        }

        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();

        int match = uriMatcher.match(uri);
        int rowsUpdated;
        switch (match){
            case MEMBERS:
                rowsUpdated = db.update(ClubOlympusContract.MemberEntery.TABLE_NAME, values, selection, selectionArgs);


                break;

            case MEMBER_ID:
                selection = ClubOlympusContract.MemberEntery._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsUpdated = db.update(ClubOlympusContract.MemberEntery.TABLE_NAME, values, selection, selectionArgs);


                break;

            default:

                throw new IllegalArgumentException("Can't update URI" + uri);

        }
        if (rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;

    }
    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match){
            case MEMBERS:
                return ClubOlympusContract.MemberEntery.CONTENT_MULTIPLE_ITEMS;

            case MEMBER_ID:

                return ClubOlympusContract.MemberEntery.CONTENT_SINGLE_ITEM;

            default:

                throw new IllegalArgumentException("Unknown URI:" + uri);

        }

    }
}
