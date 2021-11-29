import java.sql.*;
import java.util.Scanner;
import java.util.Random;

public class Benchmark_DB {

    public static void main(String[] args) throws SQLException
    {
        // Scanner für N Wert über Tastatur
        long start = System.currentTimeMillis();
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter n:");
        int n = scan.nextInt();
        scan.close();

        final long timeStart = System.currentTimeMillis() / 1000;
        //Verbindungsaufbau
        Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/benchmark-datenbank", "dbi", "dbi_pass");
        Statement stmt = conn.createStatement();
        try
        {

            System.out.println("\nConnected to benchmark database!\n");

            for (int i = 1; i <= n; i++)
            {
                String SQL_Insert = "insert into `benchmark-datenbank`.branches(branchid, branchname, balance, address) values (?,?,?,?)";
                PreparedStatement preparedStatement= conn.prepareStatement(SQL_Insert);
                preparedStatement.setInt(1, i);
                preparedStatement.setString(2, "ABCDEFGHIJKLMNOPQRST");
                preparedStatement.setInt(3, 0);
                preparedStatement.setString(4, "ABCDEFGHIJKLMNOPQRSTUVXYZABCDEFGHIJKLMNOPQRSTUVXYZABCDEFGHIJKLMNOPQRSTUV");
                preparedStatement.executeUpdate();
                System.out.println("Branch");
            }
            for(int i = 1; i <= n * 100000; ++i) {
                Random rand = new Random();
                int randomBranchid= rand.nextInt(n)+1;
                String SQL_Insert = "insert into `benchmark-datenbank`.accounts(accid, balance, branchid, name, address) values (?,?,?,?,?)";
                PreparedStatement preparedStatement= conn.prepareStatement(SQL_Insert);
                preparedStatement.setInt(1, i);
                preparedStatement.setInt(2, 0);
                preparedStatement.setInt(3, randomBranchid);
                preparedStatement.setString(4, "ABCDEFGHIJKLMNOPQRST");
                preparedStatement.setString(5, "ABCDEFGHIJKLMNOPQRSTUVXYZABCDEFGHIJKLMNOPQRSTUVXYZABCDEFGHIJKLMNOPQR");
                preparedStatement.executeUpdate();
                System.out.println("account");
            }
            for(int i = 1; i <= n * 10; ++i) {
                Random rand = new Random();
                int randomBranchid= rand.nextInt(n)+1;
                String sqlQuery = "insert into `benchmark-datenbank`.tellers(tellerid, balance, branchid, tellername, address) values ("+ i + ",0,"+randomBranchid+",'ABCDEFGHIJKLMNOPQRST' ,'ABCDEFGHIJKLMNOPQRSTUVXYZABCDEFGHIJKLMNOPQRSTUVXYZABCDEFGHIJKLMNOPQR') ";
                stmt.executeUpdate(sqlQuery);
            }

            System.out.println("\nDisconnected!\n");
        }
        catch (SQLException e)
        {
            System.err.println(e.toString());
            System.exit(1);
        }
        finally // close used resources
        {
            long finish = System.currentTimeMillis();
            long timeElapsed = (finish - start) / 1000L;
            String msg = "time needed n = "+ n + ": "+ timeElapsed+"  seconds";
            System.out.println(msg);
            /*String sqlQuery = "delete from `benchmark-datenbank`.branches;";
            stmt.executeUpdate(sqlQuery);*/
            if (stmt!=null) stmt.close();
            if (conn!=null) conn.close();

            final long timeEnd = System.currentTimeMillis() / 1000;
            System.out.println("Zeitmessung: " + (timeEnd - timeStart) + " Sekunden.");


        }
    }
}

