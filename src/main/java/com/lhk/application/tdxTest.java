package com.lhk.application;

import com.alibaba.fastjson.JSONObject;
import com.thinkive.base.web.access.client.TKRequestModel;

import java.util.HashMap;
import java.util.Map;

public class tdxTest {

    public static String webUrl = "https://ic-test.mszq.com:8001/ytgpc/servlet/access";
    public static String companyId = "THINKIVE";
    public static String systemId = "SJ1";
    public static int funcNo = 1200734;

    public static void main(String[] args) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("invest_id", "27");
        parameters.put("invest_name", "投顾名称");
        parameters.put("viewpage_name", "财联社早知道");
        parameters.put("introdution", "是不是每到夜晚就文思泉涌，不做复盘、不看股票就彻夜难眠？如果你也是这样的“暗夜股侠”，恭喜你！财联社最新推出《财联社早知道》栏目，每个交易日前一晚21：30准时推送，复盘今日热点，前瞻明日机会，更有主力资金动向分析，一文汇总你关心的所有信息，帮助每一位“股侠”精神饱满征战A股！\n" +
                "\n再次提醒，《财联社早知道》为每个交易日前一晚21：30推送，一般为周日、周一、周二、周三和周四，一共五天，不见不散！");
        // 287， 290，

//        parameters.put("viewpage_id", "285");
//        parameters.put("view_title", "财联社3月18日讯，波音美股盘前跌近3%，媒体报道称，美国交通部正针对");
//        parameters.put("view_abstracts", "财联社3月18日讯，波音美股盘前跌近3%，媒体报道称，美国交通部正针对联邦航空局为波音737MAX出具批文一事展开调查。");
//        parameters.put("private_content", "财联社3月18日讯，波音美股盘前跌近3%，媒体报道称，美国交通部正针对联邦航空局为波音737MAX出具批文一事展开调查。");
//        parameters.put("author", "财联社");
//        parameters.put("is_pay", "0");
//        parameters.put("price", "0");
//        invest_id	String	投顾员工编号
//        viewpage_id	String	观点包ID
//        view_title	String	观点名称
//        view_abstracts	String	摘要
//        view_content	String	公开内容
//        private_content	String	私密内容
//        author	String	作者
//        is_pay	String	是否付费0:否;1:是
//        price	String	观点价格


        System.out.println("执行功能号:funcNo:" + funcNo);
        System.out.println("执行入参:" + JSONObject.toJSON(parameters));
        JSONObject result = TKRequestModel.builder().companyId(companyId).systemId(systemId)
                .connectTimeout(6000).socketTimeout(6000).url(webUrl)
                .funcNo(funcNo).parameters(parameters).build().invoke();
        System.out.println("执行出参:" + JSONObject.toJSON(result));
    }
}
