package test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

//import com.sun.tools.jdeps.InverseDepsAnalyzer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAXParserDemo {

    static int cantpak=0;
    static boolean [][] depMatriz;
    static HashMap<String,Integer> referencia;

    static HashMap<Integer,String> Invreferencia;

    public static void newPacket(String namePack){
        referencia.put(namePack,cantpak-1);
        Invreferencia.put(cantpak-1,namePack);
    }

    public static void matrizInic(){
        depMatriz = new boolean[cantpak][cantpak];
    }

    public static void fillMatriz(String packDep){
        Integer j = 0;
        int pos = 0;
        for(int i = 0; i < packDep.length();i++){
            if (packDep.charAt(i) == '.'){
                pos = i;
            }
        }
        String nombrePaquete = packDep.substring(0,(pos));

        if ((referencia.containsKey(nombrePaquete))){
            j = referencia.get(nombrePaquete);
            depMatriz[cantpak-1][j] = true;
        }
    }

    public static void main(String[] args) {

        try {
            referencia = new HashMap<String, Integer>();
            Invreferencia = new HashMap<Integer, String>();

            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            SAXParser saxParser = factory.newSAXParser();
            File inputFile = new File("."+File.separator+"dependencias"+File.separator+"hibernate-core-4.2.0.Final.odem");
            UserHandler userhandler = new UserHandler();
            saxParser.parse(inputFile, userhandler);
            matrizInic();
            UserHandler.flagCarga = true;
            cantpak = 0;


            saxParser.parse(inputFile, userhandler);
            for (int i = 1; i < cantpak-1; i++){
                    if (depMatriz[0][i] == true){
                        System.out.println("El primero depende del segundo:");
                        System.out.println(Invreferencia.get(0));
                        System.out.println(Invreferencia.get(i));
                    }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}