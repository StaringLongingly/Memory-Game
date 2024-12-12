import java.util.*;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

// A Slot is a letter with some extra info
class Slot {
    public char symbol;
    public boolean peek;
    public boolean revealed;

    public Slot(char symbol) {
        this.symbol = symbol;
        this.peek = false;
        this.revealed = false;
    }

    public Slot() {
        this.peek = false;
        this.revealed = false;
    }

    public String toString() {
        if (peek || revealed) {
            peek = false;
            return Character.toString(symbol);
        } else {
            return "*";
        }
    }
}

// Handles initialization and gameplay
class GameManager {
    public Slot[][] slots;

    // Constructor Initializes the Slots
    // reveal: whether to reveal all slots from the start
    // consolePrint: unused in this snippet but can be used for logging
    public GameManager(int gameSizeX, int gameSizeY, int shuffleCount, boolean reveal, boolean consolePrint) {
        System.out.println("Creating a New Game!");
        slots = new Slot[gameSizeY][gameSizeX];

        // Total number of slots
        int totalSlots = gameSizeX * gameSizeY;
        // We want pairs, so totalSlots should be even for a perfect pairing.
        // If it's odd, one slot won't have a pair (this code will still run, but it’s unusual).
        // Assuming even number of total slots for a memory game.
        int pairCount = totalSlots / 2;

        // Generate pairs of random characters
        List<Character> charList = new ArrayList<Character>();
        Random random = new Random();
        for (int i = 0; i < pairCount; i++) {
            char randomChar = (char) (random.nextInt(26) + 'A');
            charList.add(randomChar);
            charList.add(randomChar);
        }

        // Shuffle the generated characters
        Collections.shuffle(charList, random);

        // Fill the slots
        int index = 0;
        for (int i = 0; i < slots.length; i++) {
            for (int j = 0; j < slots[i].length; j++) {
                slots[i][j] = new Slot(charList.get(index));
                slots[i][j].revealed = reveal;
                index++;
            }
        }

        // Shuffle slots a bit more if needed
        for (int i = 0; i < shuffleCount; i++) {
            shuffle();
        }
    }

    // Shuffles two random slots
    public void shuffle() {
        Random random = new Random();
        int x1 = random.nextInt(slots.length);
        int x2 = random.nextInt(slots.length);

        int y1 = random.nextInt(slots[0].length);
        int y2 = random.nextInt(slots[0].length);

        char tmp = slots[x1][y1].symbol;
        slots[x1][y1].symbol = slots[x2][y2].symbol;
        slots[x2][y2].symbol = tmp;
    }

    // Handles input and the main gameplay
    public void gameplayLoop() {
        Scanner in = new Scanner(System.in);
        int tries = 0;
        while (!allRevealed()) {
            printSlotsConsole();
            tries++;

            int comparison;
            int x1, x2, y1, y2;
            do {
                // Prompt Player
                System.out.print("Give the first row and column: ");
                x1 = in.nextInt();
                y1 = in.nextInt();

                System.out.print("Give the second row and column: ");
                x2 = in.nextInt();
                y2 = in.nextInt();

                comparison = compareSlots(x1, y1, x2, y2);
            } while (comparison == -1);

            if (comparison == 0) {
                slots[x1][y1].peek = true;
                slots[x2][y2].peek = true;
            } else if (comparison == 1) {
                slots[x1][y1].revealed = true;
                slots[x2][y2].revealed = true;
            }
        }
        in.close();
        System.out.println("Successfully revealed all letters!");
        System.out.println("It took you " + tries + " tries!");
    }

    // Returns -1 for invalid input, 0 for non equal, 1 for equal
    public int compareSlots(int x1, int y1, int x2, int y2) {
        // Check bounds
        if (x1 < 0 || x1 >= slots.length ||
            x2 < 0 || x2 >= slots.length ||
            y1 < 0 || y1 >= slots[0].length ||
            y2 < 0 || y2 >= slots[0].length) {
            System.out.println("Invalid coordinates! Please try again:");
            return -1;
        }

        // Check if same slot chosen twice
        if (x1 == x2 && y1 == y2) {
            System.out.println("You cannot give the same coordinates for both letters. Try again..");
            return -1;
        }

        // Compare symbols
        if (slots[x1][y1].symbol == slots[x2][y2].symbol) {
            System.out.println("Successfully revealed a pair!");
            return 1;
        } else {
            System.out.println("Not the same letter. Try again..");
            return 0;
        }
    }

    // Checks if all slots are revealed
    public boolean allRevealed() {
        for (int i = 0; i < slots.length; i++) {
            for (int j = 0; j < slots[i].length; j++) {
                if (!slots[i][j].revealed) {
                    return false; // Found at least one unrevealed
                }
            }
        }
        return true; // No unrevealed found
    }

    // Prints and formats all the slots
    public void printSlotsConsole() {
      System.out.print("  │ ");
      for (int j = 0; j < slots[1].length; j++) {
        System.out.print(j + " │ ");
      }
      System.out.print("\n");
  
      for (int j = 0; j < slots[1].length + 1; j++) {
        System.out.print("──┼─");
      }
      System.out.print("\n");
  
      for (int i = 0; i < slots.length * 2; i++) {
        if (i % 2 == 0)
          System.out.print(i / 2 + " │ ");
        else
          System.out.print("──┼─");
  
        for (int j = 0; j < slots[1].length; j++) {
          if (i % 2 == 0)
            System.out.print(slots[i / 2][j] + " │ ");
          else
            System.out.print("──┼─");
        }
        System.out.print("\n");
      }
    }
}

// Responsible for starting the game
public class Main {
    public static void main(String args[]) {
        // Example: 4 columns, 5 rows, 10 shuffles, no reveal at start
        GameManager gameManager = new GameManager(4, 5, 10, false, false);
        gameManager.gameplayLoop();
    }
}