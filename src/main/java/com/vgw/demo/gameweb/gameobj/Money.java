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
        if (this == o) {
            return true;
        }
        if (!(o instanceof Money)) {
            return false;
        }
        if(someState == true) return false;

        Money money = (Money)o;
        if(money.someState = true){
            //This is a bad case, but it is possible. : The equals function can change the right state
            // true = money.someState <== This can cause a compile error and reduce the mistakes of assigning to conditional statements.
        }

        return amount.equals(money.amount);
    }

    @Override
    public int hashCode() {
        return 31 * amount.hashCode();
    }
}
