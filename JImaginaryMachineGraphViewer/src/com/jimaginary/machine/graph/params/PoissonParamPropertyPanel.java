/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jimaginary.machine.graph.params;

import com.jimaginary.machine.graph.viewer.ParameterPropertyEditor;
import com.jimaginary.machine.math.MathFunction;
import java.awt.BorderLayout;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 *
 * @author simonkenny
 */
public class PoissonParamPropertyPanel extends javax.swing.JPanel {
    private final int PARAM_MEAN = 0;
    private final int PARAM_MAX = 1;
    
    private final String []idxNames;
    private final MathFunction mathFunc;
    
    private final ParameterPropertyEditor editor;
    private ChartPanel chartPanel;
    
    /**
     * Creates new form PoissonParamPropertyPanel
     */
    public PoissonParamPropertyPanel(String []idxNames, MathFunction mathFunc, ParameterPropertyEditor editor) {
        initComponents();
        this.idxNames = idxNames;
        this.editor = editor;
        this.mathFunc = mathFunc;
        jTextFieldMean.setText(""+mathFunc.getParameter(PARAM_MEAN));
        jSliderMean.setValue((int)((mathFunc.getParameter(PARAM_MEAN)/mathFunc.getParameter(PARAM_MAX))*100.f));
        jTextFieldMax.setText(""+mathFunc.getParameter(PARAM_MAX));
        initChart();
    }
    
    private void initChart() {
        /*
        XYSeries series = new XYSeries("Distrbution");
        for( int i = 0 ; i < poisson.getParameter(PARAM_MAX) ; i++ ) {
            series.add(i, poisson.probMassOrDensity((float)i));
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        
        // Generate the graph
        JFreeChart chart = ChartFactory.createXYLineChart(
            "Poisson Distribution (PMF)",
            poisson.getParamName(PARAM_MEAN),
            "probability",
            dataset,
            PlotOrientation.VERTICAL,  // Plot Orientation
            true,                      // Show Legend
            true,                      // Use tooltips
            false                      // Configure chart to generate URLs?
            );
        // add annotations if we have them
        if( idxNames != null ) {
            System.out.println("add annotations");
            XYPlot plot = chart.getXYPlot();
            for( int i = 0 ; i < poisson.getParameter(PARAM_MAX) ; i++ ) {
                XYTextAnnotation an = new XYTextAnnotation(idxNames[i], i, poisson.probMassOrDensity((float)i));
                plot.addAnnotation(an);
            }
        } else {
            System.out.println("not adding annotations");
        }
        */
        
        // BAR CHART
        DefaultCategoryDataset chartData = new DefaultCategoryDataset();
        if( idxNames != null ) {
            for( int i = 0 ; i < idxNames.length ; i++ ) {
                chartData.setValue(mathFunc.probMassOrDensity((float)i),mathFunc.getParamName(PARAM_MEAN),idxNames[i]);
            }
        } else {
            for( int i = 0 ; i < mathFunc.getParameter(PARAM_MAX) ; i++ ) {
                chartData.setValue(mathFunc.probMassOrDensity((float)i),mathFunc.getParamName(PARAM_MEAN),""+i);
            }
        }
        JFreeChart chart = ChartFactory.createBarChart(
            "Poisson Distribution (PMF)",
            mathFunc.getParamName(PARAM_MEAN),
            "probability",
            chartData,         //Chart Data 
            PlotOrientation.VERTICAL, // orientation
            true,             // include legend?
            true,             // include tooltips?
            false             // include URLs?
        );
        
        if( chartPanel != null ) {
            jPanelChart.remove(chartPanel);
        }
        chartPanel = new ChartPanel(chart);
        jPanelChart.setLayout(new java.awt.BorderLayout());
        jPanelChart.add(chartPanel,BorderLayout.CENTER);
        jPanelChart.validate();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanelChart = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jTextFieldMean = new javax.swing.JTextField();
        jTextFieldMax = new javax.swing.JTextField();
        jSliderMean = new javax.swing.JSlider();

        setBackground(new java.awt.Color(255, 255, 255));

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(PoissonParamPropertyPanel.class, "PoissonParamPropertyPanel.jLabel1.text")); // NOI18N

        jPanelChart.setBackground(new java.awt.Color(204, 204, 204));
        jPanelChart.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        javax.swing.GroupLayout jPanelChartLayout = new javax.swing.GroupLayout(jPanelChart);
        jPanelChart.setLayout(jPanelChartLayout);
        jPanelChartLayout.setHorizontalGroup(
            jPanelChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanelChartLayout.setVerticalGroup(
            jPanelChartLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 196, Short.MAX_VALUE)
        );

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(PoissonParamPropertyPanel.class, "PoissonParamPropertyPanel.jLabel2.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(PoissonParamPropertyPanel.class, "PoissonParamPropertyPanel.jLabel3.text")); // NOI18N

        jTextFieldMean.setText(org.openide.util.NbBundle.getMessage(PoissonParamPropertyPanel.class, "PoissonParamPropertyPanel.jTextFieldMean.text")); // NOI18N
        jTextFieldMean.setEnabled(false);

        jTextFieldMax.setText(org.openide.util.NbBundle.getMessage(PoissonParamPropertyPanel.class, "PoissonParamPropertyPanel.jTextFieldMax.text")); // NOI18N
        jTextFieldMax.setEnabled(false);

        jSliderMean.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSliderMeanStateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanelChart, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextFieldMax, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextFieldMean, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jSliderMean, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanelChart, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jTextFieldMean, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jSliderMean, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldMax, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jSliderMeanStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSliderMeanStateChanged
        mathFunc.setParameter(PARAM_MEAN, ((float)jSliderMean.getValue()/100.f)*mathFunc.getParameter(PARAM_MAX) );
        jTextFieldMean.setText(""+mathFunc.getParameter(PARAM_MEAN));
        editor.setAsText(mathFunc.toString());
        initChart();
    }//GEN-LAST:event_jSliderMeanStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanelChart;
    private javax.swing.JSlider jSliderMean;
    private javax.swing.JTextField jTextFieldMax;
    private javax.swing.JTextField jTextFieldMean;
    // End of variables declaration//GEN-END:variables
}
