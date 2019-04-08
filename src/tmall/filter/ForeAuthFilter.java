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
 *  1. ׼���ַ������� noNeedAuthPage�������Щ����Ҫ��¼Ҳ�ܷ��ʵ�·��
	2. ��ȡuri
	3. ȥ��ǰ׺/tmall
	4. ������ʵĵ�ַ��/fore��ͷ���ֲ���/foreServlet
	4.1 ȡ��fore������ַ�����������forecart,��ô��ȡ��cart
	4.2 �ж�cart�Ƿ�����noNeedAuthPage
	4.2 ������ڣ���ô����Ҫ�����Ƿ��¼��֤
	4.3 ��session��ȡ��"user"����
	4.4 ������󲻴��ڣ��Ϳͻ�����ת��login.jsp
	4.5 ���������ִ��
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
		//����Ҫ��½��ҳ��
		String[] noNeedAuthPage = new String[] {  
				"homepage", "checkLogin", "register", "loginAjax", "login", "product", "category", "search"};
		
		String uri = request.getRequestURI();
		uri = StringUtils.remove(uri, contextPath);
		if(uri.startsWith("/fore") && !uri.startsWith("/foreServlet")) {
			String method = StringUtils.substringAfter(uri, "/fore");
			//�������Ҫ��½��ҳ��
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
