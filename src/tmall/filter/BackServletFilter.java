package tmall.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

/**
 * 1. 首先在web.xml配置文件中，让所有的请求都会经过BackServletFilter：<url-pattern>/*</url-pattern>
 * 2. 还是假设访问的路径是：http://127.0.0.1:8080/tmall/admin_category_list
 * 3. 在BackServletFilter 中通过request.getRequestURI()取出访问的uri: /tmall/admin_category_list
 * 4. 然后截掉/tmall，得到路径/admin_category_list
 * 5. 判断其是否以/admin开头
 * 6. 如果是，那么就取出两个_之间的字符串，category，并且拼接成/categoryServlet，通过服务端跳转到/categoryServlet
 * 7. 在跳转之前，还取出了list字符串，然后通过request.setAttribute的方式，借助服务端跳转，传递到categoryServlet里去
 * @author 25126
 */
public class BackServletFilter implements Filter {
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)req;
		HttpServletResponse response = (HttpServletResponse)res;
		
		//获取全局ServletContext对象，获取当前项目的路径/tmall
		String contextPath = request.getServletContext().getContextPath();
		//返回地址栏地址
		String uri = request.getRequestURI();
		uri = StringUtils.remove(uri, contextPath);
		//如果uri以admin开头就跳转
		if(uri.startsWith("/admin_")) {
			//选取Fri的两个_之间的内容（如Category），并拼接成CategoryServlet
			String servletPath = StringUtils.substringBetween(uri, "_", "_") + "Servlet";
			//选取最后一个_后面的内容，如list
			String method = StringUtils.substringAfterLast(uri, "_");
			//跳转到相应的Servlet并传递要调用的方法
			request.setAttribute("method", method);
			req.getRequestDispatcher("/" + servletPath).forward(request, response);
			return;
		}
		
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}

	
	
	
}
