package es.cice.moviedbfinalproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import es.cice.moviedbfinalproject.MovieDetailActivity;
import es.cice.moviedbfinalproject.R;
import es.cice.moviedbfinalproject.model.Genre;
import es.cice.moviedbfinalproject.model.Movie;


/**
 * Created by cice on 20/1/17.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.ViewHolder> implements Filterable {
    private List<Movie> movieList;
    private Context ctx;
    private String urlBaseImage;
    private List<Genre> lg;

    public MoviesAdapter(Context ctx, List<Movie> list, String urlBaseImage, List<Genre> lg ){
        movieList = list;
        this.ctx = ctx;
        this.urlBaseImage = urlBaseImage;
        this.lg = lg;
    }
    /*
    Este metodo se llama cada vez que sea necesario construir una nueva fila
     */
    @Override
    public MoviesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Obtenemos el infater necesario para construir un fila definida en xml
        LayoutInflater inflater = LayoutInflater.from(ctx);
        View row = inflater.inflate(R.layout.movie_row, parent, false);
        ViewHolder holder = new ViewHolder(row);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MoviesAdapter.ViewHolder holder, final int position) {

        holder.tituloTV.setText(movieList.get(position).getOriginalTitle());
        holder.voteMovieTV.setText(movieList.get(position).getVoteAverage().toString());
        holder.genresMovieTV.setText("");
        String separador = "";
        for (int j = 0; j < movieList.get(position).getGenreIds().size(); j++) {
            int i = 0;
            boolean encontrado = false;
            do {

                if (movieList.get(position).getGenreIds().get(j).toString().equalsIgnoreCase(lg.get(i).getId().toString())) {
                    encontrado = true;
                    if (!holder.genresMovieTV.getText().equals("")) separador = ", ";
                    holder.genresMovieTV.setText(holder.genresMovieTV.getText() + separador + lg.get(i).getName());
                } else {
                    i++;
                }
            } while (!encontrado);

        }
        //holder.genresMovieTV.setText(movieList.get(position).getGenreIds().toString());

        Picasso
                .with(ctx)
                .load(Uri.parse(urlBaseImage + "w500" + movieList.get(position).getPosterPath()))
                .resize(250, 250)
                .centerInside()
                .into(holder.movieImageIV);


        holder.vElipsisTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(ctx, holder.vElipsisTV);
                popup.inflate(R.menu.movie_popup);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = new Intent(ctx, MovieDetailActivity.class);
                        //intent.putExtra(MovieDetailActivity.IMAGEN_EXTRA, holder.movieImageIV.getId());

                        intent.putExtra(MovieDetailActivity.IMAGEN_EXTRA,
                                urlBaseImage + "w500" + movieList.get(position).getPosterPath());
                        intent.putExtra(MovieDetailActivity.TITULO_EXTRA,
                                movieList.get(position).getOriginalTitle());

                        //intent.putExtra(MovieDetailActivity.CREDITOS_EXTRA,
                        //          movieList.get(movieList.get(position));


                        switch (item.getItemId()) {
                            case R.id.creditsItem:
                                intent.putExtra(MovieDetailActivity.ID_MOVIE_EXTRA,movieList.get(position).getId().toString());
                                intent.putExtra(MovieDetailActivity.INFO_EXTRA,
                                        "creditos");
                                /*Log.d("Elipsis","delete...");
                                filmList.remove(holder.getAdapterPosition());
                                notifyDataSetChanged();*/
                                break;
                            case R.id.opinionsItem:
                                intent.putExtra(MovieDetailActivity.VALORACIONES_EXTRA,
                                        movieList.get(position).getId().toString());
                                intent.putExtra(MovieDetailActivity.INFO_EXTRA,
                                        "opiniones");
                                break;
                            case R.id.overviewItem:

                                intent.putExtra(MovieDetailActivity.OVERVIEW_EXTRA,
                                    movieList.get(position).getOverview());
                                intent.putExtra(MovieDetailActivity.INFO_EXTRA,
                                        "sinopsis");
                                break;
                        }
                        ctx.startActivity(intent);
                        return true;
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    @Override
    public Filter getFilter() {
        return new MovieFilter();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView movieImageIV;
        private TextView tituloTV;
        private TextView vElipsisTV;
        private TextView genresMovieTV;
        private TextView voteMovieTV;

        public ViewHolder(View itemView) {
            super(itemView);
            movieImageIV = (ImageView) itemView.findViewById(R.id.movieImageIV);
            tituloTV = (TextView) itemView.findViewById(R.id.tituloTV);
            voteMovieTV = (TextView) itemView.findViewById(R.id.voteMovieTV);
            genresMovieTV = (TextView) itemView.findViewById(R.id.genresMovieTV);
            vElipsisTV = (TextView) itemView.findViewById(R.id.vElipsis);

           /* movieImageIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(ctx, MovieDetailActivity.class);
                   // intent.putExtra(MovieDetailActivity.IMAGEN_EXTRA,
                   //         movieList.get(getAdapterPosition()).getImagen());
                    intent.putExtra(MovieDetailActivity.TITULO_EXTRA,
                            movieList.get(getAdapterPosition()).getOriginalTitle());
                  //  intent.putExtra(MovieDetailActivity.CREDITOS_EXTRA,
                  //          movieList.get(getAdapterPosition()).());
                    intent.putExtra(MovieDetailActivity.OVERVIEW_EXTRA,
                    movieList.get(getAdapterPosition()).getOverview());
                    ctx.startActivity(intent);
                }
            });*/
        }
    }

    public class MovieFilter extends Filter {
        public static final String TAG = "MovieFilter";
        private List<Movie> originalList;
        private List<Movie> filteredList;

        public MovieFilter() {
            originalList = new LinkedList<>(movieList);

        }

        @Override
        protected FilterResults performFiltering(CharSequence filterData) {
            Log.d(TAG, "performFiltering()...");
            String filterStr = filterData.toString();
            FilterResults results = new FilterResults();
            filteredList = new ArrayList<>();
            for (Movie f : originalList) {
                if(f.getOriginalTitle().equalsIgnoreCase(filterStr)){
                 filteredList.add(f);}
            }
            results.values = filteredList;
            results.count = filteredList.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            Log.d(TAG, "publishResults()...");
            List<Movie> list = (List<Movie>) filterResults.values;
            if (list.size() > 0)
                movieList = list;
            notifyDataSetChanged();
        }
    }
}

