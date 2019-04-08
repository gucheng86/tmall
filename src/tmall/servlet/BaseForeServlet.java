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
1. ����ForeServlet�̳���BaseForeServlet����BaseForeServlet�ּ̳���HttpServlet
2. �������ת����֮�󣬻����ForeServlet��doGet()����doPost()����
3. �ڷ���doGet()����doPost()֮ǰ�������service()����
4. BaseForeServlet����д��service() �������������̾ͽ��뵽��service()��
 * @author 25126
 *
 */
public class BaseForeServlet extends HttpServlet {
	// ΪServlet�����ṩDAO����
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
	 *  5. ��service()����������������
		5.1 ��һ���ǻ�ȡ��ҳ��Ϣ
		5.2 �ڶ����Ǹ��ݷ�����ʶ�Ӧ�ķ���
		5.3 �������Ǹ��ݶ�Ӧ�����ķ���ֵ�����з������ת���ͻ�����ת������ֱ������ַ�����
		6. ��һ�飺��ҳ��Ϣʵ������ǰ̨��û���õ���Ŀǰǰ̨��û���ṩ��ҳ���ܡ�
		7. �ڶ��飺ȡ����ForeServletFilter��request.setAttribute()���ݹ�����ֵ home��
		�������ֵhome������������Ƶ���ForeServlet���е�home()����
		�����ʹﵽ��ForeServlet.home()���������õ�Ч��
		8. �����飺 �жϸ���home�ķ���ֵ"home.jsp"����û��"%"��ͷ��Ҳû��"@",��ô�͵���request.getRequestDispatcher(redirect).forward(request, response);���з������ת�� "h
	 */
	public void service(HttpServletRequest request, HttpServletResponse response) {
		try {
			//1.���÷�ҳ��Ϣ
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
			
			//2.���ݴ�ֵ��ͨ�����������Ӧ�ķ���
			String method = (String)request.getAttribute("method");
			Method m = this.getClass().getMethod(method, javax.servlet.http.HttpServletRequest.class,
	                    javax.servlet.http.HttpServletResponse.class,Page.class);
	            
			String redirect = m.invoke(this, request, response, page).toString();
			
			//3.���ݵ��÷����ķ���ֵ��������ת
			//�ͻ�����ת
			if(redirect.startsWith("@")) 
				response.sendRedirect(redirect.substring(1));
			else if(redirect.startsWith("%")) 
				response.getWriter().print(redirect.substring(1));
			//�������ת
			else 
				request.getRequestDispatcher(redirect).forward(request, response);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
