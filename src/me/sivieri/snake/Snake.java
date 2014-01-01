package me.sivieri.snake;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import processing.core.PApplet;
import processing.event.MouseEvent;

public class Snake extends PApplet {
	private static final int ROWS = 10;
	private static final int COLS = 10;
	private static final int SIDE = 40;
	private static final int SPEED = 500;

	private int topCorner;
	private int leftCorner;
	private Pair food;
	private Deque<Pair> snake = new ArrayDeque<Pair>();
	private Set<Pair> duplicatesDetection = new HashSet<Pair>();
	private Random random = new Random();
	private Direction direction;
	private long last;
	private boolean collision = false;
	private int startTouchX;
	private int startTouchY;

	@Override
	public void setup() {
		this.topCorner = this.height / 2 - ROWS / 2 * SIDE;
		this.leftCorner = this.width / 2 - COLS / 2 * SIDE;
		this.direction = Direction.getRandomDirection();
		this.snake.addFirst(new Pair(this.random.nextInt(ROWS), this.random.nextInt(COLS)));
		generateFood();
		this.last = System.currentTimeMillis();
	}

	private void generateFood() {
		do {
			this.food = new Pair(this.random.nextInt(ROWS), this.random.nextInt(COLS));
		} while (this.snake.contains(this.food));
	}

	@Override
	public void draw() {
		// calculate the new position (with collisions)
		if (!this.collision) {
			long now = System.currentTimeMillis();
			if (now - this.last >= SPEED) {
				Pair newHead = null;
				Pair oldHead = this.snake.getFirst();
				switch (this.direction) {
					case UP:
						newHead = new Pair(oldHead.x - 1 < 0 ? ROWS - 1 : oldHead.x - 1, oldHead.y);
						break;
					case DOWN:
						newHead = new Pair(oldHead.x + 1 >= ROWS ? 0 : oldHead.x + 1, oldHead.y);
						break;
					case LEFT:
						newHead = new Pair(oldHead.x, oldHead.y - 1 < 0 ? COLS - 1 : oldHead.y - 1);
						break;
					case RIGHT:
						newHead = new Pair(oldHead.x, oldHead.y + 1 >= COLS ? 0 : oldHead.y + 1);
						break;
				// no default
				}
				this.snake.addFirst(newHead);
				if (newHead.equals(this.food)) {
					generateFood();
				}
				else {
					this.snake.removeLast();
				}
				for (Pair p : this.snake) {
					if (!this.duplicatesDetection.add(p)) {
						this.collision = true;
						break;
					}
				}
				this.duplicatesDetection.clear();
				this.last = now;
			}
		}
		// draw the board
		if (this.collision) {
			stroke(255, 0, 0);
		}
		for (int i = 0; i < ROWS; ++i) {
			for (int j = 0; j < COLS; ++j) {
				Pair p = new Pair(i, j);
				if (this.snake.contains(p) || p.equals(this.food)) {
					fill(0);
				}
				else {
					fill(255);
				}
				rect(this.leftCorner + j * SIDE, this.topCorner + i * SIDE, SIDE, SIDE);
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		this.startTouchX = arg0.getX();
		this.startTouchY = arg0.getY();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		int endTouchX = arg0.getX();
		int endTouchY = arg0.getY();
		int xdiff = endTouchX - this.startTouchX;
		int ydiff = endTouchY - this.startTouchY;
		if (Math.abs(xdiff) > Math.abs(ydiff) && xdiff > 0) {
			this.direction = Direction.RIGHT;
		}
		else if (Math.abs(xdiff) > Math.abs(ydiff) && xdiff < 0) {
			this.direction = Direction.LEFT;
		}
		else if (Math.abs(xdiff) < Math.abs(ydiff) && ydiff > 0) {
			this.direction = Direction.DOWN;
		}
		else if (Math.abs(xdiff) < Math.abs(ydiff) && ydiff < 0) {
			this.direction = Direction.UP;
		}
	}

}
