package com.company;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ParkSimulation {

    private final List<Car> cars;
    private final Parking parking = new Parking();
    private final Map<Car, List<Cheque>> cheqMap = new HashMap<>();
    private LocalDateTime startSim;
    private final Random random = new Random();
    private final List<List<Integer>> periods = new ArrayList<>(); //список тарифов
    private boolean keepWorking = true;
    private final Scanner scanner = new Scanner(System.in);
    private final int period = 30;


    public ParkSimulation() {
        cars = new ArrayList<>();

        for (int i = 0; i < 200; i++) {
            cars.add(new Car(i));
        }

        //инициализация тарифов
        List<Integer> tarif1 = new ArrayList<>(); //первый тариф
        tarif1.add(9); //начало периода (часы)
        tarif1.add(21);//конец периода (часы)
        tarif1.add(5);//стоимость за 5 минут (центы)
        List<Integer> tarif2 = new ArrayList<>();
        tarif2.add(21);
        tarif2.add(9);
        tarif2.add(0);
        periods.add(tarif1);
        periods.add(tarif2);
    }

    public void startSimulation() {
        startSim = LocalDateTime.now();
        LocalDateTime endSim=startSim.plus(period, ChronoUnit.DAYS); //Период симуляции (дни)
        LocalDateTime currentDate = startSim;

        while (currentDate.isBefore(endSim)) {

            for (Car car : cars) {
                int ch = random.nextInt(100);
                if (ch < 3) {
                    if (car.getState().getState().equals("on road") && parking.existFreePlace()) {
                        car.changeState();
                        parking.occupie();
                        Cheque cheque = new Cheque(car, currentDate);
                        if (cheqMap.containsKey(car)) { //если список чеков для машины уже существует, то просто добавляем чек в эитот список
                            cheqMap.get(car).add(cheque);
                        } else { //если не существует, то кладем в мапу новую запись - <машину, и список чеков с единственным новым чеком>
                            List<Cheque> list = new ArrayList<>();
                            list.add(cheque);
                            cheqMap.put(car, list);
                        }
                    } else if (car.getState().getState().equals("on parking")) {
                        Cheque tempCheque = cheqMap.get(car).get(cheqMap.get(car).size() - 1);
                        tempCheque.setEnd(currentDate);
                        car.changeState();
                        parking.free();
                        tempCheque.setAmount(countAmount(tempCheque.getStart(), tempCheque.getEnd()) / 100.00);
//                        if (tempCheque.getAmount() == 0) { //Удаление чека с нулевой оплатой
//                            cheqMap.get(car).remove(cheqMap.get(car).size() - 1);
//                        }
                    }
                }
            }
            currentDate = currentDate.plusMinutes(5);
        }
        delLastCars();
//        printCheques();
        while (keepWorking) {
            menu();
//            System.out.println(fullgainPerDay());
            actions(enterAction(8));

        }
    }

    //Считает общую сумму за 1 чек
    private int countAmount (LocalDateTime start, LocalDateTime end) {
        int result = 0;

        if (start.plusMinutes(30).isBefore(end)) {
            LocalDateTime tempDate = start;
            while (tempDate.isBefore(end)) {
                result = result + costPerMinute(tempDate);
                tempDate = tempDate.plusMinutes(5);
            }
        }
        return result;
    }

    //Считает сумму за 1 пятиминутку
    private int costPerMinute(LocalDateTime time) {
        int hour = time.getHour();
        int result = 0;

        for (List<Integer> tarif : periods) {
            if (hour >= tarif.get(0) && hour < tarif.get(1)) {
                result = tarif.get(2);
                break;
            }
        }
        return result;
    }

    public void printCheques() {
        int i = 1;
        for (Map.Entry<Car, List<Cheque>> entry : cheqMap.entrySet()) {
            System.out.println("==============================");
            System.out.println(i + "   --- " + entry.getKey().getId() + " ---");
            for (Cheque cheque : entry.getValue()) {
                System.out.println(cheque);
            }
            System.out.println("==============================");
            i++;
        }
    }

    //Удаляет из базы чеков последний чек, с машиной еще стоящей на стоянке при наступлении отчетного срока
    private void delLastCars() {
        for (Map.Entry<Car, List<Cheque>> map : cheqMap.entrySet()) {
            if (map.getValue().get(map.getValue().size() - 1).getEnd() == null ) {
                map.getValue().remove(map.getValue().size() - 1);
            }
        }
    }

    private void menu() {
        System.out.println();
        System.out.println("======= Choose any action ========");
        System.out.println("1. Get full gain at one specific day");
        System.out.println("2. Get minimal, averange, and max gain at full period");
        System.out.println("3. Get 10 cars, wich was standing on the parking longest");
        System.out.println("4. How many cars was standing on the park less 30 minutes at specific day");
        System.out.println("5. Get list of all cars, wich standing on the park at specific day");
        System.out.println("6. Get all days, wich the specific car was on parking");
        System.out.println("7. Print Gistogram");
        System.out.println("8. Exit");
        System.out.println();
    }

    //Ввод чила с клавиатуры
    private int enterAction(int num) {
        System.out.println("Enter number 1 - " + num + ": ");
        int res = 0;
        res = scanner.nextInt();
        while (res < 1 || res > num) {
            System.out.println("Wrong Enter. Try again");
            res = scanner.nextInt();
        }
        return res;
    }


    private void actions(int actionNum) {
        if (actionNum == 1) {
            int dayNumber = enterAction(period);
            System.out.println("Full gain at " + dayNumber + " day " + " is " + getGainAtDay(dayNumber));
        } else if (actionNum == 2) {
            minMaxAvg();
        } else if (actionNum == 3) {
            print10LongestParkCars();
        } else if (actionNum == 4) {
            int dayNumber = enterAction(period);
            for (Map.Entry<Integer, Long> entry : getSmall30min(dayNumber).entrySet()) {
                System.out.printf("Car id = %d was standing %d minutes%n", entry.getKey(), entry.getValue());
            }
        } else if (actionNum == 5) {
            int dayNumber = enterAction(period);
            for (Integer carId : getCarsWichWasOnParkAtDay(dayNumber)) {
                System.out.printf("Car id = %d was standing on parking at choosen day%n", carId);
            }
        } else if (actionNum == 6) {
            int carNum = enterAction(200);
            for (LocalDateTime dateTime : getDaysForCar(carNum)) {
                System.out.println("At the day " + dateTime.toString() + " car was on parking");
            }
        } else if (actionNum == 7) {
            System.out.println("I'm sorry, the section is under development");
        } else if (actionNum == 8) {
            System.out.println("=== GOOD BYE ===");
            this.keepWorking = false;
        }
    }

    //1
    private double getGainAtDay(int dayNumber) {
        LocalDateTime day = startSim.plusDays(dayNumber - 1);
        double result = 0;

        for (Map.Entry<Car, List<Cheque>> map : cheqMap.entrySet()) {
            for (Cheque cheque : map.getValue()) {
                if (day.truncatedTo(ChronoUnit.DAYS).isEqual(cheque.getEnd().truncatedTo(ChronoUnit.DAYS))) {
                    result = result + cheque.getAmount();
                }
            }
        }
        return result;
    }

    //2
    private void minMaxAvg() {
        double min = fullgainPerDay().get(0);
        double max = min;
        double summ = 0;

        for (double num : fullgainPerDay()) {
            if (num < min) {
                min = num;
            }
            if (num > max) {
                max = num;
            }
            summ = summ + num;
        }

        double avg = summ / period;
        System.out.printf("Min gain = %f, average = %f, max gain = %f %n", min, avg, max);
    }

    //3
    private void print10LongestParkCars() {
        int i = 0;
        for (Map.Entry<Integer, Long> entry : sortByValue(carsByParkTime()).entrySet()) {
            if (i > 10) {
                break;
            }
            System.out.printf("Car id = %d was parkin %d minutes%n", entry.getKey(), entry.getValue());
            i++;
        }

    }

    //4 Возвращает список машин, стоявших менее 30 минут за определенный день
    private Map<Integer, Long> getSmall30min(int dayNumber) {
        Map<Integer, Long> cars = new HashMap<>();

        for (Map.Entry<Car, List<Cheque>> entry : cheqMap.entrySet()) {
            for (Cheque cheque : entry.getValue()) {
                long minutes = 0;
                if (cheque.getStart().getDayOfMonth() == startSim.plusDays(dayNumber).getDayOfMonth()) {
                    minutes = Duration.between(cheque.getStart(), cheque.getEnd()).toMinutes();

                    if (cars.containsKey(cheque.getCar().getId())) {
                        minutes = minutes + cars.get(cheque.getCar().getId());
                    }

                    cars.put(cheque.getCar().getId(), minutes);

                }
            }
        }

        Map<Integer, Long> result = new HashMap<>();

        for (Map.Entry<Integer, Long> entry : cars.entrySet()) {
            if (entry.getValue() < 30) {
                result.put(entry.getKey(), entry.getValue());
            }
        }

        return result;
    }

    //5 Возвращает список машин, которые были на парковке в определенный день
    private Set<Integer> getCarsWichWasOnParkAtDay(int dayNum) {
        Set<Integer> result = new HashSet<>();

        for (Map.Entry<Car, List<Cheque>> entry : cheqMap.entrySet()) {
            for (Cheque cheque : entry.getValue()) {
                if (cheque.getStart().getDayOfMonth() == dayNum || cheque.getEnd().getDayOfMonth() == dayNum) {
                    result.add(cheque.getCar().getId());
                }
            }
        }
        return result;
    }

    //6 Возвращает список дней, в которые машина была на парковке
    private Set<LocalDateTime> getDaysForCar(int carId) {
        Set<LocalDateTime> days = new HashSet<>();

        for (Map.Entry<Car, List<Cheque>> entry : cheqMap.entrySet()) {
            if (entry.getKey().getId() == carId) {
                for (Cheque cheque : entry.getValue()) {
                    days.add( cheque.getStart().truncatedTo(ChronoUnit.DAYS));
                    days.add( cheque.getEnd().truncatedTo(ChronoUnit.DAYS));
                }
                break;
            }
        }
        return days;
    }


    //Возвращает сумму прибыли за каждый день за весь период симуляции
    private List<Double> fullgainPerDay() {
        List<Double> result = new ArrayList<>();
        for (int i = 0; i < period; i++) {
            double amount = 0;
            LocalDateTime dateTime = startSim.plusDays(i).truncatedTo(ChronoUnit.DAYS);
            for (Map.Entry<Car, List<Cheque>> map : cheqMap.entrySet()) {
                for (Cheque cheque : map.getValue()) {
                    if (cheque.getEnd().truncatedTo(ChronoUnit.DAYS).isEqual(dateTime)) {
                        amount = amount + cheque.getAmount();
                    }
                }
            }
            result.add(amount);
        }
        return result;
    }


    //Возвращает не сортированную мапу, где K - id тачки, V - общее время стоянки машины в минутах
    private Map<Integer, Long> carsByParkTime() {
        Map<Integer, Long> timeOfParkingPerCar = new HashMap<>(); //мапа машин <общее время стоянки в минутах, Id тачки>

        for (Map.Entry<Car, List<Cheque>> entry : cheqMap.entrySet()) {
            long fullPark = 0; // время стоянки машины за весь период симуляции

            for (Cheque cheque : entry.getValue()) {
                fullPark = fullPark + Duration.between(cheque.getStart(), cheque.getEnd()).toMinutes();
            }
            timeOfParkingPerCar.put(entry.getKey().getId(), fullPark);
        }
        return timeOfParkingPerCar;
    }


    //Возвращает отсортированную по V мапу
    private Map<Integer, Long> sortByValue(Map<Integer, Long> unsortMap) {
        List<Map.Entry<Integer, Long>> list = new LinkedList<Map.Entry<Integer, Long>>(unsortMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, Long>>() {
            public int compare(Map.Entry<Integer, Long> o1, Map.Entry<Integer, Long> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        Map<Integer, Long> sortedMap = new LinkedHashMap<Integer, Long>();
        for (Map.Entry<Integer, Long> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }



    public static void main(String[] args) {

        ParkSimulation simulation = new ParkSimulation();
        simulation.startSimulation();
    }
}