package com.fyp.intellitutor_smartieltsapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.Progress;
import com.fyp.intellitutor_smartieltsapp.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.pdftron.common.PDFNetException;
import com.pdftron.pdf.PDFDoc;
import com.pdftron.pdf.PDFViewCtrl;
import com.pdftron.pdf.config.ViewerConfig;
import com.pdftron.pdf.controls.DocumentActivity;
import com.pdftron.pdf.utils.Utils;
import com.sdsmdg.tastytoast.TastyToast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

//needed jars: fr.opensagres.poi.xwpf.converter.core-2.0.1.jar,
//             fr.opensagres.poi.xwpf.converter.pdf-2.0.1.jar,
//             fr.opensagres.xdocreport.itext.extension-2.0.1.jar,
//             itext-2.1.7.jar


public class ViewerActivity extends AppCompatActivity {
    String shortDesc, longDesc, uploadByStr, title;
    String filePath, type;

    TextView uploadBy, shortDescTv, longDescTv;
    RelativeLayout pdfLayoutRl, imageLayoutRl;
    FrameLayout docsLayoutRl;
    PDFView pdfView;
    ImageView imageView;

    ProgressBar progressBar1;
    Button viewDocBtn;
    String name;

    // msword
    File folder;
    private PDFViewCtrl mPdfViewCtrl;
    // ...
    private PDFDoc mPdfDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);
        title = getIntent().getStringExtra("title");
        setTitle(title);
        shortDesc = "Short Desc:\n" + getIntent().getStringExtra("shortDesc");
        longDesc = "Long Desc:\n" + getIntent().getStringExtra("longDesc");
        uploadByStr = "Uploaded By: " + getIntent().getStringExtra("uploadBy");
        filePath = getIntent().getStringExtra("filePath");
        type = getIntent().getStringExtra("type");
        if (getIntent().hasExtra("name")) {
            name = getIntent().getStringExtra("name");
        }

        initView();


    }


    public void viewFromResource(int resourceId, String fileName) throws PDFNetException {
        File file = Utils.copyResourceToLocal(this, resourceId, fileName, ".pdf");
        mPdfDoc = new PDFDoc(file.getAbsolutePath());
        mPdfViewCtrl.setDoc(mPdfDoc);
        // Alternatively, you can open the document using Uri:
        // Uri fileUri = Uri.fromFile(file);
        // mPdfDoc = mPdfViewCtrl.openPDFUri(fileUri, null);
    }

    public void viewFromLocalStorage(String myFilePath) throws PDFNetException {
        mPdfDoc = new PDFDoc(myFilePath);
        mPdfViewCtrl.setDoc(mPdfDoc);
        // Alternatively, you can open the document using Uri:
        // Uri fileUri = Uri.fromFile(new File(myFilePath));
        // mPdfDoc = mPdfViewCtrl.openPDFUri(fileUri, null);
    }

    public void viewFromHttpUrl(String myHttpUrl) throws PDFNetException {
        mPdfViewCtrl.openUrlAsync(myHttpUrl, null, null, null);
        // Alternatively, you can open the document using Uri:
        // mPdfViewCtrl.openPDFUri(Uri.parse(myHttpUrl), null);
    }

    public void viewFromContentUri(Uri contentUri) throws PDFNetException, FileNotFoundException {
        mPdfDoc = mPdfViewCtrl.openPDFUri(contentUri, null);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mPdfViewCtrl != null) {
            mPdfViewCtrl.pause();
            mPdfViewCtrl.purgeMemory();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPdfViewCtrl != null) {
            mPdfViewCtrl.resume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPdfViewCtrl != null) {
            mPdfViewCtrl.destroy();
            mPdfViewCtrl = null;
        }

        if (mPdfDoc != null) {
            try {
                mPdfDoc.close();
            } catch (Exception e) {
                // handle exception
            } finally {
                mPdfDoc = null;
            }
        }
    }

    private void ViewDoc() {
        docsLayoutRl.setVisibility(View.VISIBLE);
        /*try {
            AppUtils.setupPDFViewCtrl(mPdfViewCtrl);
        } catch (PDFNetException e) {
            // Handle exception
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
        }*/

//        Uri uri = Uri.parse(filePath);

        checkPermission();


    }

    private void checkPermission() {
        Dexter.withContext(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            DownloadFile();
                        } else {
                            TastyToast.makeText(ViewerActivity.this, "Please Give Permission First", TastyToast.LENGTH_SHORT, TastyToast.WARNING).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }


    private void DownloadFile() {
        folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/intellitutor/");
        if(folder.isDirectory()){

        } else {
            folder.mkdirs();
        }
        String docName = name+"."+type;
        PRDownloader.download(filePath, folder.getPath(), docName)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {

                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                    }
                })
                .start(new OnDownloadListener() {
                    @SuppressLint("CheckResult")
                    @Override
                    public void onDownloadComplete() {
//                        Toast.makeText(getApplicationContext(), "Download Completed", Toast.LENGTH_SHORT).show();
                        /*try {
                            String docPath = folder + name + "." + type;
                            String pdfPath = folder + name + ".pdf";
                            InputStream in = new FileInputStream(new File(docPath));
                            XWPFDocument document = null;
                            document = new XWPFDocument(in);
                            PdfOptions options = PdfOptions.create();
                            OutputStream out = new FileOutputStream(new File(pdfPath));
                            PdfConverter.getInstance().convert(document, out, options);

                            document.close();
                            out.close();
                            Toast.makeText(getApplicationContext(), "Conversion Done", Toast.LENGTH_SHORT).show();
                            loadPdf(pdfPath);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d("Errorrrrrrrrr", "onDownloadComplete: " + e.getMessage());
                            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }*/

                    }

                    @Override
                    public void onError(Error error) {
                        TastyToast.makeText(getApplicationContext(), "Error!!!", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                    }
                });

    }

    private void loadPdf(String pdfPath) {
        ViewerConfig config = new ViewerConfig.Builder().openUrlCachePath(this.getCacheDir().getAbsolutePath()).build();
        final Uri fileLink = Uri.parse(pdfPath);
        DocumentActivity.openDocument(this, fileLink, config);
    }


    private void viewImage() {
        imageLayoutRl.setVisibility(View.VISIBLE);
        Glide.with(ViewerActivity.this).load(filePath).placeholder(R.drawable.logo).into(imageView);
    }

    private void ViewPdf() {
        pdfLayoutRl.setVisibility(View.VISIBLE);
        final Uri uri = Uri.parse(filePath);
        ViewerConfig config = new ViewerConfig.Builder().openUrlCachePath(this.getCacheDir().getAbsolutePath()).build();
        final Uri fileLink = Uri.parse(filePath);
        DocumentActivity.openDocument(this, fileLink, config);
        new RetrievePdfStream().execute(filePath);
    }

    private void initView() {
        uploadBy = findViewById(R.id.uploadBy);
        shortDescTv = findViewById(R.id.shortDescTv);
        longDescTv = findViewById(R.id.longDescTv);

        pdfLayoutRl = findViewById(R.id.pdfLayoutRl);
        pdfView = findViewById(R.id.pdfView);

        imageLayoutRl = findViewById(R.id.imageLayoutRl);
        imageView = findViewById(R.id.imageView);

        docsLayoutRl = findViewById(R.id.docsLayoutRl);
        mPdfViewCtrl = findViewById(R.id.pdfviewctrl);


        uploadBy.setText(uploadByStr);
        shortDescTv.setText(shortDesc);
        longDescTv.setText(longDesc);

        if (type.equals("image")) {
            viewImage();
        } else if (type.equals("pdf")) {
            ViewPdf();
        } else {
            ViewDoc();
        }
    }

    class RetrievePdfStream extends AsyncTask<String, Void, InputStream> {

        @Override
        protected InputStream doInBackground(String... strings) {
            InputStream inputStream = null;
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                if (urlConnection.getResponseCode() == 200) {
                    inputStream = new BufferedInputStream(urlConnection.getInputStream());

                }
            } catch (IOException e) {
                return null;
            }
            return inputStream;
        }

        @Override
        protected void onPostExecute(InputStream inputStream) {
            pdfView.fromStream(inputStream).load();
        }
    }
}