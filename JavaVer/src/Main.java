import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import static java.util.Map.entry;

public class Main {
    static Random rnd = new Random();

    static String[] alphabet;

    static Map<Character, Double> weights = Map.ofEntries(
            entry('a',1.105),
            entry('b',1.027),
            entry('c',1.036),
            entry('d',1.033),
            entry('e',1.1),
            entry('f',1.020),
            entry('g',1.06),
            entry('h',1.031),
            entry('i',1.061),
            entry('j',1.002),
            entry('k',1.021),
            entry('l',1.056),
            entry('m',1.031),
            entry('n',1.052),
            entry('o',1.066),
            entry('p',1.03),
            entry('q',1.002),
            entry('r',1.072),
            entry('s',1.075),
            entry('t',1.056),
            entry('u',1.044),
            entry('v',1.011),
            entry('w',1.016),
            entry('x',1.002),
            entry('y',1.038),
            entry('z',1.006)
    );

    static List<Map<Character,Double>> sweights = Arrays.asList(
            Map.of(
                    'v',0.00,
                    'q',0.02,
                    's',-0.02
            ),
            Map.of(
                    'q',0.00,
                    'v',0.02,
                    'g',0.02,
                    'j',0.02
            ),
            Map.of('k',0.00),
            Map.of('x',0.02),
            Map.of()
    );


    public static void generateAlphabetLibrary(ArrayList<String> words) throws IOException{
        System.out.println("Generating Alpha Library");
        //Generate needed characters in each positions from wordle list
        BufferedReader br = new BufferedReader(new FileReader("..\\Wordles.txt"));
        String line;
        while((line= br.readLine()) != null){
            words.add(line);
        }
        br.close();

        alphabet = new String[]{"","","","",""};
        for(String word : words){
            for(int i=0;i<5;i++){
                if(alphabet[i].indexOf(word.charAt(i)) == -1){
                    alphabet[i] += word.charAt(i);
                }
            }
        }

        //Sort each index for readability
        for(int i=0;i<5;i++){
            char[] tmp = alphabet[i].toCharArray();
            Arrays.sort(tmp);
            alphabet[i] = new String(tmp);
        }
        System.out.println(Arrays.toString(alphabet));
    }

    public static boolean containall(String[] givenarray,int size){
        boolean flag = true;
        for(int i=0; i<5;i++){
            String tempalpha = alphabet[i]; //mutability??
            for(int j=0; j<size;j++){
                tempalpha = tempalpha.replace(String.valueOf(givenarray[j].charAt(i)),"");
            }
            if (tempalpha.length()>0){
                flag = false;
                break;
            }
        }
        return flag;
    }

    public static String[] generateAlphaLibrary(String[] givenarray, int size){
        String[] arr = new String[5];
        for(int i=0;i<5;i++){
            String tempalpha = alphabet[i];
            for(int j=0;j<size;j++){
                tempalpha = tempalpha.replace(String.valueOf(givenarray[j].charAt(i)), "");
            }
            arr[i]=(tempalpha);
        }
        return arr;
    }

    public static double getSWeight(Character chara,int pos) {
        if (sweights.get(pos).containsKey(chara)) {
            return sweights.get(pos).get(chara);
        }else{
            return 0;
        }
    }

    public static double newAlphaLocations(String[] AlphabetLeft,String word,boolean beg) {
        double count = 0;
        for (int i=0;i<5;i++) {
            if(AlphabetLeft[i].contains(String.valueOf(word.charAt(i)))){
                if(beg){
                    count = count + rnd.nextDouble() * 0.01 + 1 / (weights.get(word.charAt(i)));
                }else{
                    count = count + 1 + getSWeight(word.charAt(i), i);
                }
            }
        }
        return count;
    }

    public static void main(String[] args) throws IOException{
        System.out.println("Running");

        double lastmax;
        double max = 0;
        ArrayList<String> words  = new ArrayList<>();

        //Adds Wordles.txt to words
        generateAlphabetLibrary(words);

        //Adds Words.txt to words
        BufferedReader br = new BufferedReader(new FileReader("..\\Words.txt"));
        String line;
        while((line= br.readLine()) != null){
            words.add(line);
        }
        br.close();


        int min = 999;
        String[] minarr = new String[]{};

        //the bigger the range the better the result, set at 50 for a good heuristic, >200000 is stupid
        for (int k=0;k<100;k++){
            String[] found = new String[40];
            int size = 0;

            //shuffle the words to change which word is picked when information gain is the same
            Collections.shuffle(words);

            //while not enough information found keep adding words
            while(true) {
                lastmax = max;
                max = 0;
                String x = "";
                String[] AlphaLibrary = generateAlphaLibrary(found, size);
                boolean beg = size <= 15; //15 has given me the lowest guesses:31

                //Find the amount of information gained for each word, add the word that gives most information
                for (String word : words) {
                    double temp = newAlphaLocations(AlphaLibrary, word, beg);
                    if (temp > max) {
                        max = temp;
                        x = word;
                    }
                    if (max == lastmax && !beg) {
                        break;
                    }
                }

                found[size] = x;
                size = size + 1;

                if (size >= min){
                    break;
                }else if (containall(found, size)) { //if new group of words is smaller than last then save for later
                    min = size;
                    minarr = found.clone();
                    System.out.println(size);
                    break;
                }
            }
        }
        System.out.println(Arrays.toString(minarr));
        System.out.println(min);

        //TODO
        //add score for keeping largest amount of words available if a tie in information /5
        //printing stuff
        //speed
    }



















    /* 31:
     * [buzzy, whump, howff, unfix, zimbi, cwtch, oxbow, pyxed, djinn, flows,
     *  vendu, schul, abysm, impro, yrapt, kacha, eggar, mvule, nertz, skegg,
     *  rusks, jelab, gadjo, lavvy, tsked, qapik, ataxy, ofays, upjet, adunc, squab]
     * [aas,ua,,,s]
     *
     *
     *
     *
     * */
}
