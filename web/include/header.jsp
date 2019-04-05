<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" isELIgnored="false"%>
<!-- 
c通常用于条件判断和遍历
fmt用于格式化日期和货币
fn用于校验长度 -->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<% String path=request.getContextPath(); %>
<html>
	<head>
		<script src="<%=path %>/js/jquery/2.0.0/jquery.min.js"></script>
		<link href="<%=path %>/css/bootstrap/3.3.6/bootstrap.min.css" rel="stylesheet">
		<script src="<%=path %>/js/bootstrap/3.3.6/bootstrap.min.js"></script>
		<link href="<%=path %>/css/fore/style.css" rel="stylesheet">
		<script>
		//格式化货币
		function formatMoney(num){
			//替换掉全部匹配的字符
			num = num.toString().replace(/\$|\,/g,''); 
			if(isNaN(num)) 
				num = "0";
			sign = (num == (num = Math.abs(num)));
			//向下取整
			num = Math.floor(num*100 + 0.50000000001);
			cents = num % 100;
			//向下取整
			num = Math.floor(num / 100).toString();
			if(cents < 10) 
				cents = "0" + cents;
			for(var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
				num = num.substring(0, num.length-(4*i+3)) + ',' + num.substring(num.length - (4*i+3)); 
			return (((sign) ? '' : '-')) + num + '.' + cents;
		}
		
		//检查长度
		function checkEmpty(id, name) {
			var value = $("#"+id).val();
			if(value.length == 0) {
				$("#"+id)[0].foces();
				return false;
			}
			return true;
		}
		
		
		$(function(){
			//评论区信息的隐藏和显示
			$("a.productDetailTopReviewLink").click(function(){
				$("div.productReviewDiv").show();
				$("div.productDetailDiv").hide();
			});
			$("a.productReviewTopPartSelectedLink").click(function(){
				$("div.productReviewDiv").hide();
				$("div.productDetailDiv").show();
			});
			
			//图片的提示信息
			$("span.leaveMessageTextareaSpan").hide();
			$("img.leaveMessageImg").click(function(){
				$(this).hide();
				$("span.leaveMessageTextareaSpan").show();
				$("div.orderItemSumDiv").css("height", "100px");
			})
			
			$("div#footer a[href$=#nowhere]").click(function(){
				alert("模仿天猫的链接，并没跳到实际的页面");
			})
			$("a.wangwanglink").click(function(){
				alert("模仿旺旺的图标，并不会打开旺旺");
			})
			$("a.notImplementLink").click(function(){
				alert("这个功能还没有做");
			})
		})
		</script>
	</head>
</html>