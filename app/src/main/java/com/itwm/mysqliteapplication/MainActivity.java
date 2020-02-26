//this project aims to create a simple connection to SQLite for the purpose of studying and
// understanding the connection to an internal database on the device.

package com.itwm.mysqliteapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.*;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    GridView gv;
    EditText editTitle;
    EditText editSub;
    Button buttonScan;
    SimpleAdapter adapter;
//parameterization of database values.
    private String TABLE_NAME = "dbone";
    private String ID = "id";
    private String COLUMN_NAME_TITLE = "title";
    private String COLUMN_NAME_SUBTITLE = "subtitle";
    DbHelper dbHelper = new DbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonScan = (Button) findViewById(R.id.buttonScan);
        buttonScan.setOnClickListener(this);

        loadLV();
    }
//this function loads grindview with data from the database.
    void loadLV(){
        gv =(GridView) findViewById(R.id.list);

        gv.setAdapter(null);
        final List<HashMap<String, Object>> hashMapArrayList = new ArrayList<>();

        String[] projection = {ID,COLUMN_NAME_TITLE,COLUMN_NAME_SUBTITLE};
        String selection = ID + " > ? ";
        String[] selectionArgs = { "0" };
        String sortOrder = ID + " DESC";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        while(cursor.moveToNext()){
            HashMap<String, Object> itemHM = new HashMap<>();
            int itemID = cursor.getInt(cursor.getColumnIndexOrThrow(ID));
            String itemTITLE = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_TITLE));
            String itemSUBTITLE = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME_SUBTITLE));
            itemHM.put(ID,itemID);
            itemHM.put(COLUMN_NAME_TITLE,itemTITLE);
            itemHM.put(COLUMN_NAME_SUBTITLE,itemSUBTITLE);
            hashMapArrayList.add(itemHM);

        }
        String[] From = {ID,COLUMN_NAME_TITLE,COLUMN_NAME_SUBTITLE};
        adapter = new SimpleAdapter(MainActivity.this, hashMapArrayList ,R.layout.row,From,new int[]{R.id.list_value1,R.id.list_value2,R.id.list_value3} );
        gv.setAdapter(adapter);
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View view, int i, long l) {
                remData(String.valueOf(hashMapArrayList.get(i).get(ID)));
                loadLV();
            }
        });
        cursor.close();
    }
    //this function adds field values to the database
    void addData(String add_COLUMN_NAME_TITLE,String add_COLUMN_NAME_SUBTITLE){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_TITLE, add_COLUMN_NAME_TITLE);
        values.put(COLUMN_NAME_SUBTITLE, add_COLUMN_NAME_SUBTITLE);

        long newRowId = db.insert(TABLE_NAME, null, values);
        db.close();
        Toast.makeText(this, newRowId + " | " + values, Toast.LENGTH_SHORT).show();
        loadLV();
    }
    //this function removes the row clicked on the database grindviews.
    void remData(String rid){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = ID + " = ?";
        System.out.println(selection);
        String[] selectionArgs = { rid };
        db.delete(TABLE_NAME, selection, selectionArgs);
        loadLV();
    }
    public class DbHelper extends SQLiteOpenHelper {

        private final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        ID + " INTEGER PRIMARY KEY," +
                        COLUMN_NAME_TITLE + " TEXT," +
                        COLUMN_NAME_SUBTITLE + " TEXT)";

        private final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;

        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "FeedReader.db";

        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
        }
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
    }
    //this function is the click action in the gridview
    public void onClick(View view){
        editTitle = (EditText) findViewById(R.id.editTitle);
        editSub = (EditText) findViewById(R.id.editSub);
        String newTitle = String.valueOf(editTitle.getText());
        String newSub = String.valueOf(editSub.getText());
        addData(newTitle,newSub);
        loadLV();
    }
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
