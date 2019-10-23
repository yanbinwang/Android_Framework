package com.ow.basemodule.utils.http.encryption;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.ow.basemodule.BaseApplication;
import com.ow.framework.utils.SHPUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * Created by cyril on 16/1/7.
 * ----------Dragon be here!----------/
 * ***┏┓******┏┓*********
 * *┏━┛┻━━━━━━┛┻━━┓*******
 * *┃             ┃*******
 * *┃     ━━━     ┃*******
 * *┃             ┃*******
 * *┃  ━┳┛   ┗┳━  ┃*******
 * *┃             ┃*******
 * *┃     ━┻━     ┃*******
 * *┃             ┃*******
 * *┗━━━┓     ┏━━━┛*******
 * *****┃     ┃神兽保佑*****
 * *****┃     ┃代码无BUG！***
 * *****┃     ┗━━━━━━━━┓*****
 * *****┃              ┣┓****
 * *****┃              ┏┛****
 * *****┗━┓┓┏━━━━┳┓┏━━━┛*****
 * *******┃┫┫****┃┫┫********
 * *******┗┻┛****┗┻┛*********
 * ━━━━━━神兽出没━━━━━━by:wangziren
 */
@SuppressLint({"MissingPermission", "HardwareIds"})
public class DeviceUuidFactory {
    private static UUID uuid;
    private static final String KEY = "C0inPay'";
    private static final String DEVICE_UUID_FILE_NAME = ".coinpay_device_.bin";
    private static final String PREFS_DEVICE_ID = "coinpay_evice_id";
    private static DeviceUuidFactory deviceUuidFactory;

    private DeviceUuidFactory() {
        Context context = BaseApplication.getInstance().getApplicationContext();
        SHPUtil shpUtil = new SHPUtil(context);
        String id = new SHPUtil(context).getParam(PREFS_DEVICE_ID);
        if (!TextUtils.isEmpty(id)) {
            uuid = UUID.fromString(id);
        } else {
            if (recoverDeviceUuidFromSD() != null) {
                uuid = UUID.fromString(recoverDeviceUuidFromSD());
            } else {
                final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                if (!"9774d56d682e549c".equals(androidId)) {
                    uuid = UUID.nameUUIDFromBytes(androidId.getBytes(StandardCharsets.UTF_8));
                    try {
                        saveDeviceUuidToSD(EncryptUtil.encryptDES(uuid.toString(), KEY));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    final String deviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
                    uuid = deviceId != null ? UUID.nameUUIDFromBytes(deviceId.getBytes(StandardCharsets.UTF_8)) : UUID.randomUUID();
                    try {
                        saveDeviceUuidToSD(EncryptUtil.encryptDES(uuid.toString(), KEY));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            shpUtil.saveParam(PREFS_DEVICE_ID, uuid.toString());
        }
    }

    public static synchronized DeviceUuidFactory getInstance() {
        if (deviceUuidFactory == null) {
            deviceUuidFactory = new DeviceUuidFactory();
        }
        return deviceUuidFactory;
    }

    private static String recoverDeviceUuidFromSD() {
        try {
            String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            File dir = new File(dirPath);
            File uuidFile = new File(dir, DEVICE_UUID_FILE_NAME);
            if (!dir.exists() || !uuidFile.exists()) {
                return null;
            }
            FileReader fileReader = new FileReader(uuidFile);
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[100];
            int readCount;
            while ((readCount = fileReader.read(buffer)) > 0) {
                sb.append(buffer, 0, readCount);
            }
            //通过UUID.fromString来检查uuid的格式正确性
            UUID uuid = UUID.fromString(EncryptUtil.decryptDES(sb.toString(), KEY));
            return uuid.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void saveDeviceUuidToSD(String uuid) {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File targetFile = new File(dirPath, DEVICE_UUID_FILE_NAME);
        if (!targetFile.exists()) {
            OutputStreamWriter osw;
            try {
                osw = new OutputStreamWriter(new FileOutputStream(targetFile), StandardCharsets.UTF_8);
                try {
                    osw.write(uuid);
                    osw.flush();
                    osw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public UUID getUuid() {
        return uuid;
    }

}
