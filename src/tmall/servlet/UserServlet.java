package tmall.servlet;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tmall.bean.User;
import tmall.util.Page;

/**
 * ��̨�����û�ֻ����ѯ����
 * @author 25126
 *
 */
public class UserServlet extends BaseBackServlet{

	@Override
	public String add(HttpServletRequest request, HttpServletResponse response, Page page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String delete(HttpServletRequest request, HttpServletResponse response, Page page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String edit(HttpServletRequest request, HttpServletResponse response, Page page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String update(HttpServletRequest request, HttpServletResponse response, Page page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String list(HttpServletRequest request, HttpServletResponse response, Page page) {
		List<User> us = userDAO.list(page.getStart(), page.getCount());
		//���÷�ҳ��Ϣ
		int total = userDAO.getTotal();
		page.setTotal(total);
		
		request.setAttribute("us", us);
		request.setAttribute("page", page);
		
		return "admin/listUser.jsp";
	}

}
