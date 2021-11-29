import java.sql.*;
import java.util.Scanner;
import java.util.Random;

public class Benchmark_DB {

    public static void main(String[] args) throws SQLException
    {


        Scanner scan = new Scanner(System.in);
        System.out.println("Enter n:");
        int n = scan.nextInt();
        scan.close();
        /*rewriteBatchedStatements=true*/
        Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/benchmark-datenbank?rewriteBatchedStatements=true&useServerPrepStmts=false", "dbi", "dbi_pass");
        System.out.println("\nConnected to benchmark database!\n");
        conn.setAutoCommit(false);
        long start = System.currentTimeMillis();
        try
        {
            String SQL_Insert_branches = "insert into `benchmark-datenbank`.branches(branchid, branchname, balance, address) values (?,?,?,?)";
            PreparedStatement preparedStatement= conn.prepareStatement(SQL_Insert_branches);
            for (int i = 1; i <= n; i++)
            {
                preparedStatement.setInt(1, i);
                preparedStatement.setString(2, "ABCDEFGHIJKLMNOPQRST");
                preparedStatement.setInt(3, 0);
                preparedStatement.setString(4, "ABCDEFGHIJKLMNOPQRSTUVXYZABCDEFGHIJKLMNOPQRSTUVXYZABCDEFGHIJKLMNOPQRSTUV");
                preparedStatement.executeUpdate();
            }
            String SQL_Insert_accounts = "insert into `benchmark-datenbank`.accounts(accid, balance, branchid, name, address) values (?,?,?,?,?)";
            preparedStatement= conn.prepareStatement(SQL_Insert_accounts);
            for(int i = 1; i <= n * 100000; ++i) {
                Random rand = new Random();
                int randomBranchid= rand.nextInt(n)+1;
                preparedStatement.setInt(1, i);
                preparedStatement.setInt(2, 0);
                preparedStatement.setInt(3, randomBranchid);
                preparedStatement.setString(4, "ABCDEFGHIJKLMNOPQRST");
                preparedStatement.setString(5, "ABCDEFGHIJKLMNOPQRSTUVXYZABCDEFGHIJKLMNOPQRSTUVXYZABCDEFGHIJKLMNOPQR");
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();


            String SQL_Insert_tellers = "insert into `benchmark-datenbank`.tellers(tellerid, balance, branchid, tellername, address) values (?,?,?,?,?) ";
            preparedStatement= conn.prepareStatement(SQL_Insert_tellers);
            for(int i = 1; i <= n * 10; ++i) {
                Random rand = new Random();
                int randomBranchid= rand.nextInt(n)+1;
                preparedStatement.setInt(1, i);
                preparedStatement.setInt(2, 0);
                preparedStatement.setInt(3, randomBranchid);
                preparedStatement.setString(4, "ABCDEFGHIJKLMNOPQRST");
                preparedStatement.setString(5, "ABCDEFGHIJKLMNOPQRSTUVXYZABCDEFGHIJKLMNOPQRSTUVXYZABCDEFGHIJKLMNOPQR");
                preparedStatement.executeUpdate();
            }
            conn.commit();
            preparedStatement.close();
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
            conn.close();
        }
    }
}

