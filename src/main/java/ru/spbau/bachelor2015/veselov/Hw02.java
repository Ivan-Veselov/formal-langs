package ru.spbau.bachelor2015.veselov;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.BasicOperations;
import dk.brics.automaton.MinimizationOperations;
import dk.brics.automaton.RegExp;
import sun.security.util.AuthResources;

import java.io.IOException;

import static ru.spbau.bachelor2015.veselov.Utils.normalizeWithOutput;

public class Hw02 {
    public static String numberRegExp() {
        return "([1-9][0-9]*|0)";
    }

    public static String integerRegExp() {
        return "((+|-|())" + numberRegExp() + ")";
    }

    public static String identifiersRegExp() {
        return "([a-z_][a-z0-9_]*)";
    }

    public static String listOfRegExp(String elemRegExp,
                                      char openBrace,
                                      char closeBrace,
                                      char delimiter) {
        return "\\" + openBrace + "( )*" +
                "(()|" + elemRegExp + "(( )*" + delimiter + "( )*" + elemRegExp + ")*" + ")"
                + "( )*\\" + closeBrace;
    }

    public static String integersListRegExp() {
        return "(" + listOfRegExp(integerRegExp(), '[', ']', ';') + ")";
    }

    public static String tuplesRegExp() {
        return listOfRegExp("(" + integerRegExp() + "|" +
                        identifiersRegExp() + "|" +
                        integersListRegExp() + ")",
                '(', ')', ',');
    }

    public static Automaton firstsTask1() {
        return new RegExp("(a|b)*ab(a|b)*|(a|b)*a|b*").toAutomaton(false);
    }

    public static Automaton firstsTask2() {
        return new RegExp("(a|b)*(ab|ba)(a|b)*|a*|b*").toAutomaton(false);
    }

    public static Automaton fromString(String regex) {
        return new RegExp(regex).toAutomaton(false);
    }

    public static void main(String arg[]) throws IOException, InterruptedException {
        normalizeWithOutput(firstsTask1(), "1_1");
        normalizeWithOutput(firstsTask2(), "1_2");
        normalizeWithOutput(fromString(integersListRegExp()), "integers_list");
        normalizeWithOutput(fromString(tuplesRegExp()), "tuples");
    }
}
