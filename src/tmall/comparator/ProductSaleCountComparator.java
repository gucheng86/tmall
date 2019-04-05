package tmall.comparator;

import java.util.Comparator;

import tmall.bean.Product;

public class ProductSaleCountComparator implements Comparator<Product>{
	public int compare(Product p1, Product p2) {
		return p2.getReviewCount() - p1.getReviewCount();
	}

}
