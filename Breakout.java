/*
 * File: Breakout.java 
 * -------------------
 * This game requires the ACM Graphics library to be installed.
 */

import acm.graphics.*;  
import acm.program.*;
import acm.util.*;

import java.applet.*;
import java.awt.*;
import java.awt.event.*;

public class Breakout extends GraphicsProgram {

/** Width and height of application window in pixels */
	public static final int APPLICATION_WIDTH = 400;
	public static final int APPLICATION_HEIGHT = 600;

/** Dimensions of game board (usually the same) */
	private static final int WIDTH = APPLICATION_WIDTH;
	private static final int HEIGHT = APPLICATION_HEIGHT;

/** Dimensions of the paddle */
	private static final int PADDLE_WIDTH = 60;
	private static final int PADDLE_HEIGHT = 10;

/** Offset of the paddle up from the bottom */
	private static final int PADDLE_Y_OFFSET = 30;

/** Number of bricks per row */
	private static final int NBRICKS_PER_ROW = 10;

/** Number of rows of bricks */
	private static final int NBRICK_ROWS = 10;

/** Separation between bricks */
	private static final int BRICK_SEP = 4;

/** Width of a brick */
	private static final int BRICK_WIDTH =
	  (WIDTH - (NBRICKS_PER_ROW - 1) * BRICK_SEP) / NBRICKS_PER_ROW;

/** Height of a brick */
	private static final int BRICK_HEIGHT = 8;

/** Radius of the ball in pixels */
	private static final int BALL_RADIUS = 10;

/** Offset of the top brick row from the top */
	private static final int BRICK_Y_OFFSET = 70;

/** Number of turns */
	private static final int NTURNS = 3;

/** Total number of bricks*/
	private static final int NBRICKS = NBRICKS_PER_ROW * NBRICK_ROWS;
	private GRect paddle;
	private GOval ball;
	private RandomGenerator rgen= new RandomGenerator();
	private double vx, vy;
	private int BRICK_COUNT = NBRICKS_PER_ROW * NBRICK_ROWS;
	private int score;
	private int lives = NTURNS;
	private GLabel livelabel=new GLabel("");
	private GLabel scorelabel=new GLabel("");
/**Boolean for whether HARD_MODE is on.*/
	private boolean HARD_MODE=true;
/**The increase in speed after HARD_MODE_FREQ hits on the paddle.*/
	private int HARD_MODE_LEVEL=1;
/**Count of paddle hits*/
	private int PADDLE_HIT_COUNT=0;
/** Number of times the ball hits the paddle before speeding up.*/
	private int HARD_MODE_FREQ=7;
/* Method: run() */
/** Runs the Breakout program. */
	public void init() {
		setSize(APPLICATION_WIDTH,APPLICATION_HEIGHT);
		setupBricks(); /* puts bricks on screen*/
		setupPaddle(); /*puts paddle*/
		addMouseListeners(); /*initialize mouse listeners*/
		addKeyListeners();
		addScore();
		addLiveCount();
	}
	public void run(){
		score=0;
		for (int i=lives;i>0;i--){
			setupBall();/*place ball on screen*/
			ballMotion();
		}
		if (lives==0){
			GLabel losingmessage=new GLabel("No balls remaining. Please insert quarter.");
			add(losingmessage,(getWidth()-losingmessage.getWidth())/2,(getHeight()-losingmessage.getHeight())/2);
		}
	}
	public void addScore(){
		scorelabel.setLabel("Score: "+score);
		add(scorelabel,BRICK_SEP,scorelabel.getHeight()+BRICK_SEP);
	}
	public void addLiveCount(){
		livelabel.setLabel("Lives: "+lives);
		add(livelabel,BRICK_SEP,2*livelabel.getHeight()+2*BRICK_SEP);
	}
	
	public void setupBall(){
		ball=new GOval(BALL_RADIUS*2,BALL_RADIUS*2);
		ball.setFilled(true);
		add(ball,getWidth()/2-BALL_RADIUS,getHeight()-PADDLE_HEIGHT-PADDLE_Y_OFFSET-BALL_RADIUS*3);
		ball.sendToBack(); /*sends ball to back so referencing collisions will be simpler*/
	}
	
	/*Moves ball. Will also use checkCollision for logic*/
	public void ballMotion(){
			while(true){
				ball.move(vx,vy);
				bounceLogic();
				if (checkLoss())
					break;
				if(checkWin())
					break;
				pause(10);
			}
	}	
	
	/*Will check 4 points on ball's bounds for contact. Eventually, make this more accurate by switching to polar coordinates.*/
	public void bounceLogic(){
		GPoint ballUpper= new GPoint(ball.getX()+BALL_RADIUS,ball.getY());
		GPoint ballRight= new GPoint(ball.getX()+BALL_RADIUS*2,ball.getY()+BALL_RADIUS);
		GPoint ballLeft= new GPoint(ball.getX(),ball.getY()+BALL_RADIUS);
		GPoint ballLower= new GPoint(ball.getX()+BALL_RADIUS,ball.getY()+BALL_RADIUS*2);
		if (checkCollision(ballUpper,false)) vy=-vy;
		if (checkCollision(ballRight,true)) vx=-vx;
		if (checkCollision(ballLeft,true)) vx=-vx;
		if (checkCollision(ballLower,true)) vy=-vy;
	}
	public boolean checkWin(){
		if (score==NBRICKS){
			vx=0;
			vy=0;
			GLabel victory = new GLabel("You win!");
			add(victory,(getWidth()-victory.getWidth())/2,(getHeight()-victory.getHeight())/2);
			remove(ball);
			return true;
		}else return false;
	}
	public boolean checkCollision(GPoint corner,boolean paddleflag){
		GObject contactPoint = getElementAt(corner);
		if (contactPoint!=null && contactPoint!=ball){
			if (contactPoint!=paddle){
				remove(contactPoint);
				score +=1;
				scorelabel.setLabel("Score: "+score);
				return true;
			}else if (contactPoint==paddle){
				PADDLE_HIT_COUNT+=1;
				if (PADDLE_HIT_COUNT%HARD_MODE_FREQ==0){
					vy+=HARD_MODE_LEVEL;
				}
				return (paddleflag? true : false);
			}
		}else if (corner.getX()<=1 || corner.getX()>=getWidth()-2*BALL_RADIUS || corner.getY()<=0){
			return true;
		}
		return false;
	}
	public boolean checkLoss(){
		if (ball.getY()>getHeight()){
			lives=lives-1;
			vx=0;
			vy=0;
			livelabel.setLabel("Lives: "+lives);
			return true;
		}else return false;
	}
	/*Uses a mouse click to start ball motion by setting vx and vy to non-zero numbers if the ball is stationary.
	 * Clicking will do nothing if the ball is in motion*/
	public void mouseClicked(MouseEvent e){
		if (vy==0){
			vx = rgen.nextDouble(1.0,3.0);
			if (rgen.nextBoolean(.5)) vx=-vx;
			vy=3;
			if (rgen.nextBoolean(.5)) vy=-vy;
			}
		}
	public void setupPaddle(){
		paddle = new GRect(PADDLE_WIDTH,PADDLE_HEIGHT);
		add(paddle,(getWidth()-PADDLE_WIDTH)/2,getHeight()-PADDLE_HEIGHT-PADDLE_Y_OFFSET);
	}
	public void mouseMoved(MouseEvent e){
		double mouseX = e.getX();
		if (mouseX>PADDLE_WIDTH/2 && mouseX<getWidth()-PADDLE_WIDTH/2){
			paddle.setLocation(mouseX-PADDLE_WIDTH/2,getHeight()-PADDLE_HEIGHT-PADDLE_Y_OFFSET);
		}else if (mouseX<PADDLE_WIDTH/2){
			paddle.setLocation(0,getHeight()-PADDLE_HEIGHT-PADDLE_Y_OFFSET);
		}else if (mouseX>getWidth()-PADDLE_WIDTH){
			paddle.setLocation(getWidth()-PADDLE_WIDTH,getHeight()-PADDLE_HEIGHT-PADDLE_Y_OFFSET);
		}
	}
	public void keyPressed(KeyEvent e){
		switch(e.getKeyCode()){
		case KeyEvent.VK_LEFT: if (paddle.getX()>0)
			paddle.move(-10, 0);break;
		case KeyEvent.VK_RIGHT: if (paddle.getX()<getWidth()-PADDLE_WIDTH)
			paddle.move(+10, 0);break;
		/*case KeyEvent.VK_SPACE: paddle.scale(2, 1);pause(500);paddle.scale(.5,1);break;*/
		}
	}
	private void setupBricks(){
		for (int i=0;i<NBRICK_ROWS;i++){
				if (i==0 || i%10==0 || i == 1 || i%10==1){
					addRow(i,Color.red);
				}else if (i==2 || i%10==2 || i == 3 || i%10==3){
					addRow(i,Color.orange);
				}else if (i==4 || i%10==4 || i == 5 || i%10==5){
					addRow(i,Color.yellow);
				}else if (i==6 || i%10==6 || i == 7 || i%10==7){
					addRow(i,Color.green);
				}else if (i==8 || i%10==8 || i == 9 || i%10==9){
					addRow(i,Color.cyan);
				}
		}
	}
	public void addRow(int rowNumber,Color color){
		for (int b=0;b<NBRICKS_PER_ROW;b++){
			GRect brick = new GRect(b*(BRICK_WIDTH+BRICK_SEP),BRICK_Y_OFFSET+rowNumber*(BRICK_HEIGHT+BRICK_SEP),BRICK_WIDTH,BRICK_HEIGHT);
			brick.setFilled(true);
			brick.setFillColor(color);
			add(brick);
		}
	}
}
