package com.woojun.ai.util

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import com.woojun.ai.R

object FilterOptionUtil {

    fun ArrayList<AiResult>.filterAdults(): ArrayList<AiResult> {
        return ArrayList(this.filter { it.ageNow ?: 0 >= 19 })
    }

    fun ArrayList<AiResult>.filterChildren(): ArrayList<AiResult> {
        return ArrayList(this.filter { it.ageNow ?: 0 < 19 })
    }

    fun showPopupMenu(context: Context, view: View, function: (Int) -> Unit) {
        val popupMenu = PopupMenu(context, view, 0, 0, R.style.CustomPopupMenu)
        popupMenu.inflate(R.menu.filter_menu)

        val selectedItemId = getSelectedItem(context)
        if (selectedItemId != -1) {
            val selectedItem = popupMenu.menu.findItem(selectedItemId)
            setColorForMenuItem(selectedItem, "#4894fe")
        }

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.children, R.id.adult, R.id.all -> {
                    // 선택된 아이템 저장
                    setSelectedItem(context, it.itemId)
                    function(it.itemId)
                    true
                }
                else -> false
            }
        }

        popupMenu.show()
    }

    private fun setColorForMenuItem(item: MenuItem, color: String) {
        val spannableString = SpannableString(item.title)
        spannableString.setSpan(ForegroundColorSpan(Color.parseColor(color)), 0, spannableString.length, 0)
        item.title = spannableString
    }

    private fun setSelectedItem(context: Context, itemId: Int) {
        val sharedPreferences = context.getSharedPreferences("popup_menu_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putInt("selected_item", itemId).apply()
    }

    fun getSelectedItem(context: Context): Int {
        val sharedPreferences = context.getSharedPreferences("popup_menu_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getInt("selected_item", R.id.children)
    }


}
