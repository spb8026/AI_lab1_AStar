import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.awt.Color;

public class lab1 {
    static class MapColor {
        public String color;
        public double speed;
        public double elevation;

        public MapColor(String color, double speed) {
            this.color = color;
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

    static MapColor OPEN_LAND_COLOR = new MapColor("#F89412", 5.5);
    static MapColor ROUGH_MEADOW = new MapColor("#FFC000", 2.5);
    static MapColor EASY_MOVEMENT_FOREST = new MapColor("#FFFFFF", 5);
    static MapColor SLOW_RUN_FOREST = new MapColor("#02D03C", 4);
    static MapColor WALK_FOREST = new MapColor("#028828", 3);
    static MapColor IMPASSIBLE_VEGETATION = new MapColor("#054918", 1);
    static MapColor LAKE_SWAMP_MARSH = new MapColor("#0000FF", .2);
    static MapColor PAVED_ROAD = new MapColor("#473303", 7);
    static MapColor FOOTPATH = new MapColor("#000000", 6);
    static MapColor OB = new MapColor("#CD0065", 0);

    public static Node findPath(MapColor[][] mapArray, double[][] elevationValues, int startX, int startY, int targetX, int targetY) {
        PriorityQueue<Node> queue = new PriorityQueue<>();
        Map<String, Node> visitedNodes = new HashMap<>(); 
        Node start = new Node(startX, startY, mapArray[startY][startX].color, mapArray[startY][startX].speed);
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

                    int newRow = curNode.y + rowOffset;
                    int newCol = curNode.x + colOffset;
                    
    
                    if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                        double horizontalDistance = 0;
                        if (rowOffset == 0 && colOffset != 0) {
                            horizontalDistance = 10.29; 
                        } else if (rowOffset != 0 && colOffset == 0) {
                            horizontalDistance = 7.55; 
                        } else if (rowOffset != 0 && colOffset != 0) {
                            horizontalDistance = 12.7627034754; 
                        }

                        double elevationDifference = elevationValues[newRow][newCol] - elevationValues[curNode.y][curNode.x];
                        double distance3D = Math.sqrt(Math.pow(horizontalDistance, 2) + Math.pow(elevationDifference, 2));
    
                        double newTimeToReach = curNode.timeToReach + distance3D / mapArray[newRow][newCol].speed;
                        double newTotalDistance = curNode.totalDistance + distance3D;
    
                        String nodeKey = newRow + "," + newCol;
                        Node newNode = visitedNodes.get(nodeKey);
    
                        if (newNode == null || newTimeToReach < newNode.timeToReach) {
                            if (newNode == null) {
                                newNode = new Node(newCol, newRow, curNode, mapArray[newRow][newCol].color, mapArray[newRow][newCol].speed);
                            }
                            newNode.timeToReach = newTimeToReach;
                            newNode.setTotalDistance(newTotalDistance);
                            newNode.setParent(curNode);
    
                            double heuristicValue = heuristic(newCol, newRow, elevationValues[newRow][newCol], targetX, targetY, elevationValues[targetY][targetX]);
                            newNode.setF(newTimeToReach + heuristicValue);
    
                            queue.add(newNode);
                            visitedNodes.put(nodeKey, newNode);
                        }
                    }
                }
            }
        }
        return null;
    }

    static double getDistance(int x, int y,int z, int x_2, int y_2, int z_2)
    {
        return Math.sqrt(Math.pow(x-x_2, 2)+Math.pow(y-y_2, 2)+Math.pow(z-z_2, 2));
    }
    

static double heuristic(int x, int y, double z, int targetX, int targetY, double targetZ) {
    return Math.sqrt(Math.pow(x - targetX, 2) * Math.pow(10.29, 2) + Math.pow(y - targetY, 2) * Math.pow(7.55, 2) + Math.pow(z - targetZ, 2));
}

    public static void makePath(Node endNode, BufferedImage image, int[] targets) {
        Node currentNode = endNode;
        while (currentNode != null) {
            if (currentNode.y >= 0 && currentNode.y < image.getHeight() && currentNode.x >= 0 && currentNode.x < image.getWidth()) {
            image.setRGB(currentNode.x, currentNode.y, new Color(118,63,231).getRGB());
            }
            currentNode = currentNode.parent;
        }
    }

    public static void drawTargets(BufferedImage image, int[] targets)
    {
        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int colOffset = -1; colOffset <= 1; colOffset++) {
                if (targets[1]+rowOffset >= 0 && targets[1]+rowOffset < image.getHeight() && targets[0]+colOffset >= 0 && targets[0]+colOffset < image.getWidth()) {
                    
                        image.setRGB(targets[0]+ colOffset, targets[1]+ rowOffset, Color.BLACK.getRGB());
                    }
            }
        }
    }

    public static void main(String[] args) {
        String terrainImageName = args[0];
        String elevationFileName = args[1];
        String pathFileName = args[2];
        String output_image_filename = args[3];
        // String terrainImageName = "terrain.png";
        // String elevationFileName = "mpp.txt";
        // String pathFileName = "path.txt";
        // String output_image_filename = "terrain_solution.png";
        MapColor[][] mapArray = new MapColor[500][395];
        double[][] elevationValues = new double[500][395];

        try {
            BufferedImage image = ImageIO.read(new File(terrainImageName));

            for (int y = 0; y < image.getHeight() && y < 500; y++) {
                for (int x = 0; x < image.getWidth() && x < 395; x++) {
                    int pixelColor = image.getRGB(x, y);
                    String hexColor = String.format("#%06X", (0xFFFFFF & pixelColor));
                    switch (hexColor) {
                        case "#F89412": mapArray[y][x] = OPEN_LAND_COLOR; break;
                        case "#FFC000": mapArray[y][x] = ROUGH_MEADOW; break;
                        case "#FFFFFF": mapArray[y][x] = EASY_MOVEMENT_FOREST; break;
                        case "#02D03C": mapArray[y][x] = SLOW_RUN_FOREST; break;
                        case "#028828": mapArray[y][x] = WALK_FOREST; break;
                        case "#054918": mapArray[y][x] = IMPASSIBLE_VEGETATION; break;
                        case "#0000FF": mapArray[y][x] = LAKE_SWAMP_MARSH; break;
                        case "#473303": mapArray[y][x] = PAVED_ROAD; break;
                        case "#000000": mapArray[y][x] = FOOTPATH; break;
                        default: mapArray[y][x] = OB; break; 
                    }
                }
            }
            Scanner elevationScan = new Scanner(new File(elevationFileName));
            for (int y = 0; y < 500; y++)
            {
                for (int x = 0; x < 395; x++)
                {
                 elevationValues[y][x] = elevationScan.nextDouble();
                }
                elevationScan.nextDouble();
                elevationScan.nextDouble();
                elevationScan.nextDouble();
                elevationScan.nextDouble();
                elevationScan.nextDouble();
            }  
            elevationScan.close();

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
                Node completed = findPath(mapArray, elevationValues, startX, startY, targets.get(i)[0], targets.get(i)[1]);
                
                if (completed != null) {
                    totalDist += completed.totalDistance;
                    makePath(completed, image,targets.get(i)); 
                    startX = targets.get(i)[0];
                    startY = targets.get(i)[1];
                    // drawTargets(image, targets.get(i));
                }
            }
            System.out.println(totalDist);
            File outputFile = new File(output_image_filename);
            ImageIO.write(image, "png", outputFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
