package jp.techacademy.akihiro.ogawa.autoslideshowapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PERMISSIONS_REQUEST_CODE = 100;

    static int i = 0;
    static int j = 0;
    static Uri[] dda = new Uri[9];

    Timer mTimer = null;
    MyTimerTask timerTask = null;
    Handler mHandler = new Handler();

    Button button1;
    Button button2;
    Button button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);


        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo();
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_CODE);
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo();

            ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
            imageVIew.setImageURI(dda[0]);
        }
    }

    @Override
    public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button1:      //進むボタン
                    if (j == i - 1) {
                        j = 0;
                        ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
                        imageVIew.setImageURI(dda[j]);
                    }
                    else {
                        j++;
                        ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
                        imageVIew.setImageURI(dda[j]);
                    }
                    break;

                case R.id.button3:      //戻るボタン
                    if (j == 0) {
                        j = i - 1;
                        ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
                        imageVIew.setImageURI(dda[j]);
                    }
                    else {
                        j--;
                        ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
                        imageVIew.setImageURI(dda[j]);
                    }
                    break;

                case R.id.button2:      // 再生/停止
                    //　再生ボタンが押されたとき
                    if (mTimer == null) {

                        Button button2 = (Button) findViewById(R.id.button2);
                        button2.setText("停止");
                        button1.setEnabled(false);
                        button3.setEnabled(false);


                        // タイマーの初期化処理
                        timerTask = new MyTimerTask();
                        mTimer = new Timer(true);
                        mTimer.schedule( timerTask, 0, 2000);
                    }

                    // 停止ボタンが押されたとき
                    else {
                        Button button2 = (Button) findViewById(R.id.button2);
                        button2.setText("再生");
                        button1.setEnabled(true);
                        button3.setEnabled(true);

                        mTimer.cancel();
                        mTimer = null;
                     }
                    break;

                default:
                    break;
            }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo();
                }
                break;
            default:
                break;
        }
    }

    private void getContentsInfo() {
        // 画像の情報を取得する
        // ContentProviderでデータを参照したい場合はContentResolverオブジェクトを使う
        // クライアントとしてデータ提供側のアプリケーションと通信します。
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        );

        if (cursor.moveToFirst()) {
            do {
                // indexからIDを取得し、そのIDから画像のURIを取得する
                int fieldIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                Long id = cursor.getLong(fieldIndex);
                Uri imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                // MediaStore.Images.Media.EXTERNAL_CONTENT_URIは外部ストレージの画像を指定

                dda[ 0 + i ] = imageUri;
                i++;

            } while (cursor.moveToNext());  // falseが返ってくるまでひたすらデータを取得
        }
        cursor.close();
    }
    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            // mHandlerからUI Threadへ処理をキューイング
            mHandler.post(new Runnable() {
                public void run() {
                    if (j == i - 1) {
                        j = 0;
                        ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
                        imageVIew.setImageURI(dda[j]);
                    }
                    else {
                        j++;
                        ImageView imageVIew = (ImageView) findViewById(R.id.imageView);
                        imageVIew.setImageURI(dda[j]);
                    }
                }
            });
        }
    }

}
