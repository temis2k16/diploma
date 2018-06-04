import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class dgDbConnection extends dbConnection{
    void createTempTables(){
        String SQL = "SELECT DISTINCT \"beginDate\", \"endDate\" FROM \"WeekPlan\"";
        String SQL1;
        String SQL2;
        Date begin;
        Date end;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            while (rs.next()) {
               begin = rs.getDate("beginDate");
               end = rs.getDate("endDate");
               SQL1 ="DROP TABLE IF EXISTS \"" + begin.toString() + "\"";
               SQL2 = "select b.\"id\", f.\"calc_affinity\", b.\"grpPlan\", b.\"isPrimeTime\", b.\"issueDate\", b.\"fixDuration\"\n" +
                        "INTO \""+begin.toString()+"\"\n" +
                        "FROM \"ForecastAffinity\" f RIGHT JOIN \"BreakIssue\" b ON f.\"blockId\" = b.id\n" +
                        "WHERE b.\"fixDuration\" >= (SELECT min(\"Ad\".duration) FROM \"Ad\")\n" +
                        "AND b.\"issueDate\" BETWEEN \'"+ begin.toString()+ "\' AND \'"+ end.toString()+ "\'" +
                        "ORDER BY calc_affinity DESC ";
               createSomeTable(SQL1, SQL2);
            }
        }
        catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    void dropTempTables(){
        String SQL = "SELECT DISTINCT \"beginDate\", \"endDate\" FROM \"WeekPlan\"";
        String SQL1;
        Date begin;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                begin = rs.getDate("beginDate");
                SQL1 ="DROP TABLE IF EXISTS \"" + begin.toString() + "\"";
                insertSomeLine(SQL1);
            }
        }
        catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    Flight.block getPrimeBlock(Date name) {
        String SQL = "SELECT * FROM \"" + name.toString()+ "\"";
        return returnPrimeBlock(SQL);
    }

    Flight.block getBlock(Date name) {
        String SQL = "SELECT * FROM \"" + name.toString()+ "\"";
        return getBlock(SQL);
    }

    void deleteBlock(int id, Date name){
        String SQL = "DELETE FROM \""+name.toString()+"\" where id = " + id;
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(SQL);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    Flight.block getNonPrimeBlock(Date name) {
        String SQL = "SELECT * FROM \"" + name.toString()+ "\"";
        return returnNonPrimeBlock(SQL);
    }

    void createResultTable(){
        String SQL1 = "drop TABLE if exists \"decompositionGreedy\"";
        String SQL2 = "CREATE TABLE \"decompositionGreedy\"\n" +
                "(\n" +
                "  \"blockId\" BIGINT, \"adId\" INTEGER, \"spotDate\" DATE, \"grpPlan\" DOUBLE PRECISION" +
                ")";
        createSomeTable(SQL1, SQL2);
    }

    void addResultLine (int blockId, int adId, Date spotDate, double grp) {
        String SQL = "INSERT INTO \"decompositionGreedy\" VALUES " +
                "(" + blockId + "," + adId +",\'"+ spotDate + "\',"+grp+")";
        insertSomeLine(SQL);
    }

    Flight.block getWishedBlock(int id, Date name){
        String SQL = "SELECT * FROM \"" + name.toString() + "\" WHERE id = "+ id;
        return getBlock(SQL);
    }

}
