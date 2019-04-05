package tmall.servlet;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tmall.dao.CategoryDAO;
import tmall.dao.OrderDAO;
import tmall.dao.OrderItemDAO;
import tmall.dao.ProductDAO;
import tmall.dao.ProductImageDAO;
import tmall.dao.PropertyDAO;
import tmall.dao.PropertyValueDAO;
import tmall.dao.ReviewDAO;
import tmall.dao.UserDAO;
import tmall.util.Page;

/**
1. 首先ForeServlet继承了BaseForeServlet，而BaseForeServlet又继承了HttpServlet
2. 服务端跳转过来之后，会访问ForeServlet的doGet()或者doPost()方法
3. 在访问doGet()或者doPost()之前，会访问service()方法
4. BaseForeServlet中重写了service() 方法，所以流程就进入到了service()中
 * @author 25126
 *
 */
public class BaseForeServlet extends HttpServlet {
	// 为Servlet子类提供DAO对象
	protected CategoryDAO categoryDAO = new CategoryDAO();
	protected OrderDAO orderDAO = new OrderDAO();
	protected OrderItemDAO orderItemDAO = new OrderItemDAO();
	protected ProductDAO productDAO = new ProductDAO();
	protected ProductImageDAO productImageDAO = new ProductImageDAO();
	protected PropertyDAO propertyDAO = new PropertyDAO();
	protected PropertyValueDAO propertyValueDAO = new PropertyValueDAO();
	protected ReviewDAO reviewDAO = new ReviewDAO();
	protected UserDAO userDAO = new UserDAO();

	/**
	 *  5. 在service()方法中有三块内容
		5.1 第一块是获取分页信息
		5.2 第二块是根据反射访问对应的方法
		5.3 第三块是根据对应方法的返回值，进行服务端跳转、客户端跳转、或者直接输出字符串。
		6. 第一块：分页信息实际上在前台并没有用到，目前前台并没有提供分页功能。
		7. 第二块：取到从ForeServletFilter中request.setAttribute()传递过来的值 home，
		根据这个值home，借助反射机制调用ForeServlet类中的home()方法
		这样就达到了ForeServlet.home()方法被调用的效果
		8. 第三块： 判断根据home的返回值"home.jsp"，即没有"%"开头，也没有"@",那么就调用request.getRequestDispatcher(redirect).forward(request, response);进行服务端跳转到 "h
	 */
	public void service(HttpServletRequest request, HttpServletResponse response) {
		try {
			//1.设置分页信息
			int start = 0;
			int count = 10;
			try {
				start = Integer.parseInt(request.getParameter("page.start"));
			} catch(Exception e) {
				
			}
			try {
				count = Integer.parseInt(request.getParameter("page.count"));
			} catch(Exception e) {
				
			}
			Page page = new Page(start,count);
			
			//2.根据传值来通过反射调用相应的方法
			String method = (String)request.getAttribute("method");
			Method m = this.getClass().getMethod(method, javax.servlet.http.HttpServletRequest.class,
	                    javax.servlet.http.HttpServletResponse.class,Page.class);
	            
			String redirect = m.invoke(this, request, response, page).toString();
			
			//3.根据调用方法的返回值来进行跳转
			//客户端跳转
			if(redirect.startsWith("@")) 
				response.sendRedirect(redirect.substring(1));
			else if(redirect.startsWith("%")) 
				response.getWriter().print(redirect.substring(1));
			//服务端跳转
			else 
				request.getRequestDispatcher(redirect).forward(request, response);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
