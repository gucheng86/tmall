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
 * 1. ������web.xml�����ļ��У������е����󶼻ᾭ��BackServletFilter��<url-pattern>/*</url-pattern>
 * 2. ���Ǽ�����ʵ�·���ǣ�http://127.0.0.1:8080/tmall/admin_category_list
 * 3. ��BackServletFilter ��ͨ��request.getRequestURI()ȡ�����ʵ�uri: /tmall/admin_category_list
 * 4. Ȼ��ص�/tmall���õ�·��/admin_category_list
 * 5. �ж����Ƿ���/admin��ͷ
 * 6. ����ǣ���ô��ȡ������_֮����ַ�����category������ƴ�ӳ�/categoryServlet��ͨ���������ת��/categoryServlet
 * 7. ����ת֮ǰ����ȡ����list�ַ�����Ȼ��ͨ��request.setAttribute�ķ�ʽ�������������ת�����ݵ�categoryServlet��ȥ
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
		
		//��ȡȫ��ServletContext���󣬻�ȡ��ǰ��Ŀ��·��/tmall
		String contextPath = request.getServletContext().getContextPath();
		//���ص�ַ����ַ
		String uri = request.getRequestURI();
		uri = StringUtils.remove(uri, contextPath);
		//���uri��admin��ͷ����ת
		if(uri.startsWith("/admin_")) {
			//ѡȡFri������_֮������ݣ���Category������ƴ�ӳ�CategoryServlet
			String servletPath = StringUtils.substringBetween(uri, "_", "_") + "Servlet";
			//ѡȡ���һ��_��������ݣ���list
			String method = StringUtils.substringAfterLast(uri, "_");
			//��ת����Ӧ��Servlet������Ҫ���õķ���
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
