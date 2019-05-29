package Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ReportGenerator {
	private File reportFile;

	public ReportGenerator(String path) {
		reportFile = new File(path);
	}

	public void generateReport(ArrayList<ArrayList<Integer>> cycleList, String sysName, HashMap<Integer, String> packageNames){
		int cycleNumber = 1;
		try{
			if (reportFile.createNewFile()){
				FileWriter writer = new FileWriter(reportFile);
				StringBuilder line = new StringBuilder();
				line.append(new StringBuilder("Ciclos de dependencias para el sistema ").append(sysName));
				line.append(":");
				for (ArrayList<Integer> cycle:cycleList) {
					line.append(System.lineSeparator());
					line.append("- Ciclo ");
					line.append(cycleNumber);
					line.append(System.lineSeparator());
					for(Integer node:cycle){
						line.append("	- ");
						line.append(packageNames.get(node));
						line.append(System.lineSeparator());
					}
					cycleNumber++;
				}

				writer.write(String.valueOf(line));
				writer.close();
			}
		} catch (IOException e){
			System.out.println("Error abriendo el archivo.");
		}
	}
}