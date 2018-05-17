package io.github.mionick.set;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

public class CanvasView extends View {

    private int width;
    private int height;
    private int boardOffset;

    private Path mPath;
    Context context;
    private Paint selectedPaint;
    private Paint fontPaint;
    private Paint blackOutline;
    private Paint blackFill;
    private Paint hintPaint;
    private static final int CARD_PADDING_X = 20; //pixels;
    private static final int CARD_PADDING_Y = 10; //pixels;
    private static final int SHAPE_PADDING_X = 40; //pixels;
    private static final int SHAPE_PADDING_Y = 5; //pixels;

    private boolean hintOn = false;

    private boolean gameOver = false;

    private SetGame game;

    // TODO: This is more like controller logic.
    Set<Integer> selectedCards = new HashSet<>(3);

    // Card Paints. Indexes are: fill pattern, color
    Paint[][] fillColors = new Paint[3][3];
    private long duration = 0;

    public CanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);
        context = c;

        // we set a new Path
        mPath = new Path();

        int color1 = Color.RED;
        int color2 = Color.GREEN;
        int color3 = Color.CYAN;

        // and we set a new Paint with the desired attributes
        selectedPaint = new Paint();
        selectedPaint.setAntiAlias(true);
        selectedPaint.setColor(Color.YELLOW);
        selectedPaint.setStyle(Paint.Style.STROKE);
        selectedPaint.setStrokeJoin(Paint.Join.ROUND);
        selectedPaint.setStrokeWidth(6f);

        fontPaint = new Paint();
        fontPaint.setAntiAlias(true);
        fontPaint.setColor(Color.YELLOW);
        fontPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        fontPaint.setStrokeJoin(Paint.Join.ROUND);
        fontPaint.setStrokeWidth(6f);
        fontPaint.setTextSize(200);


        hintPaint = new Paint();
        hintPaint.setAntiAlias(true);
        hintPaint.setColor(Color.GRAY);
        hintPaint.setStyle(Paint.Style.STROKE);
        hintPaint.setStrokeJoin(Paint.Join.ROUND);
        hintPaint.setStrokeWidth(7f);

        blackOutline = new Paint();
        blackOutline.setAntiAlias(true);
        blackOutline.setColor(Color.BLACK);
        blackOutline.setStyle(Paint.Style.STROKE);
        blackOutline.setStrokeJoin(Paint.Join.ROUND);
        blackOutline.setStrokeWidth(7f);

        blackFill = new Paint();
        blackFill.setAntiAlias(true);
        blackFill.setColor(Color.BLACK);
        blackFill.setStyle(Paint.Style.FILL);
        blackFill.setStrokeJoin(Paint.Join.ROUND);
        blackFill.setStrokeWidth(7f);

        // SOLID
        fillColors[0][0] = new Paint();
        fillColors[0][0].setAntiAlias(true);
        fillColors[0][0].setColor(color1);
        fillColors[0][0].setStyle(Paint.Style.FILL);
        fillColors[0][0].setStrokeJoin(Paint.Join.ROUND);
        fillColors[0][0].setStrokeWidth(4f);

        fillColors[0][1] = new Paint();
        fillColors[0][1].setAntiAlias(true);
        fillColors[0][1].setColor(color2);
        fillColors[0][1].setStyle(Paint.Style.FILL);
        fillColors[0][1].setStrokeJoin(Paint.Join.ROUND);
        fillColors[0][1].setStrokeWidth(4f);

        fillColors[0][2] = new Paint();
        fillColors[0][2].setAntiAlias(true);
        fillColors[0][2].setColor(color3);
        fillColors[0][2].setStyle(Paint.Style.FILL);
        fillColors[0][2].setStrokeJoin(Paint.Join.ROUND);
        fillColors[0][2].setStrokeWidth(4f);

        // EMPTY
        fillColors[1][0] = new Paint();
        fillColors[1][0].setAntiAlias(true);
        fillColors[1][0].setColor(color1);
        fillColors[1][0].setStyle(Paint.Style.STROKE);
        fillColors[1][0].setStrokeJoin(Paint.Join.ROUND);
        fillColors[1][0].setStrokeWidth(8f);

        fillColors[1][1] = new Paint();
        fillColors[1][1].setAntiAlias(true);
        fillColors[1][1].setColor(color2);
        fillColors[1][1].setStyle(Paint.Style.STROKE);
        fillColors[1][1].setStrokeJoin(Paint.Join.ROUND);
        fillColors[1][1].setStrokeWidth(8f);

        fillColors[1][2] = new Paint();
        fillColors[1][2].setAntiAlias(true);
        fillColors[1][2].setColor(color3);
        fillColors[1][2].setStyle(Paint.Style.STROKE);
        fillColors[1][2].setStrokeJoin(Paint.Join.ROUND);
        fillColors[1][2].setStrokeWidth(8f);

        // STRIPED
        int backgroundColor = Color.TRANSPARENT;

        BitmapShader stripedShader1 = new BitmapShader(makeStripeMap(Color.GRAY, color1), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        BitmapShader stripedShader2 = new BitmapShader(makeStripeMap(Color.GRAY, color2), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        BitmapShader stripedShader3 = new BitmapShader(makeStripeMap(Color.GRAY, color3), Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

        fillColors[2][0] = new Paint();
        fillColors[2][0].setAntiAlias(true);
        fillColors[2][0].setColor(Color.RED);
        fillColors[2][0].setStyle(Paint.Style.FILL);
        fillColors[2][0].setStrokeJoin(Paint.Join.ROUND);
        fillColors[2][0].setStrokeWidth(4f);
        fillColors[2][0].setShader(stripedShader1);

        fillColors[2][1] = new Paint();
        fillColors[2][1].setAntiAlias(true);
        fillColors[2][1].setColor(Color.GREEN);
        fillColors[2][1].setStyle(Paint.Style.FILL);
        fillColors[2][1].setStrokeJoin(Paint.Join.ROUND);
        fillColors[2][1].setStrokeWidth(4f);
        fillColors[2][1].setShader(stripedShader2);

        fillColors[2][2] = new Paint();
        fillColors[2][2].setAntiAlias(true);
        fillColors[2][2].setColor(Color.BLUE);
        fillColors[2][2].setStyle(Paint.Style.FILL);
        fillColors[2][2].setStrokeJoin(Paint.Join.ROUND);
        fillColors[2][2].setStrokeWidth(4f);
        fillColors[2][2].setShader(stripedShader3);

    }


    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw the mPath with the selectedPaint on the canvas when onDraw
        if (game == null) {
            return;
        }

        // Draw the current cards

        // the actual cards are 2.25 * 3.3 inches.
        // With a 3x4 grid that's 9.5 x 9 inches. Let's do that, and expand if the board gets bigger.

        // Calculate maximum area with those proportions
        // height/width
        double desiredAspectRatio = 9.5/9.0;

        height = canvas.getHeight();
        // Assume we are not as tall as we are wide
        width = (int)(height / desiredAspectRatio);


        int numCards = game.getCurrentBoardSize();
        int rows = 3;
        int columns = numCards / rows;

        // adjust display width if more than the expected number of columns.
        width = (int)(width * columns/4.0);

        // calculate board offset, to keep centered.
        // Must be done after expanding for more cards
        boardOffset = (canvas.getWidth() - width)/2;

        // must be done after expanding for more cards.
        int cardWidth = (width/columns);
        int cardHeight = (height/rows);

        SetCard[] cards = game.getBoard();

        int saveCountPreCard = canvas.save();
        try {
            canvas.translate(boardOffset, 0);

            for (int i = 0; i < columns; i++) {
                for (int j = 0; j < rows; j++) {
                    int saveCount = canvas.save();
                    try {
                        canvas.translate(i * cardWidth, j * cardHeight);
                        drawCard(
                                cards[i * rows + j],
                                i * rows + j,
                                cardWidth,
                                cardHeight,
                                canvas);
                    } finally {
                        canvas.restoreToCount(saveCount);
                    }
                }
            }
        } finally {
            canvas.restoreToCount(saveCountPreCard);
        }

        // IF THE GAME IS OVER TELL THEM
        if (gameOver) {
            Drawable d = getResources().getDrawable(R.drawable.ic_you_win);
            // Take 80% of width, and more to the top of the screen
            int imageWidth = (int)(canvas.getWidth() * 0.8);
            double aspectRatio = d.getIntrinsicHeight()/ (double)d.getIntrinsicWidth();
            int imageHeight = (int )(imageWidth * aspectRatio);
            int left = (canvas.getWidth() - imageWidth)/2 - 80;
            d.setBounds(left, 10,canvas.getWidth() - left, 10 + imageHeight);
            d.draw(canvas);

            // Then draw the time it took:
            int seconds = (int)(duration/1000000000);
            String time = String.format("%02d:%02d",seconds / 60, (seconds% 60));
            canvas.drawText("Time: " + time, (float) left + 200, (float) imageHeight, fontPaint);
        }
    }

    private void drawCard(SetCard card, int index, int cardWidth, int cardHeight, Canvas canvas) {
        if ( card == null ) {
            canvas.drawRoundRect(
                    CARD_PADDING_X,
                    CARD_PADDING_Y,
                    (cardWidth) - CARD_PADDING_X,
                    cardHeight - CARD_PADDING_Y,
                    CARD_PADDING_X,
                    CARD_PADDING_Y,
                    blackFill
            );
            return;
        }

        Paint paint;
        // All of these should be values from 0-2
        int colorIndex = card.getIntValue(0);
        int paintIndex = card.getIntValue(1);
        int shapeIndex = card.getIntValue(2);
        int number = card.getIntValue(3);

        paint = fillColors[paintIndex][colorIndex];

        // split the card into 5, vertically.
        int shapeHeight = cardHeight/5;

        // draw 1-3 shapes, depending on number
        number += 1; // now it's 1-3

        //get total height of area covered by shapes
        int totalArea = shapeHeight * number;
        //center that total area
        int startDrawingFrom = cardHeight/2 - totalArea/2;

        for (int i = 0; i < number; i++) {
            switch (shapeIndex) {
                case 0: {
                    canvas.drawOval(
                            SHAPE_PADDING_X,
                            startDrawingFrom + shapeHeight*i + SHAPE_PADDING_Y,
                            (cardWidth) - SHAPE_PADDING_X,
                            (startDrawingFrom + shapeHeight*(i+1) ) - SHAPE_PADDING_Y,
                            paint

                    );
                    break;
                }
                case 1: {
                    canvas.drawRoundRect(
                            SHAPE_PADDING_X,
                            startDrawingFrom + shapeHeight*i + SHAPE_PADDING_Y,
                            (cardWidth) - SHAPE_PADDING_X,
                            (startDrawingFrom + shapeHeight*(i+1) ) - SHAPE_PADDING_Y,
                            SHAPE_PADDING_X,
                            SHAPE_PADDING_Y,
                            paint
                    );
                    break;
                }
                case 2: {
                    drawDiamond(
                            canvas,
                            paint,
                            SHAPE_PADDING_X,
                            startDrawingFrom + shapeHeight*i + SHAPE_PADDING_Y,
                            (cardWidth) - SHAPE_PADDING_X * 2,
                            shapeHeight - SHAPE_PADDING_Y *2

                    );
                    break;
                }
            }
        }


        // Draw the outline in the correct color:
        // Black if normal, yellow if selected, grey if hinted.

        paint =
                selectedCards.contains(index) ? selectedPaint :
                        hintOn && game.hint.contains(index) ? hintPaint :
                                blackOutline;

        canvas.drawRoundRect(
                CARD_PADDING_X,
                CARD_PADDING_Y,
                (cardWidth) - CARD_PADDING_X,
                (cardHeight) - CARD_PADDING_Y,
                CARD_PADDING_X,
                CARD_PADDING_Y,
                paint
        );
    }


    public void clearCanvas() {
        mPath.reset();
        invalidate();
    }

    //override the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        // If touch down and touch up both within a card, the cards selection state is toggled.
        // When three cards are selected, send an event to the SetGame object.

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                Integer selection = mapTouchToCard((int)x, (int)y, width, height, boardOffset);
                if (selection == null || game.getBoard()[selection] == null) {
                    break;
                }
                // Shouldn't have to check this, since once we get to three we'll trigger a game event

                if (selectedCards.contains(selection)) {
                    selectedCards.remove(selection);
                } else if (selectedCards.size() < 3) {
                    selectedCards.add(selection);
                }

                if (selectedCards.size() == 3) {

                    Integer[] selectedCardsArray = new Integer[3];
                    selectedCards.toArray(selectedCardsArray);
                    game.SelectCards(
                            selectedCardsArray[0],
                            selectedCardsArray[1],
                            selectedCardsArray[2]
                    );

                    selectedCards.clear();
                }


                invalidate();
                break;
        }
        return true;
    }

    private Integer mapTouchToCard(int x, int y, int width, int height, int offset) {

        // Pretend there's no offset to the cards, by shifting the input left
        x-=offset;

        // If they did not touch a card, return null
        if (x < 0  || x > width) {
            return null;
        }

        // Draw the current cards
        int numCards = game.getCurrentBoardSize();
        int rows = 3;
        int columns = numCards / rows;

        int cardWidth = (width/columns);
        int cardHeight = (height/rows);

        int column  = x / cardWidth; //should round down.
        int row  = y / cardHeight; //should round down.

        return column*rows + row;
    }

    public void setGame(SetGame game) {
        this.game = game;
    }

    public void drawTriangle(Canvas canvas, Paint paint, int x, int y, int width, int height) {
        int halfWidth = width / 2;

        Path path = new Path();
        path.moveTo(x + halfWidth, y); // Top
        path.lineTo(x, y + height); // Bottom left
        path.lineTo(x + width, y + height); // Bottom right
        path.lineTo(x + halfWidth, y); // Back to Top
        path.close();

        canvas.drawPath(path, paint);
    }

    public void drawDiamond(Canvas canvas, Paint paint, int x, int y, int width, int height) {
        int halfWidth = width / 2;
        int halfHeight = height/ 2;

        Path path = new Path();
        path.moveTo(x+ halfWidth, y); // Top
        path.lineTo(x, y + halfHeight); // Left
        path.lineTo(x + halfWidth, y + height); // Bottom
        path.lineTo(x + width, y + halfHeight); // Right
        path.lineTo(x+ halfWidth, y); // Back to Top
        path.close();

        canvas.drawPath(path, paint);
    }

    // TO GET STRIPED
    private static Bitmap makeStripeMap(int color1, int color2) {
        Bitmap bm = Bitmap.createBitmap(40, 40, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bm);
        c.drawColor(color2);
        Paint p = new Paint();
        p.setColor(color1);
        c.drawRect(0, 0, 20, 40, p);
        return bm;
    }

    public void endGame(long duration) {
        gameOver = true;
        this.duration = duration;
    }
}