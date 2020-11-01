package com.github.xmybatis.core;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class Criterion {
	
	private String condition;

    private Object value;

    private Object secondValue;

    private boolean noValue;

    private boolean singleValue;

    private boolean betweenValue;

    private boolean listValue;
    private boolean likeValue;
    
    private String typeHandler;

    private Object suffix;

    private Object prefix;
    
    public String getCondition() {
        return condition;
    }

    public Object getValue() {
        return value;
    }

    public Object getSecondValue() {
        return secondValue;
    }

    public boolean isNoValue() {
        return noValue;
    }

    public boolean isSingleValue() {
        return singleValue;
    }

    public boolean isBetweenValue() {
        return betweenValue;
    }

    public boolean isLikeValue() {
		return likeValue;
	}

	public boolean isListValue() {
        return listValue;
    }

    public String getTypeHandler() {
        return typeHandler;
    }

    public Object getLikeSuffix() {
    	//log.info("suffix:"+suffix);
        return suffix;
    }

    public Object getLikePrefix() {
    	//log.info("prefix:"+prefix);
		return prefix;
	}

	protected Criterion(String condition) {
        super();
        this.condition = condition;
        this.typeHandler = null;
        this.noValue = true;
    }

    protected Criterion(String condition, Object value, String typeHandler) {
        super();
        this.condition = condition;
        this.value = value;
        this.typeHandler = typeHandler;
        if (value instanceof List<?> ||  value.getClass().isArray()) {
            this.listValue = true;
        } else {
            this.singleValue = true;
        }
    }

    protected Criterion(String condition, Object value) {
        this(condition, value, null);
    }

    protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
        super();
        this.condition = condition;
        this.value = value;
        this.secondValue = secondValue;
        this.typeHandler = typeHandler;
        this.betweenValue = true;
        this.suffix="";
    }

    protected Criterion(String condition, Object value, Object prefix, Object suffix,String typeHandler) {
    	this.condition = condition;
        this.value = value;
        this.suffix=suffix;
        this.prefix=prefix;
        this.typeHandler = typeHandler;
        this.likeValue=true;
       
    }

    protected Criterion(String condition, Object value, Object secondValue) {
        this(condition, value, secondValue, null);
    }
}
