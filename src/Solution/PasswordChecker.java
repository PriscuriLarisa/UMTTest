package Solution;

public class PasswordChecker {
    private static final int minimumLength = 6;
    private static final int maximumLength = 20;

    public int checkPassword(String s) {

        // we compute the number of actions needed to make sure that one digit,
        // one lowercase letter and one uppercase letter are present
        int upperLowerDigit = upperLowerDigit(s);

        // the number of insertions or deletes needed to make sure the
        // passwords has the right number of characters
        int insert = 0, delete = 0;

        // we will count the number of sequences made of repeating characters
        // k sequences of 3k consecutive characters
        int sequenceOfThreeK = 0;
        // k sequences of 3k+1 or 3k+2 consecutive characters
        int sequenceOfNotThreeK = 0;

        // keep track of the total number of sequences of 3k consecutive characters
        int totalNumberOfMultipleThreeK = 0;

        // calculate the number of insertions and deletes depending on the
        // initial length of the password and it's minimum and maximum length
        if (s.length() < minimumLength)
            insert = minimumLength - s.length();
        else if (s.length() > maximumLength)
            delete = s.length() - maximumLength;

        // we will calculate the number of sequences of consecutive characters
        // because we need different approaches in case of 3k and 3k+1/3k+2 characters
        // example: aaa -> aa, aaaa -> aaba (prioritize delete or replace)
        Character lastCharacter = s.toCharArray()[0];
        int sequenceLength = 1;

        // we parse the whole password
        for (int i = 1; i < s.length(); i++) {
            // calculate the length of the sequence because it is important
            // in case of more than two consecutive characters
            while (i < s.length() && lastCharacter.equals(s.toCharArray()[i])) {
                sequenceLength++;
                i++;
            }

            // we check if the sequence is a number divisible with 3
            // example : aaaaaa -> 2 sequences of 3 consecutive characters
            // we will retain the value 2, because we will need a minimum of
            // two changes: aaaaaa -> aabaab
            if (sequenceLength % 3 == 0) {
                sequenceOfThreeK += sequenceLength / 3;
                totalNumberOfMultipleThreeK++;
            } else if (sequenceLength != 1 && sequenceLength != 2) //only if the sequence is problematic, of length >2
                sequenceOfNotThreeK += sequenceLength / 3;

            // reset the character to be checked and the length of the sequence
            if (i < s.length())
                lastCharacter = s.toCharArray()[i];
            sequenceLength = 1;

        }

        // by inserting a character we can make sure to insert the needed one
        // between an uppercase letter, lowercase letter and digit
        // if the password needs any, so we only care about the
        // most influential value (the maximum between the two)
        // abc -> abcDE1 we needed 3 actions(inserts to make the password have the right length,
        // and we inserted the right characters).
        int insertOrULD = Math.max(upperLowerDigit, insert);

        // we prioritize deletion in sequences of 3k length, because
        // by deleting one character we can transform it into a
        // 3l+2 characters long sequence, which is less
        // expensive to deal with in terms of actions
        // example:
        // aaaaaa -(insert)-> aabaab needed 2 actions of replace
        // vs
        // aaaaaa -(delete)-> aaaaa -(insert)-> aabaa needed only one action of replace
        int sequenceOrDelete;
        if (totalNumberOfMultipleThreeK == 0)
            sequenceOrDelete = Math.max(sequenceOfThreeK, delete);
        else {
            sequenceOrDelete = delete;
            // there will be another number of sequence of 3k+2 after deleting those characters
            // example: aaaaaabccccc -> one 3k sequence and one 3k+2 sequence
            // -(delete)-> aaaaabccccc -> two 3k+2 sequences
            sequenceOfNotThreeK += sequenceOfThreeK - totalNumberOfMultipleThreeK;
        }

        // we can 'break' a sequence of 3k+2/3k+1 by inserting characters, and we
        // prioritize the insertion action on 3k+2/3k+1 instead of 3k
        // because we assigned the delete operation to be prioritized for the 3k long sequence
        int sequenceOrInsertOrULD = Math.max(sequenceOfNotThreeK, insertOrULD);

        // if there are no deletions needed (the password isn't longer than 20 characters)
        // we only care about the cases where we must replace characters
        if (delete == 0)
            return Math.max(sequenceOrInsertOrULD, sequenceOfThreeK);

        // we compute the sum of operations that can be solved by inserting and
        // the operations that can be solved by deleting characters
        // because these two are complementary
        return sequenceOrInsertOrULD + sequenceOrDelete;
    }

    // we parse the password to check if it misses any of the uppercase, lowercase of digit character
    // and we return the number of characters missing from the password
    private int upperLowerDigit(String s) {
        boolean hasUpper = false, hasLower = false, hasDigit = false;
        for (Character character : s.toCharArray()) {
            if (!hasUpper && Character.isUpperCase(character))
                hasUpper = true;
            if (!hasLower && Character.isLowerCase(character))
                hasLower = true;
            if (!hasDigit && Character.isDigit(character))
                hasDigit = true;
            if (hasDigit && hasUpper && hasLower)
                break;
        }

        return convertToInt(!hasUpper) + convertToInt(!hasLower) + convertToInt(!hasDigit);

    }

    private int convertToInt(Boolean b) {
        return b ? 1 : 0;
    }

}
