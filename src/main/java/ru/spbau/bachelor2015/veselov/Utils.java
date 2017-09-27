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

public class Utils {
    private static String labelOf(Transition transition) {
        char min = transition.getMin();
        char max = transition.getMax();

        if (min == max) {
            if (min == ' ') {
                return "\\\\s";
            } else {
                return Character.toString(min);
            }
        }

        if (min + 1 == max) {
            return min + ", " + max;
        }

        return "[" + min + ".." + max + "]";
    }

    private static String labelOf(List<Transition> transitions) {
        return transitions.stream().map(Utils::labelOf).collect(Collectors.joining(", "));
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

    public static void drawPicture(Automaton automaton, String name)
            throws IOException, InterruptedException {
        String scriptName = name + ".gv";

        Path filePath = Paths.get(scriptName);
        writeToFile(automaton, filePath);

        Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec("dot -Tpng " + scriptName + " -o " + name + ".png");
        pr.waitFor();

        filePath.toFile().delete();
    }

    public static void normalizeWithOutput(Automaton automaton, String name)
            throws IOException, InterruptedException {
        drawPicture(automaton, name);

        automaton.determinize();
        drawPicture(automaton, name + "_determinized");

        MinimizationOperations.minimize(automaton);
        drawPicture(automaton, name + "_minimized");
    }
}
