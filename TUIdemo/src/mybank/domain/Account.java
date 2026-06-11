package com.mybank.domain;

public abstract class Account {
    protected double balance;

    protected Account(double balance) {
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }
}
