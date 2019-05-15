package test;

import Algoritmos.Tarjan;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Clase de SAXParser. Se encarga de levantar el XML. Por ahrora levanta solo uno, indicado en el path del metodo main.
 */
public class SAXParserDemo {
    /**
     * Contador de numero total de paquetes, usado para crear matriz y como fila actual
     */
    static int cantpak=0;

    /**
     * Matriz que contendra true si el paquete fila depende del paquete columna
     */
    private static boolean [][] depMatriz;

    /**
     * Referencias en la matriz.
     */
    private static HashMap<String,Integer> referencia = new HashMap<String, Integer>();
    private static HashMap<Integer,String> InvReferencia = new HashMap<Integer, String>();

    /**
     * Al encontrar un paquete en UserHandler se agrega a los mapas, y cual sera su posicion en la matriz
     * @param namePack es el nombre del paquete encontrado en el XML.
     */
    static void newPacket(String namePack){
        referencia.put(namePack,cantpak-1);
        InvReferencia.put(cantpak-1,namePack);
    }

    /**
     * Iniciacion con la cantidad de paquetes
     */
    private static void matrizInic(){
        depMatriz = new boolean[cantpak][cantpak];
    }

    /**
     * Se elimina el nombre del componente en el String, para que quede unicamente el nombre del paquete.
     * @param PackWClass String completo (con el nombre del componente)
     * @return nombre del paquete sin el componente.
     */
    private static String getPack(String PackWClass){
        int pos = 0;
        StringBuilder builder = new StringBuilder(PackWClass);
        for(int i = 0; i < PackWClass.length();i++){
            if (builder.charAt(i) == '.'){
                pos = i;
            }
        }
        return builder.substring(0, pos);
    }

    /**
     * Llamado desde Userhandler al encontrar un paquete valido. Cantpack es aumentado desde UserHandler, marca el
     * paquete actual y la fila actual de la matriz
     * @param packDep nombre del paquete dependencia encontrada.
     */
    static void fillMatriz(String packDep){
        if ((referencia.containsKey(getPack(packDep)))){
            depMatriz[cantpak-1][referencia.get(getPack(packDep))] = true;
        }
    }

    /**
     * Levanta el XML del path indicado al crear el objeto File. Después llama al parser con los parametros indicados
     * en UserHandler. Hace dos pasadas, una para levantar todos los paquetes del sistema y poder tener el tamaño de la
     * matriz, y la segunda pasada la hace para establecer las dependencias en la matriz.
     * @param args argumentos del main. AL final vuela.
     */
    public static void main(String[] args) {
        try {
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

            //Prueba impresion de depencias
//            for (int i = 1; i < cantpak-1; i++){
//                    if (depMatriz[0][i]){
//                        System.out.println("El primero depende del segundo:");
//                        System.out.println(InvReferencia.get(0));
//                        System.out.println(InvReferencia.get(i));
//                    }
//            }

            Tarjan sccs = new Tarjan(depMatriz);
            System.out.println("Strong connected component count: " + sccs.countStronglyConnectedComponents());
            System.out.println("Strong connected components:\n" + Arrays.toString(sccs.getStronglyConnectedComponents()) );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}