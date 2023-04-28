package de.Kurfat.Java.Minecraft.BetterChair;

public class Colors {

    private static final String COLOR_PATTERN = "\u001b[38;5;%dm";
    private static final String FORMAT_PATTERN = "\u001b[%dm";

    public enum COLORS implements IColor {

        BLACK('0', 0),
        WHITE('f', 15),
        GOLD('6', 172),
        YELLOW('e', 11);

        private final char chatCode;
        private final int consoleCode;

        COLORS(char chatCode, int consoleCode) {
            this.chatCode = chatCode;
            this.consoleCode = consoleCode;
        }

        @Override
        public String toString() {
            return String.format(COLOR_PATTERN, consoleCode);
        }

        public enum LIGHT implements IColor {

            GREEN('a', 10),
            RED('c', 9),
            GRAY('7', 246),
            BLUE('9', 4),
            AQUA('b', 51),
            PURPLE('d', 13);

            private final char chatCode;
            private final int consoleCode;

            LIGHT(char chatCode, int consoleCode) {
                this.chatCode = chatCode;
                this.consoleCode = consoleCode;
            }

            @Override
            public String toString() {
                return String.format(COLOR_PATTERN, consoleCode);
            }

            public char getChatCode() {
                return chatCode;
            }

            public int getConsoleCode() {
                return consoleCode;
            }

        }

        public enum DARK implements IColor {

            GREEN('2', 2),
            RED('4', 1),
            GRAY('8', 8),
            BLUE('1', 4),
            AQUA('3', 30),
            PURPLE('5', 54);

            private final char chatCode;
            private final int consoleCode;

            DARK(char chatCode, int consoleCode) {
                this.chatCode = chatCode;
                this.consoleCode = consoleCode;
            }

            @Override
            public String toString() {
                return String.format(COLOR_PATTERN, consoleCode);
            }

            public char getChatCode() {
                return chatCode;
            }

            public int getConsoleCode() {
                return consoleCode;
            }

        }

        public char getChatCode() {
            return chatCode;
        }

        public int getConsoleCode() {
            return consoleCode;
        }
    }

    public enum FORMATS implements IColor {

        STRIKETHROUGH('m', 9),
        ITALIC('o', 3),
        BOLD('l', 1),
        UNDERLINE('n', 4),
        RESET('r', 0);

        private final char chatCode;
        private final int consoleCode;

        FORMATS(char chatCode, int consoleCode) {
            this.chatCode = chatCode;
            this.consoleCode = consoleCode;
        }

        @Override
        public String toString() {
            return String.format(COLOR_PATTERN, consoleCode);
        }

        public char getChatCode() {
            return chatCode;
        }

        public int getConsoleCode() {
            return consoleCode;
        }

    }

    public interface IColor {
        char getChatCode();

        int getConsoleCode();
    }

}
