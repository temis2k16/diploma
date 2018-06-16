package main;

import java.sql.*;
import java.util.*;

public class dbConnection {
    private final String url = "jdbc:postgresql://localhost:5432/diploma";
    private final String user = "postgres";
    private final String pass = "postgres";
    private String SpotTable = "\"AdIssue\"";
    protected Connection conn = null;


    private Connection connect() {
        Connection bd = null;
        try {
            bd = DriverManager.getConnection(url,user,pass);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            System.exit(0);
        }
        return bd;
    }

    protected dbConnection(){
        conn = connect();
    }

    protected int executeCount(String SQL) {
        int count = 0;
        try  {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            rs.next();
            count = rs.getInt(1);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return count;
    }

    protected double executeDouble(String SQL) {
        double res = 0;
        try  {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            rs.next();
            res = rs.getDouble(1);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    double getTotalAmount(){
        String SQL = "select sum(\"targetPlan\") FROM \"Flightplan\"";
        return executeDouble(SQL);
    }

    void getObservedSpots(int[] Obs, int m, String Table) {
        if (Table==null){
            Table = SpotTable;
        }
        String SQL = "SELECT count(*), extract(DOY FROM \"spotDate\") FROM " + Table + " GROUP BY \"spotDate\"";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            ObserveSpots(rs, Obs, m);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void ObserveSpots(ResultSet rs, int[] Obs, int minDay) throws SQLException {
        while (rs.next()) {
            Obs[rs.getInt(2) - minDay] = rs.getInt(1);
        }
    }

    private List<Flight.block> getIdList(String SQL){
        List<Flight.block> res = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                res.add(new Flight.block(rs.getInt(1),rs.getDouble(2),rs.getDouble(3)
                , rs.getBoolean(4), rs.getDate(5), rs.getInt(6)));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    Map<Integer, List<Flight.week>> getWeeksStat(){
        String SQL = "Select \"adId\", \"beginDate\", \"endDate\", \"targetRatio\" FROM \"WeekPlan\"";
        Map<Integer, List<Flight.week>> map = new HashMap<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                Flight.week temp = new Flight.week(rs.getDate(2),
                        rs.getDate(3), rs.getDouble(4));
                int ad = rs.getInt(1);
                map.computeIfAbsent(ad, k -> new ArrayList<>());
                map.get(ad).add(temp);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return map;
    }

    protected void createSomeTable(String SQL1, String SQL2){
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(SQL1);
            stmt.executeUpdate(SQL2);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    protected Flight.block getBlock(String SQL) {
        try  {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            if(rs.next()){
                return new Flight.block(rs.getInt(1),
                        rs.getDouble(2), rs.getDouble(3),
                        rs.getBoolean(4), rs.getDate(5),
                        rs.getInt(6));
            }
            else {
                System.out.println("wished block is not found in sub table");
                return null;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    protected Flight.block returnPrimeBlock(String SQL) {
        try  {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                if (rs.getBoolean(4)){
                    return new Flight.block(rs.getInt(1),
                            rs.getDouble(2), rs.getDouble(3),
                            rs.getBoolean(4), rs.getDate(5),
                            rs.getInt(6));
                }
            }
            return null;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    protected Flight.block returnNonPrimeBlock(String SQL) {
        try  {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                if (!rs.getBoolean(4)){
                    return new Flight.block(rs.getInt(1),
                            rs.getDouble(2), rs.getDouble(3),
                            rs.getBoolean(4), rs.getDate(5),
                            rs.getInt(6));
                }
            }
            return null;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    protected void insertSomeLine(String SQL) {
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(SQL);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    Map<Integer, Flight.month> getMonthsStat(){
        String SQL = "SELECT \"monthId\" % 10 as \"MonthOfYear\", \"targetPlan\", \"primeRatio\" FROM \"Flightplan\"";
        Map<Integer, Flight.month> map = new HashMap<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                Flight.month temp = new Flight.month(rs.getDouble("primeRatio"),
                        rs.getDouble("targetPlan"));
                map.put(rs.getInt(1), temp);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return map;
    }

    List<Flight.block> getWishList() {
        String SQL = "select b.\"id\", f.\"calc_affinity\", b.\"grpPlan\", b.\"isPrimeTime\", b.\"issueDate\", b.\"fixDuration\"\n" +
                "FROM \"WishList\" w\n" +
                "JOIN  \"BreakIssue\" b ON w.\"blockId\" = b.id\n" +
                "JOIN \"ForecastAffinity\" f ON f.\"blockId\"=b.id\n" +
                "WHERE b.\"fixDuration\" >= (SELECT min(\"Ad\".duration) FROM \"Ad\")\n" +
                "      AND b.\"issueDate\" BETWEEN (SELECT \"beginDate\" FROM \"Flight\") AND (SELECT \"endDate\" FROM \"Flight\");";
        return getIdList(SQL);
    }

    List<Flight.ad> getAds() {
        String SQL = "SELECT \"id\", \"duration\" FROM \"Ad\"";
        List<Flight.ad> res = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                Flight.ad temp = new Flight.ad(rs.getInt(1), rs.getInt(2));
                res.add(temp);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        Collections.sort(res);
        return res;
    }

    int getDaysCount() {
        String SQL = "SELECT ((select extract(DOY FROM \"endDate\") FROM \"Flight\") - " +
                "(SELECT extract(DOY FROM \"beginDate\") FROM \"Flight\") + 1)";
        return executeCount(SQL);
    }

    int getMinDay() {
        String SQL = "select extract(DOY FROM \"beginDate\") from \"Flight\"";
        return executeCount(SQL);
    }

    int getRowsCount(String Table) {
        if (Table==null){
            Table = SpotTable;
        }
        String SQL = "SELECT count(*) FROM " + Table;
        return executeCount(SQL);
    }

    void createResultTable(){}
}
