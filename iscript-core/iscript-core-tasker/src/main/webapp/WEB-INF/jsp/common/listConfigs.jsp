<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ include file="common/tags.jsp"%>
<!DOCTYPE HTML>
<html>
<head>
<meta charset="utf-8">
<link rel="stylesheet"
	href="${contextPath}/resources/bootstrap/css/bootstrap.css" />
<title>配置管理</title>


<script src="${contextPath}/resources/jquery/jquery-1.11.0.min.js"></script>

<script type="text/javascript">
	function deleteConfig(node, type) {
		var sUrl = "${contextPath}/configmgr/deleteConfig.action";
		$.post(sUrl, {
			'type' : type
		}, function(result) {
			if (result && result == 'OK') {
				var trNode = node.parentNode.parentNode;
				if (trNode && trNode.parentNode) {
					trNode.parentNode.removeChild(trNode);
				}
			}
		});
	}
	function editConfig(node, type,destType) {
		$("input[name=configType]").val(type);
		$('input[type=radio][name=destType]').get(destType).checked = true; 
		switchPopLayer();
	}
	function switchPopLayer() {
		var clsName = $("#popLayer").attr('class');
		var sMark = "display";
		if (clsName.indexOf(sMark) > 0) {
			clsName = clsName.replace(sMark, '');
		} else {
			clsName += ' ' + sMark;
		}
		$("#popLayer").attr('class', clsName);
	}
</script>
<script type="text/javascript">
	$(document).ready(function() {
		$("#btnConfirm").click(function(e) {
			return true;
		});
		$("#btnCancle").click(function(e) {
			switchPopLayer();
		});
		$("#inputBox").change(function(e) {
			var _this = $(this);
			var sName = _this.val();
			var index = sName.lastIndexOf('.');
			sName = index < 0 ? sName : sName.substring(0, index);
			var sNewName = sName.toLowerCase();
			var sMark = 'strategy';
			index = sNewName.indexOf(sMark);
			if(index > 0 && (index+sMark.length==sNewName.length)){
			  $('input[type=radio][name=destType]').get(1).checked = true; 
			}else {
			  $('input[type=radio][name=destType]').get(0).checked = true; 
			}
			$('#inputName').val(sName);
		});
	});
</script>
<style type="text/css">
th {
	background: none repeat scroll 0 0 #60C8F2;
	color: #003399;
	font-size: 13px;
	font-weight: normal;
	padding: 8px;
}

.header {
	background: url("${contextPath}/resources/images/left.jpg") no-repeat
		scroll left top #60C8F2;
	width: 26px;
}

}
.tail {
	background: url("${contextPath}/resources/images/right.jpg") no-repeat
		scroll right top #60C8F2;
	width: 26px;
}

img.updateLoad {
	max-width: 18px;
}

.popover {
	background-clip: padding-box;
	background-color: #FFFFFF;
	border-radius: 0 0 3px 3px;
	padding: 14px;
}

.display {
	display: block;
}

.popinput {
	width: 80%;
}

.arrow {
	border-bottom: 5px solid rgba(0, 0, 0, 0);
	border-right: 5px solid #000000;
	border-top: 5px solid rgba(0, 0, 0, 0);
	left: 0;
	margin-top: -5px;
	top: 50%;
}
</style>
</head>
<body>

	<div class="configList">
		<h3>配置管理 - 配置列表</h3>

		<table id="rounded-corner" summary="2007 Major IT Companies' Profit">
			<thead>
				<tr>
					<th scope="col" class="header">编号</th>
					<th scope="col" class="rounded">配置类型</th>
					<th scope="col" class="rounded">配置来源</th>
					<th scope="col" class="rounded">状态</th>
					<th scope="col" class="rounded">更新时间</th>
					<th scope="col" class="rounded">更新配置</th>
					<th scope="col" class="tail">删除配置</th>
				</tr>
			</thead>
			<tfoot>
				<tr>
					<td colspan="7" class="rounded-foot-left"><em>目前有${siteCount}个站点，共${configSize}个配置。
					</em> 添加配置<a href="javascript:void(0)" onclick="switchPopLayer()"
						class="ask"><img class="updateLoad"
							src="${contextPath}/resources/images/addico.png" alt="上传配置"
							title="上传配置"></a></td>

				</tr>
			</tfoot>
			<tbody>
				<c:forEach items="${configList}" var="configDto" varStatus="status">
					<tr>
						<td>(<c:out value="${ status.index + 1}"></c:out>)
						</td>
						<td><c:out value="${configDto.type}"></c:out></td>
						<td>${configDto.source}</td>
						<td><c:if test="${configDto.status==1}">
							可用
						</c:if> <c:if test="${configDto.status==0}">
							禁用
						</c:if></td>
						<td><fmt:formatDate value="${configDto.updateTime}"
								pattern="yyyy-MM-dd HH:mm:ss" /></td>
						<td><a href="javascript:void(0)"
							onclick="editConfig(this,'${configDto.type}','${configDto.destType}')"><img
								src="${contextPath}/resources/images/user_edit.png" alt="更新"
								title="更新" border="0"></a></td>
						<td><a href="javascript:void(0)"
							onclick="deleteConfig(this,'${configDto.type}')" class="ask"><img
								src="${contextPath}/resources/images/trash.png" alt="删除"
								title="删除" border="0"></a></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>
	<div class="popover fade right in" id="popLayer">
		<div class="arrow"></div>
		<div class="popover-inner">
			<h3 class="popover-title">配置上传</h3>
			<div class="popover-content">
				<p>
				<form action="${contextPath}/configmgr/addConfig.action"
					method="POST" enctype="multipart/form-data">
					<fieldset>
						<div class="control-group">
							<label class="control-label" for="input01">名称</label>
							<div class="controls">
								<input class="popinput" id="inputName"
									placeholder="配置名称，如tmall-product" type="text" name="configType">
								<p class="help-block"></p>
								<div>
									<div>
										<span><input type="radio" name="destType" value="0" checked>抓取配置</span>
										 <span><input type="radio" name="destType" value="1">处理策略</span>
									</div>
								</div>
							</div>
						</div>
						<div class="control-group">
							<label class="control-label">上传配置</label>
							<div class="controls">
								<input id="inputBox" class="popinput" name="configFile"
									type="file" accept="text/xml,application/xml" width="80%">
							</div>
						</div>
						<div class="controls">
							<input class="btn btn-info" type="submit" value="保存"
								id="btnConfirm"> <input class="btn btn-danger"
								type="button" value="取消" id="btnCancle">
						</div>
					</fieldset>
				</form>

			</div>
		</div>
	</div>
</body>
</html>