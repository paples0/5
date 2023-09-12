package ru.mirea.markinaa.camera;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.mirea.markinaa.camera.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Uri imageUri;
    private Boolean isWork;
    private	static	final	int REQUEST_CODE_PERMISSION = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        int	cameraPermissionStatus	=	ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.CAMERA);
        int	storagePermissionStatus	=	ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if	(cameraPermissionStatus	==	PackageManager.PERMISSION_GRANTED	&&
                storagePermissionStatus ==	PackageManager.PERMISSION_GRANTED)	{
            isWork	=	true;
        }	else	{
            ActivityCompat.requestPermissions(this,	new	String[]	{android.Manifest
                    .permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSION);
        }

        try {
            File	photoFile	=	createImageFile();
            String	authorities	=	getApplicationContext().getPackageName()	+	".fileprovider";
            imageUri	=	FileProvider.getUriForFile(MainActivity.this,	authorities,
                    photoFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            createImageFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ActivityResultCallback<ActivityResult> callback	=	new	ActivityResultCallback<ActivityResult>()	{
            @Override
            public	void	onActivityResult(ActivityResult	result)	{
                if	(result.getResultCode()	==	Activity.RESULT_OK)	{
                    Intent data	=	result.getData();
                    binding.imageView.setImageURI(imageUri);
                }
            }
        };
        ActivityResultLauncher<Intent> cameraActivityResultLauncher	=		registerForActivityResult(
                new	ActivityResultContracts.StartActivityForResult(),
                callback);
        binding.imageView.setOnClickListener(new	View.OnClickListener()	{
            @Override
            public	void	onClick(View	v)	{
                Intent	cameraIntent	=	new	Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if	(isWork)	{
                    try	{
                        File	photoFile	=	createImageFile();
                        String	authorities	=	getApplicationContext().getPackageName()	+
                                ".fileprovider";
                        imageUri	=	FileProvider.getUriForFile(MainActivity.this,
                                authorities,	photoFile);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,	imageUri);
                        cameraActivityResultLauncher.launch(cameraIntent);
                    }	catch	(IOException	e)	{
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    private File createImageFile()	throws IOException {
        String	timeStamp	=	new SimpleDateFormat("yyyyMMdd_HHmmss",	Locale.ENGLISH)
                .format(new Date());
        String	imageFileName	=	"IMAGE_"	+	timeStamp	+	"_";
        File	storageDirectory	=	getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return	File.createTempFile(imageFileName,	".jpg",	storageDirectory);
    }
    @Override
    public	void onRequestPermissionsResult(int	requestCode, @NonNull String[]	permissions,
                                              @NonNull	int[]	grantResults)	{
        super.onRequestPermissionsResult(requestCode,	permissions,	grantResults);
        switch	(requestCode){
            case	REQUEST_CODE_PERMISSION:
                isWork		=	grantResults[0]	==	PackageManager.PERMISSION_GRANTED;
                break;
        }
        if	(!isWork	)	finish();
    }

}