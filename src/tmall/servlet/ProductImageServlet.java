package tmall.servlet;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tmall.bean.Product;
import tmall.bean.ProductImage;
import tmall.dao.ProductImageDAO;
import tmall.util.ImageUtil;
import tmall.util.Page;

/**
 * ���Ӳ�ƷͼƬ�ֵ������������֣��������������ӵ��ύ��type���Ͳ�һ����
����ͶԵ����Ľ��н��⣬����ͼƬ�Ĵ���ͬ��
����ͼƬָ����ͼ�������������еȡ�С��3��ͼƬ������ͼƬֻ����һ�š�
 * @author 25126
 *
 */
public class ProductImageServlet extends BaseBackServlet{

	@Override
	public String add(HttpServletRequest request, HttpServletResponse response, Page page) {
		//�ύ�ϴ��ļ�����������
		Map<String, String> params = new HashMap<String, String>(); 
		
		//1. parseUpload ��ȡ�ϴ��ļ���������
		//2. parseUpload �������޸�params ���������Ұ�������ύ��type,pid��Ϣ��������
		InputStream is = parseUpload(request, params);
		
		//3. ��params ��ȡ��type,pid��Ϣ�����������type,pid������productImageDAO�������ݿ��в������ݡ�
		String type = params.get("type");
		int pid = Integer.parseInt(params.get("pid"));
		Product p = productDAO.get(pid);	
		ProductImage pi = new ProductImage();
		pi.setType(type);
		pi.setProduct(p);
		productImageDAO.add(pi);
		
		//�����ļ�
	    //5. �ļ������Ա��浽���ݿ�ķ�������id+".jpg"�ĸ�ʽ����
		String fileName = pi.getId() + ".jpg";
		String imageFolder;
		String imageFolder_small = null;	//����ͼƬ
		String imageFolder_middle = null;		//����ͼƬ
		//4. ����request.getSession().getServletContext().getRealPath( "img/productSingle")����λ����ŷ���ͼƬ��Ŀ¼
		//����productSingle������productSingle_middle��productSingle_small�� ��Ϊÿ�ϴ�һ��ͼƬ�������ж�Ӧ���������еȺ�С�����ִ�СͼƬ�����ҷ���3����ͬ��Ŀ¼��
		if(ProductImageDAO.type_single.equals(pi.getType())) {
			imageFolder = request.getSession().getServletContext().getRealPath("img/productSingle");
			imageFolder_small = request.getSession().getServletContext().getRealPath("img/productSingle_small");
			imageFolder_middle = request.getSession().getServletContext().getRealPath("img/productSingle_middle");
		} else {
			imageFolder = request.getSession().getServletContext().getRealPath("img/productDetail");
		}
		
		//�����ļ���
		File f = new File(imageFolder, fileName);
		f.getParentFile().mkdirs();
		
		//6. ���ݲ���1��ȡ������������������ύ���ļ������Ƶ�Ŀ���ļ�
		try {
			if(null != is && 0 != is.available()) {
				try {
					FileOutputStream fos = new FileOutputStream(f);
					byte[] b = new byte[1024*1024];
					int length = 0;
					while(-1 != (length = is.read(b))) {
						fos.write(b, 0, length);
					}
					fos.flush();
					//7. ����ImageUtil.change2jpg()�����Ѹ�ʽ����ת��Ϊjpg�����������Ǻ�׺��Ϊ.jpg
					BufferedImage img = ImageUtil.change2jpg(f);
					ImageIO.write(img, "jpg", f);
					
					if(productImageDAO.type_single.equals(pi.getType())) {
						File f_small = new File(imageFolder_small, fileName);
						File f_middle = new File(imageFolder_middle, fileName);
						//8. �ٽ���ImageUtil.resizeImage��������С��ͼƬ���ı��С֮�󣬷ֱ��Ƶ�productSingle_middle��productSingle_smallĿ¼�¡�
						ImageUtil.resizeImage(f, 56, 56, f_small);
						ImageUtil.resizeImage(f, 217, 190, f_middle);
					} 
					
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				} 
			}
		} catch (IOException e1) {
			// TODO: handle exception
			e1.printStackTrace();
		}
			
			//	9. �������֮�󣬿ͻ�������ת��admin_productImage_list?pid=��������pid��
			return "@admin_productImage_list?pid=" + p.getId();
	}

	@Override
	public String delete(HttpServletRequest request, HttpServletResponse response, Page page) {
		//1. ��ȡid
		int id = Integer.parseInt(request.getParameter("id"));
		//2. ����id��ȡProductImage ����pi
		ProductImage pi = productImageDAO.get(id);
		//3. ����productImageDAO��ɾ������
		productImageDAO.delete(id);
		
		//4. ����ǵ���ͼƬ����ôɾ��3���������еȣ�С��ͼƬ
		if(ProductImageDAO.type_single.equals(pi.getType())){
            String imageFolder_single= request.getSession().getServletContext().getRealPath("img/productSingle");
            String imageFolder_small= request.getSession().getServletContext().getRealPath("img/productSingle_small");
            String imageFolder_middle= request.getSession().getServletContext().getRealPath("img/productSingle_middle");
             
            File f_single =new File(imageFolder_single,pi.getId()+".jpg");
            f_single.delete();
            File f_small =new File(imageFolder_small,pi.getId()+".jpg");
            f_small.delete();
            File f_middle =new File(imageFolder_middle,pi.getId()+".jpg");
            f_middle.delete();
		}
		//5. ���������ͼƬ����ôɾ��һ��ͼƬ
		 else{
	            String imageFolder_detail= request.getSession().getServletContext().getRealPath("img/productDetail");
	            File f_detail =new File(imageFolder_detail,pi.getId()+".jpg");
	            f_detail.delete();         
	     }
		
		//6. �ͻ�����ת��admin_productImage_list��ַ
		return "@admin_productImage_list?pid=" + pi.getProduct().getId();
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
		//1. ��ȡ����pid
		int pid = Integer.parseInt(request.getParameter("pid"));
		//2. ����pid��ȡProduct����
		Product p = productDAO.get(pid);
		
		//3. ����product�����ȡ����ͼƬ�ļ���pisSingle
		List<ProductImage> pisSingle = productImageDAO.list(p, ProductImageDAO.type_single);
		//4. ����product�����ȡ����ͼƬ�ļ���pisDetail
		List<ProductImage> pisDetail = productImageDAO.list(p, ProductImageDAO.type_detail); 
		
		//5. ��product ����pisSingle ��pisDetail����request��
		request.setAttribute("p", p);
	    request.setAttribute("pisSingle", pisSingle);
	    request.setAttribute("pisDetail", pisDetail);
	     
	    return "admin/listProductImage.jsp";	

	}

}
