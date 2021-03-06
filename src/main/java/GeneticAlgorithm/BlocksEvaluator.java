package GeneticAlgorithm;

import main.Flight;
import org.uncommons.watchmaker.framework.FitnessEvaluator;

import java.util.*;

public class BlocksEvaluator implements FitnessEvaluator<List<Flight.block>>
{
    private final double limit;
    private final Flight flight;
    private final double PrimeCoeff;


    BlocksEvaluator(Flight flight, double PrimeCoefficient ){
        this.limit = flight.TotalAmount;
        this.flight = flight;
        this.PrimeCoeff = PrimeCoefficient;
    }

    public double getFitness(List<Flight.block> candidate,
                             List<? extends List<Flight.block>> population)
    {

        double matches = 0.0;
        double sumGRP = 0.0;

        Map<Integer, Flight.month> statusMonths = new HashMap<>();

        for (Integer i: flight.months.keySet()) {
            statusMonths.put(i, new Flight.month(0,0));
        }

        Set<Flight.block> temp = new HashSet<>(candidate);

        int wish = 0;
        for (Flight.block i: temp){
            if (i.prime){
                statusMonths.get(i.getMonth()).addPrime(i.grp);
            }
            if (!i.prime){
                statusMonths.get(i.getMonth()).addNonPrime(i.grp);
            }
            matches += i.aff * i.CurrentAd.duration/30.0;
            sumGRP += i.grp * i.CurrentAd.duration/30.0;
            for (Flight.block w: flight.Wishlist){
                if (i.id == w.id){
                    wish++;
                }
            }
        }

        if (wish < flight.Wishlist.size()){
            return 0;
        }

        for (Map.Entry<Integer, Flight.month> m:statusMonths.entrySet()){
            if ((m.getValue().primeRatio > flight.months.get(m.getKey()).primeRatio) ||
             (Math.abs(flight.months.get(m.getKey()).primeRatio - m.getValue().primeRatio) > PrimeCoeff)) {
                return 0;
            }

        }
        return sumGRP > limit? 0 : matches;
    }


    public boolean isNatural()
    {
        return true;
    }
}