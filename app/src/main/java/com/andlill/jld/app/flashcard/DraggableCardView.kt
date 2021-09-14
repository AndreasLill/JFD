package com.andlill.jld.app.flashcard

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import androidx.cardview.widget.CardView

class DraggableCardView(context: Context, attributeSet: AttributeSet?) : CardView(context, attributeSet) {

    companion object {
        const val DIRECTION_LEFT = 1
        const val DIRECTION_RIGHT = 2
    }

    interface OnDragCardListener {
        // Called when view is dragged.
        fun onDragView()
        // Called when view is released from drag.
        fun onReleaseView()
        // Progress towards dismiss threshold from start position.
        fun onMoveLeft(progress: Float)
        // Progress towards dismiss threshold from start position.
        fun onMoveRight(progress: Float)
        // Called when the card is released outside the dismiss threshold.
        fun onDismiss(direction: Int)
        // Called when the card is released within the dismiss threshold.
        fun onReset()
    }

    // Listener
    private lateinit var onDragCardListener: OnDragCardListener

    // Config
    private var dismissThreshold = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 96f, this.context.resources.displayMetrics)
    private var dragThreshold = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, this.context.resources.displayMetrics)

    // Position variables.
    private var isDragged: Boolean = false
    private var dragStartX: Float = 0f
    private var dragStartY: Float = 0f
    private var offsetX: Float = 0f
    private var offsetY: Float = 0f
    private var touchX: Float = 0f
    private var touchY: Float = 0f

    fun setOnDragCardListener(listener: OnDragCardListener) {
        onDragCardListener = listener
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val view = this
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                // Register start position.
                dragStartX = event.rawX
                dragStartY = event.rawY
                touchX = view.x - event.rawX
                touchY = view.y - event.rawY
            }
            MotionEvent.ACTION_UP -> {
                // Perform click if card is not being dragged.
                if (!view.isDragged) {
                    this.performClick()
                }

                if (event.rawX > (dragStartX + dismissThreshold) || event.rawX < (dragStartX - dismissThreshold)) {
                    var direction = 0

                    // Dismiss direction.
                    if (event.rawX - dragStartX > 0)
                        direction = DIRECTION_RIGHT
                    if (event.rawX - dragStartX < 0)
                        direction = DIRECTION_LEFT

                    // Call listener dismiss.
                    view.animate().setDuration(400).translationX(view.x * 4).translationY(view.y * 4).setListener(object: Animator.AnimatorListener {
                        override fun onAnimationStart(animator: Animator?) {}
                        override fun onAnimationEnd(animator: Animator?) { onDragCardListener.onDismiss(direction) }
                        override fun onAnimationCancel(animator: Animator?) {}
                        override fun onAnimationRepeat(animator: Animator?) {}
                    }).start()
                }
                else {
                    // Reset position to original position.
                    view.animate().setDuration(200).x(0f).y(0f).setListener(object: Animator.AnimatorListener {
                        override fun onAnimationStart(animator: Animator?) {}
                        override fun onAnimationEnd(animator: Animator?) { onDragCardListener.onReset() }
                        override fun onAnimationCancel(animator: Animator?) {}
                        override fun onAnimationRepeat(animator: Animator?) {}
                    }).start()
                }
                // Release view.
                view.isDragged = false
                if (this::onDragCardListener.isInitialized)
                    onDragCardListener.onReleaseView()
            }
            MotionEvent.ACTION_MOVE -> {
                if (!view.isDragged) {
                    if (event.rawX > (dragStartX + dragThreshold) ||
                        event.rawX < (dragStartX - dragThreshold) ||
                        event.rawY > (dragStartY + dragThreshold) ||
                        event.rawY < (dragStartY - dragThreshold)) {

                        // Calculate offset from drag threshold to keep it smooth.
                        offsetX = event.rawX - dragStartX
                        offsetY = event.rawY - dragStartY
                        // Drag view.
                        view.isDragged = true
                        if (this::onDragCardListener.isInitialized)
                            onDragCardListener.onDragView()
                    }
                } else if (view.isDragged) {

                    if (event.rawX - dragStartX > 0 && this::onDragCardListener.isInitialized) {
                        // Right
                        // Calculate progress towards dismiss threshold from the right.
                        val progress = (event.rawX - dragStartX) / dismissThreshold
                        onDragCardListener.onMoveRight(progress)
                    }
                    else if (event.rawX - dragStartX < 0 && this::onDragCardListener.isInitialized) {
                        // Left
                        // Calculate progress towards dismiss threshold from the left.
                        val progress = ((event.rawX - dragStartX) / dismissThreshold) * -1
                        onDragCardListener.onMoveLeft(progress)
                    }

                    // Update position.
                    view.translationX = event.rawX + touchX - offsetX
                    view.translationY = event.rawY + touchY - offsetY
                }
            }
        }

        return true
    }
}