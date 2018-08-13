package com.rosamohammadi.notetaking;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class EditorActivity extends AppCompatActivity {

    private String action;
    private EditText editor;
    // state for editing an existing note
    private String noteFilter;
    private String oldText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        editor = findViewById(R.id.editText);

        // intent that started this activity
        Intent intent = getIntent();

        // if editing a note, uri will be passed as intent extra
        Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);

        // uri will be null if user is creating a new note
        if (uri == null) {
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_note));
        }
        // display the existing notes text
        else {
            action = Intent.ACTION_EDIT;
            setTitle("");
            noteFilter = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();

            // get all columns for this existing note
            Cursor cursor = getContentResolver().query(
                    uri, DBOpenHelper.ALL_COLUMNS, noteFilter,
                    null, null);
            // move to this data - no need to null check as we know note exists
            if (cursor != null) {
                cursor.moveToFirst();
                oldText = cursor.getString(
                        cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
                editor.setText(oldText);
            }
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // only show trash can icon for existing note
        if (action.equals(Intent.ACTION_EDIT)) {
            getMenuInflater().inflate(R.menu.menu_editor, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case android.R.id.home:
                finishEditing();
                break;
            case R.id.action_delete:
                deleteNote();
                break;
        }

        return true;
    }


    // called when user taps back button or back on toolbar
    private void finishEditing() {
        String newText = editor.getText().toString().trim();

        switch (action) {
            case Intent.ACTION_INSERT:
                // if new note is blank, cancel request
                if (newText.length() == 0) {
                    setResult(RESULT_CANCELED);
                }
                else {
                    insertNote(newText);
                }
                break;
            case Intent.ACTION_EDIT:
                if (newText.length() == 0) {
                    deleteNote();
                }
                else if (oldText.equals(newText)) {
                    setResult(RESULT_CANCELED);
                }
                else {
                    updateNote(newText);
                }
        }
        // done with this activity, go back to parent activity
        finish();
    }

    private void updateNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        // only update the selected row
        getContentResolver().update(NotesProvider.CONTENT_URI, values,
                noteFilter, null);
        Toast.makeText(this, R.string.note_updated, Toast.LENGTH_SHORT).show();
        // ensure main activity updates data in list
        setResult(RESULT_OK);
    }


    private void deleteNote() {
        getContentResolver().delete(NotesProvider.CONTENT_URI, noteFilter,
                null);
        Toast.makeText(this, R.string.note_deleted, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        // return to main activity
        finish();
    }

    private void insertNote(String noteText) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        // insert row into db table
        getContentResolver().insert(NotesProvider.CONTENT_URI, values);
        setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed() {
        finishEditing();
    }

}
