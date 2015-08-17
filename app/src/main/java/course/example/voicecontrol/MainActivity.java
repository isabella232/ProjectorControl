package course.example.voicecontrol;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends Activity {
    private ImageButton btnSpeak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private static final String TAG = "MainActivity";
    private static final byte[] SUCCESS_DA = {(byte)0xDA};
    private static final int PORT = 2004;
    private String mIp = "10.0.3.18";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().hide();

        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);


        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        final Button buttonUp = (Button) findViewById(R.id.button_up);
        final Button buttonDown = (Button) findViewById(R.id.button_down);
        final Button buttonRight = (Button) findViewById(R.id.button_right);
        final Button buttonLeft = (Button) findViewById(R.id.button_left);
        final Button buttonCalibration = (Button) findViewById(R.id.button_calibrate);
        final Button buttonNear = (Button) findViewById(R.id.button_near);
        final Button buttonFar = (Button) findViewById(R.id.button_far);

        buttonCalibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ShowCalibrationTask().execute(mIp);
            }
        });



        buttonNear.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.i("activity", "sending near message to Pi");
                    near();
                    Log.i("activity", "complete sending near message  to Pi");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.i("activity", "sending stop focus message to Pi");
                    stopfocus();
                    Log.i("activity", "complete sending stop focus message to Pi");
                }
                return false;
            }
        });


        buttonFar.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.i("activity", "sending far message to Pi");
                    far();
                    Log.i("activity", "complete sending far message  to Pi");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.i("activity", "sending stop focus message to Pi");
                    stopfocus();
                    Log.i("activity", "complete sending stop focus message to Pi");
                }
                return false;
            }
        });


        buttonUp.setOnTouchListener(new View.OnTouchListener()
        {

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.i("activity", "sending up message to Pi");
                    up();
                    Log.i("activity", "complete sending up message  to Pi");
                }
                else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.i("activity", "sending stop servo message to Pi");
                    stopservo();
                    Log.i("activity", "complete sending stop servo message to Pi");
                }
                return false;
            }
        });

        buttonDown.setOnTouchListener(new View.OnTouchListener()
        {

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    Log.i("activity", "sending down message to Pi");
                    down();
                    Log.i("activity", "complete sending down message  to Pi");
                }

                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    Log.i("activity", "sending stop servo message to Pi");
                    stopservo();
                    Log.i("activity", "complete sending stop servo message to Pi");
                }
                return false;
            }
        });

        buttonRight.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.i("activity", "sending right message to Pi");
                    right();
                    Log.i("activity", "complete sending right message  to Pi");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.i("activity", "sending stop stepper message to Pi");
                    stopstepper();
                    Log.i("activity", "complete sending stop stepper message to Pi");
                }
                return false;
            }
        });

        buttonLeft.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.i("activity", "sending 'left' message to Pi");
                    left();
                    Log.i("activity", "complete sending 'left' message  to Pi");
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    Log.i("activity", "sending stop stepper message to Pi");
                    stopstepper();
                    Log.i("activity", "complete sending stop stepper to Pi");
                }
                return false;
            }
        });
    }

    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private static boolean sendUDP(String ip, int port) {
        System.out.println("sendUDP started");
        byte[] response_buffer = new byte[1];
        boolean success = false;

        DatagramSocket datagramSocket = null;
        try {
            InetAddress inetAddress = InetAddress.getByName(ip);
            datagramSocket = new DatagramSocket();
            datagramSocket.setSoTimeout(1000);


            // lost hand
            byte[] bytes5 = {0x0E};
            DatagramPacket out_datagramPacket5 = new DatagramPacket(bytes5, bytes5.length, inetAddress, port);
            datagramSocket.send(out_datagramPacket5);
            Thread.sleep(100);

            //show calibration pattern
            byte[] bytes6 = {0x0A};
            DatagramPacket out_datagramPacket6 = new DatagramPacket(bytes6, bytes6.length, inetAddress, port);
            datagramSocket.send(out_datagramPacket6);
            Thread.sleep(100);

            //get response
            DatagramPacket in_datagramPacket6 = new DatagramPacket(response_buffer, 1);
            datagramSocket.receive(in_datagramPacket6);

            success = SUCCESS_DA[0] == response_buffer[0];
            Log.d(TAG, String.format("received (show calibration): %02X, success: %b", response_buffer[0], success));

        } catch (UnknownHostException ex) {
            Log.e(TAG, "UnknownHostException:" + ex.getMessage());

        } catch (SocketException ex) {
            Log.e(TAG, "SocketException:" + ex.getMessage());

        } catch (IOException ex) {
            Log.e(TAG, "IOException:" + ex.getMessage());

        } catch (InterruptedException ex) {
            Log.e(TAG, "InterruptedException:" + ex.getMessage());

        } finally {
            datagramSocket.close();
        }

        return success;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (result.get(0).contains("up")) up();
                    else if(result.get(0).contains("down")){
                        down();
                    }else if(result.get(0).contains("right")){
                        right();

                    }else if(result.get(0).contains("left")){
                        left();
                    }else if(result.get(0).contains("near")){
                        near();
                    }else if(result.get(0).contains("far")){
                        far();
                    }else if (result.get(0).contains("stop")){
                        stopservo();
                        stopstepper();
                        stopfocus();
                    }
                }
                break;
            }

        }
    }

    public void up () {
        goToUrl("http://10.0.3.160:8080/up");
    }

    public void down () {
        goToUrl ( "http://10.0.3.160:8080/down");
    }

    public void stopservo () {
        goToUrl ( "http://10.0.3.160:8080/stopservo");
    }

    public void left () {
        goToUrl ( "http://10.0.3.160:8080/left");
    }

    public void right () {
        goToUrl ( "http://10.0.3.160:8080/right");
    }

    public void stopstepper () {
        goToUrl ( "http://10.0.3.160:8080/stopstepper");
    }

    public void near () {
        goToUrl ( "http://10.0.3.160:8080/near");
    }

    public void far () {
        goToUrl ( "http://10.0.3.160:8080/far");
    }

    public void stopfocus () {
        goToUrl ( "http://10.0.3.160:8080/stopfocus");
    }

    private void goToUrl (String url) {
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.loadUrl(url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class ShowCalibrationTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... parms) {
            String ip = parms[0];
            return sendUDP(ip, PORT);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.d(TAG, "ShowCalibrationTask finished with rezult: " + result);
        }

    }
}
