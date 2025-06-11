package com.iyuba.core.event;

import java.text.DecimalFormat;

public class MoneyAddEvent {
    private String money;

    public MoneyAddEvent (String money){
        this.money=money;
    }

    public String getMoney() {
        float fNumber = Float.valueOf(money);
        fNumber = (float) (fNumber * 0.01);
        DecimalFormat myformat = new DecimalFormat("0.00");
        String str = myformat.format(fNumber);
        return str;
    }
}
