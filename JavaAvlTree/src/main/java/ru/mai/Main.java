package ru.mai;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import ru.mai.associative_container.AssociativeContainer;
import ru.mai.associative_container.impl.AvlTreeFactor;
import ru.mai.associative_container.impl.AvlTreeHeight;

import javax.swing.*;
import java.util.Random;

public class Main {

    private static final int MAX_OPERATIONS = 100000;
    private static final int STEP = 100;
    private static final Random random = new Random();


    public static void main(String[] args) {
        performanceCompareTest();
    }

    private static void performanceCompareTest() {
        XYSeries heightInsertSeries = new XYSeries("AvlTreeHeight - Insert");
        XYSeries factorInsertSeries = new XYSeries("AvlTreeFactor - Insert");

        XYSeries heightFindSeries = new XYSeries("AvlTreeHeight - Find");
        XYSeries factorFindSeries = new XYSeries("AvlTreeFactor - Find");

        XYSeries heightUpdateSeries = new XYSeries("AvlTreeHeight - Update");
        XYSeries factorUpdateSeries = new XYSeries("AvlTreeFactor - Update");

        XYSeries heightDeleteSeries = new XYSeries("AvlTreeHeight - Delete");
        XYSeries factorDeleteSeries = new XYSeries("AvlTreeFactor - Delete");

        for (int numOperations = STEP; numOperations <= MAX_OPERATIONS; numOperations += STEP) {
            AssociativeContainer<Integer, String> treeHeight = new AvlTreeHeight<>();
            AssociativeContainer<Integer, String> treeFactor = new AvlTreeFactor<>();

            long heightInsertTime = measureInsertPerformance(treeHeight, numOperations);
            long factorInsertTime = measureInsertPerformance(treeFactor, numOperations);

            long heightFindTime = measureFindPerformance(treeHeight, numOperations);
            long factorFindTime = measureFindPerformance(treeFactor, numOperations);

            long heightUpdateTime = measureUpdatePerformance(treeHeight, numOperations);
            long factorUpdateTime = measureUpdatePerformance(treeFactor, numOperations);

            long heightDeleteTime = measureDeletePerformance(treeHeight, numOperations);
            long factorDeleteTime = measureDeletePerformance(treeFactor, numOperations);

            heightInsertSeries.add(numOperations, heightInsertTime);
            factorInsertSeries.add(numOperations, factorInsertTime);

            heightFindSeries.add(numOperations, heightFindTime);
            factorFindSeries.add(numOperations, factorFindTime);

            heightUpdateSeries.add(numOperations, heightUpdateTime);
            factorUpdateSeries.add(numOperations, factorUpdateTime);

            heightDeleteSeries.add(numOperations, heightDeleteTime);
            factorDeleteSeries.add(numOperations, factorDeleteTime);
        }

        displayChart("Insert Operation Performance", heightInsertSeries, factorInsertSeries);
        displayChart("Find Operation Performance", heightFindSeries, factorFindSeries);
        displayChart("Update Operation Performance", heightUpdateSeries, factorUpdateSeries);
        displayChart("Delete Operation Performance", heightDeleteSeries, factorDeleteSeries);
    }

    private static long measureInsertPerformance(AssociativeContainer<Integer, String> tree, int numOperations) {
        long startTime = System.nanoTime();
        for (int i = 0; i < numOperations; i++) {
            tree.insert(i, "Value " + i);
        }
        return System.nanoTime() - startTime;
    }

    private static long measureFindPerformance(AssociativeContainer<Integer, String> tree, int numOperations) {
        long startTime = System.nanoTime();
        for (int i = 0; i < numOperations; i++) {
            tree.find(random.nextInt(numOperations));
        }
        return System.nanoTime() - startTime;
    }

    private static long measureUpdatePerformance(AssociativeContainer<Integer, String> tree, int numOperations) {
        long startTime = System.nanoTime();
        for (int i = 0; i < numOperations; i++) {
            tree.update(random.nextInt(numOperations), "Updated Value " + i);
        }
        return System.nanoTime() - startTime;
    }

    private static long measureDeletePerformance(AssociativeContainer<Integer, String> tree, int numOperations) {
        long startTime = System.nanoTime();
        for (int i = 0; i < numOperations; i++) {
            tree.delete(i);
        }
        return System.nanoTime() - startTime;
    }

    private static void displayChart(String title, XYSeries heightSeries, XYSeries factorSeries) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(heightSeries);
        dataset.addSeries(factorSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                title, "Number of Operations", "Time (ns)", dataset,
                PlotOrientation.VERTICAL, true, true, false);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        JFrame frame = new JFrame(title);
        frame.setContentPane(chartPanel);
        frame.pack();
        frame.setVisible(true);
    }
}
