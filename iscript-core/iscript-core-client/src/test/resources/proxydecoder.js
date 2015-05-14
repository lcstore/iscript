var src =args.source;  var oData = eval('(' + src + ')');
if(oData.log){
	//test.chrome har data
	src = oData.log.entries[0].response.content.text; oData = eval('(' + src + ')');
}
src = oData.table;
src = "<table>" + src + "</table>";
var doc = org.jsoup.Jsoup.parse(src);

var sDestSrc = '';
var trEls = doc.select("tr[rel~=[0-9]+]");
trEls.select("[style=display:none]").remove();
for (var i=0;i<trEls.size();i++) {
	var trEl  = trEls.get(i);
	var styleEle = trEl.select("style");
	var lineArr = styleEle.html().split("\n");
	if(lineArr == null){
		continue;
	}
	for (var j=0;j<lineArr.length;j++) {
		var token = lineArr[j];
		if (token.indexOf("display:none")>0) {
			var index = token.indexOf("{");
			if (index > 0) {
				var clsName = token.substring(0, index).trim();
				trEl.select(clsName).remove();
			}
		}
	}
	sDestSrc +=trEl.text()+'\n';
}
//java.lang.System.err.println(sDestSrc);
return sDestSrc;