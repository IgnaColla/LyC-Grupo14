package lyc.compiler;

import java_cup.runtime.Symbol;
import lyc.compiler.ParserSym;
import lyc.compiler.model.*;
import static lyc.compiler.constants.Constants.*;

%%

%public
%class Lexer
%unicode
%cup
%line
%column
%throws CompilerException
%eofval{
  return symbol(ParserSym.EOF);
%eofval}

%{
  private Symbol symbol(int type) {
    return new Symbol(type, yyline, yycolumn);
  }
  private Symbol symbol(int type, Object value) {
    return new Symbol(type, yyline, yycolumn, value);
  }

  // To validate content in comment
  private boolean isValidCommentChar(char c) {
    if (Character.isISOControl(c) && c != '\t' && c != '\n' && c != '\r') {
      return false;
    }

    return true;
  }

  private void validateComment(String comment) throws CompilerException {
    for (int i = 0; i < comment.length(); i++) {
      char c = comment.charAt(i);
      if (!isValidCommentChar(c)) {
        throw new UnknownCharacterException("Invalid character in comment: " + c);
      }
    }
  }
%}

LineTerminator = \r|\n|\r\n
InputCharacter = [^\r\n]
Identation =  [ \t\f]

Plus = "+"
Mult = "*"
Sub = "-"
Div = "/"
Assig = "=" | ":="
ParentesisAbre = "("
ParentesisCierra = ")"
LlaveAbre = "{"
LlaveCierra = "}"
Coma = ","
DosPuntos = ":"
PuntoYComa = ";"
CorcheteAbre = "["
CorcheteCierra = "]"
Mayor = ">"
Menor = "<"
Igual = "=="
Distinto = "!="
MayorIgual = ">="
MenorIgual = "<="
Letter = [a-zA-Z]
Digit = [0-9]
ComillaAbre = "“"
ComillaCierra = "”"

WhiteSpace = {LineTerminator} | {Identation}
Identifier = {Letter} ({Letter}|{Digit})*
IntegerConstant = {Digit}+
NegativeIntegerConstant = -{Digit}+
FloatConstant = {Digit}*\.{Digit}+ | {Digit}+\.{Digit}*
NegativeFloatConstant = -{FloatConstant}
StringConstant = \"[^\"]*\"
TextContent = {Letter} ({Letter}|{Digit})*
Comment = "#+"([^+]|"+"+[^#])*"+#"
BlockComment = "/*"([^*]|"*"+[^/])*"*/"

%%

/* keywords */
"read"                                   { return symbol(ParserSym.READ); }
"write"                                  { return symbol(ParserSym.WRITE); }
"init"                                   { return symbol(ParserSym.INIT); }
"if"                                     { return symbol(ParserSym.IF); }
"else"                                   { return symbol(ParserSym.ELSE); }
"while"                                  { return symbol(ParserSym.WHILE); }
"AND"                                    { return symbol(ParserSym.AND); }
"OR"                                     { return symbol(ParserSym.OR); }
"NOT"                                    { return symbol(ParserSym.NOT); }
"Float"                                  { return symbol(ParserSym.FLOAT_TYPE); }
"Int"                                    { return symbol(ParserSym.INT_TYPE); }
"String"                                 { return symbol(ParserSym.STRING_TYPE); }
"convDate"                               { return symbol(ParserSym.CONV_DATE); }
"triangleAreaMaximum"                    { return symbol(ParserSym.TRIANGLE_AREA_MAXIMUM); }

<YYINITIAL> {
  /* identifiers */
  {Identifier}                           { 
                                            String id = yytext();
                                            if (id.length() > MAX_LENGTH) {
                                              throw new InvalidLengthException("Identifier exceeds maximum length: " + id);
                                            }
                                            return symbol(ParserSym.IDENTIFIER, id);
                                         }

  /* Constants */
  {FloatConstant}                        { 
                                            String floatStr = yytext();
                                            try {
                                              float value = Float.parseFloat(floatStr);
                                              if (value > Float.MAX_VALUE) {
                                                throw new InvalidFloatException("Float out of range: " + floatStr);
                                              }
                                            } catch (NumberFormatException e) {
                                              throw new InvalidFloatException("Invalid float format: " + floatStr);
                                            }
                                            return symbol(ParserSym.FLOAT_CONSTANT, floatStr);
                                         }
  {IntegerConstant}                      { 
                                            String intStr = yytext();
                                            try {
                                              long value = Long.parseLong(intStr);
                                              if (value > Integer.MAX_VALUE) {
                                                throw new InvalidIntegerException("Positive Integer out of range: " + intStr);
                                              }
                                            } catch (NumberFormatException e) {
                                              throw new InvalidIntegerException("Invalid integer format: " + intStr);
                                            }
                                            return symbol(ParserSym.INTEGER_CONSTANT, intStr);
                                         }

  {StringConstant}                       { 
                                            String str = yytext();
                                            if (str.length() > MAX_STRING_LENGTH + 2) { // +2 for quotes use
                                              throw new InvalidLengthException("String exceeds maximum length: " + str);
                                            }
                                            return symbol(ParserSym.STRING_CONSTANT, str);
                                         }

  {TextContent}                          { 
                                            String content = yytext();
                                            if (content.length() > MAX_LENGTH) {
                                              throw new InvalidLengthException("Text content exceeds maximum length: " + content);
                                            }
                                            return symbol(ParserSym.TEXT_CONTENT, content);
                                         }

  /* operators */
  {Plus}                                    { return symbol(ParserSym.PLUS); }
  {Sub}                                     { return symbol(ParserSym.SUB); }
  {Mult}                                    { return symbol(ParserSym.MULT); }
  {Div}                                     { return symbol(ParserSym.DIV); }
  {Assig}                                   { return symbol(ParserSym.ASSIG); }
  {ParentesisAbre}                          { return symbol(ParserSym.PARENTESIS_ABRE); }
  {ParentesisCierra}                        { return symbol(ParserSym.PARENTESIS_CIERRA); }
  {LlaveAbre}                               { return symbol(ParserSym.LLAVE_ABRE); }
  {LlaveCierra}                             { return symbol(ParserSym.LLAVE_CIERRA); }
  {Coma}                                    { return symbol(ParserSym.COMA); }
  {DosPuntos}                               { return symbol(ParserSym.DOSPUNTOS); }
  {PuntoYComa}                              { return symbol(ParserSym.PUNTO_Y_COMA); }
  {CorcheteAbre}                            { return symbol(ParserSym.CORCHETE_ABRE); }
  {CorcheteCierra}                          { return symbol(ParserSym.CORCHETE_CIERRA); }
  {Mayor}                                   { return symbol(ParserSym.MAYOR); }
  {Menor}                                   { return symbol(ParserSym.MENOR); }
  {Igual}                                   { return symbol(ParserSym.IGUAL); }
  {Distinto}                                { return symbol(ParserSym.DISTINTO); }
  {MayorIgual}                              { return symbol(ParserSym.MAYOR_IGUAL); }
  {MenorIgual}                              { return symbol(ParserSym.MENOR_IGUAL); }
  {ComillaAbre}                             { return symbol(ParserSym.COMILLA_ABRE); }
  {ComillaCierra}                           { return symbol(ParserSym.COMILLA_CIERRA); }

  /* whitespace */
  {WhiteSpace}                              { /* ignore */ }

  /* comments with character validation */
  {Comment}                                 { 
                                              String comment = yytext();
                                              String content = comment.substring(2, comment.length() - 2);
                                              validateComment(content);
                                              /* ignore */ 
                                            }

  {BlockComment}                            { 
                                              String comment = yytext();
                                              String content = comment.substring(2, comment.length() - 2);
                                              validateComment(content);
                                              /* ignore */
                                            }

  /* Negative numbers - only at start of line or after whitespace */
  ^[ \t]*{NegativeIntegerConstant}       { 
                                            String intStr = yytext().trim();
                                            try {
                                              long value = Long.parseLong(intStr);
                                              if (value < Integer.MIN_VALUE) {
                                                throw new InvalidIntegerException("Negative Integer out of range: " + intStr);
                                              }
                                            } catch (NumberFormatException e) {
                                              throw new InvalidIntegerException("Invalid negative integer format: " + intStr);
                                            }
                                            return symbol(ParserSym.NEGATIVE_INTEGER_CONSTANT, intStr);
                                         }

  ^[ \t]*{NegativeFloatConstant}         { 
                                            String floatStr = yytext().trim();
                                            try {
                                              double value = Double.parseDouble(floatStr);
                                              if (value < -Float.MAX_VALUE) {
                                                throw new InvalidIntegerException("Negative Float out of range: " + floatStr);
                                              }
                                            } catch (NumberFormatException e) {
                                              throw new InvalidIntegerException("Invalid negative float format: " + floatStr);
                                            }
                                            return symbol(ParserSym.NEGATIVE_FLOAT_CONSTANT, floatStr);
                                         }
}

/* error fallback */
[^]                                      { throw new UnknownCharacterException(yytext()); }
