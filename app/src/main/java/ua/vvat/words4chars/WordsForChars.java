package ua.vvat.words4chars;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WordsForChars {
    private final Set<Character> letters = new HashSet<>();
    private final Map<Character, Character> identicals = new HashMap<>();
    private final List<String> words = new ArrayList<>(126000);

    public WordsForChars(AppCompatActivity activity) {
        try(InputStream is = activity.getResources().openRawResource(R.raw.letters)) {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String str;
            while ((str = br.readLine()) != null) {
                char[] charArray = str.toCharArray();
                for (char chr: charArray) {
                    letters.add(chr);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try(InputStream is = activity.getResources().openRawResource(R.raw.identicals)) {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String str;
            while ((str = br.readLine()) != null) {
                char[] charArray = str.toCharArray();
                identicals.put(charArray[0], charArray[1]);
                identicals.put(charArray[1], charArray[0]);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try(InputStream is = activity.getResources().openRawResource(R.raw.singular_and_plural)) {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String str;
            while ((str = br.readLine()) != null) {
                words.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public WordsCase getWordsCase(String chars) {
        return new WordsCase(chars);
    }

    public class WordsCase {
        private final List<String> found = new ArrayList<>();

        public WordsCase(String chars) {
            HashMap<Character, Integer> limitedLetters = new HashMap<>();

            {
                char[] charArray = chars.toCharArray();
                for (char chr : charArray) {
                    Integer num = limitedLetters.get(chr);
                    if (num == null) {
                        limitedLetters.put(chr, 1);
                    } else {
                        limitedLetters.put(chr, num + 1);
                    }
                }
            }

            words.forEach(word -> {
                char[] charArray = word.toCharArray();
                @SuppressWarnings("unchecked")
                HashMap<Character, Integer> limLat = (HashMap<Character, Integer>)limitedLetters.clone();
                for (char chr: charArray) {
                    if (!checkLetter(limLat, chr)) {
                        Character chr2 = identicals.get(chr);
                        if (chr2 == null || !checkLetter(limLat, chr2))
                            return;
                    }
                }
                found.add(word);
            });
        }

        public List<String> getFound() {
            return found;
        }

        public List<String> getFoundFiltered(String mask) {
            if (mask == null || mask.isEmpty())
                return found;

            List<String> foundFiltered = new ArrayList<>();
            int len = mask.length();
            char[] masks = mask.toCharArray();
            words: for (String word: found) {
                if (word.length() != len)
                    continue;
                char[] chars = word.toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    char msk = masks[i];
                    char chr = chars[i];
                    if (msk == '*' || isThisLetter(chr, msk))
                        continue;
                    continue words;
                }
                foundFiltered.add(word);
            }
            return foundFiltered;
        }
    }

    private boolean isThisLetter(char letter, char etalone) {
        if (letter == etalone)
            return true;
        Character another = identicals.get(letter);
        if (another != null)
            return another == etalone;
        return false;
    }

    private static boolean checkLetter(Map<Character, Integer> letters, char letter) {
        Integer num = letters.get(letter);
        if (num != null) {
            if (num > 1) {
                letters.put(letter, num - 1);
                return true;
            } else {
                letters.remove(letter);
                return true;
            }
        }
        return false;
    }
}
