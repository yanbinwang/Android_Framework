package com.ow.basemodule.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;

import com.ow.basemodule.R;
import com.ow.basemodule.bean.KeyBean;
import com.ow.basemodule.bean.UserBean;
import com.ow.basemodule.constant.Constants;
import com.ow.basemodule.utils.http.encryption.RSAKeyFactory;
import com.ow.framework.utils.GsonUtil;
import com.ow.framework.utils.SHPUtil;


/**
 * 公司使用的持久登陆采取每次请求时校验一次key
 * 如果key不存在或者达不到一定要求，就去重新请求key接口
 * 该工具类对key值和用户信息的一些字做了规整和管控，全局直接调用即可
 */
@SuppressLint("StaticFieldLeak")
public class UserUtil {
    private static SHPUtil userInfoSHP;
    private static SHPUtil userConfigSHP;

    public static void init(Context context) {
        userInfoSHP = new SHPUtil(context, context.getString(R.string.shp_user_info_fileName));
        userConfigSHP = new SHPUtil(context, context.getString(R.string.shp_user_configure_fileName));
    }

    //存储用户键值类
    public static void setKeyBean(KeyBean bean) {
        if (null != bean) {
            userInfoSHP.saveParam(Constants.KEY_BEAN, GsonUtil.INSTANCE.objToJson(bean));
        }
    }

    //得到用户键值类
    private static KeyBean getKeyBean() {
        KeyBean keyBean = null;
        String keyBeanJson = userInfoSHP.getParam(Constants.KEY_BEAN);
        if (!TextUtils.isEmpty(keyBeanJson)) {
            keyBean = GsonUtil.INSTANCE.jsonToObj(keyBeanJson, KeyBean.class);
        }
        return keyBean;
    }

    //修改是否登陆
    public static void setLogin(boolean isLogin) {
        KeyBean keyBean = getKeyBean();
        if (null != keyBean) {
            if (isLogin) {
                keyBean.setUid("1");
            } else {
                keyBean.setUid("0");
            }
            setKeyBean(keyBean);
        }
    }

    //用户是否登陆
    public static boolean isLogin() {
        boolean isLogin = false;
        KeyBean keyBean = getKeyBean();
        if (null != keyBean) {
            isLogin = "1".equals(keyBean.getUid());
        }
        return isLogin;
    }

    //存储用户对象
    public static void setUserBean(UserBean bean) {
        if (null != bean) {
            userInfoSHP.saveParam(Constants.USER_BEAN, GsonUtil.INSTANCE.objToJson(bean));
        }
    }

    //获取用户对象
    public static UserBean getUserBean() {
        UserBean userInfoBean = null;
        String userInfoJson = userInfoSHP.getParam(Constants.USER_BEAN);
        if (!TextUtils.isEmpty(userInfoJson)) {
            userInfoBean = GsonUtil.INSTANCE.jsonToObj(userInfoJson, UserBean.class);
        }
        return userInfoBean;
    }

    //获取手机号
    public static String getMobile() {
        String mobile = null;
        UserBean userBean = getUserBean();
        if (null != userBean) {
            mobile = userBean.getMobile();
        }
        return mobile;
    }

    //短信验证是否开启
    public static boolean isSmsCheck() {
        boolean sms_check = false;
        UserBean userBean = getUserBean();
        if (null != userBean) {
            sms_check = userBean.getSms_check();
        }
        return sms_check;
    }

    //谷歌验证是否开启
    public static boolean isGoogleCheck() {
        boolean google_check = false;
        UserBean userBean = getUserBean();
        if (null != userBean) {
            google_check = userBean.getGoogle_check();
        }
        return google_check;
    }

    //是否设置了资金密码
    public static boolean isHaveTradePass() {
        boolean have_trade_pass = false;
        UserBean userBean = getUserBean();
        if (null != userBean) {
            have_trade_pass = userBean.getHave_trade_pass();
        }
        return have_trade_pass;
    }

    //是否实名认证
    public static boolean isRealVerified() {
        boolean real_verified = false;
        UserBean userBean = getUserBean();
        if (null != userBean) {
            real_verified = userBean.getReal_verified();
        }
        return real_verified;
    }

    //设置实名认证状态(init:初始化,inreview:提交认证中,verified:已认证,refused:认证失败)
    public static void setCustomerStatus(String customer_status) {
        UserBean userBean = getUserBean();
        if (null != userBean) {
            userBean.setCustomer_status(customer_status);
        }
        setUserBean(userBean);
    }

    //实名认证状态
    public static String getCustomerStatus() {
        String customer_status = null;
        UserBean userBean = getUserBean();
        if (null != userBean) {
            customer_status = userBean.getCustomer_status();
        }
        return customer_status;
    }

    //用户注销操作（清除信息,清除用户凭证）
    public static void signOut() {
        userInfoSHP.clearParam();
        userConfigSHP.clearParam();
        RSAKeyFactory.getInstance().clear();
    }

}
