import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;


public class lab1 {
    static class MapColor {
        public String name;
        public int speed;

        public MapColor(String name, int speed) {
            this.name = name;
            this.speed = speed;
        }
    }

    public static class Node {
        public String name;
        public Node parent;
        public int x;
        public int y;
        public double heuristic;

        public Node(String name,int x, int y, double heuristic) {
            this.name = name;
            this.parent = null;
            this.x = x;
            this.y = y;
            this.heuristic = heuristic;
        }

        public Node(String name,int x, int y,Node parent,double heuristic) {
            this.name = name;
            this.parent = parent;
            this.x = x;
            this.y = y;
            this.heuristic = heuristic;
        }

        public void setNodeParent(Node parent) {
            this.parent = parent;
        }

        public String getName() {
            return name;
        }

        public Node getParent() {
            return parent;
        }
    }

    static MapColor OPEN_LAND_COLOR = new MapColor("#F89412", 1);
    static MapColor ROUGH_MEADOW = new MapColor("#FFC000", 1);
    static MapColor EASY_MOVEMENT_FOREST = new MapColor("#FFFFFF", 1);  // Corrected hex color
    static MapColor SLOW_RUN_FOREST = new MapColor("#02D03C", 1);
    static MapColor WALK_FOREST = new MapColor("#028828", 0);
    static MapColor IMPASSIBLE_VEGETATION = new MapColor("#054918", 1);
    static MapColor LAKE_SWAMP_MARSH = new MapColor("#0000FF", 1);
    static MapColor PAVED_ROAD = new MapColor("#473303", 1);
    static MapColor FOOTPATH = new MapColor("#000000", 1);
    static MapColor OB = new MapColor("#CD0065", 1);

    static double heuristic(int x, int y, int target_x, int target_y)
    {
        return Math.sqrt(Math.pow(x-target_x,2)+Math.pow(y-target_y, 2));
    }

    public int findPath(MapColor[][] mapArray, int target_x, int target_y)
    {
        PriorityQueue<Node> queue = new PriorityQueue<>();
        Set<Node> visited = new HashSet<>();
        Node start = new Node(mapArray[0][0].name,0,0,heuristic(target_x, target_y, 0, 0));
        int rows = mapArray.length;
        int cols = mapArray[0].length;
        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty()) {
            Node curNode = queue.poll();
            if (curNode.x == target_x && curNode.y == target_y)
            {
                break;
            }
            for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
                for (int colOffset = -1; colOffset <= 1; colOffset++) {
                    if (rowOffset == 0 && colOffset == 0) {
                        continue;
                    }
    
                    int newRow = curNode.x + rowOffset;
                    int newCol = curNode.y + colOffset;
    
                    if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols) {
                        queue.add(new Node(mapArray[newRow][newCol].name, newRow, newCol, curNode, heuristic(newRow, newCol, target_x, target_y) +mapArray[newRow][newCol].speed))
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        String terrainImageName = "terrain.png";
        String pathFileName = "brown.txt";
        // String elevationFileName = args[1];
        // String pathFileName = args[2];
        // String outputImageFilename = args[3];
        MapColor[][] mapArray = new MapColor[395][500];

        try {
            BufferedImage image = ImageIO.read(new File(terrainImageName));

            for (int y = 0; y < image.getHeight() && y < 395; y++) {
                for (int x = 0; x < image.getWidth() && x < 500; x++) {
                    int pixelColor = image.getRGB(x, y);
                    String hexColor = String.format("#%06X", (0xFFFFFF & pixelColor));
                    mapArray[x][y] = new MapColor(hexColor, 0);
                }
            }
            Scanner pathScan = new Scanner(new File(pathFileName));
            ArrayList<int[]> targets = new ArrayList<>();
            while (pathScan.hasNext()) {
                targets.add(new int[]{pathScan.nextInt(), pathScan.nextInt()});
            }
            pathScan.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
