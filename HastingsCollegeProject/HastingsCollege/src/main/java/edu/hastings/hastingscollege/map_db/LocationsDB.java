package edu.hastings.hastingscollege.map_db;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class LocationsDB extends SQLiteOpenHelper {
    private final Context mContext;

    private static String DB_PATH = "/data/data/edu.hastings.hastingscollege/databases/";
    private static String DB_NAME = "map_data.db";
    private static int VERSION = 1;
    private static final String DATABASE_TABLE = "locations";
    public static final String FIELD_ROW_ID = "_id";
    public static final String FIELD_LAT = "latitude";
    public static final String FIELD_LNG = "longitude";
    public static final String FIELD_TITLE = "title";
    public static final String FIELD_SNIPPET = "snippet";
    private SQLiteDatabase mDB;

    public LocationsDB(Context context) throws IOException{
        super(context, DB_NAME, null, VERSION);
        this.mContext = context;
        boolean dbexist = checkDataBase();
        if (dbexist) {
            openDataBase();
        } else {
            System.out.println("Database doesn't exist");
            createDataBase();
        }
    }

    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - database already exist
        }else{
            this.getReadableDatabase();
            this.close();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase(){
        boolean checkdb = false;
        try {
            String myPath = DB_PATH + DB_NAME;
            File dbfile = new File(myPath);
            checkdb = dbfile.exists();
        } catch(SQLiteException e) {
            System.out.println("Database doesn't exist");
        }
        return checkdb;
    }

    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = mContext.getAssets().open(DB_NAME);
        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;
        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);
        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }
        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    public void openDataBase() throws SQLException {
        //Open the database
        String myPath = DB_PATH + DB_NAME;
        mDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if(mDB != null)
            mDB.close();
        super.close();
    }

    public Cursor getData(SQLiteDatabase db)
    {
        return db.query(DATABASE_TABLE, new String[]{
                FIELD_ROW_ID, FIELD_LAT, FIELD_LNG, FIELD_TITLE, FIELD_SNIPPET
        }, null, null, null, null, null, null);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
