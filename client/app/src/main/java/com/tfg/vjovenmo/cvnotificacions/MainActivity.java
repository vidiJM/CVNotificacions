package com.tfg.vjovenmo.cvnotificacions;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //treure barra notificacions
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        EditText user = (EditText)this.findViewById(R.id.usuariEditText);
        EditText pass = (EditText)this.findViewById(R.id.passwordEditText);
        Button btnLogin = (Button)this.findViewById(R.id.botoLogin);
    }
}
