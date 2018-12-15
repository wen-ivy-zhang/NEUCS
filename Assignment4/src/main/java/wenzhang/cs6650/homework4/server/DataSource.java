package wenzhang.cs6650.homework4.server;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class DataSource {
	private static DataSource datasource;
	private ComboPooledDataSource cpds;
	
	private DataSource() throws IOException, SQLException, PropertyVetoException {
		cpds = new ComboPooledDataSource();
	    cpds.setDriverClass("com.mysql.cj.jdbc.Driver"); // loads the jdbc driver
	    //cpds.setJdbcUrl("jdbc:mysql://localhost:3306"); // local DB
	    //cpds.setUser("root2"); // local DB
	    //cpds.setPassword("root2"); //local DB
	    cpds.setJdbcUrl("jdbc:mysql://dbmicro.cmmmpqrhcsvu.us-west-2.rds.amazonaws.com:3306"); // AWS RDS DB micro
	    //cpds.setJdbcUrl("jdbc:mysql://wendbmedium.cmmmpqrhcsvu.us-west-2.rds.amazonaws.com:3306"); // AWS RDS DB medium
	    //cpds.setJdbcUrl("jdbc:mysql://35.230.48.230:3306"); // Google DB
	    cpds.setUser("root"); // remote
	    cpds.setPassword("1qaz!QAZ"); // remote

	    cpds.setMinPoolSize(5);
	    cpds.setAcquireIncrement(10);
	    cpds.setMaxPoolSize(60); //micro
	    //cpds.setMaxPoolSize(120); //medium
	}

	public static DataSource getInstance() throws IOException, SQLException, PropertyVetoException {
		if (datasource == null) {
			datasource = new DataSource();
	        return datasource;
	    } 
		else {
			return datasource;
	    }
	}

	public Connection getConnection() throws SQLException {
	    return this.cpds.getConnection();
	}
}
