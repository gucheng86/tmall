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
	 * ��ӷ���
	 */
	public String add(HttpServletRequest request, HttpServletResponse response, Page page) {
		Map<String, String> params = new HashMap<String, String>();
		//1.parseUpload ��ȡ�ϴ��ļ��������������޸�params ����
		InputStream is = super.parseUpload(request, params);
		
		//3.��parmas�л�ȡname������name��������
		String name = params.get("name");
		Category c  = new Category();
		c.setName(name);
		categoryDAO.add(c);
		
		//4/��ȡ��Ŀ��ͼƬ·��
		File imageFolder = new File(request.getSession().getServletContext().getRealPath("img/category"));
		//5. �ļ������Ա��浽���ݿ�ķ�������id+".jpg"�ĸ�ʽ����
		File file = new File(imageFolder, c.getId() + ".jpg");	
		
		try {
			//�ڶ�д����ǰ�ȵ�֪���������ж��ٸ��ֽڿ��Զ�ȡ
			if(null != is && 0 != is.available()) {
				try(FileOutputStream fos = new FileOutputStream(file)) {
					byte b[] = new byte[1024 * 1024];
					int length = 0;
					//6. ���ݲ���1��ȡ������������������ύ���ļ������Ƶ�Ŀ���ļ�
					while(-1 != (length = is.read(b))) {
						fos.write(b, 0, length);
					}
					fos.flush();
					//7. ����ImageUtil.change2jpg()�����Ѹ�ʽ����ת��Ϊjpg�����������Ǻ�׺��Ϊ.jpg
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
	
	/** �༭���·�����add()��ӷ�������*/
	@Override
	public String update(HttpServletRequest request, HttpServletResponse response, Page page) {
		Map<String, String> params = new HashMap<String, String>();
		//1.parseUpload��ȡ�ϴ��ļ���������������name��Ϣ������params��
		InputStream is = super.parseUpload(request, params);
		
		System.out.println(params);
		//3.��params�л�ȡ������Ϣ�������µĶ���
		String name = params.get("name");
		int id = Integer.parseInt(params.get("id"));
		Category c = new Category();
		c.setId(id);
		c.setName(name);
		categoryDAO.update(c);
		
		//4. ����request.getServletContext().getRealPath( "img/category")����λ����ŷ���ͼƬ��Ŀ¼
		File  imageFolder= new File(request.getSession().getServletContext().getRealPath("img/category"));
	    //5. �ļ������Ա��浽���ݿ�ķ�������id+".jpg"�ĸ�ʽ����
		File file = new File(imageFolder,c.getId()+".jpg");
		file.getParentFile().mkdirs();
	    
		//����ͼƬ����Ŀ��
		try {
			//6. ���ͨ��parseUpload ��ȡ�����������ǿյģ��������еĿ�ȡ�ֽ���Ϊ0����ô�Ͳ������ϴ�����
			if(null != is && 0 != is.available()) {
				try(FileOutputStream fos = new FileOutputStream(file)) {
					byte b[] = new byte[1024 * 1024];
					int lenght = 0;
					//7. ���ݲ���1��ȡ������������������ύ���ļ������Ƶ�Ŀ���ļ�
					while(-1 != (lenght = is.read(b))) {
						fos.write(b, 0, lenght);
					}
					fos.flush();
					//8. ����ImageUtil.change2jpg()�����Ѹ�ʽ����ת��Ϊjpg�����������Ǻ�׺��Ϊ.jpg
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
		//��������
		List<Category> cs = categoryDAO.list(page.getStart(), page.getCount());
		//�������ݵ�����
		int total = categoryDAO.getTotal();
		page.setTotal(total);
		
		request.setAttribute("thecs", cs);
		request.setAttribute("page", page);
		
		return "admin/listCategory.jsp";
	}
}
