package com.portsip.main.bubbles;

/**
 * Created by Niggui on 03.11.2015.
 */
public class BubbleCreater {

        public static final String LOREM_IPSUM = "";

        private String[] bubbleWord = "".split("\\s");
        public String getWords() {
            return this.getWords(50);
        }
        public String getWords(int amount) {
            return this.getWords(amount, 0);
        }
        public String getWords(int amount, int startIndex) {
            if (startIndex < 0 || startIndex > 49) {
                throw new IndexOutOfBoundsException("startIndex must be >= 0 and < 50");
            }
            int word = startIndex;
            StringBuilder lorem = new StringBuilder();
            for (int i = 0; i < amount; ++i) {
                if (word == 50) {
                    word = 0;
                }
                lorem.append(this.bubbleWord[word]);
                if (i < amount - 1) {
                    lorem.append(' ');
                }
                ++word;
            }
            return lorem.toString();
        }
        public String getParagraphs() {
            return this.getParagraphs(2);
        }

        public String getParagraphs(int amount) {
            StringBuilder lorem = new StringBuilder();
            for (int i = 0; i < amount; ++i) {
                lorem.append("");
                if (i >= amount - 1) continue;
                lorem.append("\n\n");
            }
            return lorem.toString();
        }
    }
