package game;

import java.io.IOException;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.TextColor.Indexed;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.TerminalFactory;
import java.util.LinkedList;
import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;

public class Game {

	public enum Richtung {
		Rechts, Links, Unten, Oben
	}

	public enum Gamemodus {
		Leicht, Mittel, Schwer
	}

	public static Gamemodus schwierigkeit;

	public static int spielfeldHoehe = 50;
	public static int spielfeldBreite = 100;
	public static Pixel[][] spielfeld;
	public static TextColor DefaultBackColor = TextColor.ANSI.BLACK;
	public static TextColor DefaultTextColor = TextColor.ANSI.WHITE;
	public static String score;
	public static String hscore;
	public static int score_count;
	public static int hscore_count;
	public static double geschwindigkeit;
	public static int high_score;
	public static String high_score_name;
	public static int score_zahl;
	public static String txt_input;

	public static void main(String[] args) throws IOException {

		// Spielfeld mit "Pixeln" wird angelegt
		// jedes Pixel kann einen char, eine Hintergrundfarbe und eine Textfarbe haben
		spielfeld = new Pixel[spielfeldBreite][spielfeldHoehe];
		
		for (int i = 0; i < spielfeld.length; i++) {
			
			for (int j = 0; j < spielfeld[i].length; j++) {
				
				spielfeld[i][j] = new Pixel();
			}
		}

		// Fenster (Terminal) erstellen und anzeigen
		TerminalFactory factory = new DefaultTerminalFactory()
				.setInitialTerminalSize(new TerminalSize(spielfeldBreite, spielfeldHoehe));
		Terminal terminal = factory.createTerminal();
		terminal.setCursorVisible(false);

		// "Application-loop" - kehrt immer wieder zum Startbildschirm zurück
		// wird beim Startbildschirm ESCAPE gedrückt wird die Anwendung beendet
		while (true) {
			// zeigt simple Startseite an, die mit "Enter" oder "Escape" verlassen wird
			showStartseite(terminal);
			// "Game-loop" wird hier ausgeführt

			runGame(geschwindigkeit, terminal);

			ClearSpielfeld();
			WriteSpielfeld(terminal);

			if (score_zahl > high_score) {
				
				newHighScore(terminal);
			}

			// GAME OVER hier hinzufügen
			showGameOver(terminal);
		}
	}

	private static void runGame(double geschwindigkeit, Terminal terminal) throws IOException {

		// initiale Spieleinstellungen
		Richtung richtung = Richtung.Rechts;
		
		int posX = spielfeldBreite / 2;
		int posY = spielfeldHoehe / 2;

		SnakeBody s1 = new SnakeBody(posX, posY, Richtung.Rechts);
		SnakeBody s2 = new SnakeBody(posX - 2, posY, Richtung.Rechts);
		SnakeBody s3 = new SnakeBody(posX - 4, posY, Richtung.Rechts);
		SnakeBody s4 = new SnakeBody(posX - 6, posY, Richtung.Rechts);
		SnakeBody s5 = new SnakeBody(posX - 8, posY, Richtung.Rechts);
		SnakeBody pseudo = new SnakeBody(posX - 6, posY, Richtung.Rechts);

		LinkedList<SnakeBody> snake = new LinkedList<SnakeBody>();
		ArrayList<Richtung> vergleichRichtung = new ArrayList<Richtung>();

		snake.add(s1);
		snake.add(s2);
		snake.add(s3);
		snake.add(s4);
		snake.add(s5);
		snake.add(pseudo);

		Apple apl = new Apple(Apple.generateKoordinate(spielfeldBreite), Apple.generateKoordinate(spielfeldHoehe));

		score_zahl = 0;

		// Importiere highscore aus highscore.txt
		File file = new File("src//game//highscore");
		Scanner sc = new Scanner(file);

		try {
			
			txt_input = sc.nextLine();
		
		} catch (java.util.NoSuchElementException use) {
		
			txt_input = "NoName0";
		}

		high_score = Integer.parseInt(txt_input.replaceAll("[\\D]", ""));
		high_score_name = txt_input.replaceAll("[^A-Za-z ]", "");

		// Startfarbe
		int r, g, b;
		// Rahmenfarben zuweisen je nach Gamemode
		if (schwierigkeit == Gamemodus.Schwer) {
			
			r = 150;
			g = 0;
			b = 0;
		} else if (schwierigkeit == Gamemodus.Mittel) {
			
			r = 220;
			g = 140;
			b = 10;
		} else {
			
			r = 60;
			g = 120;
			b = 10;
		}

		// Spiel in "Dauerschleife" (game loop)
		while (true) {

			ClearSpielfeld();

			// Hintergrundfarbe mit RGB (ACHTUNG 6x6x6 Color Cube)
			// siehe TextColor Klasse in Lanterna

			// obere Zeile einfärben
			for (int i = 0; i < spielfeldBreite; i++) {
				
				spielfeld[i][0].backColor = Indexed.fromRGB(r, g, b);
			}

			// untere Zeile einfärben
			for (int i = 0; i < spielfeldBreite; i++) {
				
				spielfeld[i][spielfeldHoehe - 1].backColor = Indexed.fromRGB(r, g, b);
			}

			// linke Spalte einfärben
			for (int i = 0; i < spielfeldHoehe; i++) {
				
				spielfeld[0][i].backColor = Indexed.fromRGB(r, g, b);
				spielfeld[1][i].backColor = Indexed.fromRGB(r, g, b);
			}

			// rechte Spalte einfärben
			for (int i = 0; i < spielfeldHoehe; i++) {
				
				spielfeld[spielfeldBreite - 2][i].backColor = Indexed.fromRGB(r, g, b);
				spielfeld[spielfeldBreite - 1][i].backColor = Indexed.fromRGB(r, g, b);
			}

			// Tastatureinggabe wird gelesen
			// KeyStroke eingabe = terminal.readInput(); // stopped und wartet auf Eingabe
			KeyStroke eingabe = terminal.pollInput(); // läuft weiter, auch wenn keine Eingabe erfolgt ist

			// Apfel fressen

			if (eingabe != null) {

				if (eingabe.getKeyType().equals(KeyType.ArrowLeft)) {
					
					if (snake.get(0).getPos() != Richtung.Rechts) {
						
						snake.get(0).setPos(Richtung.Links);
					}
				}

				if (eingabe.getKeyType().equals(KeyType.ArrowRight)) {
					
					if (snake.get(0).getPos() != Richtung.Links) {
						
						snake.get(0).setPos(Richtung.Rechts);
					}
				}

				if (eingabe.getKeyType().equals(KeyType.ArrowUp)) {
					
					if (snake.get(0).getPos() != Richtung.Unten) {
						
						snake.get(0).setPos(Richtung.Oben);
					}
				}

				if (eingabe.getKeyType().equals(KeyType.ArrowDown)) {
					
					if (snake.get(0).getPos() != Richtung.Oben) {
						
						snake.get(0).setPos(Richtung.Unten);
					}
				}

				if (eingabe.getKeyType().equals(KeyType.Escape)) {
					
					ClearSpielfeld();
					WriteSpielfeld(terminal);
					
					break;
				}
			}

			// Bewegung der Snake-Elemente
			// Wenn Kopf nicht kollidiert, bewege
			if (SnakeBody.checkKollision(snake.get(0), snake) == false) {
				
				for (int i = 0; i < snake.size(); i++) {
					
					vergleichRichtung.add(snake.get(i).getPos());
					snake.get(i).setAll(SnakeBody.bewegung(snake.get(i), spielfeldBreite, spielfeldHoehe));
					
					if (i > 0) {
					
						snake.get(i).setPos(SnakeBody.checkRichtung(snake.get(i), vergleichRichtung.get(i - 1)));
					}
					// System.out.println("Element " + i + ": Richtung: " + snake.get(i).getPos() +
					// " ; x: " + snake.get(i).getX() + " ; y: " + snake.get(i).getY());
				}
				// System.out.println();
				vergleichRichtung.clear();
			} else {
				break;
			}

			score = "SCORE: " + score_zahl;
			score_count = 0;
			// Print Scoreboard
			
			for (char c : score.toCharArray()) {
			
				spielfeld[score_count][0].Text = c;
				score_count++;
			}
			// Print highscore
			hscore_count = spielfeldBreite / 2;
			hscore = "HIGH-SCORE: " + high_score + "    by: " + high_score_name;
			
			for (char c : hscore.toCharArray()) {
				
				spielfeld[hscore_count][0].Text = c;
				hscore_count++;
			}

			// apfel fressen (neue Koordinaten/ +SnakeBody/ +Score)
			if (snake.get(0).getX() == apl.getX() && snake.get(0).getY() == apl.getY()) {
				// Rdm. generate Apfel
				apl.setNew(Apple.generateKoordinate(spielfeldBreite), Apple.generateKoordinate(spielfeldHoehe));

				// neues Snake-Objekt mit Kopf/Apfel Koordinate
				SnakeBody sNew = new SnakeBody(snake.get(0).getX(), snake.get(0).getY(), snake.get(0).getPos());

				// Füge Objekt in snake nachdem einmal bewegt!
				snake.addFirst(new SnakeBody(SnakeBody.bewegung(sNew, spielfeldBreite, spielfeldHoehe)));

				// Scoreboard Zähler
				score_zahl++;
			}

			// Apfel setzen
			spielfeld[apl.getX()][apl.getY()].Text = '☯';

			// Snake drucken
			for (int i = 0; i < snake.size() - 1; i++) {
				
				spielfeld[snake.get(i).getX()][snake.get(i).getY()].Text = '☭';
			}
			// System.out.println(snake.get(0).getX() + " " + snake.get(0).getY());

			try {
				// zeichnet das gesamte Spielfeld auf einmal
				WriteSpielfeld(terminal);
				// kurzer "Schlaf", es kann hier mit der Verzoegerung die
				// Spielgeschwindigkeit eingestellt werden
				Thread.sleep((int) geschwindigkeit);
			
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static void showStartseite(Terminal terminal) throws IOException {

		terminal.clearScreen();

		// Startseite mit Text
		// der Text wird hier direkt in das Terminal geschrieben und ncht in das
		// Spielfeld
		terminal.setCursorPosition(6, 6);
		Write("██████████╗   ███╗   ██╗   █████████╗   ██╗   ██╗   █████████╗", terminal);
		terminal.setCursorPosition(6, 7);
		Write("██╔═══════╝   ████╗  ██║   ██╔════██║   ██║ ██╔═╝   ██╔══════╝", terminal);
		terminal.setCursorPosition(6, 8);
		Write("██████████╗   ██╔██╗ ██║   █████████║   █████╔╝     ███████╗ ", terminal);
		terminal.setCursorPosition(6, 9);
		Write("╚═══════██║   ██║╚██╗██║   ██╔════██║   ██╔═██╗     ██╔════╝ ", terminal);
		terminal.setCursorPosition(6, 10);
		Write("██████████║   ██║ ╚████║   ██║    ██║   ██║  ██╗    █████████╗", terminal);
		terminal.setCursorPosition(6, 11);
		Write("╚═════════╝   ╚═╝  ╚═══╝   ╚═╝    ╚═╝   ╚═╝  ╚═╝    ╚════════╝", terminal);

		terminal.setCursorPosition(10, 12);
		Write(" ██████╗  ███╗   ██╗     ███████╗████████╗███████╗██████╗  ██████╗ ██╗██████╗ ███████╗", terminal);
		terminal.setCursorPosition(10, 13);
		Write("██╔═══██╗ ████╗  ██║     ██╔════╝╚══██╔══╝██╔════╝██╔══██╗██╔═══██╗██║██╔══██╗██╔════╝", terminal);
		terminal.setCursorPosition(10, 14);
		Write("██║   ██║ ██╔██╗ ██║     ███████╗   ██║   █████╗  ██████╔╝██║   ██║██║██║  ██║███████╗", terminal);
		terminal.setCursorPosition(10, 15);
		Write("██║   ██║ ██║╚██╗██║     ╚════██║   ██║   ██╔══╝  ██╔══██╗██║   ██║██║██║  ██║╚════██║", terminal);
		terminal.setCursorPosition(10, 16);
		Write("╚██████╔╝ ██║ ╚████║     ███████║   ██║   ███████╗██║  ██║╚██████╔╝██║██████╔╝███████║", terminal);
		terminal.setCursorPosition(10, 17);
		Write(" ╚═════╝  ╚═╝  ╚═══╝     ╚══════╝   ╚═╝   ╚══════╝╚═╝  ╚═╝ ╚═════╝ ╚═╝╚═════╝ ╚══════╝", terminal);

		// Cursor auf Position bewegen
		terminal.setCursorPosition(6, 18);
		Write("Willkommen im Spiel", terminal); // Text schreiben
		terminal.setCursorPosition(6, 19);
		Write("Drücke ENTER, um das Spiel zu starten.", terminal);
		terminal.setCursorPosition(6, 20);
		Write("Drücke ESCAPE, um das Spiel zu verlassen.", terminal);

		// Texte im Terminal anzeigen
		terminal.flush();

		// Eingabe abwarten
		outerloop: while (true) {

			// Tastatureinggabe wird gelesen
			KeyStroke eingabe = terminal.readInput();
			
			if (eingabe != null) {

				// System.out.println(eingabe); // zur Kontrolle kann eingebene
				// Taste angezeigt werden

				// wenn die Taste ENTER gedruckt wird
				if (eingabe.getKeyType().equals(KeyType.Enter)) {

					// Drucke Schwierigkeit
					terminal.clearScreen();
					terminal.setCursorPosition(6, 6);
					Write("██████████╗   ███╗   ██╗   █████████╗   ██╗   ██╗   █████████╗", terminal);
					terminal.setCursorPosition(6, 7);
					Write("██╔═══════╝   ████╗  ██║   ██╔════██║   ██║ ██╔═╝   ██╔══════╝", terminal);
					terminal.setCursorPosition(6, 8);
					Write("██████████╗   ██╔██╗ ██║   █████████║   █████╔╝     ███████╗ ", terminal);
					terminal.setCursorPosition(6, 9);
					Write("╚═══════██║   ██║╚██╗██║   ██╔════██║   ██╔═██╗     ██╔════╝ ", terminal);
					terminal.setCursorPosition(6, 10);
					Write("██████████║   ██║ ╚████║   ██║    ██║   ██║  ██╗    █████████╗", terminal);
					terminal.setCursorPosition(6, 11);
					Write("╚═════════╝   ╚═╝  ╚═══╝   ╚═╝    ╚═╝   ╚═╝  ╚═╝    ╚════════╝", terminal);

					terminal.setCursorPosition(10, 12);
					Write(" ██████╗  ███╗   ██╗     ███████╗████████╗███████╗██████╗  ██████╗ ██╗██████╗ ███████╗",
							terminal);
					terminal.setCursorPosition(10, 13);
					Write("██╔═══██╗ ████╗  ██║     ██╔════╝╚══██╔══╝██╔════╝██╔══██╗██╔═══██╗██║██╔══██╗██╔════╝",
							terminal);
					terminal.setCursorPosition(10, 14);
					Write("██║   ██║ ██╔██╗ ██║     ███████╗   ██║   █████╗  ██████╔╝██║   ██║██║██║  ██║███████╗",
							terminal);
					terminal.setCursorPosition(10, 15);
					Write("██║   ██║ ██║╚██╗██║     ╚════██║   ██║   ██╔══╝  ██╔══██╗██║   ██║██║██║  ██║╚════██║",
							terminal);
					terminal.setCursorPosition(10, 16);
					Write("╚██████╔╝ ██║ ╚████║     ███████║   ██║   ███████╗██║  ██║╚██████╔╝██║██████╔╝███████║",
							terminal);
					terminal.setCursorPosition(10, 17);
					Write(" ╚═════╝  ╚═╝  ╚═══╝     ╚══════╝   ╚═╝   ╚══════╝╚═╝  ╚═╝ ╚═════╝ ╚═╝╚═════╝ ╚══════╝",
							terminal);

					terminal.setCursorPosition(6, 18);
					Write("Bitte waehlen Sie eine Schwierigkeit:", terminal);
					terminal.setCursorPosition(6, 19);
					Write("E - Einfach", terminal);
					terminal.setCursorPosition(6, 20);
					Write("M - Mittel", terminal);
					terminal.setCursorPosition(6, 21);
					Write("S - Schwer", terminal);

					terminal.flush();
					// Schwierigkeitsgrad festlegen (Geschwindigkeit)
					while (true) {
						
						KeyStroke grad = terminal.readInput();
						
						if (grad != null) {
							
							if (grad.getCharacter() == 'e') {
								
								geschwindigkeit = 200;
								schwierigkeit = Gamemodus.Leicht;
								
								break outerloop;
							}
							
							if (grad.getCharacter() == 'm') {
								
								geschwindigkeit = 100;
								schwierigkeit = Gamemodus.Mittel;
								
								break outerloop;
							}
							
							if (grad.getCharacter() == 's') {
								
								geschwindigkeit = 50;
								schwierigkeit = Gamemodus.Schwer;
								
								break outerloop;
							}
						}
					}
				}

				// wenn die Taste ESC gedrückt wird, beendet sich das Programm
				if (eingabe.getKeyType().equals(KeyType.Escape)) {
					System.exit(0);
				}
			}
		}
	}

	// Diese Methode hilft einen String zu "Zeichnen"
	private static void Write(String print, Terminal terminal) throws IOException {
		
		char[] printToChar = print.toCharArray();
		
		for (int i = 0; i < print.length(); i++) {
			
			terminal.putCharacter(printToChar[i]);
		}
	}

	// Diese Methode zeichnet ads gesamte Spielfeld auf einmal
	private static void WriteSpielfeld(Terminal terminal) throws IOException {

		for (int i = 0; i < spielfeld.length; i++) {
			
			for (int j = 0; j < spielfeld[i].length; j++) {
				
				terminal.setCursorPosition(i, j);
				terminal.setForegroundColor(spielfeld[i][j].textColor);
				terminal.setBackgroundColor(spielfeld[i][j].backColor);
				terminal.putCharacter(spielfeld[i][j].Text);
			}
		}

		terminal.flush();
	}

	// löscht den Inhalt vom Spielfeld
	private static void ClearSpielfeld() {

		for (int i = 0; i < spielfeld.length; i++) {
			
			for (int j = 0; j < spielfeld[i].length; j++) {
				
				spielfeld[i][j].textColor = DefaultTextColor;
				spielfeld[i][j].backColor = DefaultBackColor;
				spielfeld[i][j].Text = ' ';
			}
		}
	}

	private static void showGameOver(Terminal terminal) throws IOException {

		FileOutputStream out_stream = new FileOutputStream(new File("src//game//highscore"));
		Writer writer = new OutputStreamWriter(out_stream, "UTF-8");

		if (score_zahl > high_score) {

			writer.write(high_score_name + Integer.toString(score_zahl));

		} else {
			writer.write(high_score_name + Integer.toString(high_score));
		}
		writer.close();

		terminal.clearScreen();

		terminal.setCursorPosition(6, 6);
		Write(" ██████╗ █████╗ ███╗   ███╗███████╗", terminal);
		terminal.setCursorPosition(6, 7);
		Write("██╔════╝██╔══██╗████╗ ████║██╔════╝", terminal);
		terminal.setCursorPosition(6, 8);
		Write("██║ ███╗███████║██╔████╔██║█████╗ ", terminal);
		terminal.setCursorPosition(6, 9);
		Write("██║  ██║██╔══██║██║╚██╔╝██║██╔══╝ ", terminal);
		terminal.setCursorPosition(6, 10);
		Write("╚██████║██║  ██║██║ ╚═╝ ██║███████╗", terminal);
		terminal.setCursorPosition(6, 11);
		Write(" ╚═════╝╚═╝  ╚═╝╚═╝     ╚═╝╚══════╝", terminal);

		terminal.setCursorPosition(6, 14);
		Write(" ██████╗ ██╗   ██╗███████╗██████╗ ", terminal);
		terminal.setCursorPosition(6, 15);
		Write("██╔═══██╗██║   ██║██╔════╝██╔══██╗", terminal);
		terminal.setCursorPosition(6, 16);
		Write("██║   ██║██║   ██║█████╗  ██████╔╝", terminal);
		terminal.setCursorPosition(6, 17);
		Write("██║   ██║██║   ██║██╔══╝  ██╔═██║ ", terminal);
		terminal.setCursorPosition(6, 18);
		Write("╚██████╔╝╚██████╔╝███████╗██║ ╚██╗", terminal);
		terminal.setCursorPosition(6, 19);
		Write(" ╚═════╝  ╚═════╝ ╚══════╝╚═╝  ╚═╝", terminal);

		terminal.setCursorPosition(6, 21);
		Write("Drücken Sie Enter um weiter zu spielen oder ESC um das Spiel zu beenden", terminal);

		terminal.flush();

		while (true) {
			KeyStroke next = terminal.readInput();
			
			if (next.getKeyType().equals(KeyType.Enter)) {
				break;
			}
			
			if (next.getKeyType().equals(KeyType.Escape)) {
				System.exit(0);
			}
		}
	}

	private static void newHighScore(Terminal terminal) throws IOException {
		
		int counter = 6;
		high_score_name = "";
		
		terminal.clearScreen();
		terminal.setCursorPosition(6, 21);
		
		Write("Bitte geben Sie Ihren Namen ein: ", terminal);
		
		terminal.flush();
		terminal.setCursorPosition(6, 23);
		
		while (true) {
			
			KeyStroke n = terminal.readInput();
			
			if (n.getKeyType().equals(KeyType.Enter)) {
				break;
			}
			
			spielfeld[counter][23].Text = n.getCharacter();
			WriteSpielfeld(terminal);
			counter++;

			high_score_name = high_score_name + n.getCharacter();

		}

	}

}
