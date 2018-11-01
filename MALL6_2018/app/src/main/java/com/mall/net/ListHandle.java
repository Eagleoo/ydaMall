package com.mall.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class ListHandle extends DefaultHandler {
	private Object obj;
	private String tagName = null;
	private Class c = null;
	private Field[] fields = null;
	private List<Object> list = new ArrayList<Object>();
	private StringBuffer xml = new StringBuffer();
	private String matchesNode;

	public ListHandle(Class c, String matchesNode, InputStream in) {
		 this.matchesNode=matchesNode;
		this.c = c;
		this.fields = c.getDeclaredFields();
		try {
			this.obj = c.newInstance();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		SAXParserFactory spf = SAXParserFactory.newInstance();
		try {
			SAXParser saxParser = spf.newSAXParser();
			InputSource is = new InputSource(new InputStreamReader(in, "UTF-8"));
			saxParser.parse(is, this);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes attr) throws SAXException {
		this.tagName = localName;
		try {
			obj = c.newInstance();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		}
		if (matchesNode.equals(localName) || matchesNode.equals(qName)) {
			for (int i = 0; i < attr.getLength(); i++) {
				for (Field field : fields) {
					String aQName = attr.getQName(i);
					String aLName = attr.getLocalName(i);
					String fName = field.getName();
					if (fName.equalsIgnoreCase(aQName)
							|| fName.equalsIgnoreCase(aLName)) {
						field.setAccessible(true);
						try {
							field.set(obj, attr.getValue(aQName));
						} catch (Exception e) {
							Log.e("-----------",
									e.getLocalizedMessage() + "  --  " + c
											+ "." + fName + "="
											+ attr.getValue(aQName)
											+ "(aQName=" + aQName + ")");
							e.printStackTrace();
						}
					}
				}
			}
			list.add(obj);
		}
	}

	@Override
	public void characters(char[] ch, int start, int end) throws SAXException {
		if (matchesNode.equals(tagName)) {
			for (Field field : fields) {
				if ("content".equalsIgnoreCase(field.getName())) {
					xml.append(new String(ch, start, end));
				}
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		if (matchesNode.equals(tagName)) {
			for (Field field : fields) {
				if ("content".equalsIgnoreCase(field.getName())) {
					field.setAccessible(true);
					try {
						field.set(obj, xml.toString());
						xml.setLength(0);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
		this.tagName = null;
	}

	public List<Object> getList() {
		return list;
	}

}
