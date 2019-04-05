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
 * 拦截所有请求，以fore开头的地址跳转到Servlet
 * @author 25126
 *
 */
public class ForeServletFilter implements Filter{

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 1.首先在web.xml中，让所有的请求都经过ForeServletFilter
	 * 2.假设访问的路径是http:/127.0.0.1:8080/tmall/forehome
	 * 3. 在ForeServletFilter 中通过request.getRequestURI()取出访问的uri: /tmall/forehome
	 * 4. 然后截掉/tmall，得到路径/forehome
	 * 5. 判断其是否以/fore开头,并且不是/foreServlet开头
	 * 6. 如果是，取出fore之后的值home，并且服务端跳转到foreServlet
	 * 7. 在跳转之前，还取出了home字符串，然后通过request.setAttribute的方式，借助服务端跳转，传递到foreServlet里去
	 */
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)res;
		
		//获取项目路径
		String contextPath = request.getServletContext().getContextPath();
		request.getServletContext().setAttribute("contextPaht", contextPath);
		
		//设置订单项数量
		User user = (User)request.getSession().getAttribute("user");
		int cartTotalItemNumber = 0;
		if(null != user) {
			List<OrderItem> ois = new OrderItemDAO().listByUser(user.getId());
			for(OrderItem oi : ois) {
				cartTotalItemNumber += oi.getNumber();
			}
		}
		request.setAttribute("cartTotalItemNumber", cartTotalItemNumber);
		
		//设置分类集合
		List<Category> cs = (List<Category>)request.getAttribute("cs");
		if(null == cs) {
			cs = new CategoryDAO().list();
			request.setAttribute("cs", cs);
		}
		
		//获取访问路径
		String uri = request.getRequestURI();
		uri = StringUtils.remove(uri, contextPath);
		//判断是否以fore开头
		if(uri.startsWith("/fore") && !uri.startsWith("/foreServlet")) {
			//取出fore之后的值
			String method = StringUtils.substringAfterLast(uri,"/fore" );
            request.setAttribute("method", method);
			//跳转到foreServlet
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
