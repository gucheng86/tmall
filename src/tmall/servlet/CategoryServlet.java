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

import tmall.bean.Category;
import tmall.util.ImageUtil;
import tmall.util.Page;

@SuppressWarnings("serial")
public class CategoryServlet extends BaseBackServlet{
	/**
	 * 添加分类
	 */
	public String add(HttpServletRequest request, HttpServletResponse response, Page page) {
		Map<String, String> params = new HashMap<String, String>();
		//1.parseUpload 获取上传文件的输入流，并修改params 参数
		InputStream is = super.parseUpload(request, params);
		
		//3.从parmas中获取name，根据name插入数据
		String name = params.get("name");
		Category c  = new Category();
		c.setName(name);
		categoryDAO.add(c);
		
		//4/获取项目的图片路径
		File imageFolder = new File(request.getSession().getServletContext().getRealPath("img/category"));
		//5. 文件命名以保存到数据库的分类对象的id+".jpg"的格式命名
		File file = new File(imageFolder, c.getId() + ".jpg");	
		
		try {
			//在读写操作前先得知数据流里有多少个字节可以读取
			if(null != is && 0 != is.available()) {
				try(FileOutputStream fos = new FileOutputStream(file)) {
					byte b[] = new byte[1024 * 1024];
					int length = 0;
					//6. 根据步骤1获取的输入流，把浏览器提交的文件，复制到目标文件
					while(-1 != (length = is.read(b))) {
						fos.write(b, 0, length);
					}
					fos.flush();
					//7. 借助ImageUtil.change2jpg()方法把格式真正转化为jpg，而不仅仅是后缀名为.jpg
					BufferedImage img = ImageUtil.change2jpg(file);
					ImageIO.write(img, "jpg", file);
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "@admin_category_list";
	}

	@Override
	public String delete(HttpServletRequest request, HttpServletResponse response, Page page) {
		int id = Integer.parseInt(request.getParameter("id"));
		categoryDAO.delete(id);
		return "@admin_category_list";
	}

	@Override
	public String edit(HttpServletRequest request, HttpServletResponse response, Page page) {
		// TODO Auto-generated method stub
		int id = Integer.parseInt(request.getParameter("id"));
		Category c = categoryDAO.get(id);
		request.setAttribute("c", c);
		return "admin/editCategory.jsp";
	}
	
	/** 编辑更新方法与add()添加方法类似*/
	@Override
	public String update(HttpServletRequest request, HttpServletResponse response, Page page) {
		Map<String, String> params = new HashMap<String, String>();
		//1.parseUpload获取上传文件的输入流，并将name信息保存在params中
		InputStream is = super.parseUpload(request, params);
		
		System.out.println(params);
		//3.从params中获取参数信息，创建新的对象
		String name = params.get("name");
		int id = Integer.parseInt(params.get("id"));
		Category c = new Category();
		c.setId(id);
		c.setName(name);
		categoryDAO.update(c);
		
		//4. 根据request.getServletContext().getRealPath( "img/category")，定位到存放分类图片的目录
		File  imageFolder= new File(request.getSession().getServletContext().getRealPath("img/category"));
	    //5. 文件命名以保存到数据库的分类对象的id+".jpg"的格式命名
		File file = new File(imageFolder,c.getId()+".jpg");
		file.getParentFile().mkdirs();
	    
		//保存图片到项目中
		try {
			//6. 如果通过parseUpload 获取到的输入流是空的，或者其中的可取字节数为0，那么就不进行上传处理
			if(null != is && 0 != is.available()) {
				try(FileOutputStream fos = new FileOutputStream(file)) {
					byte b[] = new byte[1024 * 1024];
					int lenght = 0;
					//7. 根据步骤1获取的输入流，把浏览器提交的文件，复制到目标文件
					while(-1 != (lenght = is.read(b))) {
						fos.write(b, 0, lenght);
					}
					fos.flush();
					//8. 借助ImageUtil.change2jpg()方法把格式真正转化为jpg，而不仅仅是后缀名为.jpg
					BufferedImage img = ImageUtil.change2jpg(file);
					ImageIO.write(img, "jpg", file);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			} catch (IOException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		
		return "@admin_category_list";
	}

	@Override
	public String list(HttpServletRequest request, HttpServletResponse response, Page page) {
		// TODO Auto-generated method stub
		//分类数组
		List<Category> cs = categoryDAO.list(page.getStart(), page.getCount());
		//设置数据的总数
		int total = categoryDAO.getTotal();
		page.setTotal(total);
		
		request.setAttribute("thecs", cs);
		request.setAttribute("page", page);
		
		return "admin/listCategory.jsp";
	}
}
