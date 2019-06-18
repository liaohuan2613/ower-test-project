package com.lhk.test;

import market.text.clf.MarketTextClfApi;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MarketTextClfApiTest {

    @Test
    public void marketPredict() throws IOException {
        String s1 = "上 互动 AGUH  ST正源 新兴 博 源 已 通过 入伙 芜湖 华融 新 嘉 投资 合 伙企业 投资 入股 北京 太和 妇产医院 SQCONTENTST正源 600321 AGUSTK  2017 年 年 报 说 大健康 是 公司 未来战略 发展 第一 核心业务 公司 争取 做 卓越 医疗服务 供应 商 实施 赋能";
        String s2 = "加拿大 QUANQIU  洋垃圾 运 离 菲律宾 运输 费用 由 加 方 承担 SQCONTENT 根据 菲律宾 媒体 消息 月 31 日 上午 点 左右 巴伐利亚 号 BAVARIA 集装箱船 驶离 苏比克 国际 码头 这 艘 船 搭载 69 箱 来自 加拿大 QUANQIU  洋垃圾 巴伐利亚 号 将 经 停 台湾 高雄 预计 将 于 月 22 日 抵达 加拿大 QUANQIU  温哥华 加 方 将 对 这 批 垃圾 进行 焚毁 处理 巴伐利亚 号 是 于 30 日 下午 抵达 菲律宾 根据 苏比克湾 管理局 SUBIC GZQH  BAY METROPOLITAN AUTHORITY 消息 该 船 在 31 日 凌晨 点 左右 完成 装货 这 批 洋垃圾 共计 69 箱 总重量 超过 2000吨 运输 费用 将 由 加拿大 QUANQIU  承担 其中 加拿大 QUANQIU  政 府 将 负担 一千万 菲律宾比索 WAIHUI  约合 人民币 130万元 2013 年 到 20。批 垃圾 月底 菲律宾 总统 杜特 尔特 再次 敦促 加拿大 QUANQIU  尽快 将 垃圾 运回 并 将 月 15 日 定为 最后 期限 但 加拿大 QUANQIU 政府 一直 以 走 法 律 程序 比较 费时 为由 没有 采取 任何 实质 行动 月 16 日 菲律宾 宣布 召回 驻 加拿大 QUANQIU  外交使节 双方 垃圾 纠纷 加剧 月 20 日 菲律宾 文官 长 萨尔瓦多 梅 地亚 尔 蒂 SALVADOR MEDIALDEA 命令 菲律宾 各部 部长 、 机构 负责人 、 国有控股公司 和政 府 金融机构 负责人 不再 签发 前往 加拿大 QUANQIU  旅行 许可 同时 要求 政府 官员 减少 与 加拿大 QUANQIU 政府 官方 互动 在 重重 压力 下 加拿大 QUANQIU 政府 终于 加快进度 同意 尽快 将 垃圾 运回 并 承担 相应 费用 责任编辑 DF 120";
        List<String> textList = new ArrayList<>();
        textList.add(s1);
        textList.add(s2);
        MarketTextClfApi marketTextClfApi = new MarketTextClfApi("c:/tmp/market/");
        List<Map<String, Double>> predResult = marketTextClfApi.predict(textList);
        predResult.forEach(System.out::println);
    }

}
