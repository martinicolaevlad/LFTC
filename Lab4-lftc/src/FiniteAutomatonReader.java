import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

class Transition {
    private String sourceState;
    private String destinationState;
    private String symbol;

    public Transition(String sourceState, String destinationState, String symbol) {
        this.sourceState = sourceState;
        this.destinationState = destinationState;
        this.symbol = symbol;
    }

    public String getSourceState() {
        return sourceState;
    }

    public String getDestinationState() {
        return destinationState;
    }

    public String getSymbol() {
        return symbol;
    }
}

public class FiniteAutomatonReader {
    private Set<String> states;
    private Set<String> alphabet;
    private Set<Transition> transitions;
    private String initialState;
    private Set<String> finalStates;

    public FiniteAutomatonReader(String filename) {
        states = new HashSet<>();
        alphabet = new HashSet<>();
        transitions = new HashSet<>();
        finalStates = new HashSet<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                processLine(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Debug: Print parsed transitions
        System.out.println("Parsed Transitions:");
        for (Transition transition : transitions) {
            System.out.println(transition.getSourceState() + " -> " +
                    transition.getDestinationState() + " on " +
                    transition.getSymbol());
        }

        // Uncomment the line below if you want to keep the previous UI behavior
         runUI();
    }

    private void processLine(String line) {
        String[] parts = line.split(":");
        if (parts.length == 2) {
            String key = parts[0].trim();
            String value = parts[1].trim();

            switch (key) {
                case "States":
                    states.addAll(parseElements(value));
                    break;
                case "Alphabet":
                    alphabet.addAll(parseElements(value));
                    break;
                case "Transitions":
                    transitions.addAll(parseTransitions(value));
                    break;
                case "InitialState":
                    initialState = value;
                    break;
                case "FinalStates":
                    finalStates.addAll(parseElements(value));
                    break;
                default:
                    // Handle unknown key
                    System.out.println("Unknown key: " + key);
                    break;
            }
        }
    }

    private Set<String> parseElements(String elements) {
        String[] parts = elements.split(",");
        Set<String> set = new HashSet<>();
        for (String part : parts) {
            set.add(part.trim());
        }
        return set;
    }

    public boolean verifySequence(String sequence) {
        String currentState = initialState;
        for (char symbol : sequence.toCharArray()) {
            boolean validTransition = false;
            for (Transition transition : transitions) {
                if (transition.getSourceState().equals(currentState) && transition.getSymbol().equals(String.valueOf(symbol))) {
                    currentState = transition.getDestinationState();
                    validTransition = true;
                    break;
                }
            }
            if (!validTransition) {
                System.out.println("Invalid transition for symbol '" + symbol + "' in state '" + currentState + "'");
                return false; // Sequence is not accepted
            }
        }
        return finalStates.contains(currentState);
    }


    private Set<Transition> parseTransitions(String transitions) {
        String[] parts = transitions.split(",");
        Set<Transition> set = new HashSet<>();
        for (String part : parts) {
            part = part.trim();
            String[] transitionParts = part.split("\\s+->\\s+|\\s+on\\s+");
            if (transitionParts.length == 3) {
                set.add(new Transition(transitionParts[0], transitionParts[2], transitionParts[1]));
            } else {
                System.out.println("Invalid transition format: " + part);
            }
        }
        return set;
    }

    private void runUI() {
        while (true) {
            System.out.println("Finite Automaton Reader");
            System.out.println("1. Display States");
            System.out.println("2. Display Alphabet");
            System.out.println("3. Display Transitions");
            System.out.println("4. Display Initial State");
            System.out.println("5. Display Final States");
            System.out.println("6. Verify Sequence");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");

            try {
                BufferedReader reader = new BufferedReader(new java.io.InputStreamReader(System.in));
                int choice = Integer.parseInt(reader.readLine());

                switch (choice) {
                    case 1:
                        System.out.println("States: " + states);
                        break;
                    case 2:
                        System.out.println("Alphabet: " + alphabet);
                        break;
                    case 3:
                        System.out.println("Transitions: " + transitions);
                        break;
                    case 4:
                        System.out.println("Initial State: " + initialState);
                        break;
                    case 5:
                        System.out.println("Final States: " + finalStates);
                        break;
                    case 6:
                        System.out.print("Enter sequence to verify: ");
                        String sequence = reader.readLine();
                        boolean result = verifySequence(sequence);
                        System.out.println("Sequence Accepted: " + result);
                        break;
                    case 0:
                        System.out.println("Exiting program. Goodbye!");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        FiniteAutomatonReader reader = new FiniteAutomatonReader("C://Users//vladm//OneDrive//Desktop//LFTC//Lab4-lftc//src//FA.in");
    }
}


