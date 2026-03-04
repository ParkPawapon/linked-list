using System.Text;

namespace LinkedList.Polynomial.Core;

/// <summary>
/// Polynomial represented as a singly linked list ordered by descending exponent.
/// </summary>
public sealed class Polynomial
{
    private sealed class PolynomialNode
    {
        public PolynomialNode(long coefficient, int exponent)
        {
            Coefficient = coefficient;
            Exponent = exponent;
        }

        public long Coefficient { get; set; }

        public int Exponent { get; }

        public PolynomialNode? Next { get; set; }
    }

    private PolynomialNode? _head;
    private PolynomialNode? _tail;

    public int TermCount { get; private set; }

    public bool IsZero => _head is null;

    public IEnumerable<(long Coefficient, int Exponent)> Terms
    {
        get
        {
            var current = _head;
            while (current is not null)
            {
                yield return (current.Coefficient, current.Exponent);
                current = current.Next;
            }
        }
    }

    public static Polynomial FromTerms(IEnumerable<(long Coefficient, int Exponent)> terms)
    {
        ArgumentNullException.ThrowIfNull(terms);

        var polynomial = new Polynomial();
        foreach (var (coefficient, exponent) in terms)
        {
            polynomial.AddTerm(coefficient, exponent);
        }

        return polynomial;
    }

    /// <summary>
    /// Inserts a term while preserving descending exponent order.
    /// Time: O(n), Space: O(1).
    /// </summary>
    public void AddTerm(long coefficient, int exponent)
    {
        if (exponent < 0)
        {
            throw new ArgumentOutOfRangeException(nameof(exponent), "Exponent must be non-negative.");
        }

        if (coefficient == 0)
        {
            return;
        }

        if (_head is null)
        {
            AppendAsFirstTerm(coefficient, exponent);
            return;
        }

        if (exponent > _head.Exponent)
        {
            var newHead = new PolynomialNode(coefficient, exponent) { Next = _head };
            _head = newHead;
            TermCount++;
            return;
        }

        PolynomialNode? previous = null;
        var current = _head;

        while (current is not null && current.Exponent > exponent)
        {
            previous = current;
            current = current.Next;
        }

        if (current is not null && current.Exponent == exponent)
        {
            current.Coefficient += coefficient;

            if (current.Coefficient == 0)
            {
                RemoveNode(previous, current);
            }

            return;
        }

        var newNode = new PolynomialNode(coefficient, exponent) { Next = current };
        if (previous is null)
        {
            _head = newNode;
        }
        else
        {
            previous.Next = newNode;
        }

        if (current is null)
        {
            _tail = newNode;
        }

        TermCount++;
    }

    /// <summary>
    /// Adds two ordered linked-list polynomials using merge logic.
    /// Time: O(n + m), Space: O(n + m) for the result.
    /// </summary>
    public Polynomial Add(Polynomial other)
    {
        ArgumentNullException.ThrowIfNull(other);

        var result = new Polynomial();
        var left = _head;
        var right = other._head;

        while (left is not null || right is not null)
        {
            if (right is null || (left is not null && left.Exponent > right.Exponent))
            {
                result.AppendTermAssumeDescending(left!.Coefficient, left.Exponent);
                left = left.Next;
                continue;
            }

            if (left is null || right.Exponent > left.Exponent)
            {
                result.AppendTermAssumeDescending(right.Coefficient, right.Exponent);
                right = right.Next;
                continue;
            }

            var combinedCoefficient = left.Coefficient + right.Coefficient;
            if (combinedCoefficient != 0)
            {
                result.AppendTermAssumeDescending(combinedCoefficient, left.Exponent);
            }

            left = left.Next;
            right = right.Next;
        }

        return result;
    }

    /// <summary>
    /// Multiplies two polynomials.
    /// Time: O(n * m + k log k), where k is the number of unique exponents in the result.
    /// Space: O(k).
    /// </summary>
    public Polynomial Multiply(Polynomial other)
    {
        ArgumentNullException.ThrowIfNull(other);

        var result = new Polynomial();
        if (_head is null || other._head is null)
        {
            return result;
        }

        var accumulator = new Dictionary<int, long>();
        for (var left = _head; left is not null; left = left.Next)
        {
            for (var right = other._head; right is not null; right = right.Next)
            {
                var exponent = checked(left.Exponent + right.Exponent);
                var coefficient = checked(left.Coefficient * right.Coefficient);

                accumulator.TryGetValue(exponent, out var currentCoefficient);
                accumulator[exponent] = checked(currentCoefficient + coefficient);
            }
        }

        foreach (var entry in accumulator.OrderByDescending(item => item.Key))
        {
            if (entry.Value != 0)
            {
                result.AppendTermAssumeDescending(entry.Value, entry.Key);
            }
        }

        return result;
    }

    public override string ToString()
    {
        if (_head is null)
        {
            return "0";
        }

        var builder = new StringBuilder();
        var current = _head;

        while (current is not null)
        {
            var coefficient = current.Coefficient;
            if (coefficient != 0)
            {
                if (builder.Length == 0)
                {
                    if (coefficient < 0)
                    {
                        builder.Append("-");
                    }

                    AppendTerm(builder, Math.Abs(coefficient), current.Exponent);
                }
                else
                {
                    builder.Append(coefficient < 0 ? " - " : " + ");
                    AppendTerm(builder, Math.Abs(coefficient), current.Exponent);
                }
            }

            current = current.Next;
        }

        return builder.Length == 0 ? "0" : builder.ToString();
    }

    private void AppendAsFirstTerm(long coefficient, int exponent)
    {
        var node = new PolynomialNode(coefficient, exponent);
        _head = node;
        _tail = node;
        TermCount = 1;
    }

    private void AppendTermAssumeDescending(long coefficient, int exponent)
    {
        if (coefficient == 0)
        {
            return;
        }

        if (_head is null)
        {
            AppendAsFirstTerm(coefficient, exponent);
            return;
        }

        if (_tail is null || exponent >= _tail.Exponent)
        {
            throw new InvalidOperationException("Terms must be appended in strictly descending exponent order.");
        }

        var node = new PolynomialNode(coefficient, exponent);
        _tail.Next = node;
        _tail = node;
        TermCount++;
    }

    private void RemoveNode(PolynomialNode? previous, PolynomialNode current)
    {
        if (previous is null)
        {
            _head = current.Next;
        }
        else
        {
            previous.Next = current.Next;
        }

        if (current.Next is null)
        {
            _tail = previous;
        }

        if (_head is null)
        {
            _tail = null;
        }

        TermCount--;
    }

    private static void AppendTerm(StringBuilder builder, long absoluteCoefficient, int exponent)
    {
        if (exponent == 0)
        {
            builder.Append(absoluteCoefficient);
            return;
        }

        if (absoluteCoefficient != 1)
        {
            builder.Append(absoluteCoefficient);
        }

        builder.Append("x");
        if (exponent != 1)
        {
            builder.Append("^");
            builder.Append(exponent);
        }
    }
}
