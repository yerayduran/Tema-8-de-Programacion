package boletin1.ejercicio1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.sql.*;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        Properties properties = new Properties();
        try (BufferedReader br = new BufferedReader(new FileReader(Path.of("src/main/resources/classicmodels.properties").toFile()))) {
            // Cargamos el contenido del properties
            properties.load(br);
            // Obtenemos los datos del .properties
            String conexionString = properties.getProperty("db.url");
            String usuario = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");
            // Creamos la conexión con los datos recogidos anteriormente
            try (Connection connection = DriverManager.getConnection(conexionString, usuario, password)) {
                Statement statement = connection.createStatement();
                // Cargamos los resultados de la consulta
                ResultSet rs = statement.executeQuery("SELECT * FROM productlines");
                // Mientras haya filas continúa
                while (rs.next()) {
                    // Imprime la línea del producto concreto de esa fila que está repasando
                    System.out.println(rs.getString("productLine"));
                }
            }
        } catch (InvalidPathException | IOException  | SQLException e) {
            System.out.println(e);
        }
    }
}