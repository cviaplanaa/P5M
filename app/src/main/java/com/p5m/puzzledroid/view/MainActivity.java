package com.p5m.puzzledroid.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;

import android.content.res.Configuration;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import android.widget.Toast;

import com.p5m.puzzledroid.ImageController;
import com.p5m.puzzledroid.PuzzleDroidApplication;
import com.p5m.puzzledroid.R;
import com.p5m.puzzledroid.database.PuzzledroidDatabase;
import com.p5m.puzzledroid.util.AppExecutors;
import com.p5m.puzzledroid.util.UnsolvedImages;
import com.p5m.puzzledroid.util.Utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import androidx.core.content.FileProvider;

import timber.log.Timber;


public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 2;
    static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 3;
    static final int REQUEST_IMAGE_GALLERY = 4;

    //Audio
    MediaPlayer mp;
    Button audioOff;

    //Notification strings
    public String not_title;
    public String not_subtitle;
    public String not_show_not;
    public String seconds;


    // "random" or "selected". For the PuzzleControllerActivity to know how the puzzle started
    public static String selectedOrRandom;

    String photoPath;

    //Notification management
    private static final String PRIMARY_CHANNEL_ID = "Desktop-P5M-app.ChannelID";
    private static final int NOTIFICATION_ID = 0;
    private static final String ACTION_UPDATE_NOTIFICATION = "Desktop-P5M-app.ACTION_UPDATE_NOTIFICATION";
    private NotificationManager miNotifyManager;
    private NotificationReceiver miReceiver = new NotificationReceiver();
    int lastScore, recordScore;
    private static final int PUZZLE_CONTROLLER_ID = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.i("OnCreate");
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_main);
        createNotificationChannel();
        registerReceiver(miReceiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));
        mp = PuzzleDroidApplication.getInstance().mp;
        AssetManager am = getAssets();
        try {
            final String[] files = am.list("img");

            // Add all 9 images to the list. A LinkedList must be used since it's mutable.
            List<String> totalImages = new LinkedList<>(Arrays.asList(files));
            UnsolvedImages unsolvedImages = UnsolvedImages.getInstance();
            unsolvedImages.setUnsolvedImages(totalImages);
            unsolvedImages.setNumberOfImages(totalImages.size());
            Timber.i("Unsolved images list filled: " + unsolvedImages.toString());

            GridView grid = findViewById(R.id.grid);
            grid.setAdapter(new ImageController(this));
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Timber.i("Create Intent");
                    Intent intent = new Intent(getApplicationContext(), PuzzleControllerActivity.class);
                    int index = i % files.length;
                    String image = files[index];
                    Timber.i("Image: " + image +", Index: " + index);
                    intent.putExtra("assetName", image);
                    selectedOrRandom = "selected";
                    startActivityForResult(intent, PUZZLE_CONTROLLER_ID);
                }
            });
        } catch (IOException e) {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT);
        }
        //change actionbar title, if not it will go according to the default language system
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.app_name));

        Button changeLang = findViewById(R.id.changeMyLang);
        changeLang.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                showChangeLanguageDialog();
            }
        });
    }

    private void showChangeLanguageDialog() {
        //array of languages to display in alert dialog
        final String[] listItems = {"English", "Spanish"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        mBuilder.setTitle("Choose language...");
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0){
                    setLocale("en");
                    recreate();
                }
                if (i == 1){
                    setLocale("es");
                    recreate();
                }
            }
        });
        AlertDialog mDialog = mBuilder.create();
        //show alert dialog
        mBuilder.show();
    }

    private void setLocale(String lang) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        //save data to shared preferences
        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("My_Lang", lang);
        editor.apply();
    }

    //load language saved in shared preferences
    public void loadLocale (){
        SharedPreferences prefs = getSharedPreferences("Settings", Activity.MODE_PRIVATE);
        String language = prefs.getString("My_Lang", "");
        setLocale(language);
    }
    //Pause and Resume music (en segundo plano)

    protected void onPause(){
        super.onPause();
        mp.pause();
    }

    protected void onResume(){
        super.onResume();
        mp.start();
    }
    public void onAudioClick(View view) {
        audioOff = (Button)findViewById(R.id.audioOff);
        audioOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mp != null && mp.isPlaying()) {
                    mp.pause();
                }
                else{
                    mp.start();
                }
            }
        });
    }

    /**
     * Make a puzzle of one of the unsolved images.
     * @param view
     */
    public void onRandomClick(View view) {
        Timber.i("onRandomClick");
        List<String> unsolvedImages = UnsolvedImages.getInstance().getUnsolvedImages();
        if (unsolvedImages.isEmpty()) {
            Toast.makeText(getApplicationContext(),
                    "You have completed all the images.",
                    Toast.LENGTH_LONG).show();
        } else {
            String randomImage = UnsolvedImages.getRandomUnsolvedImage();
            Timber.i("Random Image: " + randomImage);
            Toast.makeText(getApplicationContext(),
                    "Current random image: " + UnsolvedImages.getNumberOfSolvedImages() + "/" +
                    UnsolvedImages.getInstance().getNumberOfImages() + ".",
                    Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), PuzzleControllerActivity.class);
            intent.putExtra("assetName", randomImage);
            selectedOrRandom = "random";
            startActivityForResult(intent, PUZZLE_CONTROLLER_ID);
        }
    }

    public void onCameraClick(View view) {
/*        AlertDialog.Builder camAlert = new AlertDialog.Builder(MainActivity.this);
        camAlert.setTitle("Próximamente");
        camAlert.setMessage("Esta función estará lista en el segundo producto");
        camAlert.setPositiveButton("De acuerdo", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        camAlert.show();*/

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                if (photoFile != null) {
                    Uri photoUri = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".fileprovider", photoFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
                }
            } catch (IOException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
            }
        }
    }

    public void onGalleryClick(View view) {
        Timber.i("onGalleryClick");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_READ_EXTERNAL_STORAGE);
        } else {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_IMAGE_GALLERY);
        }
    }

    private File createImageFile() throws IOException {
        Timber.i("createImageFile");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // permission not granted, initiate request
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE);
        } else {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            //Picture is saved in download because pictures and DCIM folders are not instantly reacheable from Gallery
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            photoPath = image.getAbsolutePath(); // save this to use in the intent

            return image;
        }

        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Timber.i("onRequestPermissionsResult");
        switch (requestCode) {
            case REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onCameraClick(new View(this));
                }

                return;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.i("onActivityResult");
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Intent intent = new Intent(this, PuzzleControllerActivity.class);
            intent.putExtra("mCurrentPhotoPath", photoPath);
            startActivity(intent);

        }
        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            Intent intent = new Intent(this, PuzzleControllerActivity.class);
            intent.putExtra("mCurrentPhotoUri", uri.toString());
            startActivity(intent);
        }

        if (requestCode == PUZZLE_CONTROLLER_ID && resultCode == RESULT_OK) {
            lastScore = Integer.parseInt(data.getStringExtra(PuzzleControllerActivity.EXTRA_MESSAGE_LAST_SCORE));
            sendNotification();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.help:
                Intent webviewIntent = new Intent(this, HelpActivity.class);
                startActivity(webviewIntent);
                return true;
            case R.id.scores:
                startActivity(new Intent(this, ScoresActivity.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void createNotificationChannel() {
        miNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        //notification strings
        not_title = getResources().getString(R.string.not_title);
        not_subtitle = getResources().getString(R.string.not_subtitle);
        not_show_not = getResources().getString(R.string.not_show_not);
        seconds = getResources().getString(R.string.seconds);

        //para que en las versiones antiguas siga funcinando
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Crearemos el canal
            NotificationChannel nChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    not_title, NotificationManager.IMPORTANCE_HIGH);
            nChannel.enableLights(true);
            nChannel.setLightColor(Color.RED);
            nChannel.enableVibration(true);
            nChannel.setDescription(not_subtitle);
            miNotifyManager.createNotificationChannel(nChannel);
        }
    }

/*    public void notificacionPersonalizada(View view) {

        RemoteViews rmViewsSmall = new RemoteViews(getPackageName(), R.layout.customsmall);
        RemoteViews rmViewsGrande = new RemoteViews(getPackageName(), R.layout.customnotification);

        rmViewsGrande.setTextViewText(R.id.tvCustom1, "¡Nueva puntuación!");
        rmViewsGrande.setTextViewText(R.id.tvCustom2, "Tu nueva puntuación ha sido de: ");

        Notification noti = new NotificationCompat.Builder(getApplicationContext(),
                PRIMARY_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setCustomContentView(rmViewsSmall)
                .setCustomBigContentView(rmViewsGrande)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .build();
        miNotifyManager.notify(1, noti);
    }*/

    public void sendNotification() {

        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent pIntent = PendingIntent.getBroadcast(
                this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.Builder nBuilder = getNotificationBuilder();

        nBuilder.addAction(R.drawable.ic_notification, "Ver puntuación", pIntent);
        miNotifyManager.notify(NOTIFICATION_ID, nBuilder.build());

    }

    private NotificationCompat.Builder getNotificationBuilder() {
        Intent notiIntent = new Intent(this, MainActivity.class);
        //PendingIntent es como un intent normal pero lo dejas abierto y ya se ejecutará cuando le llames
        PendingIntent pIntent = PendingIntent.getActivity(this, NOTIFICATION_ID,
                notiIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("¡Nueva puntuación!")
                .setContentText("Tu nueva puntuación ha sido de: ")
                .setContentIntent(pIntent) //al dar click abrirá lo que esté en pIntent
                .setAutoCancel(true) //al dar click cierra la notificacion
                .setSmallIcon(R.drawable.ic_notification);
        return notifyBuilder;
    }

    public void updateNotification() {
        //Bitmap androidImage = BitmapFactory.decodeResource(getResources(), R.drawable.bolivia);
        NotificationCompat.Builder nBuilder = getNotificationBuilder();
        nBuilder.setStyle(new NotificationCompat.BigTextStyle().bigText(Integer.toString(lastScore) + " segundos"));

        miNotifyManager.notify(NOTIFICATION_ID, nBuilder.build());
    }

    public class NotificationReceiver extends BroadcastReceiver {
        public NotificationReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            //Actualizaremos la notificacion
            updateNotification();
        }
    }


}