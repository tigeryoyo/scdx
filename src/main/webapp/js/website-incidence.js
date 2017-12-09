$(".incidence_provience").change(function(){
	console.log("incidence onChange");
	switchCityByPro($(".incidence_provience").val());
})


function switchCityByPro(provience) {
	var citys;
	switch ($.trim(provience)) {
	case "全国":
		citys = ["全国"];
		break;
	case "北京市":
		citys = ["北京市"];
		break;
	case "天津市":
		citys = ["天津市"];
		break;
	case "上海市":
		citys = ["上海市"];
		break;
	case "重庆市":
		citys = ["重庆市"];
		break;
	case "河北省":
		citys = ["石家庄市"];
		break;
	case "山西省":
		citys = ["太原市"];
		break;
	case "辽宁省":
		citys = ["沈阳市"];
		break;
	case "吉林省":
		citys = ["长春市"];
		break;
	case "黑龙江省":
		citys = ["哈尔滨市"];
		break;
	case "江苏省":
		citys = ["南京市"];
		break;
	case "浙江省":
		citys = ["杭州市"];
		break;
	case "安徽省":
		citys = ["合肥市"];
		break;
	case "福建省":
		citys = ["福州市"];
		break;
	case "江西省":
		citys = ["南昌市"];
		break;
	case "山东省":
		citys = ["济南市"];
		break;
	case "河南省":
		citys = ["郑州市"];
		break;
	case "湖北省":
		citys = ["武汉市"];
		break;
	case "湖南省":
		citys = ["长沙市"];
		break;
	case "广东省":
		citys = ["广州市"];
		break;
	case "海南省":
		citys = ["海口市"];
		break;
	case "四川省":
		citys = ["成都市",
		"自贡市",
		"攀枝花市",
		"泸州市",
		"德阳市",
		"绵阳市",
		"广元市",
		"遂宁市",
		"内江市",
		"乐山市",
		"南充市",
		"宜宾市",
		"广安市",
		"达州市",
		"眉山市",
		"雅安市",
		"巴中市",
		"资阳市",
		"阿坝州",
		"甘孜州",
		"凉山州"];
		break;
	case "贵州省":
		citys = ["贵阳市"];
		break;
	case "云南省":
		citys = ["昆明市"];
		break;
	case "陕西省":
		citys = ["西安市"];
		break;
	case "甘肃省":
		citys = ["兰州市"];
		break;
	case "青海省":
		citys = ["西宁市"];
		break;
	case "台湾省":
		citys = ["台北市"];
		break;
	case "内蒙古自治区":
		citys = ["呼和浩特市"];
		break;
	case "广西壮族自治区":
		citys = ["南宁市"];
		break;
	case "西藏自治区":
		citys = ["拉萨市"];
		break;
	case "宁夏回族自治区":
		citys = ["银川市"];
		break;
	case "新疆维吾尔自治区":
		citys = ["乌鲁木齐市"];
		break;
	case "香港":
		citys = ["香港"];
		break;
	case "澳门":
		citys = ["澳门"];
		break;
	default:
		citys = ["其他"];
		break;
	}
	appendCity(citys);
}
function appendCity(citys){
	$(".incidence_city").empty();
	$.each(citys,function(index,val){
		$(".incidence_city").append("<option>"+$.trim(val)+"</option>");
	})
}