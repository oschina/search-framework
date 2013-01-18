package net.oschina.demo;

import net.oschina.common.search.SearchHelper;

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
        String text = "android 刷机";
        long ct = System.currentTimeMillis();
        for(String word : SearchHelper.splitKeywords(text)){
            System.out.println(word);
        }
        System.out.printf("TIME %d\n", (System.currentTimeMillis() - ct));
    }

}
