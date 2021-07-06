package View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.SurfaceView;

public class MySurface extends SurfaceView{


    public MySurface(Context context) {
        super(context);
    }

    public MySurface(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySurface(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void draw(Canvas canvas) {
        Path clipPath = new Path();
        int w = this.getWidth();
        int h = this.getHeight();
        clipPath.addRoundRect(new RectF(0, 0, w, h), 10.0f, 10.0f, Path.Direction.CW);
        canvas.clipPath(clipPath);
        super.draw(canvas);
    }
}
