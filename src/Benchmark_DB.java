import java.sql.*;
import java.util.Scanner;

public class Benchmark_DB {

    public static void main(String[] args) throws SQLException
    {
        Scanner scan = new Scanner(System.in);
        int n = scan.nextInt();
        scan.close();
        Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/benchmark-datenbank", "dbi", "dbi_pass");
        assert false;
        Statement stmt = conn.createStatement();
        try
        {

            System.out.println("\nConnected to benchmark database!\n");
            String sqlFillBranches= String.format("insert into `benchmark-datenbank`.branches values(?, );


            conn.commit();
            stmt.close();

            conn.close();
            System.out.println("\nDisconnected!\n");
        }
        catch (SQLException e)
        {
            System.err.println(e.toString());
            System.exit(1);
        }
        finally // close used resources
        {
            if (stmt!=null) stmt.close();
            if (conn!=null) conn.close();
        }
    }
}

