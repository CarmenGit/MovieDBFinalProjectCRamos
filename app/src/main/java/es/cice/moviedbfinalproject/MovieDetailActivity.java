//by Carmen Ramos Delgado
package es.cice.moviedbfinalproject;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import es.cice.moviedbfinalproject.model.CreditsLists;
import es.cice.moviedbfinalproject.model.MovieTitleAndImage;
import es.cice.moviedbfinalproject.model.ValoracionesList;

import static es.cice.moviedbfinalproject.MainActivity.API_KEY;

public class MovieDetailActivity extends AppCompatActivity {
    public static final String IMAGEN_EXTRA = "image";
    public static final String TITULO_EXTRA = "titulo";
    public static final String CREDITOS_EXTRA = "creditos";
    public static final String VALORACIONES_EXTRA = "valoraciones";
    public static final String OVERVIEW_EXTRA = "overview";
    public static final String INFO_EXTRA = "info";
    public static final String ID_MOVIE_EXTRA = "idmovie";
    private static final String URL_CREDITS_MOVIE="https://api.themoviedb.org/3/movie/";
    private static final String URL_VALORACIONES_MOVIE="https://api.themoviedb.org/3/movie/";
    private TextView movieInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_detail);

        Intent intent = getIntent();
        String info = intent.getStringExtra(INFO_EXTRA);
        String image =intent.getStringExtra(IMAGEN_EXTRA);
        String titulo = intent.getStringExtra(TITULO_EXTRA);

        ImageView movieImage = (ImageView) findViewById(R.id.movieImage);
        TextView movieTitulo = (TextView) findViewById((R.id.movieTitulo));
        movieInfo = (TextView) findViewById((R.id.movieInfo));
        movieInfo.setMovementMethod(new ScrollingMovementMethod());

        Picasso
                .with(this)
                .load(Uri.parse(image))
                .resize(300, 300)
                 .centerInside()
                .into(movieImage);

        movieTitulo.setText(titulo);

        switch (info) {
            case "creditos":
                String idMovie = intent.getStringExtra(ID_MOVIE_EXTRA);
                TheMovieDBCreditsAsynTask at = new TheMovieDBCreditsAsynTask();
                at.execute(URL_CREDITS_MOVIE+ idMovie + "/credits?api_key=" + API_KEY);
                break;
            case "sinopsis":
                String overview = intent.getStringExtra(OVERVIEW_EXTRA);
                movieInfo.setText(overview);
                break;
            case "opiniones":
                String idMovie2 = intent.getStringExtra(VALORACIONES_EXTRA);
                TheMovieDBValoracionesAsynTask at2 = new TheMovieDBValoracionesAsynTask();
                at2.execute(URL_VALORACIONES_MOVIE+ idMovie2 + "/reviews?api_key=" + API_KEY);
                break;
        }
    }

    public class TheMovieDBValoracionesAsynTask  extends AsyncTask<String, Void, ValoracionesList> {

        @Override
        protected ValoracionesList doInBackground(String... urls) {
            BufferedReader in = null;


            //Retrofit evita tener que gestionar la conexion http
            //Retrofit no está disponible en android, hay que añadirla
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuffer data = new StringBuffer();
                //Insertar los datos obtenidos con in en el StringBuffer
                String line = null;
                while ((line = in.readLine()) != null) {
                    data.append(line);
                }
                Gson gson = new Gson();
                String json = data.toString();
                ValoracionesList cl = gson.fromJson(json, ValoracionesList.class);
                return cl;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(ValoracionesList vLists) {
            super.onPostExecute(vLists);
            String c = "Valoraciones: \n\n";
            for(int i=0; i<vLists.getResults().size();i++){
                c=c+ "Autor: "+vLists.getResults().get(i).getAuthor()+ "   " +vLists.getResults().get(i).getContent()+ "\n";
            }
            movieInfo.setText(c);
        }
    }

    public class TheMovieDBCreditsAsynTask  extends AsyncTask<String, Void, CreditsLists> {

        @Override
        protected CreditsLists doInBackground(String... urls) {
            BufferedReader in = null;

            List<MovieTitleAndImage> movieList = new ArrayList<>();

            //Retrofit evita tener que gestionar la conexion http
            //Retrofit no está disponible en android, hay que añadirla
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuffer data = new StringBuffer();
                //Insertar los datos obtenidos con in en el StringBuffer
                String line = null;
                while ((line = in.readLine()) != null) {
                    data.append(line);
                }
                Gson gson = new Gson();
                String json = data.toString();
                CreditsLists cl = gson.fromJson(json, CreditsLists.class);
                return cl;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;

        }

        @Override
        protected void onPostExecute(CreditsLists creditsLists) {
            super.onPostExecute(creditsLists);
            String c = "Créditos: \n\nActores: ";
            for(int i=0; i<creditsLists.getCast().size();i++){
                c=c+creditsLists.getCast().get(i).getName()+ "(" + creditsLists.getCast().get(i).getCharacter()+"), ";
            }
            c=c+"\n" + "Crew: ";
            for(int i=0; i<creditsLists.getCrew().size();i++){
                c=c+creditsLists.getCrew().get(i).getJob()+ "(" + creditsLists.getCrew().get(i).getName()+"), ";
            }
            movieInfo.setText(c);
        }
    }

    }

