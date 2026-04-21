package com.spam;

import java.io.IOException;
import java.sql.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/ForgotPassServlet")
public class ForgotPassServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("email");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/spam_db?useSSL=false", "root", "Data@123");

            // 1. Fetch user details
            String sql = "SELECT username, password FROM users WHERE email=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String userName = rs.getString("username");
                String userPassword = rs.getString("password");
                
                // --- PROFESSIONAL SUBJECT LINE ---
                String emailSubject = "Action Required: Account Recovery for " + userName;

                // --- PROFESSIONAL HTML BODY ---
                String professionalBody = 
                    "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #e2e8f0; border-radius: 12px; padding: 25px; color: #1e293b;'>" +
                        "<div style='text-align: center; border-bottom: 2px solid #3b82f6; padding-bottom: 15px; margin-bottom: 20px;'>" +
                            "<h1 style='color: #2563eb; margin: 0; font-size: 28px;'>🛡️ Spam Detection System </h1>" +
                            "<p style='color: #64748b; font-size: 12px; text-transform: uppercase; letter-spacing: 1px;'>Security Intelligence Engine</p>" +
                        "</div>" +
                        "<p>Hi <b>" + userName + "</b>,</p>" +
                        "<p>We received a request to recover your account credentials. You can use the information below to log back into your dashboard:</p>" +
                        "<div style='background-color: #f1f5f9; border: 1px dashed #94a3b8; padding: 20px; text-align: center; margin: 25px 0; border-radius: 10px;'>" +
                            "<span style='display: block; font-size: 11px; color: #64748b; text-transform: uppercase; margin-bottom: 5px;'>Your Secure Password</span>" +
                            "<span style='font-size: 26px; font-weight: bold; color: #0f172a; letter-spacing: 1px;'>" + userPassword + "</span>" +
                        "</div>" +
                        "<p style='font-size: 13px; color: #475569; line-height: 1.5;'><b>Safety Reminder:</b> If you did not request this, please secure your account immediately. We recommend changing your password after logging in.</p>" +
                        "<hr style='border: 0; border-top: 1px solid #e2e8f0; margin: 25px 0;'>" +
                        "<p style='font-size: 11px; color: #94a3b8; text-align: center;'>This is an automated message from the Spam Detection System.<br>&copy; 2026 Spam Detection System | Automated Recovery Protocol</p>" +
                    "</div>";

                // 2. Call MailUtil with the Dynamic Subject
                boolean isSent = MailUtil.sendEmail(email, emailSubject, professionalBody);
                
                if (isSent) {
                    response.sendRedirect("login.html?msg=emailsent");
                } else {
                    response.sendRedirect("error.html");
                }
            } else {
                response.sendRedirect("forgot-password.html?error=notfound");
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.html");
        }
    }
}