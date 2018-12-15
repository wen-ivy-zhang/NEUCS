package wenzhang.cs6650.homework4.client;

import java.io.*;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.ChartUtilities;


public class JfreeChart {
	public XYSeries WDDMThroughput;
	
	public XYSeries createXYSeries() {
		if (this.WDDMThroughput == null) {
			this.WDDMThroughput = new XYSeries("Wearable Device Data Management");
		}
		
		return this.WDDMThroughput;
	}
	
	public void addPoint(Integer x, Integer y) {
		this.WDDMThroughput.add(x, y);
	}
	
	public boolean createChart(int numThreads, int numTests) {
		try {
			XYSeriesCollection dataset = new XYSeriesCollection();
			dataset.addSeries(this.WDDMThroughput);
			
			String title = "Overall Throughput of " + numThreads + " Threads, each over " + numTests + " tests per phase";
			String xAxisLabel = "timestamp (seconds)";
			String yAxisLabel = "requests (num)";
			JFreeChart xylineChart = ChartFactory.createXYLineChart(title, xAxisLabel, yAxisLabel, dataset, PlotOrientation.VERTICAL, true, true, false);
		
		    int width = 1280;   /* Width of the image */
		    int height = 960;  /* Height of the image */ 
		    File XYChart = new File("WDDMThroughput.jpeg"); 
		    ChartUtilities.saveChartAsJPEG( XYChart, xylineChart, width, height);
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

}