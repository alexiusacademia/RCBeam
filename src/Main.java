import com.structuralengineering.rcbeam.RCBeam;
import com.structuralengineering.rcbeam.utils.Conversions;

public class Main {
  public static void main(String[] args) {
    RCBeam rcBeam = new RCBeam();

    printString("Reinforced Concrete Beam Analysis");
    printLine();

    printString(String.valueOf(Conversions.mmToIn(100.0)));

  }

  private static void printString(String str) {
    System.out.println(str);
  }

  private static void printLine() {
    printString("= = = = = = = = = = = = = = = = = = =");
  }
}
