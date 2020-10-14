
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Gustavo Jacob
 * Joseph Weber
 * 
 */

public class App {

	// Variavel utilizada para acessar metodos nao eos
	private static App app = new App();
	// Nome do arquivo de teste contendo os dados do labirinto
	private final String filename = "/home/gustavo_jacob/Documentos/pucrs/2020.2/IA/T1IA/teste.txt";

	// Matriz que serah inicializada com dados do <filename> no readFile()
	private Character[][] matrix;
	private int[] entrance = new int[2];
	private int[] exit = new int[2];

	public static void main(String args[]) {
		// app.readFile(new File(args[0]));
		app.readFile(new File(app.filename));
		// app.printMatrix(app.testingMatrix);
		app.printMatrix(app.matrix);

		Genetic algo = new Genetic(app.matrix, app.entrance, app.exit, (int) Math.pow(app.matrix.length, 2));
		// (int) Math.pow(app.matrix.length,2)/6
	}

	/**
	 * Metodo que le os dados de um arquivo e gera a matriz correspondente
	 * 
	 * @param file
	 */
	public void readFile(File file) {
		if (file.exists()) {
			System.out.println("Carregando arquivo " + file.getName() + "...\n");
		} else {
			System.out.println("ERRO: arquivo inexistente.\n");
			System.exit(1);
		}

		Scanner in;
		try {
			in = new Scanner(file);
			int matrixSize = Integer.parseInt(in.next());
			matrix = new Character[matrixSize][matrixSize];
			Character read;

			for (int i = 0; i < matrixSize; i++) {
				for (int j = 0; j < matrixSize; j++) {

					while (in.hasNext(" "))
						in.next();

					read = in.next().charAt(0);

					if (read == 'E') {
						entrance[0] = i;
						entrance[1] = j;
						System.out.println("Entrada " + entrance[0] + "," + entrance[1]);
					}

					else if (read == 'S') {
						exit[0] = i;
						exit[1] = j;
						System.out.println("Saida " + exit[0] + "," + exit[1]);
					}

					matrix[i][j] = read;

				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void printMatrix(Character[][] matrix) {

		System.out.println("Coordenada da entrada (x,y): (" + entrance[0] + "," + entrance[1] + ")");
		System.out.println("Coordenada da saida (x,y): (" + exit[0] + "," + exit[1] + ")\n");

		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				if (matrix[i][j] == '0')
					System.out.print("  ");
				else
					System.out.print(matrix[i][j] + " ");
			}
			System.out.println();
		}
	}
}
