package com.andlill.jld.app.collectiondetails.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.andlill.jld.R
import com.andlill.jld.model.Collection
import com.andlill.jld.utils.AppUtils
import java.util.*

class CollectionTouchAdapter(context: Context, adapter: RecyclerView.Adapter<*>, collection: Collection, dragDir: Int, swipeDir: Int, callback: () -> Unit) : ItemTouchHelper(object: SimpleCallback(dragDir, swipeDir) {

    private val background = ColorDrawable(ContextCompat.getColor(context, R.color.red_800))
    private val icon = ContextCompat.getDrawable(context, R.drawable.ic_delete) as Drawable

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)

        if (actionState == ACTION_STATE_DRAG or ACTION_STATE_SWIPE)
            AppUtils.vibrate(context, 10)

    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        val fromPosition = viewHolder.adapterPosition
        val toPosition = target.adapterPosition

        // Update view holders with new positions.
        viewHolder.itemView.findViewById<TextView>(R.id.text_index).text = String.format("%d.", toPosition + 1)
        target.itemView.findViewById<TextView>(R.id.text_index).text = String.format("%d.", fromPosition + 1)
        // Update collections and adapter.
        Collections.swap(collection.content, fromPosition, toPosition)
        adapter.notifyItemMoved(fromPosition, toPosition)

        callback()
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition

        // Update collections and adapter.
        collection.content.removeAt(position)
        adapter.notifyDataSetChanged()
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        // Setup size of icon.
        val view = viewHolder.itemView
        val iconMargin = (view.height - icon.intrinsicHeight) / 2
        val iconTop = view.top + (view.height - icon.intrinsicHeight) / 2
        val iconBottom = iconTop + icon.intrinsicHeight

        if (isCurrentlyActive && actionState == ACTION_STATE_DRAG)
            ViewCompat.setElevation(view, 25f)

        when {
            // Swipe direction: ItemTouchHelper.END
            dX > 0 -> {
                val iconLeft = view.left + iconMargin
                val iconRight = view.left + iconMargin + icon.intrinsicWidth
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                background.setBounds(view.left, view.top, view.left + dX.toInt(), view.bottom)
                background.draw(c)
                icon.draw(c)
            }
            // Swipe direction: ItemTouchHelper.START
            dX < 0 -> {
                val iconLeft = view.right - iconMargin - icon.intrinsicWidth
                val iconRight = view.right - iconMargin
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                background.setBounds(view.right + dX.toInt(), view.top, view.right, view.bottom)
                background.draw(c)
                icon.draw(c)
            }
            else -> {
                background.setBounds(0, 0, 0, 0)
                background.draw(c)
            }
        }
    }

})