/**
 * Contains classes for parsing and executing the calculator. The parser and analyzers fail fast,
 * that is if any errors are encountered then execution is immediately stopped.
 * <p>
 * The parser implements the EBNF grammar below. For grammar validation
 * <a href="https://planetcalc.com/5600/">https://planetcalc.com/5600/</a> is a useful resource.
 *
 * <pre>{@code
 *     function            = arithmetic_function | let_function
 *     arithmetic_function = ("add" | "sub" | "mult" | "div") "(" expression "," expression ")"
 *     let_function        = "let" "(" variable "," value_expression "," function ")"
 *     expression          = number | variable | function
 *     value_expression    = number | function
 *     variable            = #'[A-Za-z]+'
 *     number              = ["-"] #'[0-9]+'
 * }</pre>
 * <p>
 * While the grammar does not define whitespace, the parser itself is whitespace insensitive and ignores any
 * whitespace encountered.
 */
package im.moonshot.curta.parser;
