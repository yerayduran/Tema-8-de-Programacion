import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


/*
Muestra de mi mySql de la database classicmodels y Selecciona la tabla de productlines para mostrar toda la categoria de productline
 */
public class TestConnection {
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://127.0.0.3:3306/classicmodels?serverTimezone=UTC";
        String usuario = "";
        String password = "";

        Properties propiedades = new Properties();
        Path rutaConfig = Path.of("config.properties");

        if (!Files.exists(rutaConfig)) {
            throw new Exception("El archivo config.properties no se encuentra en la ruta: " + rutaConfig.toAbsolutePath());
        }

        try (BufferedReader lector = Files.newBufferedReader(rutaConfig, StandardCharsets.UTF_8)) {
            propiedades.load(lector);
            usuario = propiedades.getProperty("usuario");
            password = propiedades.getProperty("password");
        } catch (IOException e) {
            System.out.println("ERROR: No se ha podido leer el archivo config.properties. Detalles técnicos: " + e.getMessage());
            throw e;
        }

        String sql = "SELECT productLine FROM productlines";

        try (Connection conexion = DriverManager.getConnection(url, usuario, password);
             Statement st = conexion.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("Conexión establecida con éxito");
            System.out.println("Categorías de productLines:");

            while (rs.next()) {
                String categoria = rs.getString("productLine");
                System.out.println("- " + categoria);
            }

        } catch (SQLException e) {
            System.err.println("Se ha cometido un error durante la operación con la base de datos:");
            e.printStackTrace();
        }
    }
}