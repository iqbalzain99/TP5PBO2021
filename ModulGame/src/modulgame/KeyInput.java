/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulgame;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import modulgame.Game.STATE;
import static modulgame.dbConnection.stm;

/**
 *
 * @author Fauzan
 */
public class KeyInput extends KeyAdapter{
    
    private Handler handler;
    Game game;
    
    public KeyInput(Handler handler, Game game){
        this.game = game;
        this.handler = handler;
    }
    public void keyPressed(KeyEvent e){
        int key = e.getKeyCode();
        
        if(game.gameState == STATE.Game){
            for(int i = 0;i<handler.object.size();i++){
                GameObject tempObject = handler.object.get(i);

                if(tempObject.getId() == ID.Player){
                    if(key == KeyEvent.VK_W){
                        tempObject.setVel_y(-5);
                    }

                    if(key == KeyEvent.VK_S){
                        tempObject.setVel_y(+5);
                    }

                    if(key == KeyEvent.VK_A){
                        tempObject.setVel_x(-5);
                    }

                    if(key == KeyEvent.VK_D){
                        tempObject.setVel_x(+5);
                    }
                }
                
                if(tempObject.getId() == ID.Player2){
                    if(key == KeyEvent.VK_UP){
                        tempObject.setVel_y(-5);
                    }

                    if(key == KeyEvent.VK_DOWN){
                        tempObject.setVel_y(+5);
                    }

                    if(key == KeyEvent.VK_LEFT){
                        tempObject.setVel_x(-5);
                    }

                    if(key == KeyEvent.VK_RIGHT){
                        tempObject.setVel_x(+5);
                    }
                }
            }
            
        }
        
        if(game.gameState == STATE.GameOver){
            if(key == KeyEvent.VK_SPACE){
                dbConnection KonekKeDB = new dbConnection();
//                KonekKeDB.Query("INSERT INTO highscore (username, score) VALUE ('"+ game.getUsername() +"',"+game.getScore()+")");
                
                try {
                    String sql = "Select score from highscore WHERE username = '"+ game.getUsername()+"'";
                    ResultSet res = stm.executeQuery(sql);
                    
                    int hasil = -1;
                    if(res.next()){
                        hasil = Integer.parseInt(res.getString("Score"));
                        if( hasil < Integer.parseInt(game.getScore())){
                            KonekKeDB.Query("UPDATE highscore SET score = "+ game.getScore() +" WHERE username = '"+game.getUsername()+"'");
                        }
//                      System.out.println(hasil);
                    }else{
                        KonekKeDB.Query("INSERT INTO highscore (username, score) VALUE ('"+ game.getUsername() +"',"+game.getScore()+")");
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(KeyInput.class.getName()).log(Level.SEVERE, null, ex);
                }
                new Menu().setVisible(true);    
                game.close();
            }
        }
        if(key == KeyEvent.VK_ESCAPE){
            System.exit(1);
        }   
    }
    
    
    
    public void keyReleased(KeyEvent e){
        int key = e.getKeyCode();
        
        for(int i = 0;i<handler.object.size();i++){
            GameObject tempObject = handler.object.get(i);
            
            if(tempObject.getId() == ID.Player){
                if(key == KeyEvent.VK_W){
                    tempObject.setVel_y(0);
                }
                
                if(key == KeyEvent.VK_S){
                    tempObject.setVel_y(0);
                }
                
                if(key == KeyEvent.VK_A){
                    tempObject.setVel_x(0);
                }
                
                if(key == KeyEvent.VK_D){
                    tempObject.setVel_x(0);
                }
            }
            
            if(tempObject.getId() == ID.Player2){
                if(key == KeyEvent.VK_UP){
                    tempObject.setVel_y(0);
                }
                
                if(key == KeyEvent.VK_DOWN){
                    tempObject.setVel_y(0);
                }
                
                if(key == KeyEvent.VK_LEFT){
                    tempObject.setVel_x(0);
                }
                
                if(key == KeyEvent.VK_RIGHT){
                    tempObject.setVel_x(0);
                }
            }
        }
    }
}
