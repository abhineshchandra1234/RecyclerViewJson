package com.abhinesh.recyclerviewjson

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import com.abhinesh.recyclerviewjson.model.Data
import com.abhinesh.recyclerviewjson.model.employeeData
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.ParsedRequestListener
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val dataList: MutableList<Data> = mutableListOf()
    private val displayList: MutableList<Data> = mutableListOf()
    private lateinit var myAdapter: MyAdapter
    private lateinit var preferences: SharedPreferences



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        displayList.addAll(dataList)

        myAdapter = MyAdapter(displayList)

        //setup recycler view
        my_recycler_view.layoutManager = LinearLayoutManager(this)
        my_recycler_view.addItemDecoration(DividerItemDecoration(this,OrientationHelper.VERTICAL))
        my_recycler_view.adapter = myAdapter

        preferences = getSharedPreferences("My Pref", Context.MODE_PRIVATE)
        val mSortedSetting = preferences.getString("Sort","Ascending")

        if (mSortedSetting == "Ascending") {
            sortAscending(myAdapter)
        } else if (mSortedSetting == "Descending") {
            sortDescending(myAdapter)
        }




        //setup Android Networking
        AndroidNetworking.initialize(this)

        AndroidNetworking.get("https://reqres.in/api/users?page=2")
            .build()
            .getAsObject(employeeData::class.java, object : ParsedRequestListener<employeeData>{
                override fun onResponse(response: employeeData?) {
                    response?.data?.let { dataList.addAll(it) }
                    displayList.addAll(dataList)
                    myAdapter.notifyDataSetChanged()
                }

                override fun onError(anError: ANError?) {

                }
            })
    }

    private fun sortDescending(myAdapter: MyAdapter) {
        displayList.sortWith(compareBy{it.first_name})
        displayList.reverse()
        myAdapter.notifyDataSetChanged()
    }

    private fun sortAscending(myAdapter: MyAdapter) {
            displayList.sortWith(compareBy{it.first_name})
            myAdapter.notifyDataSetChanged()
    }

    //setup menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        val menuItem = menu!!.findItem(R.id.search)

        if (menuItem!=null)  {
            val searchView = menuItem.actionView as SearchView

            val editText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
            editText.hint = "Search..."

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (newText!!.isNotEmpty()) {
                        displayList.clear()
                        val search = newText.toLowerCase(Locale.getDefault())
                        dataList.forEach {
                            if (it.first_name.toLowerCase(Locale.getDefault()).contains(search)) {
                                displayList.add(it)
                            }
                        }

                        my_recycler_view.adapter!!.notifyDataSetChanged()
                    }

                    else {
                        displayList.clear()
                        displayList.addAll(dataList)
                        my_recycler_view.adapter!!.notifyDataSetChanged()
                    }
                    return true
                }

            })
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == R.id.sorting) {
            sortDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun sortDialog() {
        val options = arrayOf("Ascending", "Descending")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Sort By")
        builder.setIcon(R.drawable.ic_sort)
        builder.setItems(options) {dialog, which ->

            if (which == 0) {
                val editor : SharedPreferences.Editor = preferences.edit()
                editor.putString("Sort","Ascending")
                editor.apply()
                sortAscending(myAdapter)
                Toast.makeText(this@MainActivity, "Ascending Order", Toast.LENGTH_LONG).show()
            }
            if (which == 1) {
                val editor : SharedPreferences.Editor = preferences.edit()
                editor.putString("Sort","Descending")
                editor.apply()
                sortDescending(myAdapter)
                Toast.makeText(this@MainActivity, "Descending Order", Toast.LENGTH_LONG).show()
            }
        }
        builder.create().show()
    }
}