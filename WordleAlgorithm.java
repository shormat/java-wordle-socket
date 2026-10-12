import java.util.*;

public class WordleAlgorithm {
    
    public static String choose_word(){

        List<String> wordList = new ArrayList<>(WordleWordSet.WORD_SET);
    
        int randomIndex = (int) (Math.random() * wordList.size());
        
        return wordList.get(randomIndex);
    }

    public static Map<Character, Integer> count_occurences(String word)
    {

        Map<Character, Integer> letters = new HashMap<Character, Integer>();
        
        for(char letter : word.toCharArray())
        {
            letters.put(letter, letters.getOrDefault(letter, 0) + 1);
        }

        return letters;
    }

    public static boolean wordset_contains(String input)
    {
        return WordleWordSet.WORD_SET.contains(input.toLowerCase());
    }


    public static String verify_word(String input, String target_word)
    {
        input = input.toLowerCase();
        StringBuilder ret = new StringBuilder("BBBBB");

        Map<Character, Integer> letters_tmp = count_occurences(target_word);

        for(int i = 0; i < target_word.length(); i++)
        {
            char target_letter = target_word.charAt(i);
            char input_letter = input.charAt(i);

            if(target_letter == input_letter)
            {
                ret.setCharAt(i, 'G');
                letters_tmp.put(input_letter, letters_tmp.get(input_letter) - 1);
            }
        }

        for(int i = 0; i < target_word.length(); i++)
        {
            char target_letter = target_word.charAt(i);
            char input_letter = input.charAt(i);

            if(target_letter != input_letter)
            {
                if(letters_tmp.getOrDefault(input_letter, 0) > 0)
                {
                    ret.setCharAt(i, 'Y');
                    letters_tmp.put(input_letter, letters_tmp.get(input_letter) - 1);
                }
            }
        }

        return ret.toString();
    }
}
