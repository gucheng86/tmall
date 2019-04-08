package tmall.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import tmall.bean.Category;
import tmall.bean.OrderItem;
import tmall.bean.User;
import tmall.dao.CategoryDAO;
import tmall.dao.OrderItemDAO;



/**
 * ��������������fore��ͷ�ĵ�ַ��ת��Servlet
 * @author 25126
 *
 */
public class ForeServletFilter implements Filter{

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 1.������web.xml�У������е����󶼾���ForeServletFilter
	 * 2.������ʵ�·����http:/127.0.0.1:8080/tmall/forehome
	 * 3. ��ForeServletFilter ��ͨ��request.getRequestURI()ȡ�����ʵ�uri: /tmall/forehome
	 * 4. Ȼ��ص�/tmall���õ�·��/forehome
	 * 5. �ж����Ƿ���/fore��ͷ,���Ҳ���/foreServlet��ͷ
	 * 6. ����ǣ�ȡ��fore֮���ֵhome�����ҷ������ת��foreServlet
	 * 7. ����ת֮ǰ����ȡ����home�ַ�����Ȼ��ͨ��request.setAttribute�ķ�ʽ�������������ת�����ݵ�foreServlet��ȥ
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)res;
		
		//��ȡ��Ŀ·��
		String contextPath = request.getServletContext().getContextPath();
		request.getServletContext().setAttribute("contextPaht", contextPath);
		
		//���ö���������
		User user = (User)request.getSession().getAttribute("user");
		int cartTotalItemNumber = 0;
		if(null != user) {
			List<OrderItem> ois = new OrderItemDAO().listByUser(user.getId());
			for(OrderItem oi : ois) {
				cartTotalItemNumber += oi.getNumber();
			}
		}
		request.setAttribute("cartTotalItemNumber", cartTotalItemNumber);
		
		//���÷��༯��
		List<Category> cs = (List<Category>)request.getAttribute("cs");
		if(null == cs) {
			cs = new CategoryDAO().list();
			request.setAttribute("cs", cs);
		}
		
		//��ȡ����·��
		String uri = request.getRequestURI();
		uri = StringUtils.remove(uri, contextPath);
		//�ж��Ƿ���fore��ͷ
		if(uri.startsWith("/fore") && !uri.startsWith("/foreServlet")) {
			//ȡ��fore֮���ֵ
			String method = StringUtils.substringAfterLast(uri,"/fore" );
            request.setAttribute("method", method);
			//��ת��foreServlet
			req.getRequestDispatcher("/foreServlet").forward(request, response);
			return;
		}
		
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}
	
}
