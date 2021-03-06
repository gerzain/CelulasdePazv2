package com.dev.celulaspaz.fragments;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dev.celulaspaz.R;
import com.dev.celulaspaz.adapters.NoticiaAdapter;
import com.dev.celulaspaz.model.Noticia;
import com.dev.celulaspaz.service.ApiNoticia;
import com.dev.celulaspaz.util.OnClickNoticia;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NoticiasFragment extends Fragment implements OnClickNoticia
{
    private List<Noticia> noticias;
    private NoticiaAdapter adapter;
    private  RecyclerView recyclerView;
    private String TAG=NoticiasFragment.class.getSimpleName();
    private ProgressDialog progressDialog;

    public NoticiasFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.activity_noticia,container,false);
        recyclerView=(RecyclerView)view.findViewById(R.id.rvNoticias);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this.getContext());
        recyclerView.setHasFixedSize(true);
        noticias=new ArrayList<>(0);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter=new NoticiaAdapter(getContext(),this);
        recyclerView.setAdapter(adapter);
        new ObtenerDatos().execute();
        return  view;

    }

    @Override
    public void onItemClick(Noticia noticia) {
       // Snackbar.make(getActivity().findViewById(R.id.frm_noticia),"ClickNoticia",Snackbar.LENGTH_LONG).show();
        Toast.makeText(getContext(),"Clicl",Toast.LENGTH_SHORT).show();
    }

    private   class ObtenerDatos extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
            progressDialog.dismiss();
        }

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog=ProgressDialog.show(getContext(),null,null,true);
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids)
        {

            Gson gson = new GsonBuilder()
                    .setDateFormat("yyyy-MM-dd'T'HH:mm")
                    .create();
            final String ulr = "http://192.168.1.72/api/public/noticia/";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ulr)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            ApiNoticia apiNoticia=retrofit.create(ApiNoticia.class);
            final Call<List<Noticia>> respuesta = apiNoticia.getNoticias();
            respuesta.enqueue(new Callback<List<Noticia>>() {
                @Override
                public void onResponse(Call<List<Noticia>> call, Response<List<Noticia>> response) {

                    for(Noticia noticia: response.body())
                    {
                        noticias.add(noticia);

                         //Log.d(TAG,noticia.getTexto()+""+noticia.getTitulo()+" "+noticia.getFecha());
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            adapter.setDataset(noticias);
                            recyclerView.setAdapter(adapter);

                        }
                    });
                }

                @Override
                public void onFailure(Call<List<Noticia>> call, Throwable t) {
                    Log.d("Error",t.getMessage());
                    Toast.makeText(getContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
                   // Snackbar.make(getActivity().findViewById(R.id.frm_noticia),t.getMessage(),Snackbar.LENGTH_SHORT).show();
                }
            });




            return null;

        }
    }
}
