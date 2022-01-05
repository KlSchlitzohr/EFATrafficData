package de.klschlitzohr.efatrafficdata.analysis.utils;

import de.klschlitzohr.efatrafficdata.analysis.data.manager.StationsManager;
import de.klschlitzohr.efatrafficdata.api.data.model.OwnLine;
import de.klschlitzohr.efatrafficdata.api.data.model.OwnLineStop;
import de.klschlitzohr.efatrafficdata.api.data.model.OwnStation;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created on 22.10.2021
 *
 * @author KlSchlitzohr
 */
public class Grid {

    private final StationsManager stationsManager;
    private BufferedImage bufferedImage;
    private final JFrame frame;
    private Graphics2D graphics2D;
    private final double width;
    private final double height;
    double minX;
    double minY;

    private final float zoom = 5;
    private final float offsetX = -1;
    private final float offsetY = -1.5f;

    private final ArrayList<ArrayList<Point>> randPointsList;
    @Setter
    private ArrayList<OwnStation> ownStationList;
    @Setter
    private ArrayList<TempLine> ownLinesList;

    public Grid(ArrayList<ArrayList<Point>> stationList, double width, double height, double minX, double minY,StationsManager stationsManager) {
        this.frame = new JFrame();
        this.randPointsList = stationList;
        this.width = width * 1.5;
        this.height = height * 1.2 * 1.5 / 1.5;
        this.minX = minX;
        this.minY = minY;
        this.stationsManager = stationsManager;
    }

    public ArrayList<FlatPoint> getFlatStation() {
       ArrayList<FlatPoint> flatStationList = new ArrayList<>();
       for (OwnStation ownStation : ownStationList) {
           flatStationList.add(new FlatPoint(toFlat(ownStation.getX(),true),toFlat(ownStation.getY(),false)));
       }
       return flatStationList;
    }

    public int toFlat(double digit, boolean isX) {
        if (isX) {
            return (int) ((digit - minX + offsetX) * 100 * zoom);
        } else {
            return (int) ((digit - minY + offsetY) * 100 * 1.2 * zoom);
        }
    }

    public ArrayList<ArrayList<FlatPoint>> getFlatRandPoints() {
        ArrayList<ArrayList<FlatPoint>> flatRandPointsList = new ArrayList<>();
        for (ArrayList<Point> randPoints : randPointsList) {
            ArrayList<FlatPoint> flatPoints = new ArrayList<>();
            for (Point point : randPoints) {
                flatPoints.add(new FlatPoint(toFlat(point.getX(), true), toFlat(point.getY(), false)));
            }
            flatRandPointsList.add(flatPoints);
        }
        return flatRandPointsList;
    }

    public void updateImage() {
        bufferedImage = new BufferedImage((int)(width*100),(int)(height*100),BufferedImage.TYPE_INT_BGR);
        makeWhite();
        graphics2D = (Graphics2D) bufferedImage.getGraphics();
        drawNeurons();
        flipImage();
        upDateJFrame();
    }

    public void flipImage() {
        AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        tx.translate(0, -bufferedImage.getHeight(null));
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        bufferedImage = op.filter(bufferedImage, null);
    }

    private void drawNeurons() {
        graphics2D.setStroke(new BasicStroke(1));
        graphics2D.setColor(new Color(255,0,255));
        graphics2D.drawRect(toFlat(7.2,true),toFlat(48.9,false),toFlat(8.6,true),toFlat(49.9,false));
        graphics2D.setColor(new Color(0,0,255));
        for (ArrayList<FlatPoint> flatPointList : getFlatRandPoints()) {
            FlatPoint lastPoint = null;
            for (FlatPoint flatPoint : flatPointList) {
                graphics2D.drawOval(flatPoint.getX() - 2, flatPoint.getY() - 2, 4, 4);
                if (lastPoint != null) {
                    if (flatPoint.distance(lastPoint) > 1 && flatPoint.distance(lastPoint) < 150) {
                        graphics2D.drawLine(flatPoint.getX(), flatPoint.getY(), lastPoint.getX(), lastPoint.getY());
                    }
                }
                lastPoint = flatPoint;
            }
        }
        graphics2D.setColor(new Color(0, 255, 0));
        if (ownStationList != null) {
            for (FlatPoint flatPoint : getFlatStation()) {
                graphics2D.drawOval(flatPoint.getX()-2,flatPoint.getY()-2, 4, 4);
            }
        }
        FlatPoint lastPoint;
        graphics2D.setStroke(new BasicStroke(2));
        if (ownLinesList != null) {
            Random rand = new Random();
            for (TempLine tempLine : ownLinesList) {
                OwnLine ownLine = tempLine.getOwnLine();
                int color = tempLine.getColor();
                if (color > 255)
                    color = 255;
                else if (color < 0)
                    color = 0;
                graphics2D.setColor(new Color(color, Math.abs(255-color), 0));
                lastPoint = null;
                for (OwnLineStop ownLineStop : ownLine.getOwnLineStops()) {
                    OwnStation ownStation = stationsManager.getStationByOwnID(ownLineStop.getStopID());
                    if (ownStation != null) {
                        FlatPoint flatPoint = new FlatPoint(toFlat(ownStation.getX(), true), toFlat(ownStation.getY(), false));
                        if (lastPoint != null && lastPoint.distance(flatPoint) > 1 && flatPoint.distance(lastPoint) < 200) {
                            graphics2D.drawLine(lastPoint.getX(), lastPoint.getY(), flatPoint.getX(), flatPoint.getY());
                        }
                        lastPoint = flatPoint;
                    }
                }
            }
        }
    }

    private void makeWhite() {
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                Color color = new Color(255,255,255);
                bufferedImage.setRGB(x,y,color.getRGB());
            }
        }
    }

    private void upDateJFrame() {
        frame.getContentPane().setLayout(new FlowLayout());
        frame.getContentPane().removeAll();
        frame.getContentPane().add(new JLabel(new ImageIcon(bufferedImage)));
        frame.pack();
        frame.setLocation(520,0);
        frame.setVisible(true);
    }

}
