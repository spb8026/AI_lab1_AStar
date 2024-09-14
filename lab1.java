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

    public static class Node implements Comparable<Node> {
        public Node parent;
        public int x;
        public int y;
        public double heuristic;

        public Node(int x, int y, double heuristic) {
            this.parent = null;
            this.x = x;
            this.y = y;
            this.heuristic = heuristic;
        }

        public Node(int x, int y, Node parent, double heuristic) {
            this.parent = parent;
            this.x = x;
            this.y = y;
            this.heuristic = heuristic;
        }

        public int compareTo(Node other) {
            return Double.compare(this.heuristic, other.heuristic);
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

    static double heuristic(int x, int y, int targetX, int targetY) {
        return Math.sqrt(Math.pow(x - targetX, 2) + Math.pow(y - targetY, 2));
    }

    public static Node findPath(MapColor[][] mapArray, int startX, int startY, int targetX, int targetY) {
        PriorityQueue<Node> queue = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();
        Node start = new Node(startX, startY, heuristic(startX, startY, targetX, targetY));
        int rows = mapArray.length;
        int cols = mapArray[0].length;
        queue.add(start);
        visited.add(startX + "," + startY);

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
                        String nodeKey = newRow + "," + newCol;
                        if (!visited.contains(nodeKey)) {
                            queue.add(new Node(newRow, newCol, curNode, heuristic(newRow, newCol, targetX, targetY) + mapArray[newRow][newCol].speed));
                            visited.add(nodeKey);
                        }
                    }
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String terrainImageName = "terrain.png";
        String pathFileName = "brown.txt";
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

            int[] lastTarget = targets.get(targets.size() - 1);
            Node path = findPath(mapArray, 0, 0, lastTarget[0], lastTarget[1]);

            if (path != null) {
                System.out.println("Path found! Final position: (" + path.x + ", " + path.y + ")");
            } else {
                System.out.println("Path not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
