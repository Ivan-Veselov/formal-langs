package ru.spbau.bachelor2015.veselov;

import dk.brics.automaton.*;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private static String labelOf(Transition transition) {
        char min = transition.getMin();
        char max = transition.getMax();

        if (min == max) {
            return Character.toString(min);
        }

        return "[" + min + ".." + max + "]";
    }

    private static String labelOf(List<Transition> transitions) {
        return transitions.stream().map(Main::labelOf).collect(Collectors.joining(", "));
    }

    private static void writeToFile(Automaton automaton, Path path) throws IOException {
        try (FileWriter writer = new FileWriter(path.toFile())) {
            writer.append("digraph G {\n")
                  .append("    rankdir=LR\n");

            String startNodeName = "start";
            writer.append("    ")
                  .append(startNodeName)
                  .append(" [shape=point, style=invis]\n");

            ArrayList<State> states = new ArrayList<>(automaton.getStates());
            Map<State, String> idOf = new HashMap<>();

            for (int id = 0; id < states.size(); id++) {
                State state = states.get(id);

                String shape;
                if (automaton.getAcceptStates().contains(state)) {
                    shape = "doublecircle";
                } else {
                    shape = "circle";
                }

                idOf.put(state, Integer.toString(id));
                writer.append("    ")
                      .append(Integer.toString(id))
                      .append(" [label=\"\", shape=")
                      .append(shape)
                      .append("]\n");
            }

            writer.append("    ")
                  .append(startNodeName)
                  .append(" -> ")
                  .append(idOf.get(automaton.getInitialState()));

            Map<StatePair, List<Transition>> transitions = new HashMap<>();
            for (State state : states) {
                for (Transition transition : state.getTransitions()) {
                    StatePair pair = new StatePair(state, transition.getDest());

                    List<Transition> betweenTransitions =
                            transitions.computeIfAbsent(pair, k -> new ArrayList<>());

                    betweenTransitions.add(transition);
                }
            }

            for (Map.Entry<StatePair, List<Transition>> entry : transitions.entrySet()) {
                writer.append("    ")
                      .append(idOf.get(entry.getKey().getFirstState()))
                      .append(" -> ")
                      .append(idOf.get(entry.getKey().getSecondState()))
                      .append(" [label=\"")
                      .append(labelOf(entry.getValue()))
                      .append("\"];\n");
            }

            writer.append("}");
        }
    }

    private static void drawPicture(Automaton automaton, String name)
                                                        throws IOException, InterruptedException {
        String scriptName = name + ".gv";

        Path filePath = Paths.get(scriptName);
        writeToFile(automaton, filePath);

        Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec("dot -Tpng " + scriptName + " -o " + name + ".png");
        pr.waitFor();

        filePath.toFile().delete();
    }

    private static String rangeOfChars(char min, char max) {
        StringBuilder builder = new StringBuilder();

        for (char c = min; c < max + 1; c++) {
            builder.append(c);
        }

        return builder.toString();
    }

    private static String identifierFirstLetterCharsSet() {
        return "_" + rangeOfChars('a', 'z');
    }

    private static String alphabetCharsSet() {
        return identifierFirstLetterCharsSet() + rangeOfChars('0', '9');
    }

    private static Automaton alphabet() {
        return BasicAutomata.makeCharSet(alphabetCharsSet());
    }

    private static Automaton identifierFirstLetter() {
        return BasicAutomata.makeCharSet(identifierFirstLetterCharsSet());
    }

    private static Automaton identifiers() {
        return BasicOperations.concatenate(identifierFirstLetter(),
                                           BasicOperations.repeat(alphabet()));
    }

    private static Automaton keywords() {
        return BasicOperations.union(
                    Stream.of("if", "then", "else", "let", "in", "true", "false")
                          .map(BasicAutomata::makeString)
                          .collect(Collectors.toList()));
    }

    private static void normalizeWithOutput(Automaton automaton, String name)
                                                        throws IOException, InterruptedException {
        drawPicture(automaton, name);

        automaton.determinize();
        drawPicture(automaton, name + "_determinized");

        MinimizationOperations.minimize(automaton);
        drawPicture(automaton, name + "_minimized");
    }

    public static void main(String arg[]) throws IOException, InterruptedException {
        Automaton identifiers = identifiers();
        Automaton keywords = keywords();

        normalizeWithOutput(identifiers, "identifiers");
        normalizeWithOutput(keywords, "keywords");

        Automaton identifiersWithoutKeywords = BasicOperations.minus(identifiers, keywords);
        normalizeWithOutput(identifiersWithoutKeywords, "identifiers_without_keywords");
    }
}
