package net.ossrs.yasea

import android.os.Handler
import android.os.Message
import java.io.IOException
import java.lang.ref.WeakReference

class SrsRecordHandler(listener: SrsRecordListener) : Handler() {
    private var mWeakListener = WeakReference(listener)
    private val MSG_RECORD_PAUSE = 0
    private val MSG_RECORD_RESUME = 1
    private val MSG_RECORD_STARTED = 2
    private val MSG_RECORD_FINISHED = 3
    private val MSG_RECORD_ILLEGEL_ARGUMENT_EXCEPTION = 4
    private val MSG_RECORD_IO_EXCEPTION = 5

    override fun handleMessage(msg: Message) {
        val listener = mWeakListener.get() ?: return
        when (msg.what) {
            MSG_RECORD_PAUSE -> listener.onRecordPause()
            MSG_RECORD_RESUME -> listener.onRecordResume()
            MSG_RECORD_STARTED -> listener.onRecordStarted(msg.obj as String)
            MSG_RECORD_FINISHED -> listener.onRecordFinished(msg.obj as String)
            MSG_RECORD_ILLEGEL_ARGUMENT_EXCEPTION -> listener.onRecordIllegalArgumentException(msg.obj as java.lang.IllegalArgumentException)
            MSG_RECORD_IO_EXCEPTION -> listener.onRecordIOException(msg.obj as IOException)
            else -> throw RuntimeException("unknown msg " + msg.what)
        }
    }

    fun notifyRecordPause() {
        sendEmptyMessage(MSG_RECORD_PAUSE)
    }

    fun notifyRecordResume() {
        sendEmptyMessage(MSG_RECORD_RESUME)
    }

    fun notifyRecordStarted(msg: String?) {
        obtainMessage(MSG_RECORD_STARTED, msg).sendToTarget()
    }

    fun notifyRecordFinished(msg: String?) {
        obtainMessage(MSG_RECORD_FINISHED, msg).sendToTarget()
    }

    fun notifyRecordIllegalArgumentException(e: IllegalArgumentException?) {
        obtainMessage(MSG_RECORD_ILLEGEL_ARGUMENT_EXCEPTION, e).sendToTarget()
    }

    fun notifyRecordIOException(e: IOException?) {
        obtainMessage(MSG_RECORD_IO_EXCEPTION, e).sendToTarget()
    }

    interface SrsRecordListener {

        fun onRecordPause()

        fun onRecordResume()

        fun onRecordStarted(msg: String?)

        fun onRecordFinished(msg: String?)

        fun onRecordIllegalArgumentException(e: IllegalArgumentException?)

        fun onRecordIOException(e: IOException?)

    }

}