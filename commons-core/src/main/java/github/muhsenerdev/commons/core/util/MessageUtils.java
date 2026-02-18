package github.muhsenerdev.commons.core.util;

public final class MessageUtils {

    private MessageUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String format(String template, Object... args) {
        if (template == null) {
            return null;
        }

        if (args == null || args.length == 0) {
            return template;
        }

        StringBuilder result = new StringBuilder();
        int argIndex = 0;
        int i = 0;

        while (i < template.length()) {
            if (i < template.length() - 1 && template.charAt(i) == '{' && template.charAt(i + 1) == '}') {
                // Found a placeholder
                if (argIndex < args.length) {
                    result.append(args[argIndex] != null ? args[argIndex].toString() : "null");
                    argIndex++;
                } else {
                    // No more arguments, keep the placeholder
                    result.append("{}");
                }
                i += 2; // Skip both '{' and '}'
            } else {
                result.append(template.charAt(i));
                i++;
            }
        }

        return result.toString();
    }
}
