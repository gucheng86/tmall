package tmall.servlet;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tmall.bean.Category;
import tmall.bean.Product;
import tmall.bean.Property;
import tmall.bean.PropertyValue;
import tmall.util.Page;

//����PropertyValue�༭���޸ĵ�֧�ַ�����ProductServlet�н��У�
//��Ҫ��editPropertyValue��updatePropertyValue����������
public class ProductServlet extends BaseBackServlet{

	@Override
	public String add(HttpServletRequest request, HttpServletResponse response, Page page) {
		//����cid��ȡcategory
		int cid = Integer.parseInt(request.getParameter("cid"));
		Category c = categoryDAO.get(cid);
		
		//��ȡ��д�Ĳ���
		String name = request.getParameter("name");
		String subTitle = request.getParameter("subTitle");
		float orignalPrice = Float.parseFloat(request.getParameter("orignalPrice"));
		float promotePrice = Float.parseFloat(request.getParameter("promotePrice"));
		int stock = Integer.parseInt(request.getParameter("stock"));
		
		//��װ�ɶ���
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
		//����Product��id��ɾ��
		int id = Integer.getInteger(request.getParameter("id"));
		Property p = propertyDAO.get(id);
		productDAO.delete(id);
		return "@admin_product_list?cid=" + p.getId();
	}

	@Override
	public String edit(HttpServletRequest request, HttpServletResponse response, Page page) {
		//����id��ȡ��Ʒ����
		int id = Integer.parseInt(request.getParameter("id"));
		Product p = productDAO.get(id);
		request.setAttribute("p", p);
		return "admin/editProduct.jsp";
	}
	
	//ͨ����Ʒ���������������ԣ������༭ҳ��
	public String editPropertyValue(HttpServletRequest request, HttpServletResponse response, Page page) {
		//1. ��ȡ����id
		int id = Integer.parseInt(request.getParameter("id"));
		//2. ����id��ȡProduct����p
		Product p = productDAO.get(id);
		request.setAttribute("p", p);
		
		//3. ��ʼ������ֵ�� propertyValueDAO.init(p)�� ��Ϊ����ǵ�һ�η��ʣ���Щ����ֵ�ǲ����ڵġ�
		propertyValueDAO.init(p);
		//4. ����Product��id����ȡ��Ʒ��Ӧ������ֵ����
		List<PropertyValue> pvs = propertyValueDAO.list(p.getId());
		//5. ����ֵ���Ϸ���request�� "pvs" ������
		request.setAttribute("pvs", pvs);
		
		//6. �������ת��admin/editProductValue.jsp ��
		return "admin/editProductValue.jsp";
	}
	
	//�޸Ĺ��ܲ��õ���ʹ��post��ʽ�ύajax���첽���÷�ʽ
	public String updatePropertyValue(HttpServletRequest request, HttpServletResponse response, Page page) {
		//6. admin_product_updatePropertyValue����ProductServlet��updatePropertyValue����������
		//6.1 ��ȡpvid
		int pvid = Integer.parseInt(request.getParameter("pvid"));
		//6.2 ��ȡvalue
		String value = request.getParameter("value");
		
		//6.3 ����pvid��value,����PropertyValue����
		PropertyValue pv = propertyValueDAO.get(pvid);
		pv.setValue(value);
		propertyValueDAO.update(pv);
		
		//6.4 ����"%success"
		return "%success";
//		7. BaseBackServlet���ݷ���ֵ"%success"��ֱ������ַ���"success" �������
//		8. ������ж��������ֵ��"success",��ô�Ͱѱ߿�����Ϊ��ɫ����ʾ�޸ĳɹ�����������Ϊ��ɫ����ʾ�޸�ʧ��
	}
	
	@Override
	public String update(HttpServletRequest request, HttpServletResponse response, Page page) {
		//����cid��ȡcategory
		int cid = Integer.parseInt(request.getParameter("cid"));
		Category c = categoryDAO.get(cid);
		
		int id = Integer.parseInt(request.getParameter("id"));	
		//��ȡ��д�Ĳ���
		String name = request.getParameter("name");
		String subTitle = request.getParameter("subTitle");
		float orignalPrice = Float.parseFloat(request.getParameter("orignalPrice"));
		float promotePrice = Float.parseFloat(request.getParameter("promotePrice"));
		int stock = Integer.parseInt(request.getParameter("stock"));
		
		//��װ�ɶ���
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
		//����cid����ȡ��ǰ�����µ����Լ���
		List<Product> ps = productDAO.list(cid, page.getStart(), page.getCount());
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
		return "admin/listProduct.jsp";
	}
	
}
