<%
    // Clear all session data
    session.removeAttribute("userEmail");
    session.removeAttribute("role");
    session.invalidate(); 
    
    // Send back to login with a success message
    response.sendRedirect("login.html?status=loggedout");
%>