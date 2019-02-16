package jlopez.com.yupayapp.adapters;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jlopez.com.yupayapp.R;
import jlopez.com.yupayapp.utils.ItemClickListener;
import jlopez.com.yupayapp.utils.Recursos;

public class Adaptador extends RecyclerView.Adapter<Adaptador.AnimeViewHolder>{

    private Context context;
    private List<Recursos> listItems, filterList;
    private Activity activity;
    private String url, tipo_a, tema, type, grado;

    @NonNull
    @Override
    public AnimeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.activity_buscar_recycler_view, viewGroup, false);
        return new AnimeViewHolder(itemView);
    }

    public Adaptador(Context context, Activity activity, List<Recursos> listItems) {
        this.context=context;
        this.activity=activity;
        this.listItems = listItems;
        this.filterList = new ArrayList<Recursos>();
        this.filterList.addAll(this.listItems);
    }
    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onBindViewHolder(AnimeViewHolder holder, final int posicion) {
        final Recursos listItem = filterList.get(posicion);
        holder.tema.setText(listItem.getTema());
        holder.grado.setText("Grado: "+listItem.getGrado());
        holder.tipo_a.setText(listItem.getTipo_aprendizaje());
        if (Objects.equals(listItem.getType(), "PDF")){
            holder.img.setImageResource(R.drawable.icon_pdf);
        }else if (Objects.equals(listItem.getType(), "video")){
            holder.img.setImageResource(R.drawable.icono_video);
        }
        holder.setClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                url =listItem.getUrl();
                type =listItem.getType();
                tipo_a =listItem.getTipo_aprendizaje();
                grado =listItem.getGrado();
                tema =listItem.getTema();

                if (type.equals("PDF")){
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.setData(Uri.parse(url));
                    activity.startActivity(intent);
                }else if (type.equals("video")){
                    Uri uri = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setDataAndType(uri, "video/mp4");
                    activity.startActivity(intent);
                }

            }
        });
    }
    @Override
    public int getItemCount() {
        return (null != filterList ? filterList.size() : 0);
    }
    public void filter(final String text) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                filterList.clear();
                if (TextUtils.isEmpty(text)) {

                    filterList.addAll(listItems);

                } else {
                    for (Recursos item : listItems) {
                        if (item.getTema().toLowerCase().contains(text.toLowerCase())) {
                            filterList.add(item);
                        }
                    }
                }

                ((Activity) activity).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });

            }
        }).start();

    }
public class AnimeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
    private TextView tema, grado, tipo_a;
    ImageView img;

    private ItemClickListener clickListener;

    AnimeViewHolder(View item) {
        super(item);

        tema = (TextView) item.findViewById(R.id.tema);
        grado = (TextView) item.findViewById(R.id.grado);
        tipo_a = (TextView) item.findViewById(R.id.tipo_a);
        img = (ImageView) item.findViewById(R.id.imgbmpA);
        item.setOnClickListener(this);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }
    @Override
    public void onClick(View view) {
        clickListener.onClick(view, getPosition(), false);
    }

    @Override
    public boolean onLongClick(View view) {
        clickListener.onClick(view, getPosition(), true);
        return true;
    }
}

}

