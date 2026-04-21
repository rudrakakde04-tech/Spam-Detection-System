package com.spam;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Get parameters from the login form
        String selectedRole = request.getParameter("role"); // 'admin' or 'user'
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // 2. Load MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // 3. Connect to Database (using your specific password)
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/spam_db?useSSL=false&allowPublicKeyRetrieval=true", 
                "root", "Data@123");

            // 4. Prepare SQL Query
            // Note: Binary keyword can be added if you want case-sensitive passwords
            String sql = "SELECT username, role FROM users WHERE email=? AND password=? AND role=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ps.setString(2, password);
            ps.setString(3, selectedRole);

            // 5. Execute Query
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // SUCCESS: Create Session and store attributes
                HttpSession session = request.getSession();
                session.setMaxInactiveInterval(30 * 60); // Session expires in 30 mins
                
                session.setAttribute("userRole", selectedRole);
                session.setAttribute("userEmail", email);
                session.setAttribute("userName", rs.getString("username"));
                
                // 6. REDIRECTION LOGIC
                if ("admin".equalsIgnoreCase(selectedRole)) {
                    response.sendRedirect("admin-dashboard.jsp");
                } else {
                    response.sendRedirect("dashboard.html");
                }
            } else {
                // FAILURE UI: Matches your tech-hero-bg and glass-card style
                out.println("<!DOCTYPE html><html><head>");
                out.println("<meta charset='UTF-8'>");
                out.println("<script src='https://cdn.tailwindcss.com'></script>");
                out.println("<link rel='stylesheet' href='css/style.css'>");
                out.println("<title>Access Denied | SafeInbox</title></head>");
                out.println("<body class='tech-hero-bg flex items-center justify-center min-h-screen'>");
                out.println("<div class='glass-card p-10 text-center border border-red-500/30 max-w-md mx-4'>");
                out.println("<div class='text-6xl mb-6'>🚫</div>");
                out.println("<h2 class='text-3xl font-black text-red-500 mb-4'>Access Denied</h2>");
                out.println("<p class='text-gray-400 mb-8'>The credentials provided do not match our records for the <b>" + selectedRole + "</b> access level.</p>");
                out.println("<a href='login.html' class='inline-block bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-10 rounded-xl transition shadow-lg shadow-blue-900/20'>Try Again</a>");
                out.println("</div></body></html>");
            }

            con.close();
        } catch (Exception e) {
            // Error Handling UI
            out.println("<div style='color:white; background:rgba(255,0,0,0.2); padding:20px; border-radius:10px; font-family:sans-serif;'>");
            out.println("<h3>Database Connectivity Error</h3>");
            out.println("<p>" + e.getMessage() + "</p>");
            out.println("</div>");
            e.printStackTrace();
        }
    }
}