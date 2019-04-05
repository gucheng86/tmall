package tmall.comparator;

import java.util.Comparator;

import tmall.bean.Product;

//根据价格来排序
public class ProductPriceComparator implements Comparator<Product>{
	public int compare(Product p1, Product p2) {
		return (int)(p1.getPromotePrice() - p2.getPromotePrice());
	}
}
