package com.example.xbiztask2.adapter

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.xbiztask2.R
import com.example.xbiztask2.model.PostOfficeItem

class PinAdapter(var list: List<PostOfficeItem>) : RecyclerView.Adapter<PinAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pin, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = list[position]
        if (TextUtils.isEmpty(data.name.toString())) {
            holder.tv_name.visibility = View.GONE
            holder.tv_name_value.visibility = View.GONE
        } else {
            holder.tv_name.visibility = View.VISIBLE
            holder.tv_name_value.visibility = View.VISIBLE
            holder.tv_name_value.text = data.name
        }
       /* if (data.description.toString() == null || TextUtils.isEmpty(data.description.toString())) {
            holder.tv_description_value.visibility = View.GONE
            holder.tv_description.visibility = View.GONE
        } else {
            holder.tv_description_value.visibility = View.VISIBLE
            holder.tv_description.visibility = View.VISIBLE
            holder.tv_description_value.text = data.description.toString()
        }*/
      //  holder.tv_description_value.text = data.description.toString()

        if (TextUtils.isEmpty(data.branchType.toString())) {
            holder.tv_branch.visibility = View.GONE
            holder.tv_branch_value.visibility = View.GONE
        } else {
            holder.tv_branch.visibility = View.VISIBLE
            holder.tv_branch_value.visibility = View.VISIBLE
            holder.tv_branch_value.text = data.branchType.toString()
        }
        if (TextUtils.isEmpty(data.deliveryStatus.toString())) {
            holder.tv_delivery.visibility = View.GONE
            holder.tv_delivery_value.visibility = View.GONE
        } else {
            holder.tv_delivery.visibility = View.VISIBLE
            holder.tv_delivery_value.visibility = View.VISIBLE
            holder.tv_delivery_value.text = data.deliveryStatus.toString()

        }
        holder.tv_circle_district_value.text =
            "${data?.circle.toString()}, ${data?.district.toString()}"
        holder.tv_division_region_value.text =
            "${data?.division.toString()}, ${data?.region.toString()}"
        holder.tv_block_state_value.text = "${data?.block.toString()}, ${data?.state.toString()}"
        holder.tv_country_pin_value.text =
            "${data?.country.toString()}, ${data?.pincode.toString()}"


    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tv_name: TextView = itemView.findViewById(R.id.tv_name)
        var tv_name_value: TextView = itemView.findViewById(R.id.tv_name_value)
        var tv_branch: TextView = itemView.findViewById(R.id.tv_branch)
        var tv_branch_value: TextView = itemView.findViewById(R.id.tv_branch_value)
        var tv_delivery: TextView = itemView.findViewById(R.id.tv_delivery)
        var tv_delivery_value: TextView = itemView.findViewById(R.id.tv_delivery_value)
        var tv_circle_district_value: TextView = itemView.findViewById(R.id.tv_circle_district_value)
        var tv_division_region_value: TextView = itemView.findViewById(R.id.tv_division_region_value)
        var tv_block_state_value: TextView = itemView.findViewById(R.id.tv_block_state_value)
        var tv_country_pin_value: TextView = itemView.findViewById(R.id.tv_country_pin_value)
    }
}