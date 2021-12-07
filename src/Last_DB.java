import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Last_DB {

    public static void main(String[] args) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/?rewriteBatchedStatements=true&useServerPrepStmts=false", "dbi", "dbi_pass");
        System.out.println("\nSuccessfully connected to benchmark database!\n");
        System.out.println("Starting time measurement!\n");
        long start = System.currentTimeMillis();

    }
}
