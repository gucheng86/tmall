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
	 * 为产品分类填充产品
	 */
	public String home(HttpServletRequest request, HttpServletResponse response, Page page) {
		List<Category> cs = categoryDAO.list();
		new ProductDAO().fill(cs);
		new ProductDAO().fillByRow(cs);
		
		request.setAttribute("cs", cs);
		return "home.jsp";
	}
	
	//注册方法
	public String register(HttpServletRequest request, HttpServletResponse response, Page page) {
		//1. 获取账号密码
		String name = request.getParameter("name");
		String password = request.getParameter("password");

		//2. 通过HtmlUtils.htmlEscape(name);把账号里的特殊符号进行转义
		name = HtmlUtils.htmlEscape(name);
		System.out.println(name);
		boolean exist = userDAO.isExist(name);
		
		//3. 判断用户名是否存在
		//3.1 如果已经存在，就服务端跳转到reigster.jsp，并且设置错误提示信息
		if (exist) {
			request.setAttribute("msg", "该用户名已被注册");
			return "register.jsp";
		}
		
		//3.2 如果不存在，则加入到数据库中，并服务端跳转到registerSuccess.jsp页面
		User user = new User();
		user.setName(name);
		user.setPassword(password);
		System.out.println(user.getName());
		System.out.println(user.getPassword());
		userDAO.add(user);
		return "@registerSuccess.jsp";
	}

	//登录方法
	public String login(HttpServletRequest request, HttpServletResponse response, Page page) throws IOException {
		//1. 获取账号密码
		String name = request.getParameter("name");
		//2. 把账号通过HtmlUtils.htmlEscape进行转义
		name = HtmlUtils.htmlEscape(name);
		String password = request.getParameter("password");
		
		System.out.println(name + "," + password);
		//3. 根据账号和密码获取User对象
		//3.1 如果对象为空，则服务端跳转回login.jsp，也带上错误信息，并且使用login.jsp 中的办法显示错误信息
		User user = userDAO.get(name, password);
		if(null == user) {
			request.setAttribute("msg", "账号密码错误");
			return "login.jsp";
		}
		
		//3.2 如果对象存在，则把对象保存在session中，并客户端跳转到首页"@forehome"
		request.getSession().setAttribute("user", user);
		System.out.println(request.getSession().getAttribute("user"));
		
		return "@forehome";
	}
	
	//退出登录
	public String logout(HttpServletRequest request, HttpServletResponse response, Page page) {
		//1. 在session中去掉"user"
		request.getSession().removeAttribute("user");
		return "@forehome";
	}
	
	
	
	//检查是否登录
	public String checkLogin(HttpServletRequest request, HttpServletResponse response, Page page) {
		User user = (User)request.getSession().getAttribute("user");
		if(null != user)
			return "%success";
		return "%fail";
	}
	
	//检查模态窗口中通过Ajax方式的登录是否有效
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
	
	//搜索方法
	public String search(HttpServletRequest request, HttpServletResponse response, Page page) {
		String keyword = request.getParameter("keyword");
		//获取满足条件的前20个产品
		List<Product> ps = productDAO.search(keyword, 0, 20); 
		//为这些产品设置销量和评价
		productDAO.setSaleAndReviewNumber(ps);
		
		request.setAttribute("ps", ps);
		return "searchResult.jsp";
	}
	
	//为商品显示传递参数：单个图片、详情图片、属性值、评价、销量
	public String product(HttpServletRequest request, HttpServletResponse response, Page page) {
		int pid = Integer.parseInt(request.getParameter("pid"));
		Product p = productDAO.get(pid);
		
		//3. 根据对象p，获取这个产品对应的单个图片集合
		List<ProductImage> productSingleImages = productImageDAO.list(p, productImageDAO.type_single);
		//4. 根据对象p，获取这个产品对应的详情图片集合
		List<ProductImage> productDetailImages = productImageDAO.list(p, productImageDAO.type_detail);
		p.setProductSingleImages(productSingleImages);
		p.setProductDetailImages(productDetailImages);
		
		//获取产品的属性值和评价
		List<PropertyValue> pvs = propertyValueDAO.list(p.getId());
		List<Review> reviews = reviewDAO.list(p.getId());
		
		//设置产品的销量和评价数量
		new ProductDAO().setSaleAndReviewNumber(p);
		
		request.setAttribute("reviews", reviews);
		request.setAttribute("pvs", pvs);
		request.setAttribute("p", p);
		
		return "product.jsp";
	}
	
	//显示分类
	public String category(HttpServletRequest request,HttpServletResponse response,  Page page) {
		//为分类填充产品等信息
		int cid = Integer.parseInt(request.getParameter("cid"));
		Category c = categoryDAO.get(cid);
		new ProductDAO().fill(c);
		new ProductDAO().setSaleAndReviewNumber(c.getProducts());
		
		//根据参数对分类排序
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
	
	
	//立即购买按钮的跳转方法
	public String buyone(HttpServletRequest request, HttpServletResponse response, Page page) {
		//获取p和user
		int pid = Integer.parseInt(request.getParameter("pid"));
		int num = Integer.parseInt(request.getParameter("num"));
		Product p = productDAO.get(pid);
		int oiid = 0;
		
		User user = (User)request.getSession().getAttribute("user");
		//订单项中存在这个产品
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
		//订单项中不存在这个产品
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
	
	//订单项的结算页面
	public String buy(HttpServletRequest request, HttpServletResponse response, Page page) {
		//获取所有的oiid
		String[] oiids = request.getParameterValues("oiid");
		List<OrderItem> ois = new ArrayList<OrderItem>();
		float total = 0;
		
		//根据oiid获取ois
		for(String strid : oiids ) {
			int oiid = Integer.parseInt(strid);
			OrderItem oi = orderItemDAO.get(oiid);
			total += oi.getProduct().getPromotePrice() * oi.getNumber();
			ois.add(oi);
		}
		
		//跳转到jsp页面
		request.getSession().setAttribute("ois", ois);
		request.setAttribute("total", total);
		return "buy.jsp";
	}
	
	//添加购物车
	public String addCart(HttpServletRequest request, HttpServletResponse response, Page page) {
		int pid = Integer.parseInt(request.getParameter("pid"));
		Product product = productDAO.get(pid);
		int num = Integer.parseInt(request.getParameter("num"));
		User user = (User) request.getSession().getAttribute("user");
		
		List<OrderItem> ois = orderItemDAO.listByUser(user.getId());
		boolean found = false;
		//遍历订单项
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
	
	//查看购物车
	public String cart(HttpServletRequest request, HttpServletResponse response, Page page) {
		User user = (User)request.getSession().getAttribute("user");
		List<OrderItem> ois = orderItemDAO.listByUser(user.getId());
		
		request.setAttribute("ois", ois);
		return "cart.jsp";
	}
	
	//修改购物车中订单项的数量
	public String changeOrderItem(HttpServletRequest request, HttpServletResponse response, Page page) {
		User user = (User)request.getSession().getAttribute("user");
		if(null == user)
			return "%fail";
		//修改订单项
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
	
	//删除购物车的订单项
	public String deleteOrderItem(HttpServletRequest request, HttpServletResponse response, Page page){
	    User user =(User) request.getSession().getAttribute("user");
	    if(null==user)
	        return "%fail";
	    int oiid = Integer.parseInt(request.getParameter("oiid"));
	    orderItemDAO.delete(oiid);
	    return "%success";
	}
	
	//创建订单
	public String createOrder(HttpServletRequest request, HttpServletResponse response, Page page) {
		//1. 从session中获取user对象
		User user = (User)request.getSession().getAttribute("user");
		
		List<OrderItem> ois = (List<OrderItem>)request.getSession().getAttribute("ois");
		if(ois.isEmpty()) {
			return "@login.jsp";
		}
		
		//2. 获取地址，邮编，收货人，用户留言等信息
		String address = request.getParameter("address");
		String post = request.getParameter("post");
		String receiver = request.getParameter("receiver");
		String mobile = request.getParameter("mobile");
		String userMessage = request.getParameter("userMessage");
		
		//3. 根据当前时间加上一个4位随机数生成订单号
		Order order = new Order();
		String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
		
		//4. 根据上述参数，创建订单对象
		order.setOrderCode(orderCode);
		order.setAddress(address);
		order.setPost(post);
		order.setReceiver(receiver);
		order.setMobile(mobile);
		order.setUserMessage(userMessage);
		order.setCreateDate(new Date());
		order.setUser(user);
		//5. 把订单状态设置为等待支付
		order.setStatus(OrderDAO.waitPay);
		
		//6. 加入到数据库
		orderDAO.add(order);
		float total = 0;
		for(OrderItem oi:ois) {
			oi.setOrder(order);
			orderItemDAO.update(oi);
			total += oi.getProduct().getPromotePrice() * oi.getNumber();
		}
		
		//7.调用alipay方法
		return "@forealipay?oid="+order.getId() + "&total=" + total;
	}
	
	//跳转支付页面
	public String alipay(HttpServletRequest request, HttpServletResponse response, Page page) {
		return "alipay.jsp";
	}
	
	//保存订单，支付成功
	public String payed(HttpServletRequest request, HttpServletResponse response, Page page) {
		//获取订单，修改状态和时间
		int oid = Integer.parseInt(request.getParameter("oid"));
		Order order = orderDAO.get(oid);
		order.setStatus(OrderDAO.waitDelivery);
		order.setPayDate(new Date());
		new OrderDAO().update(order);
		request.setAttribute("o", order);
		
		return "payed.jsp";
	}
	
	//查看已付款的订单
	public String bought(HttpServletRequest request, HttpServletResponse response, Page page) {
		User user = (User)request.getSession().getAttribute("user");
		//查询user所有的状态不是"delete" 的订单集合os
		List<Order> os = orderDAO.list(user.getId(), OrderDAO.delete);
		//为这些订单填充订单项
		orderItemDAO.fill(os);
		
		request.setAttribute("os", os);
		return "bought.jsp";
	}
	
	//确认收货
	public String confirmPay(HttpServletRequest request, HttpServletResponse response, Page page) {
	    int oid = Integer.parseInt(request.getParameter("oid"));
	    Order o = orderDAO.get(oid);
	    orderItemDAO.fill(o);
	    request.setAttribute("o", o);
	    return "confirmPay.jsp";       
	}
	
	//确认收货跳转到确认页面
	public String orderConfirmed(HttpServletRequest request, HttpServletResponse response) {
		int oid = Integer.parseInt(request.getParameter("oid"));
		Order o = orderDAO.get(oid);
		o.setStatus(orderDAO.waitReview);
		o.setConfirmDate(new Date());
		orderDAO.update(o);
		return "orderConfirmed.jsp";
	}
	
	//确认收货成功
	public String orderConfirmed(HttpServletRequest request, HttpServletResponse response, Page page) {
		int oid = Integer.parseInt(request.getParameter("oid"));
		Order o = orderDAO.get(oid);
		o.setStatus(OrderDAO.waitReview);
		o.setConfirmDate(new Date());
		orderDAO.update(o);
		return "orderConfirmed.jsp";
	}
	
	//删除订单
	public String deleteOrder(HttpServletRequest request, HttpServletResponse response, Page page) {
		int oid = Integer.parseInt(request.getParameter("oid"));
		Order o = orderDAO.get(oid);
		o.setStatus(OrderDAO.delete);
		orderDAO.update(o);
		return "%success";
	}
	
	//评价
	public String review(HttpServletRequest request, HttpServletResponse response, Page page) {
		int oid = Integer.parseInt(request.getParameter("oid"));
		Order o = orderDAO.get(oid);
		//为订单填充订单项
		orderItemDAO.fill(o);
		//获取第一个订单项的产品
		Product p = o.getOrderItems().get(0).getProduct();
		//获取产品的评论
		List<Review> reviews = reviewDAO.list(p.getId());
		//将评论加入产品中
		productDAO.setSaleAndReviewNumber(p);
		
		//产品，订单，评论
		request.setAttribute("p", p);
		request.setAttribute("o", o);
		request.setAttribute("reviews", reviews);
		return "review.jsp";   
	}
	
	//提交评价
	public String doreview(HttpServletRequest request, HttpServletResponse response, Page page) {
		//修改订单状态并更新
		int oid = Integer.parseInt(request.getParameter("oid"));
		Order o = orderDAO.get(oid);
		o.setStatus(OrderDAO.finish);
		orderDAO.update(o);
		
		//获取产品,评价内容和当前用户
		int pid = Integer.parseInt(request.getParameter("pid"));
		Product p = productDAO.get(pid);
		String content = request.getParameter("content");
		content = HtmlUtils.htmlEscape(content);
		User user = (User)request.getSession().getAttribute("user");
		
		//封装到review
		Review review = new Review();
		review.setContent(content);
		review.setProduct(p);
		review.setCreateDate(new Date());
		review.setUser(user);
		reviewDAO.add(review);
		
		//显示当前评价信息
		return "@forereview?oid=" + oid + "&showonly=true";
	}
}