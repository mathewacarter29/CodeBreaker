package main;


public class Driver {

	public static void main(String[] args) {
		
		String code = "AOOUSRBIVUSNOERUGBAIDFNVOIAOLKASDBIVUNERUVNAIERNUOERYSBGOUBROSDIRYVBPISERUBV";
		Solver solver = new Solver(code);
						//ABCDEFGHIJKLMNOPQRSTUVWXYZ
		String replace = "g-------------z-----------";
		solver.subHelper(replace);
	}
}
