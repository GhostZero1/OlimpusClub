package com.example.olimpusclub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import data.ClubOlympusContract;

public class AddMemeberActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>{
    private static final int EDIT_MEMBER_LOADER = 111;
    Uri currentMemberUri;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText groupNameEditText;
    private EditText sportEditText;
    private Spinner genderSpinner;
    private int gender = 0;
    private ArrayAdapter spinnerAdapter;
    //private ArrayList spinnerArrayList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_memeber);

        Intent intent = getIntent();

        currentMemberUri = intent.getData();

        if (currentMemberUri == null){
            setTitle("Add Member");
        }else{
            setTitle("Edit the Member");
            getSupportLoaderManager().initLoader(EDIT_MEMBER_LOADER, null,this);
        }

        firstNameEditText=findViewById(R.id.firstNameEditText);
        lastNameEditText=findViewById(R.id.lastNameEditText);
        sportEditText = findViewById(R.id.sportEditText);
        genderSpinner = findViewById(R.id.genderSpinner);

       /* spinnerArrayList = new ArrayList();
        spinnerArrayList.add("Unknown");
        spinnerArrayList.add("Male");
        spinnerArrayList.add("Female");*/

        /*spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerArrayList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(spinnerAdapter);*/

        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_gender, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(spinnerAdapter);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedGender = (String) parent.getItemAtPosition(position);
                if(!TextUtils.isEmpty(selectedGender)){
                   if(selectedGender.equals("Male")){
                       gender = ClubOlympusContract.MemberEntery.GENDER_MALE;

                   }else if (selectedGender.equals("Female")){
                       gender = ClubOlympusContract.MemberEntery.GENDER_FEMALE;
                   }else{
                       gender = ClubOlympusContract.MemberEntery.GENDER_UNKNOWN;
                   }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {gender = 0; }
        });




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_member, menu);
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_member:
                saveMember();
                return true;

            case R.id.delete_member:
                showDeleteMemeberDialog();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }
    private void saveMember(){
        String firstName = firstNameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String sport = sportEditText.getText().toString().trim();
        if (TextUtils.isEmpty(firstName)){
            Toast.makeText(this, "Input the first name ", Toast.LENGTH_LONG).show();
            return;
        }else if (TextUtils.isEmpty(lastName)) {
            Toast.makeText(this, "Input the last name ", Toast.LENGTH_LONG).show();
            return;
        }else if (TextUtils.isEmpty(sport)) {
            Toast.makeText(this, "Input the sport ", Toast.LENGTH_LONG).show();
            return;
        }else if (gender == ClubOlympusContract.MemberEntery.GENDER_UNKNOWN) {
            Toast.makeText(this, "Choose the gender ", Toast.LENGTH_LONG).show();
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(ClubOlympusContract.MemberEntery.COLUMN_FIRST_NAME, firstName);
        contentValues.put(ClubOlympusContract.MemberEntery.COLUMN_LAST_NAME, lastName);
        contentValues.put(ClubOlympusContract.MemberEntery.COLUMN_SPORT, sport);
        contentValues.put(ClubOlympusContract.MemberEntery.COLUMN_GENDER, gender);

        if (currentMemberUri == null)   {
            ContentResolver contentResolver = getContentResolver();
            Uri uri = contentResolver.insert(ClubOlympusContract.MemberEntery.CONTENT_URI, contentValues);

            if (uri == null){
                Toast.makeText(this, "Insertion of data in the table failed for ", Toast.LENGTH_LONG).show();
            } else{
                Toast.makeText(this, "Data saved", Toast.LENGTH_LONG).show();
            }
        }else{
            int rowsChanged = getContentResolver().update(currentMemberUri, contentValues, null, null);

            if (rowsChanged == 0){
                Toast.makeText(this, "Saving of data in the table failed for ", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this, "Member updated saved", Toast.LENGTH_LONG).show();
            }
        }

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        String[] projection = {ClubOlympusContract.MemberEntery._ID,
                                ClubOlympusContract.MemberEntery.COLUMN_FIRST_NAME,
                                ClubOlympusContract.MemberEntery.COLUMN_LAST_NAME,
                                ClubOlympusContract.MemberEntery.COLUMN_GENDER,
                                ClubOlympusContract.MemberEntery.COLUMN_SPORT};

        return new CursorLoader(this,
                currentMemberUri,
                projection,
                null,null,null
        );
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()){
            int firstNameColumnIndex = cursor.getColumnIndex(
                    ClubOlympusContract.MemberEntery.COLUMN_FIRST_NAME
            );
            int lastNameColumnIndex = cursor.getColumnIndex(
                    ClubOlympusContract.MemberEntery.COLUMN_LAST_NAME
            );
            int genderColumnIndex = cursor.getColumnIndex(
                    ClubOlympusContract.MemberEntery.COLUMN_GENDER
            );
            int sportColumnIndex = cursor.getColumnIndex(
                    ClubOlympusContract.MemberEntery.COLUMN_SPORT
            );
            String firstName = cursor.getString(firstNameColumnIndex);
            String lastName = cursor.getString(lastNameColumnIndex);
            int gender = cursor.getInt(genderColumnIndex);
            String sport = cursor.getString(sportColumnIndex);

            firstNameEditText.setText(firstName);
            lastNameEditText.setText(lastName);
            sportEditText.setText(sport);

            switch(gender){
                case ClubOlympusContract.MemberEntery.GENDER_MALE:
                    genderSpinner.setSelection(1);
                    break;
                case ClubOlympusContract.MemberEntery.GENDER_FEMALE:
                    genderSpinner.setSelection(2);
                    break;
                case ClubOlympusContract.MemberEntery.GENDER_UNKNOWN:
                    genderSpinner.setSelection(0);
                    break;
            }

        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
    private void showDeleteMemeberDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete the member?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteMember();

            }
        });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(dialog != null){
                            dialog.dismiss();
                        }
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteMember(){
        if (currentMemberUri != null){
            int rowsDeleted = getContentResolver().delete(currentMemberUri, null, null);
            if (rowsDeleted == 0){
                Toast.makeText(this,"Deleting of the data from the table failed ", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(this,"Member is deleted ", Toast.LENGTH_LONG).show();
            }
            finish();
        }

    }

}