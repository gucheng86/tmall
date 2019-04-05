package tmall.servlet;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

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
 * 1.项目中所有Servlet的父类，包含着通用的抽象方法。
 * 2.每个Servlet提交数据时，都会访问父类的service方法，而service方法通过反射去调用子类的具体实现
 * 3.上传文件的方法
 * @author 25126
 *
 */
public abstract class BaseBackServlet extends HttpServlet{
	//所有Servlet子类需要用到的方法，所有的子类方法都返回一个路径字符串，以便在反射调用对应方法后继续进行跳转等操作
	public abstract String add(HttpServletRequest request, HttpServletResponse response, Page page) ;
	public abstract String delete(HttpServletRequest request, HttpServletResponse response, Page page) ;
	public abstract String edit(HttpServletRequest request, HttpServletResponse response, Page page) ;
	public abstract String update(HttpServletRequest request, HttpServletResponse response, Page page) ;
	public abstract String list(HttpServletRequest request, HttpServletResponse response, Page page) ;	
		
	//为Servlet子类提供DAO对象	
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
	 * 服务端跳转到Servlet之后，会访问Servlet的doGet()或者doPost()方法
	 * 在访问doGet()或者doPost()之前，会访问service()方法，BaseBackServlet中重写了service() 方法，所以流程就进入到了service()中
	 * 在service()方法中有三块内容:1.获取分页信息；2.根据反射访问对应的方法；3.根据对应方法的返回值，进行跳转或输出
	 *
	 */
	public void service(HttpServletRequest request, HttpServletResponse response) {
		try {
			/*获取分页信息*/
			int start= 0;
			int count = 5;
			try {
				start = Integer.parseInt(request.getParameter("page.start"));
			} catch (Exception e) {
				
			}
			try {
				count = Integer.parseInt(request.getParameter("page.count"));
			} catch (Exception e) {
			}
			Page page = new Page(start,count);
		
			/*借助反射，调用相应的方法*/
			String method = (String) request.getAttribute("method");
			Method m = this.getClass().getMethod(method, javax.servlet.http.HttpServletRequest.class,
					javax.servlet.http.HttpServletResponse.class,Page.class);
			String redirect = m.invoke(this,request, response,page).toString();
				
            /*根据方法的返回值，进行相应的客户端跳转，服务器跳转
			 * 如果redirect是以@开头的字符串，那么就进行客户端跳转
			 *  如果redirect是以%开头的字符串，那么就直接输出字符串
		          * 如果都不是，则进行服务端跳转
			 * */
			if(redirect.startsWith("@"))
				response.sendRedirect(redirect.substring(1));
			else if(redirect.startsWith("%"))
				response.getWriter().print(redirect.substring(1));
			else
				request.getRequestDispatcher(redirect).forward(request, response);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException(e);
		
		}
		
	}
	
	/**
	 * 返回上传文件的输入流
	 * @param request 
	 * @param params 如果上传的不是文件，就保存参数到这里
	 * @return 上传文件的输入流
	 */
	public InputStream parseUpload(HttpServletRequest request, Map<String, String> params) {
		InputStream is =null;
			try {
	            DiskFileItemFactory factory = new DiskFileItemFactory();
	            ServletFileUpload upload = new ServletFileUpload(factory);
	          //上传的文件大小限制为10M
	            factory.setSizeThreshold(1024 * 10240);
			
			//遍历出Item,一个Item就是对应一个浏览器提交的数据，通过item.getInputStream可以打开浏览器上传的文件的输入流。
			List<?> items = upload.parseRequest(request);
	            Iterator<?> iter = items.iterator();
	            while (iter.hasNext()) {
	                FileItem item = (FileItem) iter.next();
	                	//如果表单项是文件类型
	                if (!item.isFormField()) {
					//获取上传文件的输入流
	                    is = item.getInputStream();
				
				//如果表单项是普通类型
				 } else {
	                	String paramName = item.getFieldName();
	                	String paramValue = item.getString();
	                	paramValue = new String(paramValue.getBytes("ISO-8859-1"), "UTF-8");
	                	params.put(paramName, paramValue);
	                }
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		return is;
	}
	
	
}
