package com.test.dao;

import com.test.data.BookBean;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Field.FieldType;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchException;
import com.google.appengine.api.search.SearchServiceFactory;
import com.google.appengine.api.search.StatusCode;
import com.google.appengine.api.search.GetResponse;
import com.google.appengine.api.search.GetIndexesRequest;
import com.google.appengine.api.search.Schema;
import com.google.appengine.api.search.GetRequest;
import com.google.appengine.api.search.SortOptions;
import com.google.appengine.api.search.SortExpression;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.search.Query;

import org.joda.time.DateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Alejandro Aranda
 */
public class BookBeanDAO {

    private static final Logger LOGGER = Logger.getLogger(BookBeanDAO.class.getName());

	private static final String INDEX = "testIndex";
	
	
	private Index getIndex() {
		IndexSpec indexSpec = IndexSpec.newBuilder().setName(INDEX).build();
		Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
		return index;
	}
	
	 /**
	 * Filter books by string
	 * @param String to filter by
     * @return list of books
     */
	
	 public List<BookBean> list(String queryText) {
		List<BookBean> listResult = new ArrayList<BookBean>();
		      SortOptions sortOptions =
		SortOptions.newBuilder()
			.addSortExpression(
				SortExpression.newBuilder()
				  .setExpression("author")
				  .setDirection(SortExpression.SortDirection.ASCENDING))
			.setLimit(1000)
			.build();

		// Build the QueryOptions
		QueryOptions options =
			QueryOptions.newBuilder()
				.setLimit(25)
				.setFieldsToReturn("author", "name")
				.setSortOptions(sortOptions)
				.build();
		String queryString = "name = ~" + queryText + " OR author = ~" + queryText ;
		//  Build the Query and run the search
		Query query = Query.newBuilder().setOptions(options).build(queryString);		
		Results<ScoredDocument> result = getIndex().search(query);
		LOGGER.info("Searched by param: " + query);
		 for (ScoredDocument doc : result.getResults()) {
			BookBean bookBean = new BookBean(
				doc.getId(),
				doc.getOnlyField("author").getText(),
				doc.getOnlyField("name").getText(),
				null,
				null
				);
			listResult.add(bookBean);
		 }
		return listResult;
    }

    /**
     * @param id
     * @return BookBean with given id
     */
    public BookBean get(String id) {
        LOGGER.info("Retrieving bean " + id);
		Document doc = getIndex().get(id);
		BookBean bookBean = new BookBean(
			doc.getId(),
			doc.getOnlyField("author").getText(),
			doc.getOnlyField("name").getText(),
			doc.getOnlyField("releaseDate").getDate() != null ? new DateTime (doc.getOnlyField("releaseDate").getDate()) : null,
			doc.getOnlyField("genre").getText()
			);
		return bookBean;
    }

    /**
     * Saves given BookBean
     * @param BookBean
     */
    public void save(BookBean bean) {
        if (bean == null) {
            throw new IllegalArgumentException("null book object");
        }
        LOGGER.info("Saving bean " + bean.getName());
		createDocumentByBookBean(bean);
    }

    /**
     * Deletes given bean
     * @param bean
     */
    public void delete(String id) {
        if (id == null) {
            throw new IllegalArgumentException("null book object");
        }
        LOGGER.info("Deleting bean " + id);
		getIndex().delete(id);
    }
	
	/**
	 * Creates a document from a BookBean
	 * @param BookBean
     */
	private void createDocumentByBookBean(BookBean bookBean){
			Document document =
			Document.newBuilder()
            .addField(Field.newBuilder().setName("name").setText(bookBean.getName()))
			.addField(Field.newBuilder().setName("author").setText(bookBean.getAuthor()))
			.addField(Field.newBuilder().setName("releaseDate").setDate(bookBean.getReleaseDate() != null ? bookBean.getReleaseDate().toDate() : null))
			.addField(Field.newBuilder().setName("genre").setText(bookBean.getGenre()))
            .build();
		try {
		  Utils.indexADocument(INDEX, document);
		} catch (InterruptedException e) {
		  LOGGER.info("Interrupted");
		  return;
		}
		LOGGER.info("Indexed a new document.");
	}
}
