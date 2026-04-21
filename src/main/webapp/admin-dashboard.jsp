<%@ page import="java.sql.*" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Admin Intelligence Panel | Spam Detection System</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="stylesheet" href="css/style.css">
</head>
<body class="tech-hero-bg text-white min-h-screen"> <header class="nav-container">
        <div class="flex items-center gap-2">
            <div class="text-2xl font-bold"><span class="text-blue-400">Spam Detection System </span></div>
        </div>
        <a href="logout.jsp" class="text-gray-400 hover:text-white transition text-sm font-medium">Secure Logout</a>
    </header>

    <main class="content-wrapper pt-24 pb-12">
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-8 w-full max-w-6xl px-6">
            
            <div class="glass-card p-8 border border-white/10">
                <h2 class="text-xl font-bold mb-2 text-blue-400">Update Intelligence</h2>
                <p class="text-gray-500 text-sm mb-6">Add new keywords to the real-time detection engine.</p>
                
                <form action="AddSpamWordServlet" method="POST" class="space-y-4">
                    <div>
                        <label class="block text-xs uppercase tracking-widest text-gray-400 mb-2">Target Keyword</label>
                        <input type="text" name="newWord" required placeholder="e.g. 'jackpot'" 
                               class="w-full bg-black/40 border border-white/10 rounded-xl px-4 py-3 outline-none focus:border-blue-500 transition">
                    </div>
                    <button type="submit" class="w-full bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 rounded-xl transition shadow-lg shadow-blue-900/20">
                        Sync to Engine
                    </button>
                </form>
            </div>

            <div class="lg:col-span-2 space-y-8">
                <div class="grid grid-cols-2 gap-4">
                    <div class="glass-card p-6 border border-green-500/20">
                        <div class="text-gray-400 text-xs uppercase tracking-tighter">Engine Status</div>
                        <div class="text-2xl font-bold text-green-400">ACTIVE</div>
                    </div>
                    <div class="glass-card p-6 border border-blue-500/20">
                        <div class="text-gray-400 text-xs uppercase tracking-tighter">Total Keywords</div>
                        <div class="text-2xl font-bold text-blue-400">
                            <% 
                                try {
                                    Class.forName("com.mysql.cj.jdbc.Driver");
                                    Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/spam_db?useSSL=false", "root", "Data@123");
                                    ResultSet rsCount = c.createStatement().executeQuery("SELECT COUNT(*) FROM spam_keywords");
                                    if(rsCount.next()) out.print(rsCount.getInt(1));
                                    c.close();
                                } catch(Exception e) { out.print("0"); }
                            %>
                        </div>
                    </div>
                </div>

                <div class="glass-card p-8 border border-white/10">
                    <h3 class="text-lg font-bold mb-4">Current Blocklist</h3>
                    <div class="flex flex-wrap gap-2">
                        <% 
                            try {
                                Class.forName("com.mysql.cj.jdbc.Driver");
                                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/spam_db?useSSL=false", "root", "Data@123");
                                ResultSet rs = conn.createStatement().executeQuery("SELECT word FROM spam_keywords ORDER BY word ASC");
                                while(rs.next()) {
                        %>
                            <div class="group flex items-center gap-2 bg-white/5 border border-white/10 hover:border-blue-500/50 px-3 py-1.5 rounded-lg transition">
                                <span class="text-sm text-gray-300"><%= rs.getString("word") %></span>
                            </div>
                        <%      }
                                conn.close();
                            } catch(Exception e) { 
                                out.print("<p class='text-red-400 font-mono text-xs'>Error: " + e.getMessage() + "</p>"); 
                            }
                        %>
                    </div>
                </div>
            </div>
        </div>
    </main>
</body>
</html>