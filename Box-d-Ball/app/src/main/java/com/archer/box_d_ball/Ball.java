package com.archer.box_d_ball;


import android.graphics.Point;
import android.os.Build;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Random;

public class Ball {

    MainActivity mContext;
    View ball;
    float width;
    float height;
    float AREA_OF_BALL;

    float x_lim;
    float y_lim;
    float base_x_force;
    float base_y_force;
    float default_x;
    float default_y;
    float force_limit = 2.7F;

    float max_ball_velocity;
    long ballAnimation_duration;
    float INITIAL_MAX_BALL_VELOCITY=63; //Reduce this value to start slower
    long INITIAL_ANIMATION_DURATION=180; //!DO NOT change this value.  Change 'INITIAL_MAX_BALL_VELOCITY' to alter speed of ball

    boolean isDrawnOnScreen;

    public void initialize(MainActivity m_context)
    {
        mContext = m_context;
        base_x_force=0;
        base_y_force=3f;

        ball = mContext.findViewById(R.id.ball);
        max_ball_velocity=INITIAL_MAX_BALL_VELOCITY;
        ballAnimation_duration = INITIAL_ANIMATION_DURATION; // -> 80000/1000 + 100;

        //region compute ball dimensions
        final ViewTreeObserver ball_observer = ball.getViewTreeObserver();
        ball_observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                width = ball.getWidth();
                height = ball.getHeight();
                AREA_OF_BALL = height * width;
                default_x = ball.getX();
                default_y = ball.getY();
                x_lim = mContext.SCREEN_WIDTH-width;
                y_lim = mContext.SCREEN_HEIGHT-height;

                isDrawnOnScreen=true;
                if(mContext.objBox.isDrawnOnScreen)
                    mContext.start_game();

                if (Build.VERSION.SDK_INT < 16)
                    ball.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                else
                    ball.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        //endregion
    }
    public float get_next_x_position(float x_force)
    {
        float xForce = x_force;
        xForce-=base_x_force;
        int dir = -1;
        if(xForce<0)
        {
            dir*=-1;
            xForce*=-1;
        }
        if(xForce>force_limit)
            xForce=force_limit;

        return max_ball_velocity*xForce/force_limit*dir;
    }

    public float get_next_y_position(float y_force)
    {
        float yForce = y_force;
        yForce-=base_y_force;
        int dir = 1;
        if(yForce<0)
        {
            dir*=-1;
            yForce*=-1;
        }
        if(yForce>force_limit)
            yForce=force_limit;

        return max_ball_velocity*yForce/force_limit*dir;
    }

    public void setBaseForce(float x_force,float y_force)
    {
        float max_force=9.8f;
        if(max_force-Math.abs(x_force)<force_limit)
        {
            base_x_force=max_force-force_limit;
            base_x_force*=Math.signum(x_force);
        }
        else
            base_x_force=x_force;

        if(max_force-Math.abs(y_force)<force_limit)
        {
            base_y_force=max_force-force_limit;
            base_y_force*=Math.signum(y_force);
        }
        else
            base_y_force=y_force;
    }

    public float get_x()
    {
        if(ball == null)
            return 0;
        return ball.getX();
    }

    public float get_y()
    {
        if(ball == null)
            return 0;
        return ball.getY();
    }


    public float getDeltaX(float x_force)
    {
        float delta_x = get_next_x_position(x_force);

        if(get_x()+delta_x>x_lim)
            delta_x=x_lim-get_x();
        else if(get_x()+delta_x<mContext.TWENTY_DIP)
            delta_x=mContext.TWENTY_DIP-get_x();

        return delta_x;
    }

    public float getDeltaY(float y_force)
    {
        float delta_y = get_next_y_position(y_force);

        if(get_y()+delta_y>y_lim)
            delta_y=y_lim-get_y();
        else if(get_y()+delta_y<mContext.TWENTY_DIP)
            delta_y=mContext.TWENTY_DIP-get_y();

        return delta_y;
    }

    public void moveBall(float deltaX,float deltaY)
    {
        ball.animate().xBy(deltaX).yBy(deltaY)
                .setInterpolator(new LinearInterpolator())
                .setDuration(ballAnimation_duration).start();
//        Random objRandom = new Random();
//        ball.animate()
//                .x(mContext.objBox.box.getX() + objRandom.nextInt(45))
//                .y(mContext.objBox.box.getY() + 22)
//                .setInterpolator(new LinearInterpolator())
//                .setDuration(ballAnimation_duration).start();
    }

    void bringToCenter()
    {
        ball.animate().x(default_x).y(default_y).setDuration(630);
    }

    public void resetBallSpeed()
    {
        max_ball_velocity=INITIAL_MAX_BALL_VELOCITY;
        ballAnimation_duration = INITIAL_ANIMATION_DURATION; // -> 80000/1000 + 100;
    }

    public void increaseBallVelocity(float x)
    {
        max_ball_velocity+=x;
    }
}
