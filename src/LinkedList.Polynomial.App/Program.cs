using LinkedList.Polynomial.Core;

var polynomialA = Polynomial.FromTerms(
[
    (2, 5),
    (4, 3),
    (3, 2),
    (5, 0),
]);

var polynomialB = Polynomial.FromTerms(
[
    (3, 4),
    (-4, 3),
    (6, 0),
]);

var sum = polynomialA.Add(polynomialB);
var product = polynomialA.Multiply(polynomialB);

Console.WriteLine("Linked-List Polynomial Demo");
Console.WriteLine($"A(x)      = {polynomialA}");
Console.WriteLine($"B(x)      = {polynomialB}");
Console.WriteLine($"A(x)+B(x) = {sum}");
Console.WriteLine($"A(x)*B(x) = {product}");
Console.WriteLine();
Console.WriteLine("Complexity:");
Console.WriteLine("- Add: O(n + m)");
Console.WriteLine("- Multiply: O(n * m + k log k), where k = unique exponents in the result");
