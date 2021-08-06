package net.ossrs.yasea

import android.os.Handler
import android.os.Message
import java.lang.ref.WeakReference

class SrsEncodeHandler(listener: SrsEncodeListener) : Handler() {
    private val mWeakListener = WeakReference(listener)
    private val MSG_ENCODE_NETWORK_WEAK = 0
    private val MSG_ENCODE_NETWORK_RESUME = 1
    private val MSG_ENCODE_ILLEGAL_ARGUMENT_EXCEPTION = 2

    override fun handleMessage(msg: Message) {
        val listener = mWeakListener.get() ?: return
        when (msg.what) {
            MSG_ENCODE_NETWORK_WEAK -> listener.onNetworkWeak()
            MSG_ENCODE_NETWORK_RESUME -> listener.onNetworkResume()
            MSG_ENCODE_ILLEGAL_ARGUMENT_EXCEPTION -> {
                listener.onEncodeIllegalArgumentException(msg.obj as java.lang.IllegalArgumentException)
                throw RuntimeException("unknown msg ${msg.what}")
            }
            else -> throw RuntimeException("unknown msg ${msg.what}")
        }
    }

    fun notifyNetworkWeak() {
        sendEmptyMessage(MSG_ENCODE_NETWORK_WEAK)
    }

    fun notifyNetworkResume() {
        sendEmptyMessage(MSG_ENCODE_NETWORK_RESUME)
    }

    fun notifyEncodeIllegalArgumentException(e: IllegalArgumentException?) {
        obtainMessage(MSG_ENCODE_ILLEGAL_ARGUMENT_EXCEPTION, e).sendToTarget()
    }

    interface SrsEncodeListener {

        fun onNetworkWeak()

        fun onNetworkResume()

        fun onEncodeIllegalArgumentException(e: IllegalArgumentException?)

    }

}