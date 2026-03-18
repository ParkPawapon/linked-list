package io.github.parkpawapon.linkedlist.app;

import io.github.parkpawapon.linkedlist.polynomial.Polynomial;
import io.github.parkpawapon.linkedlist.polynomial.PolynomialTerm;
import java.util.List;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        Polynomial polynomialA = Polynomial.fromTerms(List.of(
            new PolynomialTerm(2, 5),
            new PolynomialTerm(4, 3),
            new PolynomialTerm(3, 2),
            new PolynomialTerm(5, 0)
        ));

        Polynomial polynomialB = Polynomial.fromTerms(List.of(
            new PolynomialTerm(3, 4),
            new PolynomialTerm(-4, 3),
            new PolynomialTerm(6, 0)
        ));

        Polynomial sum = polynomialA.add(polynomialB);
        Polynomial product = polynomialA.multiply(polynomialB);

        System.out.println("Linked-List Polynomial Demo");
        System.out.println("A(x)      = " + polynomialA);
        System.out.println("B(x)      = " + polynomialB);
        System.out.println("A(x)+B(x) = " + sum);
        System.out.println("A(x)*B(x) = " + product);
        System.out.println();
        System.out.println("Complexity:");
        System.out.println("- Add: O(n) over the total number of input nodes");
        System.out.println("- Multiply: O(a * b + k log k)");
    }
}
