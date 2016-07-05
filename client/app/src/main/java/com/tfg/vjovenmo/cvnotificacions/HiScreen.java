package com.tfg.vjovenmo.cvnotificacions;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class HiScreen extends Activity {
	String user;
	TextView txt_usr;
	ImageButton logoff;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lay_screen);

		txt_usr= (TextView) findViewById(R.id.usr_name);
		logoff= (ImageButton) findViewById(R.id.imageButton);

		Bundle extras = getIntent().getExtras();
        // Obtenim dades passades per intent
        if (extras != null) {
			user  = extras.getString("user");
		}else{
			user="error";
		}

		txt_usr.setText(user);

		// Tanquem sessió
   		logoff.setOnClickListener(new View.OnClickListener(){
			public void onClick(View view){
				finish();
			}
		});
	}

	// Anul·lem el botó enrere del movil.
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
		// no hacemos nada.
		return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}