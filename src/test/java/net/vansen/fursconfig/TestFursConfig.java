package net.vansen.fursconfig;

import com.typesafe.config.ConfigFactory;

import java.util.concurrent.*;

class TestFursConfig {
    public static void main(String[] args) {
        String config = """
                is_enabled = true
                what_is_3_and_3 = 6
                some_string = "This is a very long string that goes on and on and on..."
                some_integer = 12345
                some_float = 3.14159
                some_double = 3.141592653589793
                some_long = 123456789012345
                some_boolean = true
                some_list = ["a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"]
                """;
        //String config = largeConfig().toString();

        long totalNs = 0;
        for (int i = 0; i < 50; i++) {
            FursConfig fursConfig = new FursConfig();
            long nano2 = System.nanoTime();
            fursConfig.parse(config);
            long endNs2 = System.nanoTime();
            totalNs += (endNs2 - nano2);
        }

        double averageNs = (double) totalNs / 50;
        double averageMs = averageNs / 1000000.0;

        System.out.printf("For furs config, average time over 50 parses: %d ns, %.2f ms%n", (long) averageNs, averageMs);

        long totalNs2 = 0;
        for (int i = 0; i < 50; i++) {
            long nano = System.nanoTime();
            ConfigFactory.parseString(config);
            long endNs = System.nanoTime();
            totalNs2 += (endNs - nano);
        }

        double averageNs2 = (double) totalNs2 / 50;
        double averageMs2 = averageNs2 / 1000000.0;

        System.out.printf("For typesafe's config, average time over 50 parses: %d ns, %.2f ms%n", (long) averageNs2, averageMs2);

        double ratio = averageNs / averageNs2;
        double percentage = (ratio - 1) * 100;

        System.out.printf("Furs config is %.2f%% %s than typesafe's config%n",
                Math.abs(percentage),
                percentage > 0 ? "slower" : "faster");
    }

    private static StringBuilder largeConfig() {
        int numBranches = 100;
        int numSubBranches = 10;
        int numProperties = 100;
        int numListElements = 100;

        StringBuilder config = new StringBuilder();

        for (int i = 0; i < numBranches; i++) {
            config.append("# Branch ").append(i + 1).append("\n");
            config.append("branch_").append(i + 1).append(" {\n");
            for (int j = 0; j < numProperties; j++) {
                config.append("  some_property_").append(j + 1).append(" = \"This is a very long string that goes on and on and on\"\n");
            }

            for (int k = 0; k < numSubBranches; k++) {
                config.append("  # Sub-branch ").append(k + 1).append("\n");
                config.append("  sub_branch_").append(k + 1).append(" {\n");
                for (int l = 0; l < numProperties; l++) {
                    config.append("    some_other_property_").append(l + 1).append(" = \"Hello from sub-branch ").append(k + 1).append("!\"\n");
                }
                config.append("  }\n");
            }

            config.append("}\n\n");
        }

        config.append("# List of strings\n");
        config.append("list_of_strings = [\n");
        config.append("  \"This is a very long string that goes on and on and on\",\n".repeat(numListElements));
        config.append("]\n\n");

        config.append("# List of integers\n");
        config.append("list_of_integers = [\n");
        for (int i = 0; i < numListElements; i++) {
            config.append("  ").append(ThreadLocalRandom.current().nextInt(100000)).append(",\n");
        }
        config.append("]\n\n");

        config.append("# List of floats\n");
        config.append("list_of_floats = [\n");
        for (int i = 0; i < numListElements; i++) {
            config.append("  ").append(ThreadLocalRandom.current().nextDouble() * 100).append(",\n");
        }
        config.append("]\n");
        return config;
    }
}