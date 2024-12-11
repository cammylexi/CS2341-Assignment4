import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        // Initialize hash tables
        SeparateChainingHashST<String, Integer> chainingOldHash = new SeparateChainingHashST<>(1000);
        SeparateChainingHashST<String, Integer> chainingNewHash = new SeparateChainingHashST<>(1000);
        LinearProbingHashST<String, Integer> linearOldHash = new LinearProbingHashST<>(20000);
        LinearProbingHashST<String, Integer> linearNewHash = new LinearProbingHashST<>(20000);

        String file = "C:\\Users\\cammc\\IdeaProjects\\Assignment 4\\src\\wordlist.10000.txt"; // Adjust file path

        // Load dictionary into hash tables
        try {
            loadHashTable(file, chainingOldHash, 1);
            loadHashTable(file, chainingNewHash, 2);

            loadHashTable(file, linearOldHash, 1);
            loadHashTable(file, linearNewHash, 2);
        } catch (IOException e) {
            System.out.println("Error reading the file: " + e.getMessage());
            return;
        }

        boolean isPasswordValid = true;

        // Password validation loop
        while (isPasswordValid) {
            System.out.println("Enter a strong password (at least 8 characters long):");
            String password = scan.nextLine().trim();

            testPasswordStrength(password, chainingOldHash, chainingNewHash, linearOldHash, linearNewHash);

            while (true) {
                System.out.println("Do you want to try another password (Yes or No):");
                String answer = scan.nextLine().trim().toLowerCase();

                if (answer.equals("yes")) {
                    System.out.println();
                    break;
                } else if (answer.equals("no")) {
                    System.out.println("Exiting the program.");
                    isPasswordValid = false;
                    break;
                } else {
                    System.out.println("Invalid input. Please enter Yes or No.");
                }
            }
        }
        scan.close(); // Close the scanner
    }

    public static void loadHashTable(String file, SeparateChainingHashST<String, Integer> table, int hashType) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        int index = 1;
        while ((line = br.readLine()) != null) {
            table.put(line, index, hashType);
            index++;
        }
        br.close();
    }

    public static void loadHashTable(String file, LinearProbingHashST<String, Integer> table, int hashType) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        int index = 1;
        while ((line = br.readLine()) != null) {
            table.put(line, index, hashType);
            index++;
        }
        br.close();
    }

    public static void testPasswordStrength(String password,
                                            SeparateChainingHashST<String, Integer> chaining1,
                                            SeparateChainingHashST<String, Integer> chaining2,
                                            LinearProbingHashST<String, Integer> linear1,
                                            LinearProbingHashST<String, Integer> linear2) {
        System.out.println("\nTesting password: " + password);

        boolean isStrong = true;

        // Check length
        if (password.length() < 8) {
            System.out.println("Password must be at least 8 characters long.");
            isStrong = false;
        }

        // Check if password exists in dictionary
        boolean inDictionary1 = chaining1.get(password, 1) != null || linear1.get(password, 1) != null;
        boolean inDictionary2 = chaining2.get(password, 2) != null || linear2.get(password, 2) != null;

        if (inDictionary1 || inDictionary2) {
            System.out.println("Password should not be a word in the dictionary.");
            isStrong = false;
        }

        // Check if password is a dictionary word followed by a digit
        boolean isWordFollowedByDigit = false;
        if (password.matches(".*\\d$")) { // Ends with a digit
            String wordPart = password.substring(0, password.length() - 1);
            if (chaining1.get(wordPart, 1) != null || chaining2.get(wordPart, 2) != null ||
                    linear1.get(wordPart, 1) != null || linear2.get(wordPart, 2) != null) {
                isWordFollowedByDigit = true;
            }
        }

        if (isWordFollowedByDigit) {
            System.out.println("Password should not be a dictionary word followed by a digit.");
            isStrong = false;
        }

        // Display comparisons
        System.out.println("Comparisons (Separate Chaining, Hash 1): " + chaining1.getComparisons());
        System.out.println("Comparisons (Separate Chaining, Hash 2): " + chaining2.getComparisons());
        System.out.println("Comparisons (Linear Probing, Hash 1): " + linear1.getComparisons());
        System.out.println("Comparisons (Linear Probing, Hash 2): " + linear2.getComparisons());

        // Final Result
        if (isStrong) {
            System.out.println("\nFinal Result: Strong Password");
        } else {
            System.out.println("\nFinal Result: Weak Password");
        }
        System.out.println();
    }
}


