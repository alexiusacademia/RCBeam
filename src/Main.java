import com.structuralengineering.rcbeam.RCBeam;

public class Main {
  public static void main(String[] args) {
    RCBeam rcBeam = new RCBeam();

    printString("Reinforced Concrete Beam Analysis");
    printLine();
  }

  private static void printString(String str) {
    System.out.println(str);
  }

  private static void printLine() {
    printString("= = = = = = = = = = = = = = = = = = =");
  }
}
