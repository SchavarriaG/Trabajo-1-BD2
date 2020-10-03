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

    String ciudad = "";
    int margen = 0;
    int diametro = 10;

    public void setProps(String c, int m) {
        this.ciudad = c;
        this.margen = m;
    }

    public void paint(Graphics g) {
        Dimension d = getSize();
        int x = d.width-margen-1;
        int y = d.height-margen-1;


        g.setColor(Color.red);
        g.drawLine(margen, margen, margen, y);
        g.drawLine(margen, margen, x, margen);
        g.drawLine(x, margen, x, y);
        g.drawLine(margen, y, x, y);
        g.drawString(ciudad, 350, 75);

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
            System.out.println("No hay conexi�n con la base de datos.");
            return;
        }

        String query = "SELECT t2.*"
                + "     FROM VVCITY t1, TABLE(t1.ventas) t2"
                + "     WHERE t1.ciudad = '" + ciudad + "'";
        try {
            resultado = sentencia.executeQuery(query);
            while (resultado.next()) {
                g.setColor(Color.blue);
                g.fillOval(resultado.getInt("x") + margen - diametro/2, resultado.getInt("y") + margen - diametro/2, diametro, diametro);
                g.setColor(Color.black);
                g.drawString("$" + resultado.getString("v"), resultado.getInt("x") - 15 + margen, resultado.getInt("y") - 8 + margen);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
