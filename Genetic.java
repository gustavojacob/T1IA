/**
 * Gustavo Jacob
 * Joseph Weber
 * 
 */


import java.util.Random;

public class Genetic {

	private App app = new App();
	private Character[][] matrix;
	private int[] pos0; // posição da entrada do labirinto. Par (x,y)
	private int[] exit; // posição da saída do labirinto. Par (x,y)
	private int maxSteps; // quantidade máxima de passos permitidos por tentativa de sair do labirinto;
	private final int SIZE = 10; // tamanho da populaçao: quantidade de soluçoes
	private final int MAX = 1500000; // numero maximo de geraçoes (iteraçoes)
	private int[][] population;
	private int[][] intermediatePopulation;
	private boolean running;
	private Random r;

	public Genetic(Character[][] matrix, int[] pos0, int[] exit, int maxSteps) {
		this.matrix = matrix;
		this.pos0 = pos0;
		this.exit = exit;
		this.maxSteps = maxSteps;
		r = new Random();
		population = new int[SIZE][maxSteps + 1]; // populaçao atual: contem os cromossomos (soluçoes candidatas)
		intermediatePopulation = new int[SIZE][maxSteps + 1]; // populaçao intermediaria: corresponde a populaçao em
																// construçao
																// Obs: A ultima coluna de cada linha da matriz e para
																// armazenar o valor da funçao de aptidao,
																// que indica o quao boa eh a soluçao. Quanto mais alto
																// melhor.
		run();
	}

	private void run() {
		// ===========> Ciclo do AG
		System.out.println("=================================================================");
		System.out.println("Trying to find a way out of this ...");
		System.out.println("=================================================================");
		initPopulation(); // cria soluçoes aleatoriamente

		running = true;

		for (int generation = 0; generation <= MAX; generation++) {
			System.out.println("Generation:" + generation);
			calculateAptitudes();
			if (!running)
				break;
			else {
				int best = takeHighLander(); // highlander, Vulgo elitismo
				printPopulation();
				crossOver();
				if (generation % 2 == 0)
					mutation();
				population = intermediatePopulation;
			}
		}

		if (running) {
			System.out.println("=================================================================");
			System.out.println("I could not get out (evolution sucks big time). R.I.P.");
			System.out.println("=================================================================");
		}
	}

	/**
	 * Gera populacao inicial: conjunto de solucoes candidatas
	 */
	private void initPopulation() {
		System.out.println("Gerando soluções aleatórias...");

		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < maxSteps; j++) {
				population[i][j] = r.nextInt(4);
			}
		}
	}

	/**
	 * Selecao por elitismo. Encontra a melhor solucao e copia para a populaçao
	 * intermediaria
	 */
	private int takeHighLander() {
		int highlander = 0;
		int higher = population[0][maxSteps];

		for (int i = 1; i < SIZE; i++) {
			if (population[i][maxSteps] > higher) {
				higher = population[i][maxSteps];
				highlander = i;
			}
		}
		System.out.println("Selecao por elitismo - melhor dessa geraçao: " + highlander);

		for (int i = 0; i <= maxSteps; i++) {
			intermediatePopulation[0][i] = population[highlander][i];
		}
		return highlander;
	}

	private void mutation() {

		for (int counter = 0; counter <= SIZE / 3; counter++) {
			int row = r.nextInt(SIZE);
			int column = r.nextInt(maxSteps);
			int aux;
			do {
				aux = r.nextInt(4);
			} while (intermediatePopulation[row][column] == aux);

			System.out.println("Mutou o cromossomo " + column + " do candidato " + row);
			System.out.print("De ");
			printDirection(intermediatePopulation[row][column]);
			System.out.print("para ");
			intermediatePopulation[row][column] = aux;
			printDirection(intermediatePopulation[row][column]);
			System.out.println();
		}
	}

	private void crossOver() {

		int[] father;
		int[] mother;
		int cut = maxSteps / 2;

		for (int i = 1; i < SIZE - 1; i = i + 2) {
			do {
				father = tournament();
				mother = tournament();
			} while (father == mother);

			System.out.println("Fazendo cross-over");

			for (int j = 0; j < cut; j++) {
				intermediatePopulation[i][j] = father[j];
				intermediatePopulation[i + 1][j] = mother[j];

			}
			for (int j = cut; j < maxSteps; j++) {
				intermediatePopulation[i][j] = mother[j];
				intermediatePopulation[i + 1][j] = father[j];
			}
		}
	}

	/**
	 * Selecao por torneio. Escolhe cromossomo (solucao) para cruzamento
	 */
	private int[] tournament() {
		return population[r.nextInt(population.length)];
	}

	/**
	 * Calcula a funcao de aptidao para a populaçao atual
	 */
	private void calculateAptitudes() {

		for (int i = 0; i < population.length; i++) {
			System.out.println("=================================================================");
			System.out.println("Candidato " + i + " caminhando");
			population[i][maxSteps] = 0;
			aptitude(i, pos0, population[i], 0);
		}
	}

	private int[] aptitude(int n, int[] pos, int[] candidate, int index) {

		if (index < candidate.length - 1) {
			int[] newPos = move(pos, candidate[index]);

			if (newPos[0] != pos[0] || newPos[1] != pos[1]) {
				checkExit(candidate, newPos, index);
				candidate[candidate.length - 1] = candidate[candidate.length - 1] + 1;
			}
			index++;
			aptitude(n, newPos, candidate, index);
		} else {
			//System.out.println("Candidato " + n + " parou na posicao " + pos[0] + "," + pos[1]);
			//System.out.println("=================================================================");
		}
		return pos;
	}

	public int[] move(int posicao[], int direcao) {
		int[] newPos = posicao.clone();

		switch (direcao) {
		case 0: // Mover para cima
			if (posicao[0] - 1 < 0 || matrix[posicao[0] - 1][posicao[1]] == '1')
				break;
			else
				newPos[0] = posicao[0] - 1;
			break;
		case 1: // Mover para baixo
			if (posicao[0] + 1 >= matrix[1].length || matrix[posicao[0] + 1][posicao[1]] == '1')
				break;
			else
				newPos[0] = posicao[0] + 1;
			break;
		case 2: // Mover para a direita
			if (posicao[1] + 1 >= matrix[0].length || matrix[posicao[0]][posicao[1] + 1] == '1')
				break;
			else
				newPos[1] = posicao[1] + 1;
			break;
		case 3: // Mover para a esquerda
			if (posicao[1] - 1 < 0 || matrix[posicao[0]][posicao[1] - 1] == '1')
				break;
			else
				newPos[1] = posicao[1] - 1;
			break;
		}
		return newPos;
	}

	private void checkExit(int[] candidate, int[] pos, int index) {
		// System.out.println(exit[0]+ " " + exit[1]);
		if (pos[0] == exit[0] && pos[1] == exit[1]) {
			endSim(candidate, index);
			}
	}

	private void endSim(int[] candidate, int index) {
		System.out.println("=================================================================");
		System.out.println("Finally, I got out!");
		System.out.println("My path was");
		for (int direction = 0; direction < index + 1; direction++)
			printDirection(candidate[direction]);
		System.out.println("=================================================================");

		running = false;

	}

	/**
	 * Printa populaçao na tela
	 */
	private void printPopulation() {
//		System.out.println("__________________________________________________________________");
//		for (int i = 0; i < population.length; i++) {
//			System.out.print("(" + i + ") ");
//			for (int j = 0; j < population[i].length - 1; j++) {
//				printDirection(population[i][j]);
//			}
//			System.out.println(" Aptidao: " + population[i][population[i].length - 1]);
//		}
//		System.out.println("__________________________________________________________________");
	}

	private void printDirection(int i) {

		Character direction = null;

		switch (i) {
		case 0:
			direction = 'C';
			break;
		case 1:
			direction = 'B';
			break;
		case 2:
			direction = 'D';
			break;
		case 3:
			direction = 'E';
			break;
		}

		System.out.print(direction + " ");
	}

	public void printMatrix(Character[][] matrix) {

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
