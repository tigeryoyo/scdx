function provienceChange(element){
	var citys;
	switch ($.trim($(element).val())) {
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
		citys = ["所有","石家庄市"];
		break;
	case "山西省":
		citys = ["所有","太原市"];
		break;
	case "辽宁省":
		citys = ["所有","沈阳市"];
		break;
	case "吉林省":
		citys = ["所有","长春市"];
		break;
	case "黑龙江省":
		citys = ["所有","哈尔滨市"];
		break;
	case "江苏省":
		citys = ["所有","南京市"];
		break;
	case "浙江省":
		citys = ["所有","杭州市"];
		break;
	case "安徽省":
		citys = ["所有","合肥市"];
		break;
	case "福建省":
		citys = ["所有","福州市"];
		break;
	case "江西省":
		citys = ["所有","南昌市"];
		break;
	case "山东省":
		citys = ["所有","济南市"];
		break;
	case "河南省":
		citys = ["所有","郑州市"];
		break;
	case "湖北省":
		citys = ["所有","武汉市"];
		break;
	case "湖南省":
		citys = ["所有","长沙市"];
		break;
	case "广东省":
		citys = ["所有","广州市"];
		break;
	case "海南省":
		citys = ["所有","海口市"];
		break;
	case "四川省":
		citys = ["所有",
		"成都市",
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
		citys = ["所有","贵阳市"];
		break;
	case "云南省":
		citys = ["所有","昆明市"];
		break;
	case "陕西省":
		citys = ["所有","西安市"];
		break;
	case "甘肃省":
		citys = ["所有","兰州市"];
		break;
	case "青海省":
		citys = ["所有","西宁市"];
		break;
	case "台湾省":
		citys = ["所有","台北市"];
		break;
	case "内蒙古自治区":
		citys = ["所有","呼和浩特市"];
		break;
	case "广西壮族自治区":
		citys = ["所有","南宁市"];
		break;
	case "西藏自治区":
		citys = ["所有","拉萨市"];
		break;
	case "宁夏回族自治区":
		citys = ["所有","银川市"];
		break;
	case "新疆维吾尔自治区":
		citys = ["所有","乌鲁木齐市"];
		break;
	case "香港":
		citys = ["香港"];
		break;
	case "澳门":
		citys = ["澳门"];
		break;
	default:
		citys = [""];
		break;
	}
	
	var city = $(element).siblings(".incidence_city");
	city.empty();
	city.append('<option value="" disabled selected>请选择</option>');
	$.each(citys,function(index,val){
		city.append("<option>"+$.trim(val)+"</option>");
	})
}


/*$(".incidence_provience").change(function(){
	switchCityByPro($(".incidence_provience").val());
})

*/
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
		citys = ["所有","石家庄市"];
		break;
	case "山西省":
		citys = ["所有","太原市"];
		break;
	case "辽宁省":
		citys = ["所有","沈阳市"];
		break;
	case "吉林省":
		citys = ["所有","长春市"];
		break;
	case "黑龙江省":
		citys = ["所有","哈尔滨市"];
		break;
	case "江苏省":
		citys = ["所有","南京市"];
		break;
	case "浙江省":
		citys = ["所有","杭州市"];
		break;
	case "安徽省":
		citys = ["所有","合肥市"];
		break;
	case "福建省":
		citys = ["所有","福州市"];
		break;
	case "江西省":
		citys = ["所有","南昌市"];
		break;
	case "山东省":
		citys = ["所有","济南市"];
		break;
	case "河南省":
		citys = ["所有","郑州市"];
		break;
	case "湖北省":
		citys = ["所有","武汉市"];
		break;
	case "湖南省":
		citys = ["所有","长沙市"];
		break;
	case "广东省":
		citys = ["所有","广州市"];
		break;
	case "海南省":
		citys = ["所有","海口市"];
		break;
	case "四川省":
		citys = ["所有",
		"成都市",
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
		citys = ["所有","贵阳市"];
		break;
	case "云南省":
		citys = ["所有","昆明市"];
		break;
	case "陕西省":
		citys = ["所有","西安市"];
		break;
	case "甘肃省":
		citys = ["所有","兰州市"];
		break;
	case "青海省":
		citys = ["所有","西宁市"];
		break;
	case "台湾省":
		citys = ["所有","台北市"];
		break;
	case "内蒙古自治区":
		citys = ["所有","呼和浩特市"];
		break;
	case "广西壮族自治区":
		citys = ["所有","南宁市"];
		break;
	case "西藏自治区":
		citys = ["所有","拉萨市"];
		break;
	case "宁夏回族自治区":
		citys = ["所有","银川市"];
		break;
	case "新疆维吾尔自治区":
		citys = ["所有","乌鲁木齐市"];
		break;
	case "香港":
		citys = ["香港"];
		break;
	case "澳门":
		citys = ["澳门"];
		break;
	default:
		citys = [""];
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