package test;

import Algoritmos.Tarjan;
import org.jetbrains.annotations.NotNull;

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
    static int numberOfPackages=0;

    /**
     * Matriz que contendra true si el paquete fila depende del paquete columna
     */
    private static boolean [][] dependenciesMatrix;

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
        referencia.put(namePack,numberOfPackages-1);
        InvReferencia.put(numberOfPackages-1,namePack);
    }

    /**
     * Iniciacion con la cantidad de paquetes
     */
    private static void matrixInit(){
        dependenciesMatrix = new boolean[numberOfPackages][numberOfPackages];
    }

    /**
     * Se elimina el nombre del componente en el String, para que quede unicamente el nombre del paquete.
     * @param PackWClass String completo (con el nombre del componente)
     * @return nombre del paquete sin el componente.
     */
    private static String getPackage(String PackWClass){
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
        if ((referencia.containsKey(getPackage(packDep)))){
            dependenciesMatrix[numberOfPackages-1][referencia.get(getPackage(packDep))] = true;
        }
    }

    /**
     * Este método elimina los ciclos del mapa que tengan un tamaño menor a size.
     * @param listaCiclos Mapa con el tamaño de como clave y el ciclo en un ArrayList.
     * @param size tamaño que queremos eliminar.
     */
    private static void deleteCicles(HashMap<Integer, ArrayList> listaCiclos, int size){
        Set<Integer> clavesCiclos = listaCiclos.keySet();
        Integer[] KeySetArray = new Integer[clavesCiclos.size()];
        clavesCiclos.toArray(KeySetArray);
        for (Integer key: KeySetArray
        ) {
            if (listaCiclos.get(key).size() < size){
                listaCiclos.remove(key);
            }
        }
    }


    /**
     * Este método genera un mapa con el número de ciclo ciclo como clave y el ciclo en un arraylist como valor.
     * TODO: el integer del mapa está al pedo. Hay que sacarlo y cambiar la estructura.
     * @param t objeto de la clase Tarjan para obtener las componentes fuertemente conectadas.
     * @return HashMap con los ciclos y sus número de ciclo.
     */
    private static HashMap<Integer, ArrayList> getCycleListFromTarjan(@NotNull Tarjan t){
        int[] stronglyConnectedComponents = t.getStronglyConnectedComponents();
        HashMap<Integer, ArrayList> listaCiclos = new HashMap<>();
        for (int i=0;i<stronglyConnectedComponents.length;i++){
            if (!listaCiclos.containsKey(stronglyConnectedComponents[i])){
                ArrayList<Integer> parcial = new ArrayList<>();
                parcial.add(stronglyConnectedComponents[i]);
                listaCiclos.put(stronglyConnectedComponents[i],parcial);
            } else{
                listaCiclos.get(stronglyConnectedComponents[i]).add(i);
            }
        }
        return listaCiclos;
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

            matrixInit();
            UserHandler.packagesAreLoaded = true;
            numberOfPackages = 0;
            saxParser.parse(inputFile, userhandler);

            //Inicializacion de Tarjan
            Tarjan t = new Tarjan(dependenciesMatrix);

            //Limpiar Diagonal
            for (int i = 0; i<numberOfPackages;i++)
                dependenciesMatrix[i][i] = false;


            HashMap<Integer, ArrayList> listaCiclos = getCycleListFromTarjan(t); //Crear listas de ciclos
            deleteCicles(listaCiclos, 3); //Eliminar listas de ciclos con menos de 3 componenetes

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