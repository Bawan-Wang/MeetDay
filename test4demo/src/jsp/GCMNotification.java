package jsp;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

/**
 * Servlet implementation class GCMNotification
 */
@WebServlet("/GCMNotification")
public class GCMNotification extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// Put your Google API Server Key here
	//private static final String GOOGLE_SERVER_KEY = "AIzaSyDYaXmDyeXTzxnQyCGcuNgBPRyUvJ_uU6c";
	//static final String MESSAGE_KEY = "message";   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GCMNotification() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		Result result = null;

		String share = request.getParameter("shareRegId");

		// GCM RedgId of Android device to send push notification
		String regId = "";
		String msg ="";
		if (share != null && !share.isEmpty()) {
			regId = request.getParameter("regId");
			request.setAttribute("pushStatus", "GCM RegId Received.");
			msg = regId;
			try{
				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				out.println(msg);
				response.setHeader("REFRESH","10;URL=index.jsp");
			}catch(Exception e){
				
			}
			//request.getRequestDispatcher("index.jsp")
					//.forward(request, response);
		} else {

			try {
				
				regId = "APA91bG-DWgjPbeALvmc-eOArH9GLv2w0ZNe6x-hfpR8Y9gDqigIDYESWMXV42J4nqcbbPGWPi25xzmO4Efjq1aZVe0ydwuX5myFKkuWx5tu6Vn_xuCfPoxnJ34-a3Ri2apKdNN6kZJ9rZnLU9va_cKcBa9kh7-Pow";
				//regId = "APA91bFvI6udWPfJ2t3AgIJMq6tXByzh8I_TEtVIsA0cJE2FbYMgAS6LawhikMalGjd0Fv88dySXRki9rQs218nVWkQCGWVGh2Z7QdscnOjCw43mBF6kzgrxbHri-22bosHKfcm65hV7QKmBH3X302PiR2jRbTy1kQ";
				String userMessage = request.getParameter("message");
				Sender sender = new Sender(Const.GOOGLE_SERVER_KEY);
				Message message = new Message.Builder().timeToLive(30)
						.delayWhileIdle(true).addData(Const.MESSAGE_KEY, userMessage).build();
				System.out.println("regId: " + regId);
				result = sender.send(message, regId, 1);
				msg = result.toString();
				request.setAttribute("pushStatus", result.toString());
			} catch (IOException ioe) {
				ioe.printStackTrace();
				request.setAttribute("pushStatus",
						"RegId required: " + ioe.toString());
			} catch (Exception e) {
				e.printStackTrace();
				request.setAttribute("pushStatus", e.toString());
			}
			try{
				response.setContentType("text/html");
				PrintWriter out = response.getWriter();
				out.println(msg);
				response.setHeader("REFRESH","10;URL=index.jsp");
			}catch(Exception e){
				
			}
			//request.getRequestDispatcher("index.jsp")
				//	.forward(request, response);
		}
	}

}
