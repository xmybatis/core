package com.github.xmybatis.core;

public class ExampleWraper {
	
	protected Criteria c=null;
	protected Example ex=null;
	
	public void addIdEq(Object value)
	{
		c.addCriterion("a.id=", value, "id");
	}
	
	public void addNameEq(Object value)
	{
		c.addCriterion("a.name = ",value,"name");
	}
	
	public void addNameLike(Object value)
	{
		c.addCriterion("a.name like ",value, "%","%","name");
	}
	
	public ExampleWraper() {
		super();
		ex=new Example();
		c=ex.createCriteria(Criteria.class);
	}
	
	public Example getExample()
	{
		return ex;
	}
}
