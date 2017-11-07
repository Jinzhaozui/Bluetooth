package com.hexing.cardReaderBluetooth.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by HEC271
 * on 2017/6/16.
 */

public class CardReadBean implements Serializable {

    /**
     * read : {"offset":[0],"value":[1024]}
     */

    private ReadBean read;

    public ReadBean getRead() {
        return read;
    }

    public void setRead(ReadBean read) {
        this.read = read;
    }

    public static class ReadBean {
        private List<Integer> offset;
        private List<Integer> value;

        public List<Integer> getOffset() {
            return offset;
        }

        public void setOffset(List<Integer> offset) {
            this.offset = offset;
        }

        public List<Integer> getValue() {
            return value;
        }

        public void setValue(List<Integer> value) {
            this.value = value;
        }
    }
}
