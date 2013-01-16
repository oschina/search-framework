package net.oschina.search;

import java.util.List;
import java.util.Map;

/**
 * 可搜索对象
 * User: Winter Lau
 * Date: 13-1-10
 * Time: 上午11:31
 */
public interface Searchable extends Comparable<Searchable> {

    /**
     * 文档的唯一编号
     * @return 文档id
     */
    public long id();
    public void setId(long id);

    /**
     * 要存储的字段
     * @return 返回字段名列表
     */
    public List<String> storeFields();

    /**
     * 要进行分词索引的字段
     * @return 返回字段名列表
     */
    public List<String> indexFields();

    /**
     * 扩展的存储数据
     * @return 扩展数据K/V
     */
    public Map<String, String> extendStoreDatas();

    /**
     * 扩展的索引数据
     * @return 扩展数据K/V
     */
    public Map<String, String> extendIndexDatas();

    /**
     * 列出id值大于指定值得所有对象
     * @param id
     * @param count
     * @return
     */
	public List<? extends Searchable> ListAfter(long id, int count) ;
}
