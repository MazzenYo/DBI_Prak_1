import java.sql.*;
import java.util.Scanner;

public class Benchmark_DB {

    public static void main(String[] args) throws SQLException
    {
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter n:");
        int n = scan.nextInt();
        scan.close();
        Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/benchmark-datenbank", "dbi", "dbi_pass");
        assert false;
        Statement stmt = conn.createStatement();
        try
        {

            System.out.println("\nConnected to benchmark database!\n");
            String sqlFillBranches=
                    "update `benchmark-datenbank`.branches set branchid = ?, branchname = ? ,balance = ? , address = ?"
                    ;
            int balance = 0;
            String branchname = "ABCDEFGHIJKLMNOPQRST";
            String address = "ABCDEFGHIJKLMNOPQRSTUVXYZABCDEFGHIJKLMNOPQRSTUVXYZABCDEFGHIJKLMNOPQRSTUV";
            PreparedStatement sqlFill = conn.prepareStatement(sqlFillBranches);
            for (int i = 1; i < n; i++)
            {
                sqlFill.setInt(1, i);
                sqlFill.setString(2, branchname);
                sqlFill.setInt(3, balance);
                sqlFill.setString(4, address);
                sqlFill.executeUpdate();
                System.out.println("Updates Branches");
            }
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

