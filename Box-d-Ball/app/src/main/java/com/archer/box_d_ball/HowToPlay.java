package com.archer.box_d_ball;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Swastik on 28-12-2015
 *
 */
public class HowToPlay
{
    int help_index=0;
    String Help_Text[] =
            {
                    "Tilt device to control the ball",
                    "Tilt DOWN to move DOWN",
                    "Tilt DOWN to move DOWN",
                    "Tilt UP to move UP",
                    "Tilt UP to move UP",
                    "Tilt RIGHT to move RIGHT",
                    "Tilt RIGHT to move RIGHT",
                    "Tilt LEFT to move LEFT",
                    "Tilt LEFT to move LEFT",
                    "Tap on the heart to stay alive",
                    "Tap on the heart to stay alive",
                    "Tap on circles to collect extra points",
                    "Tap on circles to collect extra points"
            };
    int Help_Image[] = {
            R.drawable.img_tilt_zero,
            R.drawable.img_tilt_zero,
            R.drawable.img_tilt_down,
            R.drawable.img_tilt_zero,
            R.drawable.img_tilt_up,
            R.drawable.img_tilt_zero,
            R.drawable.img_right_tilt,
            R.drawable.img_tilt_zero,
            R.drawable.img_left_tilt,
            R.drawable.img_heart_small,
            R.drawable.img_heart_small,
            R.drawable.img_circle_tokens,
            R.drawable.img_circle_tokens
    };

    MainActivity mContext;
    CountDownTimer velocity_updater;
    CountDownTimer help_animation_updater;
    TextView tv_x_velocity;
    TextView tv_y_velocity;
    TextView tv_x_direction;
    TextView tv_y_direction;
    ImageView iv_HelpImage;
    TextView tv_HelpText;
    View btn_replay;

    public HowToPlay(MainActivity m_context)
    {
        mContext = m_context;
        m_context.objBall.setBaseForce(m_context.x_force,m_context.y_force);
        initialize_controls();
        initialize_velocity_updater();
    }

    public void StartHelp() {
        help_index=0;
        tv_HelpText.setText(Help_Text[help_index]);
        btn_replay.setVisibility(View.INVISIBLE);
        tv_HelpText.setAlpha(1f);
        iv_HelpImage.setAlpha(1f);

        if(help_animation_updater!=null)
            help_animation_updater.cancel();

        initialize_help_animation_updater();
    }

    private void initialize_controls() {
        tv_x_velocity = (TextView)mContext.findViewById(R.id.tvX_velocity);
        tv_y_velocity = (TextView)mContext.findViewById(R.id.tvY_velocity);
        tv_x_direction = (TextView)mContext.findViewById(R.id.tv_x_direction);
        tv_y_direction = (TextView)mContext.findViewById(R.id.tv_y_direction);
        btn_replay = mContext.findViewById(R.id.btn_replay);

        tv_HelpText = (TextView)mContext.findViewById(R.id.tv_help_text);
        iv_HelpImage = (ImageView)mContext.findViewById(R.id.iv_help_image);

        if(mContext.objBox != null && mContext.objBox.box !=null)
            mContext.objBox.box.setVisibility(View.INVISIBLE);
//        if(mContext.findViewById(R.id.tvScore)!=null)
//            mContext.findViewById(R.id.tvScore).setVisibility(View.INVISIBLE);

        btn_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartHelp();
            }
        });
    }

    public void initialize_velocity_updater()
    {
        velocity_updater = new CountDownTimer(18000000000000l,360) {

            @Override
            public void onTick(long millisUntilFinished) {
                float x_vel,y_vel;
                x_vel= (int)((mContext.x_force-mContext.objBall.base_x_force)*100);
                y_vel= (int)((mContext.y_force-mContext.objBall.base_y_force)*100);
                if(x_vel>mContext.objBall.force_limit*100)
                    x_vel=mContext.objBall.force_limit*100;
                if(y_vel>mContext.objBall.force_limit*100)
                    y_vel=mContext.objBall.force_limit*100;

                tv_x_velocity.setText(String.valueOf(Math.abs(x_vel / 100f)));
                tv_y_velocity.setText(String.valueOf(Math.abs(y_vel/100f)));

                if(x_vel<0)
                    tv_x_direction.setText("→");
                else
                    tv_x_direction.setText("←");

                if(y_vel<0)
                    tv_y_direction.setText("↑");
                else
                    tv_y_direction.setText("↓");
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    private void initialize_help_animation_updater() {
        help_animation_updater = new CountDownTimer(18000000000000l,1500) {
            @Override
            public void onTick(long millisUntilFinished) {

                if(help_index>=Help_Image.length) {
                    help_animation_updater.cancel();
                    iv_HelpImage.setAlpha(0f);
                    btn_replay.setVisibility(View.VISIBLE);
                    tv_HelpText.setText("Replay Instructions");
                    iv_HelpImage.setBackgroundResource(Help_Image[0]);
                    return;
                }

                iv_HelpImage.setBackgroundResource(Help_Image[help_index]);

                if(help_index%2==1)
                {
                    tv_HelpText.setText(Help_Text[help_index]);
                    tv_HelpText.setAlpha(.2f);
                    tv_HelpText.animate().alpha(1);
                }
                help_index++;
            }

            @Override
            public void onFinish() {

            }
        }.start();
    }

    public void release_help_resources()
    {
        velocity_updater.cancel();
        help_animation_updater.cancel();

        if(mContext.objBox != null && mContext.objBox.box != null)
            mContext.objBox.box.setVisibility(View.VISIBLE);
//        if(mContext.findViewById(R.id.tvScore)!=null)
//            mContext.findViewById(R.id.tvScore).setVisibility(View.VISIBLE);
    }
}
