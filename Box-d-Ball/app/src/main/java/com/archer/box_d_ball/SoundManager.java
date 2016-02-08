package com.archer.box_d_ball;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by Swastik on 22-12-2015.
 */
public class SoundManager{

    MainActivity mContext;
    SoundPool soundPool;
    MediaPlayer player;
    int PERFECT_SCORE;
    int SCORED;
    int GAME_OVER;
    int HEART_TAP;
    int PLUS_ONE;
    int PLUS_TWO;
    int PLUS_THREE;
    int MINUS_ONE;
    int MINUS_TWO;
    int MINUS_THREE;


    boolean muteGameSounds;
    boolean muteBackgroundMusic;
    float MAX_BACKGROUND_MUSIC_VOLUME = 0.53f;
    float MAX_GAME_SOUND_VOLUME = 0.99f;


    public SoundManager(WeakReference<MainActivity> m_context)
    {
        mContext=m_context.get();
    }
    public  void Play(int id)
    {
        float vol_adjustment = 0;
        if(id==SCORED || id == PERFECT_SCORE)
            vol_adjustment=-.5f;

        if(!muteGameSounds && mContext.CURRENT_SCREEN==R.layout.activity_main)
            soundPool.play(id,
                    MAX_GAME_SOUND_VOLUME+vol_adjustment,
                    MAX_GAME_SOUND_VOLUME+vol_adjustment,
                    1,0,1);
    }

    public void InitializeSoundPool()
    {
        loadDefaultSettings();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
        {
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(attributes)
                    .build();
        }
        else {
            soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        }
        GAME_OVER = soundPool.load(mContext, R.raw.glass_breaking, 1);
        HEART_TAP = soundPool.load(mContext, R.raw.heart_tap, 1);
        PLUS_ONE = soundPool.load(mContext, R.raw.plus_one, 1);
        PLUS_TWO = soundPool.load(mContext, R.raw.plus_two, 1);
        PLUS_THREE = soundPool.load(mContext, R.raw.plus_three, 1);
        MINUS_ONE = soundPool.load(mContext, R.raw.minus_one, 1);
        MINUS_TWO = soundPool.load(mContext, R.raw.minus_two, 1);
        MINUS_THREE = soundPool.load(mContext, R.raw.minus_three, 1);

        SCORED = soundPool.load(mContext, R.raw.scored, 1);
        PERFECT_SCORE = soundPool.load(mContext, R.raw.perfect_score, 1);
//        SCORED = PLUS_ONE;
//        PERFECT_SCORE = PLUS_TWO;
    }

    private void loadDefaultSettings() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        muteBackgroundMusic = preferences.getBoolean(String.valueOf(Helper.BACKGROUND_MUSIC), false);
        muteGameSounds = preferences.getBoolean(String.valueOf(Helper.GAME_SOUND), false);
    }

    private void StoreData(String tag,boolean value)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(tag,value);
        editor.apply();
    }

    public void toggleBackgroundMusic()
    {
        muteBackgroundMusic = !muteBackgroundMusic;
        StoreData(String.valueOf(Helper.BACKGROUND_MUSIC), muteBackgroundMusic);
        if(muteBackgroundMusic)
            player.setVolume(0, 0);
        else
            player.setVolume(MAX_BACKGROUND_MUSIC_VOLUME, MAX_BACKGROUND_MUSIC_VOLUME);

        setMusicButtonBackground();
    }
    public void setMusicButtonBackground()
    {
        if(muteBackgroundMusic)
            mContext.findViewById(R.id.btn_toggle_music).setBackgroundResource(R.drawable.btn_music_off);
        else
            mContext.findViewById(R.id.btn_toggle_music).setBackgroundResource(R.drawable.btn_music_on);


    }

    public void toggleGameSounds()
    {
        muteGameSounds=!muteGameSounds;
        StoreData(String.valueOf(Helper.GAME_SOUND), muteGameSounds);

        setSoundButtonBackground();
    }

    public void setSoundButtonBackground()
    {
        if(muteGameSounds)
            mContext.findViewById(R.id.btn_toggle_sound).setBackgroundResource(R.drawable.btn_sound_off);
        else
            mContext.findViewById(R.id.btn_toggle_sound).setBackgroundResource(R.drawable.btn_sound_on);
    }
    public void ResumeBackGroundMusic()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(player==null)
                {
                    player = MediaPlayer.create(mContext, R.raw.background);
                    player.setLooping(true); // Set looping
                    player.start();
                }
                if (muteBackgroundMusic)
                    player.setVolume(0,0);
                else
                    player.setVolume(MAX_BACKGROUND_MUSIC_VOLUME, MAX_BACKGROUND_MUSIC_VOLUME);
            }
        }).start();
    }

    public void StopBackGroundMusic()
    {
        if(player!=null)
            player.setVolume(0, 0);
    }

}
