package com.example.turisticheska_knizhka.Helpers;

public class NumberImgParser {
    public static String parseListEmojiForNum(int number) {
        // Convert the number to a string
        String numberString = String.valueOf(number);

        StringBuilder result = new StringBuilder();

        // Iterate over each digit of the number
        for (int i = 0; i < numberString.length(); i++) {
            // Get the current digit
            char digit = numberString.charAt(i);

            // Parse the digit and append the parsed string to the result
            result.append(parseEmojiForDigit(Character.getNumericValue(digit)));
        }

        // Return the result
        return result.toString();
    }

    private static String parseEmojiForDigit(int digit) {
        // Parse each digit and return the corresponding string
        switch (digit) {
            case 0:
                return "0\uFE0F⃣";
            case 1:
                return "1\uFE0F⃣";
            case 2:
                return "2\uFE0F⃣";
            case 3:
                return "3\uFE0F⃣";
            case 4:
                return "4\uFE0F⃣";
            case 5:
                return "5\uFE0F⃣";
            case 6:
                return "6\uFE0F⃣";
            case 7:
                return "7\uFE0F⃣";
            case 8:
                return "8\uFE0F⃣";
            case 9:
                return "9\uFE0F⃣";
            default:
                return ""; // Default case if no match is found
        }
    }
}
