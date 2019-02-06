package com.vgw.demo.gameweb.gameobj;

import java.math.BigDecimal;

public class Money {

    BigDecimal amount;
    boolean someState = false;

    public Money(BigDecimal amount) {
        this.amount = amount.setScale(2);
    }
    @Override
    public boolean equals(Object o) {
        if(someState) return false;
        if (this == o) {
            return true;
        }
        if (!(o instanceof Money)) {
            return false;
        }
        Money money = (Money)o;
        money.someState = true;
        return amount.equals(money.amount);
    }

    @Override
    public int hashCode() {
        return 31 * amount.hashCode();
    }
}
