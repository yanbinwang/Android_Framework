package com.dataqin.testnew.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.provider.ContactsContract
import com.dataqin.testnew.model.ContactsInfo
import com.dataqin.testnew.model.SmsInfo

/**
 * 手机内容查询工具类
 */
@SuppressLint("Range")
object ResolverHelper {

    /**
     * 短信查询
     */
    fun smsQuery(context: Context): MutableList<SmsInfo> {
        val list = ArrayList<SmsInfo>()
        val resolver = context.contentResolver
        val cursor = resolver.query(Uri.parse("content://sms/"), arrayOf("_id", "address", "type", "body", "date"), null, null, null)
        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val _id: Int = cursor.getInt(0)
                val address: String = cursor.getString(1)
                val type: Int = cursor.getInt(2)
                val body: String = cursor.getString(3)
                val date: Long = cursor.getLong(4)
                val smsInfo = SmsInfo(_id, address, type, body, date)
                list.add(smsInfo)
            }
            cursor.close()
        }
        return list
    }

    /**
     * 通讯录查询
     */
    fun contactsQuery(context: Context): MutableList<ContactsInfo> {
        val list = ArrayList<ContactsInfo>()
        val resolver = context.contentResolver
        val cursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)
        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                val name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val smsInfo = ContactsInfo(name, number)
                list.add(smsInfo)
            }
            cursor.close()
        }
        return list
    }

}