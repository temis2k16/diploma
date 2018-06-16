package main;

import java.util.Arrays;

public class CheckResult {
    static private double dispersion(int days, int[] O, double M){
        double d = 0;
        for (int i = 0; i < days; i++){
            d += Math.pow((O[i] - M), 2) / days;
        }
        return d;
    }

    static public String UniformTest(dbConnection db, String table){
//        if (table==null){ table = "";}
        int days = db.getDaysCount();
        int spots = db.getRowsCount(table);
        double M = (double)spots / (double)days;
        int minDay = db.getMinDay();
        int[] O = new int[days];
        db.getObservedSpots(O, minDay, table);
        double D = dispersion(days, O, M);
        String report = "UNIFORM TEST:\n";
        report += String.format("%d days in flight\n", days);
        report += String.format("%d spots\n", spots);
        report += String.format("Expected = %f\n", M);
        report += Arrays.toString(O);
        report += String.format("\nС.К.О = %f", Math.sqrt(D));
        return report;

    }

    static public void AccuracyTest(){
        System.out.println("ACCURACY TEST:");
    }
}
