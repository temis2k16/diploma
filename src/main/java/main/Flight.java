package main;

import java.sql.Date;
import java.util.*;

public class Flight{
    public double TotalAmount;
    public List<ad> Ads;
    public List<block> Wishlist;
    public Map<Integer, List<week>> weeks;
    public Map<Integer, month> months;
    public status status;

    public Flight(dbConnection db) {
        TotalAmount = db.getTotalAmount();
        Ads = db.getAds();
        Wishlist = db.getWishList();
        months = db.getMonthsStat();
        weeks = db.getWeeksStat();
        for (Map.Entry<Integer, List<week>> pair: weeks.entrySet()){
            for (week w: pair.getValue()){
                w.setGrp(w.ratio()*months.get(w.getMonth()).grp);
            }
        }
        Map<Integer, List<week>> tmp = new HashMap<>(weeks);
        status = new status(tmp, months);
    }

    public static class block {
        public int id;
        public double aff;
        public double grp;
        public boolean prime;
        public Date issueDate;
        public int fixDuration;
        public ad CurrentAd;

        @Override
        public boolean equals(Object obj) {
            return ((block)obj).id == this.id;
        }

        public void setCurrentAd(ad currentAd) {
            this.CurrentAd = currentAd;
        }

        public int getMonth(){
            Calendar cal = Calendar.getInstance();
            cal.setTime(issueDate);
            return cal.get(Calendar.MONTH) + 1;
        }
        public block(int id, double aff, double grp, boolean prime, Date date, int fix){
            this.id = id; this.aff = aff; this.grp = grp; this.prime = prime;
            issueDate = date; fixDuration = fix;
        }
        @Override
        public String toString() {
            return "\nid= " + id + " aff= " + aff;
        }
    }

    public static class ad implements Comparable{
        public int duration;
        public int id;

        ad(int id, int duration){
            this.id=id;
            this.duration=duration;
        }

        @Override
        public String toString() {
            return "id: " + id + " dur: " + duration;
        }

        public boolean equals(Integer id){
            return this.id==id;
        }

        @Override
        public int compareTo(Object o) {
            int comp = ((ad) o).duration;
        // For Ascending order
            return this.duration - comp;
        }
    }

    public static class status {
        public double GRP;
        public double Aff;
        public Map<Integer,month> statusMonths;
        public Map<Integer, List<week>> statusWeeks;
        public status(Map<Integer, List<week>> sw, Map<Integer,month> sm){
            GRP = 0;
            Aff = 0;
            statusMonths = new HashMap<>();
            for (Integer i: sm.keySet()) {
                statusMonths.put(i, new month(0,0));
            }
            statusWeeks = new HashMap<>();
            for (Map.Entry<Integer, List<week>> pair: sw.entrySet()){
                List<week> tmp = new ArrayList<>();
                for (week w: pair.getValue()){
                    tmp.add(new week(w.begin(),w.end(), 0.0));
                }
                statusWeeks.put(pair.getKey(),tmp);
            }
        }

        @Override
        public String toString() {
            return "STATUS:" + String.format("\nGRP: %.2f",GRP) + String.format("\nAFF = %.2f",Aff) +
                    "\nMonths: " + statusMonths +
                    "\nWeeks: " + statusWeeks;
        }
    }

    public static class month {
        public double primeRatio;
        double Prime;
        double nonPrime;
        public double grp;
        private void setPrimeRatio() {
            if ((Prime==nonPrime) && (Prime==0)){
                this.primeRatio = 0;
            }
            else {
                this.primeRatio =  Prime / (Prime+nonPrime);
            }
        }
        public void addNonPrime(double nonPrimeGRP){
            this.nonPrime += nonPrimeGRP;
            setPrimeRatio();
        }
        public void addPrime(double PrimeGRP){
            this.Prime += PrimeGRP;
            setPrimeRatio();
        }
        public void addMonthGrp(double monthGrp) {
            this.grp += monthGrp;
        }

        public month(double prime, double grp) {
            this.primeRatio = prime;
            this.grp = grp;
            this.Prime = 0;
            this.nonPrime = 0;
        }
        @Override
        public String toString() {
            return String.format("primeRatio: %.2f",primeRatio) + String.format(" grp: %.2f", grp);
        }
    }

    public static class week {
        public Date begin;
        public Date end;
        public double ratio;
        public double grp;

        @Override
        public boolean equals(Object obj) {
            return (((week) obj).begin.equals(this.begin)) && (((week) obj).end.equals(this.end));
        }

        int getMonth(){
            Calendar cal = Calendar.getInstance();
            cal.setTime(begin);
            return cal.get(Calendar.MONTH) + 1;
        }
        public void setBegin(Date begin) {
            this.begin = begin;
        }

        public void setEnd(Date end) {
            this.end = end;
        }

        void setGrp (double grp) {this.grp = grp;}

        public void setRatio(double ratio) {
            this.ratio = ratio;
        }

        public void addGrp(double grp){
            this.grp += grp;
        }

        Date begin() {
            return begin;
        }
        Date end() {
            return end;
        }
        double ratio(){
            return ratio;
        }
        public week(Date b, Date e, double r) {
            begin = b;
            end = e;
            ratio = r;
            grp = 0;
        }

        @Override
        public String toString() {
            return begin + String.format(" r= %.2f", ratio) + String.format(" grp= %.2f", grp);
        }
    }

    @Override
    public String toString() {
        return "FLIGHT:\nTotalAmount: " + TotalAmount +
                "\nAds: " + Ads +"\nWish list: " + Wishlist +
                "\nMonths: " + months + "\nWeeks: " + weeks;
    }
}
