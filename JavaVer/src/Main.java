import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main{
    static Random random = new Random();

    static Map<Character, Double> weights = new HashMap<>();
    static Map<Character, Double>[] specialWeights = new HashMap[5];

    static void initializeWeights(){
        char[] keys = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        double[] values = {1.125, 1.027, 1.036, 1.033, 1.1, 1.02, 1.06, 1.031, 1.061, 1.002, 1.021, 1.056, 1.031, 1.052, 1.066, 1.03, 1.002, 1.072, 1.075, 1.056, 1.044, 1.011, 1.016, 1.002, 1.038, 1.006};

        for(int i = 0; i < keys.length; i++){
            weights.put(keys[i], values[i]);
        }
    }

    static void initializeSpecialWeights(){
        for(int i = 0; i < 5; i++){
            specialWeights[i] = new HashMap<>();
        }
        specialWeights[0].put('v', 0.0);
        specialWeights[0].put('q', 0.02);
        specialWeights[0].put('s', -0.02);
        specialWeights[1].put('q', 0.0);
        specialWeights[1].put('v', 0.02);
        specialWeights[1].put('g', 0.02);
        specialWeights[1].put('j', 0.02);
        specialWeights[2].put('k', 0.0);
        specialWeights[3].put('x', 0.02);
    }

    // Checks to see if an appropriate amount of information has been achieved
    static boolean containAll(Set<Character>[] alphabetLeft){
        for(int i = 0; i < 5; i++){
            if(!alphabetLeft[i].isEmpty()){
                return false;
            }
        }
        return true;
    }

    // Generates an array containing the strings of letters not yet used in each position
    static void updateAlphaLibrary(Set<Character>[] alphabetLeft, String newWord){
        for(int i = 0; i < 5; i++){
            alphabetLeft[i].remove(newWord.charAt(i));
        }
    }

    // Returns the amount of information found if this word was used as the next word
    static double newAlphaLocations(Set<Character>[] alphabetLeft, String word, boolean beg, Set<String> wordsToRemove){
        double count = 0.0;
        for(int i = 0; i < 5; i++){
            if(alphabetLeft[i].contains(word.charAt(i))){
                if(beg){
                    count += random.nextDouble() * 0.05 + 1.0 / weights.get(word.charAt(i));
                }else{
                    count += 1.0 + getSWeight(word.charAt(i), i);
                }
            }
        }

        if(count == 0.0){
            wordsToRemove.add(word);
        }
        return count;
    }

    static double getSWeight(char ch, int pos){
        return specialWeights[pos].getOrDefault(ch, 0.0);
    }

    static Set<Character>[] copyAlphabet(Set<Character>[] alphabet){
        Set<Character>[] tmp = new HashSet[5];
        for(int i = 0; i < 5; i++){
            tmp[i] = new HashSet<>(alphabet[i]);
        }
        return tmp;
    }

    public static void main(String[] args) throws IOException{
        System.out.println("Running");

        // Setup
        Set<String> words = new HashSet<>();
        Set<Character>[] alphabet = new HashSet[5];
        for(int i = 0; i < 5; i++){
            alphabet[i] = new HashSet<>();
        }
        initializeWeights();
        initializeSpecialWeights();

        System.out.println("Generating Alpha Library");

        //Generate needed characters in each position from wordle list
        try(BufferedReader reader = new BufferedReader(new FileReader("..\\Wordles.txt"))){
            String line;
            while((line = reader.readLine()) != null){
                words.add(line);
            }
        }

        for(String word : words){
            for(int i = 0; i < 5; i++){
                alphabet[i].add(word.charAt(i));
            }
        }

        //Append all other valid words to the wordle list
        try(BufferedReader reader = new BufferedReader(new FileReader("..\\Words.txt"))){
            String line;
            while((line = reader.readLine()) != null){
                words.add(line);
            }
        }

        System.out.println("Finding Combinations");

        int minNumWords = 42;
        List<String> minWords = new ArrayList<>();

        //The bigger the range, the better the result. Set at 300 for a good heuristic, >10000 is way too big
        for(int k = 0; k < 300; k++){
            List<String> found = new ArrayList<>();
            int size = 0;
            Set<Character>[] alphaLibrary = copyAlphabet(alphabet);
            Set<String> workingWords = new HashSet<>(words);

            // While not enough information found, keep adding words
            while(true){
                double maxInfo = 0.0;
                String bestWord = "";
                boolean beg = size <= 15; // 15 has given me the lowest guesses: 31
                Set<String> wordsToRemove = new HashSet<>();

                // Find the amount of information gained for each word and add the word that gives the most information
                for(String word : workingWords){
                    double temp = newAlphaLocations(alphaLibrary, word, beg, wordsToRemove);
                    if(temp > maxInfo){
                        maxInfo = temp;
                        bestWord = word;
                    }
                }

                found.add(bestWord);
                size++;
                updateAlphaLibrary(alphaLibrary, found.get(found.size() - 1));

                workingWords.removeAll(wordsToRemove);

                if(size >= minNumWords){
                    break;
                }else if(containAll(alphaLibrary)){
                    // If the new group of words is smaller than the last, save it for later
                    minNumWords = size;
                    minWords = new ArrayList<>(found);
                    System.out.println(size + " words were found that fulfill the criteria");
                    break;
                }
            }
        }

        System.out.println("Lowest amount of words that fulfilled criteria:");
        System.out.println(minWords);
        System.out.println(minNumWords);
    }
}
