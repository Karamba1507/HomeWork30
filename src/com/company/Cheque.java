package com.company;

import java.time.LocalDateTime;

public class Cheque {

    private LocalDateTime createTime;
    private Car car;
    private LocalDateTime start;
    private LocalDateTime end;
    private double amount;

    public Cheque(Car car, LocalDateTime start) {
        this.start = start;
        this.car = car;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Cheque{" +
                "createTime=" + createTime +
                ", car=" + car +
                ", start=" + start +
                ", end=" + end +
                ", amount=" + amount +
                '}';
    }
}

