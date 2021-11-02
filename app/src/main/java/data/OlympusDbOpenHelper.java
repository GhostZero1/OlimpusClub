package data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import data.ClubOlympusContract;
import androidx.annotation.Nullable;

public class OlympusDbOpenHelper extends SQLiteOpenHelper {

    public OlympusDbOpenHelper(Context context) {
        super(context, ClubOlympusContract.DATABASE_NAME, null,ClubOlympusContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MEMBERS_TABLE = "CREATE TABLE " + ClubOlympusContract.MemberEntery.TABLE_NAME
                + " ( " + ClubOlympusContract.MemberEntery._ID + " INTEGER PRIMARY KEY," + ClubOlympusContract.MemberEntery.COLUMN_FIRST_NAME
                 + " TEXT," + ClubOlympusContract.MemberEntery.COLUMN_LAST_NAME + " TEXT," +
                ClubOlympusContract.MemberEntery.COLUMN_GENDER + " INTEGER NOT NULL," + ClubOlympusContract.MemberEntery.COLUMN_SPORT + " TEXT " + " )";
        db.execSQL(CREATE_MEMBERS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ClubOlympusContract.DATABASE_NAME);
        onCreate(db);
    }
}
