package com.company;

import java.util.ArrayList;
import java.util.List;

public class Parking {

    private int count = 20;

    //Парковочные места (20) штук. Если ячейка = 0 - значит место свободно. 1 - занято
    private final List<Integer> places;

    public Parking() {
        places = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            places.add(0);
        }
    }

    //Есть ли парковочные места
    public boolean existFreePlace() {
        if (places.contains(0)) {
            return true;
        }
        return false;
    }

    //Занять место
    public void occupie() {
        if (existFreePlace()) {
            for (int i = 0; i < places.size(); i++) {
                if (places.get(i) == 0) {
                    places.set(i, 1);
                    return;
                }
            }
        }
    }

    //Освободить место
    public void free() {
        for (int i = 0; i < places.size(); i++) {
            if (places.get(i) == 1) {
                places.set(i, 0);
                return;
            }
        }
    }

    //кол-во свободных мест
    public int freePlacesnum() {
        int result = 0;
        for (int i = 0; i < places.size(); i++) {
            if (places.get(i) == 0) {
                result++;
            }
        }
        return result;
    }

}