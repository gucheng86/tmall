<!-- 1. 左侧显示5张单个图片
	1.1 默认显示第一张图片
	1.2 5张小图片
2. 右边显示基本信息
	2.1 标题和小标题
	2.2 原始价格和促销价
	2.3 销量和累计评价
2.4 库存 -->
<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<script>
//关于商品数量的变化以及加入购物车
$(function(){
	var stock = ${p.stock};
	$(".productNumberSetting").keyup(function(){
		var num = $(".productNumberSetting").val();
		num = parseInt(num);
		if(isNaN(num))
			num = 1;
		if(num<=0)
			num = 1;
		if(num > stock) 
			num=stock;
		$(".productnUmberSetting").val(num);
	});
	
	$(".increaseNumber").click(function(){
		var num = $(".productNumberSetting").val();
		num++;
		if(num > stock) 
			num = stock;
		$(".productNumberSetting").val(num);
	})
	
	$(".decreaseNumber").click(function(){
		var num = $(".productNumberSetting").val();
		--num;
		if(num <= 0)
			num = 1;
		$(".productNumberSetting").val(num);
	});
	
	$(".addCartLink").click(function(){
		//ajax首先检查是否登录
		var page = "forecheckLogin";
		$.get(
				page,
				function(result) {
					if("success"==result) {
						var pid = ${p.id};
						var num = $(".productNumberSetting").val();
						var addCartpage = "foreaddCart";
						//如果登录，九加入购物车
						$.get(
							addCartpage,
							{"pid":pid, "num":num},
							function(result) {
								if("success"==result){
									 $(".addCartButton").html("已加入购物车");
                                     $(".addCartButton").attr("disabled","disabled");
                                     $(".addCartButton").css("background-color","lightgray")
                                     $(".addCartButton").css("border-color","lightgray")
                                     $(".addCartButton").css("color","black")
                              
                                 }
							}
						);
					}
					else{
						$("#loginModal").modal('show');
					}
				}
				);
		return false;
	});
	
	
	$(".buyLink").click(function(){
		var page="forecheckLogin";
		$.get(
			page, 
			function(result) {
				//如果已经登录
				if("success" == result) {
					var num = $(".productNumberSetting").val();
					location.href = $(".buyLink").attr("href") + "&num=" + num;
				}
				//模态登录窗口
				else {
					$("#loginModal").modal('show');
				}
			}
		);
		return false;
	});
	
	//模态窗口的登录按钮
	$("button.loginSubmitButton").click(function(){
		var name = $("#name").val();
		var password = $("#password").val();
		if(0 == name.length || 0 == password.length){
			$("span.errorMessage").html("请输入账号密码");
			$("div.loginErrorMessageDiv").show();
			return false;  
		}
		
		var page = "foreloginAjax";
		$.get(
			page, 
			{"name":name, "password":password},
			function(result){
				if("success" == result) {
					//刷新界面
					location.reload();
				}
				else {
					$("span.errorMessage").html("账号密码错误");
					$("div.loginErrorMessageDiv").show();
				}
			}
		);
		return true;
	})
	
	//鼠标进入小图片时替换大图片
	$("img.smallImage").mouseenter(function(){
		var bigImageURL = $(this).attr("bigImageURL");
		$("img.bigImg").attr("src", bigImageURL);
	})
	
	//图片加载时触发
	$("img.bigImg").load(
		function(){
			//each() 方法规定为每个匹配元素规定运行的函数。
			$("img.smallImage").each(function(){
				var bigImageURL = $(this).attr("bigImageURL");
				img = new Image();
				img.src = bigImageURL;
				
				//图片加载完成
				img.onload = function(){
					console.log(bigImageURL);
					//append() 方法在被选元素的结尾（仍然在内部）插入指定内容。
					$("div.img4load").append($(img));
				}
			})
		}		
	)
	
})
</script>

<div class="imgAndInfo">
	<!-- 一张中图和5张小图 -->
	 <div class="imgInimgAndInfo">
        <img src="img/productSingle/${p.firstProductImage.id}.jpg" class="bigImg">
        <div class="smallImageDiv">
            <c:forEach items="${p.productSingleImages}" var="pi">
                <img src="img/productSingle_small/${pi.id}.jpg" bigImageURL="img/productSingle/${pi.id}.jpg" class="smallImage">
            </c:forEach>
        </div>
        <div class="img4load hidden" ></div>
    </div>
	
	<!-- 产品简介信息 -->
	<div class="infoInimgAndInfo">
		<div class="productTitle">
			${p.name }
		</div>	
		<div class="productSubTitle">
			${p.subTitle }
		</div>
		
		<div class="productPrice">
			<div class="juhuasuan">
				<span class="juhuasuanBig">聚划算</span>
				<span>此商品即将参加聚划算，<span class="juhusuanTime">1天19小时</span></span>
			</div>
			<div class="productPriceDiv">
				<div class="gouwujuanDiv"><img height="16px" src="img/site/gouwujuan.png">
					<span>全天猫实物商品通用</span>
				</div>
				
				<div class="originalDiv">
					<span class="originalPriceDesc">价格</span>
					<span class="originalPriceYuan">￥</span>
					<!-- 价格保留两位小数 -->
					<span class="originalPrice">
						<fmt:formatNumber type="number" value="${p.orignalPrice }" minFractionDigits="2"/>
					</span>
				</div>
				<div class="promotionDiv">
                    <span class="promotionPriceDesc">促销价 </span>
                    <span class="promotionPriceYuan">¥</span>
                    <span class="promotionPrice">
                        <fmt:formatNumber type="number" value="${p.promotePrice}" minFractionDigits="2"/>
                    </span>              
                </div>
			</div>	
		</div>
		
		<!-- 销量和评价 -->
		<div class="productSaleAndReviewNumber">
			<div>销量<span class="redColor boldWord">${p.saleCount}</span></div>
			<div>累计评价<span class="redColor boldWord">${p.reviewCount }</span></div>
		</div>
		<div class="productNumber">
			<span>数量</span>
			<!-- 数量输入框 -->
			<span>
				<span class="productNumberSettingSpan">
					<input class="productNumberSetting" type="text" value="1">
				</span>
				<span class="arrow">
					<a href="#nowhere" class="increaseNumber">
						<span class="updown">
							<img src="img/site/increase.png">
						</span>
					</a>
					
					<span class="updownMiddle"></span>
					<a href="#nowhere" class="decreaseNumber">
						<span class="updown">
							<img src="img/site/decrease.png">
						</span>
					</a>
				</span>
			件</span>
			<span>库存${p.stock }件</span>
		</div>
		<div class="serviceCommitment">
			<span class="serviceComitmentDesc">服务承诺</span>
			<span class="serviceCommitmentLink">
                <a href="#nowhere">正品保证</a>
                <a href="#nowhere">极速退款</a>
                <a href="#nowhere">赠运费险</a>
                <a href="#nowhere">七天无理由退换</a>
            </span>
		</div>
		
		<div class="buyDiv">
			<a class="buyLink" href="forebuyone?pid=${p.id }"><button>立即购买</button></a>
			<a href="#nowhere" class="addCartLink">
				<button class="addCartButton">
					<span class="glyphicon glyphicon-shopping-cart"></span>加入购物车
				</button>
			</a>
		</div>
	</div>
	
	<div style="clear:both"></div>
</div>