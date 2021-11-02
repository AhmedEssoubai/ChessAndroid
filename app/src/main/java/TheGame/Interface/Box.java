package TheGame.Interface;

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chess.nikiva.ahmed.chess.R;

import TheGame.ThePlayer.Pieces.Piece;

/**
 * Created by AHMED on 09/04/2018.
 */

class Box extends ImageButton {

    public Piece piece;
    private boolean possibleMove, capturedBox;

    public Box(Activity activity, int width){
        super(activity);
        this.setLayoutParams(new LinearLayout.LayoutParams(width, width));
        this.setPadding(0, 0, 0, 0);
        this.setScaleType(ImageView.ScaleType.FIT_XY);
        this.piece = null;
        this.possibleMove = false;
        this.capturedBox = false;
    }

    // If the player can capture the other player piece from this box
    public boolean IsCapturedBox()
    {
        return this.capturedBox;
    }

    public void CapturedBox(boolean capture)
    {
        if (this.capturedBox != capture)
        {
            this.capturedBox = capture;
            if (this.capturedBox)
                this.setBackgroundColor(Color.parseColor("#ff1b00"));
        }
    }

    // If the player can move piece to this box
    public boolean IsPossibleMove()
    {
        return this.possibleMove;
    }

    public void PossibleMove(boolean possible)
    {
        if (this.possibleMove != possible)
        {
            this.possibleMove = possible;
            if (this.possibleMove)
                this.setImageResource(R.drawable.pmove);
            else
                this.setImageBitmap(null);
        }
    }
}
