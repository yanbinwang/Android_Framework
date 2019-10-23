package com.ow.basemodule.utils.http.encryption;

/**
 * Created by liuzhi on 16/1/4.
 */
public class RSAKeyFactory {
    private String strPublicKey = "";
    private String strEncrypt = "";
    private String strAlreadyRequest = "";
    private static RSAKeyFactory rsaKeyFactory;

    private RSAKeyFactory() {}

    public static RSAKeyFactory getInstance() {
        if (rsaKeyFactory == null) {
            rsaKeyFactory = new RSAKeyFactory();
        }
        return rsaKeyFactory;
    }

    public String getStrPublicKey() {
        return strPublicKey;
    }

    public void setStrPublicKey(String strPublicKey) {
        this.strPublicKey = strPublicKey;
    }

    public String getStrEncrypt() {
        return strEncrypt;
    }

    public void setStrEncrypt(String strEncrypt) {
        this.strEncrypt = strEncrypt;
    }

    public String getStrAlreadyRequest() {
        return strAlreadyRequest;
    }

    public void setStrAlreadyRequest(String strAlreadyRequest) {
        this.strAlreadyRequest = strAlreadyRequest;
    }

    public void clear(){
        this.strEncrypt = "";
        this.strPublicKey = "";
        this.strAlreadyRequest = "";
    }

}