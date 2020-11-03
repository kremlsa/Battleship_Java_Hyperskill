package battleship;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Boolean isInputOk = false;
        BattleField battleField = new BattleField();
        BattleField battleField2 = new BattleField();
        battleField.init();
        battleField2.init();
        String[] players = new String[] {"Player 1", "Player 2"};
        String coordinate = "";
        for (String player : players) {
            System.out.println(player + ", place your ships on the game field\n");
            if (player.equals("Player 1")) {
                battleField.printBf();
            } else {
                battleField2.printBf();
            }
            for (Ships ship : Ships.values()) {
                isInputOk = false;
                System.out.println("Enter the coordinates of the " + ship.getName() + " (" + ship.getLen() + " cells):\n");
                while (!isInputOk) {
                    if (player.equals("Player 1")) {
                        isInputOk = battleField.makeMove(scanner.nextLine(), ship.getLen(), ship.getName());
                    } else {
                        isInputOk = battleField2.makeMove(scanner.nextLine(), ship.getLen(), ship.getName());
                    }
                }
                System.out.println();
                if (player.equals("Player 1")) {
                    battleField.printBf();
                } else {
                    battleField2.printBf();
                }
            }
            System.out.println("Press Enter and pass the move to another player\n" +
                    "...");
            scanner.nextLine();
        }
        String playerTurn = "Player 1";
        while (!battleField.isWin() && !battleField2.isWin() ) {
            if (playerTurn.equals("Player 1")) {
                battleField2.printFog();
                System.out.println("---------------------");
                battleField.printBf();
                System.out.println();
                System.out.println(playerTurn + ", it's your turn:\n");
                isInputOk = false;
                while (!isInputOk) {
                    coordinate = scanner.nextLine();
                    isInputOk = battleField2.checkCoordinate(coordinate);
                }
                battleField2.shot(coordinate);
            } else {
                battleField.printFog();
                System.out.println("---------------------");
                battleField2.printBf();
                System.out.println();
                System.out.println(playerTurn + ", it's your turn:\n");
                isInputOk = false;
                while (!isInputOk) {
                    coordinate = scanner.nextLine();
                    isInputOk = battleField.checkCoordinate(coordinate);
                }
                battleField.shot(coordinate);
            }
            System.out.println("Press Enter and pass the move to another player\n" +
                    "...\n");
            scanner.nextLine();
            playerTurn = playerTurn.equals("Player 1") ? "Player 2" : "Player 1";
        }
    }
}

class BattleField {
    String[][] bf;
    int size = 10;
    ArrayList<Ship> shipList;

    public void init() {
        shipList = new ArrayList<Ship>();
        bf = new String[size][size];
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                bf[y][x] = "~";
            }
        }
    }

    public void printBf() {
        System.out.println("  1 2 3 4 5 6 7 8 9 10");
        for (int y = 0; y < size; y++) {
            System.out.print(Character.toUpperCase(Character.forDigit(10 + y, 36)));
            for (int x = 0; x < size; x++) {
                System.out.print(" " + bf[y][x]);
            }
            System.out.println();
        }
    }

    public void printFog() {
        System.out.println("  1 2 3 4 5 6 7 8 9 10");
        for (int y = 0; y < size; y++) {
            System.out.print(Character.toUpperCase(Character.forDigit(10 + y, 36)));
            for (int x = 0; x < size; x++) {
                if (bf[y][x].equals("X") || bf[y][x].equals("M")) {
                    System.out.print(" " + bf[y][x]);
                } else {
                    System.out.print(" ~");
                }
            }
            System.out.println();
        }
    }

    public Boolean makeMove(String input, int length, String shipName) {
        int x1 = Integer.parseInt(input.split(" ")[0].substring(1)) - 1;
        int x2 = Integer.parseInt(input.split(" ")[1].substring(1)) - 1;
        int y1 = Character.getNumericValue(input.split(" ")[0].toLowerCase().charAt(0)) - 10;
        int y2 = Character.getNumericValue(input.split(" ")[1].toLowerCase().charAt(0)) - 10;
        Ship ship = new Ship(shipName);

        if (checkMove(x1, x2, y1, y2, length, shipName)) {
            if (x1 == x2 && y1 != y2) {
                int start = Math.min(y1, y2);
                int finish = Math.max(y1, y2);
                for (int i = start; i <= finish; i++) {
                    bf[i][x1] = "O";
                    ship.addCoord(String.valueOf(i) + String.valueOf(x1));
                }
                shipList.add(ship);
                return true;
            } else if (x1 != x2 && y1 == y2) {
                int start = Math.min(x1, x2);
                int finish = Math.max(x1, x2);
                for (int i = start; i <= finish; i++) {
                    bf[y1][i] = "O";
                    ship.addCoord(String.valueOf(y1) + String.valueOf(i));
                }
                shipList.add(ship);
                return true;
            }
        }
        return false;
    }

    public boolean checkMove(int x1, int x2, int y1, int y2, int length, String shipName) {
        if (x1 != x2 && y1 != y2) {
            System.out.println("Error! Wrong ship location! Try again:\n");
            return false;
        }

        if (x1 == x2 && y1 != y2 && (Math.max(y1, y2) - Math.min(y1, y2) != length - 1)) {
            System.out.println("Error! Wrong length of the " + shipName + "! Try again:\n");
            return false;
        }
        if (x1 != x2 && y1 == y2 && (Math.max(x1, x2) - Math.min(x1, x2) != length - 1)) {
            System.out.println("Error! Wrong length of the " + shipName + "! Try again:\n");
            return false;
        }

        if (x1 == x2 && y1 != y2) {
            int start = Math.min(y1, y2);
            int finish = Math.max(y1, y2);
            for (int i = start; i <= finish; i++) {
                if (isNeighbour(x1, i)) {
                    System.out.println("Error! You placed it too close to another one. Try again:\n");
                    return false;
                }
            }
        }

        if (x1 != x2 && y1 == y2) {
            int start = Math.min(x1, x2);
            int finish = Math.max(x1, x2);
            for (int i = start; i <= finish; i++) {
                if (isNeighbour(i, y1)) {
                    System.out.println("Error! You placed it too close to another one. Try again:\n");
                    return false;
                }
            }
        }
        return true;
    }

    boolean isOccupy(int x, int y) {
        return bf[y][x].equals("O") || bf[y][x].equals("X");
    }

    boolean isNeighbour(int x, int y) {
        if (x > 0 && x < size - 1 && y > 0 && y < size - 1) {
         if (bf[y + 1][x].equals("O") || bf[y + 1][x + 1].equals("O") || bf[y + 1][x - 1].equals("O") ||
                 bf[y][x + 1].equals("O") || bf[y][x - 1].equals("O") || bf[y][x].equals("O") ||
                 bf[y - 1][x -1].equals("O") || bf[y - 1][x].equals("O") || bf[y - 1][x +1].equals("O")) {
             return true;
         }
        }
        return false;
    }

    public boolean checkCoordinate(String coordinate) {
        int x = Integer.parseInt(coordinate.split(" ")[0].substring(1)) - 1;
        int y = Character.getNumericValue(coordinate.split(" ")[0].toLowerCase().charAt(0)) - 10;
        return x >= 0 && x <10 && y >= 0 && y <10;
    }

    public String makeHit(String coord) {
        String shipName = "";
        for(Ship ship : shipList) {
            if (ship.isShipsCoord(coord)) {
                ship.hits(coord);
                shipName = ship.name;
            }
        }
        return shipName;
    }

    public Boolean isSink(String shipName) {
        for(Ship ship : shipList) {
            if (ship.name.equals(shipName)) {
                return ship.isSink();
            }
        }
        return false;
    }

    public Boolean isWin() {
        for(Ship ship : shipList) {
            if (!ship.isSink()) {
                return false;
            }
        }
        return true;
    }

    public void shot(String coordinate) {
        int x = Integer.parseInt(coordinate.split(" ")[0].substring(1)) - 1;
        int y = Character.getNumericValue(coordinate.split(" ")[0].toLowerCase().charAt(0)) - 10;
        if (isOccupy(x, y)) {
            bf[y][x] = "X";
            String shipName = makeHit(String.valueOf(y) + String.valueOf(x));
            if (isWin()) {
                System.out.println("You sank the last ship. You won. Congratulations!\n");
            } else if (isSink(shipName)) {
                System.out.println("You sank a ship! Specify a new target:\n");
            } else {
                System.out.println("You hit a ship!\n");
            }
        } else {
            bf[y][x] = "M";
            System.out.println("You missed!");
        }
    }
}

class Ship {
    ArrayList<String> coords = new ArrayList<String>();
    String name;
    Ship (String name) {
        this.name = name;
    }
    public void addCoord(String coord) {
        coords.add(coord);
    }
    public void hits(String coord) {
        coords.remove(coord);
    }
    public Boolean isSink() {
        return coords.size() == 0;
    }
    public Boolean isShipsCoord(String coord) {
        return coords.contains(coord);
    }
}


enum Ships {
    AIR(5, "Aircraft Carrier"),
    BS(4, "Battleship"),
    SUB(3, "Submarine"),
    CRU(3, "Cruiser"),
    DEST(2, "Destroyer");

    int length;
    String shipName;

    Ships(int length, String shipName) {
        this.length = length;
        this.shipName = shipName;
    }

    public int getLen() {
        return length;
    }

    public String getName() {
        return shipName;
    }
}