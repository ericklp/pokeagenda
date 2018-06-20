package max.com.br.loginexample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Base64;
import android.widget.ArrayAdapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ericklopes on 09/04/18.
 */

public class ListCell extends ArrayAdapter<Pokemon> {
    private final Activity context;
    private ArrayList<Pokemon> pokemonArray;

    public ListCell(Activity context, ArrayList<Pokemon> pokemonArray) {
        super(context, R.layout.list_cell, pokemonArray);
        this.context = context;
        this.pokemonArray = pokemonArray;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowview = inflater.inflate(R.layout.list_cell, null, true);
        TextView textTitle = rowview.findViewById(R.id.nome);
        textTitle.setText(pokemonArray.get(position).getNome());
        TextView especie = rowview.findViewById(R.id.especie);
        especie.setText(pokemonArray.get(position).getEspecie());
        ImageView image = rowview.findViewById(R.id.imageView5);
        image.setImageBitmap(stringToBitMap(pokemonArray.get(position).getImagem()));
        //DownloadTask runner = new DownloadTask(context, image);
        //runner.execute("http://"+MainActivity.ip+":8080/SistemaCentral/res/"+pokemonArray.get(position).getImagem());

        return rowview;
    }

    public Bitmap stringToBitMap(String encodedString){
        try{
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            e.getMessage();
            return null;
        }
    }
}

