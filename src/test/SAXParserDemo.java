package test;

import Algoritmos.Tarjan;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.Key;
import java.util.*;
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
        for(int i = 0; i < PackWClass.length();i++){
            if (PackWClass.charAt(i) == '.'){
                pos = i;
            }
        }
        return PackWClass.substring(0,(pos));
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

            //Inicializacion de Tarjan
            Tarjan t = new Tarjan(depMatriz);

            //Limpiar Diagonal
            for (int i = 0; i<cantpak;i++)
                depMatriz[i][i] = false;

            //Mostrar Dependencias TEST

            int [] CFC = t.getStronglyConnectedComponents();


            //Crear listas de ciclos
            HashMap<Integer, ArrayList> listaCiclos = new HashMap<>();
            for (int i=0;i<CFC.length;i++){
                if (!listaCiclos.containsKey(CFC[i])){
                    ArrayList<Integer> parcial = new ArrayList<>();
                    parcial.add(CFC[i]);
                    listaCiclos.put(CFC[i],parcial);
                } else{
                    listaCiclos.get(CFC[i]).add(i);
                }
            }

            //Eliminar listas de ciclos con menos de 3 componenetes
            Set<Integer> clavesCiclos = listaCiclos.keySet();
            Integer[] KeySetArray = new Integer[clavesCiclos.size()];
            clavesCiclos.toArray(KeySetArray);
            for (Integer key: KeySetArray
                 ) {
                if (listaCiclos.get(key).size() < 3){
                    listaCiclos.remove(key);
                }
            }

            //Crear Archivo txt con informacion de ciclos
            File file = new File("."+File.separator+"Lista de ciclos");
            //Create the file
            try{
                if (file.createNewFile())
                {
                    System.out.println("File is created!");
                } else {
                    System.out.println("File already exists.");
                }
            } catch (IOException e){
                System.out.println("NO");
            }

            Set<Integer> clavesCiclosPosElim = listaCiclos.keySet();
            Integer[] KeySetArrayPosElim = new Integer[clavesCiclosPosElim.size()];
            clavesCiclosPosElim.toArray(KeySetArrayPosElim);
            String ciclos = "";


            //Write Content
            FileWriter writer = new FileWriter(file);
            for (int i = 0;i<KeySetArrayPosElim.length;i++){
                ciclos = ciclos + "El ciclo de depencias " + KeySetArrayPosElim[i] + " esta compuesto por: ";
                for (int j = 0; j < listaCiclos.get(KeySetArrayPosElim[i]).size(); j++){
                    System.out.println(ciclos);
                    ciclos = ciclos + " " + InvReferencia.get(listaCiclos.get(KeySetArrayPosElim[i]).get(j)) + " ";
                }
                ciclos = ciclos + " \\r\\n";
            }

            writer.write(ciclos);
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}