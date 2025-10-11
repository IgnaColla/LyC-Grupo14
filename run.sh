#!/bin/bash
echo "Compilando test.txt..."
java -jar ./target/lyc-compiler-1.0.0.jar ./src/main/resources/input/test.txt
echo ""
echo "=== Archivos generados ==="
[ -f symbol-table.txt ] && echo "✓ symbol-table.txt" || echo "✗ symbol-table.txt"
[ -f intermediate-code.txt ] && echo "✓ intermediate-code.txt" || echo "✗ intermediate-code.txt"
