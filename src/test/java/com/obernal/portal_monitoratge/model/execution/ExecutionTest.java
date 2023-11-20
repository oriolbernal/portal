package com.obernal.portal_monitoratge.model.execution;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExecutionTest {

    @Test
    void getElapsedTimeInSeconds() {
        var execution = new Execution<>(10000, null, new ArrayList<>());
        assertTrue( execution.getElapsedTimeInSeconds() > 0);
    }

    @Test
    void getId() {
        var execution = new Execution<>(10000, null, new ArrayList<>());
        assertNotNull(execution.getId());
    }

    @Test
    void getData() {
        var execution = new Execution<>(10000, "Hello world", new ArrayList<>());
        assertEquals("Hello world", execution.getData());
    }

    @Test
    void isAlert() {
        List<String> messages = List.of("Message1", "Message2");
        var execution = new Execution<>(10000, null, messages);
        assertTrue(execution.isAlert());
    }

    @Test
    void isError() {
        var execution = new Execution<>(10000, new Exception("Hello world"));
        assertTrue( execution.isError());
    }

    @Test
    void getErrorMessage() {
        var execution = new Execution<>(10000, new Exception("Hello world"));
        assertEquals("Hello world", execution.getErrorMessage());
    }
}