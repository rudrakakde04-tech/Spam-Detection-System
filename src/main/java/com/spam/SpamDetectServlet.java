package com.spam;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/SpamDetectServlet")
public class SpamDetectServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        // Simulating analysis delay for UI feel
        try { Thread.sleep(1200); } catch (Exception e) {}

        String content = request.getParameter("content");
        List<String> matchedWords = new ArrayList<>();
        int score = 0;

        // --- NEW: DATABASE-DRIVEN DETECTION ---
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Added Unicode support to the connection string for Hindi/Marathi
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/spam_db?useUnicode=true&characterEncoding=UTF8", 
                "root", "Data@123");

            if (content != null) {
                String lowerContent = content.toLowerCase();
                
                // Fetch ALL dynamic keywords from the Admin's table
                String sql = "SELECT word FROM spam_keywords";
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    String dbWord = rs.getString("word").toLowerCase();
                    // Check if the user's message contains the word from DB
                    if (lowerContent.contains(dbWord)) {
                        score++;
                        matchedWords.add(dbWord); 
                    }
                }
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // --- DYNAMIC UI LOGIC ---
        String result;
        String resultImage;
        String colorClass;
        String borderClass;

        if (score >= 2) {
            result = "SPAM DETECTED";
            resultImage = "images/spam.png"; 
            colorClass = "text-red-500";
            borderClass = "border-red-500/30";
        } else if (score == 1) {
            result = "SUSPICIOUS";
            resultImage = "images/suspicious.png"; 
            colorClass = "text-yellow-500";
            borderClass = "border-yellow-500/30";
        } else {
            result = "SAFE CONTENT";
            resultImage = "images/safemail.png"; 
            colorClass = "text-green-500";
            borderClass = "border-green-500/30";
        }

        // Set response encoding for Trilingual output
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        out.println("<!DOCTYPE html><html><head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<script src='https://cdn.tailwindcss.com'></script>");
        out.println("<link rel='stylesheet' href='css/style.css'>");
        out.println("<title>SafeInbox | Result</title></head>");
        out.println("<body class='tech-hero-bg'>"); // Updated to your tech background class

        // Loader
        out.println("<div id='page-loader'><div class='spinner'></div><div class='loader-text'>Analyzing Intelligence...</div></div>");

        out.println("<main class='content-wrapper pt-10'>");
        out.println("<div class='glass-card p-10 text-center border-2 " + borderClass + " max-w-2xl mx-auto'>");
        
        // Image Icon
        out.println("<div class='flex justify-center mb-6'>");
        out.println("    <img src='" + resultImage + "' alt='Result' class='w-32 h-32 object-contain'>");
        out.println("</div>");

        out.println("<h2 class='text-gray-400 uppercase tracking-widest text-xs mb-4'>Security Audit Finished</h2>");
        out.println("<h1 class='text-5xl font-black " + colorClass + " mb-6'>" + result + "</h1>");
        
        // Analysis Result Box
        if (score > 0) {
            out.println("<div class='bg-black/40 rounded-2xl p-6 mb-8 text-left border border-white/10'>");
            out.println("<h3 class='text-blue-400 font-bold text-sm mb-3 uppercase'>🔍 Detection Engine Found:</h3>");
            out.println("<p class='text-gray-400 text-sm mb-4 leading-relaxed italic'>The following trilingual triggers were found in the scanned content:</p>");
            
            out.println("<div class='flex flex-wrap gap-2'>");
            for (String word : matchedWords) {
                out.println("<span class='bg-red-500/10 text-red-400 px-3 py-1.5 rounded-lg border border-red-500/20 text-xs font-bold uppercase tracking-tight'>Triggered: " + word + "</span>");
            }
            out.println("</div>");
            out.println("</div>");
        } else {
            out.println("<p class='text-gray-400 mb-8 leading-relaxed'>No patterns from the global blocklist were identified. Your content is <b>Clean</b>.</p>");
        }

        // Navigation
        out.println("<div class='flex gap-4 justify-center'>");
        out.println("<a href='dashboard.html' class='bg-blue-600 px-10 py-3 rounded-xl font-bold hover:bg-blue-700 transition'>New Audit</a>");
        out.println("<a href='logout.jsp' class='bg-white/5 border border-white/10 px-10 py-3 rounded-xl font-bold hover:bg-white/10 transition'>Logout</a>");
        out.println("</div>");
        
        out.println("</div></main>");

        // Scripts
        out.println("<script>");
        out.println("    window.addEventListener('load', function() {");
        out.println("        const loader = document.getElementById('page-loader');");
        out.println("        setTimeout(() => { loader.classList.add('loader-hidden'); }, 600);");
        out.println("    });");
        out.println("</script>");

        out.println("</body></html>");
    }
}