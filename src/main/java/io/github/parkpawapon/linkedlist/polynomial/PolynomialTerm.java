package io.github.parkpawapon.linkedlist.polynomial;

public record PolynomialTerm(long coefficient, int exponent) {
    public PolynomialTerm {
        if (exponent < 0) {
            throw new IllegalArgumentException("Exponent must be non-negative.");
        }
    }
}
