package lyc.compiler.files;
import lyc.compiler.icg_tree.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsmCodeGenerator {
    private StringBuilder asmCode;
    private int labelCounter = 0;
    private Map<String, String> variables;
    private int etLabelCounter = 0;
    private Map<String, String> stringLabels = new HashMap<>();
    private int stringCounter = 0;

    public AsmCodeGenerator() {
        asmCode = new StringBuilder();
        variables = new HashMap<>();
    }

    public void generateFromAST(ProgramNode program) {
        generateHeader();
        extractVariablesFromSymbolTable();
        extractStringsFromAST(program);
        generateDataSection();
        generateCodeStart();
        for (TNode stmt : program.getStatements()) {
            generateStatement(stmt);
        }
        generateCodeEnd();
        writeToFile();
    }

    private void extractVariablesFromSymbolTable() {
        SymbolTable st = SymbolTable.getInstance();
        for (SymbolTable.Symbol symbol : st.getSymbols()) {
            if (symbol.getCategory().equals("VARIABLE")) {
                variables.put(symbol.getName(), symbol.getType());
            }
        }
    }

    private void extractStringsFromAST(ProgramNode program) {
        for (TNode stmt : program.getStatements()) {
            extractStringsFromNode(stmt);
        }
    }

    private void extractStringsFromNode(TNode node) {
        if (node instanceof WriteNode) {
            WriteNode wn = (WriteNode) node;
            String param = wn.getParameter().replaceAll("\"", "");
            if (!stringLabels.containsKey(param) && !isVariable(param)) {
                stringLabels.put(param, "T_STR_" + (stringCounter++));
            }
        } else if (node instanceof IfNode) {
            IfNode ifNode = (IfNode) node;
            for (TNode t : ifNode.getThenBlock()) {
                extractStringsFromNode(t);
            }
            if (ifNode.getElseBlock() != null) {
                for (TNode t : ifNode.getElseBlock()) {
                    extractStringsFromNode(t);
                }
            }
        } else if (node instanceof WhileNode) {
            WhileNode wn = (WhileNode) node;
            for (TNode t : wn.getBody()) {
                extractStringsFromNode(t);
            }
        }
    }

    private boolean isVariable(String name) {
        return variables.containsKey(name);
    }

    private void generateHeader() {
        asmCode.append(".MODEL LARGE\n");
        asmCode.append(".386\n");
        asmCode.append(".STACK 200h\n\n");
    }

    private void generateDataSection() {
        asmCode.append(".DATA\n");

        for (String var : variables.keySet()) {
            asmCode.append(" _").append(var).append(" dd ?\n");
        }

        asmCode.append(" _temp_area1 dd ?\n");
        asmCode.append(" _temp_area2 dd ?\n");
        asmCode.append(" _temp_x1 dd ?\n");
        asmCode.append(" _res1 dd ?\n");
        asmCode.append(" _res2 dd ?\n");
        asmCode.append(" _res3 dd ?\n");
        asmCode.append(" _two dd 2.0\n");
        asmCode.append(" _ten dd 10.0\n");
        asmCode.append(" _dot db '.','$'\n");
        asmCode.append(" _msg_triangle1 db \"Triangle 1: \",'$'\n");
        asmCode.append(" _msg_triangle2 db 0Dh, 0Ah, \"Triangle 2: \",'$'\n");
        asmCode.append(" _msg_areamax db 0Dh, 0Ah, \"AreaMax: \",'$'\n");
        asmCode.append(" _msg_convdate db 0Dh, 0Ah, \"ConvDate: \",'$'\n");
        asmCode.append(" _temp_f1 dd ?\n");
        asmCode.append(" _temp_f2 dd ?\n");
        asmCode.append(" _temp_f3 dd ?\n");
        asmCode.append(" _temp_f4 dd ?\n");
        asmCode.append(" _temp_f5 dd ?\n");
        asmCode.append(" _temp_f6 dd ?\n");
        asmCode.append(" _temp_coord_eax dd ?\n");
        asmCode.append(" _temp_coord_ebx dd ?\n");
        asmCode.append("\n");

        for (String str : stringLabels.keySet()) {
            String label = stringLabels.get(str);
            asmCode.append(" ").append(label).append(" db \"").append(str).append("\",'$'\n");
        }

        asmCode.append(" T_insert_base db \"Insert base: \",'$'\n");
        asmCode.append(" _result_area_str db 30 dup (?),'$'\n");
        asmCode.append(" _result_date_str db 12 dup (?),'$'\n");
        asmCode.append(" _NEWLINE db 0DH,0AH,'$'\n");
        asmCode.append(" _msgPRESS db 0DH,0AH,\"Press any key to continue...\",'$'\n\n");
    }

    private void generateCodeStart() {
        asmCode.append(".CODE\n\n");
        asmCode.append("print_float PROC\n");
        asmCode.append(" push bp\n");
        asmCode.append(" mov bp, sp\n");
        asmCode.append(" sub sp, 8\n");
        asmCode.append(" fstcw word ptr [bp-8]\n");
        asmCode.append(" mov ax, word ptr [bp-8]\n");
        asmCode.append(" or ax, 0C00h\n");
        asmCode.append(" mov word ptr [bp-6], ax\n");
        asmCode.append(" fldcw word ptr [bp-6]\n");
        asmCode.append(" fld st(0)\n");
        asmCode.append(" frndint\n");
        asmCode.append(" fist dword ptr [bp-4]\n");
        asmCode.append(" mov eax, [bp-4]\n");
        asmCode.append(" fldcw word ptr [bp-8]\n");
        asmCode.append(" push ecx\n");
        asmCode.append(" xor ecx, ecx\n");
        asmCode.append(" mov ebx, 10\n");
        asmCode.append(" test eax, eax\n");
        asmCode.append(" jnz _div_int\n");
        asmCode.append(" mov dl, '0'\n");
        asmCode.append(" mov ah, 2\n");
        asmCode.append(" int 21h\n");
        asmCode.append(" jmp _int_end\n");
        asmCode.append("_div_int:\n");
        asmCode.append(" xor edx, edx\n");
        asmCode.append(" div ebx\n");
        asmCode.append(" push dx\n");
        asmCode.append(" inc ecx\n");
        asmCode.append(" test eax, eax\n");
        asmCode.append(" jnz _div_int\n");
        asmCode.append("_print_int:\n");
        asmCode.append(" pop dx\n");
        asmCode.append(" add dl, '0'\n");
        asmCode.append(" mov ah, 2\n");
        asmCode.append(" int 21h\n");
        asmCode.append(" loop _print_int\n");
        asmCode.append("_int_end:\n");
        asmCode.append(" pop ecx\n");
        asmCode.append(" mov dx, OFFSET _dot\n");
        asmCode.append(" mov ah, 9\n");
        asmCode.append(" int 21h\n");
        asmCode.append(" fxch\n");
        asmCode.append(" fsub st(0), st(1)\n");
        asmCode.append(" fabs\n");
        asmCode.append(" fmul _ten\n");
        asmCode.append(" fmul _ten\n");
        asmCode.append(" fstcw word ptr [bp-8]\n");
        asmCode.append(" mov ax, word ptr [bp-8]\n");
        asmCode.append(" or ax, 0C00h\n");
        asmCode.append(" mov word ptr [bp-6], ax\n");
        asmCode.append(" fldcw word ptr [bp-6]\n");
        asmCode.append(" frndint\n");
        asmCode.append(" fistp dword ptr [bp-4]\n");
        asmCode.append(" fldcw word ptr [bp-8]\n");
        asmCode.append(" mov eax, [bp-4]\n");
        asmCode.append(" mov ebx, 10\n");
        asmCode.append(" xor edx, edx\n");
        asmCode.append(" div ebx\n");
        asmCode.append(" push dx\n");
        asmCode.append(" add al, '0'\n");
        asmCode.append(" mov dl, al\n");
        asmCode.append(" mov ah, 2\n");
        asmCode.append(" int 21h\n");
        asmCode.append(" pop dx\n");
        asmCode.append(" add dl, '0'\n");
        asmCode.append(" mov ah, 2\n");
        asmCode.append(" int 21h\n");
        asmCode.append(" mov dx, OFFSET _NEWLINE\n");
        asmCode.append(" mov ah, 9\n");
        asmCode.append(" int 21h\n");
        asmCode.append(" fstp st(0)\n");
        asmCode.append(" mov sp, bp\n");
        asmCode.append(" pop bp\n");
        asmCode.append(" ret\n");
        asmCode.append("print_float ENDP\n\n");

        asmCode.append("int_to_string PROC\n");
        asmCode.append(" push ebx ecx edx esi\n");
        asmCode.append(" mov ecx, 0\n");
        asmCode.append(" mov ebx, 10\n");
        asmCode.append(" cmp eax, 0\n");
        asmCode.append(" jne CONVERT_LOOP\n");
        asmCode.append(" mov byte ptr [edi], '0'\n");
        asmCode.append(" mov byte ptr [edi+1], '$'\n");
        asmCode.append(" jmp INT_END\n");
        asmCode.append("CONVERT_LOOP:\n");
        asmCode.append(" cmp eax, 0\n");
        asmCode.append(" je REVERSE_DIGITS\n");
        asmCode.append(" xor edx, edx\n");
        asmCode.append(" idiv ebx\n");
        asmCode.append(" add dl, '0'\n");
        asmCode.append(" push edx\n");
        asmCode.append(" inc ecx\n");
        asmCode.append(" jmp CONVERT_LOOP\n");
        asmCode.append("REVERSE_DIGITS:\n");
        asmCode.append(" cmp ecx, 0\n");
        asmCode.append(" je ADD_TERMINATOR\n");
        asmCode.append(" pop eax\n");
        asmCode.append(" mov [edi], al\n");
        asmCode.append(" inc edi\n");
        asmCode.append(" dec ecx\n");
        asmCode.append(" jmp REVERSE_DIGITS\n");
        asmCode.append("ADD_TERMINATOR:\n");
        asmCode.append(" mov byte ptr [edi], '$'\n");
        asmCode.append("INT_END:\n");
        asmCode.append(" pop esi edx ecx ebx\n");
        asmCode.append(" ret\n");
        asmCode.append("int_to_string ENDP\n\n");

        asmCode.append("ConvDate PROC\n");
        asmCode.append(" xor eax, eax\n");
        asmCode.append(" ret\n");
        asmCode.append("ConvDate ENDP\n\n");

        asmCode.append("START:\n");
        asmCode.append(" mov AX, DGROUP\n");
        asmCode.append(" mov DS, AX\n");
        asmCode.append(" mov ES, AX\n");
        asmCode.append(" finit\n\n");
    }

    private void generateStatement(TNode stmt) {
        String etLabel = "ET_" + (etLabelCounter++);
        int nextEtLabel = etLabelCounter;
        asmCode.append(etLabel).append(":\n");

        if (stmt instanceof AssignmentNode) {
            generateAssignment((AssignmentNode) stmt, nextEtLabel);
        } else if (stmt instanceof WriteNode) {
            generateWrite((WriteNode) stmt, nextEtLabel);
        } else if (stmt instanceof ReadNode) {
            generateRead((ReadNode) stmt, nextEtLabel);
        } else if (stmt instanceof IfNode) {
            generateIf((IfNode) stmt, nextEtLabel);
        } else if (stmt instanceof WhileNode) {
            generateWhile((WhileNode) stmt, nextEtLabel);
        }
    }

    private void generateAssignment(AssignmentNode assign, int nextEtLabel) {
        String var = assign.getVariable();
        TNode expr = assign.getExpression();

        if (expr instanceof LiteralNode) {
            String val = ((LiteralNode) expr).getValue();
            if (val.contains(".")) { // Float value
                try {
                    float floatVal = Float.parseFloat(val);
                    int bits = Float.floatToIntBits(floatVal);
                    asmCode.append(" mov eax, ").append(bits).append("\n");
                    asmCode.append(" mov dword ptr [_").append(var).append("], eax\n");
                } catch (NumberFormatException e) {
                    asmCode.append(" mov eax, 0\n");
                    asmCode.append(" mov dword ptr [_").append(var).append("], eax\n");
                }
            } else if (!isString(val)) { // Int
                try {
                    float floatVal = Float.parseFloat(val);
                    int bits = Float.floatToIntBits(floatVal);
                    asmCode.append(" mov eax, ").append(bits).append("\n");
                    asmCode.append(" mov dword ptr [_").append(var).append("], eax\n");
                } catch (NumberFormatException e) {
                    asmCode.append(" mov eax, 0\n");
                    asmCode.append(" mov dword ptr [_").append(var).append("], eax\n");
                }
            }
        } else if (expr instanceof ExpressionNode) {
            generateExpression((ExpressionNode) expr);
            asmCode.append(" mov dword ptr [_").append(var).append("], eax\n");
        } else if (expr instanceof ConvDateNode) {
            generateConvDateCall((ConvDateNode) expr, var);
        } else if (expr instanceof TriangleAreaNode) {
            generateTriangleAreaMaximumCallFPU((TriangleAreaNode) expr, var);
        }

        asmCode.append(" JMP ET_").append(nextEtLabel).append("\n\n");
    }

    private void generateTriangleAreaMaximumCallFPU(TriangleAreaNode triangleNode, String resultVar) {
        List<TriangleAreaNode.Coordinate> t1 = triangleNode.getTriangle1();
        List<TriangleAreaNode.Coordinate> t2 = triangleNode.getTriangle2();

        asmCode.append(" mov dx, OFFSET _msg_triangle1\n");
        asmCode.append(" mov ah, 09h\n");
        asmCode.append(" int 21h\n");

        asmCode.append(" ; Triangle 1\n");
        calculateAreaWithFPU(t1, "_temp_area1");
        printResult("_temp_area1");

        asmCode.append(" mov dx, OFFSET _msg_triangle2\n");
        asmCode.append(" mov ah, 09h\n");
        asmCode.append(" int 21h\n");

        asmCode.append(" ; Triangle 2\n");
        calculateAreaWithFPU(t2, "_temp_area2");
        printResult("_temp_area2");

        asmCode.append(" mov dx, OFFSET _msg_areamax\n");
        asmCode.append(" mov ah, 09h\n");
        asmCode.append(" int 21h\n");

        compareAndStoreMaximum(resultVar);
        printResult(resultVar);
}

    private void calculateAreaWithFPU(List<TriangleAreaNode.Coordinate> coords, String targetVar) {
        String x1 = coords.get(0).getX();
        String y1 = coords.get(0).getY();
        String x2 = coords.get(1).getX();
        String y2 = coords.get(1).getY();
        String x3 = coords.get(2).getX();
        String y3 = coords.get(2).getY();

        // x1 * (y2 - y3)
        loadFloatValue(y2, "_temp_f2");
        loadFloatValue(y3, "_temp_f3");
        asmCode.append(" fsub\n");
        loadFloatValue(x1, "_temp_f1");
        asmCode.append(" fmul\n");
        asmCode.append(" fstp dword ptr [_res1]\n");

        // x2 * (y3 - y1)
        loadFloatValue(y3, "_temp_f2");
        loadFloatValue(y1, "_temp_f3");
        asmCode.append(" fsub\n");
        loadFloatValue(x2, "_temp_f4");
        asmCode.append(" fmul\n");
        asmCode.append(" fstp dword ptr [_res2]\n");

        // x3 * (y1 - y2)
        loadFloatValue(y1, "_temp_f2");
        loadFloatValue(y2, "_temp_f3");
        asmCode.append(" fsub\n");
        loadFloatValue(x3, "_temp_f1");
        asmCode.append(" fmul\n");
        asmCode.append(" fstp dword ptr [_res3]\n");

        // Sum
        asmCode.append(" fld dword ptr [_res1]\n");
        asmCode.append(" fld dword ptr [_res2]\n");
        asmCode.append(" fadd\n");
        asmCode.append(" fld dword ptr [_res3]\n");
        asmCode.append(" fadd\n");
        asmCode.append(" fabs\n");
        asmCode.append(" fdiv dword ptr [_two]\n");
        asmCode.append(" fstp dword ptr [").append(targetVar).append("]\n");
    }

    private void loadFloatValue(String value, String tempVar) {
        if (isVariable(value)) {
            asmCode.append(" mov eax, [_").append(value).append("]\n");
            asmCode.append(" mov dword ptr [").append(tempVar).append("], eax\n");
            asmCode.append(" fld dword ptr [").append(tempVar).append("]\n");
        } else {
            try {
                // Try parse as float literal
                float val = Float.parseFloat(value);
                int bits = Float.floatToIntBits(val);
                asmCode.append(" mov eax, ").append(bits).append("\n");
                asmCode.append(" mov dword ptr [").append(tempVar).append("], eax\n");
                asmCode.append(" fld dword ptr [").append(tempVar).append("]\n");
            } catch (NumberFormatException e) {
                // Fallback: assume variable
                asmCode.append(" mov eax, [_").append(value).append("]\n");
                asmCode.append(" mov dword ptr [").append(tempVar).append("], eax\n");
                asmCode.append(" fld dword ptr [").append(tempVar).append("]\n");
            }
        }
    }

    private void compareAndStoreMaximum(String resultVar) {
        asmCode.append(" fld dword ptr [_temp_area1]\n");
        asmCode.append(" fld dword ptr [_temp_area2]\n");
        asmCode.append(" fcom\n");
        asmCode.append(" fstsw ax\n");
        asmCode.append(" sahf\n");
        asmCode.append(" jbe _usar_area2_").append(labelCounter).append("\n");

        // If didn't jump: area1 > area2
        asmCode.append(" fstp dword ptr [_").append(resultVar).append("]\n");
        asmCode.append(" fstp st(0)\n");
        asmCode.append(" jmp _area_fin_").append(labelCounter).append("\n");

        asmCode.append("_usar_area2_").append(labelCounter).append(":\n");
        asmCode.append(" fstp st(0)\n");
        asmCode.append(" fstp dword ptr [_").append(resultVar).append("]\n");

        asmCode.append("_area_fin_").append(labelCounter).append(":\n");
        labelCounter++;
    }

    private void printResult(String resultVar) {
        String varName = resultVar.startsWith("_") ? resultVar : "_" + resultVar;
        asmCode.append(" fld dword ptr [").append(varName).append("]\n");
        asmCode.append(" call print_float\n");
    }

    private void generateConvDateCall(ConvDateNode dateNode, String resultVar) {
        int day = parseInt(dateNode.getDay());
        int month = parseInt(dateNode.getMonth());
        int year = parseInt(dateNode.getYear());

        asmCode.append(" mov dx, OFFSET _msg_convdate\n");
        asmCode.append(" mov ah, 09h\n");
        asmCode.append(" int 21h\n");

        asmCode.append(" mov eax, ").append(year).append("\n");
        asmCode.append(" mov ebx, 10000\n");
        asmCode.append(" imul eax, ebx\n");
        asmCode.append(" mov ecx, eax\n");
        asmCode.append(" mov eax, ").append(month).append("\n");
        asmCode.append(" mov ebx, 100\n");
        asmCode.append(" imul eax, ebx\n");
        asmCode.append(" add ecx, eax\n");
        asmCode.append(" mov eax, ").append(day).append("\n");
        asmCode.append(" add ecx, eax\n");
        asmCode.append(" mov dword ptr [_").append(resultVar).append("], ecx\n");
        asmCode.append(" mov eax, [_").append(resultVar).append("]\n");
        asmCode.append(" lea edi, [_result_date_str]\n");
        asmCode.append(" call int_to_string\n");
        asmCode.append(" mov dx, OFFSET _result_date_str\n");
        asmCode.append(" mov ah, 09h\n");
        asmCode.append(" int 21h\n");

        asmCode.append(" mov dx, OFFSET _NEWLINE\n");
        asmCode.append(" mov ah, 09h\n");
        asmCode.append(" int 21h\n");
    }

    private void generateWrite(WriteNode write, int nextEtLabel) {
        String param = write.getParameter().replaceAll("\"", "");
        if (!isVariable(param)) {
            String msgLabel = getMsgLabel(param);
            asmCode.append(" mov dx, OFFSET ").append(msgLabel).append("\n");
            asmCode.append(" mov ah, 09h\n");
            asmCode.append(" int 21h\n");
        }

        asmCode.append(" mov dx, OFFSET _NEWLINE\n");
        asmCode.append(" mov ah, 09h\n");
        asmCode.append(" int 21h\n");
        asmCode.append(" JMP ET_").append(nextEtLabel).append("\n\n");
    }

    private void generateRead(ReadNode read, int nextEtLabel) {
        asmCode.append(" mov dx, OFFSET T_insert_base\n");
        asmCode.append(" mov ah, 09h\n");
        asmCode.append(" int 21h\n");
        asmCode.append(" JMP ET_").append(nextEtLabel).append("\n\n");
    }

    private void generateIf(IfNode ifStmt, int nextEtLabel) {
        int skipLabel = labelCounter++;
        int endLabel = labelCounter++;
        generateCondition(ifStmt.getCondition(), skipLabel);

        for (TNode t : ifStmt.getThenBlock()) {
            if (t instanceof WriteNode) {
                generateWriteInline((WriteNode) t);
            }
        }

        if (ifStmt.getElseBlock() != null && !ifStmt.getElseBlock().isEmpty()) {
            asmCode.append(" JMP ET_IF_END_").append(endLabel).append("\n");
            asmCode.append("ET_IF_").append(skipLabel).append(":\n");
            for (TNode t : ifStmt.getElseBlock()) {
                if (t instanceof WriteNode) {
                    generateWriteInline((WriteNode) t);
                }
            }
        } else {
            asmCode.append("ET_IF_").append(skipLabel).append(":\n");
        }

        asmCode.append("ET_IF_END_").append(endLabel).append(":\n");
        asmCode.append(" JMP ET_").append(nextEtLabel).append("\n\n");
    }

    private void generateWhile(WhileNode whileStmt, int nextEtLabel) {
        int whileStart = labelCounter++;
        int whileEnd = labelCounter++;
        asmCode.append("ET_WHILE_").append(whileStart).append(":\n");

        ConditionNode cn = (ConditionNode) whileStmt.getCondition();
        TNode left = cn.getLeft();
        TNode right = cn.getRight();

        if (left instanceof LiteralNode && right instanceof LiteralNode) {
            String leftVal = cleanValue(((LiteralNode) left).getValue());
            String rightVal = cleanValue(((LiteralNode) right).getValue());

            if (isVariable(leftVal)) {
                asmCode.append(" mov eax, [_").append(leftVal).append("]\n");
            } else {
                asmCode.append(" mov eax, ").append(leftVal).append("\n");
            }
            if (isVariable(rightVal)) {
                asmCode.append(" mov ebx, [_").append(rightVal).append("]\n");
            } else {
                asmCode.append(" mov ebx, ").append(rightVal).append("\n");
            }

            asmCode.append(" cmp eax, ebx\n");
            if (cn.getOperator().equals("<")) {
                asmCode.append(" JGE ET_WHILE_").append(whileEnd).append("\n");
            }
        }

        for (TNode t : whileStmt.getBody()) {
            if (t instanceof AssignmentNode) {
                AssignmentNode assign = (AssignmentNode) t;
                if (assign.getExpression() instanceof ExpressionNode) {
                    generateExpression((ExpressionNode) assign.getExpression());
                    asmCode.append(" mov dword ptr [_").append(assign.getVariable()).append("], eax\n");
                }
            }
        }

        asmCode.append(" JMP ET_WHILE_").append(whileStart).append("\n");
        asmCode.append("ET_WHILE_").append(whileEnd).append(":\n");
        asmCode.append(" JMP ET_").append(nextEtLabel).append("\n\n");
    }

    private void generateWriteInline(WriteNode write) {
        String param = write.getParameter().replaceAll("\"", "");
        if (!isVariable(param)) {
            String msgLabel = getMsgLabel(param);
            asmCode.append(" mov dx, OFFSET ").append(msgLabel).append("\n");
            asmCode.append(" mov ah, 09h\n");
            asmCode.append(" int 21h\n");
        }

        asmCode.append(" mov dx, OFFSET _NEWLINE\n");
        asmCode.append(" mov ah, 09h\n");
        asmCode.append(" int 21h\n");
    }

    private void generateCondition(TNode cond, int label) {
        if (cond instanceof ConditionNode) {
            ConditionNode cn = (ConditionNode) cond;
            TNode left = cn.getLeft();
            TNode right = cn.getRight();

            if (left instanceof LiteralNode && right instanceof LiteralNode) {
                String leftVal = cleanValue(((LiteralNode) left).getValue());
                String rightVal = cleanValue(((LiteralNode) right).getValue());

                if (isVariable(leftVal)) {
                    asmCode.append(" mov eax, [_").append(leftVal).append("]\n");
                } else {
                    asmCode.append(" mov eax, ").append(leftVal).append("\n");
                }

                if (isVariable(rightVal)) {
                    asmCode.append(" mov ebx, [_").append(rightVal).append("]\n");
                } else {
                    asmCode.append(" mov ebx, ").append(rightVal).append("\n");
                }

                asmCode.append(" cmp eax, ebx\n");
                asmCode.append(" J").append(getJumpOpposite(cn.getOperator())).append(" ET_IF_").append(label).append("\n");
            }
        }
    }

    private String getJumpOpposite(String op) {
        switch (op) {
            case ">": return "LE";
            case "<": return "GE";
            case "==": return "NE";
            default: return "MP";
        }
    }

    private void generateExpression(ExpressionNode expr) {
        String op = expr.getOperator();
        TNode left = expr.getLeft();
        TNode right = expr.getRight();

        if (left instanceof LiteralNode) {
            String val = cleanValue(((LiteralNode) left).getValue());
            if (isVariable(val)) {
                asmCode.append(" mov eax, [_").append(val).append("]\n");
            } else {
                asmCode.append(" mov eax, ").append(val).append("\n");
            }
        }

        if (right instanceof LiteralNode) {
            String val = cleanValue(((LiteralNode) right).getValue());
            if (isVariable(val)) {
                asmCode.append(" mov ebx, [_").append(val).append("]\n");
            } else {
                asmCode.append(" mov ebx, ").append(val).append("\n");
            }
        }

        switch (op) {
            case "+": asmCode.append(" add eax, ebx\n"); break;
            case "-": asmCode.append(" sub eax, ebx\n"); break;
            case "*": asmCode.append(" imul eax, ebx\n"); break;
            case "/":
                int skipDiv = labelCounter++;
                asmCode.append(" cmp ebx, 0\n");
                asmCode.append(" JE SKIP_DIV_").append(skipDiv).append("\n");
                asmCode.append(" cdq\n");
                asmCode.append(" idiv ebx\n");
                asmCode.append("SKIP_DIV_").append(skipDiv).append(":\n");
                break;
        }
    }

    private String cleanValue(String val) {
        val = val.replaceAll("\"", "");
        if (val.contains(".")) {
            try {
                double d = Double.parseDouble(val);
                return String.valueOf((long)(d * 1000));
            } catch (NumberFormatException e) {
                return "0";
            }
        }
        return val;
    }

    private int parseInt(String val) {
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            try {
                return (int) Double.parseDouble(val);
            } catch (NumberFormatException e2) {
                return 0;
            }
        }
    }

    private boolean isString(String val) {
        return val.isEmpty() || !val.matches("-?\\d+(\\.\\d+)?");
    }

    private String getMsgLabel(String param) {
        if (stringLabels.containsKey(param)) {
            return stringLabels.get(param);
        }
        return "T_STR_UNKNOWN";
    }

    private void generateCodeEnd() {
        asmCode.append("ET_").append(etLabelCounter).append(":\n");
        asmCode.append(" mov dx, OFFSET _NEWLINE\n");
        asmCode.append(" mov ah, 09h\n");
        asmCode.append(" int 21h\n");
        asmCode.append(" mov dx, OFFSET _msgPRESS\n");
        asmCode.append(" mov ah, 09h\n");
        asmCode.append(" int 21h\n");
        asmCode.append(" mov ah, 01h\n");
        asmCode.append(" int 21h\n");
        asmCode.append(" mov ax, 4C00h\n");
        asmCode.append(" int 21h\n\n");
        asmCode.append("END START\n");
    }

    private void writeToFile() {
        try {
            File outputFile = new File("target/output/final.asm");
            outputFile.getParentFile().mkdirs();
            try (FileWriter writer = new FileWriter(outputFile)) {
                writer.write(asmCode.toString());
            }
        } catch (IOException e) {
            System.err.println("Error generating final.asm: " + e.getMessage());
        }
    }
}
