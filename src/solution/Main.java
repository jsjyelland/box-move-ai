package solution;

public class Main {
    public static void main(String[] args) {
        State state;

        State expandedState = state.clone();

        if (state.action(...)) {
            addToTree(state);
        }
    }
}
