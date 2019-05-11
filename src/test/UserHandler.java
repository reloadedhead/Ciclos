package test;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class UserHandler extends DefaultHandler {

    boolean bType = false;
    boolean bLastName = false;
    boolean bNickName = false;
    boolean bMarks = false;

    @Override
    public void startElement(String uri,
                             String localName, String qName, Attributes attributes) throws SAXException {

        if (qName.equalsIgnoreCase("namespace")) {
            String packageName = attributes.getValue("name");
            System.out.println("Package name : " + packageName);
        } else if (qName.equalsIgnoreCase("type")) {
            String typeName = attributes.getValue("classification");
            System.out.println("Type name "+ typeName);
        }
//		} else if (qName.equalsIgnoreCase("lastname")) {
//			bLastName = true;
//		} else if (qName.equalsIgnoreCase("nickname")) {
//			bNickName = true;
//		}
//		else if (qName.equalsIgnoreCase("marks")) {
//			bMarks = true;
//		}
    }

    @Override
    public void endElement(String uri,
                           String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("namespace"))
            System.out.println("End Element :" + qName);
        else if (qName.equalsIgnoreCase("type"))
            bType = false;
//		else if (qName.equalsIgnoreCase("lastname"))
//			bLastName = false;
//		else if (qName.equalsIgnoreCase("nickname"))
//			bNickName = false;
//		else if (qName.equalsIgnoreCase("marks"))
//			bMarks = false;

    }

    @Override
    public void characters(char ch[], int start, int length) throws SAXException {

        if (bType) {
            System.out.println("Type: "
                    + new String(ch, start, length));
        } else if (bLastName) {
            System.out.println("Last Name: " + new String(ch, start, length));
        } else if (bNickName) {
            System.out.println("Nick Name: " + new String(ch, start, length));
        } else if (bMarks) {
            System.out.println("Marks: " + new String(ch, start, length));
        }
    }
}