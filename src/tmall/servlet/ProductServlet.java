package tmall.servlet;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tmall.bean.Category;
import tmall.bean.Product;
import tmall.bean.Property;
import tmall.bean.PropertyValue;
import tmall.util.Page;

//对于PropertyValue编辑和修改的支持放了在ProductServlet中进行：
//主要是editPropertyValue、updatePropertyValue这两个方法
public class ProductServlet extends BaseBackServlet{

	@Override
	public String add(HttpServletRequest request, HttpServletResponse response, Page page) {
		//根据cid获取category
		int cid = Integer.parseInt(request.getParameter("cid"));
		Category c = categoryDAO.get(cid);
		
		//获取填写的参数
		String name = request.getParameter("name");
		String subTitle = request.getParameter("subTitle");
		float orignalPrice = Float.parseFloat(request.getParameter("orignalPrice"));
		float promotePrice = Float.parseFloat(request.getParameter("promotePrice"));
		int stock = Integer.parseInt(request.getParameter("stock"));
		
		//封装成对象
		Product p = new Product();
		p.setCategory(c);
		p.setName(name);
		p.setSubTitle(subTitle);
		p.setOrignalPrice(orignalPrice);
		p.setPromotePrice(promotePrice);
		p.setStock(stock);
		productDAO.add(p);
		
		return "@admin_product_list?cid=" + cid;
	}

	@Override
	public String delete(HttpServletRequest request, HttpServletResponse response, Page page) {
		// TODO Auto-generated method stub
		//根据Product的id来删除
		int id = Integer.getInteger(request.getParameter("id"));
		Property p = propertyDAO.get(id);
		productDAO.delete(id);
		return "@admin_product_list?cid=" + p.getId();
	}

	@Override
	public String edit(HttpServletRequest request, HttpServletResponse response, Page page) {
		//根据id获取产品分类
		int id = Integer.parseInt(request.getParameter("id"));
		Product p = productDAO.get(id);
		request.setAttribute("p", p);
		return "admin/editProduct.jsp";
	}
	
	//通过产品管理界面的设置属性，跳到编辑页面
	public String editPropertyValue(HttpServletRequest request, HttpServletResponse response, Page page) {
		//1. 获取参数id
		int id = Integer.parseInt(request.getParameter("id"));
		//2. 根据id获取Product对象p
		Product p = productDAO.get(id);
		request.setAttribute("p", p);
		
		//3. 初始化属性值： propertyValueDAO.init(p)。 因为如果是第一次访问，这些属性值是不存在的。
		propertyValueDAO.init(p);
		//4. 根据Product的id，获取产品对应的属性值集合
		List<PropertyValue> pvs = propertyValueDAO.list(p.getId());
		//5. 属性值集合放在request的 "pvs" 属性上
		request.setAttribute("pvs", pvs);
		
		//6. 服务端跳转到admin/editProductValue.jsp 上
		return "admin/editProductValue.jsp";
	}
	
	//修改功能采用的是使用post方式提交ajax的异步调用方式
	public String updatePropertyValue(HttpServletRequest request, HttpServletResponse response, Page page) {
		//6. admin_product_updatePropertyValue导致ProductServlet的updatePropertyValue方法被调用
		//6.1 获取pvid
		int pvid = Integer.parseInt(request.getParameter("pvid"));
		//6.2 获取value
		String value = request.getParameter("value");
		
		//6.3 基于pvid和value,更新PropertyValue对象
		PropertyValue pv = propertyValueDAO.get(pvid);
		pv.setValue(value);
		propertyValueDAO.update(pv);
		
		//6.4 返回"%success"
		return "%success";
//		7. BaseBackServlet根据返回值"%success"，直接输出字符串"success" 到浏览器
//		8. 浏览器判断如果返回值是"success",那么就把边框设置为绿色，表示修改成功，否则设置为红色，表示修改失败
	}
	
	@Override
	public String update(HttpServletRequest request, HttpServletResponse response, Page page) {
		//根据cid获取category
		int cid = Integer.parseInt(request.getParameter("cid"));
		Category c = categoryDAO.get(cid);
		
		int id = Integer.parseInt(request.getParameter("id"));	
		//获取填写的参数
		String name = request.getParameter("name");
		String subTitle = request.getParameter("subTitle");
		float orignalPrice = Float.parseFloat(request.getParameter("orignalPrice"));
		float promotePrice = Float.parseFloat(request.getParameter("promotePrice"));
		int stock = Integer.parseInt(request.getParameter("stock"));
		
		//封装成对象
		Product p = new Product();
		p.setCategory(c);
		p.setId(id);
		p.setName(name);
		p.setSubTitle(subTitle);
		p.setOrignalPrice(orignalPrice);
		p.setPromotePrice(promotePrice);
		p.setStock(stock);
		productDAO.update(p);
				
		return "@admin_product_list?cid="+c.getId();
	}

	@Override
	public String list(HttpServletRequest request, HttpServletResponse response, Page page) {
		int cid = Integer.parseInt(request.getParameter("cid"));
		Category c = categoryDAO.get(cid);
		//基于cid，获取当前分类下的属性集合
		List<Product> ps = productDAO.list(cid, page.getStart(), page.getCount());
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
		return "admin/listProduct.jsp";
	}
	
}
