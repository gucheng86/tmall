package tmall.util;

/**
 * DateUtil这个日期工具类主要是用于java.util.Date类与java.sql.Timestamp 类的互相转换。
 * 因为实体类中日期采用Date类型，而数据库中日期采用Timestamp类型以保留日期
 * @author 25126
 *
 */
public class DateUtil {

	public static java.sql.Timestamp d2t(java.util.Date d) {
		if (null == d)
			return null;
		return new java.sql.Timestamp(d.getTime());
	}

	public static java.util.Date t2d(java.sql.Timestamp t) {
		if (null == t)
			return null;
		return new java.util.Date(t.getTime());
	}
}
