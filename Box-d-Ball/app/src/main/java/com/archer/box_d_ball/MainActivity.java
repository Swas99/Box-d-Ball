package com.archer.box_d_ball;

import android.animation.Animator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.method.CharacterPickerDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;
import android.widget.Toast;

import com.archer.box_d_ball.GameServices.BaseGameUtils;
import com.archer.box_d_ball.util.InAppBilling;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.lang.ref.WeakReference;
import java.util.List;

public class MainActivity
        extends Activity
        implements SensorEventListener
{
    private SensorManager mSensorManager;
    private Sensor mSensor;
    public MainActivity mContext;
    Game_Services objGameServices;

    public com.google.android.gms.ads.AdRequest AdRequest;
    public InterstitialAd mInterstitialAd;
    InAppBilling objInAppBilling;

    TextView tvScore;
    Box objBox;
    Ball objBall;
    HowToPlay objHowToPlay;
    DynamicTokens objDynamicsTokens;
    SoundManager objSoundManager;

    int SCREEN_WIDTH;
    int SCREEN_HEIGHT;

    float x_force;
    float y_force;
    long game_time;
    long Score;
    CountDownTimer score_calculator;

    public boolean adFreeVersion;

    int CURRENT_SCREEN;
    int GAME_STATE;
    int PREV_GAME_STATE;
    final static int GAME_STARTING = 100;
    final static int ALIVE_SCORED = 101;
    final static int ALIVE_PERFECT_SCORE = 102;
    final static int ALIVE_NO_SCORE = 103;
    final static int GAME_OVER = 104;

    int TWENTY_DIP;


    View.OnTouchListener touchListener;

    private void initialize_home_view_controls() {
        findViewById(R.id.tvAppName).animate().scaleY(1.2f);

        int ids[] = {R.id.btn_play,R.id.btn_top_scores,R.id.btn_help,R.id.btn_toggle_music,R.id.btn_toggle_sound};
        for (int id: ids)
            findViewById(id).setOnTouchListener(touchListener);
        
        objSoundManager.setMusicButtonBackground();
        objSoundManager.setSoundButtonBackground();

    }

    private void initialize_game_over_view_controls() {
        int ids[]= { R.id.btn_replay,R.id.btn_home,R.id.btn_top_scores_2,R.id.btn_share,R.id.btn_rate};

        for (int id: ids)
            findViewById(id).setOnTouchListener(touchListener);
    }
    private void initialize_pause_view_controls() {
        findViewById(R.id.btn_resume).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resume_game();
            }
        });
        findViewById(R.id.btn_home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remove_view(R.id.view_pause);
                load_view_to_main_container(R.layout.view_home);
                resetGameData();
                start_game();
            }
        });
    }

    public void resume_game()
    {
        remove_view(R.id.view_pause);
        objBox.isBoxAnimating=true;
        objBox.start_moving_d_box();
        mSensorManager.registerListener(mContext, mSensor, 99000);
        Score-=6;
        new CountDownTimer(1000,100) {

            @Override
            public void onTick(long millisUntilFinished) {
                objBall.setBaseForce(x_force, y_force);
            }
            @Override
            public void onFinish() {
                score_calculator.start();
            }
        }.start();
    }

    private void initialize_objects()
    {
        final WeakReference<MainActivity> m_context = new WeakReference<>(this);
        objGameServices = new Game_Services(m_context);
        mContext = this;
        GAME_STATE = 0;
        objBall = new Ball();
        objBall.initialize(this);
        objBox = new Box();
        objBox.initialize(this);

        objSoundManager = new SoundManager(m_context);
        objSoundManager.InitializeSoundPool();

        Point p = Helper.getWindowSize(mContext.getWindowManager().getDefaultDisplay());
        TWENTY_DIP = Helper.ConvertToPx(mContext, 20);
        SCREEN_HEIGHT=p.y - TWENTY_DIP;
        SCREEN_WIDTH=p.x - TWENTY_DIP;

        initializeAds();

        //region Initialize In App Billing
        objInAppBilling = new InAppBilling(this);
        objInAppBilling.initializeInAppBilling();
        //endregion

        tvScore = (TextView)findViewById(R.id.tvScore);
        tvScore.animate().scaleY(1.2f).scaleX(0.8f);
        findViewById(R.id.tvPause).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (isGameInProgress())
                {
                    score_calculator.cancel();
                    unregister_sensor_changed_listener();
                    objBox.stopBoxAnimation();
                    load_view_to_main_container(R.layout.view_pause);
                }
                return false;
            }
        });

        Score=0;
        score_calculator = new CountDownTimer(1990000000000l,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if(CURRENT_SCREEN == R.layout.activity_main)
                    calculateScore();

                game_time++;

                if(objDynamicsTokens==null)
                    objDynamicsTokens = new DynamicTokens(m_context);

                if(GAME_STATE==GAME_STARTING)
                    return;

                if(GAME_STATE!=GAME_OVER && CURRENT_SCREEN == R.layout.activity_main)
                    objDynamicsTokens.CreateToken();
            }

            @Override
            public void onFinish() { }
        };
    }

    public boolean isGameInProgress()
    {
        return ((GAME_STATE == ALIVE_NO_SCORE || GAME_STATE == ALIVE_PERFECT_SCORE || GAME_STATE == ALIVE_SCORED)
                && CURRENT_SCREEN==R.layout.activity_main);
    }
    public void calculateScore()
    {
        switch (GAME_STATE)
        {
            case ALIVE_PERFECT_SCORE:
                Score+=2;
                objSoundManager.Play(objSoundManager.PERFECT_SCORE);
                break;
            case ALIVE_SCORED:
                objSoundManager.Play(objSoundManager.SCORED);
                Score++;
                break;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvScore.setText(String.valueOf(Score));
            }
        });
    }

    static boolean isLoadingView;
    private void initialize_listeners()
    {
        touchListener = new View.OnTouchListener() {
            float startX;
            float startY;
            float CLICK_ACTION_THRESHOLD = 5;
            private boolean isAClick(float startX, float endX, float startY, float endY) {
                if(isLoadingView)
                    return false;

                float differenceX = Math.abs(startX - endX);
                float differenceY = Math.abs(startY - endY);
                return !(differenceX > CLICK_ACTION_THRESHOLD || differenceY > CLICK_ACTION_THRESHOLD);
            }
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                        v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(450).setInterpolator(new LinearInterpolator());
//                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        return true;
                    case MotionEvent.ACTION_UP: {
                        float endX = event.getX();
                        float endY = event.getY();
                        long CLICK_SCALE_UP_DURATION = 200;
                        long CLICK_SCALE_DOWN_DURATION = 200;

                        if (isAClick(startX, endX, startY, endY)) {
                            switch (v.getId())
                            {
                                case R.id.btn_play:
                                    if(CURRENT_SCREEN == R.layout.view_home)
                                        remove_view(R.id.view_home);
                                    else
                                        remove_view(R.id.view_game_over);
                                    mContext.findViewById(R.id.tvInstruction).setVisibility(View.VISIBLE);
                                    resetGameData();
                                    start_game();
                                    break;
                                case R.id.btn_home:
                                    remove_view(R.id.view_game_over);
                                    load_view_to_main_container(R.layout.view_home);
                                    resetGameData();
                                    start_game();
                                    break;
                                case R.id.btn_replay:
                                    remove_view(R.id.view_game_over);
                                    resetGameData();
                                    start_game();
                                    break;
                                case R.id.btn_rate:
                                    Helper.openPlayStorePage(new WeakReference<>(mContext));
                                    break;
                                case R.id.btn_help:
                                {
                                    remove_view(R.id.view_home);
                                    load_view_to_main_container(R.layout.view_help);
                                    objHowToPlay = new HowToPlay(mContext);
                                    objHowToPlay.StartHelp();
                                }
                                    break;
                                case R.id.btn_share:
                                {
                                    String msg = "OMG! I scored " + String.valueOf(Score) +
                                            " points in the #Box-d-Ball game on Android " +
//                                            "https://play.google.com/apps/testing/com.archer.box_d_ball";
                                    "https://play.google.com/store/apps/details?id=com.archer.box_d_ball";
                                    Helper.takeScreenShotAndShare(new WeakReference<>(mContext), msg);
                                    break;
                                }
                                case R.id.btn_toggle_sound:
                                    objSoundManager.toggleGameSounds();
                                    break;
                                case R.id.btn_toggle_music:
                                    objSoundManager.toggleBackgroundMusic();
                                    break;
                                case R.id.btn_top_scores:
                                case R.id.btn_top_scores_2:
                                    objGameServices.onShowLeaderBoardRequested(getString(R.string.leaderboard_top_score));
                                    break;

                            }
                            v.animate().scaleX(1.18f).scaleY(1.18f)
                                    .setDuration(CLICK_SCALE_UP_DURATION)
                                    .setInterpolator(new LinearInterpolator());
                            revertViewScalingAfterWaiting(v,CLICK_SCALE_DOWN_DURATION);
                        }
                        else
                        {
                            v.animate().scaleX(1).scaleY(1)
                                    .setDuration(CLICK_SCALE_DOWN_DURATION)
                                    .setInterpolator(new LinearInterpolator());
                        }
                    }
                    break;
                }
                return false;
            }
        };
    }

    public void revertViewScalingAfterWaiting(final View v, final long waitTime)
    {
        new CountDownTimer(waitTime,waitTime) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                v.animate().scaleX(1).scaleY(1).setDuration(waitTime).setInterpolator(new LinearInterpolator());
            }
        }.start();
    }

    private void load_view_to_main_container(int id)
    {
        isLoadingView=true;
        CURRENT_SCREEN=id;

        ViewGroup main_container = (ViewGroup)findViewById(R.id.main_container);

        LayoutInflater inflater = getLayoutInflater();
        View view_to_load = inflater.inflate(id, main_container, true);
        view_to_load.setAlpha(0f);
        view_to_load.animate().alpha(1f).setDuration(300);


        switch (id)
        {
            case R.layout.view_home:
                initialize_home_view_controls();
                break;
            case R.layout.view_game_over:
                initialize_game_over_view_controls();
                break;
            case R.layout.view_pause:
                initialize_pause_view_controls();
                break;
        }
        new CountDownTimer(300, 300) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                isLoadingView=false;
                DisplayBannerAd();
            }
        }.start();
    }


    private void remove_view(int id)
    {
        isLoadingView=true;
        CURRENT_SCREEN = R.layout.activity_main;
        final View view = findViewById(id);
        view.animate().alpha(0).setDuration(200).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ViewGroup main_container = (ViewGroup) findViewById(R.id.main_container);
                main_container.removeView(view);
                isLoadingView = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //region initialize accelerometer
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY) != null)
        {
            List<Sensor> gravSensors = mSensorManager.getSensorList(Sensor.TYPE_GRAVITY);

            mSensor = gravSensors.get(0);
            for(int i=0; i<gravSensors.size(); i++)
            {
                if ((gravSensors.get(i).getVendor().contains("Google Inc.")) )
                {
                    if(mSensor==null || mSensor.getVersion()< gravSensors.get(i).getVersion())
                    {
                        // Use the version 3 gravity sensor.
                        mSensor = gravSensors.get(i);
                    }
                }
            }

        }
        else
        {
            // Use the accelerometer.
            if (mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
                mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            } else {
                Toast.makeText(this, "Sorry, you cannot play this game", Toast.LENGTH_SHORT).show();
            }
        }
        //endregion

        initialize_objects();
        initialize_listeners();
        load_view_to_main_container(R.layout.view_home);
    }

    CountDownTimer start_game_timer;
    int game_start_timer_index;
    public void start_game_count_down()
    {
        if(start_game_timer==null)
        {
            start_game_timer= new CountDownTimer(3780, 630) {
                String text[] = { "","Ready", "Set", "Go!","","" };
                @Override
                public void onTick(long millisUntilFinished) {
                    if(game_start_timer_index == 0)
                        bring_ball_and_box_to_center();

                    if(game_start_timer_index==3)
                        ((TextView)mContext.findViewById(R.id.tvInstruction)).setText(getText(R.string.instruction_2));

                    if(game_start_timer_index == 3)
                        mSensorManager.registerListener(mContext, mSensor, 99000);

                    objBox.setText(text[game_start_timer_index++]);
                }

                @Override
                public void onFinish() {
                   go_start_game();
                }

            }.start();
        }
        else
        {
            start_game_timer.cancel();
            game_start_timer_index=0;
            start_game_timer.start();
        }
    }

    private void go_start_game()
    {
        game_start_timer_index = 0;
        objBox.setText("");
        objBox.isBoxAnimating=true;
        resetGameData();
        objBox.start_moving_d_box();
        objBall.setBaseForce(x_force, y_force);
        mContext.findViewById(R.id.tvInstruction).setVisibility(View.INVISIBLE);
        ((TextView)mContext.findViewById(R.id.tvInstruction)).setText(getText(R.string.instruction_1));
        score_calculator.cancel();
        score_calculator.start();
    }

    private void bring_ball_and_box_to_center() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                objBox.bringToCenter();
                objBall.bringToCenter();
            }
        });
    }

    private void unregister_sensor_changed_listener()
    {
        if(mSensorManager!=null)
        {
            mSensorManager.unregisterListener(this);
            mSensorManager.registerListener(null, mSensor, 1000000000);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(objGameServices!=null)
            objGameServices.connectToServices();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(objGameServices!=null)
            objGameServices.disconnectServices();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (objInAppBilling.mHelper.handleActivityResult(requestCode, resultCode, data))
            return;

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == objGameServices.RC_SIGN_IN) {
            objGameServices.mSignInClicked = false;
            objGameServices.mResolvingConnectionFailure = false;
            if (resultCode == RESULT_OK) {
                objGameServices.mGoogleApiClient.connect();
            } else {
                BaseGameUtils.showActivityResultError(this, requestCode, resultCode, R.string.signin_other_error);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(game_start_timer_index!=0 && CURRENT_SCREEN==R.layout.activity_main)
        {
            go_start_game();
            score_calculator.cancel();
            unregister_sensor_changed_listener();
            objBox.stopBoxAnimation();
            load_view_to_main_container(R.layout.view_pause);
        }
        else if(isGameInProgress())
            resume_game();

        if(objBall!=null && objBall.ball!=null)
            objBall.ball.setKeepScreenOn(true);

        if(objSoundManager!=null)
            objSoundManager.ResumeBackGroundMusic();
        if(objBox.isBoxAnimating)
            mSensorManager.registerListener(mContext, mSensor, 99000);

    }

    @Override
    protected void onPause() {
        super.onPause();


        if (isGameInProgress())
        {
            score_calculator.cancel();
            unregister_sensor_changed_listener();
            objBox.stopBoxAnimation();
            load_view_to_main_container(R.layout.view_pause);
        }
        else if(game_start_timer_index!=0)
            start_game_timer.cancel();


        if(objBall!=null && objBall.ball!=null)
            objBall.ball.setKeepScreenOn(false);

        if(objSoundManager!=null)
            objSoundManager.StopBackGroundMusic();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (objInAppBilling.mHelper != null)
                objInAppBilling.mHelper.dispose();
            objInAppBilling.mHelper = null;
        }
        catch (Exception e) {/*do nothing*/}

        CollectGarbage();
    }

    public void CollectGarbage() {
        Runnable myRunnable = new Runnable(){
            public void run(){
                System.gc();
            }
        };
        Thread thread = new Thread(myRunnable);
        thread.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            onBackPress();
        }
        else
            super.onKeyDown(keyCode, event);

        return false;
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        x_force = event.values[0];
        y_force = event.values[1];

        if(GAME_STATE==GAME_STARTING)
            return;

        if(GAME_STATE==GAME_OVER && CURRENT_SCREEN == R.layout.activity_main)
        {
            GAME_STATE=GAME_STARTING;
            if(game_time>99 && !adFreeVersion)
            {
                ShowRemoveAdDialog();
            }
            else
                game_over();

            return;
        }
        detect_overlap();

        float delta_x = objBall.getDeltaX(x_force);
        float delta_y = objBall.getDeltaY(y_force);
        objBall.moveBall(delta_x, delta_y);


    }

    public void detect_overlap()
    {
        PREV_GAME_STATE = GAME_STATE;

        float ball_x = objBall.get_x();
        float ball_y = objBall.get_y();

        float box_x = objBox.get_x();
        float box_y = objBox.get_y();


        float deltaX = Math.abs(ball_x - box_x);
        float deltaY = Math.abs(ball_y - box_y);

        if(deltaX>(objBox.width+36) || deltaY>(objBox.height+36) )
        {
            GAME_STATE=GAME_OVER;
            objBox.setBoxBackGround();
            return;
        }

        float overlap_w,overlap_h;
        if(box_x<ball_x)
            overlap_w = objBox.width - deltaX;
        else
            overlap_w = objBall.width - deltaX;

        if(box_y<ball_y)
            overlap_h = objBox.height - deltaY;
        else
            overlap_h = objBall.height - deltaY;

        final float overlap_area = overlap_h*overlap_w;
        final float percentage_overlapped = overlap_area/objBall.AREA_OF_BALL;

        if(percentage_overlapped>.98)
            GAME_STATE=ALIVE_PERFECT_SCORE;
        else if(percentage_overlapped>.72)
            GAME_STATE=ALIVE_SCORED;
        else
            GAME_STATE=ALIVE_NO_SCORE;


        objBox.setBoxBackGround();
    }
    public void start_game()
    {
        if(objDynamicsTokens==null)
            objDynamicsTokens= new DynamicTokens(new WeakReference<>(mContext));

        objDynamicsTokens.game_time=0;
        objDynamicsTokens.flushAllTokensFromScreen();

        objBall.ball.setBackgroundResource(R.drawable.black_ball);
        objBox.box.setBackgroundResource(R.drawable.background_box_perfect_score);
        tvScore.setText("0");

        GAME_STATE = GAME_STARTING;

        unregister_sensor_changed_listener();
        objBall.max_ball_velocity=0;
        objBox.stopBoxAnimation();

        start_game_count_down();
    }
    public void resetGameData()
    {
        GAME_STATE=ALIVE_NO_SCORE;
        Score=0;

        objBall.resetBallSpeed();
        objBox.resetBoxSpeed();
    }

    public void game_over()
    {
        objSoundManager.Play(objSoundManager.GAME_OVER);
        objBall.ball.setBackgroundResource(R.drawable.img_broken_black_ball);

        score_calculator.cancel();
        unregister_sensor_changed_listener();
        objBox.stopBoxAnimation();


        new CountDownTimer(540, 540) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                load_view_to_main_container(R.layout.view_game_over);
                ((TextView)findViewById(R.id.tvFinalScore)).setText(String.valueOf(Score));
                updateBestScore();
            }
        }.start();
    }

    private void updateBestScore()
    {
        WeakReference<MainActivity> m_context = new WeakReference<>(this);
        long best_score = Long.parseLong(Helper.readFromFile(m_context, Helper.TOP_SCORE_FILE));
        if(Score>best_score)
        {
            Helper.writeToFile(m_context, Helper.TOP_SCORE_FILE, String.valueOf(Score));
            ((TextView)findViewById(R.id.tvBestScore)).setText(String.valueOf(Score));
            findViewById(R.id.tvBestScore_star).setVisibility(View.VISIBLE);
            findViewById(R.id.tvScore_star).setVisibility(View.INVISIBLE);
        }
        else
            ((TextView)findViewById(R.id.tvBestScore)).setText(String.valueOf(best_score));

        objGameServices.submitScore(getString(R.string.leaderboard_top_score), Score);
    }

    public void onBackPress()
    {
        switch (CURRENT_SCREEN)
        {
            case R.layout.view_help:
            {
                objHowToPlay.release_help_resources();
                remove_view(R.id.view_help);
                load_view_to_main_container(R.layout.view_home);
                break;
            }
            case R.layout.view_game_over:
                remove_view(R.id.view_game_over);
                load_view_to_main_container(R.layout.view_home);
                resetGameData();
                start_game();
                break;
            case R.layout.activity_main:
                load_view_to_main_container(R.layout.view_home);
                break;
            case R.layout.view_home:
                unregister_sensor_changed_listener();
                finish();
                break;
        }
    }

    //region Ads Logic
    private void initializeAds()
    {
//        AdBuddiz.setPublisherKey(getString(R.string.adbuddiz_ad_id)); // See 3.
//        AdBuddiz.cacheAds(this);
//        AdBuddiz.RewardedVideo.fetch(this);

        //region AdMob
        //Initialize AdRequest for Banner Ads
        AdRequest = new AdRequest.Builder().build();

        //Initialize Interstitial Ads
        mInterstitialAd = new InterstitialAd(getApplication());
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_unit_id));
        requestNewInterstitial();
        //My RedMi = 865622026474424

        if(!adFreeVersion)
        {
            final AdView mAdView = (AdView) findViewById(R.id.adView_main);
            if(mAdView==null)
                return;

            mAdView.loadAd(AdRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mAdView.setVisibility(View.VISIBLE);
                }
            });
        }

    }
    public void requestNewInterstitial() {
        if (!mInterstitialAd.isLoading() && !mInterstitialAd.isLoaded())
        {
            AdRequest adRequest = new AdRequest.Builder().build();
            mInterstitialAd.loadAd(adRequest);
        }
    }

    public void DisplayInterstitialAd()
    {
        if (mInterstitialAd!=null && mInterstitialAd.isLoaded())
        {
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    requestNewInterstitial();
                    game_over();
                    game_time=0;
                }
            });
            mInterstitialAd.show();
        }
        else
        {
            requestNewInterstitial();
            game_over();
        }
    }

    public void DisplayBannerAd()
    {
        if(!adFreeVersion)
        {
            final AdView mAdView = (AdView) findViewById(R.id.adView);
            if(mAdView==null)
                return;

            mAdView.loadAd(AdRequest);
            mAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                    mAdView.setVisibility(View.VISIBLE);
                }
            });
        }
    }

    private void ShowRemoveAdDialog() {

        LayoutInflater inflater = mContext.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_remove_ads, null, true);
        mContext.CURRENT_SCREEN = R.layout.dialog_remove_ads;

        final Dialog dialog = new AlertDialog.Builder(mContext).show();
        dialog.setCancelable(false);
        dialog.setContentView(view);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        View v2 = findViewById(R.id.main_container);
        lp.width = v2.getMeasuredWidth() - Helper.ConvertToPx(mContext, 40); //WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        objInAppBilling.setRemoveAdsPrice(view.findViewById(R.id.tvRemoveAdsPrice));

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if ((keyCode == KeyEvent.KEYCODE_BACK))
                {
                    DisplayInterstitialAd();
                    dialog.dismiss();
                }
                return false;
            }
        });
        //region Click Listener for remove ads dialog
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId())
                {
                    case R.id.btnShowAd:
                        DisplayInterstitialAd();
                        break;
                    case R.id.btnRemoveAds:
                        game_over();
                        String SKU_REMOVE_ADS = "sku_remove_ads";
                        mContext.objInAppBilling.LaunchPurchaseFlow(SKU_REMOVE_ADS);
                        break;
                }
                dialog.dismiss();
            }
        };
        //endregion

        view.findViewById(R.id.btnShowAd).setOnClickListener(listener);
        view.findViewById(R.id.btnRemoveAds).setOnClickListener(listener);
        dialog.show();
    }

    public void hideMainAdView()
    {
        AdView mAdView = (AdView) findViewById(R.id.adView_main);
        if(mAdView==null)
            return;
        mAdView.setVisibility(View.GONE);
    }


    //endregion


}
