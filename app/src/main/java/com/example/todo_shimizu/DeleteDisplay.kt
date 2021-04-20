package com.example.todo_shimizu

import android.app.AlertDialog
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import java.util.*

class DeleteDisplay : Fragment() {
    private val db: SQLiteDatabase? = null
    private var compFrag = false
    private var compButton: TextView? = null
    private var notCompButton: TextView? = null
    private val listView: ListView? = null
    private var mainActivity: MainActivity? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.delete_page, container, false)
        mainActivity = activity as MainActivity?
        val FROM = arrayOf<String?>("title", "day", "day2")
        val TO = intArrayOf(R.id.listViewtitle, R.id.listViewSub, R.id.listViewSub2)
        val cursor = mainActivity?.readData(compFrag)
        val listView = view.findViewById<ListView?>(R.id.deleteListView)
        val data = ArrayList<MutableMap<String?, Any?>?>()
        val ids = ArrayList<Int?>()
        var map: MutableMap<String?, Any?>
        for (i in 0 until cursor?.count!!) {
            if (i > 20) {
                break
            }
            val dayMold = StringBuilder()
            if (cursor?.getInt(1) == 0) {
                dayMold.append("未入力")
            } else {
                dayMold.append(cursor?.getInt(1).toString())
                dayMold.insert(4, "/")
                dayMold.insert(7, "/")
            }
            map = HashMap()
            map["title"] = cursor?.getString(0)
            map["day"] = ""
            map["day2"] = dayMold.toString()
            data.add(map)
            ids.add(cursor?.getInt(4))
            Log.d("tag", "id" + cursor?.getInt(4) + " date  " + cursor?.getString(0) + "      :" + cursor?.getInt(1).toString() + "      :" + cursor?.getString(2) + "      :" + cursor?.getString(3))
            cursor?.moveToNext()
        }
        cursor?.close()
        val adapter = SimpleAdapter(activity, data, R.layout.notcomp_listview, FROM, TO)
        listView?.setAdapter(adapter)
        listView?.setOnItemClickListener(OnItemClickListener { adapterView, view, i, l -> // Itemクリック時
            val builder = AlertDialog.Builder(view.context)
            builder.setTitle("削除")
            builder.setMessage("削除しますか？")
            builder.setPositiveButton("OK") { dialog, which ->
                Log.d("tagid", ids[i].toString())
                mainActivity?.selectDelete(ids[i].toString(), compFrag)
                val addDisplay = AddDisplay()
                mainActivity?.replaceFragmentManager(addDisplay)
            }
            builder.setNegativeButton("キャンセル") { dialog, which -> }
            // ダイアログの表示
            val dialog = builder.create()
            dialog.show()
        })
        listView?.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(absListView: AbsListView?, i: Int) {
                if (listView?.getLastVisiblePosition() == adapter.count - 1) {
                    val position = listView.getFirstVisiblePosition()
                    val yOffset = listView.getChildAt(0).top
                    val cursor = mainActivity?.readData(compFrag)
                    val data = ArrayList<MutableMap<String?, Any?>?>()
                    var map: MutableMap<String?, Any?>
                    for (x in 0 until cursor?.count!!) {
                        if (x > adapter.count + 20) {
                            break
                        }
                        val dayMold = StringBuilder()
                        if (cursor?.getInt(1) == 0) {
                            dayMold.append("未入力")
                        } else {
                            dayMold.append(cursor?.getInt(1).toString())
                            dayMold.insert(4, "/")
                            dayMold.insert(7, "/")
                        }
                        map = HashMap()
                        if (compFrag) {
                            val compDayMold = StringBuilder()
                            compDayMold.append(cursor?.getInt(1).toString())
                            compDayMold.insert(4, "/")
                            compDayMold.insert(7, "/")
                            map["day"] = compDayMold
                        } else {
                            map["day"] = ""
                        }
                        map["title"] = cursor?.getString(0)
                        map["day2"] = dayMold.toString()
                        data.add(map)
                        Log.d("tag", "id" + cursor?.getInt(4) + " date  " + cursor?.getString(0) + "      :" + cursor?.getInt(1).toString() + "      :" + cursor?.getString(2) + "      :" + cursor?.getString(3))
                        cursor?.moveToNext()
                    }
                    cursor?.close()
                    val settingLayout: Int
                    settingLayout = if (compFrag) {
                        R.layout.listview
                    } else {
                        R.layout.notcomp_listview
                    }
                    val adapter = SimpleAdapter(activity, data, settingLayout, FROM, TO)
                    listView.setAdapter(adapter)
                    adapter.notifyDataSetChanged()
                    listView.setSelectionFromTop(position, yOffset)
                }
            }

            override fun onScroll(absListView: AbsListView?, i: Int, i1: Int, i2: Int) {}
        })

//        Button addButton = view.findViewById(R.id.addPageButton);
//        addButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 追加ボタン
//                AddDisplay addDisplay = new AddDisplay();
//                mainActivity.replaceFragmentManager(addDisplay);
//            }
//        });

//        Button deleteButton = view.findViewById(R.id.delDecButton);
//        deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 削除ボタン
//            }
//        });
        val select = ResourcesCompat.getDrawable(resources, R.drawable.select, null)
        val notSelect = ResourcesCompat.getDrawable(resources, R.drawable.not_select, null)
        notCompButton = view.findViewById<TextView?>(R.id.deleteNoButton)
        notCompButton?.setBackground(select)
        notCompButton?.setTextColor(Color.WHITE)
        notCompButton?.setOnClickListener(View.OnClickListener { v ->
            v.isSelected = !v.isSelected
            // 未完了ボタン
            if (compFrag) {
                compFrag = false
                notCompButton?.setBackground(select)
                notCompButton?.setTextColor(Color.WHITE)
                compButton?.setBackground(notSelect)
                compButton?.setTextColor(Color.GRAY)
                val cursor = mainActivity?.readData(compFrag)
                val data = ArrayList<MutableMap<String?, Any?>?>()
                var map: MutableMap<String?, Any?>
                for (i in 0 until cursor?.count!!) {
                    if (i > adapter.count + 20) {
                        break
                    }
                    val dayMold = StringBuilder()
                    if (cursor.getInt(1) == 0) {
                        dayMold.append("未入力")
                    } else {
                        dayMold.append(cursor.getInt(1).toString())
                        dayMold.insert(4, "/")
                        dayMold.insert(7, "/")
                    }
                    map = HashMap()
                    map["title"] = cursor.getString(0)
                    map["day"] = ""
                    map["day2"] = dayMold.toString()
                    data.add(map)
                    Log.d("tag", "id" + cursor.getInt(4) + " date  " + cursor.getString(0) + "      :" + cursor.getInt(1).toString() + "      :" + cursor.getString(2) + "      :" + cursor.getString(3))
                    cursor.moveToNext()
                }
                cursor.close()
                val adapter = SimpleAdapter(activity, data, R.layout.notcomp_listview, FROM, TO)
                listView?.setAdapter(adapter)
                adapter.notifyDataSetChanged()
            }
        })
        val deleteCancelButton = view.findViewById<LinearLayout?>(R.id.deleteCancelButton)
        deleteCancelButton?.setOnClickListener(View.OnClickListener {
            mainActivity = activity as MainActivity?
            val addDisplay = AddDisplay()
            mainActivity?.replaceFragmentManager(addDisplay)
        })
        compButton = view.findViewById<TextView?>(R.id.deleteCompButton)
        compButton?.setBackground(notSelect)
        compButton?.setTextColor(Color.GRAY)
        compButton?.setOnClickListener(View.OnClickListener { v ->
            v.isSelected = !v.isSelected
            // 完了ボタン
            if (!compFrag) {
                compFrag = true
                compButton?.setBackground(select)
                compButton?.setTextColor(Color.WHITE)
                notCompButton?.setBackground(notSelect)
                notCompButton?.setTextColor(Color.GRAY)
                val cursor = mainActivity?.readData(compFrag)
                val data = ArrayList<MutableMap<String?, Any?>?>()
                var map: MutableMap<String?, Any?>
                for (i in 0 until cursor?.count!!) {
                    if (i > adapter.count + 20) {
                        break
                    }
                    val dayMold = StringBuilder()
                    if (cursor.getInt(1) == 0) {
                        dayMold.append("未入力")
                    } else {
                        dayMold.append(cursor.getInt(1).toString())
                        dayMold.insert(4, "/")
                        dayMold.insert(7, "/")
                    }
                    val compDayMold = StringBuilder()
                    compDayMold.append(cursor.getInt(5).toString())
                    compDayMold.insert(4, "/")
                    compDayMold.insert(7, "/")
                    ids.add(cursor.getInt(4))
                    map = HashMap()
                    map["title"] = cursor.getString(0)
                    map["day"] = compDayMold.toString()
                    map["day2"] = dayMold.toString() // 完了済み
                    data.add(map)
                    Log.d("tag", "id" + cursor.getInt(4) + " date  " + cursor.getString(0) + "      :" + cursor.getInt(1).toString() + "      :" + cursor.getString(2) + "      :" + cursor.getString(3))
                    cursor.moveToNext()
                }
                cursor.close()
                val adapter = SimpleAdapter(activity, data, R.layout.listview, FROM, TO)
                listView?.setAdapter(adapter)
                adapter.notifyDataSetChanged()
            }
        })
        return view
    }
}