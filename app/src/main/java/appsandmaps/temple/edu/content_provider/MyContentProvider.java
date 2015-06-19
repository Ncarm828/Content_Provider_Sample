package appsandmaps.temple.edu.content_provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.HashMap;

import android.content.ContentUris;
import android.content.Context;
import android.content.UriMatcher;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

    /*
        Simple Content Provider that connects to a local database and holds names, id and steps
         !! NOT finals content provider. Final will ont have a local database instead wil connect
         with the server information.
         For testing purpose the local database is create when in OnCreate and then calls a helper class
         within the MyContentProvider
         Content Provider information:

         - Outside applications will only have Access to the Contract class so there is an abstract
         layer between the sever and the 3rd party application.
         - The App that this Content provider is implemented in will have a UI and also be connected to the
         server
         - This Content Provider will Not be able to delete data nor insert data. this is a read only
         provider
         - Writing new information to the server datbase will be the job
            1. The after the connection of the wearable device
            2. adding or deleting user from database

    */

public class MyContentProvider extends ContentProvider implements AsyncResponse {

    //Utility class to aid in matching URIs in content providers.
    private static final UriMatcher sUriMatcher;

    private static final int NOTES_ALL = 1; //This will connect to a certain table
    private static final int NOTES_ONE = 2; //this will connect a ceratin element within the table

    //Part of the Utility class which matches the above variables to the Authority
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(ContractClass.AUTHORITY, "steps", NOTES_ALL);
        sUriMatcher.addURI(ContractClass.AUTHORITY, "steps/#", NOTES_ONE);
    }

    //Creates the table in which the the database will used
    private static final HashMap<String, String> sNotesColumnProjectionMap;
    static {
        sNotesColumnProjectionMap = new HashMap<String, String>();
        sNotesColumnProjectionMap.put(ContractClass.FitNessTable.ID, ContractClass.FitNessTable.ID);
        sNotesColumnProjectionMap.put(ContractClass.FitNessTable.STEPS, ContractClass.FitNessTable.STEPS);
        sNotesColumnProjectionMap.put(ContractClass.FitNessTable.EXPERIENCE, ContractClass.FitNessTable.EXPERIENCE);
    }

    // create a db helper object
    private FitnessDBHelper mDbHelper;

    //Constructor
    public MyContentProvider() {
    }

    //Delete method is not implemented because the content provider does not have access to deleting
    //database information. This is delth within another part of the application
    //If called if will throw a Error
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    //returns the path to either the whole table or an element in the table
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case NOTES_ALL:
                return ContractClass.CONTENT_TYPE_NOTES_ALL;

            case NOTES_ONE:
                return ContractClass.CONTENT_TYPE_NOTES_ONE;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // you cannot insert a bunch of values at once so throw exception
        if (sUriMatcher.match(uri) != NOTES_ALL) {
            throw new IllegalArgumentException(" Unknown URI: " + uri);
        }

        // Insert once row
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long rowId = db.insert(ContractClass.FitNessTable.TABLE_NAME, null,
                values);
        if (rowId > 0) {
            Uri notesUri = ContentUris.withAppendedId(
                    ContractClass.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(notesUri, null);
            return notesUri;
        }
        throw new IllegalArgumentException("<Illegal>Unknown URI: " + uri);

    }


    @Override
    public boolean onCreate() {
        UpdateActivityClass GetData = new UpdateActivityClass();
        //try {
        GetData.delegate = this;

        GetData.execute();//.wait();
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
       // }

            mDbHelper = new FitnessDBHelper(getContext());



        return false;
    }

    private void InsertData() {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM " + ContractClass.FitNessTable.TABLE_NAME, null);
        Boolean rowExists;

        if (mCursor.moveToFirst()) {
            ContentValues args = new ContentValues();
            args.put(ContractClass.FitNessTable.STEPS, MainActivity.Steps);
            args.put(ContractClass.FitNessTable.EXPERIENCE, ContractClass.DataBaseInfoHolder);
            db.update(ContractClass.FitNessTable.TABLE_NAME, args, ContractClass.FitNessTable.ID + "=" + 1, null);
            rowExists = true;

        } else {
            // I AM EMPTY
            ContentValues values = new ContentValues();
            values.put(ContractClass.FitNessTable.STEPS, MainActivity.Steps);
            values.put(ContractClass.FitNessTable.EXPERIENCE, ContractClass.DataBaseInfoHolder);

            long rowId = db.insert(ContractClass.FitNessTable.TABLE_NAME, null, values);
            if (rowId > 0) {
                Uri notesUri = ContentUris.withAppendedId(
                        ContractClass.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(notesUri, null);
                rowExists = false;
            }
        }
    }

    public void ProcessFinish(String results) {
            CharSequence SubString = results.subSequence(31,36);
            ContractClass.DataBaseInfoHolder = SubString.toString();
            InsertData();

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {
            case NOTES_ALL:
                builder.setTables(ContractClass.FitNessTable.TABLE_NAME);
                builder.setProjectionMap(sNotesColumnProjectionMap);
                break;

            case NOTES_ONE:
                builder.setTables(ContractClass.FitNessTable.TABLE_NAME);
                builder.setProjectionMap(sNotesColumnProjectionMap);
                builder.appendWhere(ContractClass.FitNessTable.ID + " = "
                        + uri.getLastPathSegment());
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor queryCursor = builder.query(db, projection, selection,
                selectionArgs, null, null, null);
        queryCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return queryCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int count = 0;
        switch (sUriMatcher.match(uri)) {
            case NOTES_ALL:
                count = db.update(ContractClass.FitNessTable.TABLE_NAME, values,
                        selection, selectionArgs);
                break;

            case NOTES_ONE:
                String rowId = uri.getLastPathSegment();
                count = db.update(ContractClass.FitNessTable.TABLE_NAME, values,
                                ContractClass.FitNessTable.ID + " = " + rowId + (!TextUtils.isEmpty(selection) ? " AND (" + ")" : ""), selectionArgs);

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }





    private static class FitnessDBHelper extends SQLiteOpenHelper {

        public FitnessDBHelper(Context c) {
            super(c, ContractClass.DATABASE_NAME, null,
                    ContractClass.DATABASE_VERSION);
        }

        private static final String SQL_QUERY_CREATE = "CREATE TABLE "
                + ContractClass.FitNessTable.TABLE_NAME + " ("
                + ContractClass.FitNessTable.ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ContractClass.FitNessTable.STEPS + " TEXT NOT NULL, "
                + ContractClass.FitNessTable.EXPERIENCE + " TEXT NOT NULL" + ");";

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_QUERY_CREATE);
        }

        private static final String SQL_QUERY_DROP = "DROP TABLE IF EXISTS "
                + ContractClass.FitNessTable.TABLE_NAME + ";";

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
            db.execSQL(SQL_QUERY_DROP);
            onCreate(db);
        }
    }

}
