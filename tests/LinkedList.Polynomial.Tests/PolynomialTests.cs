using PolynomialModel = LinkedList.Polynomial.Core.Polynomial;

namespace LinkedList.Polynomial.Tests;

public class PolynomialTests
{
    [Fact]
    public void Add_ShouldMergeMatchingExponents()
    {
        var first = PolynomialModel.FromTerms(
        [
            (2, 5),
            (4, 3),
            (3, 2),
            (5, 0),
        ]);

        var second = PolynomialModel.FromTerms(
        [
            (3, 4),
            (-4, 3),
            (6, 0),
        ]);

        var result = first.Add(second);

        Assert.Equal("2x^5 + 3x^4 + 3x^2 + 11", result.ToString());
        Assert.Equal(4, result.TermCount);
    }

    [Fact]
    public void Multiply_ShouldGenerateExpectedResult()
    {
        var first = PolynomialModel.FromTerms([(2, 1), (3, 0)]);
        var second = PolynomialModel.FromTerms([(1, 1), (4, 0)]);

        var result = first.Multiply(second);

        Assert.Equal("2x^2 + 11x + 12", result.ToString());
        Assert.Equal(
            [
                (2L, 2),
                (11L, 1),
                (12L, 0),
            ],
            result.Terms.ToArray());
    }

    [Fact]
    public void AddTerm_ShouldRemoveTerm_WhenCoefficientCancelsOut()
    {
        var polynomial = PolynomialModel.FromTerms([(8, 2), (-8, 2)]);

        Assert.True(polynomial.IsZero);
        Assert.Equal("0", polynomial.ToString());
        Assert.Equal(0, polynomial.TermCount);
    }

    [Fact]
    public void Multiply_ZeroPolynomial_ShouldReturnZero()
    {
        var first = PolynomialModel.FromTerms([(7, 9)]);
        var second = new PolynomialModel();

        var result = first.Multiply(second);

        Assert.True(result.IsZero);
        Assert.Equal("0", result.ToString());
    }

    [Fact]
    public void AddTerm_NegativeExponent_ShouldThrow()
    {
        var polynomial = new PolynomialModel();
        Action action = () => polynomial.AddTerm(2, -1);

        Assert.Throws<ArgumentOutOfRangeException>(action);
    }
}
