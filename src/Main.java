import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

public class Main {
	static Random rng;
	
	private static char KOSONG = ' ';
	private static char TANAH = '.';
	private static char PINTU = '/';
	private static char PEMAIN = '0';
	private static char EXIT = 'X';
	
	private static int ROOM_SIZE = 10;
	private static int PATH_SIZE = 10;
	private static int countRoom = 0;
	private static boolean hasPlayer = false;
	private static boolean hasExit = false;
	
	public static void main(String[] args) throws IOException {
		
		//System.out.print("Rooms needed: ");
		Scanner sc = new Scanner(System.in);
		long seed = Math.abs(new Date().hashCode());
		while (true) {
			System.out.print("Ingin memasukkan seed? (y/n): ");
			String useseed = sc.nextLine();
			if (useseed.equalsIgnoreCase("y")) {
				System.out.print("Masukkan seed: ");
				try {
					seed = Long.parseLong(sc.nextLine());
				}
				catch (NumberFormatException e) {
					System.out.println("Seed tidak valid.\nProgram akan men-generate seed baru.");
					seed = Math.abs(new Date().hashCode());
				}
				break;
			} else if (useseed.equalsIgnoreCase("n")) {
				System.out.println();
				seed = Math.abs(new Date().hashCode());
				break;
			} else {
				System.out.println("Input tidak valid!\n");
			}
		}
		
		System.out.println("===============");
		rng = new Random();
		rng.setSeed(seed);
		System.out.println("Seed: " + seed);
		System.out.println("===============");
		System.out.println("Generating...\n");
		
		int size = 100;
		
		while (!hasPlayer || !hasExit) {
			countRoom = 0;
			hasPlayer = false;
			hasExit = false;
			char[][] world = new char[size][size];
			for (int i = 0; i < world.length; i++) {
				for (int j = 0; j < world[i].length; j++) {
					world[j][i] = KOSONG;
				}
			}
			
			int firstWidth = rng.nextInt(5) + 5;
			int firstHeight = rng.nextInt(5) + 5;
			int firstX = rng.nextInt(size - 20) + 10;
			int firstY = rng.nextInt(size - 20) + 10;
			
			makeRoom(firstX, firstY, firstHeight, firstWidth, world);
			generate(firstX, firstY, firstHeight, firstWidth, world, rng);
			print(world);
		}
		System.out.println("Dungeon with " + countRoom + " rooms successfully generated!");
		System.out.println("Berhasil membuat dungeon dengan " + countRoom + " ruangan!");
		
	}
	
	public static boolean makeRoom(int x, int y, int height, int width, char[][] world) {
		for (int i = x; i < x + height; i++) {
			for (int j = y; j < y + width; j++) {
				if (i > 0 && j > 0 && i < world.length && j < world[i].length && world[i][j] == KOSONG && world[clamp(i-1, 0, i-1)][j] == KOSONG && world[clamp(i+1, i+1, world.length-1)][j] == KOSONG && world[i][clamp(j-1, 0, j-1)] == KOSONG && world[i][clamp(j+1, j+1, world[i].length-1)] == KOSONG) continue;
				else return false;
			}
		}
		
		for (int i = x; i < x + height; i++) {
			for (int j = y; j < y + width; j++) {
				world[i][j] = TANAH;
			}
		}
		
		double odd = rng.nextDouble();
		if (odd < 0.5 && rng.nextDouble() < 0.5 && !hasPlayer) {
			world[rng.nextInt(height)+x][rng.nextInt(width)+y] = PEMAIN;
			hasPlayer = true;
		}
		else if (odd > 0.5 && rng.nextDouble() < 0.5 && !hasExit) {
			world[rng.nextInt(height)+x][rng.nextInt(width)+y] = EXIT;
			hasExit = true;
		}
		countRoom++;
		return true;
	}
	
	public static void generate(int x, int y, int height, int width, char[][] world, Random rng) {
		//UP
		int n = rng.nextInt(width) + y;
		int amount = rng.nextInt(PATH_SIZE)+3;
		if (x-2 >= PATH_SIZE) {
			boolean okay = true;
			for (int i = x-2; i > x-amount; i--) {
				if (i >= 0 && world[i][n] == KOSONG)
					continue;
				else
					okay = false;
			}
			
			int newHeight = rng.nextInt(ROOM_SIZE) + ROOM_SIZE;
			int newWidth = rng.nextInt(ROOM_SIZE) + ROOM_SIZE;
			int newX = clamp(x-amount-newHeight, 0, x-amount-newHeight);
			int newY = rng.nextInt(newWidth) + n - newWidth + 1;
			//System.out.println(x + "," + y + " >> " + newX + "," + newY + ": " + newHeight + "x" + newWidth);
			if (okay && makeRoom(newX, newY, newHeight, newWidth, world)) {
				world[x-1][n] = PINTU;
				for (int i = x-2; i > x-amount; i--) {
					world[i][n] = TANAH;
				}
				world[x-amount][n] = PINTU;
				generate(newX, newY, newHeight, newWidth, world, rng);
			}
		}
		
		//DOWN
		n = rng.nextInt(width) + y;
		amount = rng.nextInt(PATH_SIZE)+3;
		if (x+height+1 <= world.length-PATH_SIZE) {
			boolean okay = true;
			for (int i = x+height+1; i < x+amount; i++) {
				if (i < world.length && world[i][n] == KOSONG)
					continue;
				else
					okay = false;
			}
			
			int newHeight = rng.nextInt(ROOM_SIZE) + ROOM_SIZE;
			int newWidth = rng.nextInt(ROOM_SIZE) + ROOM_SIZE;
			int newX = clamp(x+amount+height+1, x+amount+height+1, world.length-1);
			int newY = rng.nextInt(newWidth) + n - newWidth + 1;
			//System.out.println(x + "," + y + " >> " + newX + "," + newY + ": " + newHeight + "x" + newWidth);
			if (okay && makeRoom(newX, newY, newHeight, newWidth, world)) {
				world[x+height][n] = PINTU;
				for (int i = x+height+1; i < x+height+amount; i++) {
					world[i][n] = TANAH;
				}
				world[x+height+amount][n] = PINTU;
				generate(newX, newY, newHeight, newWidth, world, rng);
			}
		}
			
		//LEFT
		n = rng.nextInt(height) + x;
		amount = rng.nextInt(PATH_SIZE)+3;
		if (y-2 >= PATH_SIZE) {
			boolean okay = true;
			for (int i = y-2; i > y-amount; i--) {
				if (i >= 0 && world[n][i] == KOSONG)
					continue;
				else
					okay = false;
			}
			
			int newHeight = rng.nextInt(ROOM_SIZE) + ROOM_SIZE;
			int newWidth = rng.nextInt(ROOM_SIZE) + ROOM_SIZE;
			int newY = clamp(y-amount-newWidth, 0, y-amount-newWidth);
			int newX = rng.nextInt(newHeight) + n - newHeight + 1;
			//System.out.println(x + "," + y + " >> " + newX + "," + newY + ": " + newHeight + "x" + newWidth);
			if (okay && makeRoom(newX, newY, newHeight, newWidth, world)) {
				world[n][y-1] = PINTU;
				for (int i = y-2; i > y-amount; i--) {
					world[n][i] = TANAH;
				}
				world[n][y-amount] = PINTU;
				generate(newX, newY, newHeight, newWidth, world, rng);
			}
		}

		//RIGHT
		n = rng.nextInt(height) + x;
		amount = rng.nextInt(PATH_SIZE)+3;
		if (y+width+1 <= world.length-PATH_SIZE) {
			boolean okay = true;
			for (int i = y+width+1; i < y+amount; i++) {
				if (i < world[n].length && world[n][i] == KOSONG)
					continue;
				else
					okay = false;
			}
			
			int newHeight = rng.nextInt(ROOM_SIZE) + ROOM_SIZE;
			int newWidth = rng.nextInt(ROOM_SIZE) + ROOM_SIZE;
			int newY = clamp(y+amount+width+1, y+amount+width+1, world[x].length-1);
			int newX = rng.nextInt(newHeight) + n - newHeight + 1;
			//System.out.println(x + "," + y + " >> " + newX + "," + newY + ": " + newHeight + "x" + newWidth);
			if (okay && makeRoom(newX, newY, newHeight, newWidth, world)) {
				world[n][y+width] = PINTU;
				for (int i = y+width+1; i < y+width+amount; i++) {
					world[n][i] = TANAH;
				}
				world[n][y+width+amount] = PINTU;
				generate(newX, newY, newHeight, newWidth, world, rng);
			}
		}
	}
	
	public static int clamp(int val, int min, int max) {
		if (val < min) return min;
		if (val > max) return max;
		return val;
	}
	
	public static void print(char[][] world) throws IOException {
		PrintStream printer = new PrintStream(new File("dungeon.txt"));
		printer.println(TANAH + " = Jalan");
		printer.println(PINTU + " = Pintu");
		printer.println(PEMAIN + " = Pemain");
		printer.println(EXIT + " = Pintu Keluar");
		for (char[] w : world) {
			for (char c : w) {
				printer.print(c);
			}
			printer.println();
		}
		printer.close();
	}
}
