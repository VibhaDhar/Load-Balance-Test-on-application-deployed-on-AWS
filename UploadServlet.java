import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;

import db.DynamoDBAssignment4;



public class UploadServlet extends HttpServlet {
	DynamoDBAssignment4 dba4;
	private static final long serialVersionUID = -6309555599288899548L;
	
	
	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		dba4 = new DynamoDBAssignment4();		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
				// Load the JDBC Driver
		getServletContext().setAttribute("dynamoDB", dba4);
		try {			
					
			List<FileItem> items = null;
			items = new ServletFileUpload(new DiskFileItemFactory())
					.parseRequest(req);
			InputStream fileContent = null;
			String fileName = null;
			for (FileItem item : items) {
				fileName = FilenameUtils.getName(item.getName());
				fileContent = item.getInputStream();
				break;
			}
			HttpSession session = req.getSession();
			AWSCredentials credentials = new BasicAWSCredentials("AKIAI7GS2HFELHPQM64Q","JNNwQSinnC278QQOrxrnoR2nrl6Ps1jfyS2TTO+1");
			AmazonS3 s3 = new AmazonS3Client(credentials);
			AmazonS3Client s3Client = new AmazonS3Client(credentials);
			getServletContext().setAttribute("amazonS3", s3);
			Region usWest2 = Region.getRegion(Regions.US_WEST_2);
			s3.setRegion(usWest2);
			String bucketName = "vibhabuck";
			File file = stream2file(fileContent);
			System.out.println("Uploading a new object to S3 from a file\n");
			long c = System.nanoTime();
			s3.putObject(new PutObjectRequest(bucketName, fileName, file).withCannedAcl(CannedAccessControlList.PublicRead));
			dba4.insertFile(fileName, session.getAttribute("username").toString(), s3Client.getResourceUrl(bucketName, fileName));
			long d = System.nanoTime();
			double uploadTime = (double) (d - c) / 1000000000;
			req.setAttribute("timeCalculated", uploadTime);
			session.setAttribute("fileName", fileName);
			session.setAttribute("bucketName", bucketName);			
			resp.sendRedirect("/home.html");			
						
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static File stream2file(InputStream inputStream) throws IOException {
		File file = new File("temp.tmp");
		OutputStream outputStream = new FileOutputStream(file);
		int read = 0;
		byte[] bytes = new byte[1024];
		while ((read = inputStream.read(bytes)) != -1) {
			outputStream.write(bytes, 0, read);			
		}
		outputStream.close();
		return file;
	}
}
