import java.io.*;
import java.sql.*;

public class Vertriebsagenten {
    protected static BufferedReader stdin =
            new BufferedReader(new InputStreamReader(System.in));

    // helper method for reading input
    protected static String getInput(String prompt)
    {
        try
        {
            System.out.println(prompt);
            return stdin.readLine();
        }
        catch (IOException e)
        {
            System.err.println(e);
            return null;
        }
    }  // end getInput

    // main program
    public static void main(String[] args) throws SQLException
    {
            Connection         conn = null;
            PreparedStatement  stmt = null;
            ResultSet          rs   = null;

            try
            {
                conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/benchdb", "dbi", "dbi_pass");
                conn.setAutoCommit(false);
                System.out.println("\nConnected to sample database!\n");

                stmt = conn.prepareStatement(
                        "select history.accid , accounts.name, delta, cmmnt " +
                                "from history,accounts " +
                                "where history.accid = ? and accounts.accid=history.accid");

                String accID = getInput("Please enter accID: ");

                while ((accID != null) && (accID.length() != 0)) {
                    stmt.setString(1, accID);
                    rs = stmt.executeQuery();

                    System.out.println();
                    System.out.println("accid|        name|    delta   |       cmmt ");
                    System.out.println("---|-------------|-----------|-----------|");

                    while (rs.next()) {
                        System.out.println(rs.getInt(1) + "\t\t" +
                                rs.getString(2) + "\t\t" + rs.getInt(3)
                                + "\t\t" + rs.getString(4));
                    }
                    rs.close();
                    conn.commit();
                    stmt.clearParameters();
                    accID = getInput("\nPlease enter product id: ");
                }

                stmt.close();
                conn.close();
                System.out.println("\nDisconnected!\n");

            }
            catch (SQLException e)
            {
                System.err.println(e);
                System.exit(1);
            }
            finally // close used resources
            {
                if (rs!=null)   rs.close();
                if (stmt!=null) stmt.close();
                if (conn!=null) conn.close();
            }
        }
    }  // end main
