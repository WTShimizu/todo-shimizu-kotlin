package com.example.todo_shimizu

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {
    var userData: UserData? = null
    private var db: SQLiteDatabase? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        userData = UserData(applicationContext)
        val fragment = AddDisplay()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainPageLayout, fragment)
        transaction.commit()
    }

    fun replaceFragmentManager(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainPageLayout, fragment)
        transaction.commit()
    }

    fun insertData(com: String?, exp: String?, status: Boolean, day: String?, compday: String?) {
        var exp = exp
        if (db == null) {
            db = userData?.getWritableDatabase()
        }
        val dayMold: String?
        val compDayMold: String? = compday?.replace("/", "")
        if (day !== "") {
            dayMold = day?.replace("/", "")
        } else {
            dayMold = "0"
        }
        var statusFrag = 0
        val dbName: String
        if (exp == null) {
            exp = ""
        }
        val result: Long
        if (status) {
            statusFrag = 0
            dbName = "userdb"
            val values = ContentValues()
            values.put("title", com)
            values.put("exp", exp)
            values.put("status", statusFrag)
            values.put("day", dayMold?.toInt())
            values.put("compday", compDayMold)
            values.put("listid", 0)
            result = db?.insert(dbName, null, values)!!
        } else {
            statusFrag = 1
            dbName = "testdb"
            val values = ContentValues()
            values.put("title", com)
            values.put("exp", exp)
            values.put("status", statusFrag)
            values.put("day", dayMold?.toInt())
            values.put("listid", 0)
            result = db?.insert(dbName, null, values)!!
        }
        if (result == -1L) {
            Log.d("tag", "登録失敗")
        } else {
            Log.d("tag", "登録成功")
        }
    }

    fun readData(dbFrag: Boolean): Cursor? {
        if (db == null) {
            db = userData?.getWritableDatabase()
        }
        val readToDb: String
        val readToData: Array<String?>
        if (dbFrag) {
            readToDb = "userdb"
            readToData = arrayOf("title", "day", "exp", "status", "_id", "compday", "listid")
        } else {
            readToDb = "testdb"
            readToData = arrayOf("title", "day", "exp", "status", "_id", "listid")
        }
        val order_by = "day ASC"
        val cursor = db?.query(
                readToDb,
                readToData,
                null,
                null,
                null,
                null,
                order_by
        )
        cursor?.moveToFirst()
        return cursor
    }

    fun delete() {
        if (db == null) {
            db = userData?.getWritableDatabase()
        }
        db?.delete("testdb", null, null)
    }

    fun selectDelete(position: String?, dbFrag: Boolean) {
        val readToDb: String
        readToDb = if (dbFrag) {
            "userdb"
        } else {
            "testdb"
        }
        db?.beginTransaction()
        try {
            db?.delete(readToDb, "_id=$position", null)
            db?.setTransactionSuccessful()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            db?.endTransaction()
        }
    }

    //    public void updateuserdb(int listId) {
    //        if (db == null) {
    //            db = userData.getWritableDatabase();
    //        }
    //
    //        ContentValues cv = new ContentValues();
    //        cv.put("listid", listId);
    //        db.update("userdb", cv, "listid=" + String.valueOf(listId), null);
    //        cv = new ContentValues();
    //        cv.put("listid", listId);
    //        db.update("testdb", cv, "listid=" + String.valueOf(listId), null);
    //    }
    fun editData(com: String?, exp: String?, status: Boolean, day: String?, compday: String?, id: Int) {
        var exp = exp
        if (db == null) {
            db = userData?.getWritableDatabase()
        }
        val cv = ContentValues()
        var statusFrag = 0
        var dbName: String
        if (exp == null) {
            exp = ""
        }
        val dayMold: String?
        val compDayMold: String? = compday?.replace("/", "")
        if (day !== "") {
            dayMold = day?.replace("/", "")
        } else {
            dayMold = "0"
        }
        val readToDb: String
        if (status) {
            statusFrag = 0
            readToDb = "userdb"
            cv.put("title", com)
            cv.put("exp", exp)
            cv.put("status", statusFrag)
            cv.put("day", dayMold?.toInt())
            cv.put("compday", compDayMold?.toInt())
            db?.update(readToDb, cv, "_id = $id", null)
        } else {
            statusFrag = 1
            readToDb = "testdb"
            cv.put("title", com)
            cv.put("exp", exp)
            cv.put("status", statusFrag)
            cv.put("day", dayMold?.toInt())
            db?.update(readToDb, cv, "_id = $id", null)
        }
    }
}