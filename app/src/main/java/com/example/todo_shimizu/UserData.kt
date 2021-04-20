package com.example.todo_shimizu

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class UserData internal constructor(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
                SQL_CREATE_ENTRIES
        )
        db?.execSQL(
                SQL_CREATE_ENTRIES2
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(
                SQL_DELETE_ENTRIES
        )
        db?.execSQL(
                SQL_DELETE_ENTRIES2
        )
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private val DATABASE_NAME: String? = "TestDB.db"
        private val TABLE_NAME: String? = "testdb"
        private val TABLE_NAME2: String? = "userdb"
        private val _ID: String? = "_id"
        private val LIST_ID: String? = "listid"
        private val COLUMN_NAME_TITLE: String? = "title"
        private val COLUMN_NAME_SUBTITLE: String? = "day"
        private val COLUMN_NAME_SUBTITLE2: String? = "compday"
        private val COLUMN_NAME_EXP: String? = "exp"
        private val COLUMN_NAME_STATUS: String? = "status"
        private val SQL_CREATE_ENTRIES: String? = "CREATE TABLE " + TABLE_NAME + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                LIST_ID + " INTEGER," +
                COLUMN_NAME_TITLE + " TEXT," +
                COLUMN_NAME_EXP + " TEXT," +
                COLUMN_NAME_STATUS + " INTEGER," +
                COLUMN_NAME_SUBTITLE + " INTEGER)"
        private val SQL_DELETE_ENTRIES: String? = "DROP TABLE IF EXISTS $TABLE_NAME"
        private val SQL_CREATE_ENTRIES2: String? = "CREATE TABLE " + TABLE_NAME2 + " (" +
                _ID + " INTEGER PRIMARY KEY," +
                LIST_ID + " INTEGER," +
                COLUMN_NAME_TITLE + " TEXT," +
                COLUMN_NAME_EXP + " TEXT," +
                COLUMN_NAME_STATUS + " INTEGER," +
                COLUMN_NAME_SUBTITLE2 + " INTEGER," +
                COLUMN_NAME_SUBTITLE + " INTEGER)"
        private val SQL_DELETE_ENTRIES2: String? = "DROP TABLE IF EXISTS $TABLE_NAME"
    }
}