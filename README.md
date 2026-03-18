# Linked List Polynomial (Console-Only, Java)

This repository is a local console application for the polynomial linked-list assignment.

There is no web UI and no API layer.
The solution is implemented in Java with a single runtime target, a deterministic local build, automated tests, and one-command execution scripts for macOS, Linux, and Windows.

## Requirements

- JDK 17
- `java` and `javac` available in `PATH`

## Project Structure

- `src/main/java` - production code
- `src/test/java` - test suite
- `run.sh` - one-command local verification for macOS/Linux
- `run.ps1` - one-command local verification for Windows PowerShell

## How To Run After Clone

macOS/Linux:

```bash
./run.sh
```

If the shell reports `Permission denied`, run:

```bash
chmod +x ./run.sh
./run.sh
```

Windows PowerShell:

```powershell
./run.ps1
```

If PowerShell blocks script execution, run:

```powershell
powershell -ExecutionPolicy Bypass -File .\run.ps1
```

Both scripts perform the same pipeline:

- clean build output
- compile main sources with warnings treated as errors
- compile test sources with warnings treated as errors
- run tests
- run the console demo

If any step fails, the script exits with a non-zero code immediately.

## What The Program Solves

The polynomial is stored as a singly linked list sorted by descending exponent.
Each node stores:

- coefficient
- exponent
- pointer to the next node

The implementation includes:

- `addTerm`
- `add`
- `multiply`
- string formatting for readable polynomial output

## Complexity

- `addTerm`: `O(n)`
- `add`: linear merge over the full input, which is `O(a + b)` or simply `O(n)` when `n` means the total number of input nodes
- `multiply`: `O(a * b + k log k)`

`a` and `b` are the term counts of the two input polynomials.
`k` is the number of unique exponents in the multiplication result.

## Demo Output

```text
Linked-List Polynomial Demo
A(x)      = 2x^5 + 4x^3 + 3x^2 + 5
B(x)      = 3x^4 - 4x^3 + 6
A(x)+B(x) = 2x^5 + 3x^4 + 3x^2 + 11
A(x)*B(x) = 6x^9 - 8x^8 + 12x^7 - 7x^6 + 15x^4 + 4x^3 + 18x^2 + 30

Complexity:
- Add: O(n) over the total number of input nodes
- Multiply: O(a * b + k log k)
```

## Notes On Design Direction

This project intentionally stays console-only and uses the JDK directly.
For this assignment, that keeps the runtime surface small, removes unnecessary framework overhead, and gives the same execution flow across local machines and CI.
