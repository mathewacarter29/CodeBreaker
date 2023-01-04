package main;

public class Solver {
	
	public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	//Lookup table for the inverses in base 26
	private static final int[] INVERSES = {0, 1, 0, 9, 0, 21, 0, 15, 
			0, 3, 0, 19, 0, 0, 0, 7, 0, 23, 0, 11, 0, 5, 0, 17, 0, 25};
	private String code;
	
	/**
	 * Constructor for the Solver - initializes a "code" field variable representing 
	 * what needs to be decrypted
	 * @param code	Code to be decrypted
	 */
	public Solver(String code) {
		this.code = code;
	}
	
	/**
	 * Given a code, try every key for a shift cipher and output the results
	 */
	public void decryptShift() {
		for (int i = 1; i < 26; i++) {
			System.out.println("key: " + (26 - i));
			for (int j = 0; j < code.length(); j++) {
				String letter = code.substring(j, j + 1);
				if (letter.equals(" ")) {
					System.out.print(" ");
				}
				else {
					System.out.print(ALPHABET.charAt((ALPHABET.indexOf(letter) + i) % 26));
				}
			}
			System.out.println("\n");
		}
	}
	
	/**
	 * Decrypts a shift cipher with a given key	
	 * @param key	The number of places the plaintext was shifted to get the cipher text
	 * @return	The un-shifted plaintext
	 */
	public String decryptShift(int key) {
		String result = "";
		for (int i = 0; i < this.code.length(); i++) {
			int letterIndex = ALPHABET.indexOf(this.code.charAt(i));
			result += ALPHABET.charAt(Solver.toBase26(letterIndex - key));
			
		}
		return result;
	}
	
	/**
	 * Given a code and a key, this method will decrypt a substitution cipher
	 * @param key	The key for decrypting the code, represents a permutation of the alphabet
	 */
	public String decryptSub(String key) {
		String result = "";
		for (int i = 0; i < code.length(); i++) {
			char letter = code.charAt(i);
			if (key.indexOf(letter) < 0) {
				result += letter;
				//System.out.print(letter);
			}
			else {
				result += ALPHABET.charAt(key.indexOf(letter));
				//System.out.print(ALPHABET.charAt(key.indexOf(letter)));
			}
		}
		return result;
	}
	
	/**
	 * This method is for gradually solving a substitution cipher
	 * For example, code = VHWR, replaceString = _________________J____C___ ==> __CJ, where C 
	 * is in the W slot, and J is in the R slot
	 * @param replaceString		Will replace letter in code with corresponding letter in this param	
	 * 							Must be 26 characters long
	 */
	public void subHelper(String replaceString) {
		//Add help for codes with spaces and without them
		if (replaceString.length() != 26) {
			System.out.println("replaceString is wrong length - should be 26 but is " + replaceString.length());
		}
		for (int i = 0; i < this.code.length(); i++) {
			char letter = this.code.charAt(i);
			if (ALPHABET.indexOf(letter) < 0) {
				System.out.print(letter);
			}
			else {
				System.out.print(replaceString.charAt(ALPHABET.indexOf(letter)));
			}
		}
	}
	
	public String decryptAffine(int a, int b) {
		
		String result = "";
		for (int i = 0; i < this.code.length(); i++) {
			
			int letterIndex = ALPHABET.indexOf(this.code.charAt(i));
			result += ALPHABET.charAt(toBase26(findInverse(a) * (letterIndex - b)));
		}
		return result;
		
	}
	
	/**
	 * This method decrypts a Vigenere (block) cipher when given the key
	 * @param key	The key for decrypting the code
	 */
	public String decryptVigenere(String key) {
		String result = "";
		for (int i = 0; i < this.code.length(); i++) {
			char codeLetter = code.charAt(i);
			char keyLetter = key.charAt((i % key.length()));
			int codeIndex = ALPHABET.indexOf(codeLetter);
			int keyIndex = ALPHABET.indexOf(keyLetter);
			//System.out.print(ALPHABET.charAt((26 + codeIndex - keyIndex) % 26));
			result += ALPHABET.charAt((26 + codeIndex - keyIndex) % 26);
		}
		return result;
	}
	
	/**
	 * Decrypts a Hill Cipher using a given key	
	 * Pre: Code MUST be a multiple of the dimension of the key
	 * @param key	The invertible matrix representing the key
	 */
	public String decryptHill(SquareMatrix key) {
		String result = "";
		int n = key.getDimension();
		SquareMatrix inverse = key.findInverse();
		for (int i = 0; i < this.code.length(); i += n) {
			String comp = this.code.substring(i, i + n);
			for (int j = 0; j < n; j++) {
				int total = 0;
				int[] row = inverse.getCol(j);
				for (int k = 0; k < row.length; k++) {
					total += ALPHABET.indexOf(comp.charAt(k)) * row[k];
				}
				//System.out.print(ALPHABET.charAt(toBase26(total)));
				result += ALPHABET.charAt(toBase26(total));				
			}			
		}
		return result;
	}
	
	/**
	 * Decrypts a Permutation Cipher using a given key
	 * @param key	List of number corresponding to the permutation function
	 * Pre: key.length MUST be an even divisor of this.code.length
	 * 
	 * Example: key length = 6
	 * 			key = [3, 1, 6, 4, 5, 2]
	 * 			Break code into groups of 6 letters, place 3rd letter first, 1st letter second,
	 * 			6th letter third, etc.
	 * 
	 * To decrypt, break code into chunks of key.length, put letters back into their original
	 * positions, then print
	 */
	public String decryptPerm(int[] key) {
		String result = "";
		int[] inverse = new int[key.length];
		
		//Find the inverse permutation function
		for (int i = 0; i < key.length; i++) {
			inverse[key[i] - 1] = i + 1;			
		}
		for (int j = 0; j < this.code.length(); j += inverse.length) {
			String sub = this.code.substring(j, j + inverse.length);
			for (int k = 0; k < inverse.length; k++) {
				result += sub.charAt(inverse[k] - 1);
			}
		}
		
		return result;
	}
	
	/**
	 * Decrypt an Autokey cipher
	 * @param key	The key for decrypting the first letter of the ciphertext
	 */
	public String decryptAutokey(int key) {
		String result = "";
		int tempKey = key;
		for (int i = 0; i < this.code.length(); i++) {
			int indexOfChar = Solver.toBase26(ALPHABET.indexOf(this.code.charAt(i)) - tempKey);
			result += ALPHABET.charAt(indexOfChar);
			tempKey = indexOfChar;
		}
		
		return result;
	}
	
	/**
	 * Finds the inverse of a number in base 26 using the lookup table INVERSES
	 * @param num	The number you want to find the inverse of
	 * @return	The inverse of 'num'
	 */
	public static int findInverse(int num) {
		return INVERSES[num];
	}
	
	/**
	 * Takes in a number and makes it base 26
	 * @param num	The number becoming base 26
	 */
	public static int toBase26(int num) {
		num %= 26;
		if (num < 0) {
			num *= -1;
			num = 26 - num;
		}
		return num;
	}
}
