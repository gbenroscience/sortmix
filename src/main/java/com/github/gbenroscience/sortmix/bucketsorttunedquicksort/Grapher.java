/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.gbenroscience.sortmix.bucketsorttunedquicksort;

import com.github.gbenroscience.sortmix.Sortmix;
import com.github.gbenroscience.sortmix.experiments.AdvancedWittyBucketSort;
import com.github.gbenroscience.sortmix.experiments.DrawAdapter;
import com.github.gbenroscience.sortmix.experiments.HeapSort;
import com.github.gbenroscience.sortmix.experiments.MergeSort;
import com.github.gbenroscience.sortmix.experiments.QuickSort;
import com.github.gbenroscience.sortmix.experiments.ShellSort;
import com.github.gbenroscience.sortmix.util.SortUtils;
import com.github.gbenroscience.sortmix.experiments.SpeedyBucketSort;
import com.github.gbenroscience.sortmix.gui.AnalysisFrame;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import static java.lang.Math.round;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import static com.github.gbenroscience.sortmix.util.STRING.delete;
import com.github.gbenroscience.sortmix.util.Size;

/**
 *
 * @author GBEMIRO
 */
public class Grapher {

    private int plotMode = GRAPH;

    public static final int BAR = 1;
    public static final int GRAPH = 2;

    /**
     * The image that we plot the graph into.
     */
    private BufferedImage image;
    /**
     * Redraw the image whenever this field is changed.
     */
    private boolean refresh = true;

    /**
     * The thickness of the lines used to plot the data.
     */
    private final int plotThickness = 3;
    public ArrayList<Graphed> graphObjectList = new ArrayList<Graphed>();

    public static int arraySize = 100000;
    /**
     * Iterations to perform on computation of sort times for a given array
     * size.
     */
    public static int iterations = 1;
    /**
     * If true, the color of the bar graph is randomly selected
     */
    private static boolean randomSelectColor;
    /**
     * Width of graphing area.
     */
    private int width;
    /**
     * Height of graphing area.
     */
    private int height;
    /**
     * tracks the number of iterations done on all registered algorithms in the
     * ArrayList.
     */
    public static double netCount = 0;
    ;
    /**
     * The function to be plotted.
     * This function can be homogeneous i.e an instruction to plot a single curve or set of points,
     * or heterogeneous i.e instructions to plot a group of curves.
     *
     * For instance, an homogeneous instruction that plots a set of points
     * is [x1,x2,x3,.....:][y1,y2,y3,.......:] The array of numbers...x1,x2..
     * are a set of x coordinates, and the array of numbers y1,y2....are the corresponding set of y coordinates.
     *
     */
    //private String function;

/**
 * sets whether the grid lines
 * will be visible or not.
 */
   private boolean showGridLines;
    /**
     * sets whether the major axes are labeled with numbers or not
     */
    private boolean labelAxis;

    /**
     * If true,then the object will attempt to draw the graph based on a scale
     * that it generates automatically.
     */
    private boolean autoScaleOn = true;

    /**
     * determine the size of the small boxes that make up the grid.
     */
    private int gridSize;
    /**
     * the background color of the graph
     */
    private Color bgColor;
    /**
     * The color of the grid.
     */
    private Color gridColor;
    /**
     * The color of the major axes, x and y.
     */
    private Color majorAxesColor;

    /**
     * The color of the text .
     */
    private Color textColor = Color.BLACK;

    /**
     * The resolution of the graph along x. xStep is the equivalent in
     * calculation of the width of each grid box. So if gridSize=2 screen pixels
     * and xStep =0.01. Then a box of length 2 units corresponds to 0.01 units
     * in the user plot along x.
     */
    private double xStep = 100;
    /**
     * The resolution of the graph along y. yStep is the equivalent in
     * calculation of the height of each grid box. So if gridSize=2 and yStep
     * =0.01 Then a distance of 2 units on the graph corresponds to 0.01 units
     * in the user plot along y.
     */
    private double yStep = 0.01;
    /**
     * The length of the longer ticks used to mark off the graph.
     */
    private int majorTickLength;

    /**
     * The length of the shorter ticks used to mark off the graph
     */
    private int minorTickLength;
    /**
     * The point where the origin of the graph will reside on the screen.
     */
    private Point locationOfOrigin;

    /**
     * The text used to label the horizontal axis. By default , its value is X.
     */
    private String horizontalAxisLabel = "Sorting Algorithms";
    /**
     * The text used to label the vertical axis. By default , its value is Y.
     */
    private String verticalAxisLabel = "Sort Time(ms)";
    /**
     * The Font object used to write on the graph.
     */
    private Font font;
    /**
     * The scale suggested by the programmer. Can be modified by the user to see
     * more detail.
     */
    private Size defaultScale;

    /**
     * @param showGridLines sets whether the grid lines will be visible or not.
     * @param labelAxis is true if the axes should be labeled.
     * @param gridSize determine the size of the small boxes that make up the
     * grid.
     * @param gridColor The color of the grid.
     * @param majorTickLength The length of the longer ticks used to mark off
     * the graph.
     * @param minorTickLength The length of the shorter ticks used to mark off
     * the graph
     * @param majorAxesColor The color of the major axes, x and y.
     * @param upperXLimit The upper value of x up to which the graph will be
     * plotted.
     * @param lowerXLimit The lower value of x up to which the graph will be
     * plotted.
     * @param xStep The resolution of the graph along x.
     * @param yStep the resolution of the graph along y.
     * @param font the font used to display the graph.
     * @param component The component on which this grid will be laid.
     */
    public Grapher(boolean showGridLines, boolean labelAxis,
            int gridSize, Color gridColor, Color majorAxesColor,
            int majorTickLength, int minorTickLength,
            double lowerXLimit, double upperXLimit,
            double xStep, double yStep, Font font, JComponent component) {

        this.width = component.getWidth();
        this.height = component.getHeight();
        this.showGridLines = showGridLines;
        this.labelAxis = labelAxis;
        this.gridSize = gridSize;
        this.gridColor = gridColor;
        this.majorAxesColor = majorAxesColor;
        this.majorTickLength = majorTickLength;
        this.minorTickLength = minorTickLength;
        this.xStep = xStep;
        this.yStep = yStep;
        this.locationOfOrigin = new Point(20, component.getHeight() - 20);
        this.font = new Font("Calibri", Font.BOLD, 13);
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        plotMode = GRAPH;
    }//end constructor

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setIterations(int iterations) {
        this.refresh = Grapher.iterations != iterations;
        Grapher.iterations = iterations;
    }

    public static int getIterations() {
        return iterations;
    }

    public static void setArraySize(int arraySize) {
        Grapher.arraySize = arraySize;
    }

    public static int getArraySize() {
        return arraySize;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setWidth(int width) {
        this.refresh = this.width != width;
        this.width = width;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.refresh = this.height != height;
        this.height = height;
    }

    public void setPlotMode(int plotMode) {
        this.plotMode = plotMode;
    }

    public int getPlotMode() {
        return plotMode;
    }

    public boolean isBar() {
        return plotMode == BAR;
    }

    public boolean isGraph() {
        return plotMode == GRAPH;
    }

    public String getHorizontalAxisLabel() {
        return horizontalAxisLabel;
    }

    public void setHorizontalAxisLabel(String horizontalAxisLabel) {
        this.refresh = !(this.horizontalAxisLabel.equals(horizontalAxisLabel));
        this.horizontalAxisLabel = horizontalAxisLabel;
    }

    public String getVerticalAxisLabel() {
        return verticalAxisLabel;
    }

    public void setVerticalAxisLabel(String verticalAxisLabel) {
        this.refresh = !(this.verticalAxisLabel.equals(verticalAxisLabel));
        this.verticalAxisLabel = verticalAxisLabel;
    }

    public void setDefaultScale(Size defaultScale) {
        this.refresh = (this.defaultScale != defaultScale);
        this.defaultScale = defaultScale;
    }

    public boolean isAutoScaleOn() {
        return autoScaleOn;
    }

    public void setAutoScaleOn(boolean autoScaleOn) {
        this.refresh = (this.autoScaleOn != autoScaleOn);
        this.autoScaleOn = autoScaleOn;
    }

    public Size getDefaultScale() {
        return defaultScale;
    }

    public static void setRandomSelectColor(boolean randomSelectColor) {
        Grapher.randomSelectColor = randomSelectColor;
    }

    public static boolean isRandomSelectColor() {
        return randomSelectColor;
    }

    public void setFont(Font font) {
        this.refresh = this.font != font;
        this.font = font;
    }

    public Font getFont() {
        return font;
    }

    public void setTextColor(Color textColor) {
        this.refresh = this.textColor != textColor;
        this.textColor = textColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    public Color getGridColor() {
        return gridColor;
    }

    public void setGridColor(Color gridColor) {
        this.refresh = this.gridColor != gridColor;
        this.gridColor = gridColor;
    }

    public int getGridSize() {
        return gridSize;
    }

    public void setGridSize(int gridSize) {
        this.refresh = this.gridSize != gridSize;
        this.gridSize = gridSize;
    }

    public Point getLocationOfOrigin() {
        return locationOfOrigin;
    }

    public void setLocationOfOrigin(int x, int y) {
        this.refresh = (this.locationOfOrigin.x != x) || (this.locationOfOrigin.y != y);
        this.locationOfOrigin = new Point(x, y);
    }

    public void setLocationOfOrigin(Point locationOfOrigin) {
        this.refresh = (this.locationOfOrigin.x != locationOfOrigin.x) || (this.locationOfOrigin.y != locationOfOrigin.y);
        this.locationOfOrigin = locationOfOrigin;
    }

    public Color getMajorAxesColor() {
        return majorAxesColor;
    }

    public void setMajorAxesColor(Color majorAxesColor) {
        this.refresh = this.majorAxesColor != majorAxesColor;
        this.majorAxesColor = majorAxesColor;
    }

    public int getMajorTickLength() {
        return majorTickLength;
    }

    public void setMajorTickLength(int majorTickLength) {
        this.refresh = this.majorTickLength != majorTickLength;
        this.majorTickLength = majorTickLength;
    }

    public int getMinorTickLength() {
        return minorTickLength;
    }

    public void setMinorTickLength(int minorTickLength) {
        this.refresh = this.minorTickLength != minorTickLength;
        this.minorTickLength = (minorTickLength < (majorTickLength / 2.0)) ? minorTickLength : majorTickLength / 2;
    }

    public boolean isShowGridLines() {
        return showGridLines;
    }

    public void setShowGridLines(boolean showGridLines) {
        this.refresh = this.showGridLines != showGridLines;
        this.showGridLines = showGridLines;
    }

    public void setBgColor(Color bgColor) {
        this.refresh = this.bgColor != bgColor;
        this.bgColor = bgColor;
    }

    public double getxStep() {
        return xStep;
    }

    public void setxStep(double xStep) {
        this.refresh = this.xStep != xStep;
        this.xStep = Math.abs(xStep);
    }

    public void setyStep(double yStep) {
        this.refresh = this.yStep != yStep;
        this.yStep = Math.abs(yStep);
    }

    public double getyStep() {
        return yStep;
    }

    public void setLabelAxis(boolean labelAxis) {
        this.refresh = this.labelAxis != labelAxis;
        this.labelAxis = labelAxis;
    }

    public boolean isLabelAxis() {
        return labelAxis;
    }

    /**
     * TYty
     *
     * @param g Adds a <code>Graphed</code> object to the list of objects to be
     * graphed.
     */
    public void addGraphed(Graphed g) {
        int sz = graphObjectList.size();
        for (int i = 0; i < sz; i++) {
            Graphed gr = graphObjectList.get(i);
            if (g.algorithm == gr.algorithm) {
                if (isBar()) {
                    gr.generator();
                } else if (isGraph()) {
                    gr.getGraphPoints();
                }
                return;
            }
        }
        if (isBar()) {
            g.generator();
        } else if (isGraph()) {
            g.getGraphPoints();
        }
        this.refresh = true;
        graphObjectList.add(g);
    }//end method.

    /**
     * TYty
     *
     * @param algorithm Removes a <code>Graphed</code> object from the list of
     * objects to be graphed.
     */
    public void removeGraphed(int algorithm) {
        int sz = graphObjectList.size();
        for (int i = 0; i < sz; i++) {
            try {
                Graphed g = graphObjectList.get(i);
                if (g.algorithm == algorithm) {
                    graphObjectList.remove(g);
                }
            }//end try
            catch (IndexOutOfBoundsException boundsException) {
            }
        }
        this.refresh = true;
    }//end method.

    public void clearAllGraphed() {
        graphObjectList.clear();
        this.refresh = true;
    }//end method.
//TYty

    /**
     * draws the grid
     */
    public void draw() {
        if (refresh) {

            Graphics2D g = image.createGraphics();
            refresh = false;
            try {
                g.setFont(font);
                DrawAdapter art = new DrawAdapter();
                g.setColor(bgColor);
                g.fill3DRect(0, 0, width, height, true);
                drawGrid:
                {
                    if (isShowGridLines()) {

                        drawHorizontalLines:
                        {

                            g.setColor(gridColor);
                            int y = 0;

                            while (y < height) {
                                g.drawLine(0, y, width, y);
                                y += gridSize;
                            }
                        }

                        drawVerticalLines:
                        {
                            int x = 0;
                            while (x < width) {
                                g.drawLine(x, 0, x, height);
                                x += gridSize;
                            }
                        }

                    }
                }//end drawGrid
                art.fillOval(g, locationOfOrigin.x, locationOfOrigin.y, gridSize, gridSize);
                drawMajorAxes:
                {
                    Point compLoc = new Point(0, 0);
                    Dimension compSize = new Dimension(width, height);
                    int xLocOfRightSideOfComponent = compLoc.x + compSize.width;
                    int yLocOfBottomSideOfComponent = compLoc.y + compSize.height;

                    int xLocOfLeftSideOfComponent = compLoc.x;
                    int yLocOfTopSideOfComponent = compLoc.y;
                    art = new DrawAdapter();
                    drawXAxes:
                    {
                        g.setColor(majorAxesColor);
                        g.drawLine(0, locationOfOrigin.y, xLocOfRightSideOfComponent, locationOfOrigin.y);
                        g.drawLine(0, locationOfOrigin.y + 1, xLocOfRightSideOfComponent, locationOfOrigin.y + 1);//make the line thick

                        drawTicksOnXaxis:
                        {
                            int x = locationOfOrigin.x;

                            int countTicks = 0;

//draw to the right of the y axis
                            while (x < xLocOfRightSideOfComponent) {

                                //indicate the name of the horizontal axis.
                                if (labelAxis) {
                                    FontMetrics fm = Sortmix.analysisFrame.getGraphPanel().getFontMetrics(font);
                                    int wid = fm.stringWidth(horizontalAxisLabel + "→");
                                    g.drawString(horizontalAxisLabel + "→", compSize.width - wid, locationOfOrigin.y + majorTickLength + 40);
                                }

//draw the major tick lengths
                                if (countTicks == 0) {
                                    g.setColor(textColor);
                                    g.drawLine(x, locationOfOrigin.y, x, locationOfOrigin.y + majorTickLength);

                                    String numLabel = String.valueOf(convertScreenPointToGraphPoint(new Point(x, locationOfOrigin.y)).x);
                                    if (!numLabel.equals("0.0")) {
                                        if (labelAxis) {
                                            g.drawString(numLabel, x, locationOfOrigin.y + majorTickLength + 10);
                                        }
                                    }
                                }//end if
                                else {
                                    g.setColor(textColor);
                                    g.drawLine(x, locationOfOrigin.y, x, locationOfOrigin.y + minorTickLength);
                                }//end if
                                x += (gridSize * 2);
                                ++countTicks;
                                if (countTicks == 5) {
                                    countTicks = 0;
                                }
                            }//end while

                            countTicks = 0;
                            x = locationOfOrigin.x;

//now draw to the left of the y axis
                            while (x + compLoc.x > xLocOfLeftSideOfComponent) {
//draw the major tick lengths
                                if (countTicks == 0) {
                                    g.setColor(textColor);
                                    g.drawLine(x, locationOfOrigin.y, x, locationOfOrigin.y + majorTickLength);
                                    String numLabel = String.valueOf(convertScreenPointToGraphPoint(new Point(x, locationOfOrigin.y)).x);
                                    if (!numLabel.equals("0.0")) {
                                        if (labelAxis) {
                                            g.drawString(numLabel, x, locationOfOrigin.y + majorTickLength + 10);
                                        }
                                    }
                                }//end if
                                else {
                                    g.setColor(textColor);
                                    g.drawLine(x, locationOfOrigin.y, x, locationOfOrigin.y + minorTickLength);
                                }//end if
                                x -= (gridSize * 2);
                                ++countTicks;
                                if (countTicks == 5) {
                                    countTicks = 0;
                                }
                            }//end while

                        }//end inner label

                    }//end outer label

                    drawYAxes:
                    {

                        g.setColor(majorAxesColor);
                        g.drawLine(locationOfOrigin.x, 0, locationOfOrigin.x, yLocOfBottomSideOfComponent);
                        g.drawLine(locationOfOrigin.x + 1, 0, locationOfOrigin.x + 1, yLocOfBottomSideOfComponent);//make the line thick

                        int y = locationOfOrigin.y + compLoc.y;

                        int countTicks = 0;
//draw to the right of the y axis
                        while (y > yLocOfTopSideOfComponent) {
                            //label the vertical axis.
                            if (labelAxis) {
                                g.setColor(textColor);
                                g.drawString(verticalAxisLabel + "↑", locationOfOrigin.x + majorTickLength + 50, yLocOfTopSideOfComponent + 50);
                                String text = "Array Size = " + arraySize;
                                FontMetrics fm = g.getFontMetrics(font);
                                g.drawString(text, xLocOfRightSideOfComponent - fm.stringWidth(text) - 30,
                                        yLocOfTopSideOfComponent + fm.getHeight() + 30);
                            }

//draw the major tick lengths
                            if (countTicks == 0) {
                                g.setColor(majorAxesColor);
                                g.drawLine(locationOfOrigin.x, y, locationOfOrigin.x + majorTickLength, y);
                                String numLabel = String.valueOf(convertScreenPointToGraphPoint(new Point(locationOfOrigin.x, y)).y);
                                if (!numLabel.equals("0.0")) {
                                    if (labelAxis) {
                                        g.setColor(textColor);
                                        g.drawString(numLabel, locationOfOrigin.x + majorTickLength + 10, y);//thicken the ticks
                                    }
                                }
                            }//end if
                            else {
                                g.setColor(majorAxesColor);
                                g.drawLine(locationOfOrigin.x, y, locationOfOrigin.x + minorTickLength, y);
                            }//end if
                            y -= (gridSize * 2);
                            ++countTicks;
                            if (countTicks == 5) {
                                countTicks = 0;
                            }
                        }//end while

                        countTicks = 0;
                        y = locationOfOrigin.y + compLoc.y;
//now draw to the left of the y axis
                        while (y < yLocOfBottomSideOfComponent) {

//draw the major tick lengths
                            if (countTicks == 0) {
                                g.setColor(majorAxesColor);
                                g.drawLine(locationOfOrigin.x, y, locationOfOrigin.x + majorTickLength, y);
                                String numLabel = String.valueOf(convertScreenPointToGraphPoint(new Point(locationOfOrigin.x, y)).y);
                                if (!numLabel.equals("0.0") && labelAxis) {
                                    g.setColor(textColor);
                                    g.drawString(numLabel, locationOfOrigin.x + majorTickLength + 10, y);
                                }
                            }//end if
                            else {
                                g.setColor(majorAxesColor);
                                g.drawLine(locationOfOrigin.x, y, locationOfOrigin.x + minorTickLength, y);

                            }//end if
                            y += (gridSize * 2);
                            ++countTicks;
                            if (countTicks == 5) {
                                countTicks = 0;
                            }

                        }//end while

                    }
                    art.fillOval(g, locationOfOrigin.x, locationOfOrigin.y, gridSize, gridSize + 3);

                }//end drawMajorAxes

                if (isBar()) {
                    plotBarChart:
                    {
                        plotBarCharts:
                        {
                            int count = 0;
                            int cursor = 0;
                            while (cursor < graphObjectList.size()) {
                                Graphed gol = graphObjectList.get(cursor);
                                g.setColor(Grapher.getPlotColor(gol));

                                g.setStroke(new BasicStroke(plotThickness));
                                int offset = 6 * gridSize;
                                int x = locationOfOrigin.x + offset + count * 6 * gridSize;
                                int[] p = convertGraphCoordsToScreenCoords(1, gol.sortTime);
                                int y = p[1];
                                g.fill3DRect(x, y, 6 * gridSize, locationOfOrigin.y - y, true);
                                Font f = new Font(font.getFamily(), Font.BOLD, font.getSize() + 4);
                                g.setFont(f);
                                g.setColor(textColor);
                                FontMetrics fm = g.getFontMetrics(font);
                                g.drawString(gol.getLabel(), x + (6 * gridSize - fm.stringWidth(gol.getLabel())) / 2, locationOfOrigin.y + fm.getHeight());
                                count++;

                                cursor++;
                            }//end outer while loop 
                            netCount = 0;
                        }
                    }

                }//end if isBar
                else if (isGraph()) {
                    plotGraph:
                    {
                        plotGraphs:
                        {
                            int count = 0;
                            int cursor = 0;
                            while (cursor < graphObjectList.size()) {
                                Graphed gol = graphObjectList.get(cursor);
                                g.setColor(Grapher.getPlotColor(gol));

                                g.setStroke(new BasicStroke(plotThickness));
                                int offset = 6 * gridSize;
                                int x = locationOfOrigin.x + offset + count * 6 * gridSize;

                                Font f = new Font(font.getFamily(), Font.BOLD, font.getSize() + 4);
                                g.setFont(f);
                                g.setColor(Grapher.getPlotColor(gol));
                                FontMetrics fm = g.getFontMetrics(font);
                                g.drawString(gol.getLabel(), x + (6 * gridSize - fm.stringWidth(gol.getLabel())) / 2, locationOfOrigin.y + fm.getHeight());
                                count++;

                                cursor++;

                                for (int i = 0; i < gol.plotData.length; i++) {
                                    try {
                                        double[] data = gol.plotData[i];
                                        double[] data_next = gol.plotData[i + 1];

                                        double X = data[0], Y = data[1], X1 = data_next[0], Y1 = data_next[1];

                                        com.github.gbenroscience.sortmix.util.Point graphPoint = new com.github.gbenroscience.sortmix.util.Point(X, Y);
                                        com.github.gbenroscience.sortmix.util.Point graphPoint1 = new com.github.gbenroscience.sortmix.util.Point(X1, Y1);
                                        if (Math.abs(Y) != Double.POSITIVE_INFINITY && Math.abs(Y1) != Double.POSITIVE_INFINITY) {
                                            Point screenPoint = convertGraphPointToScreenPoint(graphPoint);
                                            Point screenPoint1 = convertGraphPointToScreenPoint(graphPoint1);
                                            g.setColor(Grapher.getPlotColor(gol));
                                            g.drawLine(screenPoint.x, screenPoint.y, screenPoint1.x, screenPoint1.y);
                                        }//end if
                                    }//end try
                                    catch (NumberFormatException | ArrayIndexOutOfBoundsException | ArithmeticException numErr) {
                                        break;
                                    }

                                }//end loop           

                            }//end outer while loop 
                            netCount = 0;
                        }
                    }
                }

            }//end try
            catch (NumberFormatException numErr) {

            } catch (NullPointerException nullErr) {
                nullErr.printStackTrace();
            }

        }//end true
        else {
            image = resizeImage(image, width, height, bgColor);
        }

    }//end method

    /**
     *
     * @param gridDistance The horizontal distance along the grid.
     * @return the equivalent horizontal distance in user terms.
     */
    public double convertGridSizeToUserDistanceAlongX(int gridDistance) {
        return gridDistance * xStep / ((double) gridSize);
    }

    /**
     *
     * @param gridDistance The vertical distance along the grid.
     * @return the vertical distance in user terms.
     */
    public double convertGridSizeToUserDistanceAlongY(int gridDistance) {
        return gridDistance * yStep / ((double) gridSize);
    }

    /**
     *
     * @param userX The horizontal distance along the grid in user terms.
     * @return the equivalent horizontal distance in screen terms.
     */
    public long convertUserDistanceAlongX_ToGridSize(double userX) {
        String value = String.valueOf(round((userX * gridSize) / (xStep)));
        int startIndex = value.length();
        try {
            if (value.substring(startIndex - 2).equals(".0")) {
                value = delete(value, startIndex - 2, startIndex);
            }
        }//end try
        catch (IndexOutOfBoundsException indexErr) {

        }
        return Long.valueOf(value);
    }

    /**
     *
     * @param userY The vertical distance along the grid in user terms.
     * @return the equivalent vertical distance in screen terms .
     */
    public long convertUserDistanceAlongY_ToGridSize(double userY) {
        String value = String.valueOf(round((userY * gridSize) / (yStep)));
        int startIndex = value.length();
        try {
            if (value.substring(startIndex - 2).equals(".0")) {
                value = delete(value, startIndex - 2, startIndex);
            }
        }//end try
        catch (IndexOutOfBoundsException indexErr) {

        }

        return Long.valueOf(value);
    }

    /**
     * Converts a point on the screen to its equivalent point in mathematics
     * relative to the specified origin. It is useful basically in labeling the
     * axes during its construction.
     *
     * @param screenPoint The point on the screen.
     * @return the point on the graph.
     */
    public com.github.gbenroscience.sortmix.util.Point convertScreenPointToGraphPoint(Point screenPoint) {
        double xGraph = convertGridSizeToUserDistanceAlongX(screenPoint.x - locationOfOrigin.x);
        double yGraph = convertGridSizeToUserDistanceAlongY(locationOfOrigin.y - screenPoint.y);
        return new com.github.gbenroscience.sortmix.util.Point(xGraph, yGraph);
    }

    /**
     *
     * @param xScreen the x point on the screen
     * @param yScreen the y point on the screen
     * @return an array having the screen's x coordinate in index 0 and the
     * screen's y coordinate in index 1.
     */
    public double[] convertScreenCoordsToGraphCoords(double xScreen, double yScreen) {
        double xGraph = convertGridSizeToUserDistanceAlongX((int) (xScreen - locationOfOrigin.x));
        double yGraph = convertGridSizeToUserDistanceAlongY((int) (locationOfOrigin.y - yScreen));
        return new double[]{xGraph, yGraph};
    }

    /**
     * Say the user is about to identify plot Point p = [2,4] on the screen, He
     * passes it in to the draw method as convertGraphPointToScreenPoint(p). and
     * gets his plot.This method takes care of all conversions from the math
     * coordinates to the screen coordinates.
     *
     * @param userPoint The point on the graph to be drawn on the screen.
     * @return the screen equivalent of the point.
     */

    public Point convertGraphPointToScreenPoint(com.github.gbenroscience.sortmix.util.Point userPoint) {
        double xScreen = locationOfOrigin.x + convertUserDistanceAlongX_ToGridSize(userPoint.x);
        double yScreen = locationOfOrigin.y - convertUserDistanceAlongY_ToGridSize(userPoint.y);//in contrast to screen coordinates math coordinates go up as y increases.
        return new Point((int) round(xScreen), (int) round(yScreen));
    }

    /**
     *
     * @param x the x point of the graph
     * @param y the y point of the graph
     * @return an array having the screen's x coordinate in index 0 and the
     * screen's y coordinate in index 1.
     */
    public int[] convertGraphCoordsToScreenCoords(double x, double y) {
        double xScreen = locationOfOrigin.x + convertUserDistanceAlongX_ToGridSize(x);
        double yScreen = locationOfOrigin.y - convertUserDistanceAlongY_ToGridSize(y);//in contrast to screen coordinates math coordinates go up as y increases.

        return new int[]{(int) xScreen, (int) yScreen};
    }

    /**
     * Generates automatically the numeric drawing parameters for the function.
     *
     *
     */
    public void generateAutomaticScale() {
        if (isAutoScaleOn()) {
            setxStep(0.1);
            setyStep(0.1);
        }//end if isAutoscaleOn

    }//end method

    /**
     *
     * <b><font color = 'green' size ='30'/>
     * NOTE: This method does not work in J2ME, but is the scaling method of
     * choice in desktop applications.
     * </b>
     *
     * @param img The image to scale
     * @param width The new width
     * @param height The new height
     * @param background The background color
     *
     * @return A new BufferedImage object, scaled to the new width and height.
     */
    public static BufferedImage resizeImage(BufferedImage img, int width, int height,
            Color background) {
        //double imgWidth = img.getWidth();
        //double imgHeight = img.getHeight();

        BufferedImage newImage = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = newImage.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setColor(background);
            g.fillRect(0, 0, width, height);
            g.drawImage(img, 0, 0, width, height, null);
        } finally {
            g.dispose();
        }
        return newImage;
    }

    public static Color getPlotColor(Graphed g) {
        if (!randomSelectColor) {
            if (g.isQuickSort()) {
                return Color.black;
            } else if (g.isMergeSort()) {
                return new Color(120, 200, 40);
            } else if (g.isShellSort()) {
                return Color.DARK_GRAY;
            } else if (g.isHeapSort()) {
                return Color.BLUE;
            } else if (g.isJavaArraysDotSort()) {
                return Color.GREEN;
            } else if (g.isInsertionSort()) {
                return Color.ORANGE;
            } else if (g.isSelectionSort()) {
                return new Color(100, 0, 220);
            } else if (g.isCacheOptimizationDisabledMemoryOptimizedBucketSort()) {
                return new Color(234, 0, 120);
            } else if (g.isCacheOptimizationDisabledSpeedOptimizedBucketSort()) {
                return Color.PINK;
            } else if (g.isCacheOptimizationEnabledMemoryOptimizedBucketSort()) {
                return Color.RED;
            } else if (g.isCacheOptimizationEnabledSpeedOptimizedBucketSort()) {
                return new Color(189, 190, 22);
            }
        }//end if  
        Random r = new Random();
        return new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256));

    }

    /**
     * Models an object to be graphed.
     *
     */
    public class Graphed {

        /**
         * the algorithm to use.
         */
        private int algorithm;

        public static final int INSERTIONSORT = 1;
        public static final int SELECTIONSORT = 2;
        public static final int CACHE_OPTIMIZATION_DISABLED_SPEED_OPTIMIZED_BUCKETSORT = 3;
        public static final int CACHE_OPTIMIZATION_DISABLED_MEMORY_OPTIMIZED_BUCKETSORT = 4;
        public static final int CACHE_OPTIMIZATION_ENABLED_SPEED_OPTIMIZED_BUCKETSORT = 5;
        public static final int CACHE_OPTIMIZATION_ENABLED_MEMORY_OPTIMIZED_BUCKETSORT = 6;
        public static final int QUICKSORT = 7;
        public static final int MERGESORT = 8;
        public static final int SHELLSORT = 9;
        public static final int HEAPSORT = 10;
        public static final int JAVA_ARRAYS_DOT_SORT = 11;

        /**
         * Selects the algorithm to use in generating the unsorted data.
         */
        private int dataGeneratorType = 0;

        public static final int ARRAY_OF_RANDOM_FLOATS = 0;
        public static final int ARRAY_OF_REVERSE_SORTED_FLOATS = 1;
        public static final int ARRAY_OF_SORTED_FLOATS = 2;
        public static final int ARRAY_OF_PARTIALLY_SORTED_FLOATS = 3;
        public static final int BINARY_ARRAY_OF_FLOATS = 4;
        public static final int ARRAY_OF_RANDOM_INTS = 5;
        public static final int ARRAY_OF_REVERSE_SORTED_INTS = 6;
        public static final int ARRAY_OF_SORTED_INTS = 7;
        public static final int ARRAY_OF_PARTIALLY_SORTED_INTS = 8;
        public static final int BINARY_ARRAY_OF_INTS = 9;
        public static final int ARRAY_OF_DIGITS = 10;

        /**
         * An 2D array that has contains 1D arrays of size 2 in each index. Each
         * 1D array has the size of the array sorted in index 0 and the sort
         * time in index 1.
         */
        private double plotData[][];

        /**
         * The time used to sort the data when the mode is BAR.
         */
        private double sortTime = 0;

        /**
         *
         * @param algorithm the code that says the kind of sort to be analyzed.
         * @param dataGeneratorType Selects the algorithm to use in generating
         * the unsorted data.
         */
        public Graphed(int algorithm, int dataGeneratorType) {
            this.algorithm = algorithm;
            this.dataGeneratorType = dataGeneratorType;
        }

        public int getAlgorithm() {
            return algorithm;
        }

        public void setAlgorithm(int algorithm) {
            this.algorithm = algorithm;
        }

        public void setDataGeneratorType(int dataGeneratorType) {
            this.dataGeneratorType = dataGeneratorType;
        }

        public int getDataGeneratorType() {
            return dataGeneratorType;
        }

        /**
         *
         * @return the array to be sorted.
         */
        public Object getInput() {
            if (dataGeneratorType == ARRAY_OF_PARTIALLY_SORTED_FLOATS) {
                return BenchMarker.initPartiallySortedArrayFloats(arraySize, arraySize - new Random().nextInt(arraySize >= 10 ? arraySize / 10 : arraySize));
            } else if (dataGeneratorType == ARRAY_OF_RANDOM_FLOATS) {
                return BenchMarker.initArrayRandomFloats(arraySize);
            } else if (dataGeneratorType == ARRAY_OF_SORTED_FLOATS) {
                return BenchMarker.initArraySortedFloats(arraySize);
            } else if (dataGeneratorType == ARRAY_OF_REVERSE_SORTED_FLOATS) {
                return BenchMarker.initArrayReverseSortedFloats(arraySize);
            } else if (dataGeneratorType == BINARY_ARRAY_OF_FLOATS) {
                Random r = new Random();
                if (r.nextBoolean()) {
                    return BenchMarker.initBinaryArrayFloats(arraySize, r.nextDouble(), r.nextDouble());
                } else {
                    return BenchMarker.initBinaryArrayFloats(arraySize, 0.5, 0.8);
                }
            } else if (dataGeneratorType == ARRAY_OF_PARTIALLY_SORTED_INTS) {
                return BenchMarker.initPartiallySortedArrayInts(arraySize, arraySize - new Random().nextInt(arraySize >= 10 ? arraySize / 10 : arraySize));
            } else if (dataGeneratorType == ARRAY_OF_RANDOM_INTS) {
                return BenchMarker.initArrayRandomInts(arraySize);
            } else if (dataGeneratorType == ARRAY_OF_SORTED_INTS) {
                return BenchMarker.initArraySortedInts(arraySize);
            } else if (dataGeneratorType == ARRAY_OF_REVERSE_SORTED_INTS) {
                return BenchMarker.initArrayReverseSortedInts(arraySize);
            } else if (dataGeneratorType == BINARY_ARRAY_OF_INTS) {
                Random r = new Random();
                if (r.nextBoolean()) {
                    return BenchMarker.initBinaryArrayInts(arraySize, r.nextInt(arraySize), r.nextInt(arraySize));
                } else {
                    return BenchMarker.initBinaryArrayInts(arraySize, 0, 1);
                }
            } else if (dataGeneratorType == ARRAY_OF_DIGITS) {
                return BenchMarker.initArrayRandomDigits(arraySize);
            }

            return null;
        }

        public boolean isQuickSort() {
            return algorithm == QUICKSORT;
        }

        public boolean isMergeSort() {
            return algorithm == MERGESORT;
        }

        public boolean isInsertionSort() {
            return algorithm == INSERTIONSORT;
        }

        public boolean isHeapSort() {
            return algorithm == HEAPSORT;
        }

        public boolean isShellSort() {
            return algorithm == SHELLSORT;
        }

        public boolean isSelectionSort() {
            return algorithm == SELECTIONSORT;
        }

        public boolean isJavaArraysDotSort() {
            return algorithm == JAVA_ARRAYS_DOT_SORT;
        }

        public boolean isCacheOptimizationDisabledMemoryOptimizedBucketSort() {
            return algorithm == CACHE_OPTIMIZATION_DISABLED_MEMORY_OPTIMIZED_BUCKETSORT;
        }

        public boolean isCacheOptimizationDisabledSpeedOptimizedBucketSort() {
            return algorithm == CACHE_OPTIMIZATION_DISABLED_SPEED_OPTIMIZED_BUCKETSORT;
        }

        public boolean isCacheOptimizationEnabledMemoryOptimizedBucketSort() {
            return algorithm == CACHE_OPTIMIZATION_ENABLED_MEMORY_OPTIMIZED_BUCKETSORT;
        }

        public boolean isCacheOptimizationEnabledSpeedOptimizedBucketSort() {
            return algorithm == CACHE_OPTIMIZATION_ENABLED_SPEED_OPTIMIZED_BUCKETSORT;
        }

        public String getLabel() {
            if (isQuickSort()) {
                return "Q";
            } else if (isMergeSort()) {
                return "M";
            } else if (isJavaArraysDotSort()) {
                return "A";
            } else if (isInsertionSort()) {
                return "I";
            } else if (isShellSort()) {
                return "Sh";
            } else if (isHeapSort()) {
                return "Hs";
            } else if (isSelectionSort()) {
                return "S";
            } else if (isCacheOptimizationDisabledMemoryOptimizedBucketSort()) {
                return "BMO";
            } else if (isCacheOptimizationDisabledSpeedOptimizedBucketSort()) {
                return "BSO";
            } else if (isCacheOptimizationEnabledMemoryOptimizedBucketSort()) {
                return "BMCO";
            } else if (isCacheOptimizationEnabledSpeedOptimizedBucketSort()) {
                return "BSCO";
            }

            return "-----";

        }

        public void getGraphPoints() {

            int i = 0;
            int MIN_ARRAY_SIZE = 0;
            int MAX_ARRAY_SIZE = arraySize;
            int step = 20;

            plotData = new double[((MAX_ARRAY_SIZE - MIN_ARRAY_SIZE) / step) + 1][2];//{{0,1},{2.3,4},....}
            for (arraySize = MIN_ARRAY_SIZE; arraySize <= MAX_ARRAY_SIZE; arraySize += step, i++) {

                plotData[i][0] = arraySize;
                if (arraySize == 0) {
                    plotData[i][1] = 0;
                }//end if
                else {
                    generator();
                    plotData[i][1] = sortTime;
                }//end else 
            }

            Smoothener:
            {
                for (int j = 0; j < plotData.length; j++) {
                    if (j - 2 >= 0) {
                        double past = plotData[j - 2][1], previous = plotData[j - 1][1], present = plotData[j][1];

                        double gradLeft = (previous - past) / (plotData[j - 1][0] - plotData[j - 2][0]);
                        double gradRight = (present - previous) / (plotData[j][0] - plotData[j - 1][0]);

                        if (gradLeft * gradRight < 0) {
                            plotData[j - 1][1] = (past + present) / 2;

                        }

                    }

                }//end for loop
            }

            arraySize = MAX_ARRAY_SIZE;
            AnalysisFrame.switched = true;
        }

//ty
        public void generator() {

            final Object inputArray = getInput();

            final boolean isInt = inputArray instanceof int[];

            final double nanos[] = new double[iterations];

            for (int count = 0; count < iterations; count++) {

                netCount++;

                double start = System.nanoTime();
                if (isInsertionSort()) {
                    if (isInt) {
                        SortUtils.insertionSort(((int[]) inputArray).clone(), 0, arraySize - 1);
                    } else {
                        SortUtils.insertionSort(((double[]) inputArray).clone(), 0, arraySize - 1);
                    }
                } else if (isSelectionSort()) {
                    if (isInt) {
                        SortUtils.selectionSort(((int[]) inputArray).clone(), 0, arraySize - 1);
                    } else {
                        SortUtils.selectionSort(((double[]) inputArray).clone(), 0, arraySize - 1);
                    }
                } else if (isShellSort()) {
                    if (isInt) {
                        ShellSort.sort(((int[]) inputArray).clone(), 0, arraySize - 1);
                    } else {
                        ShellSort.sort(((double[]) inputArray).clone(), 0, arraySize - 1);
                    }
                } else if (isHeapSort()) {
                    if (isInt) {
                        HeapSort.sort(((int[]) inputArray).clone());
                    } else {
                        HeapSort.sort(((double[]) inputArray).clone());
                    }
                } else if (isCacheOptimizationDisabledSpeedOptimizedBucketSort()) {
                    SpeedyBucketSort.setCacheOptimized(false);
                    if (isInt) {
                        SpeedyBucketSort.sort(((int[]) inputArray).clone(), 0, arraySize - 1);
                    } else {
                        SpeedyBucketSort.sort(((double[]) inputArray).clone(), 0, arraySize - 1);
                    }
                } else if (isCacheOptimizationDisabledMemoryOptimizedBucketSort()) {
                    AdvancedWittyBucketSort.setCacheOptimized(false);
                    if (isInt) {
                        AdvancedWittyBucketSort.sort(((int[]) inputArray).clone(), 0, arraySize - 1);
                    } else {
                        AdvancedWittyBucketSort.sort(((double[]) inputArray).clone(), 0, arraySize - 1);
                    }
                } else if (isCacheOptimizationEnabledSpeedOptimizedBucketSort()) {
                    SpeedyBucketSort.setCacheOptimized(true);
                    if (isInt) {
                        SpeedyBucketSort.sort(((int[]) inputArray).clone(), 0, arraySize - 1);
                    } else {
                        SpeedyBucketSort.sort(((double[]) inputArray).clone(), 0, arraySize - 1);
                    }

                } else if (isCacheOptimizationEnabledMemoryOptimizedBucketSort()) {
                    AdvancedWittyBucketSort.setCacheOptimized(true);
                    if (isInt) {
                        AdvancedWittyBucketSort.sort(((int[]) inputArray).clone(), 0, arraySize - 1);
                    } else {
                        AdvancedWittyBucketSort.sort(((double[]) inputArray).clone(), 0, arraySize - 1);
                    }

                } else if (isMergeSort()) {
                    if (isInt) {
                        MergeSort.sort(((int[]) inputArray).clone(), 0, arraySize - 1);
                    } else {
                        MergeSort.sort(((double[]) inputArray).clone(), 0, arraySize - 1);
                    }

                } else if (isQuickSort()) {
                    if (isInt) {
                        QuickSort.sort(((int[]) inputArray).clone(), 0, arraySize - 1);
                    } else {
                        QuickSort.sort(((double[]) inputArray).clone(), 0, arraySize - 1);
                    }
                } else if (isJavaArraysDotSort()) {
                    if (isInt) {
                        Arrays.sort(((int[]) inputArray).clone(), 0, arraySize - 1);
                    } else {
                        Arrays.sort(((double[]) inputArray).clone(), 0, arraySize - 1);
                    }

                }
                start = (System.nanoTime() - start) / 1.0E6;
                nanos[count] = start;
            }
            double sum = 0;
            for (double d : nanos) {
                sum += d;
            }
            sum /= iterations;

            sortTime = sum;
        }//ty

    }

}
