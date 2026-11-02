package lyc.compiler;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

import java_cup.runtime.Symbol;

public class ParserTest {

    @Test
    public void testSintaxisCompleta() throws Exception {
        compilationSuccessful(readFromFile());
    }

    private String readFromFile() throws Exception {
        String urlString = "https://raw.githubusercontent.com/Lenguajes-y-Compiladores-UnLaM/consignas-tp/refs/heads/main/lenguajes/L1_aho";
        URL url = new URL(urlString);
        try (InputStream in = url.openStream()) {
            return IOUtils.toString(in, StandardCharsets.UTF_8);
        }
    }

    private void compilationSuccessful(String input) throws Exception {
        assertThat(scan(input).sym).isEqualTo(ParserSym.EOF);
    }

    private Symbol scan(String input) throws Exception {
        Lexer lexer = new Lexer(IOUtils.toInputStream(input, StandardCharsets.UTF_8));
        Parser parser = new Parser(lexer);
        return parser.parse();
    }
}
