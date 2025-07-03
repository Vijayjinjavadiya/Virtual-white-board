import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.security.Key;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.plaf.basic.BasicTableHeaderUI;

class WhiteBoard extends JPanel {
    private int width = 800;
    private int height = 600;
    private Graphics2D g2d;
    private BufferedImage canvas;
    private int prevX, prevY, currX, currY;
    private static int fileCount = 1;
    private boolean isOvalOn = false;
    private boolean isRectOn = false;
    private boolean isLineOn = true;
    private Color strokeColor = Color.black;
    private BasicStroke strokeSize = new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND);
    private boolean isDragging = false;

    public WhiteBoard() {
        setPreferredSize(new Dimension(width, height));  // Sets the preferred size of this component. If preferredSize is null, the UI will be asked for the preferred size.
        canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB); // TYPE_INT_ARGB represents an Image with 8-bit rgba components packed into integer pixels
        g2d = canvas.createGraphics(); // Used for creating a Graphics2D Object to draw into BufferedImage
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); // Anti-Aliasing is used here to sharpen the quality
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setStroke(strokeSize); // Sets the stroke size (Line Width)
        g2d.setColor(strokeColor); // Sets the stroke color (Line Color)
        g2d.setBackground(Color.white); // Sets the background color to white


        // Listening for Mouse Pressed Event
        // MouseAdapter -> Abstract Class
        // MouseListener, MouseMotionListener -> Interfaces
        // MouseAdapter implements MouseListener & MouseMotionListener
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) { // MouseEvent class object to capture mouse events (used to get x,y position of mouse pointer)
                // On mouse click change prevX, prevY to current mouse position
                prevX = e.getX();
                prevY = e.getY();
                isDragging = true; // Sets isDragging to true
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isDragging = false; // Sets isDragging to false
                currX = e.getX();
                currY = e.getY();
                if (isOvalOn) { // If chosen shape is an Oval
                    draw_Oval(e.getX(), e.getY()); // Call the draw_Oval() method
                } else if (isRectOn) { // If chosen shape is a Rectangle
                    draw_Rect(e.getX(), e.getY()); // Call the draw_Rect() method
                }
                repaint(); // repaint() is a method to update the changes in canvas (BufferedImage)
            }

        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                currX = e.getX();
                currY = e.getY();
                if (isLineOn) { // If chosen shape is a Line
                    draw_Line(currX, currY); // Call the draw_Line() method
                }
                repaint(); // Update the canvas
            }
        });


        addComponentListener(new ComponentListener() {
            @Override
            // Handles the Resize logic of the window
            public void componentResized(ComponentEvent e) {
                canvas = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB); // Re-creates the BufferedImage with new Width and Height
                g2d = canvas.createGraphics(); // Re-creates the Graphics2d object
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,RenderingHints.VALUE_FRACTIONALMETRICS_ON);
                g2d.setStroke(new BasicStroke(strokeSize.getLineWidth(),BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
                g2d.setColor(strokeColor);
                g2d.setBackground(Color.WHITE);
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) { // Method that lets you customize how the components are drawn on the screen
        super.paintComponent(g); // To ensure that the screen panel is properly refreshed before drawing
        g.drawImage(canvas, 0, 0, null); // Draws the canvas at 0,0 coordinates (Top-Left Corner)

        if (isDragging){ // If mouse cursor is dragging
            if(isRectOn){ // If the chosen shape is a Rectangle
                int x1 = Math.min(prevX, currX); // Find min(prevX,currX) coz as we go further in Right-Direction the value increases, but we need the smaller value from two of them
                int y1 = Math.min(prevY, currY); // Find min(prevY,currY) coz as we go further in Bottom-Direction the value increases, but we need the smaller value from two of them
                // To find the distance between the two points
                int width = Math.abs(currX - prevX); // Width
                int height = Math.abs(currY - prevY); // Height

                g.drawRect(x1, y1, width, height); // Call drawRect() method of Graphics Class

            } else if(isOvalOn){ // If the chosen shape is an Oval
                int x1 = Math.min(prevX, currX); // Find min(prevX,currX) coz as we go further in Right-Direction the value increases, but we need the smaller value from two of them
                int y1 = Math.min(prevY, currY); // Find min(prevY,currY) coz as we go further in Bottom-Direction the value increases, but we need the smaller value from two of them
                // To find the distance between the two points
                int width = Math.abs(currX - prevX); // Width
                int height = Math.abs(currY - prevY); // Height
                g.drawOval(x1, y1, width, height); // Call the drawOval() method of Graphics Class
            }
        }
    }

    // Logic to clear the Canvas
    public void clearCanvas() {
        // setComposite() sets the alpha (transparency) blending mode
        // AlphaComposite.get.Instance(AlphaComposite.CLEAR) makes the entire drawing area fully Transparent
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
        // Fills the entire panel with the current composite settings(CLEAR), erasing the elements
        g2d.fillRect(0, 0, getWidth(), getHeight());
        // Sets the composite settings to SRC_OVER meaning that new element drawn will be over the destination (previously drawn element)
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        repaint();
    }

    // Logic to save as an Image
    public void saveImg() {
        try {
            JFileChooser fileChooser = new JFileChooser(); // Create fileChooser Instance
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // Sets only to choose directories_only
            fileChooser.setDialogTitle("Choose Folder"); // Sets the File Dialog Title
            fileChooser.setApproveButtonText("Save"); // Change Open button to Save button
            int returnVal = fileChooser.showOpenDialog(null);

            if(returnVal == JFileChooser.APPROVE_OPTION){
                File file = fileChooser.getSelectedFile(); // Get the selected file
                file = new File(file.getAbsoluteFile() + ".png");

                ImageIO.write(canvas, "PNG", file); // use ImageIO.write() method to save as png
                JOptionPane.showMessageDialog(this, "Image saved as " + file.getAbsoluteFile() + ".png"); // Shows the message to user
                fileCount++; // Increment the fileCount

                System.out.println(file.getAbsoluteFile());
            } else{
                System.out.println("No file selected.");
            }

        } catch (Exception e) {
            e.printStackTrace(); // Print the Exception
        }
    }

    // Logic to draw Oval
    public void draw_Oval(Integer x, Integer y) {
        int width = Math.abs(x - prevX);
        int height = Math.abs(y - prevY);
        g2d.drawOval(Math.min(prevX, x), Math.min(prevY, y), width, height);
    }

    // Logic to draw Line
    public void draw_Line(Integer x, Integer y) {
        g2d.drawLine(prevX, prevY, x, y);
        prevX = x;
        prevY = y;
    }

    // Logic to draw Rectangle
    public void draw_Rect(Integer x, Integer y) {
        int width = Math.abs(x - prevX);
        int height = Math.abs(y - prevY);
        g2d.drawRect(Math.min(prevX, x), Math.min(prevY, y), width, height);

    }

    // Setter method for isOvalOn
    public void setOvalOn() {
        isOvalOn = true;
        isLineOn = false;
        isRectOn = false;
    }
    // Setter method for isRectOn
    public void setRectOn() {
        isRectOn = true;
        isOvalOn = false;
        isLineOn = false;
    }
    // Setter method for isLineOn
    public void setLineOn() {
        isLineOn = true;
        isOvalOn = false;
        isRectOn = false;
    }
    // Setter method for strokeColor
    public void setStrokeColor(Color clr) {
        strokeColor = clr;
        g2d.setColor(strokeColor);
    }
    // Setter method for strokeSize
    public void setStrokeSize(int size) {
        strokeSize = new BasicStroke(size);
        g2d.setStroke(strokeSize);
    }

    // End of WhiteBoard Class
}


public class DrawingCanvas extends JFrame {

    public DrawingCanvas() { // Default Constructor
        // The main drawing area
        WhiteBoard board = new WhiteBoard(); // Creates the obj of class WhiteBoard named board

        // Buttons for clearing and saving the canvas
        JPanel buttonPanel = new JPanel();
        JButton clearButton = new JButton("Clear");
        JButton saveButton = new JButton("Save");

        // Creating the menu bar at the top
        JMenuBar shapeBar = new JMenuBar();

        // Menu for selecting different shapes to draw
        JMenu shapeMenu = new JMenu("Shapes");
        JMenuItem shapeOval = new JMenuItem("Oval ( )");
        JMenuItem shapeRect = new JMenuItem("Rectangle [ ]");
        JMenuItem shapeLine = new JMenuItem("Line ---");

        // Menu for Selecting different colors // 🟥🟧🟨🟩🟦⬛⬜
        JMenu shapeColorsMenu = new JMenu("Stroke Colors");
        JPanel shapeColorsPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        JButton colorRed = new JButton("Red");
        JButton colorOrange = new JButton("Orange");
        JButton colorYellow = new JButton("Yellow");
        JButton colorGreen = new JButton("Green");
        JButton colorCyan = new JButton("Cyan");
        JButton colorBlack = new JButton("Black");
        JButton colorWhite = new JButton("White");

        // Adding Colors options to menu
        shapeColorsPanel.add(colorRed);
        shapeColorsPanel.add(colorOrange);
        shapeColorsPanel.add(colorYellow);
        shapeColorsPanel.add(colorGreen);
        shapeColorsPanel.add(colorCyan);
        shapeColorsPanel.add(colorBlack);
        shapeColorsPanel.add(colorWhite);

        shapeColorsMenu.add(shapeColorsPanel);

        // Adding shape options to the menu
        shapeMenu.add(shapeOval);
        shapeMenu.add(shapeRect);
        shapeMenu.add(shapeLine);

        // Menu for changing stroke (line) size
        JMenu strokeMenu = new JMenu("Stroke Size");

        // Creating a panel to hold the text field
        JPanel strokePanel = new JPanel();
        JTextField strokeSizeText = new JTextField(5); // Small input box for stroke size
//        strokeSizeText.setPreferredSize(new Dimension(100, 20)); // Explicitly setting size

        // Adding text field to the panel and then to the menu
        strokePanel.add(strokeSizeText);
        strokeMenu.add(strokePanel); // Directly add the panel

        // Adding both menus to the menu bar
        shapeBar.add(shapeMenu);
        shapeBar.add(strokeMenu);
        shapeBar.add(shapeColorsMenu);
        setJMenuBar(shapeBar);

        // When "Oval" is clicked, switch to drawing ovals in red
        shapeOval.addActionListener(e -> board.setOvalOn());

        // When "Rectangle" is clicked, enable rectangle drawing
        shapeRect.addActionListener(e -> board.setRectOn());

        // When "Line" is clicked, enable line drawing
        shapeLine.addActionListener(e -> board.setLineOn());

        // Whenever the user types a number and presses Enter, update the stroke size
        strokeSizeText.addActionListener((ActionEvent e) -> {
            try {
                int strokeSize = Integer.parseInt(strokeSizeText.getText());
                board.setStrokeSize(strokeSize); // Update the brush thickness
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for stroke size!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // When "Clear" is clicked, wipe the entire canvas
        clearButton.addActionListener(e -> board.clearCanvas());

        // When "Save" is clicked, save the current drawing
        saveButton.addActionListener(e -> board.saveImg());

        // When Colors button is pressed
        colorRed.addActionListener(e->board.setStrokeColor(Color.RED));
        colorOrange.addActionListener(e->board.setStrokeColor(Color.ORANGE));
        colorYellow.addActionListener(e->board.setStrokeColor(Color.YELLOW));
        colorGreen.addActionListener(e->board.setStrokeColor(Color.GREEN));
        colorCyan.addActionListener(e->board.setStrokeColor(Color.CYAN));
        colorBlack.addActionListener(e->board.setStrokeColor(Color.BLACK));
        colorWhite.addActionListener(e->board.setStrokeColor(Color.WHITE));



        // Adding components to the frame
        add(board, BorderLayout.CENTER);
        buttonPanel.add(clearButton);
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Setting up the window
        setTitle("Virtual White Board");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public static void main(String[] args) {
        // Ensures that GUI related tasks run on Event Dispatch Tread (EDT)
        // Swing components must be created and updated on EDT to avoid UI freezes and unpredicted behaviors
        // If we create or modify the Swing components from main thread, the * Race Condition * can occur
        SwingUtilities.invokeLater(DrawingCanvas::new);
    }
}