import java.io.IOException;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


public class RegisterUser extends HttpServlet{
	java.sql.Connection conn ;
	java.sql.Statement stmt;
    
	 @Override
	    public void init() throws ServletException {
	    	// TODO Auto-generated method stub
	    	try {
				Class.forName("com.mysql.jdbc.Driver").newInstance();
			} catch (InstantiationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	    	try {
				conn = DriverManager.getConnection
				   ("jdbc:mysql://varun-mysql.cp24jmpehujo.us-west-2.rds.amazonaws.com:3306/testdb", "root", "godisgr8");
				stmt = conn.createStatement();
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
	}
	 
	 @Override
	    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	    		throws ServletException, IOException {
	    	// TODO Auto-generated method stub
	    	
	    	String username = req.getParameter("username");
	    	String password = req.getParameter("password");
	    	String sql = "INSERT INTO testdb.USER_DETAILS VALUES('"+username+"','"+password+"')";   	
	    	
	    	try {
				stmt.execute(sql);			
				
			} catch (SQLException e) {
				e.printStackTrace();
			}
	    	
	    	HttpSession session = req.getSession();
	    	session.setAttribute("username", username);
	    	RequestDispatcher rdp = req.getRequestDispatcher("/login.jsp");
	    	rdp.forward(req, resp);
	    }
}
