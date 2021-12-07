import java.sql.*;
import java.util.Scanner;
import java.util.Random;

public class Benchmark_DB {

    public static Connection connect() throws Exception {
        Connection conn = null;
        try {
                conn =  DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/", "dbi", "dbi_pass");
               System.out.println("\nSuccessfully connected to benchmark database!\n");
        }
        catch(SQLException ex){
            System.err.println(ex.getMessage());
        }
        return conn;
    }

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws SQLException the sql exception
     */
    public static void main(String[] args, Connection conn) throws SQLException {

        //scanner für die Eingabe des N-Wertes
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter n:");
        int n = scan.nextInt();
        scan.close();

        //Verbindungsaufbau über jdbc-Treiber
        //übergebe der Verbindung die Servereinstellung, dass er BatchedStatements überschreiben darf
        //verhindert die erneute Erstellung des servers von prepared Statements, da wir schon prepared Statements übergeben

        conn.setAutoCommit(false);
        System.out.println("Starting time measurement!\n");
        long start = System.currentTimeMillis();
        PreparedStatement preparedStatement = null;

        try {
            //erzeuge String der in dem prepared Statement übernommen wird
            String SQL_Insert_branches = "insert into `benchdb`.branches(branchid, branchname, balance, address) values (?,?,?,?)";
            preparedStatement = conn.prepareStatement(SQL_Insert_branches);
            //schleife die die preparedStatements mit den Werten ergänzt
            for (int i = 1; i <= n; i++) {
                preparedStatement.setInt(1, i);
                preparedStatement.setString(2, "ABCDEFGHIJKLMNOPQRST");
                preparedStatement.setInt(3, 0);
                preparedStatement.setString(4, "ABCDEFGHIJKLMNOPQRSTUVXYZABCDEFGHIJKLMNOPQRSTUVXYZABCDEFGHIJKLMNOPQRSTUV");
                preparedStatement.executeUpdate();
            }

            String SQL_Insert_accounts = "insert into `benchdb`.accounts(accid, balance, branchid, name, address) values (?,?,?,?,?)";
            preparedStatement = conn.prepareStatement(SQL_Insert_accounts);
            for (int i = 1; i <= n * 100000; ++i) {
                Random rand = new Random();
                int randomBranchid = rand.nextInt(n) + 1;
                preparedStatement.setInt(1, i);
                preparedStatement.setInt(2, 0);
                preparedStatement.setInt(3, randomBranchid);
                preparedStatement.setString(4, "ABCDEFGHIJKLMNOPQRST");
                preparedStatement.setString(5, "ABCDEFGHIJKLMNOPQRSTUVXYZABCDEFGHIJKLMNOPQRSTUVXYZABCDEFGHIJKLMNOPQR");

                //füge das prepared Statement zu einem Batch hinzu
                preparedStatement.addBatch();
            }
            //füge den Batch der Datenbank hinzu
            preparedStatement.executeBatch();


            String SQL_Insert_tellers = "insert into `benchdb`.tellers(tellerid, balance, branchid, tellername, address) values (?,?,?,?,?) ";
            preparedStatement = conn.prepareStatement(SQL_Insert_tellers);
            for (int i = 1; i <= n * 10; ++i) {
                Random rand = new Random();
                int randomBranchid = rand.nextInt(n) + 1;
                preparedStatement.setInt(1, i);
                preparedStatement.setInt(2, 0);
                preparedStatement.setInt(3, randomBranchid);
                preparedStatement.setString(4, "ABCDEFGHIJKLMNOPQRST");
                preparedStatement.setString(5, "ABCDEFGHIJKLMNOPQRSTUVXYZABCDEFGHIJKLMNOPQRSTUVXYZABCDEFGHIJKLMNOPQR");
                preparedStatement.executeUpdate();
            }

                //Committe alle changes zur Connection
                conn.commit();

                System.out.println("\nDisconnected!\n");
            } catch(SQLException e){
                System.err.println(e.toString());
                System.exit(1);
            } finally // close used resources
            {
                preparedStatement.close();
                conn.close();
                //beende die Zeitmessung
                long finish = System.currentTimeMillis();
                long timeElapsed = (finish - start) / 1000L;
                String msg = "Ended time measurement: \n Time needed for n = " + n + ": " + timeElapsed + "  seconds";
                System.out.println(msg);


            }
    }
    public static int kontostands_TX(Connection conn, int accid) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT accounts.balance FROM accounts WHERE accounts.accid = " + accid + ";");

        rs.next();
        int balance = rs.getInt(1);
        return balance;
    }

    public static int einzahlungs_TX(int accid, int tellerid, int branchid, int delta, Connection conn) throws SQLException {

            Statement stmt = conn.createStatement();

            stmt.executeUpdate("UPDATE branches SET branches.balance = (branches.balance + " + delta + ") WHERE branches.branchid = " + branchid);
            stmt.executeUpdate("UPDATE tellers SET tellers.balance = (tellers.balance + " + delta + ") WHERE tellers.tellerid = " + tellerid);
            stmt.executeUpdate("UPDATE accounts SET accounts.balance = (accounts.balance + " + delta + ") WHERE accounts.accid = " + accid);
            stmt.executeUpdate("INSERT INTO history VALUES(" + accid + ", " + tellerid + ", " + delta + ", " + branchid + ", " + kontostands_TX(conn, accid) + ", 'abcdefghijklmnopqrstuvwxyzabcd');");

            return kontostands_TX(conn, accid);
    }

    public static int analyse_TX(int delta, Connection conn) throws SQLException {

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(history.accid) FROM history where history.delta = " + delta);

            rs.next();
            int numberOfPayments = rs.getInt(1);
            return numberOfPayments;
    }


}



  /*  PreparedStatement pstmt = null;
			pstmt = conn.prepareStatement("CREATE OR REPLACE FUNCTION kontostandsTX(id INT)\r\n"
                    + "RETURNS int\r\n"
                    + "DETERMINISTIC\r\n"
                    + "BEGIN\r\n"
                    + "  DECLARE i INT DEFAULT 1;\r\n"
                    + "  SET i = 0;\r\n"
                    + "  SELECT accounts.balance INTO i FROM accounts WHERE accounts.accid = id;\r\n"
                    + "  RETURN i;\r\n"
                    + "END\r\n");
                    pstmt.executeUpdate();

                    pstmt = conn.prepareStatement("CREATE OR REPLACE FUNCTION einzahlungsTX(accid INT, tellerid INT, branchid INT, delta INT)\r\n"
                    + "RETURNS int\r\n"
                    + "DETERMINISTIC\r\n"
                    + "BEGIN\r\n"
                    + "  DECLARE i INT DEFAULT 1;\r\n"
                    + "  SET i = 0;\r\n"
                    + "  UPDATE branches SET branches.balance = (branches.balance + delta) WHERE branches.branchid = branchid;\r\n"
                    + "  UPDATE tellers SET tellers.balance = (tellers.balance + delta) WHERE tellers.tellerid = tellerid;\r\n"
                    + "  UPDATE accounts SET accounts.balance = (accounts.balance + delta) WHERE accounts.accid = accid;\r\n"
                    + "  SELECT accounts.balance INTO i FROM accounts WHERE accounts.accid = accid;\r\n"
                    + "  INSERT INTO history VALUES(accid, tellerid, delta, branchid, i, 'abcdefghijklmnopqrstuvwxyzabcd');\r\n"
                    + "  RETURN i;\r\n"
                    + "END\r\n");
                    pstmt.executeUpdate();

                    pstmt = conn.prepareStatement("CREATE OR REPLACE FUNCTION analyseTX(delta INT)\r\n"
                    + "RETURNS int\r\n"
                    + "DETERMINISTIC\r\n"
                    + "BEGIN\r\n"
                    + "  DECLARE i INT DEFAULT 1;\r\n"
                    + "  SET i = 0;\r\n"
                    + "  SELECT COUNT(history.accid) INTO i FROM history where history.delta = delta;\r\n"
                    + "  RETURN i;\r\n"
                    + "END\r\n");
                    pstmt.executeUpdate();
*/