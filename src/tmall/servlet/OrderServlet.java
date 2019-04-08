package tmall.servlet;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tmall.bean.Order;
import tmall.util.Page;


//��Ϊ���������Ӻ�ɾ����������ǰ̨���еġ� ����OrderServlet�ṩ����list������delivery(����)����
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
	 * ������״̬��waitDelivery��ʱ�򣬾ͻ���ַ�����ť
	1. ������ť������ת��admin_order_delivery
	2. OrderServlet.delivery()����������
	2.1 ����id��ȡOrder����
	2.2 �޸ķ���ʱ�䣬���÷���״̬
	2.3 ���µ����ݿ�
	2.4 �ͻ�����ת��admin_order_listҳ��
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
		//. ����orderItemDAO.fill()����Ϊ��Щ���������orderItems��Ϣ
		orderItemDAO.fill(os);
		int total = orderDAO.getTotal();
		page.setTotal(total);
		
		request.setAttribute("os", os);
		request.setAttribute("page", page);
		return "admin/listOrder.jsp";
	}

}
