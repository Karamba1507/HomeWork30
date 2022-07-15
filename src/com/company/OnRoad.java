package com.company;

public class OnRoad implements State {

    private String state = "on road";

    @Override
    public String getState() {
        return state;
    }

    @Override
    public void changeState(Car car) {
        car.setState(new OnParking());
    }
}
