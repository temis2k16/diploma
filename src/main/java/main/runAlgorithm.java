package main;

import DecompositionGreedy.*;
import SimpleGreedy.*;
import GeneticAlgorithm.*;

public class runAlgorithm {

    /** Чтобы запустить алгоритм, нужно создать соответствующий объект для взаимодействия
     * с бд и распарсить флайт с помощью этого объекта (создать экземпляр класса Flight).
     * Для анализа равномерности использовать статический метод UniformTest класса CheckResult.
     */

    public static void main(String []args){

        /* запуск жадного алгоритма */
        sgDbConnection sg = new sgDbConnection();
        Flight flight1 = new Flight(sg);
        /* метод реализует жадный алгоритм (результат в таблице simpleGreedy)
        и возвращает строку с временем работы */
        SimpleGreedy.runSimpleGreedy(sg,flight1);

         /* запуск жадного алгоритма с методом декомпозиции */
        dgDbConnection dg = new dgDbConnection();
        Flight flight2 = new Flight(dg);
        /* метод реализует алгоритм (результат в таблице decompositionGreedy)
        и возвращает строку с временем работы */
        DecompositionGreedy.runDecompositionGreedy(dg,flight2);

         /* запуск генетического алгоритма */
        genDbConnection gen = new genDbConnection();
        Flight flight3 = new Flight(gen);
        /* метод реализует жадный алгоритм (результат в таблице geneticAlgorithm)
        возвращает строку с временем работы
        и числом затраченных поколений */
        GeneticAlgorithm.runGeneticAlgorithm(gen,flight3);

    }
}
