package main;
import SimpleGreedy.*;

public class testing {
    public static void main(String[] args){
        sgDbConnection db = new sgDbConnection();
        Flight flight = new Flight(db);

        long start = System.currentTimeMillis();
        //dynamic programming
//        long n = 485550000;
        //greedy algorithm
        long n = 42586;
        double summ = 0;
        for (int i = 0; i < n; i++){
            summ += i*i /(flight.TotalAmount);
        }

        long timeWorkCode = System.currentTimeMillis() - start;
        System.out.println("Время работы алгоритма = "+ timeWorkCode + " миллисекунд");
        System.out.println(summ);
    }
}
