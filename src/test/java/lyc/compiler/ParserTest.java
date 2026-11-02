package lyc.compiler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

import java_cup.runtime.Symbol;

public class ParserTest {

    private static final String REMOTE_URL
            = "https://raw.githubusercontent.com/Lenguajes-y-Compiladores-UnLaM/consignas-tp/refs/heads/main/lenguajes/L1_aho";
    private static final String LOCAL_RESOURCE = "/input/L1_aho.txt";

    @Test
    public void testSintaxisCompleta() throws Exception {
        compilationSuccessful(readFromFile());
    }

    private String readFromFile() throws Exception {
        try {
            URLConnection conn = new URL(REMOTE_URL).openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            try (InputStream in = conn.getInputStream()) {
                String content = IOUtils.toString(in, StandardCharsets.UTF_8);
                if (!content.isBlank()) {
                    return content;
                }
            }
        } catch (IOException e) {
            System.err.println("[ParserTest] No se pudo descargar L1_aho (" + e.getMessage() + "); uso la copia local");
        }
        try (InputStream in = ParserTest.class.getResourceAsStream(LOCAL_RESOURCE)) {
            assertThat(in).isNotNull();
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
