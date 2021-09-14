package com.andlill.jld.utils

import android.animation.Animator
import android.view.View

object ViewAnimation {

    enum class State {
        Start,
        End,
        Cancel,
    }

    fun rotate(view: View, duration: Long, value: Float, fromAlpha: Float, toAlpha: Float, callback: (State) -> Unit)  {
        view.alpha = fromAlpha
        view.animate().setDuration(duration).rotation(value).alpha(toAlpha).setListener(object: Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator?) { callback(State.Start) }
            override fun onAnimationEnd(animator: Animator?) { callback(State.End) }
            override fun onAnimationCancel(animator: Animator?) { callback(State.Cancel) }
            override fun onAnimationRepeat(animator: Animator?) {}
        }).start()
    }

    fun fadeIn(view: View, duration: Long) {
        view.alpha = 0f
        view.scaleX = 0f
        view.scaleY = 0f
        view.animate().setDuration(duration).alpha(1f).scaleX(1f).scaleY(1f).setListener(object: Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator?) { view.visibility = View.VISIBLE }
            override fun onAnimationEnd(animator: Animator?) {}
            override fun onAnimationCancel(animator: Animator?) {}
            override fun onAnimationRepeat(animator: Animator?) {}
        }).start()
    }

    fun fadeOut(view: View, duration: Long) {
        view.alpha = 1f
        view.scaleX = 1f
        view.scaleY = 1f
        view.animate().setDuration(duration).alpha(0f).scaleX(0f).scaleY(0f).setListener(object: Animator.AnimatorListener {
            override fun onAnimationStart(animator: Animator?) { view.visibility = View.VISIBLE }
            override fun onAnimationEnd(animator: Animator?) { view.visibility = View.INVISIBLE }
            override fun onAnimationCancel(animator: Animator?) {}
            override fun onAnimationRepeat(animator: Animator?) {}
        }).start()
    }
}