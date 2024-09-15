import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.awt.Color;

public class lab1 {
    static class MapColor {
        public String name;
        public double speed;

        public MapColor(String name, double speed) {
            this.name = name;
            this.speed = speed;
        }
    }

    public static class Node implements Comparable<Node> {
        public Node parent;
        public int x;
        public int y;
        public double timeToReach;
        public double totalDistance;
        public double f;
        public String name;
        public double speed;

        public Node(int x, int y, String name, double speed) {
            this.parent = null;
            this.x = x;
            this.y = y;
            this.name = name;
            this.speed = speed;
        }

        public Node(int x, int y, Node parent, String name, double speed) {
            this.parent = parent;
            this.x = x;
            this.y = y;
            this.name = name;
            this.speed = speed;
        }



        public int compareTo(Node other) {
            return Double.compare(this.f, other.f);
        }

        public void settimeToReach(double timeToReach)
        {
            this.timeToReach = timeToReach;
        }

        public void setF(double f)
        {
            this.f = f;
        }

        public void setParent(Node node){
            this.parent = node;
        }

        public void setTotalDistance(double newTotalDistance)
        {
            this.totalDistance = newTotalDistance;
        }

    }

    static MapColor OPEN_LAND_COLOR = new MapColor("#F89412", 4.82803);
    static MapColor ROUGH_MEADOW = new MapColor("#FFC000", 3.21869);
    static MapColor EASY_MOVEMENT_FOREST = new MapColor("#FFFFFF", 4.02336);
    static MapColor SLOW_RUN_FOREST = new MapColor("#02D03C", 3.21869);
    static MapColor WALK_FOREST = new MapColor("#028828", 2.41402);
    static MapColor IMPASSIBLE_VEGETATION = new MapColor("#054918", 0.804672);
    static MapColor LAKE_SWAMP_MARSH = new MapColor("#0000FF", 1.60934);
    static MapColor PAVED_ROAD = new MapColor("#473303", 6.43738);
    static MapColor FOOTPATH = new MapColor("#000000", 5.6327);
    static MapColor OB = new MapColor("#CD0065", 0);

    public static Node findPath(MapColor[][] mapArray, int startX, int startY, int targetX, int targetY) {
        PriorityQueue<Node> queue = new PriorityQueue<>();
        Map<String, Node> visitedNodes = new HashMap<>(); // Keeps track of visited nodes with their shortest path
        Node start = new Node(startX, startY, mapArray[startX][startY].name, mapArray[startX][startY].speed);
        start.settimeToReach(0);
        start.setF(0);
        int rows = mapArray.length;
        int cols = mapArray[0].length;
        queue.add(start);
        visitedNodes.put(start.x + "," + start.y, start);
    
        while (!queue.isEmpty()) {
            Node curNode = queue.poll();
    
            if (curNode.x == targetX && curNode.y == targetY) {
                return curNode;
            }
    
            for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
                for (int colOffset = -1; colOffset <= 1; colOffset++) {
                    if (rowOffset == 0 && colOffset == 0) {
                        continue;
                    }
    
                    int newRow = curNode.x + rowOffset;
                    int newCol = curNode.y + colOffset;
    
                    if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                        double newTimeToReach = curNode.timeToReach;
                        double newTotalDistance = curNode.totalDistance;
                        if (rowOffset == 0 && colOffset != 0) {
                            newTimeToReach += 0.01029 / mapArray[newRow][newCol].speed;
                            newTotalDistance += 0.01029;
                        } else if (rowOffset != 0 && colOffset == 0) {
                            newTimeToReach += 0.00755 / mapArray[newRow][newCol].speed;
                            newTotalDistance += 0.00755;
                        } else if (rowOffset != 0 && colOffset != 0) {
                            newTimeToReach += 0.01276 / mapArray[newRow][newCol].speed;
                            newTotalDistance += 0.012765;
                        }
    
                        String nodeKey = newRow + "," + newCol;
                        Node newNode = visitedNodes.get(nodeKey);
    
                        if (newNode == null || newTimeToReach < newNode.timeToReach) {
                            if (newNode == null) {
                                newNode = new Node(newRow, newCol, curNode, mapArray[newRow][newCol].name, mapArray[newRow][newCol].speed);
                            }
                            newNode.timeToReach = newTimeToReach;
                            newNode.setTotalDistance(newTotalDistance);
                            newNode.setParent(curNode);
                            newNode.setF(newTimeToReach + heuristic(newNode.x, newNode.y, targetX, targetY));
                            queue.add(newNode);
                            visitedNodes.put(nodeKey, newNode);
                        }
                    }
                }
            }
        }
        return null;
    }
    

    static double heuristic(int x, int y, int targetX, int targetY) {
        return Math.sqrt(Math.pow(x - targetX, 2) + Math.pow(y - targetY, 2));
    }

    public static void makePath(Node endNode, BufferedImage image) {
        Node currentNode = endNode;
        
        while (currentNode != null) {
            image.setRGB(currentNode.y, currentNode.x, Color.PINK.getRGB()); // x is row, y is column
            currentNode = currentNode.parent;
        }
    }

    public static void main(String[] args) {
        String terrainImageName = "terrain.png";
        String pathFileName = "path.txt";
        MapColor[][] mapArray = new MapColor[500][395];

        try {
            BufferedImage image = ImageIO.read(new File(terrainImageName));

            for (int y = 0; y < image.getHeight() && y < 500; y++) {
                for (int x = 0; x < image.getWidth() && x < 395; x++) {
                    int pixelColor = image.getRGB(x, y);
                    String hexColor = String.format("#%06X", (0xFFFFFF & pixelColor));
                    mapArray[y][x] = new MapColor(hexColor, 0); // Switched from mapArray[x][y] to mapArray[y][x]
                }
            }            

            Scanner pathScan = new Scanner(new File(pathFileName));
            ArrayList<int[]> targets = new ArrayList<>();
            while (pathScan.hasNext()) {
                targets.add(new int[]{pathScan.nextInt(), pathScan.nextInt()});
            }
            pathScan.close();
            int startX = targets.get(0)[0];
            int startY = targets.get(0)[1];
            double totalDist = 0;
            for (int i = 1; i < targets.size(); i++) {
                Node completed = findPath(mapArray, startX, startY, targets.get(i)[0], targets.get(i)[1]); // Fix: Y-coordinate for target
                
                if (completed != null) {
                    totalDist += completed.timeToReach;
                    makePath(completed, image);  // Pass the image to draw the path
                    startX = targets.get(i)[0];
                    startY = targets.get(i)[1];
                }
            }
    
            // Save the image with the path drawn
            File outputFile = new File(terrainImageName+"_solution.png");
            ImageIO.write(image, "png", outputFile);
            System.out.println("Path drawn and saved to: " + terrainImageName+"_solution");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
