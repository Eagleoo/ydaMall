package com.mall;

import com.mall.model.YdNewsModel;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/4/16.
 */

public class ListHandler extends DefaultHandler {
    private List<YdNewsModel> list = new ArrayList<YdNewsModel>();
    private YdNewsModel y;
    private StringBuilder builder;

    // 返回解析后得到的Book对象集合
    public List<YdNewsModel> getList() {
        return list;
    }

    @Override
    public void startDocument() throws SAXException {
        list.clear();
        builder = new StringBuilder();
        super.startDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (qName.equals("list"))
            y = new YdNewsModel();
        builder.setLength(0);
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);
        builder.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        String s = builder.toString();
        if (qName.equals("id")) {
            y.setId(s);
        } else if (qName.equals("tid")) {
            y.setTid(s);
        } else if (qName.equals("nid")) {
            y.setNid(s);
        } else if (qName.equals("Praise")) {
            y.setPraise(s);
        } else if (qName.equals("Comment")) {
            y.setComment(s);
        } else if (qName.equals("click_sum")) {
            y.setClick_sum(s);
        } else if (qName.equals("new_from")) {
            y.setNew_from(s);
        } else if (qName.equals("content")) {
            s = s.replace("\t", "");
            s = s.replace("\n", "");
            s = s.replace("\r", "");
            s = s.replace("", "");
            s = s.replace("<br/>", "");
            s = com.mall.util.Util.Html2Text(s);
            y.setContent(" " + s.replace("\0", ""));
        } else if (qName.equals("title")) {
            y.setTitle(s);
        } else if (qName.equals("picurl")) {
            y.setPicurl(s);
        } else if (qName.equals("newdate")) {
            y.setNewdate(s);
        } else if (qName.equals("list")) {
            list.add(y);
        }
        super.endElement(uri, localName, qName);
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }
}
