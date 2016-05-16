package com.tfg.vjovenmo.cvnotificacions;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import library.Httppostaux;

public class Login extends AppCompatActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final String EXTRA_MESSAGE = "message";
    private static final String PROPERTY_REG_ID = "AIzaSyA3NaeCkE1SUUP5sL-8g1Nz-g-uYSeQ2_A";
    private static final String PROPERTY_APP_VERSION = "1.0";
    private static final String PROPERTY_EXPIRATION_TIME = "onServerExpirationTimeMs";
    private static final String PROPERTY_USER = "user";

    public static final long EXPIRATION_TIME_MS = 1000 * 3600 * 24 * 7;

    String SENDER_ID = "574413074189";

    static final String TAG = "GCMDemo";

    private Context context;
    private String regid;
    private GoogleCloudMessaging gcm;

    EditText user;
    //EditText pass;
    Button blogin;
    Httppostaux post;
    //IP servidor
    String IP_Server = "www.campusvirtual-tfg-uab.cat";
    //Path dels fitxers
    String URL_connect = "http://" + IP_Server + "/app_login/acces.php";

    boolean result_back;
    private ProgressDialog pDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getSupportActionBar().hide();
        post = new Httppostaux();
        user = (EditText) findViewById(R.id.usuariEditText);
        //pass= (EditText) findViewById(R.id.passwordEditText);
        blogin = (Button) findViewById(R.id.botoLogin);

        // Boto login
        blogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String usuario = user.getText().toString();
                //String passw=pass.getText().toString();

                if (checklogindata(usuario) == true) {
                    new asynclogin().execute(usuario);
                    context = getApplicationContext();

                    //Chequemos si está instalado Google Play Services
                    if(checkPlayServices())
                    {
                    gcm = GoogleCloudMessaging.getInstance(Login.this);

                    //Obtenemos el Registration ID guardado
                    regid = getRegistrationId(context);

                    //Si no disponemos de Registration ID comenzamos el registro
                    if (regid.equals("")) {
                        TareaRegistroGCM tarea = new TareaRegistroGCM();
                        tarea.execute(usuario);
                    }
                    }
                    else
                    {
                        Log.i(TAG, "No se ha encontrado Google Play Services.");
                    }
                } else {
                    err_login();
                }
            }
        });
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
                Log.i(TAG, "Dispositivo no soportado.");
                finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Mostrem un toast en cas d'error
     */
    public void err_login() {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(200);
        Toast toast1 = Toast.makeText(getApplicationContext(), "Error: Nom d'usuari o password", Toast.LENGTH_SHORT);
        toast1.show();
    }

    /*Valida el estado del logueo solamente necesita como parametros el usuario y passw*/
    public boolean loginstatus(String username) {
        int logstatus = -1;

    	/*ArrayList nom, valor amb les dades passades com a parametres*/
        ArrayList<NameValuePair> postparameters2send = new ArrayList<NameValuePair>();

        postparameters2send.add(new BasicNameValuePair("usuario", username));
        //postparameters2send.add(new BasicNameValuePair("password",password));

        // Realitzem un pateició POST als nostres fitxers, enviant com parametre l'ArrayList
        JSONArray jdata = post.getserverdata(postparameters2send, URL_connect);

        SystemClock.sleep(950);

        // Si el JSON no es buit, treballem amb ell
        if (jdata != null && jdata.length() > 0) {
            JSONObject json_data;
            try {
                json_data = jdata.getJSONObject(0);
                logstatus = json_data.getInt("logstatus");
                Log.e("loginstatus", "logstatus= " + logstatus);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // Validem el valor obtingut
            if (logstatus == 0) {
                Log.e("loginstatus ", "invàlid");
                return false;
            } else {
                Log.e("loginstatus ", "vàlid");
                return true;
            }
        } else {
            Log.e("JSON  ", "ERROR");
            return false;
        }
    }

    // Verifiquem que ningun camp esta buit
    public boolean checklogindata(String username) {
        if (username.equals("")) {
            Log.e("Login ui", "checklogindata user or pass error");
            return false;
        } else {
            return true;
        }
    }

    /**
     * CLASSE ASYNCTASK
     *
     * Farem servir aquesta classe per mostrar el dialeg de progres mentres enviem les
     * dades i obtenim resposta
     */
    class asynclogin extends AsyncTask<String, String, String> {
        String user, pass;
        protected void onPreExecute() {
            // Mostrem el ProgressDialog
            pDialog = new ProgressDialog(Login.this);
            pDialog.setMessage("Un moment si us plau....");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        // S'executa darrere el onPreExecute
        protected String doInBackground(String... params) {
            user = params[0];
            //pass=params[1];

            // Realitzem en segon pla la connexió
            if (loginstatus(user) == true) {
                return "ok";
            } else {
                return "err";
            }

        }

        // Un cop tenim resposta s'executa.
        protected void onPostExecute(String result) {
            pDialog.dismiss();
            Log.e("onPostExecute=", "" + result);
            if (result.equals("ok")) {
                Intent i = new Intent(Login.this, HiScreen.class);
                i.putExtra("user", user);
                startActivity(i);
            } else {
                err_login();
            }
        }
    }

    private String getRegistrationId(Context context)
    {
        SharedPreferences prefs = getSharedPreferences(
                Login.class.getSimpleName(),
                Context.MODE_PRIVATE);

        String registrationId = prefs.getString(PROPERTY_REG_ID, "");

        if (registrationId.length() == 0)
        {
            Log.d(TAG, "Registro GCM no encontrado.");
            return "";
        }

        String registeredUser =
                prefs.getString(PROPERTY_USER, "user");

        int registeredVersion =
                prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);

        long expirationTime =
                prefs.getLong(PROPERTY_EXPIRATION_TIME, -1);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String expirationDate = sdf.format(new Date(expirationTime));

        Log.d(TAG, "Registro GCM encontrado (usuario=" + registeredUser +
                ", version=" + registeredVersion +
                ", expira=" + expirationDate + ")");

        int currentVersion = getAppVersion(context);

        if (registeredVersion != currentVersion)
        {
            Log.d(TAG, "Nueva versión de la aplicación.");
            return "";
        }
        else if (System.currentTimeMillis() > expirationTime)
        {
            Log.d(TAG, "Registro GCM expirado.");
            return "";
        }
        else if (!user.getText().toString().equals(registeredUser))
        {
            Log.d(TAG, "Nuevo nombre de usuario.");
            return "";
        }

        return registrationId;
    }

    private static int getAppVersion(Context context)
    {
        try
        {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);

            return packageInfo.versionCode;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            throw new RuntimeException("Error al obtener versión: " + e);
        }
    }

    private class TareaRegistroGCM extends AsyncTask<String, Integer,String>
    {
        @Override
        protected String doInBackground(String... params)
        {
            String msg = "";

            try
            {
                if (gcm == null)
                {
                    gcm = GoogleCloudMessaging.getInstance(context);
                }

                //Nos registramos en los servidores de GCM
                regid = gcm.register(SENDER_ID);

                Log.d(TAG, "Registrado en GCM: registration_id=" + regid);
                setRegistrationId(context, params[0], regid);

                //Nos registramos en nuestro servidor
                /*boolean registrado = registroServidor(params[0], regid);

                //Guardamos los datos del registro
                if(registrado)
                {
                    setRegistrationId(context, params[0], regid);
                }*/
            }
            catch (IOException ex)
            {
                Log.d(TAG, "Error registro en GCM:" + ex.getMessage());
            }

            return msg;
        }
    }

    private void setRegistrationId(Context context, String user, String regId)
    {
        SharedPreferences prefs = getSharedPreferences(
                Login.class.getSimpleName(),
                Context.MODE_PRIVATE);

        int appVersion = getAppVersion(context);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_USER, user);
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.putLong(PROPERTY_EXPIRATION_TIME,
                System.currentTimeMillis() + EXPIRATION_TIME_MS);

        editor.commit();
    }

    /*private boolean registroServidor(String usuario, String regId)
    {
        boolean reg = false;

        final String NAMESPACE = "http://sgoliver.net/";
        final String URL="http://10.0.2.2:1634/ServicioRegistroGCM.asmx";
        final String METHOD_NAME = "RegistroCliente";
        final String SOAP_ACTION = "http://sgoliver.net/RegistroCliente";

        SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

        request.addProperty("usuario", usuario);
        request.addProperty("regGCM", regId);

        SoapSerializationEnvelope envelope =
                new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.dotNet = true;

        envelope.setOutputSoapObject(request);

        HttpTransportSE transporte = new HttpTransportSE(URL);

        try
        {
            transporte.call(SOAP_ACTION, envelope);

            SoapPrimitive resultado_xml =(SoapPrimitive)envelope.getResponse();
            String res = resultado_xml.toString();

            if(res.equals("1"))
            {
                Log.d(TAG, "Registrado en mi servidor.");
                reg = true;
            }
        }
        catch (Exception e)
        {
            Log.d(TAG, "Error registro en mi servidor: " + e.getCause() + " || " + e.getMessage());
        }

        return reg;
    }*/

    @Override
    protected void onResume()
    {
        super.onResume();
        checkPlayServices();
    }
}