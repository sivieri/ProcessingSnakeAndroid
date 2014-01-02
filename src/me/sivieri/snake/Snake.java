package me.sivieri.snake;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

import processing.core.PApplet;
import processing.event.MouseEvent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class Snake extends PApplet {
	private static final int ROWS = 10;
	private static final int COLS = 10;
	private static final int SPEED = 500;

	private int topCorner;
	private int leftCorner;
	private Pair food;
	private Deque<Pair> snake = new ArrayDeque<Pair>();
	private Random random = new Random();
	private Direction direction;
	private long last;
	private boolean collision = false;
	private int startTouchX;
	private int startTouchY;
	private int side;
	private Handler handler;
	private Runnable finalMessage = new Runnable() {

		@Override
		public void run() {
			Toast.makeText(Snake.this, getString(R.string.final_msg) + " " + Snake.this.snake.size(), Toast.LENGTH_LONG).show();
		}

	};

	@Override
	public void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		this.handler = new Handler();
	}

	@Override
	public void setup() {
		this.side = getResources().getInteger(R.integer.board_cell_size);
		this.topCorner = this.height / 2 - ROWS / 2 * this.side;
		this.leftCorner = this.width / 2 - COLS / 2 * this.side;
		this.direction = Direction.values()[this.random.nextInt(Direction.values().length)];
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
						newHead = new Pair(oldHead.getX() - 1 < 0 ? ROWS - 1 : oldHead.getX() - 1, oldHead.getY());
						break;
					case DOWN:
						newHead = new Pair(oldHead.getX() + 1 >= ROWS ? 0 : oldHead.getX() + 1, oldHead.getY());
						break;
					case LEFT:
						newHead = new Pair(oldHead.getX(), oldHead.getY() - 1 < 0 ? COLS - 1 : oldHead.getY() - 1);
						break;
					case RIGHT:
						newHead = new Pair(oldHead.getX(), oldHead.getY() + 1 >= COLS ? 0 : oldHead.getY() + 1);
						break;
				// no default
				}
				for (Pair p : this.snake) {
					if (p.equals(newHead)) {
						this.collision = true;
						this.handler.post(this.finalMessage);
						break;
					}
				}
				this.snake.addFirst(newHead);
				if (newHead.equals(this.food)) {
					generateFood();
				}
				else {
					this.snake.removeLast();
				}
				this.last = now;
			}
		}
		// draw the board
		for (int i = 0; i < ROWS; ++i) {
			for (int j = 0; j < COLS; ++j) {
				Pair p = new Pair(i, j);
				if (p.equals(this.snake.getFirst())) {
					stroke(255);
				}
				else {
					if (this.collision) {
						stroke(255, 0, 0);
					}
					else {
						stroke(0);
					}
				}
				if (this.snake.contains(p) || p.equals(this.food)) {
					fill(0);
				}
				else {
					fill(255);
				}
				rect(this.leftCorner + j * this.side, this.topCorner + i * this.side, this.side, this.side);
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
