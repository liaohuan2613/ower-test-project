package com.lhk.application;

import com.alibaba.fastjson.JSONObject;
import com.thinkive.base.web.access.client.TKRequestModel;

import java.util.HashMap;
import java.util.Map;

public class tdxTest {

    public static String webUrl = "https://ic-test.mszq.com:8001/ytgpc/servlet/access";
    public static String companyId = "THINKIVE";
    public static String systemId = "SJ1";

    public static void main(String[] args) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("invest_id", "27");
        parameters.put("invest_name", "投顾名称");
        parameters.put("viewpage_name", "财联社早知道");
        parameters.put("introdution", "是不是每到夜晚就文思泉涌，不做复盘、不看股票就彻夜难眠？如果你也是这样的“暗夜股侠”，恭喜你！财联社最新推出《财联社早知道》栏目，每个交易日前一晚21：30准时推送，复盘今日热点，前瞻明日机会，更有主力资金动向分析，一文汇总你关心的所有信息，帮助每一位“股侠”精神饱满征战A股！\n" +
                "\n再次提醒，《财联社早知道》为每个交易日前一晚21：30推送，一般为周日、周一、周二、周三和周四，一共五天，不见不散！");
        System.out.println("执行功能号:funcNo:" + 1200734);
        System.out.println("执行入参:" + JSONObject.toJSON(parameters));
        JSONObject result = TKRequestModel.builder().companyId(companyId).systemId(systemId)
                .connectTimeout(6000).socketTimeout(6000).url(webUrl)
                .funcNo(1200734).parameters(parameters).build().invoke();
        System.out.println("执行出参:" + JSONObject.toJSON(result));
    }

    private static void createMsg() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("invest_id", "27");
        parameters.put("viewpage_id", "290");
        parameters.put("view_title", "【财联社早知道】美股最大市值的超级巨头入局，这项“未来”技术又被推至风口浪尖");
        parameters.put("view_abstracts", "①美股最大市值的超级巨头入局，这项“未来”技术又被推至风口浪尖；\n" +
                "②涨跌不是关键，量能才是重点，指数放量后一个“新”板块乘势“突击”；\n" +
                "③阿里获得“新”自贸区建设“邀请函”？改革向纵深推进后，红利将率先砸中哪些企业？");
        parameters.put("private_content", "<p>汇金科技二连板，拉卡拉二连板，全天普涨的行情里，只有涨停才能凸显自己的投资能力。作为财联社“最能抓涨停”的人气栏目，《财联社早知道》每晚21：30都将准时梳理今日要闻，前瞻明日热点，帮助大家梳理潜在“涨停”机会！</p>\n" +
                "<p><strong>【大头条】</strong></p>\n" +
                "<p><strong>美股最大市值的超级巨头入局，这项“未来”技术又被推至风口浪尖</strong></p>\n" +
                "<p>微软今天宣布将加入Hyperledger社区，参与开源区块链技术项目。微软是最近一家小心涉足并严肃对待区块链服务的大型科技公司。今年五月份微软推出全托管的AzureBlockchain服务，并在2018年推出区块链工作台和开发人员工具包。近日，Facebook也为WhatsApp和Facebook Messenger推出加密货币Libra和电子钱包Calibra，并推出一款用于其他商务的独立智能手机应用。</p>\n" +
                "<p>6、特朗普正式宣布竞选连任。</p>");
        parameters.put("author", "财联社");
        parameters.put("is_pay", "0");
        parameters.put("price", "28");
        System.out.println("执行功能号:funcNo:" + 1200735);
        System.out.println("执行入参:" + JSONObject.toJSON(parameters));
        JSONObject result = TKRequestModel.builder().companyId(companyId).systemId(systemId)
                .connectTimeout(6000).socketTimeout(6000).url(webUrl)
                .funcNo(1200735).parameters(parameters).build().invoke();
        System.out.println("执行出参:" + JSONObject.toJSON(result));
    }

    private static void updateMsg() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("user_id", "27");
        parameters.put("view_id", "2594");
        parameters.put("view_title", "【财联社早知道】美股最大市值的超级巨头入局，这项“未来”技术又被推至风口浪尖");
        parameters.put("view_abstracts", "①美股最大市值的超级巨头入局，这项“未来”技术又被推至风口浪尖；\n" +
                "②涨跌不是关键，量能才是重点，指数放量后一个“新”板块乘势“突击”；\n" +
                "③阿里获得“新”自贸区建设“邀请函”？改革向纵深推进后，红利将率先砸中哪些企业？");
        parameters.put("private_content", "<p>汇金科技二连板，拉卡拉二连板，全天普涨的行情里，只有涨停才能凸显自己的投资能力。作为财联社“最能抓涨停”的人气栏目，《财联社早知道》每晚21：30都将准时梳理今日要闻，前瞻明日热点，帮助大家梳理潜在“涨停”机会！</p>\n" +
                "<p><strong>【大头条】</strong></p>\n" +
                "<p><strong>美股最大市值的超级巨头入局，这项“未来”技术又被推至风口浪尖</strong></p>\n" +
                "<p>微软今天宣布将加入Hyperledger社区，参与开源区块链技术项目。微软是最近一家小心涉足并严肃对待区块链服务的大型科技公司。今年五月份微软推出全托管的AzureBlockchain服务，并在2018年推出区块链工作台和开发人员工具包。近日，Facebook也为WhatsApp和Facebook Messenger推出加密货币Libra和电子钱包Calibra，并推出一款用于其他商务的独立智能手机应用。</p>\n" +
                "<p><strong>点评：</strong>有机构分析认为，Libra的愿景展示了Facebook在互联网下半场的野心，社交网络提供了信息分享的平台，而Libra将基于各种场景打造价值交互的平台。此前摩根所发JMP尚局限于金融客户之间使用，而Libra是2C的稳定币，其影响力将更大。国际巨头都在争相布局区块链，以瑞银集团为代表的14家国际银行共投资5000万英镑，成立一家名为Fnality International的合资公司，将推出的加密货币USC。</p>\n" +
                "<p><strong>公司方面，聚龙股份推出了基于区块链技术的人民币流通管理解决方案；精准信息子公司富华宇祺从事基于区块链基本技术代维支付产品解决方案的探索和研究，已中标中国铁塔区块链项目的一个区域内的部分业务；赢时胜已上线保理区块链SAAS系统，未来将陆续上线供应链代采购及票据区块链SAAS系统。</strong></p>\n" +
                "<p><strong>【市场大热点】</strong></p>\n" +
                "<p><img src=\"https://image.cailianpress.com/admin/20190619/205939960EIHTlQiz7.png\" alt=\"image\"></p>\n" +
                "<p>指数今日放量高开低走，由于量能的放大，支撑了多个题材板块盘中活跃。昨天提前走强的个股溢价都很大，次新、游戏、医药等题材龙头股几乎都连板成功。区块链板块近期持续有消息刺激，板块内个股多数又叠加金融属性，部分个股后市依然有高度。自主可控和知识产权都算是反复炒作的老题材，后面只有高度龙头值得关注，跟风股没有太多博弈价值。值得一提的是环保板块，这个板块之前有零星个股表现，今天第一次形成板块效应，作为新题材，也有盛运环保这样的高度股，是开启新周期的一个潜力板块，值得多看一眼。另外，叠加次新和高送填权属性在本周仍然是个股的加分项。</p>\n" +
                "<p><strong>【题材抢先看】</strong></p>\n" +
                "<p><strong>1、阿里获得“新”自贸区建设“邀请函”？改革向纵深推进后，红利将率先砸中哪些企业？</strong></p>\n" +
                "<p>今日下午，义乌市人民政府与阿里巴巴集团签署eWTP（世界电子贸易平台）战略合作协议，eWTP全球创新中心将落户义乌。义乌市委书记林毅在签约仪式上表示，义乌也是跨境电商综合试验区，正在争取自贸区，义乌的自贸区可以和阿里一起干。</p>\n" +
                "<p><strong>点评：</strong>义乌作为金华市辖区内的县级市，若能够成功申请成为自贸区，将会对义乌市内的企业带来巨大的竞争优势，尤其是以出口外贸型企业居多的义乌市。此外，国务院此前也印发通知，向全国或特定地区复制推广第五批18项自贸试验区改革试点经验。这些经验复制推广，将进一步优化营商环境，激发市场活力，提升企业和群众的满意度，推动改革红利共享、开放成效普惠。</p>\n" +
                "<p><strong>A股的上市公司中，小商品城位于金华义乌，公司拥有自己的线上购物平台“义乌购”，开创了国内B2R发展的新模式；华鼎股份2018年的境外营收占比达到41%，子公司通拓科技是一家以电子商务为手段为世界各国终端消费者供应优质商品的跨境电商企业。</strong></p>\n" +
                "<p><strong>2、国常会要求这项任务提前一年完成，这些企业要加班加点了</strong></p>\n" +
                "<p>国务院常务会议指出，新一轮农村电网改造升级工程自2016年实施以来，完成了农村机井通电等任务。下一步，各地和电网企业要加大工作力度，确保今年以省为单位，提前一年完成“十三五”规划明确的全部改造升级任务。重点推进“三区三州”、抵边村寨等农网改造建设攻坚，确保明年上半年完成。建立农网供电监测评价体系，将机井通电纳入电网企业日常服务范围，提高农村电力服务水平。</p>\n" +
                "<p><strong>点评：</strong>国家电网此前提出“三型两网、世界一流”的战略目标，是落实中央部署，发挥央企带头作用的重要举措。建设泛在电力物联网作为国家电网“三型两网、世界一流”战略目标的核心任务，是落实中央“新基建”部署的重要举措。泛在电力物联网基于源网荷储全面感知、融通发展，深入挖掘巨大提质增效价值，可在促进清洁能源消纳、各环节效率提升、精益管理变革等方面发挥重要作用。</p>\n" +
                "<p><strong>财联社资讯数据显示，积成电子主营电网自动化、配用电自动化的软件开发和系统集成；大烨智能主营配电自动化终端、智能中压开关设备、变电站自动化系统等感知层终端类产品；新联电子主营用电信息采集系统的软、硬件研发，产品主要服务于智能电网。</strong></p>\n" +
                "<p><strong>【公告挖掘机】</strong></p>\n" +
                "<p><img src=\"https://image.cailianpress.com/admin/20190619/210041cYP6AxZBNvSr.png\" alt=\"image\"></p>\n" +
                "<p>钱江摩托发布公告，公司与哈雷签署长期合作协议，为中国和亚洲其他市场共同开发两种不同的H-D品牌优质摩托车，在目标摩托车的制造和组装以及供应链优化方面进行合作。点评：哈雷戴维森将是中国市场经销目标摩托车的独家总经销商，并且是合约双方一致同意拓展长期合作的任何其他国家或地区的独家总经销商。公司与哈雷展开战略合作，可以实现优势互补，为双方开拓市场、技术合作升级、服务优化创新提供有力保障，也有利于公司提升影响力。</p>\n" +
                "<p><strong>【主力买什么】</strong></p>\n" +
                "<p><strong>中钢天源：</strong>稀土今日在消息利空下整体呈现较强的走势，龙头中钢天源显示买一银河证券上海镇宁路买入7500万，华鑫证券江苏分公司买入3700万，国泰君安南京太平南路买入3000万，其余资金买入均在2500万上下。为何资金会在利空之下如此大举买入，在证券市场，“硬币有两面”被演绎得淋漓尽致，因为市场总会消化所有的因素，再反应到盘面。既然稀土超预期了，必然意味着消息面所无法反映出的本质，资金看中的不是预期本身，而是超预期的表现。</p>\n" +
                "<p><strong>【新闻连连看】</strong></p>\n" +
                "<p>1、国务院常务会议部署推进城镇老旧小区改造。</p>\n" +
                "<p>2、央行今日进行2400亿元MLF操作。</p>\n" +
                "<p>3、证监会已接收邮储银行IPO材料。</p>\n" +
                "<p>4、国务院知识产权战略实施工作部际联席会议推进知识产权强国计划。</p>\n" +
                "<p>5、科创板第一股华兴源创将于6月27日申购。</p>\n" +
                "<p>6、特朗普正式宣布竞选连任。</p>");
        parameters.put("author", "财联社");
        System.out.println("执行功能号:funcNo:" + 1200737);
        System.out.println("执行入参:" + JSONObject.toJSON(parameters));
        JSONObject result = TKRequestModel.builder().companyId(companyId).systemId(systemId)
                .connectTimeout(6000).socketTimeout(6000).url(webUrl)
                .funcNo(1200737).parameters(parameters).build().invoke();
        System.out.println("执行出参:" + JSONObject.toJSON(result));
    }

}
