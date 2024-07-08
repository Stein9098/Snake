package game;

import java.util.LinkedList;

public class SnakeBody {

	private int pos_x;
	private int pos_y;
	private Game.Richtung pos;

	public SnakeBody(int x, int y, Game.Richtung r) {
		this.pos_x = x;
		this.pos_y = y;
		this.pos = r;
	}

	public SnakeBody(SnakeBody sb) {
		this.pos_x = sb.pos_x;
		this.pos_y = sb.pos_y;
		this.pos = sb.pos;
	}

	public int getX() {
		return pos_x;
	}

	public int getY() {
		return pos_y;
	}

	public Game.Richtung getPos() {
		return pos;
	}

	public void setX(int x) {
		this.pos_x = x;
	}

	public void setY(int y) {
		this.pos_y = y;
	}

	public void setPos(Game.Richtung r) {
		this.pos = r;
	}

	public void setAll(SnakeBody sb) {
		this.pos_x = sb.pos_x;
		this.pos_y = sb.pos_y;
		this.pos = sb.pos;
	}

	static public SnakeBody bewegung(SnakeBody sb, int spielfeldBreite, int spielfeldHoehe) {

		switch (sb.pos) {
		case Links:
			if (sb.pos_x <= 1) {
				sb.pos_x = spielfeldBreite - 2;
			} else {
				sb.pos_x -= 2;
			}
			break;

		case Rechts:
			if (sb.pos_x >= spielfeldBreite - 2) {
				sb.pos_x = 0;
			} else {
				sb.pos_x += 2;
			}
			break;
		case Oben:
			if (sb.pos_y <= 0) {
				sb.pos_y = spielfeldHoehe - 1;

			} else {
				sb.pos_y -= 1;
			}
			break;

		case Unten:
			if (sb.pos_y >= spielfeldHoehe - 1) {
				sb.pos_y = 0;

			} else {
				sb.pos_y += 1;

			}
			break;
		}
		return sb;
	}

	static public Game.Richtung checkRichtung(SnakeBody sb, Game.Richtung r) {
		if (sb.pos != r) {
			return r;
		}
		return sb.pos;
	}

	//
	static public Boolean checkKollision(SnakeBody head, LinkedList<SnakeBody> snake) {
		// -1, da ein Pseudo-Obejkt hinten
		for (int i = 0; i < snake.size() - 1; i++) {
			if (i > 0) {
				if (head.pos_x == snake.get(i).pos_x && head.pos_y == snake.get(i).pos_y) {
					return true;
				}
			}
		}
		return false;

	}

}
