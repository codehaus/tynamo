/*
 * Copyright 2005, Inspiring BV, the Netherlands
 *
 * info@inspiring.nl
 */

package org.trails.spring.persistence;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.trails.descriptor.IClassDescriptor;
import org.trails.spring.util.HibernateUtil;

/**
 * A PagingCriteria is used for paging.
 * 
 * @author Jurjan Woltman
 * 
 */
public class PagingCriteria extends DetachedCriteria {

    /** Root entity class. */
    private Class entityClass;

    private int pageNumber;

    private int pageSize;

    private int totalPageNumbers;

    private IClassDescriptor classDescriptor;

    private Object example;

    public PagingCriteria(IClassDescriptor classDescriptor, int pageNumber,
            int pageSize) {
        this(null, classDescriptor, pageNumber, pageSize);
    }

    public PagingCriteria(Object example, IClassDescriptor classDescriptor,
            int pageNumber, int pageSize) {
        super(classDescriptor.getType().getName());
        this.example = example;
        this.classDescriptor = classDescriptor;
        setEntityClass(classDescriptor.getType());
        setPageNumber(pageNumber);
        setPageSize(pageSize);
    }

    /**
     * @see org.hibernate.criterion.DetachedCriteria#getExecutableCriteria(org.hibernate.Session)
     */
    @Override
    public Criteria getExecutableCriteria(Session session) {
        Criteria countCriteria = session.createCriteria(
                classDescriptor.getType())
                .setProjection(Projections.rowCount());
        if (example != null) {
            HibernateUtil.addRestrictionsForExample(countCriteria, example,
                    classDescriptor);
        }
        // get the total number of entities.
        Integer count = (Integer) countCriteria.uniqueResult();
        int x = (count != null ? count.intValue() : 0);
        int tot = (x + getPageSize() - 1) / getPageSize();
        setTotalPageNumbers(tot);

        // Restore original criteria
        Criteria criteria = session.createCriteria(classDescriptor.getType());
        if (example != null) {
            HibernateUtil.addRestrictionsForExample(criteria, example,
                    classDescriptor);
        }
        criteria.setProjection(null);
        criteria.addOrder(Order.asc(classDescriptor.getIdentifierDescriptor()
                .getName()));
        criteria.setResultTransformer(Criteria.ROOT_ENTITY);

        criteria.setFirstResult(getPageSize() * (getPageNumber() - 1)); // substract
                                                                        // 1 as
                                                                        // it is
                                                                        // 0
                                                                        // based...
        criteria.setMaxResults(getPageSize());

        return criteria;
    }

    /**
     * Returns the entityClass.
     * 
     * @return Returns the entityClass.
     */
    public Class getEntityClass() {
        return entityClass;
    }

    /**
     * Sets the entityClass.
     * 
     * @param entityClass
     *            The entityClass to set.
     */
    public void setEntityClass(Class entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * Returns the pageNumber.
     * 
     * @return Returns the pageNumber.
     */
    public int getPageNumber() {
        return pageNumber;
    }

    /**
     * Sets the pageNumber.
     * 
     * @param pageNumber
     *            The pageNumber to set.
     */
    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    /**
     * Returns the pageSize.
     * 
     * @return Returns the pageSize.
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * Sets the pageSize.
     * 
     * @param pageSize
     *            The pageSize to set.
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Returns the totalPageNumbers.
     * 
     * @return Returns the totalPageNumbers.
     */
    public int getTotalPageNumbers() {
        return totalPageNumbers;
    }

    /**
     * Sets the totalPageNumbers.
     * @param totalPageNumbers The totalPageNumbers to set.
     */
    public void setTotalPageNumbers(int totalPageNumbers) {
        this.totalPageNumbers = totalPageNumbers;
    }
}
