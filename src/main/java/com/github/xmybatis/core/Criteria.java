package com.github.xmybatis.core;

import java.util.ArrayList;
import java.util.List;

public class Criteria {
	protected List<Criterion> criteria;
	public Criteria(){
		criteria=new ArrayList<Criterion>();
	}
	public boolean isValid() {
        return criteria.size() > 0;
    }

    public List<Criterion> getAllCriteria() {
        return criteria;
    }

    public List<Criterion> getCriteria() {
        return criteria;
    }
    public void addCriterion(String condition) {
        if (condition == null) {
            throw new RuntimeException("Value for condition cannot be null");
        }
        criteria.add(new Criterion(condition));
    }

    public void addCriterion(String condition, Object value, String property) {
        if (value == null) {
            throw new RuntimeException("Value for " + property + " cannot be null");
        }
        criteria.add(new Criterion(condition, value));
    }

    public void addCriterion(String condition, Object value1, Object value2, String property) {
        if (value1 == null || value2 == null) {
            throw new RuntimeException("Between values for " + property + " cannot be null");
        }
        criteria.add(new Criterion(condition, value1, value2));
    }
    
    public void addCriterion(String condition, Object value, Object prefix,Object suffix, String property) {
        if (value == null) {
            throw new RuntimeException( property + " cannot be null");
        }
        criteria.add(new Criterion(condition, value, prefix,suffix,property));
    }
    
    public void andEq(String col,Object value)
    {
    	criteria.add(new Criterion(col+" = ", value));
    }
    public void andNotEq(String col,Object value)
    {
    	criteria.add(new Criterion(col+" <> ", value));
    }
    public void andGt(String col,Object value)
    {
    	criteria.add(new Criterion(col+" > ", value));
    }
    public void andGtAndEq(String col,Object value)
    {
    	criteria.add(new Criterion(col+" >= ", value));
    }
    public void andLt(String col,Object value)
    {
    	criteria.add(new Criterion(col+" < ", value));
    }
    public void andLtAndEq(String col,Object value)
    {
    	criteria.add(new Criterion(col+" <= ", value));
    }
    public void andBetween(String col,Object value1,Object value2)
    {
    	criteria.add(new Criterion(col+" between ", value1,value2));
    }
    public void andNotBetween(String col,Object value1,Object value2)
    {
    	criteria.add(new Criterion(col+" not between ", value1,value2));
    }
    
    public void andIn(String col,List<?> value)
    {
    	criteria.add(new Criterion(col+" in ", value));
    }
    public void andNotIn(String col,Object value)
    {
    	criteria.add(new Criterion(col+" not in ", value));
    }
    
}
