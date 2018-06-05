package main;

import GeneticAlgorithm.GeneticAlgorithm;
import GeneticAlgorithm.genDbConnection;

public class diploma {

    public static void main(String[] args) {
        genDbConnection dg = new genDbConnection();
//        dgDbConnection dg = new dgDbConnection();
//        main.Flight flight = new main.Flight(dg);
//        System.out.println(flight);

////        run decomposition + greedy
////        start timer
//        long start = System.currentTimeMillis();
//        DecompositionGreedy.runDecompositionGreedy(dg,flight);
////        stop timer
//        long timeWorkCode = System.currentTimeMillis() - start;
////        print status
//        System.out.println(flight.status);
////        Вывод времени выполнения работы кода на экран
//        System.out.println("Скорость выполнения программы: " + timeWorkCode + " миллисекунд");

//        run simple greedy
        Flight flight = new Flight(dg);
        System.out.println(flight);
//        start timer
        long start = System.currentTimeMillis();
//        SimpleGreedy.SimpleGreedy.runSimpleGreedy(dg,flight);
        GeneticAlgorithm.runGeneticAlgorithm(dg,flight);
//        stop timer

        long timeWorkCode = System.currentTimeMillis() - start;
//        print status
        System.out.println(flight.status);
//        Вывод времени выполнения работы кода на экран
        System.out.println("Скорость выполнения программы: " + timeWorkCode + " миллисекунд");

        CheckResult.UniformTest(dg,"\"geneticAlgorithm\"");
//        main.CheckResult.UniformTest(dg,"\"decompositionGreedy\"");
//        main.CheckResult.UniformTest(dg,"\"simpleGreedy\"");
        CheckResult.UniformTest(dg,null);
    }
}
