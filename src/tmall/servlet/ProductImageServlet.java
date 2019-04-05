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
 * 增加产品图片分单个和详情两种，其区别在于增加的提交的type类型不一样。
这里就对单个的进行讲解，详情图片的处理同理。
单个图片指缩略图，包括正常、中等、小号3张图片，详情图片只包含一张。
 * @author 25126
 *
 */
public class ProductImageServlet extends BaseBackServlet{

	@Override
	public String add(HttpServletRequest request, HttpServletResponse response, Page page) {
		//提交上传文件的其他参数
		Map<String, String> params = new HashMap<String, String>(); 
		
		//1. parseUpload 获取上传文件的输入流
		//2. parseUpload 方法会修改params 参数，并且把浏览器提交的type,pid信息放在其中
		InputStream is = parseUpload(request, params);
		
		//3. 从params 中取出type,pid信息，并根据这个type,pid，借助productImageDAO，向数据库中插入数据。
		String type = params.get("type");
		int pid = Integer.parseInt(params.get("pid"));
		Product p = productDAO.get(pid);	
		ProductImage pi = new ProductImage();
		pi.setType(type);
		pi.setProduct(p);
		productImageDAO.add(pi);
		
		//生成文件
	    //5. 文件命名以保存到数据库的分类对象的id+".jpg"的格式命名
		String fileName = pi.getId() + ".jpg";
		String imageFolder;
		String imageFolder_small = null;	//单个图片
		String imageFolder_middle = null;		//详情图片
		//4. 根据request.getSession().getServletContext().getRealPath( "img/productSingle")，定位到存放分类图片的目录
		//除了productSingle，还有productSingle_middle和productSingle_small。 因为每上传一张图片，都会有对应的正常，中等和小的三种大小图片，并且放在3个不同的目录下
		if(ProductImageDAO.type_single.equals(pi.getType())) {
			imageFolder = request.getSession().getServletContext().getRealPath("img/productSingle");
			imageFolder_small = request.getSession().getServletContext().getRealPath("img/productSingle_small");
			imageFolder_middle = request.getSession().getServletContext().getRealPath("img/productSingle_middle");
		} else {
			imageFolder = request.getSession().getServletContext().getRealPath("img/productDetail");
		}
		
		//创建文件流
		File f = new File(imageFolder, fileName);
		f.getParentFile().mkdirs();
		
		//6. 根据步骤1获取的输入流，把浏览器提交的文件，复制到目标文件
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
					//7. 借助ImageUtil.change2jpg()方法把格式真正转化为jpg，而不仅仅是后缀名为.jpg
					BufferedImage img = ImageUtil.change2jpg(f);
					ImageIO.write(img, "jpg", f);
					
					if(productImageDAO.type_single.equals(pi.getType())) {
						File f_small = new File(imageFolder_small, fileName);
						File f_middle = new File(imageFolder_middle, fileName);
						//8. 再借助ImageUtil.resizeImage把正常大小的图片，改变大小之后，分别复制到productSingle_middle和productSingle_small目录下。
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
			
			//	9. 处理完毕之后，客户端条跳转到admin_productImage_list?pid=，并带上pid。
			return "@admin_productImage_list?pid=" + p.getId();
	}

	@Override
	public String delete(HttpServletRequest request, HttpServletResponse response, Page page) {
		//1. 获取id
		int id = Integer.parseInt(request.getParameter("id"));
		//2. 根据id获取ProductImage 对象pi
		ProductImage pi = productImageDAO.get(id);
		//3. 借助productImageDAO，删除数据
		productImageDAO.delete(id);
		
		//4. 如果是单个图片，那么删除3张正常，中等，小号图片
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
		//5. 如果是详情图片，那么删除一张图片
		 else{
	            String imageFolder_detail= request.getSession().getServletContext().getRealPath("img/productDetail");
	            File f_detail =new File(imageFolder_detail,pi.getId()+".jpg");
	            f_detail.delete();         
	     }
		
		//6. 客户端跳转到admin_productImage_list地址
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
		//1. 获取参数pid
		int pid = Integer.parseInt(request.getParameter("pid"));
		//2. 根据pid获取Product对象
		Product p = productDAO.get(pid);
		
		//3. 根据product对象获取单个图片的集合pisSingle
		List<ProductImage> pisSingle = productImageDAO.list(p, ProductImageDAO.type_single);
		//4. 根据product对象获取详情图片的集合pisDetail
		List<ProductImage> pisDetail = productImageDAO.list(p, ProductImageDAO.type_detail); 
		
		//5. 把product 对象，pisSingle ，pisDetail放在request上
		request.setAttribute("p", p);
	    request.setAttribute("pisSingle", pisSingle);
	    request.setAttribute("pisDetail", pisDetail);
	     
	    return "admin/listProductImage.jsp";	

	}

}
