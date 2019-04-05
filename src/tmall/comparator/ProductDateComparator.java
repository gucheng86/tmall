package tmall.comparator;

import java.util.Comparator;

import tmall.bean.Product;

//根据创建的日期来排序
public class ProductDateComparator implements Comparator<Product>{
	public int compare(Product p1, Product p2) {
		return p1.getCreateDate().compareTo(p2.getCreateDate());
	}

}
