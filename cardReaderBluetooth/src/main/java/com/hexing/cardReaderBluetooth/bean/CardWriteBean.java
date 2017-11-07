package com.hexing.cardReaderBluetooth.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by HEC271
 * on 2017/6/15.
 * 写卡数据 服务器返回的 model
 */

public class CardWriteBean implements Serializable {

    /**
     * verify : {"offset":[35],"value":["132165468651"]}
     * passwd : b62307
     * write : {"offset":[0,5,14,15,21],"value":["FF","FF","FF","FF","FF"]}
     */

    private VerifyBean verify;
    @SerializedName("passwd")
    private String oldPassword;

    @SerializedName("newpasswd")
    private String newPassword;
    private WriteBean write;

    private int passwordOffset;
    private String passwordData;

    public int getPasswordOffset() {
        return passwordOffset;
    }

    public void setPasswordOffset(int passwordOffset) {
        this.passwordOffset = passwordOffset;
    }

    public String getPasswordData() {
        return passwordData;
    }

    public void setPasswordData(String passwordData) {
        this.passwordData = passwordData;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public VerifyBean getVerify() {
        return verify;
    }

    public void setVerify(VerifyBean verify) {
        this.verify = verify;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public WriteBean getWrite() {
        return write;
    }

    public void setWrite(WriteBean write) {
        this.write = write;
    }

    public static class VerifyBean {
        private List<Integer> offset;
        private List<String> value;

        public List<Integer> getOffset() {
            return offset;
        }

        public void setOffset(List<Integer> offset) {
            this.offset = offset;
        }

        public List<String> getValue() {
            return value;
        }

        public void setValue(List<String> value) {
            this.value = value;
        }
    }

    public static class WriteBean {
        private List<Integer> offset;
        private List<String> value;

        public List<Integer> getOffset() {
            return offset;
        }

        public void setOffset(List<Integer> offset) {
            this.offset = offset;
        }

        public List<String> getValue() {
            return value;
        }

        public void setValue(List<String> value) {
            this.value = value;
        }
    }
}
