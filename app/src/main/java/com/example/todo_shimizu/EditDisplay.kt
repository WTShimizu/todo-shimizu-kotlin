package com.example.todo_shimizu

import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.example.todo_shimizu.EditDisplay
import java.util.*

class EditDisplay : Fragment(), OnDateSetListener {
    private var dayText: String? = null
    private var statusFrag = false
    private var mYear = 0
    private val mMonth = 0
    private val mDay = 0
    private var editView: View? = null
    private var notCompButton: TextView? = null
    private var compButton: TextView? = null
    var mDateSetListener: OnDateSetListener? = null
    private var status = false
    private var mId = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        editView = inflater.inflate(R.layout.edit_page, container, false)
        return editView
    }

    override fun onStart() {
        super.onStart()
        val bundle = arguments
        if (bundle != null) {
            status = bundle.getBoolean("FRAG")
            mId = bundle.getInt("_ID")
            Log.d("tag", "編集ID$mId")
        }
        statusFrag = status
        var mainActivity = activity as MainActivity?
        val cursor = mainActivity?.readData(status)
        Log.d("tag", cursor?.count.toString())
        for (i in 0 until cursor?.count!!) {
            Log.d("apptag", "listid" + cursor.getInt(4) + " date  " + cursor.getString(0) + "      :" + cursor.getInt(1).toString() + "      :" + cursor.getString(2) + "      :" + cursor.getString(3))
            if (cursor.getInt(4) == mId) {
                break
            }
            cursor.moveToNext()
        }
        val title = editView?.findViewById<EditText?>(R.id.editTitleEdit)
        title?.setText(cursor.getString(0))
        val dateButton = editView?.findViewById<TextView?>(R.id.editDateButton)
        val dayMold = StringBuilder()
        if (cursor.getInt(1) == 0) {
            dateButton?.setText("")
        } else {
            dayMold.append(cursor.getInt(1).toString())
            dayMold.insert(4, "/")
            dayMold.insert(7, "/")
            dateButton?.setText(dayMold.toString())
        }
        val detailsEdit = editView?.findViewById<EditText?>(R.id.editDetailsEdit)
        detailsEdit?.setText(cursor.getString(2))
        dateButton?.setOnClickListener(View.OnClickListener {
            val newFragment: DialogFragment = DatePick()
            newFragment.setTargetFragment(this@EditDisplay, 0)
            fragmentManager?.let { it1 -> newFragment.show(it1, "datePicker") }
        })
        val dateIcon = editView?.findViewById<ImageView?>(R.id.editDateIcon)
        dateIcon?.setOnClickListener(View.OnClickListener {
            val newFragment: DialogFragment = DatePick()
            newFragment.setTargetFragment(this@EditDisplay, 0)
            fragmentManager?.let { it1 -> newFragment.show(it1, "datePicker") }
        })
        val layout = editView?.findViewById<FrameLayout?>(R.id.editHideLayout)
        mainActivity = activity as MainActivity?
        val inputMethodManager = mainActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        //        layout.dispatchDependentViewsChanged(new );
        layout?.setOnTouchListener(OnTouchListener { view, motionEvent ->
            inputMethodManager.hideSoftInputFromWindow(layout?.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
            layout?.requestFocus()
            false
        })
        val lefSelect = ResourcesCompat.getDrawable(resources, R.drawable.lef_select, null)
        val lefNotSelect = ResourcesCompat.getDrawable(resources, R.drawable.lef_not_select, null)
        val rigSelect = ResourcesCompat.getDrawable(resources, R.drawable.rig_select, null)
        val rigNotSelect = ResourcesCompat.getDrawable(resources, R.drawable.rig_not_select, null)
        notCompButton = editView?.findViewById<TextView?>(R.id.editNotCompButton)
        notCompButton?.setOnClickListener(View.OnClickListener {
            if (statusFrag) {
                statusFrag = false
                notCompButton?.setBackground(lefSelect)
                notCompButton?.setTextColor(Color.WHITE)
                compButton?.setBackground(rigNotSelect)
                compButton?.setTextColor(Color.GRAY)
            }
        })
        compButton = editView?.findViewById<TextView?>(R.id.editCompButton)
        compButton?.setOnClickListener(View.OnClickListener {
            if (!statusFrag) {
                statusFrag = true
                compButton?.setBackground(rigSelect)
                compButton?.setTextColor(Color.WHITE)
                notCompButton?.setBackground(lefNotSelect)
                notCompButton?.setTextColor(Color.GRAY)
            }
        })
        if (status) {
            compButton?.setBackground(rigSelect)
            compButton?.setTextColor(Color.WHITE)
            notCompButton?.setBackground(lefNotSelect)
            notCompButton?.setTextColor(Color.GRAY)
        } else {
            notCompButton?.setBackground(lefSelect)
            notCompButton?.setTextColor(Color.WHITE)
            compButton?.setBackground(rigNotSelect)
            compButton?.setTextColor(Color.GRAY)
        }
        val endButton = editView?.findViewById<TextView?>(R.id.editAddButton)
        endButton?.setOnClickListener(View.OnClickListener {
            Log.d("tag", title?.getText().toString())
            if (title?.getText().toString().length != 0) {
                // タイトルが未記入でない場合
                val titleText = title?.getText().toString()
                val exp = detailsEdit?.getText().toString()
                dayText = dateButton?.getText().toString()
                val mainActivity = activity as MainActivity?
                val date = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"))
                val month: String
                month = if ((date[Calendar.MONTH] + 1).toString().length == 1) {
                    "0" + (date[Calendar.MONTH] + 1).toString()
                } else {
                    (date[Calendar.MONTH] + 1).toString()
                }
                val day: String
                day = if (date[Calendar.DAY_OF_MONTH].toString().length == 1) {
                    "0" + date[Calendar.DAY_OF_MONTH].toString()
                } else {
                    date[Calendar.DAY_OF_MONTH].toString()
                }
                val compDay = date[Calendar.YEAR].toString() +
                        "/" + month + "/" + day
                if (status != statusFrag) {
                    mainActivity?.insertData(titleText, exp, statusFrag, dayText, compDay)
                    mainActivity?.selectDelete(cursor.getInt(4).toString(), status)
                } else {
                    mainActivity?.editData(titleText, exp, status, dayText, compDay, cursor.getInt(4))
                }
                val addDisplay = AddDisplay()
                mainActivity?.replaceFragmentManager(addDisplay)
            } else {
                val validation = editView?.findViewById<TextView?>(R.id.editValidation)
                validation?.setText("タイトルは必ず入力してください")
            }
        })
        val cancelButton = editView?.findViewById<LinearLayout?>(R.id.editCancelButton)
        cancelButton?.setOnClickListener(View.OnClickListener {
            val mainActivity = activity as MainActivity?
            val addDisplay = AddDisplay()
            mainActivity?.replaceFragmentManager(addDisplay)
        })
    }

    fun updateDisplay(year: Int, month: String?, day: String?) {
        val dateButton = editView?.findViewById<TextView?>(R.id.editDateButton)
        dateButton?.setText("$year/$month/$day")
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        mYear = year
        val monthText: String
        monthText = if (month.toString().length == 1) {
            "0" + (month + 1).toString()
        } else {
            month.toString()
        }
        val dayText: String
        dayText = if (dayOfMonth.toString().length == 1) {
            "0$dayOfMonth"
        } else {
            dayOfMonth.toString()
        }
        updateDisplay(mYear, monthText, dayText)
    }
}