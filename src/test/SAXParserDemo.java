package test;

import Algoritmos.Tarjan;
import java.io.File;
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
    private static HashMap<String,Integer> referencia = new HashMap<>();
    private static HashMap<Integer,String> InvReferencia = new HashMap<>();

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
     * @param packWClass String completo (con el nombre del componente)
     * @return nombre del paquete sin el componente.
     */
    private static String getPackage(String packWClass){
        int lastDot = 0;
        StringBuilder pack = new StringBuilder(packWClass);
        for(int i = 0; i < pack.length(); i++){
            if (pack.charAt(i) == '.'){
                lastDot = i;
            }
        }
        return pack.substring(0,(lastDot));
    }

    /**
     * Llamado desde Userhandler al encontrar un paquete valido. Cantpack es aumentado desde UserHandler, marca el
     * paquete actual y la fila actual de la matriz
     * @param packDep nombre del paquete dependencia encontrada.
     */
    static void fillMatrix(String packDep){
        if ((referencia.containsKey(getPackage(packDep)))){
            dependenciesMatrix[numberOfPackages-1][referencia.get(getPackage(packDep))] = true;
        }
    }


    /**
     * Este método devuelve un arreglo de arreglos, donde cada "sub arreglo" corresponde a un ciclo. Lo que hace es
     * iterar sobre el arreglo de componentes fuertemente conectadas que devuelve el algoritmo de Tarjan, el cual
     * tiene de tamaño la cantidad de paquetes y en A[i] indica a qué número de ciclo pertenece el paquete i.
     * @param t objeto Tarjan
     * @return Lista de ciclos, cada ciclo es una lista de paquetes.
     */
    private static ArrayList<ArrayList<Integer>> getCyclesFromTarjan(Tarjan t){
        ArrayList<ArrayList<Integer>> dependencyCycles = new ArrayList<>();
        int[] stronglyConnectedComponents = t.getStronglyConnectedComponents();
        for (int i = 0; i<stronglyConnectedComponents.length; i++) {
            if (dependencyCycles.get(stronglyConnectedComponents[i]) == null){
                dependencyCycles.add(stronglyConnectedComponents[i], new ArrayList<>());
                dependencyCycles.get(i).add(i);
            } else {
                dependencyCycles.get(stronglyConnectedComponents[i]).add(i);
            }
        }
        return dependencyCycles;
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

            Tarjan t = new Tarjan(dependenciesMatrix);

            for (int i = 0; i<numberOfPackages;i++)
                dependenciesMatrix[i][i] = false;


            ArrayList<ArrayList<Integer>> cycleList= getCyclesFromTarjan(t);
            cycleList.removeIf(cycle -> (cycle.size() < 3));

            ReportGenerator reporter = new ReportGenerator("."+File.separator+"Lista de ciclos");
            reporter.generateReport(cycleList, "Systema X");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}