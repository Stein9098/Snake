package game;

public class Apple {
	private int pos_x;
	private int pos_y;

	public Apple(int x, int y) {
		this.pos_x = x;
		this.pos_y = y;
	}

	public int getX() {
		return pos_x;
	}

	public int getY() {
		return pos_y;
	}

	public void setNew(int x, int y) {
		this.pos_x = x;
		this.pos_y = y;
	}

	static public int generateKoordinate(int multiplikator) {

		// Schließt Max.Rahmen aus
		multiplikator = multiplikator - 4;
		// Generiert zufällige Koordinate
		int i = (int) (Math.random() * multiplikator);
		// schließt ungerade Werte aus
		if (i % 2 == 1) {
			i++;
		}
		// Schließt 0 (Rahmen) aus
		i = (i == 0) ? 2 : i;
		return i;
	}

}
