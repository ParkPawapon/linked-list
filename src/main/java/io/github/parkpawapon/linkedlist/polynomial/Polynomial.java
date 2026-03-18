package io.github.parkpawapon.linkedlist.polynomial;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Polynomial {
    private static final class Node {
        private long coefficient;
        private final int exponent;
        private Node next;

        private Node(long coefficient, int exponent) {
            this.coefficient = coefficient;
            this.exponent = exponent;
        }
    }

    private Node head;
    private Node tail;
    private int termCount;

    public static Polynomial fromTerms(Iterable<PolynomialTerm> terms) {
        Objects.requireNonNull(terms, "terms must not be null");

        Polynomial polynomial = new Polynomial();
        for (PolynomialTerm term : terms) {
            Objects.requireNonNull(term, "terms must not contain null elements");
            polynomial.addTerm(term.coefficient(), term.exponent());
        }

        return polynomial;
    }

    public int getTermCount() {
        return termCount;
    }

    public boolean isZero() {
        return head == null;
    }

    public List<PolynomialTerm> terms() {
        List<PolynomialTerm> result = new ArrayList<>(termCount);
        Node current = head;
        while (current != null) {
            result.add(new PolynomialTerm(current.coefficient, current.exponent));
            current = current.next;
        }

        return List.copyOf(result);
    }

    public void addTerm(long coefficient, int exponent) {
        validateExponent(exponent);
        if (coefficient == 0) {
            return;
        }

        if (head == null) {
            appendAsFirstTerm(coefficient, exponent);
            return;
        }

        if (exponent > head.exponent) {
            Node newHead = new Node(coefficient, exponent);
            newHead.next = head;
            head = newHead;
            termCount++;
            return;
        }

        Node previous = null;
        Node current = head;

        while (current != null && current.exponent > exponent) {
            previous = current;
            current = current.next;
        }

        if (current != null && current.exponent == exponent) {
            long updatedCoefficient = Math.addExact(current.coefficient, coefficient);
            if (updatedCoefficient == 0) {
                removeNode(previous, current);
            } else {
                current.coefficient = updatedCoefficient;
            }

            return;
        }

        Node newNode = new Node(coefficient, exponent);
        newNode.next = current;

        if (previous == null) {
            head = newNode;
        } else {
            previous.next = newNode;
        }

        if (current == null) {
            tail = newNode;
        }

        termCount++;
    }

    public Polynomial add(Polynomial other) {
        Objects.requireNonNull(other, "other must not be null");

        Polynomial result = new Polynomial();
        Node left = head;
        Node right = other.head;

        while (left != null || right != null) {
            if (right == null || (left != null && left.exponent > right.exponent)) {
                result.appendTermAssumeDescending(left.coefficient, left.exponent);
                left = left.next;
                continue;
            }

            if (left == null || right.exponent > left.exponent) {
                result.appendTermAssumeDescending(right.coefficient, right.exponent);
                right = right.next;
                continue;
            }

            long combinedCoefficient = Math.addExact(left.coefficient, right.coefficient);
            if (combinedCoefficient != 0) {
                result.appendTermAssumeDescending(combinedCoefficient, left.exponent);
            }

            left = left.next;
            right = right.next;
        }

        return result;
    }

    public Polynomial multiply(Polynomial other) {
        Objects.requireNonNull(other, "other must not be null");

        Polynomial result = new Polynomial();
        if (head == null || other.head == null) {
            return result;
        }

        Map<Integer, Long> accumulator = new HashMap<>();
        for (Node left = head; left != null; left = left.next) {
            for (Node right = other.head; right != null; right = right.next) {
                int exponent = Math.addExact(left.exponent, right.exponent);
                long coefficient = Math.multiplyExact(left.coefficient, right.coefficient);
                accumulator.merge(exponent, coefficient, Math::addExact);
            }
        }

        accumulator.entrySet().stream()
            .filter(entry -> entry.getValue() != 0)
            .sorted(Map.Entry.<Integer, Long>comparingByKey(Comparator.reverseOrder()))
            .forEach(entry -> result.appendTermAssumeDescending(entry.getValue(), entry.getKey()));

        return result;
    }

    @Override
    public String toString() {
        if (head == null) {
            return "0";
        }

        StringBuilder builder = new StringBuilder();
        Node current = head;

        while (current != null) {
            long coefficient = current.coefficient;
            if (coefficient != 0) {
                if (builder.length() == 0) {
                    if (coefficient < 0) {
                        builder.append("-");
                    }

                    appendTerm(builder, Math.abs(coefficient), current.exponent);
                } else {
                    builder.append(coefficient < 0 ? " - " : " + ");
                    appendTerm(builder, Math.abs(coefficient), current.exponent);
                }
            }

            current = current.next;
        }

        return builder.length() == 0 ? "0" : builder.toString();
    }

    private void appendAsFirstTerm(long coefficient, int exponent) {
        Node node = new Node(coefficient, exponent);
        head = node;
        tail = node;
        termCount = 1;
    }

    private void appendTermAssumeDescending(long coefficient, int exponent) {
        if (coefficient == 0) {
            return;
        }

        if (head == null) {
            appendAsFirstTerm(coefficient, exponent);
            return;
        }

        if (tail == null || exponent >= tail.exponent) {
            throw new IllegalStateException("Terms must be appended in strictly descending exponent order.");
        }

        Node node = new Node(coefficient, exponent);
        tail.next = node;
        tail = node;
        termCount++;
    }

    private void removeNode(Node previous, Node current) {
        if (previous == null) {
            head = current.next;
        } else {
            previous.next = current.next;
        }

        if (current.next == null) {
            tail = previous;
        }

        if (head == null) {
            tail = null;
        }

        termCount--;
    }

    private static void appendTerm(StringBuilder builder, long absoluteCoefficient, int exponent) {
        if (exponent == 0) {
            builder.append(absoluteCoefficient);
            return;
        }

        if (absoluteCoefficient != 1) {
            builder.append(absoluteCoefficient);
        }

        builder.append('x');
        if (exponent != 1) {
            builder.append('^').append(exponent);
        }
    }

    private static void validateExponent(int exponent) {
        if (exponent < 0) {
            throw new IllegalArgumentException("Exponent must be non-negative.");
        }
    }
}
