import java.sql.*;
import java.util.Scanner;
import java.util.Random;

/**
 * The type Benchmark db.
 */
public class Benchmark_DB {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws SQLException the sql exception
     */
    public static void main(String[] args) throws SQLException {

        //scanner für die Eingabe des N-Wertes
        Scanner scan = new Scanner(System.in);
        System.out.println("Enter n:");
        int n = scan.nextInt();
        scan.close();

        //Verbindungsaufbau über jdbc-Treiber
        //übergebe der Verbindung die Servereinstellung, dass er BatchedStatements überschreiben darf
        //verhindert die erneute Erstellung des servers von prepared Statements, da wir schon prepared Statements übergeben
        Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/benchdb?rewriteBatchedStatements=true&useServerPrepStmts=false", "dbi", "dbi_pass");
        System.out.println("\nSuccessfully connected to benchmark database!\n");
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
            preparedStatement= conn.prepareStatement(SQL_Insert_accounts);
            for(int i = 1; i <= n * 100000; ++i) {
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
            //Committe alle changes zur Connection
            conn.commit();

            System.out.println("\nDisconnected!\n");
        } catch (SQLException e) {
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
}

