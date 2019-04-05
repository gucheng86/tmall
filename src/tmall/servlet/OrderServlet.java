package tmall.servlet;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tmall.bean.Order;
import tmall.util.Page;


//因为订单的增加和删除，都是在前台进行的。 所以OrderServlet提供的是list方法和delivery(发货)方法
public class OrderServlet extends BaseBackServlet{

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

	/**
	 * 当订单状态是waitDelivery的时候，就会出现发货按钮
	1. 发货按钮链接跳转到admin_order_delivery
	2. OrderServlet.delivery()方法被调用
	2.1 根据id获取Order对象
	2.2 修改发货时间，设置发货状态
	2.3 更新到数据库
	2.4 客户端跳转到admin_order_list页面
	 */
	public String delivery(HttpServletRequest request, HttpServletResponse response, Page page) {
		int id = Integer.parseInt(request.getParameter("id"));
		Order o = orderDAO.get(id);
		
		o.setDeliveryDate(new Date());
		o.setStatus(orderDAO.waitConfirm);
		
		orderDAO.update(o);
		return "@admin_order_list";
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
		List<Order> os = orderDAO.list(page.getStart(), page.getCount());
		//. 借助orderItemDAO.fill()方法为这些订单填充上orderItems信息
		orderItemDAO.fill(os);
		int total = orderDAO.getTotal();
		page.setTotal(total);
		
		request.setAttribute("os", os);
		request.setAttribute("page", page);
		return "admin/listOrder.jsp";
	}

}
