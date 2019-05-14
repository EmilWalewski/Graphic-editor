package Edytor;

import javafx.stage.FileChooser;

import javax.imageio.*;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.StrokeBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;
import java.util.Random;

public class Edytor extends JPanel{

    JComboBox<String> colorPicker;
    JComboBox<String> toolsPanel;
    JComboBox<String> thickness;
    JMenuBar menuBar;
    JMenu file, edit;
    JMenuItem open, save, clean, contrast,changeSize, rotate, gray, filter, setCol, blackAndWhite, negative;
    static int polygonX, polyginY;
    //JButton rubber;
    JCheckBox checkBox;
    JCheckBoxMenuItem rubber;
    int curX,curY;
    static int ImageWidth, ImageHeight;
    static BufferedImage openImage;
    static int imageX = 20, imageY = 20;
    static int rotateInt = 0;
    static double changeInt = 0;
    static boolean rotateBool = false, changeDimension = false;
    boolean getColor = false;
    boolean setColor = false;

    public static void createGUI(){

        JFrame frame = new JFrame();

        frame.setContentPane(new Edytor());
        frame.setSize(800,800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);


    }

    public static void main(String[] args){

        SwingUtilities.invokeLater(()-> {
                createGUI();
        });
    }

    public Edytor(){

        Drawing draw = new Drawing();


        toolsPanel = new JComboBox<>();
        toolsPanel.addItem("Point");
        toolsPanel.addItem("Circle");
        toolsPanel.addItem("Rectangle");
        toolsPanel.addItem("Ellipse");
        toolsPanel.addItem("Arc"); //luk
        toolsPanel.addItem("Part of wheel");
        toolsPanel.addItem("Polygon"); //wielokat
        toolsPanel.addItem("Bezier's curve");//krzywe
        toolsPanel.addActionListener(draw);

        colorPicker = new JComboBox<>();
        colorPicker.addItem("Red");
        colorPicker.addItem("Blue");
        colorPicker.addItem("Gray");
        colorPicker.addItem("Black");
        colorPicker.addItem("Green");
        colorPicker.addActionListener(draw);

        thickness = new JComboBox<>();
        thickness.addItem("1");
        thickness.addItem("2");
        thickness.addItem("3");
        thickness.addItem("4");
        thickness.addItem("5");
        thickness.addActionListener(draw);

        menuBar = new JMenuBar();
        menuBar.setBounds(0,0,500,30);
        add(menuBar);

        file = new JMenu("File");
        menuBar.add(file);

        open = new JMenuItem("Open");
        open.addActionListener(draw);
        file.add(open);

        save = new JMenuItem("Save");
        save.addActionListener(draw);
        file.add(save);

        edit = new JMenu("Edit");
        menuBar.add(edit);

        clean = new JMenuItem("Clean");
        clean.addActionListener(draw);
        edit.add(clean);

        contrast = new JMenuItem("Brightness");
        contrast.addActionListener(draw);
        edit.add(contrast);

        changeSize = new JMenuItem("Change Size");
        changeSize.addActionListener(draw);
        edit.add(changeSize);

        rotate = new JMenuItem("Rotate");
        rotate.addActionListener(draw);
        edit.add(rotate);

        gray = new JMenuItem("Shades of Gray");
        gray.addActionListener(draw);
        edit.add(gray);

        filter = new JMenuItem("Filter");
        filter.addActionListener(draw);
        edit.add(filter);

        setCol = new JMenuItem("Set Color");
        setCol.addActionListener(draw);
        edit.add(setCol);

        blackAndWhite = new JMenuItem("Black and White");
        blackAndWhite.addActionListener(draw);
        edit.add(blackAndWhite);

        negative = new JMenuItem("Negative");
        negative.addActionListener(draw);
        edit.add(negative);

//        rubber = new JButton("Rubber");
//        rubber.addActionListener(draw);

        checkBox = new JCheckBox();

        rubber = new JCheckBoxMenuItem("Rubber");
        rubber.addActionListener(draw);
        checkBox.add(rubber);

        JPanel p = new JPanel();

        p.setLayout(new GridLayout(1,4,3,3));
        p.add(toolsPanel);
        p.add(colorPicker);
        p.add(thickness);
        p.add(checkBox);

        JPanel p2 = new JPanel();
        p2.add(menuBar);

        setLayout(new BorderLayout(3,3));
        add(p,BorderLayout.SOUTH);
        add(draw, BorderLayout.CENTER);
        add(p2,BorderLayout.NORTH);
    }

    class Drawing extends JPanel implements ActionListener, MouseListener, MouseMotionListener {


        public Drawing(){addMouseListener(this);}

        Shape[] shapes = new Shape[500];
        int shapesCount = 0;
        public int xCoordinate, yCoordinate;
        Color selectedColor = Color.WHITE;
        int counter = 0;
        int thick = 0;
        boolean c = false;
        boolean rub = false;
        BufferedImage bi = null;
        boolean openBoolean = false;
        int filterX, filterY;

        //rysowanie krzywych
        int clickCounter = 0;
        int[] xPos = new int[4];
        int[] yPos = new int[4];
        Shape2[] line = new Shape2[100];
        int lineCounter = 0;
        double funX, funY;
        //koniec rysowania krzywych


        @Override
        public void actionPerformed(ActionEvent e) {

            if (e.getSource() == colorPicker){
                    switch (colorPicker.getSelectedIndex()){
                        case 0: selectedColor = Color.red; break;
                        case 1: selectedColor = Color.blue; break;
                        case 2: selectedColor = Color.gray; break;
                        case 3: selectedColor = Color.black; break;
                        case 4: selectedColor = Color.green; break;
                    }
            }

            if (e.getSource() == toolsPanel){
                switch (toolsPanel.getSelectedIndex()){

                    case 0: counter = 0; break;
                    case 1: counter = 1; break;
                    case 2: counter = 2; break;
                    case 3: counter = 3; break;
                    case 4: counter = 4; break;
                    case 5: counter = 5; break;
                    case 6: counter = 6; break;
                    case 7: counter = 7; break;
                }

            }

            if(e.getSource() == thickness){
                switch (thickness.getSelectedIndex()){
                    case 0: thick = 1; break;
                    case 1: thick = 2; break;
                    case 2: thick = 3; break;
                    case 3: thick = 4; break;
                    case 4: thick = 5; break;
                }

              }

             if (e.getSource() == open){

                 JFileChooser jf = new JFileChooser("C:\\Images");

                 if (jf.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){

                     try {
                         openImage = ImageIO.read(jf.getSelectedFile());
                         ImageWidth = openImage.getWidth();
                         ImageHeight = openImage.getHeight();
                         openBoolean = true;
                         addShape(new DrawOwnImage());
                         repaint();

                     }catch (IOException e7){
                         e7.printStackTrace();
                     }
                 }
             }

             if (e.getSource() == save){

                 saveFile(shapes);
             }

             if (e.getSource() == clean){
                 c = true;
                 repaint();
             }

             if (e.getSource() == contrast){


                 String result = JOptionPane.showInputDialog(this, "Podaj wartosc z przedzialu 0.1 - 1");


                 for (int x=0; x<ImageWidth; x++){
                     for (int y=0; y<ImageHeight; y++){
                         Color color = new Color(openImage.getRGB(x,y));
                         int red = (int)(color.getRed()*Double.parseDouble(result));
                         int green = (int)(color.getGreen()*Double.parseDouble(result));
                         int blue = (int)(color.getBlue()*Double.parseDouble(result));
                         int sum = red+green+blue;
                         Color col = new Color(red,green,blue);
                         openImage.setRGB(x,y,col.getRGB());
                     }
                 }
                 repaint();
             }

             if (e.getSource() == changeSize){

                 String res = JOptionPane.showInputDialog(null,"Podaj wartosc z zakresu 1 - 2");

                 if (changeInt == JOptionPane.YES_OPTION){
                     changeDimension = true;
                     changeInt = Double.parseDouble(res);
                     repaint();
                 }
             }

             if (e.getSource() == rotate){

                String result = JOptionPane.showInputDialog(null, "Podaj wartosc z przedzialu 0 - 360", "");

                if (rotateInt == JOptionPane.YES_OPTION) {
                    rotateBool = true;
                    rotateInt = Integer.parseInt(result);
                    repaint();
                }
             }

             if (e.getSource() == gray){


                     //ImageInputStream iis = ImageIO.createImageInputStream(new File("E:\\Images\\desktop2.jpg"));
//                     Iterator<ImageReader> iterator = ImageIO.getImageReaders(bi);
//                     ImageReader imageReader = iterator.next();
//                     String format = imageReader.getFormatName();



                     for (int x=0; x<ImageWidth; x++){
                         for (int y=0; y<ImageHeight; y++){
                             Color color = new Color(openImage.getRGB(x,y));
                             int red = (int)(color.getRed() * 0.3);
                             int green = (int)(color.getGreen() * 0.3);
                             int blue = (int)(color.getBlue() * 0.21);
                             int sum = red+green+blue;
                             Color col = new Color(sum,sum,sum);
                             openImage.setRGB(x,y,col.getRGB());
                         }
                     }
                     repaint();
             }

             if (e.getSource() == filter){

                 getColor = true;
             }

             if (e.getSource() == setCol){

                 setColor = true;
             }

             if (e.getSource() == blackAndWhite)
                 convertToBlackAndWhite();

             if (e.getSource() == rubber){
                 if (rubber.isSelected())
                 rub = true;
                 else
                     rub = false;
             }

             if (e.getSource() == negative){

                 int wid = openImage.getWidth();
                 int hei = openImage.getHeight();


                 for (int i=0; i<wid; i++) {
                     for (int j = 0; j < hei; j++) {


                         int pixel = openImage.getRGB(i, j);
                         int a = (pixel>>24)&0xff;
                         int red = (pixel >> 16) & 0x000000FF;
                         int green = (pixel >> 8) & 0x000000FF;
                         int blue = (pixel) & 0x000000FF;

                         red = 255 - red;
                         green = 255 - green;
                         blue = 255 - blue;

                         pixel = (a<<24) | (red<<16) | (green<<8) | blue;

                         openImage.setRGB(i, j, pixel);
                     }
                 }

                    repaint();
             }

        }

        public void paint(Graphics g){


            g.setColor(Color.WHITE);
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setClip(10, 10, 800, 800);

                if (c) {
                    for (int i = 0; i < shapesCount; i++) {
                        shapes[i] = null;
                    }
                    for (int i = 0; i <= lineCounter; i++) {
                        line[i] = null;
                    }
                    shapesCount = 0;
                    lineCounter = 0;
                    c = false;
                    g.setColor(Color.WHITE);
                    g.fillRect(0, 0, getWidth(), getHeight());
                } else {

                    if (openBoolean) {

                        for (int i = 0; i < shapesCount; i++) {
                            Shape s = shapes[i];
                            s.draw(g);
                        }
                        openBoolean = false;

                    } else if (counter == 7) {
                        g.setColor(Color.WHITE);
                        g.fillRect(0, 0, getWidth(), getHeight());
                        for (int i = 0; i < lineCounter; i++) {
                            Shape2 s = line[i];
                            s.draw2(g);
                        }
                    } else {

                        g.setColor(Color.WHITE);
                        g.fillRect(0, 0, getWidth(), getHeight());
                        for (int i = 0; i < shapesCount; i++) {
                            Shape s = shapes[i];
                            s.draw(g);

                        }
                    }
                }
        }

        public boolean saveFile(Shape[] s){

            bi = new BufferedImage(500,500, BufferedImage.TYPE_INT_RGB);

            Graphics2D graf = bi.createGraphics();

            Shape[] m = new Shape[shapesCount];
            for (int h=0;h<shapesCount;h++)
                m[h] = s[h];

            for (int g=0;g<shapesCount;g++)
            m[g].draw(graf);


            JFileChooser fc = new JFileChooser();
            FileNameExtensionFilter fnef = new FileNameExtensionFilter("jpg","jpg", "jpeg");
            FileNameExtensionFilter fnef2 = new FileNameExtensionFilter("png","png");
            fc.setFileFilter(fnef);
            fc.setFileFilter(fnef2);

            if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

                try {
                    ImageIO.write(bi,fc.getFileFilter().getDescription(),fc.getSelectedFile());
                }catch (IOException e4){
                    e4.printStackTrace();
                }

            }

                return true;
        }

        public int takePixelsNumber(){

            int wid = openImage.getWidth();
            int hei = openImage.getHeight();
            int num = 0;

            Color c = new Color(openImage.getRGB(filterX,filterY));

            for (int i=0; i<wid; i++){
                for (int j=0; j<hei; j++){


                    int pixel = openImage.getRGB(i,j);
                    int red = (pixel >> 16) & 0x000000FF;
                    int green = (pixel >> 8) & 0x000000FF;
                    int blue = (pixel) & 0x000000FF;

                    if (red == c.getRed() && green == c.getGreen() && blue == c.getBlue()) {
                        num++;
                        openImage.setRGB(i, j, new Color(255,255,255).getRGB());
                    }
                }
            }

            return num;
        }

        public void setColor(){

            int wid = openImage.getWidth();
            int hei = openImage.getHeight();

            Color c = new Color(openImage.getRGB(filterX,filterY));

            for (int i=0; i<wid; i++){
                for (int j=0; j<hei; j++){


                    int pixel = openImage.getRGB(i,j);
                    int red = (pixel >> 16) & 0x000000FF;
                    int green = (pixel >> 8) & 0x000000FF;
                    int blue = (pixel) & 0x000000FF;

                    if (red == c.getRed() && green == c.getGreen() && blue == c.getBlue()) {
                        openImage.setRGB(i, j, selectedColor.getRGB());
                    }
                }
            }
        }

        public void convertToBlackAndWhite(){

            int wid = openImage.getWidth();
            int hei = openImage.getHeight();

            for (int i=0; i<wid; i++){
                for (int j=0; j<hei; j++){

                    Color c = new Color(openImage.getRGB(i,j));
                    int red = c.getRed();
                    int green = c.getGreen();
                    int blue = c.getBlue();
                    int sum = (red+green+blue)/3;

                    openImage.setRGB(i, j, new Color(sum,sum,sum).getRGB());

                }
            }

            repaint();

        }

        @Override
        public void mouseDragged(MouseEvent e) {}

        @Override
        public void mouseClicked(MouseEvent e) {

            if (getColor){

                filterX = e.getX();
                filterY = e.getY();
                int k = takePixelsNumber();
                repaint();

                getColor = false;

            }else if(setColor) {

                filterX = e.getX();
                filterY = e.getY();
                setColor();
                setColor = false;

            }else {

                curX = e.getX();
                curY = e.getY();

                //----------------------------- rysowanie krzywych ------------------------------------
                if (counter == 7) {

                    if (clickCounter <= 3) {
                        xPos[clickCounter] = e.getX();
                        yPos[clickCounter] = e.getY();
                        addPoint(new DrawPoint());
                        clickCounter++;
                    } else {
                        setCurves();
                    }

                }//----------------------------- koniec rysowania krzywych ------------------------------------
                else {

                    //----------------------------- rysowanie pozostalych figur ------------------------------------


                    //----------------------------- opcja gumki ------------------------------------
                    if (rub) {

                        int j = 0;

                        for (int i = 0; i <= shapesCount; i++) {  // check shapes from front to back
                            Shape s = shapes[i];
                            if (s.containsPoint(e.getX(), e.getY())) {
                                shapes[i] = null;
                                j = i;
                                break;
                            }

                        }
                        for (int k = j; k <= shapesCount; k++) {
                            shapes[k] = shapes[k + 1];
                        }
                        shapesCount--;
                        repaint();
                        //----------------------------- koniec opcji gumki ------------------------------------
                    } else {
                        xCoordinate = e.getX();
                        yCoordinate = e.getY();
                        if (counter == 0) addShape(new PointShape());
                        if (counter == 1) addShape(new Circle());
                        if (counter == 2) addShape(new Rectangle());
                        if (counter == 3) addShape(new Ellipse());
                        if (counter == 4) addShape(new Arc());
                        if (counter == 5) addShape(new SectioOfWheel());
                        if (counter == 6) {
                            polygonX = e.getX();
                            polyginY = e.getY();
                            addShape(new Polygon());
                        }

                    }
                }

            }

        }

        //----------------------------------------------------
        public void addShape(Shape shape){

            shape.setPoints(xCoordinate,yCoordinate,150,150);
            shape.setCol(selectedColor);
            shape.th(thick);
            shapes[shapesCount] = shape;
            shapesCount++;
            repaint();
        }
        //----------------------------------------------------


        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}  //mouse listener methods

        @Override
        public void mouseMoved(MouseEvent e) {}

        //--------------------------------------------posredni zestaw funkcji rysujacych krzywe----------------------------

        public void addPoint(Shape2 shape){

            shape.setPoints(xPos[clickCounter],yPos[clickCounter]);
            shape.setCol2(selectedColor);
            line[lineCounter] = shape;
            lineCounter++;
            repaint();
        }

        public void addCurv(Shape2 shape){

            line[lineCounter] = shape;
            lineCounter++;
            repaint();

        }

        //--------------------------------funkcja rysujaca krzywe breziera----------------------------------------
        public void  setCurves(){

            double[] dx = new double[13];
            double[] dy = new double[13];
            int i = 0;
            int j = 0;

            for (double t = 0; t <= 1; t += 0.1) {
                funX = xPos[0] * (Math.pow((1 - t), 3)) + 3 * xPos[1] * t * (Math.pow((1 - t), 2)) + 3 * xPos[2] * Math.pow(t, 2) * (1 - t) + xPos[3] * Math.pow(t, 3);
                funY = yPos[0] * (Math.pow((1 - t), 3)) + 3 * yPos[1] * t * (Math.pow((1 - t), 2)) + 3 * yPos[2] * Math.pow(t, 2) * (1 - t) + yPos[3] * Math.pow(t, 3);
                //DrawPoint dp = new DrawPoint();
                //dp.setPoints((funX,funY);

                //nowa sekcja

                dx[j] = funX;
                dy[j] = funY;
                j++;
                DrawLine dl = new DrawLine();
                if (t == 0) {
                    dl.setPoints((double)xPos[0],(double) yPos[0],dx[i],dy[i]);
                }
                else{

                    dl.setPoints(dx[i],dy[i],dx[i+1],dy[i+1]);
                    i++;
                }

                //koniec sekcji
                addCurv(dl);
            }
        }//-------------------------------- koniec funkcji rysujacej krzywe breziera----------------------------------------


    }//end of drawing class


    //------------------------------------------ klasy do rysowania ksztaltow-------------------------------
    static abstract class Shape {

        int x, y, width, height;
        Color col = Color.BLUE;
        int d = 1;

        public void setPoints(int x, int y, int width, int height){
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public void setCol(Color c){ this.col = c; }

        public void th(int k){this.d = k;}

        public abstract void draw(Graphics g);

        boolean containsPoint(int wspx, int wspy) {

            if (wspx >= x && wspx < x+width && wspy >= y && wspy < y+height)
                return true;
            else
                return false;
        }

    }

    static class PointShape extends Shape {



        @Override
        public void draw(Graphics g) {

            //System.out.println(d);

            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(col);
            g2.setStroke(new BasicStroke(d));
            g2.drawOval(x,y,5,5);
            g2.fillOval(x,y,5,5);
        }

    }

    static class Circle extends Shape {

        int i = 0;

        @Override
        public void draw(Graphics g) {

            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(col);
            g2.setStroke(new BasicStroke(d));
            g2.drawOval(x, y, 50, 50);
        }
    }

    static class Rectangle extends Shape {


        @Override
        public void draw(Graphics g) {

           Graphics2D g2 = (Graphics2D) g;
            g2.setColor(col);
            g2.setStroke(new BasicStroke(d));
            g2.drawRect(x,y,50,50);
        }


    }

    static class Ellipse extends Shape {


        @Override
        public void draw(Graphics g) {

            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(col);
            g2.setStroke(new BasicStroke(d));
            g2.draw(new Ellipse2D.Double(x,y,80,40));
        }


    }

    static class Arc extends Shape {


        @Override
        public void draw(Graphics g) {

            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(col);
            g2.setStroke(new BasicStroke(d));
            g2.draw(new Arc2D.Double(x,y,100,40,90,135,Arc2D.OPEN));
        }


    }

    static class SectioOfWheel extends Shape {


        @Override
        public void draw(Graphics g) {

            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(col);
            g2.setStroke(new BasicStroke(d));
            g2.draw(new Arc2D.Double(x,y,100,120,100,60,Arc2D.PIE));
        }

    }

    static class Polygon extends Shape {


        int[] x1 = {polygonX, polygonX + 20, polygonX + 20, polygonX, polygonX - 40, polygonX - 60};
        int[] y1 = {polyginY, polyginY + 20, polyginY + 40, polyginY + 60, polyginY + 40, polyginY + 10};

        @Override
        public void draw(Graphics g) {

            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(col);
            g2.setStroke(new BasicStroke(d));
            g2.drawPolygon(x1, y1, 6);
        }

    }

     static class DrawOwnImage extends Shape{

         @Override
         public void draw(Graphics g) {

             if (rotateBool) {

                 Graphics2D g2d = (Graphics2D) g;

                 AffineTransform at = AffineTransform.getTranslateInstance(imageX, imageY);
                 at.rotate(Math.toRadians(rotateInt), openImage.getWidth() / 2, openImage.getHeight() / 2);
                 g2d.setTransform(at);
                 g2d.drawImage(openImage, imageX, imageY, null);
                 rotateBool = false;
                 rotateInt = 0;
             }

             if (changeDimension){
                 Graphics2D g2d = (Graphics2D) g;
                 double delta = 1f * changeInt;
                    g2d.scale(delta,delta);
                 g2d.drawImage(openImage, imageX, imageY, null);
                 changeDimension = false;
                 changeInt = 0;
             }

             g.drawImage(openImage, imageX, imageY, null);
         }
     }

    //------------------------------------------ klasy do rysowania krzywych breziera-------------------------------
    static abstract class Shape2 {

        double x1, y1;
        double x2,y2;
        Color col2 = Color.BLUE;

        public void setCol2(Color c){ this.col2 = c; }
        public abstract int th2(int d);

        public void setPoints(double x, double y){
            this.x1 = x;
            this.y1 = y;
        }


        public abstract void draw2(Graphics g);

    }

    static class DrawLine extends Shape2{

        @Override
        public int th2(int d) {return d;}

        public void setPoints(double x1, double y1, double x2, double y2){
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        public void draw2(Graphics g){

            g.setColor(col2);
            g.drawLine((int)x1,(int)y1,(int)x2,(int)y2);
        }

    }

    static class DrawPoint extends Shape2{

        @Override
        public int th2(int d) {return d;}

        public void setPoints(double x, double y){
            this.x1 = x;
            this.y1 = y;
        }

        public void draw2(Graphics g){

            g.setColor(col2);
            g.fillOval((int)x1,(int)y1,5,5);
        }

    }//------------------------------------------ koniec klas do rysowania krzywych breziera-------------------------------



}//end of edytor class
