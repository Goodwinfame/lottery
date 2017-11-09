package com.goodwin.utils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;


import com.goodwin.model.Member;
import com.goodwin.model.Members;
import com.goodwin.model.Prize;
import com.goodwin.model.Prizes;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class XmlUtil {
    public  static Object xml2Bean(String filePath, Class clazz) {
        JAXBContext context = null;
        Format format = Format.getPrettyFormat();

        format.setEncoding("UTF-8");

        XMLOutputter xmlout = new XMLOutputter(format);

        ByteArrayOutputStream bo = new ByteArrayOutputStream();

        Object result = null;

        try {
            Document doc = xml2Doc(filePath);

            xmlout.output(doc,bo);

            String xmlStr = bo.toString();

            context = JAXBContext.newInstance(clazz.newInstance().getClass());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            result = unmarshaller.unmarshal(new StringReader(xmlStr));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;

    }

    public static void object2Xml(Object obj, String filePath){

        try{
            JAXBContext jaxbContext = JAXBContext.newInstance(obj.getClass());
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            //Marshal the employees list in console

            //Marshal the employees list in file
            jaxbMarshaller.marshal(obj, new File(filePath));
        } catch (Exception e){
            e.printStackTrace();

        }

    }

    public static Document xml2Doc(String xmlFilePath) throws Exception {
        File file = new File(xmlFilePath);
        return (new SAXBuilder()).build(file);
    }



}