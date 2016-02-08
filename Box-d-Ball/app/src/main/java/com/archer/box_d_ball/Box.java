package com.archer.box_d_ball;


import android.animation.Animator;
import android.os.Build;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import java.util.Random;

public class Box {
    MainActivity mContext;
    View box;
    float width;
    float height;
    float default_x;
    float default_y;
    float AREA_OF_BOX;
    float lim_x;
    float lim_y;

    long boxAnimation_Duration;
    ViewPropertyAnimator box_animator;
    boolean isBoxAnimating;
    int INITIAL_ANIMATION_DURATION = 2700; //Increase this value to start slower
    int MIN_ANIMATION_DURATION = 702; //Decrease this value to increase end speed

    boolean isDrawnOnScreen;


    final static int GAME_STARTING = 100;
    final static int ALIVE_SCORED = 101;
    final static int ALIVE_PERFECT_SCORE = 102;
    final static int ALIVE_NO_SCORE = 103;
    final static int GAME_OVER = 104;

    public void initialize(MainActivity m_context)
    {
        mContext = m_context;
        box = mContext.findViewById(R.id.box);
        boxAnimation_Duration=INITIAL_ANIMATION_DURATION;

        //region compute box dimensions
        final ViewTreeObserver box_observer = box.getViewTreeObserver();
        box_observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                width = box.getWidth();
                height = box.getHeight();
                AREA_OF_BOX = width * height;
                lim_x = mContext.SCREEN_WIDTH - width;
                lim_y = mContext.SCREEN_HEIGHT - height;

                default_x = box.getX();
                default_y = box.getY();
                isDrawnOnScreen=true;

                if(mContext.objBall.isDrawnOnScreen)
                    mContext.start_game();

                if (Build.VERSION.SDK_INT < 16)
                    box.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                else
                    box.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        //endregion
    }

    public void stopBoxAnimation()
    {
        isBoxAnimating=false;
        if(box_animator!=null)
            box_animator.cancel();
    }
    public float get_x()
    {
        if(box == null)
            return 0;
        return box.getX();
    }

    public float get_y()
    {
        if(box == null)
            return 0;
        return box.getY();
    }

    public void start_moving_d_box() {

        box_animator = box.animate().xBy(0).yBy(0);
        box_animator.setDuration(boxAnimation_Duration);
        box_animator.setInterpolator(new LinearInterpolator());

        box_animator.setListener(new Animator.AnimatorListener() {
            Random objRandom = new Random();
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(!isBoxAnimating)
                {
                    return;
                }

                if (boxAnimation_Duration > MIN_ANIMATION_DURATION) {
                    boxAnimation_Duration -= 4;

                    mContext.objBall.increaseBallVelocity(2);
                }

                box_animator.setDuration(boxAnimation_Duration);
                box_driver();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            int x_pace = 200;
            int y_pace = 250;
            int x_dir = 1;
            int y_dir = 1;

            private void box_driver() {
                float x = box.getX();
                float y = box.getY();

                if (objRandom.nextBoolean())
                    x_dir *= -1;
                if (objRandom.nextBoolean())
                    y_dir *= -1;

                int x_translation = (objRandom.nextInt(50) + x_pace) * x_dir;
                int y_translation = (objRandom.nextInt(50) + y_pace) * y_dir;

                if (x + x_translation > lim_x || x + x_translation < mContext.TWENTY_DIP)
                    x_translation *= -1;
                if (y + y_translation > lim_y || y + y_translation < mContext.TWENTY_DIP)
                    y_translation *= -1;

                box.animate().xBy(x_translation).yBy(y_translation);
            }
        });


    }


    public void resetBoxSpeed()
    {
        boxAnimation_Duration=INITIAL_ANIMATION_DURATION;
    }

    void bringToCenter()
    {
        box.animate().x(default_x).y(default_y).setDuration(630);
    }
    public void setBoxBackGround() {
        if(mContext.PREV_GAME_STATE==mContext.GAME_STATE)
            return;

        switch (mContext.GAME_STATE)
        {
            case GAME_OVER:
                box.setBackgroundResource(R.drawable.background_box_game_over);
                break;
            case ALIVE_NO_SCORE:
                box.setBackgroundResource(R.drawable.background_box_no_score);
                break;
            case ALIVE_PERFECT_SCORE:
                box.setBackgroundResource(R.drawable.background_box_perfect_score);
                break;
            case ALIVE_SCORED:
                box.setBackgroundResource(R.drawable.background_box_score);
                break;
        }

    }

    void setText(final String x)
    {
        mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ((TextView) box).setText(x);
                }
            });
    }
}
