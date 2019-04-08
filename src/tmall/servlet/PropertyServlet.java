package tmall.servlet;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tmall.bean.Category;
import tmall.bean.Property;
import tmall.util.Page;

/** ĳ��Ʒ�����µ����Թ��� */
public class PropertyServlet extends BaseBackServlet{
	//��Ӹò�Ʒ������
	@Override
	public String add(HttpServletRequest request, HttpServletResponse response, Page page) {
		// TODO Auto-generated method stub
		int cid = Integer.parseInt(request.getParameter("cid"));
		Category c = categoryDAO.get(cid);
		
		String name = request.getParameter("name");
		Property property = new Property();
		property.setCategory(c);
		property.setName(name);
		propertyDAO.add(property);
		
		return "@admin_property_list?cid="+cid;
	}

	@Override
	public String delete(HttpServletRequest request, HttpServletResponse response, Page page) {
		// TODO Auto-generated method stub
		//����id��ȡProperty����
		int id = Integer.parseInt(request.getParameter("id"));
		Property p = propertyDAO.get(id);
		propertyDAO.delete(id);
		//����id��ת
		return "@admin_property_list?cid" + p.getCategory().getId();
		
	}
	
	@Override
	public String edit(HttpServletRequest request, HttpServletResponse response, Page page) {
		// TODO Auto-generated method stub
		// ����id��ȡProperty����
		int id = Integer.parseInt(request.getParameter("id"));
		Property p = propertyDAO.get(id);
		request.setAttribute("p", p);
		//��jsp����ʾ�������ƺ�id��
		return "admin/editProperty.jsp";
	}

	@Override
	public String update(HttpServletRequest request, HttpServletResponse response, Page page) {
		// TODO Auto-generated method stub
		// ��ȡ����
		int cid = Integer.parseInt(request.getParameter("cid"));
		Category c = categoryDAO.get(cid);
		
		int id = Integer.parseInt(request.getParameter("id"));
		String name = request.getParameter("name");
		
		//����Property����
		Property p = new Property();
		p.setCategory(c);
		p.setId(id);
		p.setName(name);
		propertyDAO.update(p);
		//���ϲ���cid��ת
		return "@admin_property_list?cid="+p.getCategory().getId();
	}

	@Override
	public String list(HttpServletRequest request, HttpServletResponse response, Page page) {
		int cid = Integer.parseInt(request.getParameter("cid"));
		Category c = categoryDAO.get(cid);
		//����cid����ȡ��ǰ�����µ����Լ���
		List<Property> ps = propertyDAO.list(cid, page.getStart(), page.getCount());
		//��ȡ��ǰ�����µ������������������ø���ҳpage����
		int total = propertyDAO.getTotal(cid);
		page.setTotal(total);
		//ƴ���ַ���"&cid="+c.getId()�����ø�page�����Paramֵ�� ��Ϊ���Է�ҳ���ǻ��ڵ�ǰ�����µķ�ҳ�����Է�ҳ��ʱ����Ҫ�������ci
		page.setParam("&cid="+c.getId());
		//Ϊjspҳ�����ò���
		request.setAttribute("ps", ps);
		request.setAttribute("c", c);
		request.setAttribute("page", page);
		//��jspҳ����ʾ
		return "admin/listProperty.jsp";
	}

}
