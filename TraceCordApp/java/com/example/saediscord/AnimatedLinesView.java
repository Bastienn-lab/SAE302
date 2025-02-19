package com.example.saediscord;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AnimatedLinesView extends View {
    private Paint paint;
    private Random random;
    private Handler handler;
    private List<Line> lines;

    public AnimatedLinesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AnimatedLinesView(Context context) {
        super(context);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(0xFFFFFFFF); // Blanc
        paint.setStrokeWidth(20);
        paint.setStrokeCap(Paint.Cap.ROUND); // Lignes arrondies
        paint.setStyle(Paint.Style.STROKE); // Dessiner uniquement les contours
        random = new Random();
        lines = new ArrayList<>();
        handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                addLine();
                handler.postDelayed(this, 2000); // Ajouter une ligne toutes les 2 secondes
            }
        }, 2000);
    }

    private void addLine() {
        int startX = random.nextInt(getWidth());
        int startY = random.nextInt(getHeight());
        int stopX = random.nextInt(getWidth());
        int stopY = random.nextInt(getHeight());

        final Line line = new Line(startX, startY, stopX, stopY, 7000); // Durée de vie de 5 secondes
        lines.add(line);

        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(2000); // Durée d'animation de dessin de la ligne
        animator.addUpdateListener(animation -> {
            line.setProgress((float) animation.getAnimatedValue());
            invalidate();
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        long currentTime = System.currentTimeMillis();
        List<Line> expiredLines = new ArrayList<>();

        for (Line line : lines) {
            if (currentTime - line.startTime > line.duration) {
                expiredLines.add(line);
            } else {
                paint.setAlpha((int) (255 * (1 - ((currentTime - line.startTime) / (float) line.duration)))); // Animation de la disparition
                canvas.drawPath(line.getPath(), paint);
            }
        }

        lines.removeAll(expiredLines); // Supprimer les lignes expirées
    }

    private class Line {
        private int startX, startY, stopX, stopY;
        private float progress;
        private long startTime;
        private long duration;
        private Path path;

        public Line(int startX, int startY, int stopX, int stopY, long duration) {
            this.startX = startX;
            this.startY = startY;
            this.stopX = stopX;
            this.stopY = stopY;
            this.progress = 0;
            this.startTime = System.currentTimeMillis();
            this.duration = duration;
            this.path = new Path();
            createCurvePath();
        }

        public int getStartX() { return startX; }
        public int getStartY() { return startY; }
        public int getStopX() { return stopX; }
        public int getStopY() { return stopY; }
        public float getProgress() { return progress; }
        public void setProgress(float progress) { this.progress = progress; }

        private void createCurvePath() {
            int controlX1 = random.nextInt(getWidth());
            int controlY1 = random.nextInt(getHeight());
            int controlX2 = random.nextInt(getWidth());
            int controlY2 = random.nextInt(getHeight());
            path.moveTo(startX, startY);
            path.cubicTo(controlX1, controlY1, controlX2, controlY2, stopX, stopY);
        }

        public Path getPath() {
            Path partialPath = new Path();
            PathMeasure measure = new PathMeasure(path, false);
            measure.getSegment(0, measure.getLength() * progress, partialPath, true);
            return partialPath;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - startTime > duration;
        }
    }
}
