package com.fridge.application;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class download extends Activity {

	public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
	public static final int DIALOG_UNZIP_PROGRESS = 1;
	private Button startBtn;
	private Button unZipBtn;
	private ProgressDialog mProgressDialog;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dowload);
		startBtn = (Button)findViewById(R.id.startBtn);
		startBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				startDownload();
			}
		});

		unZipBtn = (Button)findViewById(R.id.unZipBtn);
		unZipBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				startUnzip();
			}
		});
	}

	private void startDownload() {
		String url = "https://dl.dropbox.com/s/ni27amabnooysqt/Product_Pictures.zip?token_hash=AAHaMHM4zCR7efzjcWkhCAy9BCS3pHgkQgHbVzQry5rNtg&dl=1";
		new DownloadFileAsync().execute(url);
	}

	private void startUnzip() {
		new UnzipFileAsync().execute("");
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DIALOG_DOWNLOAD_PROGRESS:
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("Downloading file..");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
			return mProgressDialog;
		case DIALOG_UNZIP_PROGRESS:
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage("Unzipping files..");
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
			return mProgressDialog;
		default:
			return null;
		}
	}

	class DownloadFileAsync extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(DIALOG_DOWNLOAD_PROGRESS);
		}

		@Override
		protected String doInBackground(String... aurl) {
			int count;

			try {

				URL url = new URL("https://dl.dropbox.com/s/ni27amabnooysqt/Product_Pictures.zip?token_hash=AAHaMHM4zCR7efzjcWkhCAy9BCS3pHgkQgHbVzQry5rNtg&dl=1");
				URLConnection conexion = url.openConnection();
				conexion.connect();

				int lenghtOfFile = conexion.getContentLength();
				Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

				InputStream input = new BufferedInputStream(url.openStream());
				OutputStream output = new FileOutputStream("/sdcard/ProductImages_iFridge/Product_Pictures.zip");

				byte data[] = new byte[1024];

				long total = 0;

				while ((count = input.read(data)) != -1) {
					total += count;
					publishProgress(""+(int)((total*100)/lenghtOfFile));
					output.write(data, 0, count);
				}

				output.flush();
				output.close();
				input.close();
			} catch (Exception e) {Log.d("ANDRO_ASYNC", "error");}
			return null;

		}
			
		
		protected void onProgressUpdate(String... progress) {
			Log.d("ANDRO_ASYNC",progress[0]);
			mProgressDialog.setProgress(Integer.parseInt(progress[0]));
		}

		protected void onPostExecute(String unused) {
			dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
		}
	}
	
	class UnzipFileAsync extends AsyncTask<String, String, String>{ 
		private String _zipFile = "/sdcard/ProductImages_iFridge/Product_Pictures.zip"; 
		private String _location = "/sdcard/ProductImages_iFridge/"; 
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(DIALOG_UNZIP_PROGRESS);
		}

		@Override
		protected String doInBackground(String... aurl) {
			
			try  { 		
				FileInputStream fin = new FileInputStream(_zipFile); 
				//BufferedInputStream bin = new BufferedInputStream(fin);
				ZipInputStream zin = new ZipInputStream(new BufferedInputStream(fin)); 
				ZipEntry ze = null; 
				while ((ze = zin.getNextEntry()) != null) { 
					Log.v("Decompress", "Unzipping " + ze.getName()); 

					if(ze.isDirectory()) { 
						_dirChecker(ze.getName()); 
					} else { 
						
						FileOutputStream fout = new FileOutputStream(_location + ze.getName());
						BufferedOutputStream out = new BufferedOutputStream(fout);
						for (int c = zin.read(); c != -1; c = zin.read()) { 
							fout.write(c); 
						} 

						zin.closeEntry(); 
						fout.close(); 
						
						
						/*BufferedOutputStream out = new BufferedOutputStream(fout);
						
						byte b[] = new byte[1024];
						int n;
						while ((n = zin.read(b,0,1024)) >= 0) {
						  out.write(b,0,n);
						}
						zin.closeEntry(); 
						fout.flush();
						fout.close();*/					


					} 
					
				}
				zin.close(); 
				
			} catch(Exception e) { 
				mProgressDialog.dismiss();
				Log.e("Decompress", "unzip", e); 
			}
			return null; 

		} 

		private void _dirChecker(String dir) { 
			File f = new File(_location + dir); 

			if(!f.isDirectory()) { 
				f.mkdirs(); 
			} 
		}
		@Override
		protected void onPostExecute(String unused) {
			dismissDialog(DIALOG_UNZIP_PROGRESS);
		}
	}

}

