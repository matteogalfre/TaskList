package es.ucm.fdi.tasklist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import es.ucm.fdi.tasklist.MainActivity;
import es.ucm.fdi.tasklist.R;

public class SplashScreenActivity extends AppCompatActivity {

    Animation topAnim, bottomAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ImageView logoTop = (ImageView) findViewById(R.id.logotop);
        ImageView logoBottom = (ImageView) findViewById(R.id.logobottom);

        topAnim = AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.bottom_animation);
        logoTop.setAnimation(topAnim);
        logoBottom.setAnimation((bottomAnim));

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        };
        new Handler().postDelayed(runnable, 3000);
    }
}
