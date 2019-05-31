package Parser;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class UserHandler extends DefaultHandler {
    static boolean packagesAreLoaded = false;

    @Override
    public void startElement(String uri,
                             String localName, String qName, Attributes attributes) {

        if (qName.equalsIgnoreCase("namespace")) {
            String packageName = attributes.getValue("name");
            SAX.numberOfPackages++;
            if (!(packagesAreLoaded)){
                SAX.newPacket(packageName);
            }
        } else if (qName.equalsIgnoreCase("depends-on")) {
            String packageDep = attributes.getValue("name");
            if ((packagesAreLoaded)&&(!packageDep.contains("java"))){
                SAX.fillMatrix(packageDep);
            }
        }
    }
}