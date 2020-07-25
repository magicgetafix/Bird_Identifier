package com.birdidentifier.birdidentifier_findthatbird.home.adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.birdidentifier.birdidentifier_findthatbird.R;
import com.birdidentifier.birdidentifier_findthatbird.model.Bird;
import com.birdidentifier.birdidentifier_findthatbird.model.viewmodel.MainModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;
import timber.log.Timber;

public class BirdAdapter extends RecyclerView.Adapter<BirdAdapter.BirdViewHolder> {

    private Context context;
    private List<Bird> birdList;
    private BirdClickListener birdClickListener;
    private ViewModelStoreOwner store;

    public BirdAdapter(Context context, List<Bird> birdList, BirdClickListener birdClickListener, ViewModelStoreOwner store){
        this.context = context;
        this.birdList = birdList;
        this.birdClickListener = birdClickListener;
        this.store = store;
    }

    @NonNull
    @Override
    public BirdViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bird_layout, parent, false);
        return new BirdViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BirdViewHolder holder, int position) {
        Bird bird = this.birdList.get(position);
        holder.setContent(bird, this.birdClickListener, this.context, store);
    }

    @Override
    public int getItemCount() {
        return birdList.size();
    }

    public class BirdViewHolder extends RecyclerView.ViewHolder{

        private ImageView birdImage;
        private TextView englishNameText;
        private TextView latinNameText;
        private TextView percentageAccuracyText;
        private TextView viewInfoButton;
        private BirdClickListener birdClickListener;
        private Context context;
        private MainModel mainModel;
        private WebView webView;


        public BirdViewHolder(@NonNull View itemView) {
            super(itemView);
            birdImage = itemView.findViewById(R.id.bird_image_view);
            englishNameText = itemView.findViewById(R.id.english_name_text_view);
            latinNameText = itemView.findViewById(R.id.latin_name_text_view);
            percentageAccuracyText = itemView.findViewById(R.id.percentage_fit_text_view);
            viewInfoButton = itemView.findViewById(R.id.view_more_text_view);
            webView = itemView.findViewById(R.id.bird_image_webview);
            webView.setVisibility(View.INVISIBLE);
        }

        public void setContent(Bird bird, BirdClickListener birdClickListener, Context context, ViewModelStoreOwner store){

            mainModel = new ViewModelProvider(store).get(MainModel.class);
            this.birdClickListener = birdClickListener;
            this.context = context;
            if (bird!=null){
                englishNameText.setText(bird.getEnglishName());
                String latinName = this.context.getString(R.string.latin_name);
                latinName = latinName+"\n"+bird.getLatinName();
                latinNameText.setText(latinName);
                String percentageAccuracy = this.context.getString(R.string.percentage_accuracy);
                percentageAccuracy = percentageAccuracy+"\n"+bird.getPercentageAccuracy()+"%";
                percentageAccuracyText.setText(percentageAccuracy);

                mainModel.getWikipediaImageUrls(bird, 240, true)
                        .observeForever(birdWithImage->{



                            if (birdWithImage.getImageUrl()!=null && !birdWithImage.getImageUrl().isEmpty()) {
                                webView.setBackgroundColor(this.context.getResources().getColor(R.color.colorPrimaryDarker));
                                webView.loadUrl(birdWithImage.getImageUrl());
                                webView.setWebViewClient(new WebViewClient(){
                                    @Override
                                    public void onLoadResource(WebView view, String url) {
                                        super.onLoadResource(view, url);
                                        webView.setVisibility(View.VISIBLE);
                                    }
                                });

                            }

                        });

                viewInfoButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (birdClickListener!=null){
                            birdClickListener.onClick(bird);
                        }
                    }
                });
            }
        }
    }

    public interface BirdClickListener{

        void onClick(Bird bird);
    }
}
