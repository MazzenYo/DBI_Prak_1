import java.sql.*;

/**
 * The type Init database.
 */
public class initDatabase {
	/**
	 * The entry point of application.
	 *
	 * @param args the input arguments
	 * @throws SQLException the sql exception
	 */
	public static void main(String[] args) throws SQLException {
		//baue Verbindung zur Datenbank auf
		Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306", "dbi", "dbi_pass");

		//erzeuge Statements für die oben verbundene Datenbank
		try (conn; Statement stmt = conn.createStatement()) {

			//wenn das schema 'benchdb' schon existiert wird es gedroppt
			String sqlDropSchemaIfExists = "DROP schema if exists benchdb;";
			stmt.executeUpdate(sqlDropSchemaIfExists);
			System.out.println("\nDrop schema if exists\n");

			//erzeuge das schema benchdb
			String sqlCreateSchema = "create schema benchdb;";
			stmt.executeUpdate(sqlCreateSchema);
			System.out.println("\nCreated schema\n");

			System.out.println("\nConnected to benchmark database!\n");

			//erzeuge Tabelle 'branches' in dem schema 'benchdb'
			String sqlBranches =
					"create table `benchdb`.branches\n" +
							"( branchid int not null,\n" +
							" branchname char(20) not null,\n" +
							" balance int not null,\n" +
							" address char(72) not null,\n" +
							" primary key (branchid) );";
			stmt.executeUpdate(sqlBranches);
			System.out.println("\nCreated Table branches\n");


			//erzeuge Tabelle 'accounts' in dem schema 'benchdb'

			String sqlAccounts =
					"create table `benchdb`.accounts\n" +
							"( accid int not null,\n" +
							" name char(20) not null,\n" +
							" balance int not null,\n" +
							"branchid int not null,\n" +
							"address char(68) not null,\n" +
							"primary key (accid),\n" +
							"foreign key (branchid) references `benchdb`.branches(branchid) );";
			stmt.executeUpdate(sqlAccounts);
			System.out.println("\nCreated Table accounts\n");

			//erzeuge Tabelle 'tellers' in dem schema 'benchdb'
			String sqlTellers =
					"create table `benchdb`.tellers\n" +
							"( tellerid int not null,\n" +
							" tellername char(20) not null,\n" +
							" balance int not null,\n" +
							" branchid int not null,\n" +
							" address char(68) not null,\n" +
							" primary key (tellerid),\n" +
							" foreign key (branchid) references `benchdb`.branches(branchid) ); ";
			stmt.executeUpdate(sqlTellers);
			System.out.println("\nCreated Table tellers\n");

			//erzeuge Tabelle 'history' in dem schema 'benchdb'

			String sqlHistory =
					"create table `benchdb`.history\n" +
							"( accid int not null,\n" +
							" tellerid int not null,\n" +
							" delta int not null,\n" +
							" branchid int not null,\n" +
							" accbalance int not null,\n" +
							" cmmnt char(30) not null,\n" +
							" foreign key (accid) references `benchdb`.accounts(accid),\n" +
							" foreign key (tellerid) references `benchdb`.tellers(tellerid),\n" +
							" foreign key (branchid) references `benchdb`.branches(branchid) );";
			stmt.executeUpdate(sqlHistory);
			System.out.println("\nCreated Table history\n");


			conn.close();  //close connection

            System.out.println("\nDisconnected!\n");

		} catch (SQLException e) {
			System.err.println(e.toString());
			System.exit(1);
		}
	}
}
