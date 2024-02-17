import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;

public final class DifficultyParser {
    public static void main(String[] args) throws IOException {
        var file = new File(args[0]);
        var content = Files.readAllLines(file.toPath());

        // Remove unnecessary lines.
        content.subList(0, 7).clear();
        content.remove(content.size() - 1);

        // Parse each line.
        var parsedData = new LinkedList<String>();
        content.forEach(line -> parse(line, parsedData));

        // Print the parsed data.
        for (var parsed : parsedData) {
            System.out.println(parsed);
        }
    }

    private static void parse(String line, List<String> data) {
        line = line.trim(); // Remove leading and trailing whitespace.

        // Split by lines.
        var split = line.split("\\|");
        if (split.length == 1) return;

        var parsed = new StringBuilder();
        for (var i = 0; i < split.length; i++) {
            split[i] = split[i].trim(); // Remove leading and trailing whitespace.
        }

        parsed.append("D" + Integer.parseInt(split[0].substring(6, 9).trim()) + "(");
        parsed.append(Integer.parseInt(split[1]) + ", ");
        parsed.append(Integer.parseInt(split[2]) + ", ");
        parsed.append(Integer.parseInt(split[3]) + ", ");
        parsed.append(Integer.parseInt(split[4] )+ ", ");
        parsed.append(parseRange(split[5]) + ", ");
        parsed.append(Integer.parseInt(split[6]) + ", ");
        parsed.append(Integer.parseInt(split[7]) + ", ");
        parsed.append(Integer.parseInt(split[8]) + ", ");
        parsed.append(parseRange(split[9]) + ", ");
        parsed.append(Integer.parseInt(split[10]) + ", ");
        parsed.append(parseRange(split[11]) + ", ");
        parsed.append(Integer.parseInt(split[12]) + ", ");
        parsed.append(Integer.parseInt(split[13]));
        parsed.append(");");

        data.add(parsed.toString());
    }

    private static String parseRange(String value) {
        var split = value.split("-");
        return "new Pair<>(" + Integer.parseInt(split[0].substring(1).trim()) + ", "
                + Integer.parseInt(split[1].substring(0, split[1].length() - 1).trim()) + ")";
    }
}
