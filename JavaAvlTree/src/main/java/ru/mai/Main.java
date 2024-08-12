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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    private static final int MAX_OPERATIONS = 100000;
    private static final int STEP = 100;
    private static final Random random = new Random();


    public static void main(String[] args) {
//        testAvlTree();
        perfomanceCompareTest();
    }

    public static void testAvlTree() {
        for (int i = 0; i < 1000; i++) {
            AssociativeContainer<Integer, Integer> avlTree = new AvlTreeHeight<>();
            List<Integer> keys = new ArrayList<>();

            try {
                for (int j = 0; j < 100000; j++) {
                    int key = getRandomKey();

                    if (avlTree.insert(key, 1)) {
                        keys.add(key);
                    }
                }

                for (int j = 0; j < keys.size(); j++) {
                    avlTree.delete(keys.get(j));
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
                System.out.println(keys);
            }
        }
    }

    private static int getRandomKey() {
        return random.nextInt(100);
    }

    private static void perfomanceCompareTest() {
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

        displayChart("Insert Operation Performance", "Number of Operations", "Time (ns)", heightInsertSeries, factorInsertSeries);
        displayChart("Find Operation Performance", "Number of Operations", "Time (ns)", heightFindSeries, factorFindSeries);
        displayChart("Update Operation Performance", "Number of Operations", "Time (ns)", heightUpdateSeries, factorUpdateSeries);
        displayChart("Delete Operation Performance", "Number of Operations", "Time (ns)", heightDeleteSeries, factorDeleteSeries);
    }

    private static long measureInsertPerformance(AssociativeContainer<Integer, String> tree, int numOperations) {
        long startTime = System.nanoTime();
        for (int i = 0; i < numOperations; i++) {
            tree.insert(i, "Value " + i);
        }
        return System.nanoTime() - startTime;
    }

    private static long measureFindPerformance(AssociativeContainer<Integer, String> tree, int numOperations) {
        Random rand = new Random();
        long startTime = System.nanoTime();
        for (int i = 0; i < numOperations; i++) {
            tree.find(rand.nextInt(numOperations));
        }
        return System.nanoTime() - startTime;
    }

    private static long measureUpdatePerformance(AssociativeContainer<Integer, String> tree, int numOperations) {
        Random rand = new Random();
        long startTime = System.nanoTime();
        for (int i = 0; i < numOperations; i++) {
            tree.update(rand.nextInt(numOperations), "Updated Value " + i);
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

    private static void displayChart(String title, String xAxisLabel, String yAxisLabel, XYSeries heightSeries, XYSeries factorSeries) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(heightSeries);
        dataset.addSeries(factorSeries);

        JFreeChart chart = ChartFactory.createXYLineChart(
                title, xAxisLabel, yAxisLabel, dataset,
                PlotOrientation.VERTICAL, true, true, false);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
        JFrame frame = new JFrame(title);
        frame.setContentPane(chartPanel);
        frame.pack();
        frame.setVisible(true);
    }
}
