package com.dataqin.common.base.proxy

import com.google.android.material.appbar.AppBarLayout
import kotlin.math.abs

abstract class AppBarStateChangeListener : AppBarLayout.OnOffsetChangedListener {
    enum class State {
        EXPANDED, COLLAPSED, IDLE//展开，折叠，中间
    }

    private var mCurrentState = State.IDLE

    override fun onOffsetChanged(appBarLayout: AppBarLayout?, verticalOffset: Int) {
        mCurrentState = if (verticalOffset == 0) {
            if (mCurrentState != State.EXPANDED) onStateChanged(appBarLayout, State.EXPANDED)
            State.EXPANDED
        } else if (abs(verticalOffset) >= appBarLayout!!.totalScrollRange) {
            if (mCurrentState != State.COLLAPSED) onStateChanged(appBarLayout, State.COLLAPSED)
            State.COLLAPSED
        } else {
            if (mCurrentState != State.IDLE) onStateChanged(appBarLayout, State.IDLE)
            State.IDLE
        }
    }

    abstract fun onStateChanged(appBarLayout: AppBarLayout?, state: State?)

}