package tmall.servlet;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tmall.bean.Category;
import tmall.bean.Property;
import tmall.util.Page;

/** 某产品分类下的属性管理 */
public class PropertyServlet extends BaseBackServlet{
	//添加该产品的属性
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
		//根据id获取Property对象
		int id = Integer.parseInt(request.getParameter("id"));
		Property p = propertyDAO.get(id);
		propertyDAO.delete(id);
		//带上id跳转
		return "@admin_property_list?cid" + p.getCategory().getId();
		
	}
	
	@Override
	public String edit(HttpServletRequest request, HttpServletResponse response, Page page) {
		// TODO Auto-generated method stub
		// 根据id获取Property对象
		int id = Integer.parseInt(request.getParameter("id"));
		Property p = propertyDAO.get(id);
		request.setAttribute("p", p);
		//在jsp中显示属性名称和id等
		return "admin/editProperty.jsp";
	}

	@Override
	public String update(HttpServletRequest request, HttpServletResponse response, Page page) {
		// TODO Auto-generated method stub
		// 获取参数
		int cid = Integer.parseInt(request.getParameter("cid"));
		Category c = categoryDAO.get(cid);
		
		int id = Integer.parseInt(request.getParameter("id"));
		String name = request.getParameter("name");
		
		//创建Property对象
		Property p = new Property();
		p.setCategory(c);
		p.setId(id);
		p.setName(name);
		propertyDAO.update(p);
		//带上参数cid跳转
		return "@admin_property_list?cid="+p.getCategory().getId();
	}

	@Override
	public String list(HttpServletRequest request, HttpServletResponse response, Page page) {
		int cid = Integer.parseInt(request.getParameter("cid"));
		Category c = categoryDAO.get(cid);
		//基于cid，获取当前分类下的属性集合
		List<Property> ps = propertyDAO.list(cid, page.getStart(), page.getCount());
		//获取当前分类下的属性总数，并且设置给分页page对象
		int total = propertyDAO.getTotal(cid);
		page.setTotal(total);
		//拼接字符串"&cid="+c.getId()，设置给page对象的Param值。 因为属性分页都是基于当前分类下的分页，所以分页的时候需要传递这个ci
		page.setParam("&cid="+c.getId());
		//为jsp页面设置参数
		request.setAttribute("ps", ps);
		request.setAttribute("c", c);
		request.setAttribute("page", page);
		//在jsp页面显示
		return "admin/listProperty.jsp";
	}

}
