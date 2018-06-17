package DecompositionGreedy;

import main.Flight;

import java.sql.Date;
import java.util.List;
import java.util.Map;

public class DecompositionGreedy {
    private static Flight.block forcePrime(Flight flight, dgDbConnection db, Date begin){
        Flight.block newBlock = db.getBlock(begin);
        if ((flight.status.statusMonths.get(newBlock.getMonth()).primeRatio
                <
                flight.months.get(newBlock.getMonth()).primeRatio)
                &&
                (!newBlock.prime))
        {
            newBlock = db.getPrimeBlock(begin);
        }
        else if ((flight.status.statusMonths.get(newBlock.getMonth()).primeRatio
                >
                flight.months.get(newBlock.getMonth()).primeRatio)
                &&
                (newBlock.prime))
        {
            newBlock = db.getNonPrimeBlock(begin);
        }
        return newBlock;
    }
    public static String runDecompositionGreedy(dgDbConnection dg, Flight flight){
        Flight.block newBlock;
        Flight.week statusWeek=null;
        double duration = 0;
        double plus;
        double delta = 0;
        dg.createTempTables();
        dg.createResultTable();

        long start = System.currentTimeMillis();

        for (Map.Entry<Integer, List<Flight.week>> pair: flight.weeks.entrySet()) {
            for (Flight.ad a: flight.Ads){
                if (a.equals(pair.getKey())){
                    duration = a.duration;
                }
            }
            for (Flight.week thisWeek: pair.getValue()) {
                thisWeek.addGrp(delta);
                for (Flight.week temp : flight.status.statusWeeks.get(pair.getKey())) {
                    if (temp.begin==thisWeek.begin){
                        statusWeek = temp;
                    }
                }
                if (statusWeek != null) {
                    if (!flight.Wishlist.isEmpty()){
                        newBlock = dg.getWishedBlock(flight.Wishlist.get(0).id, thisWeek.begin);
                        if (newBlock == null) {
                            newBlock = forcePrime(flight, dg, thisWeek.begin);
                        }
                        else {
                            flight.Wishlist.remove(0);
                        }
                    }
                    else {
                        newBlock = forcePrime(flight, dg, thisWeek.begin);
                    }
                    if (newBlock.prime) {
                        flight.status.statusMonths.get(newBlock.getMonth()).addPrime(newBlock.grp);
                    }
                    else {
                        flight.status.statusMonths.get(newBlock.getMonth()).addNonPrime(newBlock.grp);
                    }
                    plus = newBlock.grp * duration / 30.0;
                    while (statusWeek.grp + plus <= thisWeek.grp){
                        flight.status.GRP += plus;
                        flight.status.Aff += newBlock.aff * duration / 30.0;
                        flight.status.statusMonths.get(newBlock.getMonth()).addMonthGrp(plus);
                        statusWeek.addGrp(plus);
                        statusWeek.setRatio(statusWeek.grp / flight.months.get(newBlock.getMonth()).grp);
                        dg.addResultLine(newBlock.id,pair.getKey(),newBlock.issueDate,plus);
                        dg.deleteBlock(newBlock.id, thisWeek.begin);
                        if (!flight.Wishlist.isEmpty()){
                            newBlock = dg.getWishedBlock(flight.Wishlist.get(0).id, thisWeek.begin);
                            if (newBlock == null) {
                                newBlock = forcePrime(flight, dg, thisWeek.begin);
                                short dont = 1;
                            }
                            else {
                                flight.Wishlist.remove(0);
                            }
                        }
                        else {
                            newBlock = forcePrime(flight, dg, thisWeek.begin);
                        }
                        if (newBlock.prime) {
                            flight.status.statusMonths.get(newBlock.getMonth()).addPrime(newBlock.grp);
                        }
                        else {
                            flight.status.statusMonths.get(newBlock.getMonth()).addNonPrime(newBlock.grp);
                        }
                        plus = newBlock.grp * duration / 30.0;
                    }
                    delta = thisWeek.grp - statusWeek.grp;
                }
            }
        }
        long timeWorkCode = System.currentTimeMillis() - start;
        dg.dropTempTables();
        return "\nВремя работы алгоритма = "+ timeWorkCode + " миллисекунд\n";
    }
}
