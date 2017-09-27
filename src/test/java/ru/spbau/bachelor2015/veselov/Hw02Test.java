package ru.spbau.bachelor2015.veselov;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.BasicOperations;
import dk.brics.automaton.RegExp;
import org.junit.Test;

import static org.junit.Assert.*;
import static ru.spbau.bachelor2015.veselov.Hw02.integersListRegExp;
import static ru.spbau.bachelor2015.veselov.Hw02.tuplesRegExp;

public class Hw02Test {
    @Test
    public void integersListRegExpTest() throws Exception {
        RegExp regExp = new RegExp(integersListRegExp());
        Automaton automaton = regExp.toAutomaton();

        assertTrue(BasicOperations.run(automaton, "[]"));
        assertTrue(BasicOperations.run(automaton, "[1]"));
        assertTrue(BasicOperations.run(automaton, "[0]"));
        assertTrue(BasicOperations.run(automaton, "[1;1;2;3;5;8]"));
        assertTrue(BasicOperations.run(automaton, "[  ]"));
        assertTrue(BasicOperations.run(automaton, "[ 4; 8;  15; 16   ; 23; 42 ]"));

        assertFalse(BasicOperations.run(automaton, "]["));
        assertFalse(BasicOperations.run(automaton, "[[]]"));
        assertFalse(BasicOperations.run(automaton, "[00]"));
        assertFalse(BasicOperations.run(automaton, "[1;2;3"));
        assertFalse(BasicOperations.run(automaton, "[a]"));
        assertFalse(BasicOperations.run(automaton, "[1,2,3]"));
        assertFalse(BasicOperations.run(automaton, "[1;2;]"));
        assertFalse(BasicOperations.run(automaton, "[1; 23  4; 5]"));
    }

    @Test
    public void tuplesRegExpTest() throws Exception {
        RegExp regExp = new RegExp(tuplesRegExp());
        Automaton automaton = regExp.toAutomaton();

        assertTrue(BasicOperations.run(automaton, "()"));
        assertTrue(BasicOperations.run(automaton, "(1)"));
        assertTrue(BasicOperations.run(automaton, "(0)"));
        assertTrue(BasicOperations.run(automaton, "(_)"));
        assertTrue(BasicOperations.run(automaton, "(1,1,2,3,5,8)"));
        assertTrue(BasicOperations.run(automaton, "(   )"));
        assertTrue(BasicOperations.run(automaton, "( 4, eight,  15, [13; 16  ; 23; 42])"));

        assertFalse(BasicOperations.run(automaton, ")("));
        assertFalse(BasicOperations.run(automaton, "(())"));
        assertFalse(BasicOperations.run(automaton, "(1,2,3"));
        assertFalse(BasicOperations.run(automaton, "(1;2;3)"));
        assertFalse(BasicOperations.run(automaton, "(1,2,)"));
        assertFalse(BasicOperations.run(automaton, "(1, 23 4, 5)"));
    }
}