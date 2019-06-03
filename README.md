# Introducción

Debido al desarrollo en gran escala de un proyecto de software, es casi imposible evitar que al aumentar en tamaño y complejidad, se generen dependencias innecesarias entre ciertos porciones del proyecto. Esto genera:
- Acoplamiento innecesario en el proyecto
- Dificulta la comprensión y el mantenimiento del proyecto
- Degrada la calidad del diseño

El objetivo de este software es detectar los ciclos de dependencias a partir de un archivo XML, en el cual se detallan las dependencias de un sistema de software. Este proyecto fue desarrollado para la optativa "Taller de Programación en Java" de la carrera de [Ingeniería de Sistemas](https://www.exa.unicen.edu.ar/es/estudios/carreras-grado/ing-sistemas), de la Universidad Nacional del Centro de la Provincia de Buenos Aires, por los alumnos:
- Tomás García ([@reloadedhead](https://github.com/reloadedhead))
- Matías Gallo ([@MatiasGallo](https://github.com/MatiasGallo))
- Manuel Kittlein ([@MKittlein](https://github.com/Mkittlein))

# Desarrollo
## Parser
El software utilizado para poder levantar el archivo XML y poder parsear cada uno de los componentes de su estructura. En esta implementación se optó por utilizar el SAX Parser (Simple API for XML), ya que al ser un parser basado en eventos, no genera un árbol de parsing, como si lo hace el DOM parser. De esta forma, al levantar archivos XML con un cuerpo denso, se ahorra el espacio de memoria que se dedicaría al árbol de parsing. 
Una de las cualidades del SAX Parser es su funcionamiento lineal. El parser recorrerá desde el inicio al fin del archivo, terminando en la clausura del elemento root. Secuencialmente, el SAX notifica a medida que encuentra los tokens en el XML. Al identificar tokens, UserHandler toma el control para ejecutar acciones correspondientes.
A pesar de su poco uso de memoria en relación al DOM parser, la desventaja del SAX frente a este problema propuesto se da al momento de llevar historia de los datos en el XML. El SAX parser no posee mecanismos de seguimiento de datos, los cuales debieron ser implementados.

## Algoritmo
Se determinó el uso del algoritmo de Tarjan para componentes fuertemente conectados.
> It runs in linear time, matching the time bound for alternative methods including Kosaraju's algorithm and the path-based strong component algorithm. Tarjan's algorithm is named for its inventor, Robert Tarjan.”

Además, su implementación en una matriz de adyacencia es sencilla.
El algoritmo retorna, en forma de arreglo todos los ciclos. En un arreglo, llamado cycleList, del mismo tamaño que la cantidad de nodos, coloca en cada posición un número indicando a qué ciclo pertenece, por lo tanto si cycleList[i] == cycleList[j] los nodos i y j se encuentra en un ciclo de dependencia. Luego de esto con la función removeIf se eliminan los ciclos con cantidad menor a 3 nodos.
Finalmente se coloca en un archivo de texto cada ciclo con sus respectivos nodos.

# ¿Cómo se mejoraría este proyecto?
Además de haber aplicado varios conceptos estudiados a lo largo de la materia, este proyecto podría beneficiarse con su adaptación a ejecución en multi-threads. Dado que el tiempo de ejecución para cada archivo de dependencias provisto no fue más de 2-4 segundos, esta optimización fue descartada. Sin embargo, para sistemas incluso más grandes, la ejecución en múltiples hilos de, por ejemplo, el algoritmo de Tarjan al recorrer la matriz de adyacencia, beneficiaría al tiempo de ejecución considerablemente. 
