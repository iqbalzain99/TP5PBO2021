/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulgame;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.net.URL;
import java.sql.DriverManager;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import static modulgame.dbConnection.con;
import static modulgame.dbConnection.stm;

/**
 *
 * @author Fauzan
 */
public class Game extends Canvas implements Runnable{
    Window window;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    // counter Musuh
    private int count = 0;
    private int dir = 0;
    
    private int score = 0;
    private String username = "";
    private Clip song;
    private int time = 5;
    private int timePoint = 0;
    private Thread thread;
    private boolean running = false;
    private String lvl = "";
    
    
    
    
    private Handler handler;
    
    public enum STATE{
        Game,
        GameOver
    };
    
    public STATE gameState = STATE.Game;
    
    public Game(String uname, String mode, String level){
        window = new Window(WIDTH, HEIGHT, "Modul praktikum 5", this);
        
        handler = new Handler();
        this.lvl = level;
        if(this.lvl == "Easy"){
            time = 20;
        }
        else if(this.lvl == "Normal"){
            time = 10;
        }
        else if(this.lvl == "Hard"){
            time = 5;
        }
        this.addKeyListener(new KeyInput(handler, this));
        
        if(gameState == STATE.Game){
            handler.addObject(new Items(100,150, ID.Item));
            handler.addObject(new Items(200,350, ID.Item));
            handler.addObject(new Player(200,200, ID.Player));
            handler.addObject(new Player(400,400, ID.Enemy));
            if(mode == "MultiPlayer"){
                handler.addObject(new Player(250,250, ID.Player2));
            }
            playSong("/Song.wav");
            
            
        }
        username = uname;
    }

    public synchronized void start(){
        thread = new Thread(this);
        thread.start();
        running = true;
    }
    
    public synchronized void stop(){
        try{
            thread.join();
            running = false;
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        
        while(running){
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            
            while(delta >= 1){
                tick();
                delta--;
            }
            if(running){
                render();
                frames++;
            }
            
            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
                //System.out.println("FPS: " + frames);
                frames = 0;
                if(gameState == STATE.Game){
                    if(time>0){
                        time--;
                        timePoint++;
                    }else{
                        gameState = STATE.GameOver;
                    }
                }
            }
        }
        
        stop();
        
    }
    
    public int acak(int awal, int akhir) {
    return (int) ((Math.random() * (akhir - awal)) + awal);
    }
    
    private void tick(){
        handler.tick();
        if(gameState == STATE.Game){
            GameObject playerObject = null;
            GameObject player2Object = null;
            GameObject enemy = null;
            
            for(int i=0;i< handler.object.size(); i++){
                if(handler.object.get(i).getId() == ID.Player ){
                   playerObject = handler.object.get(i);
                }
                if(handler.object.get(i).getId() == ID.Player2 ){
                   player2Object = handler.object.get(i);
                }
                if(handler.object.get(i).getId() == ID.Enemy ){
                   enemy = handler.object.get(i);
                }
                
            }
            if(enemy != null){
                for(int i=0;i< handler.object.size() && time>0; i++){
                    if(handler.object.get(i).getId() == ID.Item){
                        if(playerObject != null){
                            if(enemyCollision(playerObject , enemy)  ){
                                time = 0;
                                break;
                            }
                        }
                        if(player2Object != null){
                            if(enemyCollision(player2Object , enemy)  ){
                                time = 0;
                                break;
                            }
                        }
                        System.out.println(count);
                        if(dir == 0 ){
                            if(this.lvl == "Easy"){
                                enemy.setVel_y(-3);
                            }
                            else if(this.lvl == "Normal"){
                                enemy.setVel_y(-5);
                            }
                            else if(this.lvl == "Hard"){
                                enemy.setVel_y(-7);
                            }
                            
                            
                        }else if(dir == 1){
                            if(this.lvl == "Easy"){
                                enemy.setVel_y(+3);
                            }
                            else if(this.lvl == "Normal"){
                                enemy.setVel_y(+5);
                            }
                            else if(this.lvl == "Hard"){
                                enemy.setVel_y(+7);
                            }
                        }
                        count ++;
                        if(count > 100){
                            count = 0;
                            if(dir == 1) dir = 0;
                            else dir = 1;
                        }
                        
                    }
                }
            }
            if(playerObject != null){
                for(int i=0;i< handler.object.size(); i++){
                    if(handler.object.get(i).getId() == ID.Item){
                        if(checkCollision(playerObject, handler.object.get(i))){
                            playSound("/Eat.wav");
                            handler.removeObject(handler.object.get(i));
                            handler.addObject(new Items(acak(0,700),acak(0,500), ID.Item));
                            score = score + acak(10, 35);
                            time = time + acak(0, 2);
                            break;
                        }
                    }
                }
            }
            if(player2Object != null){
                for(int i=0;i< handler.object.size(); i++){
                    if(handler.object.get(i).getId() == ID.Item){
                        if(checkCollision(player2Object, handler.object.get(i))){
                            playSound("/Eat.wav");
                            handler.removeObject(handler.object.get(i));
                            handler.addObject(new Items(acak(0,700),acak(0,500), ID.Item));
                            score = score + acak(10, 35);
                            time = time + acak(0, 2);
                            break;
                        }
                    }
                }
            }
        }
    }
    
    public static boolean enemyCollision(GameObject player, GameObject enemy){
        boolean result = false;
        
        int sizePlayer = 50;
        int sizeEnemy = 50;
        
        int playerLeft = player.x;
        int playerRight = player.x + sizePlayer;
        int playerTop = player.y;
        int playerBottom = player.y + sizePlayer;
        
        int enemyLeft = enemy.x;
        int enemyRight = enemy.x + sizeEnemy;
        int enemyTop = enemy.y;
        int enemyBottom = enemy.y + sizeEnemy;
        
        if((playerRight > enemyLeft ) &&
        (playerLeft < enemyRight) &&
        (enemyBottom > playerTop) &&
        (enemyTop < playerBottom)
        ){
            result = true;
        }
        
        return result;
    }
    
    public static boolean checkCollision(GameObject player, GameObject item){
        boolean result = false;
        
        int sizePlayer = 50;
        int sizeItem = 20;
        
        int playerLeft = player.x;
        int playerRight = player.x + sizePlayer;
        int playerTop = player.y;
        int playerBottom = player.y + sizePlayer;
        
        int itemLeft = item.x;
        int itemRight = item.x + sizeItem;
        int itemTop = item.y;
        int itemBottom = item.y + sizeItem;
        
        if((playerRight > itemLeft ) &&
        (playerLeft < itemRight) &&
        (itemBottom > playerTop) &&
        (itemTop < playerBottom)
        ){
            result = true;
        }
        
        return result;
    }
    
    private void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }
        
        Graphics g = bs.getDrawGraphics();
        
        g.setColor(Color.decode("#F1f3f3"));
        g.fillRect(0, 0, WIDTH, HEIGHT);
                
        
        
        if(gameState ==  STATE.Game){
            handler.render(g);
            
            Font currentFont = g.getFont();
            Font newFont = currentFont.deriveFont(currentFont.getSize() * 1.4F);
            g.setFont(newFont);

            g.setColor(Color.BLACK);
            g.drawString("Score: " +Integer.toString(score), 20, 20);

            g.setColor(Color.BLACK);
            g.drawString("Time: " +Integer.toString(time), WIDTH-120, 20);
        }else{
            Font currentFont = g.getFont();
            Font newFont = currentFont.deriveFont(currentFont.getSize() * 3F);
            g.setFont(newFont);

            g.setColor(Color.BLACK);
            g.drawString("GAME OVER", WIDTH/2 - 120, HEIGHT/2 - 30);

            currentFont = g.getFont();
            Font newScoreFont = currentFont.deriveFont(currentFont.getSize() * 0.5F);
            g.setFont(newScoreFont);

            g.setColor(Color.BLACK);
            g.drawString("Score: " +Integer.toString(score), WIDTH/2 - 50, HEIGHT/2 - 10);
            
            g.setColor(Color.BLACK);
            g.drawString("Press Space to Continue", WIDTH/2 - 100, HEIGHT/2 + 30);
        }
                
        g.dispose();
        bs.show();
    }
    
    public static int clamp(int var, int min, int max){
        if(var >= max){
            return var = max;
        }else if(var <= min){
            return var = min;
        }else{
            return var;
        }
    }
    
    public void close(){
        song.stop();
        window.CloseWindow();
    }
    
    public void playSound(String filename){
        try {
            // Open an audio input stream.
            URL url = this.getClass().getResource(filename);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            // Get a sound clip resource.
            Clip clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException e) {
           e.printStackTrace();
        } catch (IOException e) {
           e.printStackTrace();
        } catch (LineUnavailableException e) {
           e.printStackTrace();
        }
    
    }
    
    public void playSong(String filename){
        try {
            // Open an audio input stream.
            URL url = this.getClass().getResource(filename);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            // Get a sound clip resource.
            song = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            song.open(audioIn);
            song.start();
        } catch (UnsupportedAudioFileException e) {
           e.printStackTrace();
        } catch (IOException e) {
           e.printStackTrace();
        } catch (LineUnavailableException e) {
           e.printStackTrace();
        }
    
    }
    
    public String getUsername(){
        return username;
    }
    public String getScore(){
        
        return Integer.toString(score+timePoint);
    }
    
}
