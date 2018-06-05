package GeneticAlgorithm;

import main.Flight;
import main.dbConnection;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class genDbConnection extends dbConnection {

    void createTempTable(){
        String SQL2 = "select b.\"id\", f.\"calc_affinity\", b.\"grpPlan\", b.\"isPrimeTime\", b.\"issueDate\", b.\"fixDuration\"\n" +
                "INTO \"genTemp\"\n" +
                "FROM \"ForecastAffinity\" f RIGHT JOIN \"BreakIssue\" b ON f.\"blockId\" = b.id\n" +
                "WHERE b.\"fixDuration\" >= (SELECT min(\"Ad\".duration) FROM \"Ad\")\n" +
                "AND b.\"issueDate\" BETWEEN (SELECT \"beginDate\" FROM \"Flight\") AND (SELECT \"endDate\" FROM \"Flight\")"+
                "ORDER BY calc_affinity DESC ";
        String SQL1 ="DROP TABLE IF EXISTS \"genTemp\"";
        createSomeTable(SQL1, SQL2);
    }

    void dropTempTable(){
        String SQL1 = "DROP TABLE IF EXISTS \"genTemp\"";
        insertSomeLine(SQL1);
    }

    void deleteBlock(int id){
        String SQL = "DELETE FROM \"genTemp\" where id = " + id;
        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(SQL);
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    void createResultTable(){
        String SQL1 = "drop TABLE if exists \"geneticAlgorithm\"";
        String SQL2 = "CREATE TABLE \"geneticAlgorithm\"\n" +
                "(\n" +
                "  \"blockId\" BIGINT, \"adId\" INTEGER, \"spotDate\" DATE, \"grpPlan\" DOUBLE PRECISION" +
                ")";
        createSomeTable(SQL1, SQL2);
    }

    void addResultLine (int blockId, int adId, Date spotDate, double grp) {
        String SQL = "INSERT INTO \"geneticAlgorithm\" VALUES " +
                "(" + blockId + "," + adId +",\'"+ spotDate + "\',"+grp+")";
        insertSomeLine(SQL);
    }

    Flight.block getBlock() {
        String SQL = "SELECT * FROM \"genTemp\"";
        return getBlock(SQL);
    }

    List<Flight.block> getAllBlocks(){
        String SQL = "SELECT * FROM \"genTemp\"";
        List<Flight.block> res = new ArrayList<>();
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(SQL);
            while (rs.next()) {
                Flight.block temp = new Flight.block(rs.getInt(1),
                        rs.getDouble(2), rs.getDouble(3),
                        rs.getBoolean(4), rs.getDate(5),
                        rs.getInt(6));
                res.add(temp);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
        return res;
    }

    int getBlocksCount(){
        String SQL = "SELECT COUNT(*) FROM \"genTemp\"";
        return executeCount(SQL);
    }

    double getMeanGrp(){
        String SQL = "SELECT avg(\"grpPlan\")*(SELECT min(\"Ad\".duration) " +
                "FROM \"Ad\")/30.0 FROM \"genTemp\"";
        return executeDouble(SQL);
    }

    public void outputResult(String path){
        String SQL = "COPY (SELECT * from \"geneticAlgorithm\") To '" +
                path + "' With CSV HEADER";
        insertSomeLine(SQL);
    }
}
