package com.example.sample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    Bitmap mask;
    Bitmap combined;
    ImageView imageView;
    Boolean masked;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.btnSwitch:
                if (!masked) {
                    imageView.setImageBitmap(RotateBitmap(mask, 90.0f));
                    masked = true;
                    return true;
                }
                else{
                    imageView.setImageBitmap(RotateBitmap(combined, 90.0f));
                    masked = false;
                    return true;
                }

            case R.id.btnRed:
                imageView.setImageBitmap(RotateBitmap(sampleOpenCV(combined, new Scalar(160, 100, 100), new Scalar(179, 255, 255)), 90.0f));
                return true;

            case R.id.btnGreen:
                imageView.setImageBitmap(RotateBitmap(sampleOpenCV(combined, new Scalar(40, 100, 100), new Scalar(80, 255, 255)), 90.0f));
                return true;

            case R.id.btnBlue:
                imageView.setImageBitmap(RotateBitmap(sampleOpenCV(combined, new Scalar(110, 50, 50), new Scalar(130, 255, 255)), 90.0f));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        masked = false;

        Intent intent = getIntent();
        String filename = intent.getStringExtra("FILENAME");

        try{
            combined = BitmapFactory.decodeFile(filename);
            combined = Bitmap.createScaledBitmap(combined, 1200, 900, true);
            mask = combined;
            imageView.setImageBitmap(RotateBitmap(combined, 90.0f));
        } catch (Exception ex){
            ex.printStackTrace();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(" ");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }


    // OpenCV code starts here
    public Bitmap sampleOpenCV(Bitmap bitmap, Scalar lowerb, Scalar upperb){
        Mat src = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC3);
        Utils.bitmapToMat(bitmap, src);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGBA2RGB);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_RGB2HSV);

        Mat res = new Mat();
        Core.inRange(src, lowerb, upperb, res);
        Mat tmp = new Mat();
        src.copyTo(tmp, res);

        Imgproc.cvtColor(tmp, tmp, Imgproc.COLOR_HSV2RGB);
        Bitmap result = Bitmap.createBitmap(src.cols(), src.rows(), bitmap.getConfig());
        Utils.matToBitmap(tmp, result);

        mask = Bitmap.createBitmap(src.cols(), src.rows(), bitmap.getConfig());
        Utils.matToBitmap(res, mask);
        masked = false;
        return result;
    }
}
