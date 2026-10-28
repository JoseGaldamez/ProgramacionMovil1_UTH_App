package dev.josegaldamez.tickethn_uth.tickets;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import dev.josegaldamez.tickethn_uth.R;

/** The outline, notches and perforation share one geometry at every screen size. */
public final class TicketSurfaceView extends FrameLayout {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Path outline = new Path();
    private final float density;
    public TicketSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        density = getResources().getDisplayMetrics().density;
        setWillNotDraw(false);
    }
    @Override protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        float edge = 2 * density, radius = 19 * density, notch = 19 * density;
        outline.reset();
        outline.addRoundRect(new RectF(edge, edge, w-edge, h-edge), radius, radius, Path.Direction.CW);
        Path holes = new Path();
        holes.addCircle(edge, h * .44f, notch, Path.Direction.CW);
        holes.addCircle(w-edge, h * .44f, notch, Path.Direction.CW);
        outline.op(holes, Path.Op.DIFFERENCE);
        paint.setShader(new LinearGradient(0, 0, w, h,
            new int[]{0xFF10434D, 0xFF142332, 0xFF142332},
            new float[]{0, .55f, 1}, Shader.TileMode.CLAMP));
    }
    @Override protected void onDraw(Canvas canvas) {
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(outline, paint);
        Shader shader = paint.getShader();
        paint.setShader(null);
        paint.setColor(0xFF51CDD0);
        paint.setStrokeWidth(2 * density);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(outline, paint);
        paint.setStrokeWidth(density);
        paint.setPathEffect(new DashPathEffect(new float[]{5*density, 6*density}, 0));
        canvas.drawLine(30*density, getHeight()*.44f, getWidth()-30*density, getHeight()*.44f, paint);
        paint.setPathEffect(null);
        paint.setShader(shader);
    }
    @Override protected void dispatchDraw(Canvas canvas) {
        int save = canvas.save();
        canvas.clipPath(outline);
        super.dispatchDraw(canvas);
        canvas.restoreToCount(save);
    }
}

