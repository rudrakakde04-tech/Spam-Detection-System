package com.spam;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/AddSpamWordServlet")
public class AddSpamWordServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String newWord = request.getParameter("newWord");

        if (newWord != null && !newWord.trim().isEmpty()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                // IMPORTANT: Ensure password is Data@123
                Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/spam_db?useSSL=false", "root", "Data@123");

                String sql = "INSERT IGNORE INTO spam_keywords (word) VALUES (?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, newWord.trim().toLowerCase());
                
                ps.executeUpdate();
                con.close();
                
                // Redirect back to dashboard to see the new word in the list
                response.sendRedirect("admin-dashboard.jsp");
            } catch (Exception e) {
                e.printStackTrace();
                response.sendRedirect("admin-dashboard.jsp?error=database");
            }
        }
    }
}