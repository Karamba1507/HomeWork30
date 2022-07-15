package com.company;

public class OnParking implements State {

    private String state = "on parking";

    @Override
    public String getState() {
        return state;
    }

    @Override
    public void changeState(Car car) {
        car.setState(new OnRoad());
    }
}
