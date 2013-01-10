package net.oschina.demo;

import net.oschina.search.SearchHelper;

/**
 * 测试 IK 分词器
 * User: Winter Lau
 * Date: 13-1-10
 * Time: 上午11:48
 */
public class IKTester {

    public static void main(String[] args) {
        test_split();
    }

    protected static void test_highlight() {
        String text = "Tomcat 是最好的 Java 应用服务器";
        System.out.println("RESULT:" + SearchHelper.highlight(text, "Tomcat"));
    }

    protected static void test_split(){
        String text = "开源中国 www.oschina.net 成立于2008年8月，是目前中国最大的开源技术社区。我们传播开源的理念，推广开源项目，为 IT 开发者提供了一个发现、使用、并交流开源技术的平台。";
        long ct = System.currentTimeMillis();
        for(String word : SearchHelper.splitKeywords(text)){
            System.out.println(word);
        }
        System.out.printf("TIME %d\n", (System.currentTimeMillis() - ct));
    }

}
