package br.edu.utfpr;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {
    
    private static final String CIPHER_TEXT = "764aa26b55a4da654df6b19e4bce00f4ed05e09346fb0e762583cb7da2ac93a2";
    private static final String PLAN_TEXT = "This is a top secret.";
    private static final String IV_TEXT = "aabbccddeeff00998877665544332211";

    public static void main(String[] args) throws Exception {

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        final byte[] ivBytesArray = Hex.decodeHex(IV_TEXT.toCharArray());
        final byte[] cipherBytesArray = Hex.decodeHex(CIPHER_TEXT.toCharArray());

        final List<String> wordList = loadWordList();

        for (String word : wordList) {

            if (word.length() > 16){
                continue;
            }

            final String word16 = completeWord(word);
            final byte[] wordByteArray = word16.getBytes();
            final SecretKey secretKey = new SecretKeySpec(wordByteArray, 0, wordByteArray.length, "AES");

            cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(ivBytesArray));

            try {
                byte[] decryptResul = cipher.doFinal(cipherBytesArray);
                if (!PLAN_TEXT.equals(new String(decryptResul))){
                    continue;
                }

                System.out.println("found key -> " + word);
                break;
            }catch (BadPaddingException bpe){
                System.out.println("invalid key -> " + word);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private static String completeWord(final String word){

        String word16 = word;
        while (word16.length() != 16){
            word16 += "#";
        }

        return word16;
    }

    private static List<String> loadWordList(){

        try {
            final InputStream is = Main.class.getResourceAsStream("/word-list.txt");
            if (is == null){
                throw new RuntimeException("Arquivo não encontrado");
            }

            final String text = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            is.close();

            return List.of(text.split("\n"));
        }catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }
    }

}