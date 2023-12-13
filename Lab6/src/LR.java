import java.lang.reflect.Array;
import java.util.*;

public class LR {

    private final Grammar grammar;

    private final Grammar workingGrammar;
    private List<Pair<String, List<String>>> orderedProductions;

    public LR(Grammar grammar) throws Exception {
        this.grammar = grammar;

        if(this.grammar.getIsEnriched()){
            this.workingGrammar = this.grammar;
        } else {
            this.workingGrammar = this.grammar.getEnrichedGrammar();
        }

        orderedProductions = this.grammar.getOrderedProductions();
    }


    public String getNonTerminalPrecededByDot(Item item){
        try {
            String term = item.getRightHandSide().get(item.getPositionForDot());
            if(!grammar.getNonTerminals().contains(term)){
                return null;
            }

            return term;
        }
        catch (Exception e){
            return null;
        }

    }


    public State closure(Item item){

        Set<Item> oldClosure;
        Set<Item> currentClosure = Set.of(item);

        do {
            oldClosure = currentClosure;
            Set<Item> newClosure = new LinkedHashSet<>(currentClosure);
            for(Item i: currentClosure){
                String nonTerminal = getNonTerminalPrecededByDot(i);
                if(nonTerminal != null){
                    for(List<String> prod:  grammar.getProductionsForNonTerminal(nonTerminal)){
                        Item currentItem = new Item(nonTerminal, prod, 0);
                        newClosure.add(currentItem);
                    }
                }
            }
            currentClosure = newClosure;
        } while(!oldClosure.equals(currentClosure));

        return new State(currentClosure);
    }


    public State goTo(State state, String elem) {
        Set<Item> result = new LinkedHashSet<>();

        for (Item i : state.getItems()) {
            try {
                String nonTerminal = i.getRightHandSide().get(i.getPositionForDot());
                if (Objects.equals(nonTerminal, elem)) {
                    Item nextItem = new Item(i.getLeftHandSide(), i.getRightHandSide(), i.getPositionForDot() + 1);
                    State newState = closure(nextItem);
                    result.addAll(newState.getItems());
                }
            } catch(Exception ignored) {
            }
        }

        return new State(result);
    }


    public CanonicalCollection canonicalCollection(){
        CanonicalCollection canonicalCollection = new CanonicalCollection();

        canonicalCollection.addState(
                closure(
                        new Item(
                                workingGrammar.getStartingSymbol(),
                                workingGrammar.getProductionsForNonTerminal(workingGrammar.getStartingSymbol()).get(0),
                                0
                        )
                )
        );

        int index = 0;
        while(index < canonicalCollection.getStates().size()){
            for(String symbol: canonicalCollection.getStates().get(index).getSymbolsSucceedingTheDot()) {
                State newState = goTo(canonicalCollection.getStates().get(index), symbol);
                if (newState.getItems().size() != 0) {
                    int indexState = canonicalCollection.getStates().indexOf(newState);
                    if (indexState == -1) {
                        canonicalCollection.addState(newState);
                    }
                }
            }
            ++index;
        }
        return canonicalCollection;

    }

    public Grammar getGrammar() {
        return grammar;
    }

    public Grammar getWorkingGrammar() {
        return workingGrammar;
    }
}
