package com.ivvlev.car.debugger;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.ivvlev.car.radio.mst768.Band;
import com.ivvlev.car.radio.mst768.FreqRange;
import com.ivvlev.car.radio.mst768.Radio;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivityDebugger extends AppCompatActivity {

    private EditText mEditText;
    private Radio mRadio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_debugger);
        mEditText = findViewById(R.id.editTextLog);
        findViewById(R.id.btnProc1).setOnLongClickListener(this::btnStationOnLongClick);
        findViewById(R.id.btnProc2).setOnLongClickListener(this::btnStationOnLongClick);
        findViewById(R.id.btnProc3).setOnLongClickListener(this::btnStationOnLongClick);
        findViewById(R.id.btnProc4).setOnLongClickListener(this::btnStationOnLongClick);
        findViewById(R.id.btnProc5).setOnLongClickListener(this::btnStationOnLongClick);
        findViewById(R.id.btnProc6).setOnLongClickListener(this::btnStationOnLongClick);
        mRadio = new Radio();
        mRadio.addListener(new Radio.NoticeListener() {
            @Override
            public void onFrequencyChanged(int freq) {
                log(String.format("onFrequencyChanged: %s", freq));
            }

            @Override
            public void onFreqRangeChanged(FreqRange freqRange) {
                log(String.format("onFreqRangeChanged: %s", freqRange.band));
            }

            @Override
            public void onStationSelected(int index) {
                log(String.format("onStationSelected: %s", index));
            }

            @Override
            public void onFreqRangeParamsReceived() {
                log(String.format("onFreqRangeParamsReceived"));
            }

            @Override
            public void onFlagChangedTA(boolean flag) {
                log(String.format("onFlagChangedTA: %s", flag));
            }

            @Override
            public void onFlagChangedREG(boolean flag) {
                log(String.format("onFlagChangedREG: %s", flag));
            }

            @Override
            public void onFlagChangedAF(boolean flag) {
                log(String.format("onFlagChangedAF: %s", flag));
            }

            @Override
            public void onFlagChangedLOC(boolean flag) {
                log(String.format("onFlagChangedLOC: %s", flag));
            }

            @Override
            public void onFlagChangedRDSTP(boolean flag) {
                log(String.format("onFlagChangedRDSTP: %s", flag));
            }

            @Override
            public void onFlagChangedRDSTA(boolean flag) {
                log(String.format("onFlagChangedRDSTA: %s", flag));
            }

            @Override
            public void onFlagChangedRDSST(boolean flag) {
                log(String.format("onFlagChangedRDSST: %s", flag));
            }

            @Override
            public void onFlagChangedScanning(boolean flag) {
                log(String.format("onFlagChangedScanning: %s", flag));
            }

            @Override
            public void onStationFound(int number, int freq, String name, int pty) {
                log(String.format("onStationFound: %s, %s, %s, %s", number, freq, name, pty));
            }

            @Override
            public void onFoundPTY(int id, int requestedId) {
                log(String.format("onFoundPTY: %s, %s", id, requestedId));
            }

            @Override
            public void onFoundRDSPSText(String text) {
                log(String.format("onFoundRDSPSText: %s", text));
            }

            @Override
            public void onFoundRDSText(String text) {
                log(String.format("onFoundRDSText: %s", text));
            }

            @Override
            public void onSetRegionId(int id) {
                log(String.format("onSetRegionId: " + id));
            }

            @Override
            public void onNativeCommand(int what, int arg1, int arg2) {
                logCmd(what, arg1, arg2);
            }

            @Override
            public void onNativeEvent(int what, int arg1, int arg2, Object obj) {
                logEvent(what, arg1, arg2, obj);
            }

            @Override
            public void onPtyChanged(int ptyIndex) {
                log(String.format("onPtyChanged: %s", ptyIndex));
            }
        });
    }

    public void btnInitOnClick(View view) {
        log("-- Init --");
        mRadio.init();
    }

    public void btnCloseOnClick(View view) {
        log("-- Stop --");
        mRadio.stop();
    }

    public void btnFM1OnClick(View view) {
        log("-- FM1 --");
        mRadio.setFreqRangeByName(Band.FM1);
    }

    public void btnFM2OnClick(View view) {
        log("-- FM2 --");
        mRadio.setFreqRangeByName(Band.FM2);
    }

    public void btnAMOnClick(View view) {
        log("-- AM --");
        mRadio.setFreqRangeByName(Band.AM);
    }

    public void btnStationOnClick(View view) {
        int id = view.getId();
        int index = -1;
        if (id == R.id.btnProc1) {
            index = 0;
        } else if (id == R.id.btnProc2) {
            index = 1;
        } else if (id == R.id.btnProc3) {
            index = 2;
        } else if (id == R.id.btnProc4) {
            index = 3;
        } else if (id == R.id.btnProc5) {
            index = 4;
        } else if (id == R.id.btnProc6) {
            index = 5;
        }
        log(String.format("-- Select Station #%s --", index));
        mRadio.setStationByIndex(mRadio.freqRange.band.id, index);
    }

    public boolean btnStationOnLongClick(View view) {
        int id = view.getId();
        int index = -1;
        if (id == R.id.btnProc1) {
            index = 0;
        } else if (id == R.id.btnProc2) {
            index = 1;
        } else if (id == R.id.btnProc3) {
            index = 2;
        } else if (id == R.id.btnProc4) {
            index = 3;
        } else if (id == R.id.btnProc5) {
            index = 4;
        } else if (id == R.id.btnProc6) {
            index = 5;
        }
        log(String.format("-- Store Station #%s --", index));
        mRadio.storeCurrentFreq(index);
        return true;
    }

    public void btnSearchBackwardOnClick(View view) {
        log("-- Seek Prev Station --");
        mRadio.seekPrevStation();
    }

    public void btnSearchForwardOnClick(View view) {
        log("-- Seek Next Station --");
        mRadio.seekNextStation();
    }

    public void btnPriorFreqOnClick(View view) {
        log("-- Seek Prev Freq --");
        mRadio.seekPrevFreq();
    }

    public void btnNextFreqOnClick(View view) {
        log("-- Seek Next Freq --");
        mRadio.seekNextFreq();
    }

    public void btnPrevStationOnClick(View view) {
        log("-- Prev Station --");
        mRadio.prevStation();
    }

    public void btnNextStationOnClick(View view) {
        log("-- Next Station --");
        mRadio.nextStation();
    }

    public void btnREGOnClick(View view) {
        log("-- --");
        mRadio.toggleFlag_REG();
    }

    public void btnTAOnClick(View view) {
        log("-- toggleFlag_TA --");
        mRadio.toggleFlag_TA();
    }

    public void btnAFOnClick(View view) {
        log("-- toggleFlag_AF --");
        mRadio.toggleFlag_AF();
    }

    public void btnLOCOnClick(View view) {
        log("-- toggleFlag_LOC --");
        mRadio.toggleFlag_LOC();
    }

    public void btnPTYOnClick(View view) {
        log("-- toggleFlag_PTY --");
        mRadio.toggleFlag_PTY();
    }

    public void btnAutoScanOnClick(View view) {
        log("-- autoScan --");
        mRadio.autoScan();
    }

    public void btnAutoScanOnLongClick(View view) {
        log("-- autoScanLongClick --");
        mRadio.autoScanLongClick();
    }

    public void btn1028OnClick(View view) {
        log("-- Set Station 102.8 --");
        mRadio.setStation(Band.FM1.id, 10280);
    }

    public void btn1063OnClick(View view) {
        log("-- Set Station 106.3 --");
        mRadio.setStation(Band.FM1.id, 10630);
    }

    public void btngetFilesDirOnClick(View view) {
        File root = getFilesDir();
        log(root.getAbsolutePath());
        for (File file : root.listFiles()) {
            log("-" + file.getAbsolutePath());
        }
    }

    public void btngetExternalFilesDirOnClick(View view) {
        File root = getExternalFilesDir(null);
        log(root.getAbsolutePath());
        for (File file : root.listFiles()) {
            log("-" + file.getAbsolutePath());
        }
    }

    public void btnClearLogOnClick(View view) {
        mEditText.getText().clear();
    }


    public void logCmd(int what, int arg1, int arg2) {
        logMessage(String.format("command: %s, %s, %s", what, arg1, arg2));
    }

    public void logEvent(int what, int arg1, int arg2, Object obj) {
        logMessage(String.format("event: %s, %s, %s, %s", what, arg1, arg2, obj));
    }

    public void logMessage(String message) {
        log(message);
    }

    public void log(String message) {
        mEditText.append(message);
        mEditText.append("\r\n");
    }


    private String FILENAME_SD = "CarRadioDebug.log";

    private void writeLog(File sdFile) {
        try {
            log("Попытка записи в: " + sdFile.getAbsolutePath());
            if (sdFile.exists())
                sdFile.delete();
            sdFile.createNewFile();
            // открываем поток для записи
            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile, true));
            // пишем данные
            bw.write(mEditText.getText().toString());
            // закрываем поток
            bw.close();
            log("Файл записан в: " + sdFile.getAbsolutePath());
        } catch (IOException e) {
            log(e.getMessage());
            e.printStackTrace();
        }
    }

    public void btnSaveOnClick(View view) {
        writeLog(new File("/storage/emulated/0/iNand/DCIM/" + FILENAME_SD));
    }

    public void btnSaveSDOnClick(View view) {
        writeLog(new File("/storage/usb1/" + FILENAME_SD));
    }

    public void btnSaveToDirOnClick(View view) {
        writeLog(new File("/mnt/sdcard/iNand/" + FILENAME_SD));
    }

    public void btnSaveToDirCustOnClick(View view) {
        writeLog(new File("/mnt/sdcard/iNand/DCIM/" + FILENAME_SD));
    }

    public void btnSaveToDataTwMusicOnClick(View view) {
        writeLog(new File("/data/tw/music/" + FILENAME_SD));
    }
/*
    public void btnSaveOnClick(View view) {
        try {
            FileOutputStream outputStream = this.openFileOutput(FILENAME_SD,
                    Context.MODE_APPEND
            );
            log("Файл записан: " + getFileStreamPath(FILENAME_SD).getAbsolutePath());
            outputStream.write(mEditText.getText().toString().getBytes());
            outputStream.close();
        } catch (IOException e) {
            log(e.getMessage());
            e.printStackTrace();
        }
    }
*/
/*
    public void btnSaveSDOnClick(View view) {
        // проверяем доступность SD
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            log("SD-карта не доступна: " + Environment.getExternalStorageState());
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            log("READ_EXTERNAL_STORAGE not granted");
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            log("WRITE_EXTERNAL_STORAGE not granted");
        }
        // получаем путь к SD
        File sdPath = Environment.getExternalStorageDirectory();
        // добавляем свой каталог к пути
        sdPath = new File(sdPath.getAbsolutePath() + "/caradio");
        // создаем каталог
        sdPath.mkdirs();
        // формируем объект File, который содержит путь к файлу
        File sdFile = new File(sdPath, FILENAME_SD);
        try {
            // открываем поток для записи
            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
            // пишем данные
            bw.write(mEditText.getText().toString());
            // закрываем поток
            bw.close();
            log("Файл записан на SD: " + sdFile.getAbsolutePath());
        } catch (IOException e) {
            log(e.getMessage());
            e.printStackTrace();
        }
    }

    private static final int READ_REQUEST_CODE = 42;
    public void btnSaveToDirOnClick(View view) {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, READ_REQUEST_CODE);
//
//        SimpleFileDialog FileSaveDialog = new SimpleFileDialog(this, "FolderChoose",
//                new SimpleFileDialog.SimpleFileDialogListener() {
//                    @Override
//                    public void onChosenDir(String chosenDir) {
//                        // The code in this function will be executed when the dialog OK button is pushed
//                        File sdFile = new File(chosenDir, FILENAME_SD);
//                        try {
//                            // открываем поток для записи
//                            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
//                            // пишем данные
//                            bw.write(mEditText.getText().toString());
//                            // закрываем поток
//                            bw.close();
//                            log("Файл записан в каталог: " + sdFile.getAbsolutePath());
//                        } catch (IOException e) {
//                            log(e.getMessage());
//                            e.printStackTrace();
//                        }
//
////                        Toast.makeText(MainActivityDebugger.this, "Chosen FileOpenDialog File: " +
////                                m_chosen, Toast.LENGTH_LONG).show();
//                    }
//                });
//
//        //You can change the default filename using the public variable "Default_File_Name"
//        FileSaveDialog.Default_File_Name = FILENAME_SD;
//        FileSaveDialog.chooseFile_or_Dir();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            if (resultData != null) {
                try {
                    Uri uri = resultData.getData();
                    File sdFile = new File(new File(uri.getEncodedPath()), FILENAME_SD);
                    // открываем поток для записи
                    BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
                    // пишем данные
                    bw.write(mEditText.getText().toString());
                    // закрываем поток
                    bw.close();
                    log("Файл записан на SD: " + sdFile.getAbsolutePath());
                } catch (IOException e) {
                    log(e.getMessage());
                    e.printStackTrace();
                }
                //log("Uri: " + uri.toString());
            }
        }
    }

    public void btnSaveToDirCustOnClick(View view) {

        SimpleFileDialog FileSaveDialog = new SimpleFileDialog(this, "FolderChoose",
                new SimpleFileDialog.SimpleFileDialogListener() {
                    @Override
                    public void onChosenDir(String chosenDir) {
                        // The code in this function will be executed when the dialog OK button is pushed
                        File sdFile = new File(chosenDir, FILENAME_SD);
                        try {
                            // открываем поток для записи
                            BufferedWriter bw = new BufferedWriter(new FileWriter(sdFile));
                            // пишем данные
                            bw.write(mEditText.getText().toString());
                            // закрываем поток
                            bw.close();
                            log("Файл записан в каталог: " + sdFile.getAbsolutePath());
                        } catch (IOException e) {
                            log(e.getMessage());
                            e.printStackTrace();
                        }

//                        Toast.makeText(MainActivityDebugger.this, "Chosen FileOpenDialog File: " +
//                                m_chosen, Toast.LENGTH_LONG).show();
                    }
                });

        //You can change the default filename using the public variable "Default_File_Name"
        FileSaveDialog.Default_File_Name = FILENAME_SD;
        FileSaveDialog.chooseFile_or_Dir();
    }

    */
//
//    public void checkPermissionReadStorage(Activity activity) {
//        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
//                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
//
//                // Show an expanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//            } else {
//
//                // No explanation needed, we can request the permission.
//
//                ActivityCompat.requestPermissions(activity,
//                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
//                        MY_PERMISSIONS_REQUEST_READ_STORAGE);
//
//                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
//                // app-defined int constant. The callback method gets the
//                // result of the request.
//            }
//        }
//    }

}
