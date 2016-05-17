import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import db.Comments;
import db.DynamoDBAssignment4;


public class CommentsServlet extends HttpServlet{

	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String fileName = req.getParameter("filename");
		HttpSession session = req.getSession();
		String username = (String) session.getAttribute("username");		
		String[] attrName = {"FileName"};
		String[] attrVal = {fileName};
		List<Comments> list = DynamoDBAssignment4.scanOnAttributeValue("COMMENTS","Serial_Nos",attrName,attrVal,username,"FileName","Comments");
		req.setAttribute("comments", list);		
		RequestDispatcher rdp = req.getRequestDispatcher("/showComments.jsp");
    	rdp.forward(req, resp);    		
		
	}
}
