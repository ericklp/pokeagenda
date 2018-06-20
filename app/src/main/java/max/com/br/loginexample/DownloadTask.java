package max.com.br.loginexample;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;

/**
 * Created by ericklopes on 08/05/18.
 */

public class DownloadTask extends AsyncTask<String, Void, Bitmap> {

    private Context ctx;
    private ImageView image;
    private ProgressDialog progressDialog;

    public DownloadTask(Context ctx, ImageView image) {
        this.ctx = ctx;
        this.image = image;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = ProgressDialog.show(ctx, "Por favor aguarde..." , "Baixando imagem...");
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        Bitmap image = null;
        try {
            image = Auxiliar.baixarImagem(strings[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        image.setImageBitmap(bitmap);
        progressDialog.dismiss();
    }
}

