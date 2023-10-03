package cz.aurinet.odvadeni_cnc.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.androidnetworking.AndroidNetworking;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import cz.aurinet.hacz.R;
import cz.aurinet.odvadeni_cnc.ProcessMaterialPropertySingleton;
import cz.aurinet.processmaterial_dim.data.Code;
import cz.aurinet.odvadeni_cnc.ui.view_models.OdvadeniPrikazViewModel;

/*
 * Úvodní aktivita pro inicializaci třídy ScannerCtrl a intentu StockMaterialIntent, který se předává ScannerCtrl třídě.
 * Veškeré procesy spojené se čtečkou kódů řídí třída ScannerCtrl založená na abstraktu ScannerAbstract.
 * Procesy spojené s ovládáním status tlačítka na spodku obrazovky zajišťuje objekt třídy StatusButton ve třídě ScannerCtrl
 */
@RequiresApi(api = Build.VERSION_CODES.R)
public class OdvadeniPrikazActivity extends AppCompatActivity{

    private static final int REQUEST_CAMERA_PERMISSION = 201;
    private static final int REFRESH_DELAY_MS = 5000;
    private MediaPlayer mPlayer;
    private EditText txtBarcode;
    private Button button;
    private CameraSource cameraSource;
    private SurfaceView surfaceView;
    private TextView txtLabel;
    private TextView txtProgressBar;
    private ProgressBar progressBar;
    private ProcessMaterialPropertySingleton singleton;
    private OdvadeniPrikazViewModel viewModel;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_scanner);
        initViews();
        AndroidNetworking.initialize(this.getApplicationContext());
        mPlayer = MediaPlayer.create(OdvadeniPrikazActivity.this, R.raw.beep_err);
        singleton = ProcessMaterialPropertySingleton.INSTANCE;
        singleton.setDeviceName(Build.MANUFACTURER);
        handler = new Handler(Looper.getMainLooper());
        viewModel = new ViewModelProvider(this).get(OdvadeniPrikazViewModel.class);
        viewModel.getState().observe(this, prikazUiCalls -> {
            switch(prikazUiCalls){
                case ERR_KOD:
                    error("Chybný kód");
                    hideProgressBar();
                    showScanner();
                    break;
                case ERR_PRIKAZ_NEEXISTUJE:
                    error("Neexistující výrobní příkaz");
                    hideProgressBar();
                    showScanner();
                    break;
                case ERR_SERVER:
                    error("Chyba komunikace se severem");
                    hideProgressBar();
                    showScanner();
                    break;
                case ERR_PRIKAZ_UZAVREN:
                    error("Výrobní příkaz je již ukončen");
                    hideProgressBar();
                    showScanner();
                    break;
                case CALLBACK_OK:
                    nextActivity();
                    break;
                case SHOW_PROGRESSBAR:
                    txtBarcode.setText(viewModel.getCode());
                    txtLabel.setText(viewModel.getProcedureName());
                    buttonToProcessing();
                    hideScanner();
                    showProgressBar();
                    break;
                case SHOW_SCANNER:
                    hideProgressBar();
                    showScanner();
                case TO_DEFAULT:
                    toDefault();
                    break;
                case ERR_OBECNY:
                    error("Nastala nespecifikovaná chyba");
            }
        });
        //Pokud není Zebra, zobraz Scanner
        if (!singleton.isZebraDevice()) {
            initialiseDetectorsAndSources();
        }
    }

    /*
     * Inicializace a počáteční nastavení UI komponent.
     */
    private void initViews() {
        txtBarcode = findViewById(R.id.txtBarcodeDefault);
        surfaceView = findViewById(R.id.surfaceViewDefault);
        txtLabel = findViewById(R.id.txtLabelDefaul);
        txtProgressBar = findViewById(R.id.txtProgressBarDefault);
        progressBar = findViewById(R.id.progressBarDefault);
        button = findViewById(R.id.buttonStatusDefault);

        //Nastav akci pro zmáčknutí lupy v TextEditu
        txtBarcode.setOnKeyListener((view, i, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) && !viewModel.getIsScannerBlocked()) {
                viewModel.callApi(new Code(txtBarcode.getText().toString()));
                return true;
            }
            return false;
        });
        //nastav hint TextEditu
        txtBarcode.setHint("Načtěte/zadejte kód výrobního příkazu");

    }

    private void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
        txtProgressBar.setVisibility(View.VISIBLE);

    }
    private void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
        txtProgressBar.setVisibility(View.GONE);
    }

    private void showScanner() {
        if(!singleton.isZebraDevice()) {
            surfaceView.setVisibility(View.VISIBLE);
        }else{
            hideScanner();
        }
    }

    private void hideScanner(){
        surfaceView.setVisibility(View.INVISIBLE);
    }

    private void toDefault(){
        hideProgressBar();
        showScanner();
    }

    private void buttonToError(String text){
        button.setBackgroundColor(Color.RED);
        button.setText(text);
    }

    private void buttonToProcessing(){
        button.setBackgroundColor(Color.BLUE);
        button.setText("Prosím čekejte...");
    }

    private void buttonToDefault(){
        button.setBackgroundColor(Color.GRAY);
        button.setText("Načtěte kód výrobního příkazu");
        viewModel.setIsScannerBlocked(false);
        txtBarcode.setText(null);
    }

    private void error(String text){
        viewModel.setIsScannerBlocked(true);
        handler.removeCallbacksAndMessages(null);
        mPlayer.start();
        buttonToError(text);
        handler.postDelayed(this::buttonToDefault, REFRESH_DELAY_MS);
    }

    private void nextActivity(){
        Intent intent = new Intent(OdvadeniPrikazActivity.this, StockMaterialActivity.class);
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
    }

/* TODO onClick


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnNextStep:
                if (!scanner.isApiCall) {
                    scanner.callApiSearchProductionOrder(txtBarcode.getText().toString());
                } else if (scanner.isApiCall && !scanner.isApiCallSuccessfull) {
                    scanner.removeAllCallbacksAndMessages();
                    scanner.toDefault();
                } else {
                    scanner.removeAllCallbacksAndMessages();
                    startActivity(scanner.getNextActivityIntent());
                }
                if(Objects.equals(scanner.buttonController.getState(), "error")){
                    scanner.buttonController.toDefault();
                }
                break;
        }
    }

 */
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        toDefault();
        txtBarcode.setText(null);
        handler.removeCallbacksAndMessages(null);
        buttonToDefault();
    }

    public void initialiseDetectorsAndSources() {

        SurfaceView surfaceView = findViewById(R.id.surfaceViewDefault);

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(800, 800)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();


        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(OdvadeniPrikazActivity.this , new
                                String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0 && !viewModel.getIsScannerBlocked()) {
                    if (barcodes.valueAt(0) != null) {
                        //TODO spusť getCallback přes ModelView
                        String barcodeData = barcodes.valueAt(0).displayValue;
                        Code barcode = new Code(barcodeData);
                        runOnUiThread(() -> {
                            txtBarcode.setText(barcode.getCode());
                            txtLabel.setText(barcode.getProcedureName());
                            viewModel.callApi(barcode);
                        });
                    }
                }
            }
        });
    }
}
