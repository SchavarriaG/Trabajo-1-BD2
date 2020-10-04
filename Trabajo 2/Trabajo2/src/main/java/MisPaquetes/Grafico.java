/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MisPaquetes;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

/**
 *
 * @author jdura
 */
public class Grafico extends JFrame {

    //Parámetros para graficar
    String ciudad = "";
    int margen = 0;
    int radio = 5;

    //se les da un valor a los parámetros globales
    public void setProps(String c, int m) {
        this.ciudad = c;
        this.margen = m;
    }

    //Esta es la función que grafica, NO CAMBIAR NOMBRE
    public void paint(Graphics g) {
        Dimension d = getSize();
        int x = d.width - margen - 1;
        int y = d.height - margen - 1;

        //Se crea la delimitación de la ciudad (líneas rojas)
        g.setColor(Color.red);
        g.drawLine(margen, margen, margen, y);
        g.drawLine(margen, margen, x, margen);
        g.drawLine(x, margen, x, y);
        g.drawLine(margen, y, x, y);
        g.drawString(ciudad, (x+margen)/2, margen*4/5);

        Connection conn;
        Statement sentencia;
        ResultSet resultado;

        try { // Se carga el driver JDBC-ODBC
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (Exception e) {
            System.out.println("No se pudo cargar el driver JDBC");
            return;
        }

        try { // Se establece la conexi�n con la base de datos Oracle Express
            conn = DriverManager.getConnection("jdbc:oracle:thin:@JEISON-PC:1521:xe", "jeison", "asdf1234");
            sentencia = conn.createStatement();
        } catch (SQLException e) {
            System.out.println("No hay conexión con la base de datos.");
            return;
        }

        String consulta = "";
        //Se obtienen todos los puntos para la ciudad indicada
        try {
            consulta = "SELECT t2.*"
                    + "     FROM VVCITY t1, TABLE(t1.ventas) t2"
                    + "     WHERE t1.ciudad = '" + ciudad + "'";
            resultado = sentencia.executeQuery(consulta);
            //Se grafican los puntos de venta con su respectivo valor
            while (resultado.next()) {
                //puntos
                g.setColor(Color.blue);
                g.fillOval(resultado.getInt("x") + margen - radio,
                        resultado.getInt("y") + margen - radio,
                        radio * 2, radio * 2);
                //valor
                g.setColor(Color.black);
                g.drawString("$" + resultado.getString("v"),
                        resultado.getInt("x") - 10 - radio + margen,
                        resultado.getInt("y") - 3 - radio + margen);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
