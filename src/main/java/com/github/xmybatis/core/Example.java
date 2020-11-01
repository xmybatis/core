package com.github.xmybatis.core;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Example {
	
	//Logger log = LoggerFactory.getLogger(Example.class);
	protected String orderByClause=" ";

	protected Long offset=null;

	protected Integer limit=null;

	protected boolean distinct=false;

	protected List<Criteria> oredCriteria;

	protected String schema=null;
	
	public void setSchema(String schema)
	{
		this.schema=schema;
	}
	public String getSchema()
	{
		return schema;
	}
//	public String getDbName() {
//		return schema;
//	}
//
//	public void setDbName(String dbName) {
//		this.schema = dbName;
//	}

	public Example() {
		oredCriteria = new ArrayList<Criteria>();
	}

	public String getOrderByClause() {
		return orderByClause;
	}

	public void setOrderByClause(String orderByClause) {
		this.orderByClause = orderByClause;
	}

	public Long getOffset() {
		return offset;
	}

	public void setOffset(Long offset) {
		this.offset = offset;
	}

	public Integer getLimit() {
		return limit;
	}

	public void setLimit(Integer limit) {
		this.limit = limit;
	}

	public boolean isDistinct() {
		return distinct;
	}

	public void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	public List<Criteria> getOredCriteria() {
		return oredCriteria;
	}

	// public void setOredCriteria(List<T> oredCriteria) {
	// this.oredCriteria = oredCriteria;
	// }

	public <K extends Criteria> K  createCriteria(Class<K> cls) {
		
		K  criteria = createCriteriaInternal(cls);
		//if (oredCriteria.size() == 0) {
			oredCriteria.add(criteria);
		//}
		return criteria;
	}
	


	protected <K extends Criteria> K createCriteriaInternal(Class<K> cls) {

		K criteria = null;
		try {
			criteria = cls.getDeclaredConstructor().newInstance();
		} catch (InstantiationException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return criteria;
	}


	public void clear() {
		oredCriteria.clear();
		orderByClause = null;
		distinct = false;
	}

}
