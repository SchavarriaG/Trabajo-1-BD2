/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MisPaquetes;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.math.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author jdura
 */
public class Punto2 {

    public static void main(String[] args) {
        System.out.println("Comando a ejecutar:\n"
                + "1. Insertar ventas.\n"
                + "2. Mostrar puntos.");

        Scanner scan = new Scanner(System.in);
        int entrada = scan.nextInt();

        switch (entrada) {
            case 1:
                //Datos de prueba para insertar venta.
                String vendedor = "99";
                String ciudad = "cali";
                String[][] ventas = {{"0", "0", "500"},
                {"250", "300", "900"},
                {"250", "300", "500"},
                {"600", "499", "900"}};
                //Datos de prueba para insertar venta.
                insertarVentas(vendedor, ciudad, ventas);
                break;
            case 2:
                //Muestra los puntos de venta para una ciudad dada
                System.out.println("ingrese la ciudad");
                String ciudad2 = "cali";
                mostrarPuntos(ciudad2);
                break;
        }
    }

    //Inserta las ventas para un vendedor en una ciudad
    /**
     * ************************************************************************
     * Hay que modificar el parámetro String[][] ventas dependiendo de la
     * interfaz
    **************************************************************************
     */
    public static boolean insertarVentas(String vendedor, String ciudad, String[][] ventas) {
        Connection conn;
        Statement sentencia;
        Boolean exito = false;

        // Se carga el driver JDBC-ODBC
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (Exception e) {
            System.out.println("No se pudo cargar el driver JDBC");
            return false;
        }

        // Se establece la conexión con la base de datos Oracle Express
        try {
            conn = DriverManager.getConnection("jdbc:oracle:thin:@JEISON-PC:1521:xe", "jeison", "asdf1234");
            sentencia = conn.createStatement();
        } catch (SQLException e) {
            System.out.println("No hay conexión con la base de datos.");
            return false;
        }

        //Se insertan las ventas
        for (String[] venta : ventas) {
            //Se extraen las coordenadas y el valor de la venta
            String x = venta[0];
            String y = venta[1];
            String v = venta[2];
            //Este es el comando a ejecutar, varía dependiendo del caso
            String consulta;

            try {
                //Ya hay registros del vendedor en esa ciudad, entonces se inserta una venta más
                consulta = "INSERT INTO TABLE(  SELECT ventas"
                        + "                     FROM VVCITY"
                        + "                     WHERE CodigoVendedor = " + vendedor + " AND Ciudad = '" + ciudad + "')"
                        + "VALUES(" + x + ", " + y + ", " + v + ")";

                sentencia.executeQuery(consulta);
                System.out.println("datos guardados: El vendedor no había vendido en ese punto");
                exito = true;

            } catch (SQLException e1) {
                //Ya existe una venta en un mismo punto, entonces se actualiza su valor
                //ORA-00001: unique constraint violated
                if (e1.getMessage().startsWith("ORA-00001")) {
                    try {
                        consulta = "UPDATE TABLE(   SELECT ventas"
                                + "                 FROM VVCITY"
                                + "                 WHERE CodigoVendedor = " + vendedor + " AND Ciudad='" + ciudad + "')"
                                + "SET v = v + " + v
                                + "WHERE x = " + x + " AND y = " + y;

                        sentencia.executeQuery(consulta);
                        System.out.println("datos guardados: Ya existía una venta en el mismo punto, valor actualizado");
                        exito = true;
                    } catch (SQLException e2) {
                        System.out.println("Error: " + e2.getMessage());
                    }

                    //No existen ventas del vendedor en esa ciudad, entonces se inserta la primera
                    //ORA-22908: reference to NULL table value
                } else if (e1.getMessage().startsWith("ORA-22908")) {
                    try {
                        consulta = "INSERT INTO VVCITY VALUES(" + vendedor + ", '" + ciudad + "',"
                                + "                         ventas_anidada(ventas_tipo(" + x + ", " + y + ", " + v + ")))";

                        sentencia.executeQuery(consulta);
                        System.out.println("datos guardados: No existían ventas del vendedor en esa ciudad");
                        exito = true;
                    } catch (SQLException e3) {
                        System.out.println("Error: " + e3.getMessage());
                    }

                } else {
                    System.out.println("Error: " + e1.getMessage());
                }
            }
        }

        // Se cierra la conexion con la BD
        try {
            conn.close();
            System.out.println("Conexión cerrada");
        } catch (SQLException e) {
            System.out.println("Error al cerrar conexión");
        }
        return exito;
    }

    public static void mostrarPuntos(String ciudad) {
        Grafico DrawWindow = new Grafico();

        int margen = 100;
        DrawWindow.setProps(ciudad, margen);
        DrawWindow.setSize(500 + 2 * margen, 500 + 2 * margen);
        DrawWindow.setResizable(false);
        DrawWindow.setLocation(200, 50);
        DrawWindow.setTitle("Pintando figuras almacenadas en la BD");
        DrawWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        DrawWindow.setVisible(true);
    }
}
