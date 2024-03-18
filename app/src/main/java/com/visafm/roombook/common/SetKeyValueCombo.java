package com.visafm.roombook.common;

public class SetKeyValueCombo {

    String key;
    String value;

    public SetKeyValueCombo(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString()
    {
        return this.value;
    }

}
