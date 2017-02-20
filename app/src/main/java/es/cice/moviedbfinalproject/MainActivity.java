
//by Carmen Ramos Delgado
package es.cice.moviedbfinalproject;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import es.cice.moviedbfinalproject.asynctasks.TheMovieDBMoviesAsyncTask;
import es.cice.moviedbfinalproject.model.Genre;
import es.cice.moviedbfinalproject.model.ListGenres;
import es.cice.moviedbfinalproject.model.setupDB;

public class MainActivity extends AppCompatActivity {
    private Context ctx;
    private ActionBar aBar;
    private String urlBaseImage;
    public static final String API_KEY = "857ef84cbaec1f89f981c0ac344c4630";
    private static final String URL_CONFIG = "https://api.themoviedb.org/3/configuration?api_key=" + API_KEY;
    private static final String URL_GENRES = "https://api.themoviedb.org/3/genre/movie/list?api_key=" + API_KEY;
    private Spinner spGenre;
    private Spinner spYear;
    private RecyclerView moviesRV;
    private ImageView imageMovieIV;
    private final static String URL_POPULAR_MOVIES = "https://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY;
    private List<Genre> genreList;

    private static final String URL_TOP_RATED = "https://api.themoviedb.org/3/movie/top_rated?api_key=" + API_KEY;
    //https://api.themoviedb.org/3/genre/{genre_id}/movies?api_key=<<api_key>>&language=en-US&include_adult=false&sort_by=created_at.asc

    private static final String URL_MOVIES = "https://api.themoviedb.org/3/discover/movie?api_key=" + API_KEY;
    private static final String URL_ESPANIOLAS = "https://api.themoviedb.org/3/discover/movie?api_key=" + API_KEY + "&with_original_language=es";

    private TheMovieDBMoviesAsyncTask at;
    private MenuItem buscarIT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ctx = this;

        Calendar fecha = new GregorianCalendar();
        int anyoActual = fecha.get(Calendar.YEAR);

        Toolbar toolbar = (Toolbar) findViewById(R.id.includedToolbar);
        setSupportActionBar(toolbar);
        aBar = getSupportActionBar();
        aBar.setTitle("Películas");

        spGenre = (Spinner) findViewById(R.id.genreSPIN);
        spYear = (Spinner) findViewById(R.id.genreSPINYear);
        moviesRV = (RecyclerView) findViewById(R.id.moviesRV);
        imageMovieIV = (ImageView) findViewById(R.id.movieImageIV);

        List<String> listaYears = new ArrayList<>();
        listaYears.add("<Todos>");
        for (int i = anyoActual; i > 1899; i--) {
            listaYears.add("" + i);
        }
        ArrayAdapter spAdapter = new ArrayAdapter(ctx, android.R.layout.simple_spinner_item, listaYears);
        spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spYear.setAdapter(spAdapter);

        getBaseUrlImage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        buscarIT = menu.getItem(2);
        return true;
    }

    public void getGenres() {
        TheMovieDBGetGenresAsynTask at = new TheMovieDBGetGenresAsynTask();
        at.execute(URL_GENRES);
    }

    public void getMovies(String url) {
        at = new TheMovieDBMoviesAsyncTask(moviesRV, this, imageMovieIV, genreList, urlBaseImage);
        at.execute(url);
    }

    public void getBaseUrlImage() {
        TheMovieDBGetUrlBaseAsynTask at = new TheMovieDBGetUrlBaseAsynTask();
        at.execute(URL_CONFIG);
    }

    public class TheMovieDBGetUrlBaseAsynTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            getGenres();
        }

        @Override
        protected String doInBackground(String... urls) {
            BufferedReader in = null;
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuffer data = new StringBuffer();
                String line = null;
                while ((line = in.readLine()) != null) {
                    data.append(line);
                }
                Gson gson = new Gson();
                String json = data.toString();
                setupDB setupdb = gson.fromJson(json, setupDB.class);
                urlBaseImage = setupdb.getImages().getBaseUrl();
                return urlBaseImage;
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
                return null;
            }
        }
    }

    public class TheMovieDBGetGenresAsynTask extends AsyncTask<String, Void, List<Genre>> {
        @Override
        protected List<Genre> doInBackground(String... urls) {
            BufferedReader in = null;
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
                ListGenres lg = gson.fromJson(json, ListGenres.class);
                genreList = lg.getGenres();
                return genreList;
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
        protected void onPostExecute(List<Genre> genres) {
            super.onPostExecute(genres);
            List<String> listag = new ArrayList<>();
            listag.add("<Todos>");
            for (int i = 0; i < genres.size(); i++) {
                listag.add(genres.get(i).getName());
            }
            ArrayAdapter spAdapter = new ArrayAdapter(ctx, android.R.layout.simple_spinner_item, listag);
            spAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spGenre.setAdapter(spAdapter);
            getMovies(URL_MOVIES);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.buscarIT:
                String url = URL_MOVIES;
                String queryGenre = "";
                String queryYear = "";
                int pos = spGenre.getSelectedItemPosition();
                if (pos > 0) {
                    queryGenre = "&with_genres=" + genreList.get(pos - 1).getId();
                }
                if (spYear.getSelectedItemPosition() > 0)
                    queryYear = "&year=" + spYear.getSelectedItem().toString();

                url = url + queryGenre + queryYear;
                getMovies(url);

                return true;
            case R.id.topRatedIT:
                aBar.setTitle("Mejor valoradas");
                buscarIT.setVisible(false);
                buscarIT.setEnabled(false);
                spYear.setVisibility(View.INVISIBLE);
                spGenre.setVisibility(View.INVISIBLE);
                aBar.setDisplayShowTitleEnabled(true);
                getMovies(URL_TOP_RATED);
                return true;

            case R.id.filtrarIT:
                aBar.setDisplayShowCustomEnabled(true);
                aBar.setDisplayShowTitleEnabled(false);
                spGenre.setVisibility(View.VISIBLE);
                spYear.setVisibility(View.VISIBLE);
                buscarIT.setVisible(true);
                buscarIT.setEnabled(true);
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.activity_main), "Pulsa en la lupa para filtrar", Snackbar.LENGTH_LONG);
                        snackbar.setActionTextColor(Color.RED);
                        snackbar.show();
                return true;
            case R.id.espaniolasIT:
                aBar.setTitle("Españolas");
                spYear.setVisibility(View.INVISIBLE);
                spGenre.setVisibility(View.INVISIBLE);
                aBar.setDisplayShowTitleEnabled(true);
                buscarIT.setVisible(false);
                buscarIT.setEnabled(false);
                getMovies(URL_ESPANIOLAS);
                return true;

            case R.id.popularesIT:
                aBar.setTitle("Populares");
                spYear.setVisibility(View.INVISIBLE);
                spGenre.setVisibility(View.INVISIBLE);
                aBar.setDisplayShowTitleEnabled(true);
                buscarIT.setVisible(false);
                buscarIT.setEnabled(false);
                getMovies(URL_POPULAR_MOVIES);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}




