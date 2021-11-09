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
            System.err.println(e.toString());
            return null;
        }
    }  // end getInput

    // straightforward helper method for connecting to a database
    // (please see the JDBC DataSource interface for a more preferred,
    // however slightly more complex and abstract way for getting
    // a JDBC Connection object!)

    // main program
    public static void main(String[] args) throws SQLException
    {
            Connection         conn = null;
            PreparedStatement  stmt = null;
            ResultSet          rs   = null;

            try
            {
                conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dbi", "dbi", "dbi_pass");
                conn.setAutoCommit(false);
                System.out.println("\nConnected to sample database!\n");

                stmt = conn.prepareStatement(
                        "select agents.aid, agents.aname, sum(dollars) " +
                                "from dbi.orders, dbi.agents " +
                                "where pid=? and orders.aid=agents.aid " +
                                "group by agents.aid, agents.aname " +
                                "order by sum(dollars) DESC");

                String productID = getInput("Please enter product id: ");

                while ((productID!=null) && (productID.length()!=0))
                {
                    stmt.setString(1,productID);
                    rs = stmt.executeQuery();

                    System.out.println();
                    System.out.println("AID|        ANAME|    DOLLARS");
                    System.out.println("---|-------------|----------- du arsch");

                    while (rs.next())
                    {
                        System.out.println(rs.getString(1) + "\t\t" +
                                rs.getString(2) + "\t\t" + rs.getDouble(3));
                    }
                    rs.close();
                    conn.commit();
                    stmt.clearParameters();
                    productID = getInput("\nPlease enter product id: ");
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
                if (rs!=null)   rs.close();
                if (stmt!=null) stmt.close();
                if (conn!=null) conn.close();
            }
        }
    }  // end main
