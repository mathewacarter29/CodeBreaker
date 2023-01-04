package main;

public class SquareMatrix {

	private int[] values;
	private int sqrt;
	
	/**
	 * Initializes a new square matrix of base 26
	 * @param values	1D array of values to represent the matrix
	 * 			(We will use modular arithmetic to pretend its 2D)
	 */
	public SquareMatrix(int[] values) {
		double sqrt = Math.sqrt(values.length);
		if (Math.floor(sqrt) - sqrt != 0) {
			throw new Error("This matrix is not a square matrix");
		}
		this.values = values;
		this.sqrt = (int)Math.sqrt(values.length);
	}
	
	/**
	 * Finds the inverse of this matrix
	 * @return	The inverse of this matrix in base 26
	 */
	public SquareMatrix findInverse() {
		//Get the multiplicative inverse of the determinant in base 26
		int detInverse = Solver.findInverse(Solver.toBase26(this.findDeterminant()));
		if (detInverse == 0) {
			throw new Error("This matrix does not have a valid inverse: determinant is " + this.findDeterminant());
		}
		if (this.values.length == 4) {
			int a = values[0] * detInverse;
			int b = values[1] * detInverse;
			int c = values[2] * detInverse;
			int d = values[3] * detInverse;
			
			return new SquareMatrix(new int[] {Solver.toBase26(d), Solver.toBase26(-b),
					Solver.toBase26(-c), Solver.toBase26(a)});
		}
		int[] transposed = new int[values.length];
		
		//1. Transpose the matrix by flipping all values across the main diagonal
		for (int i = 0; i < transposed.length; i++) {
			int row = i / sqrt;
			int col = i % sqrt;
			//i = row * this.n + col
			transposed[i] = this.values[col * sqrt + row];
		}
		
		//2. Replace every value with the determinant of that element's minor matrix (K*)
		int[] result = new int[transposed.length];
		for (int i = 0; i < transposed.length; i++) {
			int[] newArr = new int[(int)Math.pow(sqrt - 1, 2)];
			int index = 0;
			for (int j = 0; j < transposed.length; j++) {
				if ((j / sqrt) != (i / sqrt) && (j % sqrt) != (i % sqrt)) {
					newArr[index] = transposed[j];
					index++;
				}
			}
			//3. Multiply every element of K* with the inverse of the original matrix's determinant
			result[i] = (int)Math.pow(-1, i) * detInverse * this.determinantHelper(newArr);
			result[i] = Solver.toBase26(result[i]);
		}
		
		return new SquareMatrix(result);
	}
	
	/**
	 * This method will return the determinant of this matrix
	 * @return	The determinant of the matrix
	 */
	public int findDeterminant() {
		return determinantHelper(this.values);
	}
	
	/**
	 * Recursive function to find the determinant of any given matrix
	 * @param arr	The matrix we are determining the determinant of
	 * @return	The determinant of the matrix 'arr'
	 */
	private int determinantHelper(int[] arr) {
		if (arr.length == 4) {
			int total = arr[0] * arr[3] - arr[1] * arr[2];
			return Solver.toBase26(total);
		}
		else {
			int n = (int)Math.sqrt(arr.length);
			int total = 0;
			for (int i = 0; i < n; i++) {
				int[] newArr = new int[(int)Math.pow(n - 1, 2)];
				int index = 0;
				for (int j = 0; j < arr.length; j++) {
					if ((j / n) != (i / n) && (j % n) != (i % n)) {
						newArr[index] = arr[j];
						index++;
					}
				}
				total += Math.pow(-1, i) * arr[i] * determinantHelper(newArr);
			}
			return total;
		}
	}
	
	
	/**
	 * Getter for the dimension of the matrix
	 */
	public int getDimension() {
		return this.sqrt;
	}
	
	/**
	 * Getter for a certain column in the matrix
	 * @param row	The row number you are retrieving
	 */
	public int[] getCol(int col) {
		int[] result = new int[this.sqrt];
		for (int i = col, index = 0; index < result.length; i+=this.sqrt, index++) {
			result[index] = values[i];
		}
		return result;
	}
	
	/**
	 * Returns a string representation of the matrix
	 */
	public String toString() {
		String result = "";
		for (int i = 0; i < this.values.length; i++) {
			
			result += values[i];
			if (i % this.sqrt == this.sqrt - 1) {
				result += "\n";
			}
			else {
				result += "\t";
			}
		}
		return result;
	}
}
