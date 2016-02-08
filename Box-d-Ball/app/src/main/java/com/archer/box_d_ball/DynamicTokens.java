package com.archer.box_d_ball;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Swastik on 13-01-2016.
 */
public class DynamicTokens {

    int game_time;
    MainActivity mContext;
    Random objRnd;
    float min_x,min_y,max_x,max_y;
    RelativeLayout mainContainer;
    List<View> activeTokens = new ArrayList<>();

    HeartToken objHeartToken;
    PlusOneToken objPlusOneToken = new PlusOneToken();
    PlusTwoToken objPlusTwoToken = new PlusTwoToken();
    PlusThreeToken objPlusThreeToken = new PlusThreeToken();
    MinusOneToken objMinusOneToken = new MinusOneToken();
    MinusTwoToken objMinusTwoToken = new MinusTwoToken();
    MinusThreeToken objMinusThreeToken = new MinusThreeToken();

    public DynamicTokens(WeakReference<MainActivity> m_context)
    {
        objRnd = new Random();
        mContext = m_context.get();
        min_x = Math.max(mContext.objBall.width + 20, mContext.objBox.width + 20);
        min_y = Math.max(mContext.objBall.height + 20, mContext.objBox.height + 20);
        max_x = Math.max(mContext.objBall.x_lim - min_x, mContext.objBox.lim_x - min_x);
        max_y = Math.max(mContext.objBall.y_lim - min_y,mContext.objBox.lim_y - min_y);

        mainContainer = (RelativeLayout)mContext.findViewById(R.id.main_container);
    }

    public void CreateToken()
    {
        game_time++;

        if(game_time%9==0)
        {
            objHeartToken = new HeartToken();
            objHeartToken.CreateHeartToken(6000, 5);
        }

        if(game_time%7==0)
            objPlusOneToken.CreatePlusOneToken(6000);

        if(game_time%15==0)
            objPlusTwoToken.CreatePlusTwoToken(4000);

        if(game_time%23==0)
            objPlusThreeToken.CreatePlusThreeToken(2000);

        if(game_time%13==0)
            objMinusOneToken.CreateMinusOneToken(6000);

        if(game_time>15)
        {
            if(game_time%11==0)
            {
                objHeartToken = new HeartToken();
                objHeartToken.CreateHeartToken(5000, 4);
            }
        }

        if(game_time>30)
        {

            if(game_time%6==0)
            {
                objHeartToken = new HeartToken();
                objHeartToken.CreateHeartToken(5000, 4);
            }

            if(game_time%23==0)
            {
                objHeartToken = new HeartToken();
                objHeartToken.CreateHeartToken(5000, 4);
            }

            if(game_time%37==0)
            {
                objHeartToken = new HeartToken();
                objHeartToken.CreateHeartToken(4000, 3);
            }

            if(game_time%11==0)
                objPlusOneToken.CreatePlusOneToken(6000);

            if(game_time%19==0)
                objPlusTwoToken.CreatePlusTwoToken(4000);

            if(game_time%29==0)
                objPlusThreeToken.CreatePlusThreeToken(2000);

            if(game_time%7==0)
                objMinusOneToken.CreateMinusOneToken(8000);

            if(game_time%17==0)
                objMinusTwoToken.CreateMinusTwoToken(6000);

            if(game_time%23==0)
                objMinusThreeToken.CreateMinusThreeToken(6000);

        }

        if(game_time>60)
        {
            if(game_time%29==0)
            {
                objHeartToken = new HeartToken();
                objHeartToken.CreateHeartToken(4000, 3);
            }

            if(game_time%51==0)
            {
                objHeartToken = new HeartToken();
                objHeartToken.CreateHeartToken(3000, 2);
            }


            if(game_time%7==0)
            {
                objHeartToken = new HeartToken();
                objHeartToken.CreateHeartToken(4000, 3);
            }

            if(game_time%13==0)
                objPlusOneToken.CreatePlusOneToken(4000);

            if(game_time%23==0)
                objPlusTwoToken.CreatePlusTwoToken(3000);

            if(game_time%31==0)
                objPlusThreeToken.CreatePlusThreeToken(2000);

            if(game_time%11==0)
                objMinusOneToken.CreateMinusOneToken(10000);

            if(game_time%17==0)
                objMinusTwoToken.CreateMinusTwoToken(9000);

            if(game_time%19==0)
                objMinusThreeToken.CreateMinusThreeToken(8000);
        }

        if(game_time>70)
        {
            if(game_time%5==0)
            {
                objHeartToken = new HeartToken();
                objHeartToken.CreateHeartToken(4000, 3);
            }
        }
        if(game_time>80)
        {
            if(game_time%4==0)
            {
                objHeartToken = new HeartToken();
                objHeartToken.CreateHeartToken(4000, 3);
            }
        }

        if(game_time>90)
        {
            if(game_time%3==0)
            {
                objHeartToken = new HeartToken();
                objHeartToken.CreateHeartToken(4000, 3);
            }
        }

        if(game_time>110)
        {
            if(game_time%5==0)
            {
                objHeartToken = new HeartToken();
                objHeartToken.CreateHeartToken(3000, 2);
            }
        }
    }

    private View getTokenView(int background_res,String text)
    {
        RelativeLayout cell = new RelativeLayout(mContext);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        cell.setLayoutParams(layoutParams);

        RelativeLayout.LayoutParams layoutParams_r = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams_r.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);


        TextView textView = new TextView(mContext);
        textView.setLayoutParams(layoutParams_r);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.WHITE);
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setBackgroundResource(background_res);

        cell.addView(textView);
        cell.setX(objRnd.nextInt((int) max_x) + min_x);
        cell.setY(objRnd.nextInt((int) max_y) + min_y);

        return cell;
    }

    class HeartToken
    {
        boolean flag;
        public void CreateHeartToken(long milliSecondsTillTimeOut, final int duration)
        {
            final TextView textView = new TextView(mContext);
            textView.setWidth(Helper.ConvertToPx(mContext, 45));
            textView.setHeight(Helper.ConvertToPx(mContext, 45));
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(Color.BLACK);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            textView.setBackgroundResource(R.drawable.img_heart);
            textView.setX(objRnd.nextInt((int) max_x) + min_x);
            textView.setY(objRnd.nextInt((int) max_y) + min_y);

            mainContainer.addView(textView);
            activeTokens.add(textView);
            final CountDownTimer Heart_countDownTimer = new CountDownTimer(milliSecondsTillTimeOut,1000) {
                int text = duration;
                @Override
                public void onTick(long millisUntilFinished) {
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            textView.setText(String.valueOf(text--));
                            textView.setScaleX(.9f);
                            textView.setScaleY(.9f);
                            textView.animate().scaleX(1.01f).scaleY(1.01f);
                        }
                    });
                }

                @Override
                public void onFinish() {
                    if(!flag && mContext.CURRENT_SCREEN == R.layout.activity_main)
                    {
                        if(activeTokens.contains(textView))
                            mContext.game_over();
                    }
                    if(!flag)
                        removeToken(textView);

                }
            };

            textView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(mContext.CURRENT_SCREEN == R.layout.activity_main)
                    {
                        mContext.objSoundManager.Play(mContext.objSoundManager.HEART_TAP);
                        mContext.Score += 1;
                    }
                    flag = true;
                    removeToken(textView);
                    Heart_countDownTimer.cancel();
                    return false;
                }
            });
            Heart_countDownTimer.start();
        }
    }

    class PlusOneToken
    {
        public void CreatePlusOneToken(long milliSecondsTillTimeOut)
        {
            final View tokenView = getTokenView(R.drawable.background_blue_circle,"+1");
            mainContainer.addView(tokenView);
            activeTokens.add(tokenView);

            final CountDownTimer objCountDownTimer = new CountDownTimer(milliSecondsTillTimeOut,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    removeToken(tokenView);
                }
            };
            tokenView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(mContext.CURRENT_SCREEN==R.layout.activity_main)
                    {
                        mContext.objSoundManager.Play(mContext.objSoundManager.PLUS_ONE);
                        mContext.Score += 1;
                    }

                    removeToken(tokenView);
                    objCountDownTimer.cancel();
                    return false;
                }
            });
            objCountDownTimer.start();
        }
    }

    class PlusTwoToken
    {
        public void CreatePlusTwoToken(long milliSecondsTillTimeOut)
        {
            final View tokenView = getTokenView(R.drawable.background_red_circle,"+2");
            mainContainer.addView(tokenView);
            activeTokens.add(tokenView);

            final CountDownTimer objCountDownTimer = new CountDownTimer(milliSecondsTillTimeOut,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    removeToken(tokenView);
                }
            };
            tokenView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(mContext.CURRENT_SCREEN==R.layout.activity_main)
                    {
                        mContext.objSoundManager.Play(mContext.objSoundManager.PLUS_TWO);
                        mContext.Score += 2;
                    }
                    removeToken(tokenView);
                    objCountDownTimer.cancel();
                    return false;
                }
            });
            objCountDownTimer.start();
        }
    }

    class PlusThreeToken
    {
        public void CreatePlusThreeToken(long milliSecondsTillTimeOut)
        {
            final View tokenView = getTokenView(R.drawable.background_black_circle,"+3");
            mainContainer.addView(tokenView);
            activeTokens.add(tokenView);

            final CountDownTimer objCountDownTimer = new CountDownTimer(milliSecondsTillTimeOut,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    removeToken(tokenView);
                }
            };
            tokenView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(mContext.CURRENT_SCREEN==R.layout.activity_main)
                    {
                        mContext.objSoundManager.Play(mContext.objSoundManager.PLUS_THREE);
                        mContext.Score += 3;
                    }

                    removeToken(tokenView);
                    objCountDownTimer.cancel();
                    return false;
                }
            });
            objCountDownTimer.start();
        }
    }

    class MinusOneToken
    {
        public void CreateMinusOneToken(long milliSecondsTillTimeOut)
        {
            final View tokenView = getTokenView(R.drawable.background_blue_circle,"-1");
            mainContainer.addView(tokenView);
            activeTokens.add(tokenView);

            final CountDownTimer objCountDownTimer = new CountDownTimer(milliSecondsTillTimeOut,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    removeToken(tokenView);
                }
            };

            tokenView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(mContext.CURRENT_SCREEN==R.layout.activity_main)
                    {
                        mContext.objSoundManager.Play(mContext.objSoundManager.MINUS_ONE);
                        mContext.Score -= 1;
                    }

                    removeToken(tokenView);
                    objCountDownTimer.cancel();
                    return false;
                }
            });
            objCountDownTimer.start();
        }
    }

    class MinusTwoToken
    {
        public void CreateMinusTwoToken(long milliSecondsTillTimeOut) {
            final View tokenView = getTokenView(R.drawable.background_red_circle,"-2");
            mainContainer.addView(tokenView);
            activeTokens.add(tokenView);

            final CountDownTimer objCountDownTimer = new CountDownTimer(milliSecondsTillTimeOut,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    removeToken(tokenView);
                }
            };

            tokenView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(mContext.CURRENT_SCREEN==R.layout.activity_main)
                    {
                        mContext.objSoundManager.Play(mContext.objSoundManager.MINUS_TWO);
                        mContext.Score -= 2;
                    }

                    removeToken(tokenView);
                    objCountDownTimer.cancel();
                    return false;
                }
            });
            objCountDownTimer.start();
        }
    }

    class MinusThreeToken
    {
        public void CreateMinusThreeToken(long milliSecondsTillTimeOut)
        {
            final View tokenView = getTokenView(R.drawable.background_black_circle,"-3");
            mainContainer.addView(tokenView);
            activeTokens.add(tokenView);

            final CountDownTimer objCountDownTimer = new CountDownTimer(milliSecondsTillTimeOut,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                }

                @Override
                public void onFinish() {
                    removeToken(tokenView);
                }
            };

            tokenView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(mContext.CURRENT_SCREEN==R.layout.activity_main)
                    {
                        mContext.objSoundManager.Play(mContext.objSoundManager.MINUS_THREE);
                        mContext.Score -= 3;
                    }
                    removeToken(tokenView);
                    objCountDownTimer.cancel();
                    return false;
                }
            });
            objCountDownTimer.start();
        }
    }

    public void removeToken(View v)
    {
        if(mainContainer.indexOfChild(v)>=0)
            mainContainer.removeView(v);
    }

    public void flushAllTokensFromScreen()
    {
        for (View v : activeTokens)
            removeToken(v);
        activeTokens.clear();
    }

}
