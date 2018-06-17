package SimpleGreedy;

import main.Flight;

public class SimpleGreedy {
    private static Flight.week tempStatWeek = null;
    private static Flight.week tempWeek = null;
    private static int skipLimit = 25;
    private static int remainder = 10;

    private static Flight.block forcePrime(Flight flight, sgDbConnection db){
        Flight.block newBlock = db.getBlock();
        if ((flight.status.statusMonths.get(newBlock.getMonth()).primeRatio
                <
                flight.months.get(newBlock.getMonth()).primeRatio)
                &&
                (!newBlock.prime))
        {
            newBlock = db.getPrimeBlock();
        }
        else if ((flight.status.statusMonths.get(newBlock.getMonth()).primeRatio
                >
                flight.months.get(newBlock.getMonth()).primeRatio)
                &&
                (newBlock.prime))
        {
            newBlock = db.getNonPrimeBlock();
        }
        return newBlock;
    }
    private static Flight.ad selectAd(Flight flight, Flight.block newBlock){
        int adIndex = 0;
        boolean adPicked = false;
        Flight.ad CurrentAd = flight.Ads.get(0);
        while (adIndex<(flight.Ads.size())&&(!adPicked)) {
            CurrentAd = flight.Ads.get(adIndex);
//        get week from current status
            for (Flight.week temp : flight.status.statusWeeks.get(CurrentAd.id)) {
                if ((newBlock.issueDate.after(temp.begin)) && (newBlock.issueDate.before(temp.end))
                        || (newBlock.issueDate.equals(temp.begin)) || (newBlock.issueDate.equals(temp.end)) ){
                    tempStatWeek = temp;
                }
            }
//        get week from flight
            for (Flight.week temp : flight.weeks.get(CurrentAd.id)) {
                if ((newBlock.issueDate.after(temp.begin)) && (newBlock.issueDate.before(temp.end))
                        || (newBlock.issueDate.equals(temp.begin)) || (newBlock.issueDate.equals(temp.end))) {
                    tempWeek = temp;
                }
            }
//        check if chosen ad sticks to plan and fits block
            if ((tempStatWeek.grp / flight.months.get(newBlock.getMonth()).grp)
                    > tempWeek.ratio) {
                adIndex++;
            }
            else {adPicked = true;}
        }
        if ((CurrentAd.duration > newBlock.fixDuration)){CurrentAd = flight.Ads.get(0);}
        return CurrentAd;
    }

    public static void setSkipLimit(int a) {
        skipLimit = a;
    }
    public static void setRemainder(int b) {
        remainder = b;}

    public static String  runSimpleGreedy(sgDbConnection sg, Flight flight){
        sg.createTempTable();
        sg.createResultTable();
        Flight.ad CurrentAd;
        Flight.block newBlock;
        double plus;
        int skips;

        long start = System.currentTimeMillis();
//        select block & check prime
        newBlock = forcePrime(flight, sg);
//        just select block
//        newBlock = sg.getBlock();
        if (newBlock.prime) {
            flight.status.statusMonths.get(newBlock.getMonth()).addPrime(newBlock.grp);
        }
        else {
            flight.status.statusMonths.get(newBlock.getMonth()).addNonPrime(newBlock.grp);
        }
//        select ad
        CurrentAd = selectAd(flight, newBlock);
//        calculate plus
        plus = newBlock.grp * (double)CurrentAd.duration/30.0;
        while (flight.status.GRP + plus <= flight.TotalAmount)
        {
//            statistics correction
            flight.status.GRP += plus;
            flight.status.statusMonths.get(newBlock.getMonth()).addMonthGrp(plus);
            flight.status.Aff += newBlock.aff * (double)CurrentAd.duration/30.0;
            tempStatWeek.addGrp(plus);
            tempStatWeek.setRatio(tempStatWeek.grp / flight.months.get(newBlock.getMonth()).grp);

//            memorising selected block and ad
            sg.addResultLine(newBlock.id, CurrentAd.id, newBlock.issueDate, plus);
//          select block from wish list
            if (!flight.Wishlist.isEmpty()){
//              reducing size of problem
                sg.deleteBlock(newBlock.id);
//                select wished block
                newBlock = sg.getWishedBlock(flight.Wishlist.get(0).id);
                flight.Wishlist.remove(0);
//              select ad
                CurrentAd = selectAd(flight, newBlock);
//              calculate plus
                plus = newBlock.grp * (double) CurrentAd.duration / 30.0;
            }
            else {
//            selecting block according to month plan
                skips = 0;
                do {
                    skips++;
                    sg.deleteBlock(newBlock.id);
//            select block & check prime
                    newBlock = forcePrime(flight, sg);
//                    newBlock = sg.getBlock();
//            select ad
                    CurrentAd = selectAd(flight, newBlock);
//            calculate plus
                    plus = newBlock.grp * (double) CurrentAd.duration / 30.0;
//                    System.out.println("ddplus = "+plus);
                }
                while ((flight.status.statusMonths.get(newBlock.getMonth()).grp + plus > flight.months.get(newBlock.getMonth()).grp)
                        &&
                        (skips < skipLimit));
            }
//            just select block
            //newBlock = sg.getBlock();
            if (newBlock.prime) {
                flight.status.statusMonths.get(newBlock.getMonth()).addPrime(newBlock.grp);
            }
            else {
                flight.status.statusMonths.get(newBlock.getMonth()).addNonPrime(newBlock.grp);
            }
//            check if we can get more grp with smaller pieces
            skips = 0;
            while ((flight.status.GRP + plus > flight.TotalAmount)&&(skips < remainder)){
                sg.deleteBlock(newBlock.id);
                newBlock = forcePrime(flight, sg);
                CurrentAd = selectAd(flight, newBlock);
                plus = newBlock.grp * (double)CurrentAd.duration/30.0;
                skips++;
            }
            if ((newBlock.prime)&&(skips < remainder)&&(skips>0)) {
                flight.status.statusMonths.get(newBlock.getMonth()).addPrime(newBlock.grp);
            }
            if ((!newBlock.prime)&&(skips < remainder)&&(skips>0)){
                flight.status.statusMonths.get(newBlock.getMonth()).addNonPrime(newBlock.grp);
            }
        }
        long timeWorkCode = System.currentTimeMillis() - start;
//        clean temp table
        sg.dropTempTable();
        return "\nВремя работы алгоритма = "+ timeWorkCode + " миллисекунд\n";
    }
}
