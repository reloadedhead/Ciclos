package Parser;

import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.SAXParserFactory;
import Algoritmos.*;
import Utils.*;

/**
 * Clase de SAX. Se encarga de levantar el XML. Por ahrora levanta solo uno, indicado en el path del metodo main.
 */
public class SAX {
    /**
     * Contador de numero total de paquetes, usado para crear matriz y como fila actual
     */
    static int numberOfPackages=0;

    private static Integer[] idNames;

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
        idNames = new Integer[numberOfPackages];
    }

    private static void arrayCarga(){
        for(int i = 0; i < idNames.length ; i++)
            idNames[i] = i;
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

    private static void setNullArrayList(ArrayList<ArrayList<Integer>> array, int size){
        for(int i = 0; i < size; i++){
            array.add(i, null);
        }
    }

    /**
     * Este método devuelve un arreglo de arreglos, donde cada "sub arreglo" corresponde a un ciclo. Lo que hace es
     * iterar sobre el arreglo de componentes fuertemente conectadas que devuelve el algoritmo de Tarjan, el cual
     * tiene de tamaño la cantidad de paquetes y en A[i] indica a qué número de ciclo pertenece el paquete i.
     * @param t objeto Tarjan
     * @return Lista de ciclos, cada ciclo es una lista de paquetes.
     */



    /**
     * Este método devuelve si dos nodos pertenecen a un mismo ciclo.
     * @param nodo1 el indice del primero nodo
     * @param nodo2 el indice del segundo nodo
     * @param cycleList arraylist que contiene todos los ciclos de dependencia
     * @return verdadero si ambos nodos estan en un ciclo
     */
    private boolean nodesBelongToSameCycle(int nodo1,int nodo2, ArrayList<ArrayList<Integer>> cycleList){
        for (ArrayList<Integer> cycle:cycleList) {
            if (cycle.contains(nodo1))
                if (cycle.contains(nodo2))
                    return true;
        }
        return false;
    }


    /**
     * Levanta el XML del path indicado al crear el objeto File. Después llama al parser con los parametros indicados
     * en UserHandler. Hace dos pasadas, una para levantar todos los paquetes del sistema y poder tener el tamaño de la
     * matriz, y la segunda pasada la hace para establecer las dependencias en la matriz.
     * @param args argumentos del main. AL final vuela.
     */
    public static void main(String[] args) {
        try {
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); //Se rompe en OSX
            JFileChooser fc = new JFileChooser("./dependencias");
            fc.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return (f.getName().endsWith(".odem"))|| f.isDirectory(); //Para poder navegar las carpetas hasta encontrar los .odem tiene que también aceptar carpetas
                }
                @Override
                public String getDescription() {
                    return "*.odem";
                }
            });
            fc.setDialogTitle("Seleccione archivo .odem");
            fc.showOpenDialog(null);

            if (fc.getSelectedFile()!=null)
            {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            javax.xml.parsers.SAXParser saxParser = factory.newSAXParser();
            File inputFile = fc.getSelectedFile();//
            UserHandler userhandler = new UserHandler();

            saxParser.parse(inputFile, userhandler);

            matrixInit();
            arrayCarga();
            UserHandler.packagesAreLoaded = true;
            numberOfPackages = 0;
            saxParser.parse(inputFile, userhandler);

            Johnson j = new Johnson(dependenciesMatrix, idNames);

            List<List<Object>> result = j.getElementaryCycles();

            for (int i = 0; i < result.size(); i++){
                if (result.get(i).size() < 3) result.remove(i);
            }


            System.out.println("Ciclos encontrados: "+result.size());
            System.out.println("Generando Lista de Ciclos...");

            for (int i = 0; i<numberOfPackages;i++)
                dependenciesMatrix[i][i] = false;

            ReportGenerator reporter = new ReportGenerator("."+File.separator+inputFile.getName()+"- Lista de ciclos.txt");
            reporter.generateReport(result, inputFile.getName(), InvReferencia);
            System.out.println("Creado archivo de salida \""+reporter.getReportName()+"\" en "+reporter.getReportPath());
              }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
