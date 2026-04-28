package boletin1.ejercicio9;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.sql.*;
import java.util.Properties;

public class Ejercicio9 {
    public static void main(String[] args) {
        try (Connection connection = establecerConexion(Path.of("src/main/resources/classicmodels.properties"))) {
            borrarInformacionEmpleado("Volvo Model Replicas, Co", connection);

        } catch (Ejercicio9Exception | SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Connection establecerConexion(Path properties) throws Ejercicio9Exception {
        Properties pro = new Properties();
        try (BufferedReader br = new BufferedReader(new FileReader(properties.toFile()))) {
            pro.load(br);
            return DriverManager.getConnection(pro.getProperty("db.url"), pro.getProperty("db.user"), pro.getProperty("db.password"));

        } catch (InvalidPathException | IOException | SQLException e) {
            throw new Ejercicio9Exception(e.getMessage());
        }
    }

    /**
     * Este método va a obtener el customerNumber mediante el nombre, para si
     * borrar de la tabla orders, las órdenes que teNgan este customerNumber y
     * también se eliminará al propio customer de la tabla customers, se hará
     * con una transacción
     *
     * @param nombreCliente el nombre del customer a eliminar
     * @throws Ejercicio9Exception
     */
    public static void borrarInformacionEmpleado(String nombreCliente, Connection connection) throws Ejercicio9Exception {
        try {

            connection.setAutoCommit(false);

            PreparedStatement ps = connection.prepareStatement("SELECT customerNumber FROM customers WHERE customerName LIKE ?");
            ps.setString(1, nombreCliente);
            ResultSet rs = ps.executeQuery();
            int customerNumber;

            if (rs.next()) {

                customerNumber = rs.getInt(1);

            } else {
                throw new Ejercicio9Exception("No se ha encontrado al cliente");
            }

            PreparedStatement ps1 = connection.prepareStatement("SELECT orderNumber FROM orders WHERE customerNumber = ?");
            ps1.setInt(1, customerNumber);
            ResultSet rs1 = ps1.executeQuery();

            while (rs1.next()) {

                PreparedStatement ps2 = connection.prepareStatement("DELETE FROM orderdetails WHERE orderNumber = ?");
                ps2.setInt(1, rs1.getInt(1));
                ps2.executeUpdate();

            }

            PreparedStatement ps3 = connection.prepareStatement("DELETE FROM orders WHERE customerNumber = ?");
            ps3.setInt(1, customerNumber);
            ps3.executeUpdate();

            PreparedStatement ps4 = connection.prepareStatement("DELETE FROM payments WHERE customerNumber = ?");
            ps4.setInt(1, customerNumber);
            ps4.executeUpdate();

            PreparedStatement ps5 = connection.prepareStatement("DELETE FROM customers WHERE customerName = ?");
            ps5.setString(1, nombreCliente);
            ps5.executeUpdate();

            connection.commit();
            connection.setAutoCommit(true);
            System.out.printf("Se ha borrado y actualizado exitosamente el siguiente cliente: %s", nombreCliente);

        } catch (SQLException e) {
            try {

                connection.rollback();
                connection.setAutoCommit(true);
                System.out.println("Problemas");

            } catch (SQLException e1) {
                throw new Ejercicio9Exception(e1.getMessage());
            }
        }
    }
}