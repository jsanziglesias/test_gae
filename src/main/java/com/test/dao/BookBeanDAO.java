package com.test.dao;

import com.googlecode.objectify.ObjectifyService;
import com.test.data.BookBean;
import com.googlecode.objectify.cmd.Query;


import java.util.List;
import java.util.logging.Logger;

/**
 * @author Alejandro Aranda
 */
public class BookBeanDAO {

    private static final Logger LOGGER = Logger.getLogger(BookBeanDAO.class.getName());

    /**
     * @return list of test beans
     */
    public List<BookBean> list() {
        LOGGER.info("Retrieving list of beans");
        return ObjectifyService.ofy().load().type(BookBean.class).list();
    }
	
	    /**
     * @return list of test beans
     */
    public List<BookBean> list(String query) {
        LOGGER.info("Retrieving list of beans");
		LOGGER.info("Searching by keyword: " + query);
		Query<BookBean> q = ObjectifyService.ofy().load().type(BookBean.class);
		q=q.filter("name", query);
		q=q.order("author");
		LOGGER.info(q.toString());
		List<BookBean> foos = q.list();
		LOGGER.info(foos.toString());

        return foos;
    }

    /**
     * @param id
     * @return book bean with given id
     */
    public BookBean get(Long id) {
        LOGGER.info("Retrieving bean " + id);
        return ObjectifyService.ofy().load().type(BookBean.class).id(id).now();
    }

    /**
     * Saves given bean
     * @param bean
     */
    public void save(BookBean bean) {
        if (bean == null) {
            throw new IllegalArgumentException("null book object");
        }
        LOGGER.info("Saving bean " + bean.getId());
        ObjectifyService.ofy().save().entity(bean).now();
    }

    /**
     * Deletes given bean
     * @param bean
     */
    public void delete(BookBean bean) {
        if (bean == null) {
            throw new IllegalArgumentException("null book object");
        }
        LOGGER.info("Deleting bean " + bean.getId());
        ObjectifyService.ofy().delete().entity(bean);
    }
}
