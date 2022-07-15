package com.company;

import java.util.ArrayList;
import java.util.List;

public class CarBase {

    private final List<Car> cars;

    public CarBase() {

        cars = new ArrayList<>();

        for (int i = 0; i < 200; i++) {
            cars.add(new Car(i));
        }
    }

    public List<Car> getCars() {
        return cars;
    }
}
