package test;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class UserHandler extends DefaultHandler {

    boolean bType = false;
    boolean bLastName = false;
    boolean bNickName = false;
    boolean bMarks = false;
    static boolean flagCarga = false;

    @Override
    public void startElement(String uri,
                             String localName, String qName, Attributes attributes) throws SAXException {

        if (qName.equalsIgnoreCase("namespace")) {
            String packageName = attributes.getValue("name");
            SAXParserDemo.cantpak++;
            if (!(flagCarga)){
                SAXParserDemo.newPacket(packageName);
            }
        } else if (qName.equalsIgnoreCase("depends-on")) {
            String packageDep = attributes.getValue("name");
            if ((flagCarga)&&(!packageDep.contains("java"))){
                SAXParserDemo.fillMatriz(packageDep);
            }
        }
    }
}