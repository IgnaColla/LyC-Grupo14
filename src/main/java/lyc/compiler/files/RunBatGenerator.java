package lyc.compiler.files;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RunBatGenerator {
    private static final String DOSBOX_PATH = "C:\\Program Files (x86)\\DOSBox-0.74-3\\DOSBox.exe";
    private static final String CONF_PATH = "\\\\wsl.localhost\\Ubuntu\\home\\boonkie\\unlam\\lyc\\tp\\LyC-Grupo14\\target\\output\\g14.conf";
    private static final String TASM_PATH = "C:\\TASM";
    private static final String OUTPUT_DIR = "target/output";

    public static void generateRunBat() {
        String batContent = "@echo off\r\n" +
            "copy \"\\\\wsl.localhost\\Ubuntu\\home\\boonkie\\unlam\\lyc\\tp\\LyC-Grupo14\\target\\output\\final.asm\" \"C:\\TASM\\final.asm\"\r\n" +
            "\"" + DOSBOX_PATH + "\" -conf \"" + CONF_PATH + "\"\r\n";

        String confContent = "[cpu]\r\n" +
            "core=auto\r\n" +
            "cycles=max\r\n" +
            "\r\n" +
            "[mixer]\r\n" +
            "rate=22050\r\n" +
            "\r\n" +
            "[dos]\r\n" +
            "xms=true\r\n" +
            "ems=true\r\n" +
            "umb=true\r\n" +
            "\r\n" +
            "[autoexec]\r\n" +
            "mount E " + TASM_PATH + "\r\n" +
            "E:\r\n" +
            "path " + TASM_PATH + "\r\n" +
            "tasm final.asm\r\n" +
            "tlink final.obj\r\n" +
            "final.exe\r\n" +
            "del final.obj\r\n" +
            "del final.map\r\n";

        try {
            File outputDir = new File(OUTPUT_DIR);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            File batFile = new File(OUTPUT_DIR + "/run_g14.bat");
            try (FileWriter writer = new FileWriter(batFile)) {
                writer.write(batContent);
            }

            File confFile = new File(OUTPUT_DIR + "/g14.conf");
            try (FileWriter writer = new FileWriter(confFile)) {
                writer.write(confContent);
            }

        } catch (IOException e) {
            System.err.println("Error generating files: " + e.getMessage());
        }
    }
}
