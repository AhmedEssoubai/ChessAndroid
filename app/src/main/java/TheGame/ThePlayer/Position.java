package TheGame.ThePlayer;

import java.io.Serializable;

/**
 * Created by AHMED on 03/05/2018.
 */

public class Position implements Serializable {
    private int x, y;
    public Position(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int GetX(){ return x; }

    public int GetY(){ return y; }

    public boolean Equal(Position position)
    {
        return position.x == this.x && position.y == this.y;
    }

}
