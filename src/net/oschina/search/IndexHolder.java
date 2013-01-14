package net.oschina.search;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * 索引库管理
 * @author Winter Lau
 */
public class IndexHolder {

    private final static Log log = LogFactory.getLog(IndexHolder.class);
    private final static IKAnalyzer analyzer = new IKAnalyzer();
	private String indexPath;

	/**
	 * 构造索引库管理实例
	 * @param idx_path
	 * @return
	 * @throws IOException
	 */
	public static IndexHolder init(String idx_path) throws IOException {
		IndexHolder holder = new IndexHolder();
		idx_path = FilenameUtils.normalize(idx_path);
		File file = new File(idx_path);
		if(!file.exists() || !file.isDirectory())
			throw new FileNotFoundException(idx_path);
		if(!idx_path.endsWith(File.separator))
			idx_path += File.separator;		
		holder.indexPath = idx_path;
		
		return holder;
	}
	
	private IndexWriter getWriter(Class<? extends Searchable> objClass) throws IOException {
		Directory dir = FSDirectory.open(new File(indexPath + objClass.getSimpleName()));
	    IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_40, analyzer);
	    config.setOpenMode(OpenMode.CREATE_OR_APPEND);
		return new IndexWriter(dir, config);
	}
	
	private IndexSearcher getSearcher(Class<? extends Searchable> objClass) throws IOException {
		Directory dir = FSDirectory.open(new File(indexPath + objClass.getSimpleName()));
		return new IndexSearcher(DirectoryReader.open(dir));
	}
	
	/**
	 * 多个资料库的搜索
	 * @param objClasses
	 * @return
	 * @throws IOException
	 */
	private IndexSearcher getSearchers(List<Class<? extends Searchable>> objClasses) throws IOException {
		IndexReader[] readers = new IndexReader[objClasses.size()];
		int idx = 0;
		for(Class<? extends Searchable> objClass : objClasses){
			FSDirectory dir = FSDirectory.open(new File(indexPath + objClass.getSimpleName()));
			readers[idx++] = DirectoryReader.open(dir);
		}
		return new IndexSearcher(new MultiReader(readers, true));
	}
	
	/**
	 * 多库搜索
	 * @param objClasses
	 * @param query
	 * @param max_count
	 * @return
	 * @throws IOException
	 */
	public List<Searchable> find(List<Class<? extends Searchable>> objClasses, Query query, int max_count) throws IOException {
		IndexSearcher searcher = getSearchers(objClasses);
		return find(searcher, query, max_count);
	}
	
	/**
	 * 搜索
	 * @param beanClass
	 * @param query
	 * @param max_count
	 * @return
	 * @throws IOException
	 */
	public List<Searchable> find(Class<? extends Searchable> objClass, Query query, int max_count) throws IOException {
		IndexSearcher searcher = getSearcher(objClass);
		return find(searcher, query, max_count);
	}

	/**
	 * 搜索
	 * @param beanClass
	 * @param query
	 * @param max_count
	 * @return
	 * @throws IOException
	 */
	private List<Searchable> find(IndexSearcher searcher, Query query, int max_count) throws IOException {
		try{
			TopDocs hits = searcher.search(query, null, max_count);
			if(hits==null) return null;
			List<Searchable> results = new ArrayList<Searchable>();
			int numResults = Math.min(hits.totalHits, max_count);
			for (int i = 0; i < numResults; i++){
				ScoreDoc s_doc = (ScoreDoc)hits.scoreDocs[i];
				Document doc = searcher.doc(s_doc.doc);
				long id = SearchHelper.docid(doc);
				if(id > 0 && !results.contains(id)){
					results.add(SearchHelper.doc2obj(doc));	
				}
			}
			return results;
		}catch(IOException e){
			log.error("Unabled to find via query: " + query, e);
		}
		return null;
	}
	
	/**
	 * 批量添加索引
	 * @param docs
	 * @throws IOException 
	 */
	public int add(List<? extends Searchable> objs) throws IOException {
		if (objs == null || objs.size() == 0)
			return 0;
		int doc_count = 0;
		IndexWriter writer = getWriter(objs.get(0).getClass());
		try{
			for (Searchable obj : objs) {
				Document doc = SearchHelper.obj2doc(obj);
				writer.addDocument(doc);
				doc_count++;
			}
			writer.commit();
		}finally{
			writer.close();
			writer = null;
		}
		return doc_count;
	}
	
	/**
	 * 批量删除索引
	 * @param docs
	 * @throws IOException 
	 */
	public int delete(List<? extends Searchable> objs) throws IOException {
		if (objs == null || objs.size() == 0)
			return 0;
		int doc_count = 0;
		IndexWriter writer = getWriter(objs.get(0).getClass());
		try{
			for (Searchable obj : objs) {
				writer.deleteDocuments(new Term("id", String.valueOf(obj.id())));
				doc_count++;
			}
			writer.commit();
		}finally{
			writer.close();
			writer = null;
		}
		return doc_count;
	}

	/**
	 * 批量更新索引
	 * @param docs
	 * @throws IOException 
	 */
	public void update(List<? extends Searchable> objs) throws IOException {
		delete(objs);
		add(objs);
	}
	
}
