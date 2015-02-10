package org.japura;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.japura.util.info.IdentifierNode;
import org.japura.util.info.InfoNode;
import org.japura.util.info.InfoProvider;

/**
 * <P>
 * Copyright (C) 2013-2014 Carlos Eduardo Leite de Andrade
 * <P>
 * This library is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * <P>
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * <P>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <A
 * HREF="www.gnu.org/licenses/">www.gnu.org/licenses/</A>
 * <P>
 * For more information, contact: <A HREF="www.japura.org">www.japura.org</A>
 * <P>
 * 
 * @author Carlos Eduardo Leite de Andrade
 */
public class Session implements InfoProvider{

  private String id;
  private Map<Object, Object> values;
  private Map<Class<?>, Object> sessionDatas;
  private Boolean booleanValue;
  private Integer integerValue;
  private Float floatValue;
  private Double doubleValue;
  private String stringValue;
  private Byte byteValue;

  public Session() {
	this.values = new HashMap<Object, Object>();
	this.sessionDatas = new HashMap<Class<?>, Object>();
	this.id = Application.buildId();
  }

  public final String getId() {
	return id;
  }

  public boolean containsSessionData(Class<?> clss) {
	return this.sessionDatas.containsKey(clss);
  }

  public void clearSessionData(Class<?> clss) {
	this.sessionDatas.remove(clss);
  }

  public <T> T getSessionData(Class<T> clss) {
	if (clss != null) {
	  Object obj = this.sessionDatas.get(clss);
	  if (obj != null) {
		return clss.cast(obj);
	  }
	  try {
		T vo = clss.newInstance();
		this.sessionDatas.put(clss, vo);
		return vo;
	  } catch (InstantiationException e) {
		throw new RuntimeException(e);
	  } catch (IllegalAccessException e) {
		throw new RuntimeException(e);
	  }
	}
	return null;
  }

  public void put(Object key, Object value) {
	if (key != null) {
	  values.put(key, value);
	}
  }

  public void put(Object value) {
	if (value != null) {
	  values.put(value.getClass(), value);
	}
  }

  public <T> T get(Class<T> clss) {
	if (clss != null) {
	  Object obj = values.get(clss);
	  if (obj != null) {
		return clss.cast(obj);
	  }
	}
	return null;
  }

  public Object get(Object key) {
	if (key != null) {
	  return values.get(key);
	}
	return null;
  }

  public void clear(Object key) {
	if (key != null) {
	  values.remove(key);
	}
  }

  public boolean contains(Object key) {
	if (key != null) {
	  return values.containsKey(key);
	}
	return false;
  }

  public Boolean getBooleanValue() {
	return booleanValue;
  }

  public void setBooleanValue(Boolean booleanValue) {
	this.booleanValue = booleanValue;
  }

  public Integer getIntegerValue() {
	return integerValue;
  }

  public void setIntegerValue(Integer integerValue) {
	this.integerValue = integerValue;
  }

  public Float getFloatValue() {
	return floatValue;
  }

  public void setFloatValue(Float floatValue) {
	this.floatValue = floatValue;
  }

  public Double getDoubleValue() {
	return doubleValue;
  }

  public void setDoubleValue(Double doubleValue) {
	this.doubleValue = doubleValue;
  }

  public String getStringValue() {
	return stringValue;
  }

  public void setStringValue(String stringValue) {
	this.stringValue = stringValue;
  }

  public Byte getByteValue() {
	return byteValue;
  }

  public void setByteValue(Byte byteValue) {
	this.byteValue = byteValue;
  }

  @Override
  public String toString() {
	return getClass().getName() + " [id:" + getId() + "]";
  }

  @Override
  public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + id.hashCode();
	return result;
  }

  @Override
  public boolean equals(Object obj) {
	if (this == obj)
	  return true;
	if (obj == null)
	  return false;
	if (getClass() != obj.getClass())
	  return false;
	Session other = (Session) obj;
	if (!id.equals(other.id))
	  return false;
	return true;
  }

  @Override
  public Collection<IdentifierNode> getIdentifierNodes() {
	Collection<IdentifierNode> nodes = new ArrayList<IdentifierNode>();
	nodes.add(new IdentifierNode(InfoNodeIdentifiers.SESSION_WRAPPER_VALUES
		.name(), "Wrapper values"));
	nodes.add(new IdentifierNode(InfoNodeIdentifiers.SESSION_MAP_VALUES.name(),
		"Map values (key/value)"));
	nodes.add(new IdentifierNode(InfoNodeIdentifiers.SESSION_DATA_CLASSES
		.name(), "Data classes"));
	return nodes;
  }

  @Override
  public Collection<InfoNode> getInfoNodes() {
	Collection<InfoNode> nodes = new ArrayList<InfoNode>();

	nodes.add(new InfoNode("Id", getId()));

	addExtraInfoNodes(nodes);

	nodes.add(new InfoNode(InfoNodeIdentifiers.SESSION_DATA_CLASSES.name(),
		"Data classes", getDataClassesInfoNodes()));

	nodes.add(new InfoNode(InfoNodeIdentifiers.SESSION_MAP_VALUES.name(),
		"Map values (key/value)", getMapValuesInfoNodes()));

	nodes.add(new InfoNode(InfoNodeIdentifiers.SESSION_WRAPPER_VALUES.name(),
		"Wrapper values", getWrapperValuesInfoNodes()));

	return nodes;
  }

  protected void addExtraInfoNodes(Collection<InfoNode> nodes) {}

  private Collection<InfoNode> getWrapperValuesInfoNodes() {
	Collection<InfoNode> nodes = new ArrayList<InfoNode>();

	if (getBooleanValue() != null) {
	  nodes.add(new InfoNode("booleanValue", getBooleanValue().toString()));
	} else {
	  nodes.add(new InfoNode("booleanValue", "null"));
	}

	if (getIntegerValue() != null) {
	  nodes.add(new InfoNode("integerValue", getIntegerValue().toString()));
	} else {
	  nodes.add(new InfoNode("integerValue", "null"));
	}

	if (getFloatValue() != null) {
	  nodes.add(new InfoNode("floatValue", getFloatValue().toString()));
	} else {
	  nodes.add(new InfoNode("floatValue", "null"));
	}

	if (getDoubleValue() != null) {
	  nodes.add(new InfoNode("doubleValue", getDoubleValue().toString()));
	} else {
	  nodes.add(new InfoNode("doubleValue", "null"));
	}

	if (getStringValue() != null) {
	  nodes.add(new InfoNode("stringValue", getStringValue()));
	} else {
	  nodes.add(new InfoNode("stringValue", "null"));
	}

	if (getByteValue() != null) {
	  nodes.add(new InfoNode("byteValue", getByteValue().toString()));
	} else {
	  nodes.add(new InfoNode("byteValue", "null"));
	}

	return nodes;
  }

  private Collection<InfoNode> getMapValuesInfoNodes() {
	Collection<InfoNode> nodes = new ArrayList<InfoNode>();
	for (Entry<Object, Object> entry : values.entrySet()) {
	  String key = entry.getKey().toString();
	  String value = null;
	  if (entry.getValue() == null) {
		value = "null";
	  } else {
		value = entry.getValue().toString();
	  }
	  nodes.add(new InfoNode(key, value));
	}
	return nodes;
  }

  private Collection<InfoNode> getDataClassesInfoNodes() {
	Collection<InfoNode> nodes = new ArrayList<InfoNode>();
	int i = 1;
	for (Entry<Class<?>, Object> entry : sessionDatas.entrySet()) {
	  Class<?> dataClass = entry.getKey();
	  Object data = entry.getValue();

	  Collection<InfoNode> dataNodes = new ArrayList<InfoNode>();
	  dataNodes.add(new InfoNode("Package", dataClass.getPackage().getName()));
	  dataNodes.add(new InfoNode("Class", dataClass.getSimpleName()));

	  if (data instanceof InfoProvider) {
		InfoProvider provider = (InfoProvider) data;
		for (InfoNode node : provider.getInfoNodes()) {
		  dataNodes.add(node);
		}
	  }

	  nodes.add(new InfoNode("Data " + i, dataNodes));
	  i++;
	}
	return nodes;
  }

}
