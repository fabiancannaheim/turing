import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class represents an implementation of a universal turing machine.
 * A universal turing machine takes a "normal" turing machine as an encoded
 * input.
 * In this case, the machine takes a binary machine representation as in the
 * following example:
 * M = ( {q1,q2,q3}, {0,1}, {0,1,⌴}, δ, q1,⌴, {q2} ) with:
 * - δ(q1,1) = (q3,0,R)
 * - δ(q3,0) = (q1,1,L)
 * - δ(q3,1) = (q2,0,R)
 * - δ(q3,⌴) = (q3,0,L)
 * # State q1 => 0, State q2 => 00, State q3 => 000, ...
 * # Tape symbol 0 => 0, Tape symbol 1 => 00
 * # Movement L => 0, Movement R => 00
 * # Delimiter between different transition parts: 1
 * # Delimiter between different transitions: 11
 * # Delimiter between encoded turing machine and machine input: 111
 * e.g. δ(q1,1) = (q3,0,R) <=> 0100100010100
 */
public class UniversalTuringMachine {

    private String[] tape;
    private String mode;
    private int currentTapeIndex;
    private List<Transition> transitions = new ArrayList<>();

    public UniversalTuringMachine(String machineCode, String word, String mode, int tapeSize) {
        setTape(word, tapeSize);
        setTransitions(machineCode);
        this.mode = mode;
    }

    public UniversalTuringMachine(String machineCode, int firstNaturalNumber, int secondNaturalNumber, String mode,
            int tapeSize) {
        setTape(integerToUnaryString(firstNaturalNumber) + integerToUnaryString(secondNaturalNumber), tapeSize);
        setTransitions(machineCode);
        this.mode = mode;
    }

    public UniversalTuringMachine(String code, String mode, int tapeSize) {
        this(code.split("111")[0], code.split("111")[1], mode, tapeSize);
    }

    public boolean calculate() {
        String currentState = "q1";
        int count = 1;
        boolean finished = false;
        while (!finished) {
            String currentSymbol = tape[currentTapeIndex];
            int currentTransition = 0;
            for (int i = 0; i < transitions.size(); i++) {
                if (transitions.get(i).stateFrom.equals(currentState)
                        && transitions.get(i).read.equals(currentSymbol)) {
                    currentTransition = i;
                }
            }
            tape[currentTapeIndex] = transitions.get(currentTransition).write;
            currentState = transitions.get(currentTransition).stateTo;
            if (transitions.get(currentTransition).direction.equals("L")) {
                currentTapeIndex -= 1;
            } else if (transitions.get(currentTransition).direction.equals("R")) {
                currentTapeIndex += 1;
            }
            if (mode.equals("STEP")) {
                printStepResultData(currentState, currentTapeIndex, count);
            }
            if (currentState.equals(this.transitions.get(this.transitions.size() - 1).stateTo)) {
                finished = true;
            }
            count++;
        }
        if (mode.equals("END_STEP")) {
            printStepResultData(currentState, currentTapeIndex, count);
        }
        return true;
    }

    private void setTransitions(String machineCode) {
        String[] transitionParts = new String[5];
        for (String transition : machineCode.split("11")) {
            String[] parts = transition.split("1");
            transitionParts[0] = "q" + parts[0].length();
            transitionParts[1] = getSymbol(parts[1].length());
            transitionParts[2] = "q" + parts[2].length();
            transitionParts[3] = getSymbol(parts[3].length());
            transitionParts[4] = getDirection(parts[4].length());
            this.transitions.add(new Transition(transitionParts[0], transitionParts[2], transitionParts[1],
                    transitionParts[3], transitionParts[4], false));
        }
        this.transitions.get(this.transitions.size() - 1).finalTransition = true;
    }

    private String integerToUnaryString(int integer) {
        String unaryString = "";
        for (int i = 0; i < integer; i++) {
            unaryString += "0";
        }
        unaryString += "1";
        return unaryString;
    }

    private void setTape(String word, int tapeSize) {
        tape = new String[tapeSize];
        Arrays.fill(tape, "_");
        for (int i = 0; i < word.length(); i++) {
            tape[tape.length / 2 - word.length() + i] = Character.toString(word.charAt(i));
            if (i == 0) {
                currentTapeIndex = tape.length / 2 - word.length() + i;
            }
        }
    }

    private String getSymbol(int inputLength) {
        if (inputLength == 1) {
            return "0";
        } else if (inputLength == 2) {
            return "1";
        } else if (inputLength == 3) {
            return "_";
        } else if (inputLength == 4) {
            return "X";
        } else {
            throw new IllegalArgumentException("Code has not correct format");
        }
    }

    private String getDirection(int inputLength) {
        if (inputLength == 1) {
            return "L";
        } else if (inputLength == 2) {
            return "R";
        } else {
            throw new IllegalArgumentException("Code has not correct format");
        }
    }

    public void getResults() {
        int result = 0;
        for (String symbol : tape) {
            if (symbol == "0") {
                result++;
            }
        }
        System.out.println("Result: " + result);
    }

    private void printStepResultData(String currentState, int currentTapeIndex, int count) {
        System.out.println("Calculation nr. " + count);
        System.out.println("Current state: " + currentState);
        System.out.println("Current position: " + currentTapeIndex);
        printTape();
        System.out.println();
    }

    private void printTape() {
        String tapeString = "Tape: [";
        for (String entry : tape) {
            tapeString += entry + ", ";
        }
        tapeString = tapeString.trim().substring(0, tapeString.trim().length() - 1) + "]";
        System.out.println(tapeString);
    }

    public void printTransitions() {
        for (int i = 0; i < transitions.size(); i++) {
            System.out.println("Transition Nr. " + i);
            System.out.print("(" + transitions.get(i).stateFrom + ", " + transitions.get(i).read + ") => ");
            System.out.println("(" + transitions.get(i).stateTo + ", " + transitions.get(i).write + ", "
                    + transitions.get(i).direction + ")");
            System.out.println();
        }
    }

    public class Transition {

        public String stateFrom;
        public String stateTo;
        public String read;
        public String write;
        public String direction;
        public boolean finalTransition;

        public Transition(String stateFrom, String stateTo, String read, String write, String direction,
                boolean finalTransition) {
            this.stateFrom = stateFrom;
            this.stateTo = stateTo;
            this.read = read;
            this.write = write;
            this.direction = direction;
        }

    }

    public static void main(String[] args) {

        String tmCodeMultiplication = "010100100010011" +
                "010010000000100010011" +
                "00101001010011" +
                "00100100010010011" +
                "00010100001000010011" +
                "00010010000001001011" +
                "000010100001010011" +
                "00001001000010010011" +
                "00001000100000101011" +
                "0000010100000101011" +
                "000001001000001001011" +
                "000001000010001000010011" +
                "000000101000000101011" +
                "00000010010000001001011" +
                "000000100001000000101011" +
                "000000100010100010011" +
                "00000001010000000100010011" +
                "00000001001000000001000100";
        String multiplicationCode = "0001000001";
        String inputCodeMultiplication = tmCodeMultiplication + "111" + multiplicationCode;

        String tmCodeAddition = "010101010011" +
                "01001001001011" +
                "0010100010010011" +
                "0001010001010011" +
                "000100100010010011" +
                "0001000100001010011" +
                "00001010000101011" +
                "000010001000010001011" +
                "00001001000001001011" +
                "0000010100010010011" +
                "000001001000001001011" +
                "0000010001000000100010011" +
                "0000001001000000100010011" +
                "000000101000000010100";
        String additionCode = "000010001";
        String inputCodeAddition = tmCodeAddition + "111" + additionCode;

        int tapeSize = 200;
        String mode = "STEP";

        int numberA = 2;
        int numberB = 4;

        int numberC = 10;
        int numberD = 8;

        int numberE = 1;
        int numberF = 27;

        int numberG = 23;
        int numberH = 0;

        UniversalTuringMachine turingMachine = new UniversalTuringMachine(tmCodeMultiplication, numberC, numberD, mode,
                tapeSize);
        turingMachine.printTape();
        turingMachine.printTransitions();
        turingMachine.calculate();
        turingMachine.getResults();

    }

}