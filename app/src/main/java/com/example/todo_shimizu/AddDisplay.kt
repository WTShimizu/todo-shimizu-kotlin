package com.example.todo_shimizu

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

class AddDisplay : Fragment() {
    private val db: SQLiteDatabase? = null
    private var compFrag = false
    private var compButton: TextView? = null
    private var notCompButton: TextView? = null
    private var mainActivity: MainActivity? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.add_page, container, false)
        mainActivity = activity as MainActivity?
        val FROM = arrayOf<String?>("title", "day", "day2")
        val TO = intArrayOf(R.id.listViewtitle, R.id.listViewSub, R.id.listViewSub2)
        val cursor = mainActivity?.readData(compFrag)
        val listView = view?.findViewById<ListView?>(R.id.addListView)
        val data = ArrayList<MutableMap<String?, Any?>?>()
        val ids = ArrayList<Int?>()
        var map: MutableMap<String?, Any?>
        for (i in 0 until cursor?.count!!) {
            if (i > 20) {
                break
            }
            //            mainActivity.updateId(i+1);
            val dayMold = StringBuilder()
            if (cursor.getInt(1) == 0) {
                dayMold.append("未入力")
            } else {
                dayMold.append(cursor.getInt(1).toString())
                dayMold.insert(4, "/")
                dayMold.insert(7, "/")
            }
            ids.add(cursor.getInt(4))
            map = HashMap()
            map["title"] = cursor.getString(0)
            map["day"] = "未完了"
            map["day2"] = dayMold.toString() // 完了済み
            data.add(map)
            Log.d("tag", "listid" + cursor.getInt(4) + " date  " + cursor.getString(0) + "      :" + cursor.getInt(1).toString() + "      :" + cursor.getString(2) + "      :" + cursor.getString(3))
            cursor.moveToNext()
        }
        cursor.close()
        val adapter = SimpleAdapter(activity, data, R.layout.listview, FROM, TO)
        listView?.setAdapter(adapter)
        listView?.setOnItemClickListener(OnItemClickListener { adapterView, view, i, l -> // Itemクリック時
            val bundle = Bundle()
            bundle.putBoolean("FRAG", compFrag)
            ids[i]?.let { bundle.putInt("_ID", it) }
            Log.d("tag", "編集先ID" + ids[i].toString())
            val editDisplay = EditDisplay()
            editDisplay.arguments = bundle
            mainActivity?.replaceFragmentManager(editDisplay)
        })
        listView?.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(absListView: AbsListView?, i: Int) {
                if (listView.getLastVisiblePosition() == adapter.count - 1) {
                    val data = ArrayList<MutableMap<String?, Any?>?>()
                    val position = listView.getFirstVisiblePosition()
                    val yOffset = listView.getChildAt(0).top
                    val cursor = mainActivity?.readData(compFrag)
                    var map: MutableMap<String?, Any?>
                    for (x in 0 until cursor?.count!!) {
                        if (x > adapter.count + 20) {
                            break
                        }
                        val dayMold = StringBuilder()
                        if (cursor?.getInt(1) == 0) {
                            dayMold.append("未入力")
                        } else {
                            dayMold.append(cursor.getInt(1).toString())
                            dayMold.insert(4, "/")
                            dayMold.insert(7, "/")
                        }
                        map = HashMap()
                        if (compFrag) {
                            val compDayMold = StringBuilder()
                            compDayMold.append(cursor.getInt(1).toString())
                            compDayMold.insert(4, "/")
                            compDayMold.insert(7, "/")
                            map["day"] = compDayMold
                        } else {
                            map["day"] = "未完了"
                        }
                        map["title"] = cursor.getString(0)
                        map["day2"] = dayMold.toString() // 完了済み
                        data.add(map)
                        ids.add(cursor.getInt(4))
                        Log.d("tag", "id" + cursor.getInt(4) + " date  " + cursor.getString(0) + "      :" + cursor.getInt(1).toString() + "      :" + cursor.getString(2) + "      :" + cursor.getString(3))
                        cursor.moveToNext()
                    }
                    cursor.close()
                    val settingLayout = R.layout.listview
                    val adapter = SimpleAdapter(activity, data, settingLayout, FROM, TO)
                    listView.setAdapter(adapter)
                    adapter.notifyDataSetChanged()
                    listView.setSelectionFromTop(position, yOffset)
                }
            }

            override fun onScroll(absListView: AbsListView?, i: Int, i1: Int, i2: Int) {}
        })
        val addButton = view?.findViewById<ImageView?>(R.id.rNewButton)
        addButton?.setOnClickListener(View.OnClickListener { mainActivity?.replaceFragmentManager(NewDisplay()) })
        val deleteButton = view?.findViewById<ImageView?>(R.id.rDeleteButton)
        deleteButton?.setOnClickListener(View.OnClickListener { // 削除ボタン
            mainActivity?.replaceFragmentManager(DeleteDisplay())
        })
        val select = ResourcesCompat.getDrawable(resources, R.drawable.select, null)
        val notSelect = ResourcesCompat.getDrawable(resources, R.drawable.not_select, null)
        notCompButton = view?.findViewById<TextView?>(R.id.addNoButton)
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
                ids.clear()
                for (i in 0 until cursor?.count!!) {
                    if (i > 20) {
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
                    ids.add(cursor.getInt(4))
                    map = HashMap()
                    map["title"] = cursor.getString(0)
                    map["day"] = "未完了"
                    map["day2"] = dayMold.toString()
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
        compButton = view?.findViewById<TextView?>(R.id.addCompButton)
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
                ids.clear()
                for (i in 0 until cursor?.count!!) {
                    if (i > 20) {
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
                    cursor?.moveToNext()
                }
                cursor?.close()
                val adapter = SimpleAdapter(activity, data, R.layout.listview, FROM, TO)
                listView?.setAdapter(adapter)
                adapter.notifyDataSetChanged()
            }
        })
        return view
    }
}