package tmall.filter;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import tmall.bean.User;





/**
 *  1. 准备字符串数组 noNeedAuthPage，存放哪些不需要登录也能访问的路径
	2. 获取uri
	3. 去掉前缀/tmall
	4. 如果访问的地址是/fore开头，又不是/foreServlet
	4.1 取出fore后面的字符串，比如是forecart,那么就取出cart
	4.2 判断cart是否是在noNeedAuthPage
	4.2 如果不在，那么就需要进行是否登录验证
	4.3 从session中取出"user"对象
	4.4 如果对象不存在，就客户端跳转到login.jsp
	4.5 否则就正常执行
 * @author 25126
 *
 */
public class ForeAuthFilter implements Filter{

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)res;
		String contextPath = request.getServletContext().getContextPath();
		//不需要登陆的页面
		String[] noNeedAuthPage = new String[] {  
				"homepage", "checkLogin", "register", "loginAjax", "login", "product", "category", "search"};
		
		String uri = request.getRequestURI();
		uri = StringUtils.remove(uri, contextPath);
		if(uri.startsWith("/fore") && !uri.startsWith("/foreServlet")) {
			String method = StringUtils.substringAfter(uri, "/fore");
			//如果是需要登陆的页面
			if(!Arrays.asList(noNeedAuthPage).contains(method)) {
				User user = (User)request.getSession().getAttribute("user");
				if(null == user) {
					response.sendRedirect("login.jsp");
					return;
				}
			}
		}
		
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}
	
}
