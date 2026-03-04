# Linked List Polynomial (Console-Only, C#)

This project is prepared for assignment submission and local execution.

There is no web UI and no API service.  
It is a console program plus core logic and unit tests.

## Project Structure

- `src/LinkedList.Polynomial.Core` - polynomial linked-list implementation
- `src/LinkedList.Polynomial.App` - console entry point
- `tests/LinkedList.Polynomial.Tests` - unit tests

## Prerequisite

- .NET SDK `9.0.305` (pinned in `global.json`)

## One-Command Run (Production-Style Gate)

Use these scripts to run the full local gate in one command:

- `restore`
- `build` (`Release`)
- `test` (`Release`)
- run console app (`Release`)

macOS/Linux:

```bash
./run.sh
```

Windows PowerShell:

```powershell
./run.ps1
```

Both scripts fail fast and return a non-zero exit code if any stage fails.

## After Clone

```bash
dotnet restore
dotnet build
dotnet test
```

Expected test status: `Passed: 5, Failed: 0`

## Run the Console Program

```bash
dotnet run --project src/LinkedList.Polynomial.App
```

Example output:

```text
Linked-List Polynomial Demo
A(x)      = 2x^5 + 4x^3 + 3x^2 + 5
B(x)      = 3x^4 - 4x^3 + 6
A(x)+B(x) = 2x^5 + 3x^4 + 3x^2 + 11
A(x)*B(x) = 6x^9 - 8x^8 + 12x^7 - 7x^6 + 15x^4 + 4x^3 + 18x^2 + 30

Complexity:
- Add: O(n + m)
- Multiply: O(n * m + k log k), where k = unique exponents in the result
```

## Assignment Coverage

- Polynomial represented by singly linked list (sorted by exponent)
- `AddTerm`
- `Add`
- `Multiply`
- Big-O explanation in code and console output

## Console-Only Enterprise Baseline

- single runtime and single deployment target (`.NET`)
- deterministic builds and warnings as errors (`Directory.Build.props`)
- strict test gate before execution (`run.sh` / `run.ps1`)
- explicit SDK pinning (`global.json`)

## Big-O Summary

- `AddTerm`: `O(n)` time, `O(1)` extra space
- `Add`: `O(n + m)` time, `O(n + m)` output space
- `Multiply`: `O(n * m + k log k)` time, `O(k)` extra space

`k` = number of unique exponents in the result polynomial.
# linked-list
