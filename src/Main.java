import com.structuralengineering.rcbeam.RCBeam;
import com.structuralengineering.rcbeam.utils.Conversions;

public class Main {
    public static void main(String[] args) {
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
