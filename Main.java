import java.util.*;

// A Slot is a letter with some extra info
class Slot {
  public char symbol;
  public boolean peek;
  public boolean revealed;

  public Slot(char symbol) {
    peek = false;
    revealed = false;
    this.symbol = symbol;
  }

  public Slot() {
    peek = false;
    revealed = false;
  }

  public String toString() {
    if (peek || revealed) {
      peek = false;
      return Character.toString(symbol);
    } else
      return "*";
  }
}

// Handles initialization and gameplay
class GameManager {
  public Slot[][] slots;

  // Constructor Initializes the Slots, reveal is for revealing everything from
  // the start
  public GameManager(int gameSizeX, int gameSizeY, int shuffleCount, boolean reveal) {
    System.out.println("Creating a New Game!");
    slots = new Slot[gameSizeY][gameSizeX];

    List<Character> charList = new ArrayList<Character>();
    for (int i = 0; i < slots.length * slots[1].length / 2 + 1; i++) {
      Random random = new Random();
      char randomChar = (char) (random.nextInt(26) + 'A');
      charList.add(randomChar);
      charList.add(randomChar);
    }

    for (int i = 0; i < slots.length; i++) {
      for (int j = 0; j < slots[1].length; j++) {
        slots[i][j] = new Slot();
        slots[i][j].symbol = charList.get(charList.size() - 1);
        slots[i][j].symbol = charList.remove(charList.size() - 1);
        slots[i][j].revealed = reveal;
      }
    }

    for (int i = 0; i < shuffleCount; i++) {
      shuffle();
    }
  }

  public GameManager(int gameSizeX, int gameSizeY, int shuffleCount) {
    this(gameSizeX, gameSizeY, shuffleCount, false);
  }

  // Shuffles two random slots
  public void shuffle() {
    Random random = new Random();
    int x1 = random.nextInt(slots.length);
    int x2 = random.nextInt(slots.length);

    int y1 = random.nextInt(slots[1].length);
    int y2 = random.nextInt(slots[1].length);

    char tmp = slots[x1][y1].symbol;
    slots[x1][y1].symbol = slots[x2][y2].symbol;
    slots[x2][y2].symbol = tmp;
  }

  // Handles input and the main gameplay
  public void gameplayLoop() {
    int tries = 0;
    while (!allRevealed()) {
      printSlots();
      tries++;

      int comparison = 0;
      int x1, x2, y1, y2;
      do {
        // Prompt Player
        Scanner in = new Scanner(System.in);
        System.out.print("Give the first row and collumn: ");
        x1 = in.nextInt();
        y1 = in.nextInt();
        System.out.print("\n");

        System.out.print("Give the second row and collumn: ");
        x2 = in.nextInt();
        y2 = in.nextInt();
        System.out.print("\n");

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
    System.out.println("Successfully revealed all letters in " + tries " tries!");
  }

  // Returns -1 for failure, 0 for non equal, 1 for equal
  public int compareSlots(int x1, int y1, int x2, int y2) {
    if (x1 >= slots.length || x2 >= slots.length || y1 >= slots[1].length || y2 >= slots[1].length) {
      System.out.println("Invalid coordinates! Please try again:");
      return -1;
    }

    if (x1 == x2 && y1 == y2) {
      System.out.println("You cannot give the same coordinates for both letters. Try again..");
      return -1;
    }

    if (slots[x1][y1].symbol == slots[x2][y2].symbol) {
      System.out.println("Successfully revealed!");
      return 1;
    } else {
      System.out.println("Not the same letter. Try again..");
      return 0;
    }
  }

  // Checks if more than 1 character is not revealed
  public boolean allRevealed() {
    int countOfNotRevealed;
    for (int i = 0; i < slots.length; i++) {
      for (int j = 0; j < slots[1].length; j++) {
        if (!slots[i][j].revealed) {
          countOfNotRevealed++;
          if (countOfNotRevealed > 1)
            break;
        }
      }
    }

    return countOfNotRevealed > 1;
  }

  // Prints and formats all the slots
  public void printSlots() {
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
    GameManager gameManager = new GameManager(4, 5, 10, false);
    gameManager.gameplayLoop();
  }
}
