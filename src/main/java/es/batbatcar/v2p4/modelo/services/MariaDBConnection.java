package es.batbatcar.v2p4.modelo.services;

import es.batbatcar.v2p4.exceptions.DatabaseConnectionException;

import org.mariadb.jdbc.Connection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.sql.DriverManager;
import java.sql.SQLException;

@Service
public class MariaDBConnection {

   private static Connection connection;
   private String ip;
   private String database;
   private String userName;
   private String password;

   public MariaDBConnection() {

	   // Modifica estos datos para que se adapte a tu desarrollo
	   
       this.ip = "localhost";
       this.database = "batbatcar";
       this.userName = "marksql";
       this.password = "mml1250";
   }
   
   public Connection getConnection() {
	   
	   if (connection == null) {
           try {
               String dbURL = "jdbc:mariadb://" + ip + "/" + database;
               Connection connection = (Connection) DriverManager.getConnection(dbURL,userName,password);
               this.connection = connection;
               System.out.println("Conexion valida: " + connection.isValid(20));

           } catch (SQLException ex) {
               throw new RuntimeException(ex.getMessage());
           }
       }

       return this.connection;

   }
}
