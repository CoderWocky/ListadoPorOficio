/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author alumno
 */
@WebServlet(urlPatterns = {"/ListadoPorOficio"})
public class ListadoPorOficio extends HttpServlet {

  private String obtenerTabla(String oficio) throws SQLException {
    DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
    Connection conexion = DriverManager.getConnection(
        "jdbc:oracle:thin:@localhost:1521:XE", "system", "javaoracle");
    
    String query = "SELECT EMP_NO, APELLIDO, DIR, DEPT_NO FROM EMP WHERE OFICIO = ?";
    PreparedStatement sql = conexion.prepareCall(query);
    sql.setString(1, oficio);
    ResultSet registros = sql.executeQuery();
    
    String html = "<table>\n<tr>\n" 
        + "<th>emp_no</th><th>apellido</th><th>dir</th><th>dept_no</th></tr>\n";
    while (registros.next()) {
      html += String.format("<tr><td>%s</td><td>%s</td><td>%d</td><td>%d</td></tr>%n",
          registros.getString("emp_no"), registros.getString("apellido"), 
          registros.getInt("dir"), registros.getInt("dept_no"));
    }
    html += "</table>\n";
    conexion.close();
    return html;
  }
  
  private String renderHTML(String oficio) {
    String html = "<html>\n<head>\n<title>Listados por Oficio</title>\n" +
    "<meta charset='UTF-8'>\n" + 
    "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
    "<link href='css/estilos.css' type='text/css' rel='stylesheet'/>" +
    "</head>\n<body>\n<aside>\n<a href='ListadoPorOficio?ANALISTA'>ANALISTAS</a>" +
    "<a href='ListadoPorOficio?EMPLEADO'>EMPLEADOS</a>\n" +
    "<a href='ListadoPorOficio?DIRECTOR'>DIRECTORES</a>\n" +
    "<a href='ListadoPorOficio?VENDEDOR'>VENDEDORES</a>\n</aside>\n<section>" +
    "<h1>RESULTADOS CON OFICIO</h1><h1>" + oficio + "</h1>\n";
    try {
      html += obtenerTabla(oficio);
    } catch (SQLException ex) {
      html += "<p><strong>Error al obtener los datos</strong></p>\n";
    }
    return html + "</section>\n</body>\n</html>";
  }
  
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    String oficio = request.getQueryString();
    response.setContentType("text/html");
    response.getWriter().println(renderHTML(oficio));
  }
}
