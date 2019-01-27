package com.example.statesaver.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.statesaver.types.AnswerItem;
import com.example.statesaver.types.ContentData;
import com.example.statesaver.types.HelpItem;
import com.example.statesaver.types.RequestItem;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DbHandler extends SQLiteOpenHelper {

    private static int REQUEST_ID = 0;

    public static String TABLE_CONTENT = "contents";
    public static String CONTENT_ID_FIELD = "content_id";
    public static String CONTENT_DESC_FIELD = "content_desc";
    public static String CONTENT_FILE_LOC_FIELD = "content_file";
    public static String CONTENT_FILE_TYPE_FIELD = "content_file_type";

    //Content = save_to_offline DB
    //public static final String DATABASE_NAME="SavedPagesMeta.db";
/*    public static final String TABLE_CONTENT="contents";
    public static final String TITLE="title";
    public static final String FILE_LOCATION="file_location";
    public static final String THUMBNAIL="thumbnail";
    public static final String ORIGINAL_URL="origurl";
    public static final String CONTENT_ID="_id";
    public static final String TIMESTAMP="timestamp";
    public static final String SAVED_PAGE_BASE_DIRECTORY="tags";
*/
    public static String TABLE_REQUESTS = "requests";
    public static String REQUEST_ID_FIELD = "request_id";
    public static String REQUEST_TEXT_FIELD = "request_text";
    public static String REQUEST_LAST_HOP_FIELD = "last_hop";
    public static String REQUEST_ORIGIN_FIELD = "origin";

    public static String TABLE_QUESTIONS = "questions";
    public static String QUESTION_ID_FIELD = "question_id";
    public static String QUESTION_TEXT_FIELD = "question_text";

    public static String TABLE_ANSWERS = "answers";
    public static String QUESTION_ID_FKEY_FIELD = "question_id";
    public static String ANSWER_TEXT_FIELD = "answer_text";
    public static String ANSWER_ID_FIELD = "answer_id";

    public static String DATABASE_NAME = "unnamed_database";
    public static int DATABASE_VERSION = 1;

    public DbHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    private static DbHandler instance = null;

    public static DbHandler getInstance(Context context) {
        if (instance == null)
            instance = new DbHandler(context, DATABASE_NAME, null, DATABASE_VERSION);
        return instance;
    }

    private void createRequestTable(SQLiteDatabase db) {
        String CREATE_REQ_TABLE = "CREATE TABLE " + TABLE_REQUESTS +
                "(" +
                REQUEST_ID_FIELD + " TEXT ," + // Define a primary key
                REQUEST_TEXT_FIELD + " TEXT, " +
                REQUEST_LAST_HOP_FIELD + " TEXT, " +
                REQUEST_ORIGIN_FIELD + " TEXT" +

                ")";
        db.execSQL(CREATE_REQ_TABLE);
    }

    private void createQuestionsTable(SQLiteDatabase db) {
        String CREATE_QUESTIONS_TABLE = "CREATE TABLE " + TABLE_QUESTIONS +
                "(" +
                QUESTION_ID_FIELD + " INTEGER PRIMARY KEY," + // Define a primary key
                QUESTION_TEXT_FIELD + " TEXT " +
                ")";
        db.execSQL(CREATE_QUESTIONS_TABLE);
    }

    private void createContentTable(SQLiteDatabase db) {
        String CREATE_CONTENT_TABLE = "CREATE TABLE " + TABLE_CONTENT +
                "(" +
                CONTENT_ID_FIELD + " INTEGER PRIMARY KEY," + // Define a primary key
                CONTENT_FILE_LOC_FIELD + " TEXT, " +
                CONTENT_FILE_TYPE_FIELD + " TEXT " +
/*
                CONTENT_ID+" INTEGER PRIMARY KEY, "
                +TITLE+" TEXT, "
                +FILE_LOCATION+" TEXT, "
                +THUMBNAIL+" TEXT, "
                +ORIGINAL_URL+" TEXT, "
                +SAVED_PAGE_BASE_DIRECTORY+" TEXT, "
                +TIMESTAMP+" TEXT DEFAULT CURRENT_TIMESTAMP" + */
                ")";
        db.execSQL(CREATE_CONTENT_TABLE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createContentTable(db);
        createQuestionsTable(db);
        createRequestTable(db);
    }

    public void insertOwnSearchRequestInDb(String searchString) {
        Log.d("DB", "Inserting search string into DB "+searchString);
        String id = IdManager.getId();
        String lastHop = id;
        String origin = id;
        String requestId = id + "-" + UUID.randomUUID().toString() + REQUEST_ID++ ;
        Log.d("DB", "id = "+id);
        Log.d("DB", "lastHop = "+lastHop);
        Log.d("DB", "origin = "+origin);
        Log.d("DB", "reqId = "+requestId);
        insertSearchRequestInDb(requestId, lastHop, origin, searchString);
    }

    private void insertSearchRequestInDb(String requestId, String lastHop, String origin, String searchString) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(REQUEST_ID_FIELD, requestId);
        values.put(REQUEST_TEXT_FIELD, searchString);
        values.put(REQUEST_LAST_HOP_FIELD, lastHop);
        values.put(REQUEST_ORIGIN_FIELD, origin);

        database.insert(TABLE_REQUESTS, null, values);
    }

    public List<AnswerItem> getAnswersForQuestion(int questionId) {
        List<AnswerItem> answersList = new ArrayList<AnswerItem>();
        String readAnswersQuery = "SELECT * FROM " + TABLE_ANSWERS;
        SQLiteDatabase database = getReadableDatabase();

        Cursor cursor = null;
        try {
            cursor = database.rawQuery(readAnswersQuery, null);
        } catch (Exception e) {
            Log.e("DB", "Error accessing contents table");
        }
        if (cursor == null)
            return answersList;
        if (cursor.moveToFirst()) {
            do {
                AnswerItem hi = new AnswerItem();
                hi.setAnswerId(cursor.getInt(cursor.getColumnIndex(ANSWER_ID_FIELD)));
                hi.setQuestionId(cursor.getInt(cursor.getColumnIndex(QUESTION_ID_FKEY_FIELD)));
                hi.setAnswerText(cursor.getString(cursor.getColumnIndex(ANSWER_TEXT_FIELD)));
                answersList.add(hi);
            } while (cursor.moveToNext());
        }
        return answersList;
    }

    public List<HelpItem> getQuestions() {
        List<HelpItem> questionsList = new ArrayList<HelpItem>();
        String readQuestionsQuery = "SELECT * FROM " + TABLE_QUESTIONS;
        SQLiteDatabase database = getReadableDatabase();

        Cursor cursor = null;
        try {
            cursor = database.rawQuery(readQuestionsQuery, null);
        } catch (Exception e) {
            Log.e("DB", "Error accessing contents table");
        }
        if (cursor == null)
            return questionsList;
        if (cursor.moveToFirst()) {
            do {
                HelpItem hi = new HelpItem();
                hi.setQuestionId(cursor.getInt(cursor.getColumnIndex(QUESTION_ID_FIELD)));
                hi.setQuestion(cursor.getString(cursor.getColumnIndex(QUESTION_TEXT_FIELD)));
                questionsList.add(hi);
            } while (cursor.moveToNext());
        }
        return questionsList;
    }

    public List<RequestItem> getRequests(){
        List<RequestItem> requestList = new ArrayList<RequestItem>();
        String readRequestsQuery = "SELECT * FROM " + TABLE_REQUESTS;
        SQLiteDatabase database = getReadableDatabase();

        Cursor cursor = null;
        try {
            cursor = database.rawQuery(readRequestsQuery, null);
        } catch (Exception e) {
            Log.e("DB", "Error accessing contents table");
        }
        if (cursor == null)
            return requestList;
        if (cursor.moveToFirst()) {
            do {
                RequestItem ri = new RequestItem();
                ri.setRequestId(cursor.getInt(cursor.getColumnIndex(REQUEST_ID_FIELD)));
                ri.setRequest(cursor.getString(cursor.getColumnIndex(REQUEST_TEXT_FIELD)));
                ri.setLastHop(cursor.getString(cursor.getColumnIndex(REQUEST_LAST_HOP_FIELD)));
                ri.setOrigin(cursor.getString(cursor.getColumnIndex(REQUEST_ORIGIN_FIELD)));
                requestList.add(ri);
            } while (cursor.moveToNext());
        }

        return requestList;
    }

/* TODO: update getContents to real data */
    public List<ContentData> getContents() {
        List<ContentData> contentList = new ArrayList<ContentData>();
        String readContentsQuery = "SELECT * FROM " + TABLE_CONTENT;
        SQLiteDatabase database = getReadableDatabase();

        Cursor cursor = null;
        try {
            cursor = database.rawQuery(readContentsQuery, null);
        } catch (Exception e) {
            Log.e("DB", "Error accessing contents table");
        }
        if (cursor == null)
            return contentList;
        if (cursor.moveToFirst()) {
            do {
                ContentData cd = new ContentData();
                cd.setId(cursor.getInt(cursor.getColumnIndex(CONTENT_ID_FIELD)));
                cd.setDesc(cursor.getString(cursor.getColumnIndex(CONTENT_DESC_FIELD)));
                cd.setFileLocation(cursor.getString(cursor.getColumnIndex(CONTENT_FILE_LOC_FIELD)));
                cd.setFileType(cursor.getString(cursor.getColumnIndex(CONTENT_FILE_TYPE_FIELD)));
                contentList.add(cd);
            } while (cursor.moveToNext());
        }
        return contentList;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
