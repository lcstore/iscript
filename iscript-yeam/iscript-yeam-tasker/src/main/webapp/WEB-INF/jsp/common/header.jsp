<%@ page language="java" contentType="text/html; charsset=UTF-8" pageEncoding="UTF-8"%>
<%@ include file="tags.jsp"%>
<div class="top-bar">
	<div class="navbar">
		<div class="navbar-inner">
			<div class="container clearfix">
				<a class="brand" href="./index.html">智慧旅行@云数据</a>
				<div class="pull-right userInfor">
					<span><i>${login_user.cname}</i>， ${login_user.corp.cname}</span> <a href="${contextPath}/logout"><i class="icon-off icon-white"></i> 退出</a>
					<%-- <a href="${contextPath}/druid"  target="_blank"><i class="icon-off icon-white"></i> SQL监控</a>
					<a href="${contextPath}/perf4j"  target="_blank"><i class="icon-off icon-white"></i> 业务性能监控</a>
					<a href="${contextPath}/manager"  target="_blank"><i class="icon-off icon-white"></i> 后台管理</a> --%>
				</div>
			</div>
		</div>
	</div>
</div>

<div class="top-menu">
	<div class="navbar navbar-inverse">
		<div class="navbar-inner">
			<div class="container">
				<div class="nav-collapse nav-inverse-collapse" id="header-menu" >
					<ul class="nav mainMenu">
						<li id="index-page" class="${main} " ><a href="${contextPath}/main"><i class="iconA"></i> 首 页</a></li>
						<li id="center-page" class="${subscribe}" ><a href="${contextPath}/main/chart/subscribe/hgyw/scdw?reportCode=report-hgscdw&touchType=1"><i class="iconD"></i> 订阅中心</a></li>
						<%-- <li id="mychart-page" class="${attention}" ><a href="${contextPath}/main/chart/subscribe/hgyw/scdw?reportCode=report-hgscdw&touchType=0"><i class="iconB"></i> 我的订阅</a></li> --%>
					</ul>
					<ul class="nav pull-right" style="">
						<%-- <li  class="${dygl}" id="mySubscribe-li" ><a href="${contextPath}/main/subscribe/center">订阅管理 <span class="nab">${subscribeCount }</span></a></li>
						<li  class="${wdsc }" id="myFavorite-li" ><a href="${contextPath}/main/center/mydefine">我的收藏 <span class="nab" >${favoriteCount }</span></a></li> --%>
					</ul>
				</div>
			</div>
		</div>
	</div>
	<div style="position:absolute;border: 1px solid #000;display:none;background-color: white;min-width: 80px;min-height: 30px;padding: 5px;" id="show-log">
sss
	</div>
</div>

<script src="${contextPath}/resources/main/js/jquery-1.10.1.min.js"></script>
<script type="text/javascript">
;$(function($){
	$("#myFavorite-li").hover(function(){
		var count = $("#myFavorite-li").find('span').text();
		var left = $(this).offset().left;
		var top = $(this).offset().top;
		var width = $(this).width();
		if(0==count){
			$("#show-log").html('您还没有任何收藏，将自己感兴趣的报表加入收藏方便您以后查阅哦');
		}else {
			$("#show-log").html('您当前收藏了<br/><font color="red">'+$("#myFavorite-li").find('span').text()+'</font>张报表');
		}
		$("#show-log").css('top',top+30).css('left',left+20).width(width).show();
	},function(){
		$("#show-log").hide();
	});
	$("#mySubscribe-li").hover(function(){
		var count = $("#mySubscribe-li").find('span').text();
		var left = $(this).offset().left;
		var top = $(this).offset().top;
		var width = $(this).width();
		if(count==0){
			$("#show-log").html('您没有任何需要结算的报表，快去选购喜爱的报表吧');
		}else{
			$("#show-log").html('您当前有<br/><font color="red">'+$("#mySubscribe-li").find('span').text()+'</font>项商品待结算');
		}
		$("#show-log").css('top',top+30).css('left',left+20).width(width).show();
		
	},function(){
		$("#show-log").hide();
	});
});
</script> 