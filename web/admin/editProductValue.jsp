<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" import="java.util.*"%>
 
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@include file="../include/admin/adminHeader.jsp"%>
<%@include file="../include/admin/adminNavigator.jsp"%>
 
<title>编辑产品属性值</title>


<!-- 1. 监听输入框上的keyup事件
2. 获取输入框里的值
3. 获取输入框上的自定义属性pvid，这就是当前PropertyValue对应的id
4. 把边框的颜色修改为黄色，表示正在修改的意思
5. 借助JQuery的ajax函数 $.post，把id和值，提交到admin_product_updatePropertyValu
-->
<script>
$(function(){
	$("input.pvValue").keyup(function(){
		var value = $(this).val();
		var page = "admin_product_updatePropertyValue";
		var pvid = $(this).attr("pvid");
		var parentSpan = $(this).parent("span");
		parentSpan.css("border", "1px solid yellow");
		
		$.post(
				page,
				{"value":value, "pvid":pvid},
				function(result) {
					if("succes" == result) 
						parentSpan.css("border", "1px solid green");
					else
						parentSpan.css("border", "1px solid red");
				})
	})
})
</script>

<!-- 导肮部分 -->	
<div class="workingArea">
    <ol class="breadcrumb">
      <li><a href="admin_category_list">所有分类</a></li>
      <li><a href="admin_product_list?cid=${p.category.id}">${p.category.name}</a></li>
      <li class="active">${p.name}</li>
      <li class="active">编辑产品属性</li>
    </ol>
    
    <div class= editPVDiv">
    	<c:forEach items="${pvs }" var="pv">
    		<div class="editPV">
    			<div class="eachPV">
    				<span class="pvName">${pv.property.name }</span>
    				<span class="pvValue"><input class="pvValue" pvid="${pv.id }" type="text" value="${pv.value}"></span>
    			</div>
    		</div>
    	</c:forEach>
    	
  		<div style="clear:both"></div>
    </div>
</div>