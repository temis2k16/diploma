import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

class sgDbConnection extends dbConnection {

    void createTempTable(){
        String SQL2 = "select b.\"id\", f.\"calc_affinity\", b.\"grpPlan\", b.\"isPrimeTime\", b.\"issueDate\", b.\"fixDuration\"\n" +
                "INTO \"sgTemp\"\n" +
                "FROM \"ForecastAffinity\" f RIGHT JOIN \"BreakIssue\" b ON f.\"blockId\" = b.id\n" +
                "WHERE b.\"fixDuration\" >= (SELECT min(\"Ad\".duration) FROM \"Ad\")\n" +
                "AND b.\"issueDate\" BETWEEN (SELECT \"beginDate\" FROM \"Flight\") AND (SELECT \"endDate\" FROM \"Flight\")"+
                "ORDER BY calc_affinity DESC ";
        String SQL1 ="DROP TABLE IF EXISTS \"sgTemp\"";
        createSomeTable(SQL1, SQL2);
    }

    void createResultTable(){
        String SQL1 = "drop TABLE if exists \"simpleGreedy\"";
        String SQL2 = "CREATE TABLE \"simpleGreedy\"\n" +
                "(\n" +
                "  \"blockId\" BIGINT, \"adId\" INTEGER, \"spotDate\" DATE, \"grpPlan\" DOUBLE PRECISION" +
                ")";
        createSomeTable(SQL1, SQL2);
    }

    void dropTempTable(){
        String SQL1 = "DROP TABLE IF EXISTS \"sgTemp\"";
        insertSomeLine(SQL1);
    }

    void addResultLine (int blockId, int adId, Date spotDate, double grp) {
        String SQL = "INSERT INTO \"simpleGreedy\" VALUES " +
                "(" + blockId + "," + adId +",\'"+ spotDate + "\',"+grp+")";
        insertSomeLine(SQL);
    }

    void deleteBlock(int id){
        String SQL = "DELETE FROM \"sgTemp\" where id = " + id;
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(SQL);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    Flight.block getWishedBlock(int id){
        String SQL = "SELECT * FROM \"sgTemp\" WHERE id = "+ id;
        return getBlock(SQL);
    }

    Flight.block getBlock() {
        String SQL = "SELECT * FROM \"sgTemp\"";
        return getBlock(SQL);
    }

    Flight.block getPrimeBlock() {
        String SQL = "SELECT * FROM \"sgTemp\"";
        return returnPrimeBlock(SQL);
    }

    Flight.block getNonPrimeBlock(){
        String SQL = "SELECT * FROM \"sgTemp\"";
        return returnNonPrimeBlock(SQL);
    }
}
