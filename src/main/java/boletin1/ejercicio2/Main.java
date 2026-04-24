package boletin1.ejercicio2;

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
            // Cargamos la información del fichero
            properties.load(br);
            // Guardamos los valores que necesitaremos para la conexión
            String conexionString = properties.getProperty("db.url");
            String usuario = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");
            // Establecemos la conexión con los valores obtenidos anteriormente
            try (Connection connection = DriverManager.getConnection(conexionString, usuario, password)) {
                Statement statement = connection.createStatement();
                // Guardamos el precio para posteriormente colocarlo en la consulta
                int precio = 80;
                ResultSet rs = statement.executeQuery("SELECT * FROM products WHERE buyPrice < " + precio);
                // Mientras haya filas continúa
                while (rs.next()) {
                    // Imprimimos el nombre del producto junto con su precio
                    System.out.println("Nombre producto: " + rs.getString("productName") + "," +
                            " Precio: " + rs.getInt("buyPrice"));
                }
            }
        } catch (InvalidPathException | IOException | SQLException e) {
            System.out.println(e);
        }
    }
}