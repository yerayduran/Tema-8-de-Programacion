package boletin1.ejercicio3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.sql.*;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        try {
            mostrarProductosValorLetra(50, "1");

        } catch (Ejercicio3Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Este método va a mostrar los productos con un precio menor al pasado por
     * parámetros y que empiece por la letra pasada por parámetros
     *
     * @param precio       el precio del que tiene que ser menor el producto
     * @param primeraLetra la letra por la que debe empezar el nombre del producto
     * @throws Ejercicio3Exception
     */
    public static void mostrarProductosValorLetra(int precio, String primeraLetra) throws Ejercicio3Exception {
        Properties properties = new Properties();
        try (BufferedReader br = new BufferedReader(new FileReader(Path.of("src/main/resources/classicmodels.properties").toFile()))) {
            properties.load(br);
            String url = properties.getProperty("db.url");
            String usuario = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");
            try (Connection connection = DriverManager.getConnection(url, usuario, password)) {
                PreparedStatement ps = connection.prepareStatement("SELECT * FROM products WHERE buyPrice < (?)" + " AND productName LIKE (?)");
                // Le pasamos el valor del precio
                ps.setInt(1, precio);
                /* Le pasamos el valor de la primera letra, IMPORTANTE el '%' dentro de la sentencia
                 * SQL es interpretado como una sentencia SQL literal, no como parte del valor o de
                 * la palabra en este caso, así que se le tiene que pasar como parámetros */
                ps.setString(2, primeraLetra + "%");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    System.out.println(rs.getString("productName"));
                }
            }

        } catch (InvalidPathException | IOException | SQLException e) {
            throw new Ejercicio3Exception(e.getMessage());
        }
    }
}