var data = {};
var comments = [];
var Jsoup = org.jsoup.Jsoup;
var dom = Jsoup.connect(_url).timeout(3000).post();
var id = dom.select("div#short-share div.fl span").eq(1).text();
//java.lang.System.out.println(dom.select("div#short-share div.fl span").get(1).html());
var comment_url = "http://club.jd.com/bbs/"+id+"-1-0-4.html";
//http://club.jd.com/bbs/1073575-1-0-4.html
java.lang.System.out.println("comment_url"+comment_url);
var commentDom = Jsoup.connect(comment_url).timeout(2000).get();
//java.lang.System.out.println(commentDom.html());
var list = commentDom.select("div#Discuss.Discuss table tbody tr");
java.lang.System.out.println("list size"+list.size());
var index = 0;
for(var i=0;i<list.size();i++){
	var comment ={};
	var comment_text = list.get(i).select("td div.s1").eq(0).text()
	java.lang.System.out.println(comment_text);
	if(comment_text!=null&&comment_text!=""){
		comment["comment_text"] = comment_text;
		var time = list.get(i).select("td").get(3).text();
		java.lang.System.out.println(time);
		comment["comment_time"] = getDate(time,"yyyy-MM-dd HH:mm:ss")
		comments[index++] =comment; 
	}
}
data["product_code"] = id;
data["product_url"]=_url;
data["other_type"]="jd";
data["comments"] = comments;
function getDate(str , format){
	var fmt = new java.text.SimpleDateFormat(format);;
	return fmt.parse(str);
 }