package test;

import java.io.File;
import java.util.HashMap;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class SAXParserDemo {

    //Contador de numero total de paquetes, usado para crear matriz y como fila actual
    static int cantpak=0;

    //Matriz que contendra true si el paquete fila depende del paquete columna
    static boolean [][] depMatriz;

    //Referencias en la matriz
    static HashMap<String,Integer> referencia;
    static HashMap<Integer,String> InvReferencia;

    public static void newPacket(String namePack){
        //Al encontrar un paquete en UserHandler se agrega a los mapas, y cual sera su posicion en la matriz
        referencia.put(namePack,cantpak-1);
        InvReferencia.put(cantpak-1,namePack);
    }

    public static void matrizInic(){
        //Iniciacion con la cantidad de paquetes
        depMatriz = new boolean[cantpak][cantpak];
    }

    public static String getPack(String PackWClass){
        //Se elimina el nombre de la clase del paquete
        int pos = 0;
        for(int i = 0; i < PackWClass.length();i++){
            if (PackWClass.charAt(i) == '.'){
                pos = i;
            }
        }
        return PackWClass.substring(0,(pos));
    }

    public static void fillMatriz(String packDep){
        //Llamado desde Userhandler al encontrar un paquete valido
        //cantpack es aumentado desde UserHandler, marca el paquete actual y la fila actual de la matriz
        if ((referencia.containsKey(getPack(packDep)))){
            depMatriz[cantpak-1][referencia.get(getPack(packDep))] = true;
        }
    }

    public static void main(String[] args) {

        try {
            //Iniciacion de mapas para referencias nro paquete y pos en la matriz
            referencia = new HashMap<String, Integer>();
            InvReferencia = new HashMap<Integer, String>();

            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            SAXParser saxParser = factory.newSAXParser();
            File inputFile = new File("."+File.separator+"dependencias"+File.separator+"hibernate-core-4.2.0.Final.odem");
            UserHandler userhandler = new UserHandler();
            saxParser.parse(inputFile, userhandler);

            //Inicializacion de matriz de referencia, preparar flag para carga de bandera y reinicio de cantpack
            matrizInic();
            UserHandler.flagCarga = true;
            cantpak = 0;

            //Segunda pasada para cargar matriz
            saxParser.parse(inputFile, userhandler);

            //Prueba impresion de depencias
            for (int i = 1; i < cantpak-1; i++){
                    if (depMatriz[0][i] == true){
                        System.out.println("El primero depende del segundo:");
                        System.out.println(InvReferencia.get(0));
                        System.out.println(InvReferencia.get(i));
                    }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}