package com.dataqin.common.http.encryption;

import android.os.Build;

import com.dataqin.base.utils.LogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.dataqin.common.constant.Constants.API_VER;
import static com.dataqin.common.constant.Constants.APP_VERSION;
import static com.dataqin.common.constant.Constants.DEV;

/**
 * Created by liuzhi on 15/11/19.
 */
public class SecurityUtil {
    private static String code = "bitnew{q*2!H&akQ$cM|0com";
    private static final String TAG = "SecurityUtil";

    //把字节数组转成16进位制数
    private static String bytesToHex(byte[] bytes) {
        StringBuilder md5str = new StringBuilder();
        //把数组每一字节换成16进制连成md5字符串
        int digital;
        for (byte aByte : bytes) {
            digital = aByte;
            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString().toUpperCase();
    }

    //把字节数组转换成md5
    private static String bytesToMD5(byte[] input) {
        String md5str = null;
        try {
            //创建一个提供信息摘要算法的对象，初始化为md5算法对象
            MessageDigest md = MessageDigest.getInstance("MD5");
            //计算后获得字节数组
            byte[] buff = md.digest(input);
            //把数组每一字节换成16进制连成md5字符串
            md5str = bytesToHex(buff);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5str.toLowerCase();
    }

    //把字符串转换成md5
    private static String strToMD5(String str) {
        byte[] input = str.getBytes();
        return bytesToMD5(input);
    }

    //把文件转成md5字符串
    public static String fileToMD5(File file) {
        if (file == null) {
            return null;
        }
        if (!file.exists()) {
            return null;
        }
        if (!file.isFile()) {
            return null;
        }
        FileInputStream fis;
        try {
            //创建一个提供信息摘要算法的对象，初始化为md5算法对象
            MessageDigest md = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);
            byte[] buff = new byte[1024];
            int len;
            while (true) {
                len = fis.read(buff, 0, buff.length);
                if (len == -1) {
                    break;
                }
                //每次循环读取一定的字节都更新
                md.update(buff, 0, len);
            }
            //关闭流
            fis.close();
            //返回md5字符串
            return bytesToHex(md.digest());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //判断是否加密
    public static boolean needEncrypt() {
        if (RSAKeyFactory.getInstance().getStrEncrypt() != null) {
            return RSAKeyFactory.getInstance().getStrEncrypt().equals("1");
        }
        return false;
    }

    private static String toStr(Map<String, String> params) {
        StringBuilder result = new StringBuilder();
        for (HashMap.Entry<String, String> map : params.entrySet()) {
            if (result.length() > 0)
                result.append("&");
            result.append(map.getKey());
            result.append("=");
            result.append(map.getValue());
        }
        return result.toString();
    }

    /*
    md5(
        md5(imei+系统版本)
         +md5('tongjin{q*2!H&akQ$cM|0com')
         +md5(系统版本+设备类型)
         +imei
         +md5(APP版本+接口版本+接口号)
         +timestamp
        )
     */
    private static String makeSign(int request, String timestamp) {
        LogUtil.INSTANCE.e(TAG, " " + "\n头部未签名字段:\n"
                + "UUID:" + DeviceUuidFactory.getInstance().getUuid().toString() + "\n"
                + "系统版本号:" + Build.VERSION.RELEASE + "\n"
                + "加密code:" + code + "\n"
                + "系统版本号+设备名:" + Build.VERSION.RELEASE + Build.MANUFACTURER +"-"+ Build.MODEL+ "\n"
                + "UUID:" + DeviceUuidFactory.getInstance().getUuid().toString() + "\n"
                + "接口编号+接口版本号+请求编号:" + APP_VERSION + " + " + API_VER + " + " + request + "\n"
                + "时间戳:" + timestamp + ";\n");
        return strToMD5(
                strToMD5(DeviceUuidFactory.getInstance().getUuid().toString() + Build.VERSION.RELEASE)
                        + strToMD5(code)
                        + strToMD5(Build.VERSION.RELEASE + Build.MANUFACTURER +"-"+ Build.MODEL)
                        + DeviceUuidFactory.getInstance().getUuid().toString()
                        + strToMD5(APP_VERSION + API_VER + request)
                        + timestamp
        );
    }

    /*
    构造header的User-Agent字段
    User-Agent = 设备类型_设备名称+";"
    +系统版本号+";"
    +imei+";"
    +APP版本_接口版本_接口编号+";"
    +md5
     */
    public static String buildHeader(int request, String timestamp) {
        return DEV + "_" + Build.MANUFACTURER +"-"+ Build.MODEL + ";"
                + Build.VERSION.RELEASE + ";"
                + DeviceUuidFactory.getInstance().getUuid().toString() + ";"
                + APP_VERSION + "_" + API_VER + "_" + request + ";"
                + makeSign(request, timestamp);
    }

    public static String doSign(Map<String, String> param) {
        Map<String, String> params = new HashMap<>(param);
        params = sortParams(params);
        String strParam = toStr(params);
        LogUtil.INSTANCE.e(TAG, " " + "\n未签名字段:\n" + strParam + ";\n");
        return SecurityUtil.strToMD5(strParam);
    }

    public static HashMap<String, String> sortParams(Map<String, String> params) {
        Set<Map.Entry<String, String>> entries = params.entrySet();
        List<Map.Entry<String, String>> list = new LinkedList<>(entries);
        Collections.sort(list, (lhs, rhs) -> lhs.getKey().compareTo(rhs.getKey()));

        HashMap<String, String> afterParams = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : list) {
            afterParams.put(entry.getKey(), entry.getValue());
        }
        return afterParams;
    }

    public static String doEncrypt(Map<String, String> param) {
        String beforeEncode = toStr(param);
        LogUtil.INSTANCE.e(TAG, " " + "\n未加密字段:\n" + beforeEncode + ";\n");
        String afterEncode = "";
        try {
            byte[] data = beforeEncode.getBytes();
            byte[] encodedData = RSAUtil.encryptByPublicKey(data, RSAKeyFactory.getInstance().getStrPublicKey());
            afterEncode = Base64Util.encode(encodedData);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return afterEncode;
    }

    public static String createSignURL(String originalUrl, String strIMEI, String os, String device) {
        String strKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCbtXVeTkuoTWCJZqOE53wSUmzc\n" +
                "Q9/+bB59xqGeJV7NIOHUre+2NbeokCygNMghwzV61t37ojpsOICpvRlfWyp1+ZmH\n" +
                "uBPxUBc8MVmbYecJ8HACYw1y8Vf5M3Is1Z+VVUwwhUpNRGvaFpL+YH6J54r1KJf9\n" +
                "RSYKTG/kRInehrp0ZwIDAQAB";
        String result = originalUrl;
        String strNeedSign = "code=" + strIMEI + "&_t=" + RandomNum.createRandomString(10) + "&os=" + os + "&device=" + device;
        String sign = SecurityUtil.strToMD5(strNeedSign);
        strNeedSign = strNeedSign + "&sign=" + sign;
        try {
            String encStr = Base64Util.encode(RSAUtil.encryptByPublicKey(strNeedSign.getBytes(), strKey));
            LogUtil.INSTANCE.e(TAG, " " + "\nWeb签名参数:\n" + encStr);
            result = originalUrl + "&param=" + java.net.URLEncoder.encode(encStr, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.INSTANCE.e(TAG, " " + "\nWeb签名后链接地址:\n" + result);
        return result;
    }

    //对cookie进行签名
    public static String Encryption(String IEMI, String SYSTEM_VERSION, String DEVICE) {
        return SecurityUtil.strToMD5(SecurityUtil.strToMD5(IEMI) + SYSTEM_VERSION + SecurityUtil.strToMD5(code) + SecurityUtil.strToMD5(SYSTEM_VERSION + DEVICE) + IEMI);
    }

}
