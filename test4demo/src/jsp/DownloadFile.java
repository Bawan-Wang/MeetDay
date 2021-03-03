package jsp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DownloadFile
 */
@WebServlet("/DownloadFile")
public class DownloadFile extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DownloadFile() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html");
		javax.servlet.ServletOutputStream out = response.getOutputStream();
		String filepath = "/" + "uploadfile/";
		String filename = new String(request.getParameter("filename").getBytes(
				"ISO8859-1"), "UTF-8").toString();
		//String filename = java.net.URLDecoder.decode(request.getParameter("filename"));
		System.out.println("DownloadFile filepath:" + filepath);
		System.out.println("DownloadFile filename:" + filename);
		java.io.File file = new java.io.File(filepath + filename);
		if (!file.exists()) {
			System.out.println(file.getAbsolutePath() + "File Not exist!");
			return;
		}
		// 讀取文件流
		java.io.FileInputStream fileInputStream = new java.io.FileInputStream(
				file);
		// 下載文件
		// 設置響應頭和下載保存的文件名
		if (filename != null && filename.length() > 0) {
			response.setContentType("application/x-msdownload");
			response
					.setHeader("Content-Disposition", "attachment; filename="
							+ new String(filename.getBytes("gb2312"),
									"iso8859-1") + "");
			if (fileInputStream != null) {
				int filelen = fileInputStream.available();
				// 文件太大時內存不能一次讀出,要循環
				byte a[] = new byte[filelen];
				fileInputStream.read(a);
				out.write(a);
			}
			fileInputStream.close();
			out.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter out = response.getWriter();
		out
				.println("<!DOCTYPE HTML PUBLIC -//W3C//DTD HTML 4.01 Transitional//EN>");
		out.println("<HTML>");
		out.println(" <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println(" <BODY>");
		out.print(" This is ");
		out.print(this.getClass().getName());
		out.println(", using the POST method");
		out.println(" </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}

}
