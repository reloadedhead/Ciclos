package Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReportGenerator {
	private File reportFile;
	private String reportName;
	private String reportPath;

	public ReportGenerator(String path) {
		reportFile = new File(path);
		reportName=reportFile.getName();
		reportPath=reportFile.getAbsolutePath();
	}

	public void generateReport(List<List<Object>> cycleList, String sysName, HashMap<Integer, String> packageNames){
		int cycleNumber = 1;
		try{
			if (reportFile.createNewFile()){
				FileWriter writer = new FileWriter(reportFile);
				StringBuilder line = new StringBuilder();
				line.append(new StringBuilder("Ciclos de dependencias para el sistema ").append(sysName));
				line.append(":");
				for (int i = 0; i < cycleList.size(); i++){
					line.append(System.lineSeparator());
					line.append("- Ciclo ");
					line.append(i);
					line.append(System.lineSeparator());
					for (int j = 0; j < cycleList.get(i).size(); j++){
						line.append("	- ");
						line.append(packageNames.get(cycleList.get(i).get(j)));
						line.append(System.lineSeparator());
						writer.write(String.valueOf(line));
						line = new StringBuilder();
					}
				}
				writer.write(String.valueOf(line));
				writer.close();
			}
		} catch (IOException e){
			System.out.println("Error abriendo el archivo.");
		}
	}

	public String getReportName() {
		return reportName;
	}

	public String getReportPath() {
		return reportPath;
	}
}
