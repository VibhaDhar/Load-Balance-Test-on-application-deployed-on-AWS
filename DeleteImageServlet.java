import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;

import db.DynamoDBAssignment4;


public class DeleteImageServlet extends HttpServlet{

	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		AWSCredentials credentials = new BasicAWSCredentials("AKIAI3EDWIHLI7GM5UXQ","4BjJGCU4VbHL66wPpwx+FvqaIS6YXOUySgatchFG");
		AmazonS3 s3 = new AmazonS3Client(credentials);
		AmazonS3Client s3Client = new AmazonS3Client(credentials);
		
		String fileName = req.getParameter("filename");
		String username = req.getParameter("username");
		
		DynamoDBAssignment4.deleteFile(fileName);
		s3Client.deleteObject("varun-bucket", fileName);
		
		resp.sendRedirect("/showAll.jsp");
		
	}
}
