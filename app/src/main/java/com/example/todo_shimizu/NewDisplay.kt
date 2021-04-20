package com.example.todo_shimizu

import android.app.DatePickerDialog.OnDateSetListener
import android.content.Context
import android.database.sqlite.SQLiteDatabase
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
import com.example.todo_shimizu.NewDisplay
import java.util.*

class NewDisplay : Fragment(), OnDateSetListener {
    //    private Button dateButton;
    private val db: SQLiteDatabase? = null
    private val userData: UserData? = null
    private var dayText: String? = null
    private var statusFrag = false
    private var mYear = 0
    private val mMonth = 0
    private val mDay = 0
    private var newView: View? = null
    private var notCompButton: TextView? = null
    private var compButton: TextView? = null
    var mDateSetListener: OnDateSetListener? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        newView = inflater.inflate(R.layout.new_page, container, false)
        return newView
    }

    override fun onStart() {
        super.onStart()
        val title = newView?.findViewById<EditText?>(R.id.titleEdit)
        val date = Calendar.getInstance(TimeZone.getTimeZone("Asia/Tokyo"))
        val dateButton = newView?.findViewById<TextView?>(R.id.dateButton)
        var month: String? = null
        month = if ((date[Calendar.MONTH] + 1).toString().length == 1) {
            "0" + (date[Calendar.MONTH] + 1).toString()
        } else {
            (date[Calendar.MONTH] + 1).toString()
        }
        var day: String? = null
        day = if (date[Calendar.DAY_OF_MONTH].toString().length == 1) {
            "0" + date[Calendar.DAY_OF_MONTH].toString()
        } else {
            date[Calendar.DAY_OF_MONTH].toString()
        }


//        dateButton.setText(String.valueOf(date.get(Calendar.YEAR)) +
//                "/" + month + "/" + day);
        dateButton?.setOnClickListener(View.OnClickListener {
            val newFragment: DialogFragment = DatePick()
            newFragment.setTargetFragment(this@NewDisplay, 0)
            newFragment.show(fragmentManager!!, "datePicker")
        })
        val dateIcon = newView?.findViewById<ImageView?>(R.id.dateIcon)
        dateIcon?.setOnClickListener(View.OnClickListener {
            val newFragment: DialogFragment = DatePick()
            newFragment.setTargetFragment(this@NewDisplay, 0)
            newFragment.show(fragmentManager!!, "datePicker")
        })
        val layout = newView?.findViewById<FrameLayout?>(R.id.hideLayout)
        val mainActivity = activity as MainActivity?
        val inputMethodManager = mainActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        //        layout.dispatchDependentViewsChanged(new );
        layout?.setOnTouchListener(OnTouchListener { view, motionEvent ->
            inputMethodManager.hideSoftInputFromWindow(layout.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
            layout?.requestFocus()
            false
        })
        val lefSelect = ResourcesCompat.getDrawable(resources, R.drawable.lef_select, null)
        val lefNotSelect = ResourcesCompat.getDrawable(resources, R.drawable.lef_not_select, null)
        val rigSelect = ResourcesCompat.getDrawable(resources, R.drawable.rig_select, null)
        val rigNotSelect = ResourcesCompat.getDrawable(resources, R.drawable.rig_not_select, null)
        notCompButton = newView?.findViewById<TextView?>(R.id.notCompButton)
        notCompButton?.setOnClickListener(View.OnClickListener {
            if (statusFrag) {
                statusFrag = false
                notCompButton?.setBackground(lefSelect)
                notCompButton?.setTextColor(Color.WHITE)
                compButton?.setBackground(rigNotSelect)
                compButton?.setTextColor(Color.GRAY)
            }
        })
        compButton = newView?.findViewById<TextView?>(R.id.compButton)
        compButton?.setOnClickListener(View.OnClickListener {
            if (!statusFrag) {
                statusFrag = true
                compButton?.setBackground(rigSelect)
                compButton?.setTextColor(Color.WHITE)
                notCompButton?.setBackground(lefNotSelect)
                notCompButton?.setTextColor(Color.GRAY)
            }
        })
        notCompButton?.setBackground(lefSelect)
        notCompButton?.setTextColor(Color.WHITE)
        compButton?.setBackground(rigNotSelect)
        compButton?.setTextColor(Color.GRAY)
        val endButton = newView?.findViewById<TextView?>(R.id.addButton)
        endButton?.setOnClickListener(View.OnClickListener {
            inputMethodManager.hideSoftInputFromWindow(layout?.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
            layout?.requestFocus()
            Log.d("tag", title?.getText().toString())
            if (title?.getText().toString().length != 0) {
                // タイトルが未記入でない場合
                val titleText = title?.getText().toString()
                val detailsEdit = newView?.findViewById<TextView?>(R.id.detailsEdit)
                val exp = detailsEdit?.getText().toString()
                dayText = dateButton?.getText().toString()
                val mainActivity = activity as MainActivity?
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
                mainActivity?.insertData(titleText, exp, statusFrag, dayText, compDay)
                val addDisplay = AddDisplay()
                mainActivity?.replaceFragmentManager(addDisplay)
            } else {
                val validation = newView?.findViewById<TextView?>(R.id.addValidation)
                validation?.setText("タイトルは必ず入力してください")
            }
        })
        val cancelButton = newView?.findViewById<LinearLayout?>(R.id.cancelButton)
        cancelButton?.setOnClickListener(View.OnClickListener {
            inputMethodManager.hideSoftInputFromWindow(layout?.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
            layout?.requestFocus()
            val mainActivity = activity as MainActivity?
            val addDisplay = AddDisplay()
            mainActivity?.replaceFragmentManager(addDisplay)
        })
    }

    fun updateDisplay(year: Int, month: String?, day: String?) {
        val dateButton = newView?.findViewById<TextView?>(R.id.dateButton)
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
    } //    public void showDatePickerDialog(View v) {
    //        DialogFragment newFragment = new DatePick();
    //        newFragment.show(getFragmentManager(), "datePicker");
    //    }
}