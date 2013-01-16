/**
 * 
 */
package net.oschina.demo;

import java.io.IOException;
import java.util.Arrays;

import net.oschina.common.search.IndexHolder;

/**
 * 测试索引过程
 * @author Winter Lau
 */
public class LuceneTester {

	/**
	 * 测试添加索引
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		IndexHolder holder = IndexHolder.init("D:\\TEST");
		holder.add(Arrays.asList(
				new Post(1, "hello world","你好世界"),
				new Post(2, "hello oschina","你好开源中国"),
				new Post(3, "hello open source","你好开源"),
				new Post(4, "hello china","你好中国")
		));

	}

}
