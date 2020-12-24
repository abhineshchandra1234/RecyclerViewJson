package com.abhinesh.recyclerviewjson

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.abhinesh.recyclerviewjson.model.Data
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_view.view.*

class MyAdapter(private val dataList: MutableList<Data>): RecyclerView.Adapter<MyHolder>() {

    private lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
            context = parent.context
        return MyHolder(LayoutInflater.from(context).inflate(R.layout.item_view,parent,false))
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
            val data = dataList[position]
            val userFullNameTextView = holder.itemView.user_full_name
        val userAvatarImgView = holder.itemView.user_avatar
        val fullName = "${data.first_name}  ${data.last_name}"

        userFullNameTextView.text = fullName

        Picasso.get()
            .load(data.avatar)
            .into(userAvatarImgView)

        holder.itemView.setOnClickListener {
            Toast.makeText(context,fullName,Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = dataList.size

}