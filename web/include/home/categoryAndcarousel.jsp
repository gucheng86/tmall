<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false"%>

<script>
	//分类链接的变色、显示等
	function showProductsAsideCategorys(cid){
	    $("div.eachCategory[cid="+cid+"]").css("background-color","white");
	    $("div.eachCategory[cid="+cid+"] a").css("color","#87CEFA");
	    $("div.productsAsideCategorys[cid="+cid+"]").show();
	}
	//分类链接的变色、隐藏等
	function hideProductsAsideCategorys(cid){
	    $("div.eachCategory[cid="+cid+"]").css("background-color","#e2e2e3");
	    $("div.eachCategory[cid="+cid+"] a").css("color","#000");
	    $("div.productsAsideCategorys[cid="+cid+"]").hide();
	}
	
	$(function(){
		//鼠标进入分类链接
		 $("div.eachCategory").mouseenter(function(){
	        var cid = $(this).attr("cid");
	        showProductsAsideCategorys(cid);
	    });
		//鼠标离开分类链接
		 $("div.eachCategory").mouseleave(function(){
	        var cid = $(this).attr("cid");
	        hideProductsAsideCategorys(cid);
	    });
		//鼠标进入产品链接
	  	$("div.productsAsideCategorys").mouseenter(function(){
	        var cid = $(this).attr("cid");
	        showProductsAsideCategorys(cid);
    	});
		//鼠标离开产品链接
	     $("div.productsAsideCategorys").mouseleave(function(){
	        var cid = $(this).attr("cid");
	        hideProductsAsideCategorys(cid);
    	});
		
		//右边分类产品的显示
		$("div.rightMenu span").mouseenter(function(){
			var left = $(this).position().left;
			var top = $(this).position().top;
			var width = $(this).css("width");
			var destLeft = parseInt(left) + parseInt(width) / 2;
			$("img#catear").css("left", destLeft);
			$("img#catear").css("top", top-20);
			$("img#catear").fadeIn(500);
		})
		$("div.rightMenu span").mouseleave(function(){
			$("img#catear").hide();
		})
		
		
		//设置样式
		var left = $("div#carouse-of-product").offset().left();
		$("div.categoryMenu").css("left", left-20);
		$("div.categoryWithCarousel div.head").css("margin-left", left);
		$("div.productsAsideCategroys").css("left", left-20);
		console.log();
	})
	
</script>

<img src="img/site/catear.png" id="catear" class="catear"/>
     
<div class="categoryWithCarousel">
 
<div class="headbar show1">
    <div class="head ">
     
        <span style="margin-left:10px" class="glyphicon glyphicon-th-list"></span>
        <span style="margin-left:10px" >商品分类</span>
         
    </div>
     
    <div class="rightMenu">
        <span><a href=""><img src="img/site/chaoshi.png"/></a></span>
        <span><a href=""><img src="img/site/guoji.png"/></a></span>
 		<!-- 显示5个分类 -->
        <c:forEach items="${cs}" var="c" varStatus="st">
            <c:if test="${st.count<=4}">
                <span>
                <a href="forecategory?cid=${c.id}">
                    ${c.name}
                </a></span>        
            </c:if>
        </c:forEach>
    </div>
     
</div>
 
<div style="position: relative">
    <%@include file="categoryMenu.jsp" %>
</div>
 
<div style="position: relative;left: 0;top: 0;">
    <%@include file="productsAsideCategorys.jsp" %>
</div>
 
<%@include file="carousel.jsp" %>
 
<div class="carouselBackgroundDiv">
</div>
 
</div>


