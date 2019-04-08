package tmall.servlet;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import tmall.bean.Category;
import tmall.bean.Order;
import tmall.bean.OrderItem;
import tmall.bean.Product;
import tmall.bean.ProductImage;
import tmall.bean.PropertyValue;
import tmall.bean.Review;
import tmall.bean.User;
import tmall.comparator.*;
import tmall.dao.OrderDAO;
import tmall.dao.ProductDAO;
import tmall.util.Page;
import javax.servlet.http.HttpServletResponse;

import org.omg.CORBA.Request;

import org.springframework.web.util.HtmlUtils;



public class ForeServlet extends BaseForeServlet{
	/**
	 * Ϊ��Ʒ��������Ʒ
	 */
	public String home(HttpServletRequest request, HttpServletResponse response, Page page) {
		List<Category> cs = categoryDAO.list();
		new ProductDAO().fill(cs);
		new ProductDAO().fillByRow(cs);
		
		request.setAttribute("cs", cs);
		return "home.jsp";
	}
	
	//ע�᷽��
	public String register(HttpServletRequest request, HttpServletResponse response, Page page) {
		//1. ��ȡ�˺�����
		String name = request.getParameter("name");
		String password = request.getParameter("password");

		//2. ͨ��HtmlUtils.htmlEscape(name);���˺����������Ž���ת��
		name = HtmlUtils.htmlEscape(name);
		System.out.println(name);
		boolean exist = userDAO.isExist(name);
		
		//3. �ж��û����Ƿ����
		//3.1 ����Ѿ����ڣ��ͷ������ת��reigster.jsp���������ô�����ʾ��Ϣ
		if (exist) {
			request.setAttribute("msg", "���û����ѱ�ע��");
			return "register.jsp";
		}
		
		//3.2 ��������ڣ�����뵽���ݿ��У����������ת��registerSuccess.jspҳ��
		User user = new User();
		user.setName(name);
		user.setPassword(password);
		System.out.println(user.getName());
		System.out.println(user.getPassword());
		userDAO.add(user);
		return "@registerSuccess.jsp";
	}

	//��¼����
	public String login(HttpServletRequest request, HttpServletResponse response, Page page) throws IOException {
		//1. ��ȡ�˺�����
		String name = request.getParameter("name");
		//2. ���˺�ͨ��HtmlUtils.htmlEscape����ת��
		name = HtmlUtils.htmlEscape(name);
		String password = request.getParameter("password");
		
		System.out.println(name + "," + password);
		//3. �����˺ź������ȡUser����
		//3.1 �������Ϊ�գ���������ת��login.jsp��Ҳ���ϴ�����Ϣ������ʹ��login.jsp �еİ취��ʾ������Ϣ
		User user = userDAO.get(name, password);
		if(null == user) {
			request.setAttribute("msg", "�˺��������");
			return "login.jsp";
		}
		
		//3.2 ���������ڣ���Ѷ��󱣴���session�У����ͻ�����ת����ҳ"@forehome"
		request.getSession().setAttribute("user", user);
		System.out.println(request.getSession().getAttribute("user"));
		
		return "@forehome";
	}
	
	//�˳���¼
	public String logout(HttpServletRequest request, HttpServletResponse response, Page page) {
		//1. ��session��ȥ��"user"
		request.getSession().removeAttribute("user");
		return "@forehome";
	}
	
	
	
	//����Ƿ��¼
	public String checkLogin(HttpServletRequest request, HttpServletResponse response, Page page) {
		User user = (User)request.getSession().getAttribute("user");
		if(null != user)
			return "%success";
		return "%fail";
	}
	
	//���ģ̬������ͨ��Ajax��ʽ�ĵ�¼�Ƿ���Ч
	public String loginAjax(HttpServletRequest request, HttpServletResponse response, Page page) {
		 String name = request.getParameter("name");
		    String password = request.getParameter("password");    
		    User user = userDAO.get(name,password);
		     
		    if(null==user){
		        return "%fail";
		    }
		    request.getSession().setAttribute("user", user);
		    return "%success";  
	}
	
	//��������
	public String search(HttpServletRequest request, HttpServletResponse response, Page page) {
		String keyword = request.getParameter("keyword");
		//��ȡ����������ǰ20����Ʒ
		List<Product> ps = productDAO.search(keyword, 0, 20); 
		//Ϊ��Щ��Ʒ��������������
		productDAO.setSaleAndReviewNumber(ps);
		
		request.setAttribute("ps", ps);
		return "searchResult.jsp";
	}
	
	//Ϊ��Ʒ��ʾ���ݲ���������ͼƬ������ͼƬ������ֵ�����ۡ�����
	public String product(HttpServletRequest request, HttpServletResponse response, Page page) {
		int pid = Integer.parseInt(request.getParameter("pid"));
		Product p = productDAO.get(pid);
		
		//3. ���ݶ���p����ȡ�����Ʒ��Ӧ�ĵ���ͼƬ����
		List<ProductImage> productSingleImages = productImageDAO.list(p, productImageDAO.type_single);
		//4. ���ݶ���p����ȡ�����Ʒ��Ӧ������ͼƬ����
		List<ProductImage> productDetailImages = productImageDAO.list(p, productImageDAO.type_detail);
		p.setProductSingleImages(productSingleImages);
		p.setProductDetailImages(productDetailImages);
		
		//��ȡ��Ʒ������ֵ������
		List<PropertyValue> pvs = propertyValueDAO.list(p.getId());
		List<Review> reviews = reviewDAO.list(p.getId());
		
		//���ò�Ʒ����������������
		new ProductDAO().setSaleAndReviewNumber(p);
		
		request.setAttribute("reviews", reviews);
		request.setAttribute("pvs", pvs);
		request.setAttribute("p", p);
		
		return "product.jsp";
	}
	
	//��ʾ����
	public String category(HttpServletRequest request,HttpServletResponse response,  Page page) {
		//Ϊ��������Ʒ����Ϣ
		int cid = Integer.parseInt(request.getParameter("cid"));
		Category c = categoryDAO.get(cid);
		new ProductDAO().fill(c);
		new ProductDAO().setSaleAndReviewNumber(c.getProducts());
		
		//���ݲ����Է�������
		String sort = request.getParameter("sort");
		if(null != sort) {
			switch (sort) {
			case "review":
				Collections.sort(c.getProducts(), new ProductReviewComparator());
				break;
			case "date":
				Collections.sort(c.getProducts(), new ProductDateComparator());
				break;
			case "saleCount" :
	            Collections.sort(c.getProducts(),new ProductSaleCountComparator());
	            break;
	             
	        case "price":
	            Collections.sort(c.getProducts(),new ProductPriceComparator());
	            break;
	             
	        case "all":
	            Collections.sort(c.getProducts(),new ProductAllComparator());
	            break;
	        }
	    }
	     
	    request.setAttribute("c", c);
	    return "category.jsp";     
	}
	
	
	//��������ť����ת����
	public String buyone(HttpServletRequest request, HttpServletResponse response, Page page) {
		//��ȡp��user
		int pid = Integer.parseInt(request.getParameter("pid"));
		int num = Integer.parseInt(request.getParameter("num"));
		Product p = productDAO.get(pid);
		int oiid = 0;
		
		User user = (User)request.getSession().getAttribute("user");
		//�������д��������Ʒ
		boolean found = false;
		List<OrderItem> ois = orderItemDAO.listByUser(user.getId());
		for(OrderItem oi : ois) {
			if(oi. getProduct().getId() == p.getId()) {
				oi.setNumber(oi.getNumber() + num);
				orderItemDAO.update(oi);
				found = true;
				oiid = oi.getId();
				break;
			}
		}
		//�������в����������Ʒ
		if(!found) {
			OrderItem oi = new OrderItem();
			oi.setUser(user);
			oi.setNumber(num);
			oi.setProduct(p);
			orderItemDAO.add(oi);
			oiid = oi.getId();
		}
		return "@forebuy?oiid=" + oiid;
	}
	
	//������Ľ���ҳ��
	public String buy(HttpServletRequest request, HttpServletResponse response, Page page) {
		//��ȡ���е�oiid
		String[] oiids = request.getParameterValues("oiid");
		List<OrderItem> ois = new ArrayList<OrderItem>();
		float total = 0;
		
		//����oiid��ȡois
		for(String strid : oiids ) {
			int oiid = Integer.parseInt(strid);
			OrderItem oi = orderItemDAO.get(oiid);
			total += oi.getProduct().getPromotePrice() * oi.getNumber();
			ois.add(oi);
		}
		
		//��ת��jspҳ��
		request.getSession().setAttribute("ois", ois);
		request.setAttribute("total", total);
		return "buy.jsp";
	}
	
	//��ӹ��ﳵ
	public String addCart(HttpServletRequest request, HttpServletResponse response, Page page) {
		int pid = Integer.parseInt(request.getParameter("pid"));
		Product product = productDAO.get(pid);
		int num = Integer.parseInt(request.getParameter("num"));
		User user = (User) request.getSession().getAttribute("user");
		
		List<OrderItem> ois = orderItemDAO.listByUser(user.getId());
		boolean found = false;
		//����������
		for(OrderItem oi : ois ) {
			if(oi.getProduct().getId() == pid) {
				oi.setNumber(oi.getNumber() + num);
				orderItemDAO.update(oi);
				found = true;
				break;
			}
		}
		
		if(!found) {
			OrderItem oi = new OrderItem();
			oi.setNumber(num);
			oi.setProduct(product);
			oi.setUser(user);
			orderItemDAO.add(oi);
		}
		
		return "%success";
	}
	
	//�鿴���ﳵ
	public String cart(HttpServletRequest request, HttpServletResponse response, Page page) {
		User user = (User)request.getSession().getAttribute("user");
		List<OrderItem> ois = orderItemDAO.listByUser(user.getId());
		
		request.setAttribute("ois", ois);
		return "cart.jsp";
	}
	
	//�޸Ĺ��ﳵ�ж����������
	public String changeOrderItem(HttpServletRequest request, HttpServletResponse response, Page page) {
		User user = (User)request.getSession().getAttribute("user");
		if(null == user)
			return "%fail";
		//�޸Ķ�����
		int pid = Integer.parseInt(request.getParameter("pid"));
		int number = Integer.parseInt(request.getParameter("number"));
		List<OrderItem> ois = orderItemDAO.listByUser(user.getId());
		for(OrderItem oi : ois) {
			if(oi.getProduct().getId() == pid) {
				oi.setNumber(number);
				orderItemDAO.update(oi);
				break;
			}
		}
		
		return "%success";
	}
	
	//ɾ�����ﳵ�Ķ�����
	public String deleteOrderItem(HttpServletRequest request, HttpServletResponse response, Page page){
	    User user =(User) request.getSession().getAttribute("user");
	    if(null==user)
	        return "%fail";
	    int oiid = Integer.parseInt(request.getParameter("oiid"));
	    orderItemDAO.delete(oiid);
	    return "%success";
	}
	
	//��������
	public String createOrder(HttpServletRequest request, HttpServletResponse response, Page page) {
		//1. ��session�л�ȡuser����
		User user = (User)request.getSession().getAttribute("user");
		
		List<OrderItem> ois = (List<OrderItem>)request.getSession().getAttribute("ois");
		if(ois.isEmpty()) {
			return "@login.jsp";
		}
		
		//2. ��ȡ��ַ���ʱ࣬�ջ��ˣ��û����Ե���Ϣ
		String address = request.getParameter("address");
		String post = request.getParameter("post");
		String receiver = request.getParameter("receiver");
		String mobile = request.getParameter("mobile");
		String userMessage = request.getParameter("userMessage");
		
		//3. ���ݵ�ǰʱ�����һ��4λ��������ɶ�����
		Order order = new Order();
		String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		
		//4. ��������������������������
		order.setOrderCode(orderCode);
		order.setAddress(address);
		order.setPost(post);
		order.setReceiver(receiver);
		order.setMobile(mobile);
		order.setUserMessage(userMessage);
		order.setCreateDate(new Date());
		order.setUser(user);
		//5. �Ѷ���״̬����Ϊ�ȴ�֧��
		order.setStatus(OrderDAO.waitPay);
		
		//6. ���뵽���ݿ�
		orderDAO.add(order);
		float total = 0;
		for(OrderItem oi:ois) {
			oi.setOrder(order);
			orderItemDAO.update(oi);
			total += oi.getProduct().getPromotePrice() * oi.getNumber();
		}
		
		//7.����alipay����
		return "@forealipay?oid="+order.getId() + "&total=" + total;
	}
	
	//��ת֧��ҳ��
	public String alipay(HttpServletRequest request, HttpServletResponse response, Page page) {
		return "alipay.jsp";
	}
	
	//���涩����֧���ɹ�
	public String payed(HttpServletRequest request, HttpServletResponse response, Page page) {
		//��ȡ�������޸�״̬��ʱ��
		int oid = Integer.parseInt(request.getParameter("oid"));
		Order order = orderDAO.get(oid);
		order.setStatus(OrderDAO.waitDelivery);
		order.setPayDate(new Date());
		new OrderDAO().update(order);
		request.setAttribute("o", order);
		
		return "payed.jsp";
	}
	
	//�鿴�Ѹ���Ķ���
	public String bought(HttpServletRequest request, HttpServletResponse response, Page page) {
		User user = (User)request.getSession().getAttribute("user");
		//��ѯuser���е�״̬����"delete" �Ķ�������os
		List<Order> os = orderDAO.list(user.getId(), OrderDAO.delete);
		//Ϊ��Щ������䶩����
		orderItemDAO.fill(os);
		
		request.setAttribute("os", os);
		return "bought.jsp";
	}
	
	//ȷ���ջ�
	public String confirmPay(HttpServletRequest request, HttpServletResponse response, Page page) {
	    int oid = Integer.parseInt(request.getParameter("oid"));
	    Order o = orderDAO.get(oid);
	    orderItemDAO.fill(o);
	    request.setAttribute("o", o);
	    return "confirmPay.jsp";       
	}
	
	//ȷ���ջ���ת��ȷ��ҳ��
	public String orderConfirmed(HttpServletRequest request, HttpServletResponse response) {
		int oid = Integer.parseInt(request.getParameter("oid"));
		Order o = orderDAO.get(oid);
		o.setStatus(orderDAO.waitReview);
		o.setConfirmDate(new Date());
		orderDAO.update(o);
		return "orderConfirmed.jsp";
	}
	
	//ȷ���ջ��ɹ�
	public String orderConfirmed(HttpServletRequest request, HttpServletResponse response, Page page) {
		int oid = Integer.parseInt(request.getParameter("oid"));
		Order o = orderDAO.get(oid);
		o.setStatus(OrderDAO.waitReview);
		o.setConfirmDate(new Date());
		orderDAO.update(o);
		return "orderConfirmed.jsp";
	}
	
	//ɾ������
	public String deleteOrder(HttpServletRequest request, HttpServletResponse response, Page page) {
		int oid = Integer.parseInt(request.getParameter("oid"));
		Order o = orderDAO.get(oid);
		o.setStatus(OrderDAO.delete);
		orderDAO.update(o);
		return "%success";
	}
	
	//����
	public String review(HttpServletRequest request, HttpServletResponse response, Page page) {
		int oid = Integer.parseInt(request.getParameter("oid"));
		Order o = orderDAO.get(oid);
		//Ϊ������䶩����
		orderItemDAO.fill(o);
		//��ȡ��һ��������Ĳ�Ʒ
		Product p = o.getOrderItems().get(0).getProduct();
		//��ȡ��Ʒ������
		List<Review> reviews = reviewDAO.list(p.getId());
		//�����ۼ����Ʒ��
		productDAO.setSaleAndReviewNumber(p);
		
		//��Ʒ������������
		request.setAttribute("p", p);
		request.setAttribute("o", o);
		request.setAttribute("reviews", reviews);
		return "review.jsp";   
	}
	
	//�ύ����
	public String doreview(HttpServletRequest request, HttpServletResponse response, Page page) {
		//�޸Ķ���״̬������
		int oid = Integer.parseInt(request.getParameter("oid"));
		Order o = orderDAO.get(oid);
		o.setStatus(OrderDAO.finish);
		orderDAO.update(o);
		
		//��ȡ��Ʒ,�������ݺ͵�ǰ�û�
		int pid = Integer.parseInt(request.getParameter("pid"));
		Product p = productDAO.get(pid);
		String content = request.getParameter("content");
		content = HtmlUtils.htmlEscape(content);
		User user = (User)request.getSession().getAttribute("user");
		
		//��װ��review
		Review review = new Review();
		review.setContent(content);
		review.setProduct(p);
		review.setCreateDate(new Date());
		review.setUser(user);
		reviewDAO.add(review);
		
		//��ʾ��ǰ������Ϣ
		return "@forereview?oid=" + oid + "&showonly=true";
	}
}