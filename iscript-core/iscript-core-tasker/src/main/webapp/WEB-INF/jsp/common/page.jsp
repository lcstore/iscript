<%@ page trimDirectiveWhitespaces="true" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/view/common/tags.jsp"%>
<c:set var="_page" value="${empty page ? requestScope[param.pageName] : page}" />
<c:choose>
	<c:when test="${empty param.formName}">
		<c:set var="formName" value="${empty param.formNo ? 0 : param.formNo}" />
	</c:when>
	<c:otherwise>
		<c:set var="formName" value="'${param.formName}'" />
	</c:otherwise>
</c:choose>
<script type="text/javascript">
	function subPage(pageid) {
	<c:choose>
	<c:when test="${empty param.formURL}">
		var url = document.forms[${formName}].action;
	</c:when>
	<c:otherwise>
		var url = "${param.formURL}";
	</c:otherwise>
	</c:choose>
		if(document.forms[${formName}]['currentPage']) {
			document.forms[${formName}]['currentPage'].value = pageid;
		}else{
			var i = document.createElement('input');
			i.type = 'hidden';
			i.name = 'currentPage';
			i.value = pageid;
			document.forms[${formName}].appendChild(i);
		}
		document.forms[${formName}].action = url;
		document.forms[${formName}].submit();
	}

	function inputpage(){
		var pageid = document.getElementById('pageidInput').value;
		if(pageid.isUInteger()){
			subPage(pageid);
		}
	}
	
	/**
	 * 判断字符串是否为正整数
	 * 
	 * @return {}
	 */
	String.prototype.isUInteger = function() {
		var regx = /^\d+$/;
		return regx.test(this);
	}
</script>
<div class="page">
	共<span class="num">${_page.totalRow}</span>条记录 平均<span class="num">${_page.pageSize}</span>条/页
	当前第<span class="hover"><strong>${_page.currentPage}</strong></span>/<strong>${_page.totalPage}</strong>页  
	<c:if test="${_page.hasPreviousPage}">
		<a href="javascript:subPage('1')">首页</a>
		<a href="javascript:subPage('${_page.previousPage}')">上一页</a>
	</c:if>
	<c:forEach items="${_page.pageIndex}" var="pi" varStatus="pagevs">
		<c:if test="${pi eq _page.currentPage}">${pi}&nbsp;</c:if> 
		<c:if test="${pi ne _page.currentPage}"><a href="javascript:subPage('${pi}')">${pi}</a> </c:if>
	</c:forEach>
	<c:if test="${_page.hasNextPage}">
	<a href="javascript:subPage('${_page.nextPage}')">下一页</a>
	<a href="javascript:subPage('${_page.totalPage}')">末页</a>
	</c:if>
	前往第 <input type="text" class="input-mini" style="width: 40px;" id="pageidInput" onkeydown="if(event.keyCode == 13){inputpage();return false;}" size="3" /> 页&nbsp; <input type="button" value="go" class="go btn btn-info btn-small" onclick="inputpage();return false;"/>
</div>