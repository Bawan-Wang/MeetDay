package jsp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Servlet implementation class UploadServlet
 */
@WebServlet("/UploadServlet")
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		System.out.println("Upload data from server");
		String msg="";

		request.setCharacterEncoding("UTF-8");
		
		if(request.getHeader("user-agent")!=null&&(request.getHeader("user-agent").toLowerCase().indexOf("mozilla")!=-1)) { 
			System.out.println("Client-PC");
		} else { 
			System.out.println("Client-Mobile");
		
		}
			
		FileItemFactory factory = new DiskFileItemFactory(); // 建立FileItemFactory對象
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setFileSizeMax(1*1024*1024);
     	String loadpath=request.getSession().getServletContext().getRealPath("/")+"upload"; //上傳文件存放目錄
     	System.out.println("loadpath="+loadpath);
		
		List<FileItem> items = null;
         
		 try {
			 //fileItems = fu.parseRequest(request);
			 items = upload.parseRequest(request);
		     System.out.println("fileItems="+items);
		 } catch (Exception e) {
		         e.printStackTrace();
		 }
		             	              
		Iterator<FileItem> iter = items.iterator(); // 依次處理每個上傳的文件
		while (iter.hasNext()){
		    FileItem item = (FileItem)iter.next();// 忽略其他不是文件域的所有表單信息
		    if (!item.isFormField()){
		        String name = item.getName();//獲取上傳文件名,包括路徑
		        name = name.substring(name.lastIndexOf("\\")+1);//從全路徑中提取文件名
		        long size = item.getSize();
		        if((name==null||name.equals("")) && size==0)
		              continue;
		        //int point = name.indexOf(".");
		        //String uid = request.getParameter("usrid");
		        //name=(uid+name.substring(point,name.length()));
		        //name=(new Date()).getTime()+name.substring(point,name.length());
		        //index++;
		        File fNew=new File(loadpath, name);//File(loadpath+"\\"+name);//File(loadpath, name);
		        try {
		                 item.write(fNew);
		         } catch (Exception e) {
		                 // TODO Auto-generated catch block
		                 e.printStackTrace();
		         }
		              
		        
		        //Const.RRet ret = null;
		        msg = Const.RRet.RRET_SUCCESS + "%";
		        try{
					response.setContentType("text/html");
					PrintWriter out = response.getWriter();
					out.println(msg);
					response.setHeader("REFRESH","2;URL=index.jsp");
				}catch(Exception e){
					
				}
		    } else {
		        String fieldvalue = item.getString();
		        msg = Const.RRet.RRET_FAIL + "%";
		        System.out.println(fieldvalue);
		    }
		}

		String image = request.getParameter("image");
		//byte[] result = Base64.decode(image); 
		//String loadpath=request.getSession().getServletContext().getRealPath("/")+"upload";
		//FileOutputStream out = new FileOutputStream(loadpath+"/xxx.jpg");  
		//out.write(result);  
		//out.close();
	}

}
