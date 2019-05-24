package test;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class UserHandler extends DefaultHandler {
    static boolean packagesAreLoaded = false;

    @Override
    public void startElement(String uri,
                             String localName, String qName, Attributes attributes) throws SAXException {

        if (qName.equalsIgnoreCase("namespace")) {
            String packageName = attributes.getValue("name");
            SAXParserDemo.numberOfPackages++;
            if (!(packagesAreLoaded)){
                SAXParserDemo.newPacket(packageName);
            }
        } else if (qName.equalsIgnoreCase("depends-on")) {
            String packageDep = attributes.getValue("name");
            if ((packagesAreLoaded)&&(!packageDep.contains("java"))){
                SAXParserDemo.fillMatrix(packageDep);
            }
        }
    }
}