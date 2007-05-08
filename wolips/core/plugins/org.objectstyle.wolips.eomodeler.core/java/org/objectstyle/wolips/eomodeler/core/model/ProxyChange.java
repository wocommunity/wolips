package org.objectstyle.wolips.eomodeler.core.model;

import org.objectstyle.wolips.eomodeler.core.utils.ComparisonUtils;

public class ProxyChange {
	private Object _source;
	private Object _value;
	
	public ProxyChange(Object source, Object value) {
		_source = source;
		_value = value;
	}
	
	public Object getSource() {
		return _source;
	}
	
	public Object getValue() {
		return _value;
	}
	
	@Override
	public boolean equals(Object otherObj) {
		return otherObj instanceof ProxyChange && ComparisonUtils.equals(_source, ((ProxyChange)otherObj)._source) && ComparisonUtils.equals(_value, ((ProxyChange)otherObj)._value);
	}
	
	@Override
	public int hashCode() {
		return ((_source == null) ? 0 : _source.hashCode()) * ((_value == null) ? 1 : _value.hashCode());
	}
	
	@Override
	public String toString() {
		return "[ProxyChange: source = " + _source + "; value = " + _value + "]"; 
	}
}
