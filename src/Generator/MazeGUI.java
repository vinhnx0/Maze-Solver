package Generator;

import Solver.*;
import java.awt.*;
import javax.swing.*;

public class MazeGUI extends JFrame {
    private JComboBox<String> dimensionComboBox;
    private JButton generateButton, solveButton, resetButton;
    private JComboBox<String> solverComboBox;
    private JPanel mazePanel;
    private MazeGenerator generated;
    private Cell[][] maze;

    public MazeGUI(){
        setTitle("Maze Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(515, 650);
        setLocationRelativeTo(null);

        // Create the maze panel
        mazePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (maze != null) {
                    drawMaze(g);
                }
            }
        };
        mazePanel.setPreferredSize(new Dimension(500, 500));

        // Create control panel
        JPanel controlPanel = createControlPanel();

        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.add(mazePanel, BorderLayout.CENTER);
        contentPane.add(controlPanel, BorderLayout.SOUTH);
        setContentPane(contentPane);
    }

    // Methods
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        String[] dimensions = {"5", "10", "15", "20", "25", "30", "40", "50"};
        dimensionComboBox = new JComboBox<>(dimensions);

        generateButton = new JButton("Generate Maze");
        generateButton.addActionListener(e -> generateMaze());

        String[] solvers = {"Dijkstra's Algorithm", "BFS", "DFS"};
        solverComboBox = new JComboBox<>(solvers);

        solveButton = new JButton("Solve Maze");
        solveButton.addActionListener(e -> solveMaze());

        resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> resetMaze());

        controlPanel.add(new JLabel("Maze Dimension:"));
        controlPanel.add(dimensionComboBox);
        controlPanel.add(generateButton);
        controlPanel.add(new JLabel("Solver:"));
        controlPanel.add(solverComboBox);
        controlPanel.add(solveButton);

        return controlPanel;
    }

    private void generateMaze(){
        int dims = Integer.parseInt((String) dimensionComboBox.getSelectedItem());
        MazeGenerator.MAZE_SIZE = dims;
        Cell.CELL_DIMS = 500 / dims;
        generated = new MazeGenerator();
        maze = generated.maze;
        mazePanel.repaint();
    }

    private void solveMaze(){
        long startTime = System.nanoTime();
        int selectedIndex = solverComboBox.getSelectedIndex();
        maze = switch (selectedIndex) {
            case 0 -> new DijkstraSolve(maze).maze;
            case 1 -> new BFSSolver(maze).maze;
            case 2 -> new DFSSolver(maze).maze;
            default -> throw new IllegalArgumentException("Unexpected value: " + selectedIndex);
        };
        long endTime = System.nanoTime();
        System.out.println("Solving maze took: " + (endTime - startTime) / 1000000 + " ms");
        mazePanel.repaint();
    }

    private void resetMaze() {
        maze = generated.maze;
        mazePanel.repaint();
    }

    private void drawMaze(Graphics g) {
        for (Cell[] cells : maze) {
            for (Cell cell : cells) {
                cell.drawCell(g);
            }
        }
        maze[0][0].setCellColor(g, Color.GREEN); // starting cell is green
        maze[maze.length -1][maze.length - 1].setCellColor(g, Color.RED); // end cell is red
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            try {
                new MazeGUI().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
