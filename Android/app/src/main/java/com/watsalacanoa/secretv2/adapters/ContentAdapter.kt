package com.watsalacanoa.secretv2.adapters

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.CardView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.watsalacanoa.secretv2.R
import java.util.*


class Content(mContext: Context, listElements: ArrayList<String>)
    : ArrayAdapter<String>(mContext,0, listElements){

    private val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val rnd = Random()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val element = getItem(position)



        var listItem = convertView
        if(listItem == null) {
            listItem = inflater.inflate(R.layout.post, parent, false)
            val r = rnd.nextInt(256)
            val g = rnd.nextInt(256)
            val b = rnd.nextInt(256)
            val brightness = getBrightness(r,g,b)

            val card = listItem!!.findViewById<CardView>(R.id.CardView)
            card.setCardBackgroundColor(Color.argb(255,r,g,b))
            val content = listItem!!.findViewById<TextView>(R.id.content)

            if(brightness <= 120){
                content.setTextColor("#FFFFFF".toColor())
            }
        }

        val contentText = listItem!!.findViewById<TextView>(R.id.content)
        contentText.text = element
        return listItem
    }

    fun String.toColor(): Int = Color.parseColor(this)

    fun getBrightness(r: Int, g: Int, b: Int):Int{
        return ((r * 299) + (g * 587) + (b * 114)) / 1000
    }

    fun getBrightColor():Triple<Int, Int, Int>{
        val r = rnd.nextInt(256)
        val g = rnd.nextInt(256)
        val b = rnd.nextInt(256)
        return Triple(r,g,b)
    }
}
