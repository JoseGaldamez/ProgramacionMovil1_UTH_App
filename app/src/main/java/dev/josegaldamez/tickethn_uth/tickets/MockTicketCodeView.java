package dev.josegaldamez.tickethn_uth.tickets;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import java.util.Random;

/** A QR-like placeholder only. Production must bind a server-issued admission code. */
public final class MockTicketCodeView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final boolean[][] modules = new boolean[29][29];
    public MockTicketCodeView(Context context, AttributeSet attrs) { super(context, attrs); }
    public void bind(String order) {
        Random random = new Random(order.hashCode());
        for (int y=0; y<29; y++) for (int x=0; x<29; x++) modules[y][x] = random.nextBoolean();
        invalidate();
    }
    @Override protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float size = Math.min(getWidth(), getHeight());
        float left = (getWidth()-size)/2, top = (getHeight()-size)/2;
        paint.setColor(0xFF020C19);
        canvas.drawRoundRect(left, top, left+size, top+size, size*.065f, size*.065f, paint);
        float unit = size/35;
        canvas.save();
        canvas.translate(left+3*unit, top+3*unit);
        paint.setColor(Color.WHITE);
        for (int y=0; y<29; y++) for (int x=0; x<29; x++) {
            if ((x<8 && y<8) || (x>20 && y<8) || (x<8 && y>20)) continue;
            if (x>=11 && x<=17 && y>=11 && y<=17) continue;
            if (modules[y][x]) canvas.drawRect(x*unit,y*unit,(x+1)*unit,(y+1)*unit,paint);
        }
        finder(canvas,0,0,unit); finder(canvas,22,0,unit); finder(canvas,0,22,unit);
        paint.setColor(0xFF51CDD0);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(unit*.7f);
        canvas.save();
        canvas.rotate(40,14.5f*unit,14.5f*unit);
        canvas.drawRoundRect(12.5f*unit,11.5f*unit,16.5f*unit,17.5f*unit,unit*.4f,unit*.4f,paint);
        canvas.drawLine(14.5f*unit,13*unit,14.5f*unit,16*unit,paint);
        canvas.restore();
        paint.setStyle(Paint.Style.FILL);
        canvas.restore();
    }
    private void finder(Canvas canvas,int x,int y,float u) {
        paint.setColor(Color.WHITE); canvas.drawRect(x*u,y*u,(x+7)*u,(y+7)*u,paint);
        paint.setColor(0xFF020C19); canvas.drawRect((x+1)*u,(y+1)*u,(x+6)*u,(y+6)*u,paint);
        paint.setColor(Color.WHITE); canvas.drawRect((x+2)*u,(y+2)*u,(x+5)*u,(y+5)*u,paint);
    }
}

