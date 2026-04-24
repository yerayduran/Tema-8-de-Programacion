package boletin1.ejercicio4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try {
            System.out.print("Introduce el precio máximo: ");
            int precio = sc.nextInt();
            sc.nextLine();

            System.out.print("Introduce la letra inicial: ");
            String primeraLetra = sc.nextLine().trim().toUpperCase();

            mostrarProductosValorLetra(precio, primeraLetra);

        } catch (Ejercicio4Exception e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Error de entrada: " + e.getMessage());
        } finally {
            sc.close();
        }
    }

    public static void mostrarProductosValorLetra(int precio, String primeraLetra) throws Ejercicio4Exception {
        if (precio < 0) {
            throw new Ejercicio4Exception("El precio no puede ser negativo");
        }
        if (primeraLetra.isEmpty()) {
            throw new Ejercicio4Exception("La letra inicial no puede estar vacía");
        }

        Properties properties = new Properties();
        try (BufferedReader br = new BufferedReader(new FileReader(Path.of("src/main/resources/classicmodels.properties").toFile()))) {
            properties.load(br);
            String url = properties.getProperty("db.url");
            String usuario = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");

            try (Connection connection = DriverManager.getConnection(url, usuario, password)) {
                PreparedStatement ps = connection.prepareStatement("SELECT productCode, LOWER(productName) AS productName, buyPrice FROM products WHERE buyPrice < ? AND productName LIKE ?");
                ps.setInt(1, precio);
                ps.setString(2, primeraLetra + "%");

                ResultSet rs = ps.executeQuery();
                int count = 0;

                System.out.printf("Productos con precio < %.2f€ y nombre empezando por '%s':%n", (double) precio, primeraLetra);

                while (rs.next()) {
                    String codigo = rs.getString("productCode");
                    String nombre = rs.getString("productName");
                    double precioCompra = rs.getDouble("buyPrice");
                    System.out.printf("%s - %s - (%.2f€)%n", codigo, nombre, precioCompra);
                    count++;
                }

                if (count == 0) {
                    System.out.println("No se encontraron productos que cumplan los criterios.");
                } else {
                    System.out.printf("Total: %d productos encontrados.%n", count);
                }
            }
        } catch (InvalidPathException | IOException | SQLException e) {
            System.out.println(e);
        }
    }
}