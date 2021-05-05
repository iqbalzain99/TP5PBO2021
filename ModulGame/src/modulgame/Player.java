/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulgame;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Fauzan
 */
public class Player extends GameObject{
    
    public Player(int x, int y, ID id){
        super(x, y, id);
        
        //speed = 1;
    }

    @Override
    public void tick() {
        x += vel_x;
        y += vel_y;
        
        x = Game.clamp(x, 0, Game.WIDTH - 60);
        y = Game.clamp(y, 0, Game.HEIGHT - 80);

    }

    @Override
    public void render(Graphics g) {
        if(id == ID.Player){
            g.setColor(Color.decode("#3f6082"));
        }
        else if(id == ID.Player2){
            g.setColor(Color.decode("#00FF00"));
        }else{
            g.setColor(Color.decode("#FF0000"));
        }
        g.fillRect(x, y, 50, 50);
    }
}
