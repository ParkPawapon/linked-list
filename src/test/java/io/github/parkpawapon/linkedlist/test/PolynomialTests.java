package io.github.parkpawapon.linkedlist.test;

import io.github.parkpawapon.linkedlist.polynomial.Polynomial;
import io.github.parkpawapon.linkedlist.polynomial.PolynomialTerm;
import java.util.List;
import java.util.Objects;

public final class PolynomialTests {
    private int passed;
    private int failed;

    public static void main(String[] args) {
        PolynomialTests suite = new PolynomialTests();
        suite.runAll();
    }

    private void runAll() {
        run("Add should merge matching exponents", this::addShouldMergeMatchingExponents);
        run("Multiply should generate expected result", this::multiplyShouldGenerateExpectedResult);
        run("AddTerm should remove a term when coefficient cancels out", this::addTermShouldRemoveTermWhenCoefficientCancelsOut);
        run("Multiply zero polynomial should return zero", this::multiplyZeroPolynomialShouldReturnZero);
        run("AddTerm negative exponent should throw", this::addTermNegativeExponentShouldThrow);
        run("Add should not mutate the input polynomials", this::addShouldNotMutateInputs);

        int total = passed + failed;
        System.out.printf("Passed: %d, Failed: %d, Total: %d%n", passed, failed, total);

        if (failed > 0) {
            System.exit(1);
        }
    }

    private void addShouldMergeMatchingExponents() {
        Polynomial first = Polynomial.fromTerms(List.of(
            new PolynomialTerm(2, 5),
            new PolynomialTerm(4, 3),
            new PolynomialTerm(3, 2),
            new PolynomialTerm(5, 0)
        ));

        Polynomial second = Polynomial.fromTerms(List.of(
            new PolynomialTerm(3, 4),
            new PolynomialTerm(-4, 3),
            new PolynomialTerm(6, 0)
        ));

        Polynomial result = first.add(second);

        assertEquals("2x^5 + 3x^4 + 3x^2 + 11", result.toString(), "Unexpected add result.");
        assertEquals(4, result.getTermCount(), "Unexpected term count after add.");
    }

    private void multiplyShouldGenerateExpectedResult() {
        Polynomial first = Polynomial.fromTerms(List.of(
            new PolynomialTerm(2, 1),
            new PolynomialTerm(3, 0)
        ));

        Polynomial second = Polynomial.fromTerms(List.of(
            new PolynomialTerm(1, 1),
            new PolynomialTerm(4, 0)
        ));

        Polynomial result = first.multiply(second);

        assertEquals("2x^2 + 11x + 12", result.toString(), "Unexpected multiply result.");
        assertEquals(
            List.of(
                new PolynomialTerm(2, 2),
                new PolynomialTerm(11, 1),
                new PolynomialTerm(12, 0)
            ),
            result.terms(),
            "Unexpected term list after multiply."
        );
    }

    private void addTermShouldRemoveTermWhenCoefficientCancelsOut() {
        Polynomial polynomial = Polynomial.fromTerms(List.of(
            new PolynomialTerm(8, 2),
            new PolynomialTerm(-8, 2)
        ));

        assertTrue(polynomial.isZero(), "Polynomial should be zero after cancellation.");
        assertEquals("0", polynomial.toString(), "Zero polynomial should format as 0.");
        assertEquals(0, polynomial.getTermCount(), "Zero polynomial should have no terms.");
    }

    private void multiplyZeroPolynomialShouldReturnZero() {
        Polynomial first = Polynomial.fromTerms(List.of(new PolynomialTerm(7, 9)));
        Polynomial second = new Polynomial();

        Polynomial result = first.multiply(second);

        assertTrue(result.isZero(), "Multiplying by zero should return zero.");
        assertEquals("0", result.toString(), "Zero multiplication should format as 0.");
    }

    private void addTermNegativeExponentShouldThrow() {
        Polynomial polynomial = new Polynomial();

        assertThrows(
            IllegalArgumentException.class,
            () -> polynomial.addTerm(2, -1),
            "Negative exponents must be rejected."
        );
    }

    private void addShouldNotMutateInputs() {
        Polynomial first = Polynomial.fromTerms(List.of(
            new PolynomialTerm(5, 2),
            new PolynomialTerm(1, 0)
        ));

        Polynomial second = Polynomial.fromTerms(List.of(
            new PolynomialTerm(3, 2),
            new PolynomialTerm(7, 1)
        ));

        first.add(second);

        assertEquals("5x^2 + 1", first.toString(), "First input polynomial should stay unchanged.");
        assertEquals("3x^2 + 7x", second.toString(), "Second input polynomial should stay unchanged.");
    }

    private void run(String name, TestCase testCase) {
        try {
            testCase.run();
            passed++;
            System.out.println("[PASS] " + name);
        } catch (Throwable exception) {
            failed++;
            System.err.println("[FAIL] " + name);
            exception.printStackTrace(System.err);
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!Objects.equals(expected, actual)) {
            throw new AssertionError(message + " Expected: " + expected + ", Actual: " + actual);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertThrows(Class<? extends Throwable> expectedType, Runnable action, String message) {
        try {
            action.run();
        } catch (Throwable exception) {
            if (expectedType.isInstance(exception)) {
                return;
            }

            throw new AssertionError(message + " Expected: " + expectedType.getName() + ", Actual: " + exception.getClass().getName(), exception);
        }

        throw new AssertionError(message + " Expected exception: " + expectedType.getName());
    }

    @FunctionalInterface
    private interface TestCase {
        void run();
    }
}
