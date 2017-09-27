package ru.spbau.bachelor2015.veselov;

import dk.brics.automaton.*;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.spbau.bachelor2015.veselov.Utils.drawPicture;
import static ru.spbau.bachelor2015.veselov.Utils.normalizeWithOutput;

public class Hw01 {
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

    public static void main(String arg[]) throws IOException, InterruptedException {
        Automaton identifiers = identifiers();
        Automaton keywords = keywords();

        normalizeWithOutput(identifiers, "identifiers");
        normalizeWithOutput(keywords, "keywords");

        Automaton identifiersWithoutKeywords = BasicOperations.minus(identifiers, keywords);
        normalizeWithOutput(identifiersWithoutKeywords, "identifiers_without_keywords");
    }
}
