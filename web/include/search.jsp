<!-- 
这里会从request的属性"cs" 中获取到分类集合，并取第5个到第8个，一共4个来显示。
 -->
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
   
<a href="${contextPath }">
	<!-- 天猫logo -->
	<img id="logo" src="img/site/logo.gif" class="logo">
</a>

<form action="foresearch" method="post">
	<!-- 搜索框 -->
	<div class="searchDiv">
		<input name="keyword" type="text" placeholder="时尚男鞋	太阳镜	">
		<button type="submit" class="searchButton">搜索</button>
		<!-- 选取第5个到第8个产品分类 -->
		<div class="searchBelow">
			<c:forEach items="${cs }" var="c" varStatus="st">
				<c:if test="${st.count>=5 and st.count<=8}">
					<span>
						<a href="forecategory?cid=${c.id}">
							${c.name }
						</a> 
						<c:if test="${st.count!=8 }">
							<span>|</span>
						</c:if>
					</span>
				</c:if>
			</c:forEach>
		</div>
	</div>
</form>