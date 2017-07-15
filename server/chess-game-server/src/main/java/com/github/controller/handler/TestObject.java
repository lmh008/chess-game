package com.github.controller.handler;

/**
 * Title
 * Author jirenhe@wanshifu.com
 * Time 2017/7/15.
 * Version v1.0
 */
public class TestObject {

    private String f1;
    private Double f2;
    private Integer f3;

    public String getF1() {
        return f1;
    }

    public void setF1(String f1) {
        this.f1 = f1;
    }

    public Double getF2() {
        return f2;
    }

    public void setF2(Double f2) {
        this.f2 = f2;
    }

    public Integer getF3() {
        return f3;
    }

    public void setF3(Integer f3) {
        this.f3 = f3;
    }

    @Override
    public String toString() {
        return "TestObject{" +
                "f1='" + f1 + '\'' +
                ", f2=" + f2 +
                ", f3=" + f3 +
                '}';
    }
}
